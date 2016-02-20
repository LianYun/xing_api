
功能总结：
1. 使用async-http-client从网站获得JSON数据至本地内存中。
2. 使用lift-json包对JSON数据进行解析，给出对应的数据。

#### 1. WebClient

异步客户端请求对象。
+ 接受"GET"和URL，返回对应的json格式的Future包装的字符串。
+ 接受"POST"和数据以及对应的URL，并返回Future包装的json格式的字符串对象。

#### 2. 
