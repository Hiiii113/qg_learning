# Spring 核心思想

## 前言

Spring 框架是现在 Java 企业开发的标准，从 2003 年创建过后，Spring 框架越来越完善，解决了 Java 开发中很多复杂问题，是目前 Java 生态中最主流的框架之一。

Spring 框架对于 Java 开发的方方面面都有涉及，被称为 **Spring 全家桶**

分别有以下几种：

- Web：Spring Web MVC、Spring Web Flux
- 持久层：Spring Data、Srping Data Redis、Srping Data MongoDB
- 安全校验：Spring Security
- 构建工程脚手架：Spring Boot
- 微服务：Srping Cloud

**而在Spring当中，有三个核心思想需要学习：**

- IOC（控制反转）
- DI（依赖注入）
- AOP（面向切口编程）

## 1. IOC（控制反转）

IOC 是 Spring 全家桶各个功能模块的基础，**构建对象的容器**

所谓的**控制反转**的意思就是：常规情况下，对象都是开发者根据需求自行创建的，但是当项目越复杂，这种方式越来越不方便，所以**控制反转就是将对象的创建进行反转**，开发者不需要手动 new 对象，而是利用 IOC 容器，根据需求由 IOC 帮助创建和管理

>  不用 IOC：所有对象由开发者自行创建
>
> 使用IOC：所有对象由 Spring 框架来帮助创建

IOC 容器的使用方式有两种：**基于 XML**和**基于注解**

其中基于注解更加常用，基于XML很少使用

### 1.1.1 基于 XML

基于 XML 的方法其实就是：开发者把需要的对象在 XML 中进行配置，Spring 框架读取这个配置，根据配置文件的内容创建对象

示例：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- 在这里配置 Bean -->
    <bean id="user" class="hiiii113.User">
	<!-- id: 创建出的对象的名字 class: 根据哪个类来创建，写全限定类名 -->
    	<property name="username" value="zhangsan"></property>
    	<property name="password" value="123456"></property>
    </bean>
</beans>
```

通过这个方法，我们就成功的使用 XML 来操作 IOC 为我们创建对象了，那么如何调用呢？

```java
public static void main(String[] args)
{
	// 通过 XML 文件名创建一个 IOC 容器
	ApplicationContext context = new ClassPathXmlApplicationContext("IOC.xml");
    // 从 IOC 容器中使用 getBean方法获取对象
	System.out.println(context.getBean("user"));
}
```

这样，我们就成功的基于 XML 使用 IOC 创建了对象并调用了！

>这里`getBean`方法的传入值可以是 Bean 的名称或者类字面量
>
>也就是说使用`context.getBean("User.class")`也是可以的

### 1.1.2 基于注解

前面基于 XML 的使用方式还是过于繁琐了，因此，基于注解的方法才是现在主流的方法

其中，基于注解也有两种方式：**配置类**和**扫包+注解**

#### 1.1.2.1 配置类

配置类其实就是把 XML 配置文件更换成一个 Java 类

示例：

```java
@Configuration
public class BeanConfiguration
{
	// 我们只需要创建一个类，并让这个类返回一个对象即可
    @Bean
    public User user()
    {
        User user = new user();
        user.setUsername("zhangsan");
        user.setPassword("123456");
        return user;
    }
}
```

> `@Configuration`标识了这是一个配置类
>
> `@Bean`标识了这是一个方法

这样，就替代了使用 XML 的方式了

那么如何使用呢？

```java
public static void main(String[] args)
{
	// 通过配置类创建一个 IOC 容器
	ApplicationContext context = new AnnotationConfigApplicationContext(BeanConfiguration.class)
    // 从 IOC 容器中使用 getBean方法获取对象
	System.out.println(context.getBean(BeanConfiguration.class));
}
```

和使用配置类不同的是，我们需要使用`AnnotationConfigApplicationContext`方法通过**反射**获取IOC容器，这里也可以填入配置类的类字面量，也可以指定包名，让 Spring 自动扫包

> 可以在配置类的`@Bean`注解中使用`@Bean(name="user")`或者`@Bean(value="user")`指定 Bean 的名称
>
> 如果不指定则默认是**方法名**

#### 1.1.2.2 扫包+注解

前面的方法都是需要单独撰写一个**中间体**来达到创建对象的目的，比较麻烦

而**扫包+注解**的方式就可以实现**类 -> 对象**的直接创建

我们需要把目标类变成这样：

```java
@Data
@Component
public class User
{
    @value("zhangsan")
	private String username;
	@value("123456")
    private String password;
}
```

加上的`@Component`就是这种方式的**核心**

此时的使用方法：

```java
public static void main(String[] args)
{
	// 通过扫包创建一个 IOC 容器
	ApplicationContext context = new ClassPathXmlApplicationContext("hiiii113.entity");
    // 从 IOC 容器中使用 getBean方法获取对象
	System.out.println(context.getBean(User.class));
}
```

> 可以在`@Component`后面加上名字给该`Bean`取名
>
> 比如：`@Component("user")`

---

## 2. DI（依赖注入）

DI 和 IOC 拥有很强的连接性

> IOC 是让容器帮助你创建一个对象，不再需要手动创建
>
> DI 是在一个类中需要使用到其他对象的时候，由 Spring 框架自动注入
>
> 也就是说：**IOC 是思想，而 DI 是具体的使用方式**

DI 有三种使用方式：

### 2. 1 字段注入

```java
public class Service
{
	@Autowired
	private User user;
}
```

通过`@Autowired`注解，让 Spring通过类型名（这里是`User`）查找对象并注入到这个类里面

### 2.2 构造器注入（推荐）

```java
public class Service
{
	private final User user;
	
	@Autowired // Spring 4.3+且单构造器可省略
	public Service(User user)
	{
		this.user = user;
	}
}
```

### 2.3 Setter 注入

```java
public class Service
{
	private User user;
	
	// 通过Setter方法注入
	@Autowired
	public void setUserService(UserService userService)
	{
		this.userService = userService;
	}
}
```

---

## 3. AOP（面向切面编程）

面向切面编程，是一种**抽象化的面向对象编程**，可以认为是对面向对象编程的一种**补充**

> 比如对于一个项目中，有许多业务方法，我们需要给这些方法加上**打印日志**的功能，也就是在每一个方法的固定位置加上一串代码打印相关参数，可是这样过于麻烦
>
> 此时如果使用 AOP，我们就可以把这些非业务逻辑在统一的地方处理，实现核心业务和非核心业务的解耦合

实现过程图示：

<img src="D:\Typora\repo\计算机\后端\assets\image-20260321160241543-1774080178927-1.png" alt="面向切面编程的形象化表示" style="zoom: 80%;" />

![AOP的实现过程](D:\Typora\repo\计算机\后端\assets\image-20260321162150677-1774081314414-3.png)

### 3.1 实现业务代码

首先，我们简单的实现一个计算器的类和他的实现类

接口：

```
public interface Counter
{
    int add(int num1, int num2);

    int sub(int num1, int num2);

    int mul(int num1, int num2);

    int div(int num1, int num2);
}
```

实现类：

```java
@Component
public class CounterImpl1 implements Counter
{
    @Override
    public int add(int num1, int num2)
    {
        int res = num1 + num2;
        return res;
    }

    @Override
    public int sub(int num1, int num2)
    {
        int res = num1 - num2;
        return res;
    }

    @Override
    public int mul(int num1, int num2)
    {
        int res = num1 * num2;
        return res;
    }

    @Override
    public int div(int num1, int num2)
    {
        int res = num1 / num2;
        return res;
    }
}

```

### 3.2 创建切面类

```java
@Component
@Aspect // 声明这个类是一个切面类
public class LoggerAspect
{
    // Before: 方法生效前处理，execution(返回类型 + 全限定类名.方法名)
    @Before("execution(public int hiiii113.impl.CounterImpl1.*(..))")
    // 使用JoinPoint获取对应的方法信息
    public void before(JoinPoint joinPoint)
    {
        // 获取方法名
        String name = joinPoint.getSignature().getName();
        // 获取参数列表
        System.out.println(name + "方法的参数是：" + Arrays.toString(joinPoint.getArgs()));
    }

    // 使用returning绑定现有参数result，实现将返回值和result绑定
    @AfterReturning(value = "execution(public int hiiii113.impl.CounterImpl1.*(..))", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result)
    {
        // 获取方法名
        String name = joinPoint.getSignature().getName();
        // 获取参数列表
        System.out.println(name + "方法的结果是：" + result);
    }
}
```

### 3.3 配置扫包和自动生成代理

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context https://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/aop https://www.springframework.org/schema/aop/spring-aop.xsd">

    <!-- 自动扫包 -->
    <context:component-scan base-package="hiiii113"></context:component-scan>

    <!-- 开启自动生成代理 -->
    <aop:aspectj-autoproxy></aop:aspectj-autoproxy>
</beans>
```

### 3.4 使用

```java
public class Test
{
    public static void main(String[] args)
    {
        ApplicationContext context = new ClassPathXmlApplicationContext("IOC.xml");
        Counter counter = context.getBean(Counter.class);
        System.out.println(counter.add(8, 9));
        System.out.println(counter.sub(8, 9));
        System.out.println(counter.mul(8, 9));
        System.out.println(counter.div(8, 9));
    }
}
```

结果：

![运行结果](D:\Typora\repo\计算机\后端\assets\image-20260321165702666.png)