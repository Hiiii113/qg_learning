# Sa-Token

## 一、了解

首先我们可以了解一下 sa-token 是什么

> 官方网站：[Sa-Token](https://sa-token.cc/)
>
> 官方文档：[框架介绍](https://sa-token.cc/doc.html#/)

sa-token 是现在非常好用的一款轻量级 Java 权限认证框架，主要解决：**登录认证**、**权限认证**、**单点登录**、**OAuth2.0**、**分布式Session会话**、**微服务网关鉴权** 等一系列权限相关问题。

![satoken介绍图片](D:\repo\计算机\后端\assets\image-20260322102117841-1774146079629-15.png)

## 二、SpringBoot 集成 sa-token

### 1. 创建项目

创建一个 SpringBoot 项目

### 2. 添加依赖

在项目中添加依赖：

```xml
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-spring-boot-starter</artifactId>
    <version>1.45.0</version>
</dependency>
```

- 如果你使用的 `SpringBoot 3.x`，请引入 `sa-token-spring-boot3-starter`。
- 如果你使用的 `SpringBoot 4.x`，请引入 `sa-token-spring-boot4-starter`。

### 3. 设置配置文件

`application.yml`风格：

```yml
server:
    # 端口
    port: 8081
    
############## Sa-Token 配置 (文档: https://sa-token.cc) ##############
sa-token: 
    # token 名称（同时也是 cookie 名称）
    token-name: satoken
    # token 有效期（单位：秒） 默认30天，-1 代表永久有效
    timeout: 2592000
    # token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结，默认-1 代表不限制，永不冻结
    active-timeout: -1
    # 是否允许同一账号多地同时登录 （为 true 时允许一起登录, 为 false 时新登录挤掉旧登录）
    is-concurrent: true
    # 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个 token, 为 false 时每次登录新建一个 token）
    is-share: false
    # token 风格（默认可取值：uuid、simple-uuid、random-32、random-64、random-128、tik）
    token-style: uuid
    # 是否输出操作日志 
    is-log: true
```

### 4. 新建启动类

新建启动类

```java
@SpringBootApplication
public class SaTokenDemoApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(SaTokenDemoApplication.class, args);
        System.out.println("启动成功，Sa-Token 配置如下：" + SaManager.getConfig());
    }
}
```

### 5. 创建测试 controller

```java
@RestController
@RequestMapping("/user/")
public class UserController
{
    // 测试登录，浏览器访问： http://localhost:8081/user/doLogin?username=zhang&password=123456
    @RequestMapping("doLogin")
    public String doLogin(String username, String password)
    {
        // 此处仅作模拟示例，真实项目需要从数据库中查询数据进行比对 
        if("zhang".equals(username) && "123456".equals(password))
        {
            StpUtil.login(10001);
            return "登录成功";
        }
        return "登录失败";
    }

    // 查询登录状态，浏览器访问： http://localhost:8081/user/isLogin
    @RequestMapping("isLogin")
    public String isLogin()
    {
        return "当前会话是否登录：" + StpUtil.isLogin();
    }
}
```

### 6. 运行

在浏览器访问：

> 登录：http://localhost:8081/user/doLogin?username=zhang&password=123456
>
> 查询登录状态：http://localhost:8081/user/isLogin

![运行结果](D:\repo\计算机\后端\assets\image-20260322103327012-1774146809384-17.png)

## 三、登录验证

### 1. 开始登录

在一个网站的登录过程中，无论是账密登录还是手机验证码登录，**本质上都是通过提交一定的认证信息，使得系统可以定位到 Ta 的唯一标识—— userId**

当我们拿到了 userId 后，便可以调用框架提供的 API 进行登录：

```java
// 会话登录：参数填写要登录的账号id，建议的数据类型：long | int | String， 不可以传入复杂类型，如：User、Admin 等等
StpUtil.login(Object userId);     
```

只需要这一行代码，Sa-Token 就为这个账号创建了一个 token 凭证，并且通过 cookie 上下文返回给了前端

> 对于前后端分离项目，还是需要自己手动传token，用户点击登录后，后端查找对应的 userId，使用 Sa-Token 的工具生成 Token 并且返回给前端，前端使用 localStorage 存储，每次请求都携带这个 Token

### 2. 校验是否登录

我们开发一些接口的时候，可能遇到当前接口需要登录后才能访问，此时就需要校验是否登录了

可以使用以下方法判断当前会话是否登录：

```java
// 判断当前会话是否已经登录，返回 true=已登录，false=未登录
StpUtil.isLogin();

// 检验当前会话是否已经登录, 如果已登录代码会安全通过，未登录则抛出异常：`NotLoginException`
StpUtil.checkLogin();
```

在实际的接口当中，我们既可以根据是否登录提示具体的信息，也可以直接抛出异常，使用全局异常处理同一处理

### 3. 会话查询

如果你需要获取当前登录的是谁

```java
// 获取当前会话账号id, 如果未登录，则抛出异常：`NotLoginException`
StpUtil.getLoginId();

// 类似查询API还有：
StpUtil.getLoginIdAsString();    // 获取当前会话账号id, 并转化为`String`类型
StpUtil.getLoginIdAsInt();       // 获取当前会话账号id, 并转化为`int`类型
StpUtil.getLoginIdAsLong();      // 获取当前会话账号id, 并转化为`long`类型

// ---------- 以下方法可以指定未登录情形下返回的默认值 ----------

// 获取当前会话账号id, 如果未登录，则返回 null 
StpUtil.getLoginIdDefaultNull();

// 获取当前会话账号id, 如果未登录，则返回默认值 （`defaultValue`可以为任意类型）
StpUtil.getLoginId(T defaultValue);
```

### 4. token 查询

```java
// 获取当前会话的 token 值
StpUtil.getTokenValue();

// 获取当前`StpLogic`的 token 名称
StpUtil.getTokenName();

// 获取指定 token 对应的账号id，如果未登录，则返回 null
StpUtil.getLoginIdByToken(String tokenValue);

// 获取当前会话剩余有效期（单位：s，返回-1代表永久有效）
StpUtil.getTokenTimeout();

// 获取当前会话的 token 信息参数
StpUtil.getTokenInfo();
```

### 5. 会话注销

```java
// 当前会话注销登录
StpUtil.logout();
```

## 四、权限认证

### 1. 思路

权限认证的最终目的在于：规定哪些用户可以访问哪些 接口/页面/资源

对于同一个页面：

- 管理员：正常返回数据
- 普通账号：权限不足，拒绝访问

![Sa-Token 权限认证](D:\repo\计算机\后端\assets\image-20260322121419280-1774152861051-25.png)

那么框架是如何判断一个账号是否拥有权限访问某个端口的呢？

从底层数据来讲，**每个账号都会拥有一组权限码集合，框架需要做的就是校验这个集合中是否存在指定的权限码**

![So-Token 权限校验](D:\repo\计算机\后端\assets\image-20260322121614720-1774152976334-27.png)

所以我们需要解决两个问题：

1. 如何定义一个账号需要的权限码集合？
2. 本次操作需要校验的权限码是哪个？

### 2. 定义一个账号的权限码集合

这里只给出使用数据库存储信息的获取方式：

```java
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface
{
    private final JdbcTemplate jdbcTemplate; // 直接用 JdbcTemplate 查数据库最快

    // 返回一个账号所拥有的权限码集合
    @Override
    public List<String> getPermissionList(Object loginId, String loginType)
    {
        // 将 loginId 转为 int
        int userId = Integer.parseInt(loginId.toString());

        // 编写 SQL，查询该用户所有的 permission_code
        String sql = "SELECT p.permission_code FROM sys_permission p " +
                "JOIN sys_role_permission rp ON p.id = rp.permission_id " +
                "JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
                "WHERE ur.user_id = ?";

        // 查询并返回
        return jdbcTemplate.queryForList(sql, String.class, userId);
    }

    // 返回一个账号所拥有的角色标识集合
    @Override
    public List<String> getRoleList(Object loginId, String loginType)
    {
        // 编写 SQL，查询该用户所有的 role_id
        String sql = "SELECT r.role_code FROM sys_role r " +
                "JOIN sys_user_role ur ON r.id = ur.role_id " +
                "WHERE ur.user_id = ?";
        // 返回
        return jdbcTemplate.queryForList(sql, String.class, loginId);
    }
}
```

## 五、踢人下线

所谓的踢人下线，其实就是找到指定`loginId`对应的`Token`，并设置器失效

<img src="D:\repo\计算机\后端\assets\image-20260322125308379-1774155191860-29.png" alt="被踢下线了" style="zoom:67%;" />

### 1. 强制注销

```java
StpUtil.logout(10001);                    // 强制指定账号注销下线 
StpUtil.logout(10001, "PC");              // 强制指定账号指定端注销下线 
StpUtil.logoutByTokenValue("token");      // 强制指定 Token 注销下线 
```

### 2. 踢人下线

```java
StpUtil.kickout(10001);                    // 将指定账号踢下线 
StpUtil.kickout(10001, "PC");              // 将指定账号指定端踢下线
StpUtil.kickoutByTokenValue("token");      // 将指定 Token 踢下线
```

强制注销 和 踢人下线 的区别在于：

- 强制注销等价于对方主动调用了注销方法，再次访问会提示：Token无效
- 踢人下线不会清除Token信息，而是将其打上特定标记，再次访问会提示：Token已被踢下线

### 3. 顶人下线

“顶人下线” 操作发生在框架登录时顶退旧登录设备，属于框架内部操作，一般情形下你不会调用到此 API

```java
StpUtil.replaced(10001);                    // 将指定账号顶下线 
StpUtil.replaced(10001, "PC");              // 将指定账号指定端顶下线
StpUtil.replacedByTokenValue("token");      // 将指定 Token 顶下线
```

## 六、注释鉴权

注释鉴权是 Sa-Token 的一个非常方便的特性，他能够非常优雅的将鉴权与业务代码分离！

- `@SaCheckLogin`: 登录校验 —— 只有登录之后才能进入该方法
- `@SaCheckRole("admin")`: 角色校验 —— 必须具有指定角色标识才能进入该方法
- `@SaCheckPermission("user:add")`: 权限校验 —— 必须具有指定权限才能进入该方法
- `@SaCheckSafe`: 二级认证校验 —— 必须二级认证之后才能进入该方法
- `@SaCheckHttpBasic`: HttpBasic校验 —— 只有通过 HttpBasic 认证后才能进入该方法
- `@SaCheckHttpDigest`: HttpDigest校验 —— 只有通过 HttpDigest 认证后才能进入该方法
- `@SaCheckDisable("comment")`：账号服务封禁校验 —— 校验当前账号指定服务是否被封禁
- `@SaCheckSign`：API 签名校验 —— 用于跨系统的 API 签名参数校验
- `@SaIgnore`：忽略校验 —— 表示被修饰的方法或类无需进行注解鉴权和路由拦截器鉴权

Sa-Token 使用全局拦截器完成鉴权，为了不影响性能，默认关闭，需要手动将 Sa-Token 的全局拦截器注册到项目中

### 1. 注册拦截器

1. 以 SpringBoot2 项目为例，新建配置类`SaTokenConfigure.java`

```java
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    // 注册 Sa-Token 拦截器，打开注解式鉴权功能 
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能 
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");    
    }
}
```

2. 随后就可以愉快的使用注解鉴权了

```java
// 登录校验：只有登录之后才能进入该方法 
@SaCheckLogin                        
@RequestMapping("info")
public String info() {
    return "查询用户信息";
}

// 角色校验：必须具有指定角色才能进入该方法 
@SaCheckRole("super-admin")        
@RequestMapping("add")
public String add() {
    return "用户增加";
}

// 权限校验：必须具有指定权限才能进入该方法 
@SaCheckPermission("user-add")        
@RequestMapping("add")
public String add() {
    return "用户增加";
}

// 二级认证校验：必须二级认证之后才能进入该方法 
@SaCheckSafe()        
@RequestMapping("add")
public String add() {
    return "用户增加";
}

// Http Basic 校验：只有通过 Http Basic 认证后才能进入该方法 
@SaCheckHttpBasic(account = "sa:123456")
@RequestMapping("add")
public String add() {
    return "用户增加";
}

// Http Digest 校验：只有通过 Http Digest 认证后才能进入该方法 
@SaCheckHttpDigest(value = "sa:123456")
@RequestMapping("add")
public String add() {
    return "用户增加";
}

// 校验当前账号是否被封禁 comment 服务，如果已被封禁会抛出异常，无法进入方法 
@SaCheckDisable("comment")                
@RequestMapping("send")
public String send() {
    return "查询用户信息";
}
```

## 七、路由拦截鉴权

假如我们使用注解鉴权实现登录鉴权，即使是在每个类上加注解，也显得麻烦

这个时候我们如果使用**路由拦截鉴权**，将会大大降低代码量

### 1. 注册 Sa-Token 路由拦截器

```java
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer
{

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        // 注册 Sa-Token 拦截器
        registry.addInterceptor(new SaInterceptor(handle ->
        {
            // 除了排除路径，其他路径必须登录
            SaRouter.match("/**")
                    .notMatch("/users/login", "/users", "/users/logout", "/upload/**") // 放行注册、登录和登出及静态资源获取接口
                    .check(r -> StpUtil.checkLogin()); // 其他检查是否登录

        })).addPathPatterns("/**"); // 所有请求都拦截
    }
}
```

> 只需要在`notMatch()`方法内填入不需要拦截的接口，即可实现其他接口统一实现登录鉴权了