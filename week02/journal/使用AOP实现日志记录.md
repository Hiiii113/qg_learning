# 使用AOP实现日志记录

Spring 的核心思想之一 AOP （面向切面编程）的一个典型应用就是实现日志记录

拥有了 AOP 后，无需在每个方法上添加输出日志的代码，即可实现日志记录，大大节省重复代码量

> 这里使用的是 MYSQL 数据库保存日志实体，并借此实现查询日志的接口

## 一、安装依赖

本笔记使用的是 SpringBoot 实现，需要检查 `pom.xml` 中是否含有以下三项依赖：

```xml
<!-- Spring Boot Starter Web（包含 AOP） -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- AOP -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>

<!-- Lombok（省略 getter/setter 和日志声明） -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
```

## 二、编写注解

我们需要编写自定义注解类，用于标识哪一个方法、类需要记录日志

```java
@Target({ElementType.METHOD}) // 只作用于方法
@Retention(RetentionPolicy.RUNTIME) // 运行时生效
public @interface LogAnnotation
{
    // 模块名
    String module() default "";

    // 操作名
    String operator() default "";
}
```

> 可以自行更改注解名和内含的信息，这里以**模块--操作方法**为例

## 三、编写日志表和对应实体

```sql
-- 日志表
CREATE TABLE `sys_operation_log`
(
    id             int          NOT NULL AUTO_INCREMENT COMMENT '日志ID（自增主键）',
    user_number    varchar(50)  NOT NULL COMMENT '操作用户',
    operation_time datetime     NOT NULL COMMENT '操作时间',
    module         varchar(100) NOT NULL COMMENT '操作模块（如：用户管理）',
    operator       varchar(255)          DEFAULT NULL COMMENT '操作描述（如：新增用户）',
    method         varchar(255) NOT NULL COMMENT '操作方法全路径',
    params         text COMMENT '方法参数（JSON格式）',
    result         text COMMENT '操作结果（成功/失败，JSON格式）',
    exception      text COMMENT '异常信息（失败时记录）',
    cost_time      bigint                DEFAULT NULL COMMENT '操作耗时（毫秒）',
    client_ip      varchar(50)           DEFAULT NULL COMMENT '客户端IP',
    create_time    datetime     not null default current_timestamp comment '创建时间',
    PRIMARY KEY (id),
    KEY idx_operation_time (operation_time) COMMENT '按操作时间查询索引',
    KEY idx_username (user_number) COMMENT '按用户查询索引'
) COMMENT ='系统操作日志表';
```

```java
@Data
@TableName("sys_operation_log")
public class Log
{
    // 日志 id
    @TableId(type = IdType.AUTO)
    private int id;

    // 操作用户
    private String userNumber;

    // 操作时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operationTime;

    // 操作模块
    private String module;

    // 操作名
    private String operator;

    // 操作方法全路径
    private String method;

    // 方法参数(JSON格式)
    private Object params;

    // 操作结果
    private Object result;

    // 异常信息(失败时记录)
    private String exception;

    // 操作耗时
    private Long costTime;

    // 客户端IP
    private String clientIp;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
```

## 四、编写切面类（核心）

为了让日志的保存和日志的提取分开来，先新建一个`LogHandler`接口

```java
public interface LogHandler
{
    // 保存log实体
    void saveOperationLog(Log log) throws JsonProcessingException;
}
```

再写`LogAspect`用于实现 AOP 提取日志

```java
@Component
@Aspect
@Slf4j
@RequiredArgsConstructor // 使用lombok自动注入所需的
public class LogAspect
{
    // Jackson
    private final ObjectMapper objectMapper;
    // LogHandler
    private final LogHandler logHandler;

    // 遇到 @LogAnnotation 的时候进行切入
    @Pointcut("@annotation(hiiii113.log.LogAnnotation)")
    public void pointcut()
    {
    }

    // 环绕通知
    @Around("pointcut()") // 遇到pointcut()这个函数对应的注解的时候切入
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable
    {
        // 记录方法开始时间
        long startTime = System.currentTimeMillis();

        // 初始化日志实体
        Log logEntity = new Log();
        logEntity.setOperationTime(LocalDateTime.now()); // 操作时间(当前时间)

        // 获取客户端IP（从请求上下文获取）
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null)
        {
            HttpServletRequest request = requestAttributes.getRequest();
            logEntity.setClientIp(request.getRemoteAddr()); // 客户端IP
        }

        // 获取方法信息（全路径、参数）
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 方法全路径：包名 + 类名 + 方法名
        logEntity.setMethod(method.getDeclaringClass().getName() + "." + method.getName());
        // 方法参数：数组转字符串
        logEntity.setParams(joinPoint.getArgs());

        // 获取 @LogAnnotation 注解信息
        LogAnnotation logAnnotation = method.getAnnotation(LogAnnotation.class);
        logEntity.setModule(logAnnotation.module());
        logEntity.setOperator(logAnnotation.operator());

        // 获取操作用户(使用 Sa-Token 的方法)
        Object loginId = StpUtil.getLoginIdDefaultNull();
        logEntity.setUserNumber(loginId != null ? loginId.toString() : "-");

        Object businessResult; // 业务方法返回结果
        try
        {
            // 执行目标业务方法（核心业务逻辑）
            businessResult = joinPoint.proceed();
            // 方法执行成功：标记结果
            logEntity.setResult(businessResult);
        }
        catch (Exception e)
        {
            // 方法执行失败：记录异常信息
            logEntity.setResult("失败");
            logEntity.setException(e.getMessage()); // 异常信息（简化，可记录堆栈）
            throw e; // 重新抛出异常，不影响原有业务异常处理逻辑
        }
        finally
        {
            // 计算操作耗时（结束时间-开始时间）
            logEntity.setCostTime(System.currentTimeMillis() - startTime);

            // 8. 保存日志（调用业务模块实现的LogHandler）
            saveOperationLog(logEntity);
        }

        // 返回业务方法结果，不影响业务流程
        return businessResult;
    }

    private void saveOperationLog(Log logEntity)
    {
        try
        {
            logHandler.saveOperationLog(logEntity);
        }
        catch (Exception e)
        {
            // 日志保存失败不影响主业务，仅记录日志告警
            log.error("记录系统操作日志失败，日志信息：{}", logEntity, e);
        }
    }
}
```

再写好保存 Log 实体到数据库的`LogHandlerImpl`实现类

```java
@Component
@RequiredArgsConstructor
public class LogHandlerImpl implements LogHandler
{
    // 注入
    private final LogMapper logMapper;
    private final ObjectMapper objectMapper;

    @Override
    public void saveOperationLog(Log log) throws JsonProcessingException
    {
        // 将Object类型的params/result序列化为JSON字符串
        if (log.getParams() != null)
        {
            log.setParams(objectMapper.writeValueAsString(log.getParams()));
        }
        if (log.getResult() != null)
        {
            log.setResult(objectMapper.writeValueAsString(log.getResult()));
        }

        // 调用MyBatis Mapper插入数据库
        logMapper.insert(log);
    }
}
```

## 五、使用

只需要在需要记录日志的类、方法上面加上`@LogAnnotation`注解，即可生成日志

```java
// 获取用户信息
@LogAnnotation(module = "users", operator = "获取用户信息")
@GetMapping("/info")
@SaCheckLogin
public Result<User> getUserInfo()
{
    String userNumber = StpUtil.getLoginIdAsString();
    User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUserNumber, userNumber));
    return Result.success("查询成功！", user, 200);
}
```