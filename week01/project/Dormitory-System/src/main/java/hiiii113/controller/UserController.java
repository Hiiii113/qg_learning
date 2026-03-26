package hiiii113.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import hiiii113.logAop.LogAnnotation;
import hiiii113.dto.BindDormitoryDto;
import hiiii113.dto.ModifyPasswordDto;
import hiiii113.dto.UserLoginDto;
import hiiii113.dto.UserRegisterDto;
import hiiii113.entity.User;
import hiiii113.service.UserService;
import hiiii113.util.Result;
import org.springframework.web.bind.annotation.*;

/**
 * users 的接口
 */
@RestController
@RequestMapping("/users")
public class UserController
{
    // 单构造器自动注入
    private final UserService userService;
    public UserController(UserService userService)
    {
        this.userService = userService;
    }

    // 登录
    @LogAnnotation(module = "users", operator = "用户登录")
    @PostMapping("/login")
    public Result<String> login(@RequestBody UserLoginDto dto)
    {
        userService.login(dto.getUserNumber(), dto.getPassword());
        // 调用 Sa-Token 实现登录鉴权
        StpUtil.login(dto.getUserNumber());
        // 获取 Token
        String token = StpUtil.getTokenValue();
        // 返回信息和 Token
        return Result.success("登录成功！", token, 200);
    }

    // 注册
    @LogAnnotation(module = "users", operator = "用户注册")
    @PostMapping
    public Result<Void> register(@RequestBody UserRegisterDto dto)
    {
        userService.register(dto.getUserNumber(), dto.getPassword(), dto.getRole());
        return Result.success("注册成功！", 201);
    }

    // 退出登录
    @LogAnnotation(module = "users", operator = "退出登录")
    @GetMapping("/logout")
    public Result<Void> logout()
    {
        // 清除 Token
        StpUtil.logout();
        // 返回退出登录成功
        return Result.success("退出登录成功！", 200);
    }

    // 绑定宿舍
    @LogAnnotation(module = "users", operator = "绑定宿舍")
    @PostMapping("/{userId}/dormitories")
    @SaCheckLogin
    public Result<Void> bindDormitory(@PathVariable int userId, @RequestBody BindDormitoryDto dto)
    {
        userService.bindDormitory(userId, dto.getDormRoom());
        return Result.success("绑定成功！", 200);
    }

    // 修改密码
    @LogAnnotation(module = "users", operator = "修改密码")
    @PutMapping("/{userId}/password")
    @SaCheckLogin
    public Result<Void> modifyPassword(@PathVariable int userId, @RequestBody ModifyPasswordDto dto)
    {
        userService.modifyPassword(userId, dto.getPassword());
        return Result.success("修改成功！", 200);
    }

    // 获取用户信息
    @LogAnnotation(module = "users", operator = "获取用户信息")
    @GetMapping("/info")
    @SaCheckLogin
    public Result<User> getUserInfo()
    {
        String userNumber = StpUtil.getLoginIdAsString();
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUserNumber, userNumber));
        return Result.success("查询成功！", user, 200);
    }
}
