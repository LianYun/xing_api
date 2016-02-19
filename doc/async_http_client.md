

项目在HTTP请求时使用了async-http-client库。

### async-http-client的相关资源
+ [Github项目地址](https://github.com/AsyncHttpClient/async-http-client)
+ [Getting Started（本文主要参考）](https://jfarcand.wordpress.com/2010/12/21/going-asynchronous-using-asynchttpclient-the-basic/)
+ [简单的例子](https://jfarcand.wordpress.com/2011/12/21/writing-websocket-clients-using-asynchttpclient/)

### 知识要点

#### 1. 为什么要避免阻塞

"PUT"、"POST"等方法不需要等待response。异步可以支持长连接高并发。

```
AsyncHttpClient client = new AsyncHttpClient();
Response response = client.prepareGet(("http://sonatype.com").execute().get();

// PUT操作则不需要等待，因而没有get，如下：
AsyncHttpClient client = new AsyncHttpClient();
Response response = client.preparePut(("http://sonatype.com/myFile.avi").execute();
```

#### 2. 异步处理器

```
public interface AsyncHandler<T> {
    void onThrowable(Throwable t);
    STATE onHeaderReceived(HttpResponseHeaders headers) throws Exception;
    STATE onStatusReceived(HttpResponseStatus responseStatus) throws Exception;
    STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception;
    T onCompleted() throws Exception;
}
```

STATE{CONTINUE, ABORT}

```
AsyncHttpClient client = new AsyncHttpClient();
client.prepareGet("http://sonatype.com")
  .execute(new AsyncHandler<T>() {

     void onThrowable(Throwable t) {
     }
     public STATE onBodyPartReceived(HttpResponseBodyPart bodyPart)
       throws Exception{
        return STATE.CONTINUE;
     }
     public STATE onStatusReceived(HttpResponseStatus responseStatus)
       throws Exception {
        return STATE.CONTINUE;
     }
     public STATE onHeadersReceived(HttpResponseHeaders headers)
       throws Exception {
        return STATE.CONTINUE;
     }
     T onCompleted() throws Exception {
       return T;
   }
});
```
#### 3. 创建请求对象

```
RequestBuilder builder = new RequestBuilder("PUT");
  Request request = builder
      .setUrl(...)
      .addHeader("name", "value")
      .setBody(new File("myUpload.avi"))
      .build();
```

**未解决**：avoid loading unnecessary bytes in memeory.

```
public interface BodyGenerator {
    Body createBody() throws IOException;
}

public interface Body {
    long getContentLength()
    long read(ByteBuffer buffer) throws IOException;
    void close() throws IOException;
}

```

采用上面的方法，不会将非必要的bytes加载至内存，极大地提升了性能。

一个RequestBuilder也可以用于配置每一个请求的configration，比如设置Proxy和请求的超时时间。

```
PerRequestConfig requestConfig = new PerRequestConfig();
requestConfig.setRequestTimeoutInMs(5 * 1000);
requestConfig.setProxy(new ProxyServer(...));
Future responseFuture = client.prepareGet("http://")
        .setPerRequestConfig(requestConfig)
        .execute();
// public ListenableFuture<Response> execute()
// 而ListenableFuture实现了Future接口。

```

#### 4. 创建响应对象

可以在`AsyncHandler.onCompleted()`中创建一个响应对象并返回。也可以使用
`ResponseBuilder.accumulate()`方法添加式地构建一个响应对象。

```
MyAsyncHandler<Response> asyncHandler = new MyAsyncHanfler<Response>() {
  private final Response.ResponseBuilder builder =
          new Response.ResponseBuilder();

  public STATE onBodyPartReceived(final HttpResponseBodyPart content)
    throws Exception {
      builder.accumulate(content);
      return STATE.CONTINUE;
  }

  public STATE onStatusReceived(final HttpResponseStatus status)
     throws Exception {
      builder.accumulate(status);
      return STATE.CONTINUE;
  }

  public STATE onHeadersReceived(final HttpResponseHeaders headers)
     throws Exception {
      builder.accumulate(headers);
      return STATE.CONTINUE;
  }

  public Response onCompleted() throws Exception {
      return builder.build();
  }
}

Response response = client.prepareGet("http://sonatype.com")
     .execute(asyncHandler).get();
```

响应的对象体可能尺寸过大，无法一次全部加载到内存中，因此推荐奖数据缓存后flush到硬盘上去。
实现如下：

```
MyAsyncHandler<Response> asyncHandler = new MyAsyncHanfler<Response>() {
   private final Response.ResponseBuilder builder =
      new Response.ResponseBuilder();

   public STATE onBodyPartReceived(final HttpResponseBodyPart content)
     throws Exception {
       content.write(myOutputStream);
       return STATE.CONTINUE;
   }

   public STATE onStatusReceived(final HttpResponseStatus status)
     throws Exception {
       builder.accumulate(status);
       return STATE.CONTINUE;
   }

   public STATE onHeadersReceived(final HttpResponseHeaders headers)
      throws Exception {
       builder.accumulate(headers);
       return STATE.CONTINUE;
   }

   public Response onCompleted() throws Exception {
       return builder.build();
   }
}

Response response = client.prepareGet("http://sonatype.com")
   .execute(asyncHandler).get();
```

注意在上面的情况下，无法直接调用`Response.getResponseBodyAsStream()`和`getResponseBody()`
这会导致抛出`IllegalStateException`异常。

#### 5. 配置AsyncHttpClient，包括Compression、Connection Pool、Proxy、Times out、Thread Pools，Security等。

```
Builder builder = new AsyncHttpClientConfig.Builder();
builder.setCompressionEnabled(true)
    .setAllowPoolingConnection(true)
    .setRequestTimesout(30000)
    .build();
AsyncHttpClient client = new AsyncHttpClient(builder.build());
```

如果不想使用默认的ExecutorService，可以自己如下设定：
```
Builder builder = new AsyncHttpClientConfig.Builder();
builder.setExecutorService(myOwnThreadPool);
AsyncHttpClient client = new AsyncHttpClient(builder.build());
```

在`AsyncHttpClientConfig.Builder`中有很多的方法用于设定请求客户端的各种参数。

#### 6. 设置SSL

不需要使用自己的证书时只需要设定如下：

```
AsyncHttpClient client = new AsyncHttpClient();
Response response = client.prepareGet(("https://sonatype.com").execute().get();
```

Client库会探测到这个HTTPS连接，并自动从可信赖的key store中获得对应的key。

#### 7. 使用过滤器Filters

库支持三种类型的过滤器：Request, Response和IOException。

##### 1) Request Filter

用于在应用收到请求前做一些处理。

```
public class ThrottleRequestFilter implements RequestFilter {
    private final int maxConnections;
    private final Semaphore available;
    private final int maxWait;

    public ThrottleRequestFilter(int maxConnections) {
        this.maxConnections = maxConnections;
        this.maxWait = Integer.MAX_VALUE;
        available = new Semaphore(maxConnections, true);
    }

    public ThrottleRequestFilter(int maxConnections, int maxWait) {
        this.maxConnections = maxConnections;
        this.maxWait = maxWait;
        available = new Semaphore(maxConnections, true);
    }

    public FilterContext filter(FilterContext ctx) throws FilterException {

        try {
            if (!available.tryAcquire(maxWait, TimeUnit.MILLISECONDS)) {
                throw new FilterException(
                    String.format("No slot available for Request %s "
                            "with AsyncHandler %s",
                            ctx.getRequest(), ctx.getAsyncHandler()));
            };
        } catch (InterruptedException e) {
            throw new FilterException(
                    String.format("Interrupted Request %s" +
                         "with AsyncHandler %s",
                            ctx.getRequest(), ctx.getAsyncHandler()));
        }

        return new FilterContext(
             new AsyncHandlerWrapper(ctx.getAsyncHandler()), ctx.getRequest());
    }

    private class AsyncHandlerWrapper implements AsyncHandler<T> {

        private final AsyncHandler asyncHandler;

        public AsyncHandlerWrapper(AsyncHandler asyncHandler) {
            this.asyncHandler = asyncHandler;
        }

        public void onThrowable(Throwable t) {
            asyncHandler.onThrowable(t);
        }

        public STATE onBodyPartReceived(HttpResponseBodyPart bodyPart)
                throws Exception {
            return asyncHandler.onBodyPartReceived(bodyPart);
        }

        public STATE onStatusReceived(HttpResponseStatus responseStatus)
              throws Exception {
            return asyncHandler.onStatusReceived(responseStatus);
        }

        public STATE onHeadersReceived(HttpResponseHeaders headers)
              throws Exception {
            return asyncHandler.onHeadersReceived(headers);
        }

        public T onCompleted() throws Exception {
            available.release();
            return asyncHandler.onCompleted();
        }
    }
}

```

上面的代码中我们首先装饰了原始的AsyncHandler， 并使用Semaphore阻塞了请求。
使用如下所示：
```
AsyncHttpClientConfig.Builder b =
                 new AsyncHttpClientConfig.Builder();
b.addRequestFilter(new ThrottleRequestFilter(100));
AsyncHttpClient c = new AsyncHttpClient(b.build());
```

##### 2) Response Filter


##### 3) IOException Filter


#### 8. Uploading file: Progress Listener

当上传二进制文件时，应用需要按照上载位置采取一些行动。AsyncHttpClient库提供了一个特殊的
AsyncHandler来处理这些情况：

```
public interface ProgressAsyncHandler<T> extends AsyncHandler<T> {
    STATE onHeaderWriteCompleted();
    STATE onContentWriteCompleted();
    STATE onContentWriteProgress(long amount, long current, long total);
}
```

#### 9. Configuring Authentication: BASIC, DIGEST or NTLM

AsyncHttpClient使用RealmBuilder来配置请求的认证。

```
AsyncHttpClient client = new AsyncHttpClient();
Realm realm = new Realm.RealmBuilder()
    .setPrincipal(user)
    .setPassword(admin)
    .setUsePreemptiveAuth(true)
    .setScheme(AuthScheme.BASIC)
    .build();
// 在一次请求时使用
client.prepareGet("http://...").setRealm(realm).execute();

// 在Client的配置层级使用这个Realm
Builder builder = new AsyncHttpClientConfig.Builder();
builder.setRealm(realm).build();
AsyncHttpClient client = new AsyncHttpClient(builder.build());
```

库中支持三种认证类型：`BASIC`、`DIGEST`、`NTLM`。
也可以通过自定义Response Filter设定自己的认证机制。

#### 10. 配置Proxy

```
AsyncHttpClient client = new AsyncHttpClient();
        Future<Response> f = client
                .prepareGet("http://....)
                .setProxyServer(new ProxyServer("127.0.0.1", 8080))
                .execute();
```
认证SSL的Client。

```
ProxyServer ps = new ProxyServer(ProxyServer.Protocol.HTTPS, "127.0.0.1", 8080);
AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
RequestBuilder rb = new RequestBuilder("GET")
             .setProxyServer(ps)
             .setUrl("https://twitpic.com:443");

Future responseFuture = asyncHttpClient
    .executeRequest(rb.build(), new AsyncCompletionHandlerBase() {
@Override
public void onThrowable(Throwable t) {}

@Override
public Response onCompleted(Response response) throws Exception {
    return response;
}});

Response r = responseFuture.get();
```

有口令的Proxy：

```
ProxyServer ps = new ProxyServer(ProxyServer.Protocol.HTTPS,
                                  "127.0.0.1",
                                  8080,
                                  "admin",
                                  "password");
AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
RequestBuilder rb = new RequestBuilder("GET")
    .setProxyServer(ps).setUrl("https://twitpic.com:443");

Future responseFuture = asyncHttpClient
   .executeRequest(rb.build(), new AsyncCompletionHandlerBase() {

@Override
public void onThrowable(Throwable t) {}

@Override
public Response onCompleted(Response response) throws Exception {
    return response;
}});

Response r = responseFuture.get();
```
#### 11. 交换提供者

AsyncHttpClient默认使用了Netty's Framework，作为自己的HTTP processor。当然也提供了其他的选项：

+ 使用URLConnection构建的JDKAsyncHttpProvider
+ 使用ApacheAsyncHttpProvider构建的提供者

```
AsyncHttpClient client = new AsyncHttpClient(
    new ApacheAsyncHttpProvider(new AsyncHttpClientConfig.Builder().build()));

AsyncHttpclient client = new AsyncHttpClient(
    new JDKAsyncHttpProvider(new AsyncHttpClientConfig.Builder().build()));
```

配置Provider的内部属性，将NettyAsyncHttpProvider 从NIO设定为blocking I/O:

```
NettyAsyncHttpProviderConfig config = new NettyAsyncHttpProviderConfig();
config.setProperty(NettyAsyncHttpProviderConfig.USE_BLOCKING_IO, "true");

AsyncHttpClientConfig c = new AsyncHttpClientConfig()
      .setAsyncHttpClientProviderConfig(config).build();

AsyncHttpClient client = new AsyncHttpClient(new NettyAsyncHttpProvider(config));
```

