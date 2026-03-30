package hiiii113.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import hiiii113.entity.Log;

/**
 * 用于实现查询 log 逻辑
 */
public interface LogService extends IService<Log>
{
    // 获取日志
    IPage<Log> getLogs(int currentPage, int size);
}
