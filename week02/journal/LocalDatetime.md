# LocalDateTime

## 一、LocalDateTime 和 datetime

在数据库中存储时间我们一般使用的是`datetime`这个类型，而在实体类中我们通常使用`LocalDateTime`与之对应，这就会产生问题：

**如何转化？**

> 这种功能其实在 JDBC 底层就已经实现，只需要使用`getObject()`方法即可：
>
> ```
> ResultSet rs = statement.executeQuery("SELECT create_time FROM log");
> LocalDateTime time = rs.getObject("create_time", LocalDateTime.class); // 通过反射转化
> ```
>
> 而在 Mybatis Plus 中，由于我们不需要手动获取结果，所以是 Mybatis Plus 自动转化的

然而，当我们想要以字符串形式输出时，就会带来问题：**如何输出？**

1. 使用 `toString()`方法

   ```java
   String str2 = now.toString(); // 输出：2026-03-29T14:30:00，带了一个 T 不好看
   ```

2. 使用`DateTimeFormatter`

   ```java
   DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
   String str = now.format(formatter);
   // 输出：2026-03-29 14:30:00，自定义格式
   ```

**而在后端返回前端的过程中，我们需要将`LocalDateTime`转化成 JSON 格式返回，这个时候就需要用 Jackson 了**

Jackson 是 SpringBoot 自带的转化器，但是默认会将`LocalDateTime`转化成`yyyy-MM-ddTHH:mm:ss`的格式，中间有个 T，不好看

我们只需要在实体类的`LocalDateTime`数据上加上`@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")`注解即可让 Jackson 以指定方式转化
