package hiiii113.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hiiii113.entity.Log;
import hiiii113.service.LogHandler;
import hiiii113.mapper.LogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 用于保存 log 实体
 */
@Component
@RequiredArgsConstructor
public class LogHandlerImpl implements LogHandler
{
    // 注入
    private final LogMapper logMapper;
    private final ObjectMapper objectMapper;

    @Override
    public void saveOperationLog(Log log) throws JsonProcessingException
    {
        // 关键：将Object类型的params/result序列化为JSON字符串（适配数据库text类型）
        if (log.getParams() != null)
        {
            log.setParams(objectMapper.writeValueAsString(log.getParams()));
        }
        if (log.getResult() != null)
        {
            log.setResult(objectMapper.writeValueAsString(log.getResult()));
        }

        // 调用MyBatis Mapper插入数据库
        logMapper.insert(log);
    }
}
