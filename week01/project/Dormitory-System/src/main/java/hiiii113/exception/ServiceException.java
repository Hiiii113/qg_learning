package hiiii113.exception;

import lombok.Getter;

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
