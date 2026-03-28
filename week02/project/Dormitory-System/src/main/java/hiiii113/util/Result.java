package hiiii113.util;

import lombok.Data;

/**
 * 前端统一返回值
 * @param <T> 泛型
 */
@Data
public class Result<T>
{
    // 提示信息
    private String msg;
    // 状态码
    private Integer code;
    // 数据
    private T data;

    // 带数据的 success 方法
    public static <T> Result<T> success(String msg, T data, Integer code)
    {
        Result<T> result = new Result<>();
        result.msg = msg;
        result.code = code;
        result.data = data;
        return result;
    }

    // 不带数据的 success 方法
    public static Result<Void> success(String msg, Integer code)
    {
        Result<Void> result = new Result<>();
        result.msg = msg;
        result.code = code;
        result.data = null;
        return result;
    }

    // 带数据的 error 方法
    public static <T> Result<T> error(String msg, T data,Integer code)
    {
        Result<T> result = new Result<>();
        result.msg = msg;
        result.code = code;
        result.data = null;
        return result;
    }

    // 不带数据的 error 方法
    public static Result<Void> error(String msg, Integer code)
    {
        Result<Void> result = new Result<>();
        result.msg = msg;
        result.code = code;
        result.data = null;
        return result;
    }
}