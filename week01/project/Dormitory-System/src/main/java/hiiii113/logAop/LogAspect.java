package hiiii113.logAop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;

/**
 * 日志记录 aop
 */
@Component
@Aspect
@Slf4j
public class LogAspect
{
    // Jackson
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 遇到 @LogAnnotation 的时候进行切入
    @Pointcut("@annotation(hiiii113.logAop.LogAnnotation)")
    public void pointcut()
    {
    }

    // 环绕通知
    @Around("pointcut()") // 遇到pointcut()这个函数对应的注解的时候切入
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable
    {
        try
        {
            // 记录开始时间
            long beginTime = System.currentTimeMillis();
            // 执行业务方法
            Object result = joinPoint.proceed();
            // 计算执行时长（毫秒）
            long time = System.currentTimeMillis() - beginTime;
            // 保存日志
            recordLog(joinPoint, time);
            return result;
        }
        // 防止日志记录的时候出现异常
        catch (Throwable e)
        {
            log.error("接口异常: {}, 消息: {}",
                    e.getClass().getSimpleName(),
                    e.getMessage());
            throw e;
        }
    }

    // 保存日志
    private void recordLog(ProceedingJoinPoint joinPoint, long time) throws JsonProcessingException
    {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 通过方法签名拿到 Method 对象
        Method method = signature.getMethod();
        // 从方法上拿到 @logAnnotation 注解对象
        LogAnnotation logAnnotation = method.getAnnotation(LogAnnotation.class);
        // 打印
        log.info("===================log start=======================");
        log.info("module:{}", logAnnotation.module());
        log.info("operator:{}", logAnnotation.operator());

        // 请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        log.info("request method:{}", className + '.' + methodName + "()");

        // 请求的参数名
        Object[] args = joinPoint.getArgs();
        // 参数
        String params;
        if (args == null || args.length == 0)
        {
            params = "无参数";
        }
        else
        {
            // 把 MultipartFile 替换成文件名，其他参数正常序列化
            Object[] safeArgs = new Object[args.length];
            for (int i = 0; i < args.length; i++)
            {
                if (args[i] instanceof MultipartFile file)
                {
                    safeArgs[i] = "[文件:" + file.getOriginalFilename() + "]";
                }
                else
                {
                    safeArgs[i] = args[i];
                }
            }
            params = objectMapper.writeValueAsString(safeArgs.length == 1 ? safeArgs[0] : safeArgs);
        }
        // 打印
        log.info("params:{}", params);
        log.info("time : {} ms", time);
        log.info("===================log end=======================");
    }
}
