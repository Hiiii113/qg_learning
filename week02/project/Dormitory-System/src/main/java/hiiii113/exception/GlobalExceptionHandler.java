package hiiii113.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import hiiii113.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * 全局异常拦截器，统一处理异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler
{
    // 业务异常，返回前端
    @ExceptionHandler(ServiceException.class)
    public Result<Void> handleServiceException(ServiceException e)
    {
        log.error("业务异常: ", e);
        return Result.error(e.getMsg(), e.getCode());
    }

    // 未登录异常，返回前端
    @ExceptionHandler({NotLoginException.class})
    public Result<Void> handleNotLoginException(NotLoginException e)
    {
        log.error("未登录异常: ", e);
        return Result.error("请登录再访问！", 401);
    }

    // 无权限异常，返回前端
    @ExceptionHandler({NotPermissionException.class})
    public Result<Void> handleNotPermissionException(NotPermissionException e)
    {
        log.error("无权限异常: ", e);
        return Result.error("该账号无权限！", 401);
    }

    // 数据库异常，返回前端
    @ExceptionHandler(SQLException.class)
    public Result<Void> handleSQLException(SQLException e)
    {
        log.error("数据库异常: ", e);
        return Result.error("数据库错误！", 500);
    }

    // 未知异常，返回前端
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e)
    {
        log.error("未知异常: ", e);
        return Result.error("服务器内部错误！", 500);
    }
}
