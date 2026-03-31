package hiiii113.exception;

import lombok.Getter;

/**
 * 自定义业务异常，包含状态码，返回时直接提取状态码
 */
@Getter
public class ServiceException extends RuntimeException
{
    // 业务状态码常量
    public static final int CODE_SUCCESS = 200;
    public static final int CODE_CREATED = 201;
    public static final int CODE_BAD_REQUEST = 400;
    public static final int CODE_UNAUTHORIZED = 401;
    public static final int CODE_FORBIDDEN = 403;
    public static final int CODE_NOT_FOUND = 404;
    public static final int CODE_ERROR = 500;

    private final String msg; // 返回的错误信息
    private final Integer code; // 返回的状态码

    public ServiceException(String msg, Integer code)
    {
        this.msg = msg;
        this.code = code;
    }
}
