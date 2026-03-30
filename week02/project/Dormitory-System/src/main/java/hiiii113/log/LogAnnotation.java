package hiiii113.log;

import java.lang.annotation.*;

/**
 * 日志记录的注解
 */
@Target({ElementType.METHOD}) // 只作用于方法
@Retention(RetentionPolicy.RUNTIME) // 运行时生效
public @interface LogAnnotation
{
    // 模块名
    String module() default "";

    // 操作名
    String operator() default "";
}
