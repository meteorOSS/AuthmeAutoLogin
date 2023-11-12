# AuthmeAutoLogin

### 为使用AuthMe的服务器提供了更简洁的登陆流程;
### 去除主菜单，直接输入密码进入指定的服务器 (新用户将自动注册)

模组基于 1.18 开发
安装前请确保使用的是 [AuthMeReloaded](https://github.com/AuthMe/AuthMeReloaded)

### 动图展示:

[![autologin.gif](https://img.fastmirror.net/s/2023/11/13/65513e7016bff.gif)](https://img.fastmirror.net/s/2023/11/13/65513e7016bff.gif)

### 怎么更改进入的服务器IP?

在客户端config中找到 autologin-client.toml进行修改

```
[AutoLoginSetting]
	#你的服务器ip地址
	ipAddress = "127.0.0.1:8903"
```

配套bukkit插件 
[AuthmeAutoLogin-plugin](https://github.com/meteorOSS/AuthmeAutoLogin-plugin)https://github.com/meteorOSS/AuthmeAutoLogin-plugin)
