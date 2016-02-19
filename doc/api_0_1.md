
[xing项目API介绍](https://github.com/LianYun/xing/wiki/%E5%BA%94%E7%94%A8%E7%BC%96%E7%A8%8B%E6%8E%A5%E5%8F%A3%E4%BB%8B%E7%BB%8D)

目前使用API v0.1版本。

### API v0.1 路径

#### 前缀
```
val prefix = "http://xing.movecloud.me/api/v0.1"

```

#### 获得认证信息

```
/token
```

#### 获取用户相关信息

```
/users/<int:id>
/users/<int:id>/conferences
/users/<int:id>/followed
/users/<int:id>/followers
```

#### 获得会议相关信息

```
/conferences
/conferences/<int:id>
/conferences/<int:id>/topics
/conferences/<int:id>/city
/conferences/<int:id>/comments
```

#### 获得评论信息

```
/comments/<int:id>
```

#### 发布新会议


#### 发表新评论


