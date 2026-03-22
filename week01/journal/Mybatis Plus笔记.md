# Mybatis Plus笔记

## 1. 前言

>  Mybatis Plus 是对 Mybatis 的一款增强框架，对 Mybatis 拥有完全的兼容性，
>  既可以使用 Mybatis 的语法进行开发，又可以使用 Mybatis Plus 的特性进行开发

官网：[MyBatis-Plus 🚀 为简化开发而生](https://www.baomidou.com/)

## 2. 快速入门

### 2.1 引入 Mybatis Plus 的起步依赖

在官网找到 SpringBoot 对应版本的 Mybatis Plus 的起步依赖

SpringBoot 4 版本的起步依赖如下：

```xml
<dependency>  
    <groupId>com.baomidou</groupId>  
    <artifactId>mybatis-plus-spring-boot4-starter</artifactId>  
    <version>3.5.15</version>  
</dependency>
```

### 2.2 定义 Mapper

自定义的 Mapper 继承 Mybatis Plus 继承的 BaseMapper 接口：

```java
public interface UserMapper extends BaseMapper<User>
{
}
```

继承 BaseMapper 的目的是：BaseMapper 中有 Mybatis Plus 提供的众多的**增删改查**方法：

这些方法就意味着以后不需要再去写这些基本的单表 SQL 语句，节省开发时间

### 2.3 常用注解

Mybatis Plus 是通过扫描实体类，并且基于**反射**获取实体类信息作为数据库表信息的

在前面撰写 UserMapper 的时候，我们继承的 BaseMapper 传入的这个实体类就是需要扫描的实体类

```java
public interface UserMapper extends BaseMapper<User>
{
}
```

但是，Mybatis Plus 并不能通过实体类就知道你的**表中字段**的对应关系

所以，Mybatis Plus 采用了**约定大于配置**的想法

约定如下：

1. 类名驼峰转下划线作为表名
	比如类名 `UserInfo` 约定的表名就是 `user_info`
2. 名为 id 的字段作为**主键（primary key）**
3. 变量名驼峰转下划线作为表的字段名
	比如表中字段 `create_time` 在实体类中就约定是 `createTime`

但是，并不是所有人都喜欢这个约定，所以，Mybatis Plus 中提供了相关注解用于解决该问题

常用注解：
- `@TableName`：用来指定表名
- `@TableId`：用来指定表中的主键字段信息
	TableId 有两个属性
	- `value`：指定表中对应主键的字段名，可以只填这一个字段
	- `type`：用于设置主键的类型，默认为雪花算法
- `@TableField`：用来指定表中的普通字段信息
	- 使用场景：
		- 成员变量名和数据库字段名不一致
		- 成员变量名以 is 开头，且是布尔值
		- 成员变量名和数据库关键字冲突
		- 成员变量不是数据库字段

比如：

```java
// 表名和实体类名不一致
@TableName("tb_user")
public class User  
{  
    // 设置该id字段的类型是自动增加的
    @TableId(type = IdType.AUTO)  
    private int id;
  
    // 表中字段和变量名不一致，用于映射
    @TableField("user_id")  
    private String userNumber;  
  
    // 一样的就不需要加注解
    private String username;  
  
    private String password;
    
    @TableField("is_married")
    private Boolean isMarried;
    
    @TableFiled("`order`")
    private int order;
  
    // 创建时间  
    @TableField(fill = FieldFill.INSERT)  
    private Date createTime;  
  
    // 更新时间  
    @TableField(fill = FieldFill.UPDATE)  
    private Date updateTime;  
}
```


### 2.4 常用配置

MybatisPlus 的配置项继承了 Mybatis 的一些配置项，也带有了自己独特的配置

常见配置如下：

```yml
mybatis-plus:
  type-aliases-package: com.itheima.mp.domain.po # 别名扫描包
  mapper-locations: "classpath*:/mapper/**/*.xml" # Mapper.xml文件地址，默认值
  configuration:
    map-underscore-to-camel-case: true # 是否开启下划线和驼峰的映射
    cache-enabled: false # 是否开启二级缓存
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl # 开启控制台SQL日志
  global-config:
    db-config:
      id-type: assign_id # id为雪花算法生成
      update-strategy: not_null # 更新策略：只更新非空字段
```

其实大部分设置都是有默认值的，只有在有需要特别设置的时候才需要设置

设置选项可以查阅官方文档：[使用配置 | MyBatis-Plus](https://www.baomidou.com/reference/)

一般一个项目在 application.yml 文件需要的配置如下：

```yml
# 1. 服务器端口配置
server:
  port: 8080 # 项目访问端口
  servlet:
    context-path: /api # 统一加个前缀，方便前端管理

# 2. 数据库连接配置
spring:
  datasource:
    # 修改为数据库名
    url: jdbc:mysql://localhost:3306/db?useSSL=false&serverTimezone=GMT%2B8&characterEncoding=utf-8
    username: root
    password: 密码
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  # 3. JSON 日期格式化
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8

# 4. MyBatis-Plus 配置
mybatis-plus:
  # XML 文件存放路径
  mapper-locations: classpath*:/mapper/**/*.xml
  # 实体类所在包
  type-aliases-package: hiiii113.entity
  configuration:
    # 开启驼峰映射
    map-underscore-to-camel-case: true
    # 开启 SQL 日志
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      # ID 类型：auto (数据库自增) 或 assign_id (雪花算法)
      id-type: auto
      # 更新策略：not_null (只更新非 null 字段)
      update-strategy: not_null
```


### 2.5 总结

Mybatis Plus 的使用步骤：

1. 引入起步依赖
2. 自定义 Mapper 并继承 BaseMapper
3. 在实体类上添加注解声明表信息
4. 在 application.yml 里根据需要添加配置

---

## 3. 核心功能

### 3.1 条件构造器

这里面 BaseMapper 提供的方法参数里面的 Wrapper 就是**条件构造器**

#### 3.1.1 概述

Wrapper 类是一个父类，他的下面有几个子类：

在这些条件构造器中，有多种方法可以实现构造复杂的 SQL 条件语句

#### 3.1.2 例子

使用 QueryWrapper 查询 `id, username, info, balance` 字段，条件是名字带 `o` 并且 `balance > 1000`

```java
@Test
void testQueryWrapper()
{
    // 1. 构建查询条件
    QueryWrapper<User> wrapper = new QueryWrapper<User>()
        .select("id", "username", "info", "balance")
        .like("username", "o")
        .ge("balance", 1000);
        
    // 2. 查询
    userMapper.selectList(wrapper);
}
```

需要更新 `username = jack` 的字段的 `balance` 为 2000

```java
@Test
void testUpdateByQueryWrapper()
{
    // 1. 要更新的数据
    User user = new User();
    user.setBalance(2000);
    
    // 2. 更新的条件
    QueryWrapper<User> wrapper = new QueryWrapper<User>().eq("username", "jack");
    
    // 3. 执行更新
    userMapper.update(user, wrapper);
}
```

需要更新 id 为 1, 2, 4 的 用户余额，扣 200

```java
@Test
void testUpdateWrapper()
{
    List<Long> ids = List.of(1L, 2L, 4L);
    
    UpdateWrapper<User> wrapper = new UpdateWrapper<User>()
        .setSql("balance = balance - 200")
        .in("id", ids);
        
    userMapper.update(null, wrapper);
}
```

### 3.2 lambda 语法

前面几个例子虽然可以实现查询功能，但是有缺点

1. 无法让编译器检查字段名
2. 部分地方硬编码严重

学习使用 lambda 语法就可以解决这个问题

**例子：**

同样的需求：使用 QueryWrapper 查询 `id, username, info, balance` 字段，条件是名字带 `o` 并且 `balance > 1000`

```java
@Test
void testLambdaQueryWrapper()
{
    // 1. 构建查询条件
    LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
        .select(User::getId, User::getUsername, User::getInfo, User::getBalance)
        .like(User::getUsername, "o")
        .ge(User::getBalance, 1000);
        
    // 2. 查询
    List<User> users = userMapper.selectList(wrapper);
    users.forEach(System.out::println);
}
```

lmabda 的语法其实就是将手写的**硬编码字符串**部分变成方法名

MybatisPlus 内部基于反射获取这个方法的字段名，甚至可以根据@TableFiled 注解**自动映射**

## 4. 自定义 sql

自定义 sql 的意思就是使用 MybatisPlus 的 Wrapper 来构建 where 条件，自己写剩下的部分

原因是：**对于复杂的业务逻辑，MybatisPlus 无法快速帮助我们构建 sql 语句**

### 4.1 步骤

1. 构建查询 / 更新条件

```java
List<Long> ids = List.of(1L, 2L, 4L);
int amount = 200;

// 1. 构建 Wrapper，这里利用了 LambdaQueryWrapper 构建 WHERE 条件
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().in(User::getId, ids);

// 2. 调用自定义 Mapper 方法
userMapper.updateBalanceByIds(wrapper, amount);
```

2. Mapper 接口定义

```java
// 注意：必须使用 @Param("ew")，这行代码是 MP 的硬性约定
void updateBalanceByIds(@Param("ew") LambdaQueryWrapper<User> wrapper, @Param("amount") int amount);
```

3. xml 映射文件编写

```xml
<update id="updateBalanceByIds">
    UPDATE tb_user 
    SET balance = balance - #{amount} 
    ${ew.customSqlSegment}
</update>
```


## 5. IService

MybatisPlus 不仅提供了 BaseMapper 这种方法用于帮助写 sql 语句，同时提供了 Iservice 这个类帮助我们实现了很多很多 service 层代码

### 5.1 使用步骤

MybatisPlus 里面有一个 IService 的接口，声明大量方法

而 IService 又有一个具体的实现类叫做 ServiceImpl

**所以我们只要接口继承 IService，实现类继承 ServiceImpl 即可**

示例：

UserService 接口：

```java
public interface UserService extends IService<User>
```

UserServiceImpl 实现类：

```java
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService
```

注意这里的实现类继承的时候需要填入两个泛型：一个 **mapper 类**和一个**实体类**

### 5.3 常用方法

IService 中有许多实用的方法，如下：

**增：**

| 方法                                                 | 说明                       |
| ---------------------------------------------------- | -------------------------- |
| `save(T entity)`                                     | 插入一条记录               |
| `saveBatch(Collection<T> entityList)`                | 批量插入                   |
| `saveBatch(Collection<T> entityList, int batchSize)` | 批量插入（指定每批数量）   |
| `saveOrUpdate(T entity)`                             | 有 id 则更新，无 id 则插入 |
| `saveOrUpdateBatch(Collection<T> entityList)`        | 批量保存或更新             |

**删：**

| 方法                                                     | 说明              |
| -------------------------------------------------------- | ----------------- |
| `removeById(Serializable id)`                            | 根据 id 删除      |
| `removeByIds(Collection<? extends Serializable> idList)` | 根据 id 批量删除  |
| `remove(Wrapper<T> queryWrapper)`                        | 根据条件删除      |
| `removeByMap(Map<String, Object> columnMap)`             | 根据 Map 条件删除 |

**改：**

| 方法                                                       | 说明                            |
| ---------------------------------------------------------- | ------------------------------- |
| `updateById(T entity)`                                     | 根据 id 更新（null 字段不更新） |
| `update(T entity, Wrapper<T> updateWrapper)`               | 根据条件更新                    |
| `updateBatchById(Collection<T> entityList)`                | 批量根据 id 更新                |
| `updateBatchById(Collection<T> entityList, int batchSize)` | 批量更新（指定每批数量）        |

**查：（单个）**

| 方法                                                         | 说明                                           |
| ------------------------------------------------------------ | ---------------------------------------------- |
| `getById(Serializable id)`                                   | 根据 id 查询一条                               |
| `getOne(Wrapper<T> queryWrapper)`                            | 根据条件查询一条                               |
| `getOne(Wrapper<T> queryWrapper, boolean throwEx)`           | 查询一条，`false` 时无结果返回 null 而不抛异常 |
| `getMap(Serializable id)`                                    | 根据 id 查询，返回 Map                         |
| `getObj(Serializable id, Function<? super Object, V> mapper)` | 根据 id 查询，返回指定类型                     |

**查：（列表）**

| 方法                                                   | 说明                     |
| ------------------------------------------------------ | ------------------------ |
| `list()`                                               | 查询所有                 |
| `list(Wrapper<T> queryWrapper)`                        | 根据条件查询列表         |
| `listByIds(Collection<? extends Serializable> idList)` | 根据 id 批量查询         |
| `listByMap(Map<String, Object> columnMap)`             | 根据 Map 条件查询列表    |
| `listMaps(Wrapper<T> queryWrapper)`                    | 查询列表，每条记录为 Map |