package hiiii113.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hiiii113.entity.Log;
import hiiii113.mapper.LogMapper;
import hiiii113.service.LogService;
import org.springframework.stereotype.Service;

/**
 * 用于实现查询 log 逻辑
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements LogService
{
    @Override
    public IPage<Log> getLogs(int currentPage, int size)
    {
        // 构造分页对象
        Page<Log> page = new Page<>(currentPage, size);

        // 构造查询条件
        LambdaQueryWrapper<Log> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Log::getCreateTime); // 排序

        // 调用分页查询方法并返回
        return page(page, wrapper);
    }
}
