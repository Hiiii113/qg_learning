package hiiii113.exception;

import hiiii113.util.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(ServiceException.class)
    public Result<Void> handleServiceException(ServiceException e)
    {
        return Result.error(e.getMsg(), e.getCode());
    }

    @ExceptionHandler(SQLException.class)
    public Result<Void> handleSQLException(SQLException e)
    {
        e.printStackTrace();
        return Result.error("数据库错误！", 500);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e)
    {
        e.printStackTrace();
        return Result.error("服务器内部错误！", 500);
    }
}
