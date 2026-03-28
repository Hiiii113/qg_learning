package hiiii113.exception;

import lombok.Getter;

/**
 * 自定义业务异常，包含状态码，返回时直接提取状态码
 */
@Getter
public class ServiceException extends RuntimeException
{
    private final String msg; // 返回的错误信息
    private final Integer code; // 返回的状态码

    public ServiceException(String msg, Integer code)
    {
        this.msg = msg;
        this.code = code;
    }
}
