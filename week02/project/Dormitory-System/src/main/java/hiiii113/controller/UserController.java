package hiiii113.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import hiiii113.dto.BindDormitoryDto;
import hiiii113.dto.ModifyPasswordDto;
import hiiii113.dto.UserLoginDto;
import hiiii113.dto.UserRegisterDto;
import hiiii113.entity.User;
import hiiii113.logAop.LogAnnotation;
import hiiii113.mapper.UserRoleMapper;
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
        // 调用 Sa-Token 登录功能
        userService.login(dto.getUserNumber(), dto.getPassword());
        // 根据 userNumber 获取 userId
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUserNumber, dto.getUserNumber()));
        int userId = user.getId();
        // 调用 Sa-Token 实现登录鉴权
        StpUtil.login(userId);
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
    @PostMapping("/dormitories")
    @SaCheckLogin
    public Result<Void> bindDormitory(@RequestBody BindDormitoryDto dto)
    {
        // 从 Token 获取用户 id
        int userId = StpUtil.getLoginIdAsInt();
        userService.bindDormitory(userId, dto.getDormRoom());
        return Result.success("绑定成功！", 200);
    }

    // 修改密码
    @LogAnnotation(module = "users", operator = "修改密码")
    @PutMapping("/password")
    @SaCheckLogin
    public Result<Void> modifyPassword(@RequestBody ModifyPasswordDto dto)
    {
        // 从 Token 获取用户 id
        int userId = StpUtil.getLoginIdAsInt();
        userService.modifyPassword(userId, dto.getPassword());
        return Result.success("修改成功！", 200);
    }

    // 获取用户信息
    @LogAnnotation(module = "users", operator = "获取用户信息")
    @GetMapping("/me")
    @SaCheckLogin
    public Result<User> getUserInfo()
    {
        // 从 Token 获取用户工号
        int userId = StpUtil.getLoginIdAsInt();
        User user = userService.getUserInfo(userId);
        return Result.success("查询成功！", user, 200);
    }
}
