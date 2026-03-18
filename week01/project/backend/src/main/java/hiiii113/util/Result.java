package hiiii113.util;

import lombok.Data;

@Data
public class Result<T>
{
    private String msg;
    private Integer code;
    private T data;

    public static <T> Result<T> success(String msg, T data, Integer code)
    {
        Result<T> result = new Result<>();
        result.msg = msg;
        result.code = code;
        result.data = data;
        return result;
    }

    public static Result<Void> success(String msg, Integer code)
    {
        Result<Void> result = new Result<>();
        result.msg = msg;
        result.code = code;
        result.data = null;
        return result;
    }

    public static <T> Result<T> error(String msg, T data,Integer code)
    {
        Result<T> result = new Result<>();
        result.msg = msg;
        result.code = code;
        result.data = null;
        return result;
    }

    public static Result<Void> error(String msg, Integer code)
    {
        Result<Void> result = new Result<>();
        result.msg = msg;
        result.code = code;
        result.data = null;
        return result;
    }
}