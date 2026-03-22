# SpringBoot

## 1. 概述

> SpringBoot 就是 Spring 的快速启动框架，其中的核心理念是“**约定优于配置**”，使得你可以用最少的配置快速搭建一个可运行的 Spring 应用

Spring 有以下几个特点来快速配置 Spring：

- 自动配置

  - 只要引入了 spring-boot-starter-web 依赖，SpringBoot 自动帮你配好：Tomcat、Spring MVC、Jackson 等

- 起步依赖

  - 只需一个依赖，可以自动引入一系列相关依赖，并且版本自动管理

  - ```
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    ```

  - 常用 starter：

  - | Starter                        | 用途                                 |
    | ------------------------------ | ------------------------------------ |
    | `spring-boot-starter-web`      | Web 开发（内含 Tomcat + Spring MVC） |
    | `spring-boot-starter-data-jpa` | 数据库 JPA 操作                      |
    | `spring-boot-starter-security` | 安全认证                             |
    | `spring-boot-starter-test`     | 单元测试                             |

- 内嵌服务器

  - 传统方式：打 WAR 包 → 部署到外部 Tomcat
  - SpringBoot：打 JAR 包 → java -jar 直接运行，自带 Tomcat

- 开箱即用

  - 导入起步依赖后，即可快速创建一个 Spring 应用，无需任何配置

---

## 2. 创建 SpringBoot 项目

在 idea 中创建一个 Maven 项目

![image-20260321184645664](D:\Typora\repo\计算机\后端\assets\image-20260321184645664-1774090009432-1.png)

导入 SpringBoot 的父工程

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.5</version>
    <relativePath/>
</parent>
```

也可以直接在 idea 中使用自带的 Spring Boot 选项创建，这种方式无需引入 SpringBoot 的父工程

<img src="D:\Typora\repo\计算机\后端\assets\image-20260321185330216-1774090413969-3.png" alt="image-20260321185330216" style="zoom: 80%;" />

随后导入`spring-boot-starter-web`的起步依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

编写引导类：

引导类是一个程序的入口，和使用 Tomcat 不同，使用 SpringBoot 的项目直接运行引导类即可运行服务器

示例代码：

```java
package hiiii113;  
  
import org.springframework.boot.SpringApplication;  
import org.springframework.boot.autoconfigure.SpringBootApplication;  
  
@SpringBootApplication  
public class HelloApplication  
{  
    public static void main(String[] args)  
    {  
    	// 传入引导类的class对象和args参数列表
        SpringApplication.run(HelloApplication.class, args);  
    }  
}
```

## 3. SpringBoot 整合 Spring MVC

### 3.1 实现第一个 api 接口

使用 SpringBoot 来写 Spring MVC 不需要那么麻烦，步骤如下：

1. 创建一个请求处理类，并加上`@RestController`注解即可
2. 添加具体的方法，并在方法名上加上`@RequestMapping("/xxx")`标识匹配的路径即可
3. 可以根据需要在类名上加上注解`@RequestMapping("/xxx")`，代表的是给这个类中所有方法加上前缀

```java
@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/hello")
    @ResponseBody
    public String hello()
    {
        return "Hello, Spring MVC!";
    }

    @RequestMapping("/info")
    @ResponseBody
    public String info()
    {
        return "这是用户信息接口";
    }
}
```

随后找到启动类：

```java
@SpringBootApplication
public class Application
{
    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }
}
```

运行即可

### 3.2 yml 配置文件修改配置

如果你使用的是 idea 内置的 SpringBoot 选项创建的 SpringBoot，那么就可以在 resource 目录下找到 application.yml（application.yaml）文件

这其中有许多配置可以设置，这里只介绍两个常用的配置：

- 修改 Tomcat 服务器默认端口
- 增加项目路径前缀

```yml
server:
  port: 9090
  servlet:
    context-path: /api
```

### 3.3 实现 Restful 请求

Restful 其实就是：**用 URL 定位资源，用 HTTP 方法描述操作**

- URL 定位资源：URL 中的单词被规定是表示**资源**的，而不能是包含表示操作、动作的，且资源名要是**复数形式**

  - `"/getUser?id=1"`❌
  - `"/users/1"`✅

- HTTP 方法表示操作

  - 在日常使用的 GET、POST 请求之外，还有两种请求：PUT 和 DELETE，我们需要根据日常需求选择使用

    <img src="D:\Typora\repo\计算机\后端\assets\image-20260321191719807-1774091841844-5.png" alt="Restful请求类型" style="zoom: 67%;" />

示例：

```java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users") // 资源名称
public class UserController
{
    // 查询所有 GET /users
    @GetMapping
    public List<User> list()
    {
        return userService.findAll();
    }

    // 查询单个 GET /users/1
    @GetMapping("/{id}")
    public User getById(@PathVariable Long id)
    {
        return userService.findById(id);
    }

    // 新增 POST /users
    @PostMapping
    public User add(@RequestBody User user)
    {
        return userService.save(user);
    }

    // 修改 PUT /users/1
    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User user)
    {
        return userService.update(id, user);
    }

    // 删除 DELETE /users/1
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id)
    {
        userService.delete(id);
    }
}
```

在 SpringBoot 当中接收不同形式的 HTTP 方法有对应的 Mapping 注解，加上即可接收

需要的值可以使用`{xx}`或者直接接收 JSON 数据来实现（GET 请求只能用`{xx}`形式）

> `@RequestBody`：将接收到的 JSON 数据直接转化成对应的对象（dto）
>
> `@PathVariable`：接收在 URL 里面的值

## 4. SpringBoot 多环境配置和修改项目启动水印

### 4.1 多环境配置

在项目开发过程中，我们可能遇到不同的配置，比如在开发环境、测试环境和应用环境时，分别拥有不同的配置

我们可以通过这样的结构实现多环境配置：

```
src/main/resources/
├── application.yml          # 主配置文件
├── application-dev.yml      # 开发环境
├── application-test.yml     # 测试环境
└── application-prod.yml     # 生产环境
```

**主配置文件：**用于指定激活哪一个配置

```
spring:
  profiles:
    active: dev    # 切换环境，改成 test 或 prod 即可
```

**开发环境：**application-dev.yml

```yml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb_dev
    username: root
    password: 123456
```

**测试环境：**application-test.yml

```yml
server:
  port: 8081
  servlet:
    context-path: /test-api

spring:
  datasource:
    url: jdbc:mysql://192.168.1.100:3306/mydb_test
    username: test_user
    password: test_123
```

**生产环境：**application-prod.yml

```yml
server:
  port: 9090
  servlet:
    context-path: /prod-api

spring:
  datasource:
    url: jdbc:mysql://10.0.0.1:3306/mydb_prod
    username: prod_user
    password: prod_456
```

**需要使用哪一个环境直接在主配置文件修改即可！**

### 4.2 修改项目启动水印

运行 SpringBoot 的启动类后，会发现 SpringBoot 的水印

![SpringBoot水印](D:\Typora\repo\计算机\后端\assets\image-20260321201241001-1774095163688-7.png)

我们只需要在 resource 目录下添加 banner.txt 文件，并写入想要显示的效果即可

![Hiiii113项目水印](D:\Typora\repo\计算机\后端\assets\image-20260321201345646-1774095227612-9.png)

可以使用网站：[Text to ASCII Art Generator (TAAG)](http://patorjk.com/software/taag/#p=display&f=Graffiti&t=Type+Something+&x=none&v=4&h=4&w=80&we=false)



