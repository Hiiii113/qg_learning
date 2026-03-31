package hiiii113.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import hiiii113.entity.Log;
import hiiii113.service.LogService;
import hiiii113.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用于处理日志获取的 Controller
 */
@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
@SaCheckPermission("log:list") // 需要 log:list 权限
@SaCheckLogin // 需要登录
public class LogController
{
    // 自动注入
    private final LogService logService;

    // 获取系统日志(分页查询)
    @GetMapping
    public Result<IPage<Log>> getLogs(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer size)
    {
        IPage<Log> res = logService.getLogs(page, size);
        return Result.ok("查询成功！", res);
    }
}
