package hiiii113.log;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import hiiii113.entity.Log;
import hiiii113.service.LogHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 日志记录 aop
 */
@Component
@Aspect
@Slf4j
@RequiredArgsConstructor // 使用lombok自动注入所需的
public class LogAspect
{
    // Jackson
    private final ObjectMapper objectMapper;
    // LogHandler
    private final LogHandler logHandler;

    // 遇到 @LogAnnotation 的时候进行切入
    @Pointcut("@annotation(hiiii113.log.LogAnnotation)")
    public void pointcut()
    {
    }

    // 环绕通知
    @Around("pointcut()") // 遇到pointcut()这个函数对应的注解的时候切入
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable
    {
        // 记录方法开始时间
        long startTime = System.currentTimeMillis();

        // 初始化日志实体
        Log logEntity = new Log();
        logEntity.setOperationTime(LocalDateTime.now()); // 操作时间(当前时间)

        // 获取客户端IP（从请求上下文获取）
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null)
        {
            HttpServletRequest request = requestAttributes.getRequest();
            logEntity.setClientIp(request.getRemoteAddr()); // 客户端IP
        }

        // 获取方法信息（全路径、参数）
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 方法全路径：包名 + 类名 + 方法名
        logEntity.setMethod(method.getDeclaringClass().getName() + "." + method.getName());
        // 方法参数：数组转字符串
        logEntity.setParams(Arrays.toString(joinPoint.getArgs()));

        // 获取@OperationLog注解信息
        LogAnnotation logAnnotation = method.getAnnotation(LogAnnotation.class);
        logEntity.setModule(logAnnotation.module());
        logEntity.setOperator(logAnnotation.operator());

        // 获取操作用户(使用 Sa-Token 的方法)
        Object loginId = StpUtil.getLoginIdDefaultNull();
        logEntity.setUserNumber(loginId != null ? loginId.toString() : "-");

        Object businessResult; // 业务方法返回结果
        try
        {
            // 执行目标业务方法（核心业务逻辑）
            businessResult = joinPoint.proceed();
            // 方法执行成功：标记结果
            logEntity.setResult(objectMapper.writeValueAsString(businessResult));
        }
        catch (Exception e)
        {
            // 方法执行失败：记录异常信息
            logEntity.setResult("失败");
            logEntity.setException(e.getMessage()); // 异常信息（简化，可记录堆栈）
            throw e; // 重新抛出异常，不影响原有业务异常处理逻辑
        }
        finally
        {
            // 计算操作耗时（结束时间-开始时间）
            logEntity.setCostTime(System.currentTimeMillis() - startTime);

            // 8. 保存日志（调用业务模块实现的LogHandler）
            saveOperationLog(logEntity);
        }

        // 返回业务方法结果，不影响业务流程
        return businessResult;
    }

    private void saveOperationLog(Log logEntity)
    {
        try
        {
            logHandler.saveOperationLog(logEntity);
        }
        catch (Exception e)
        {
            // 日志保存失败不影响主业务，仅记录日志告警
            log.error("记录系统操作日志失败，日志信息：{}", logEntity, e);
        }
    }
}
