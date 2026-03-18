package hiiii113.controller;

import hiiii113.dto.BindDormitoryDto;
import hiiii113.dto.ModifyPasswordDto;
import hiiii113.dto.UserLoginDto;
import hiiii113.dto.UserRegisterDto;
import hiiii113.entity.User;
import hiiii113.service.UserService;
import hiiii113.util.Result;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/login")
    public Result<User> login(@RequestBody UserLoginDto dto)
    {
        User user = userService.login(dto.getUserNumber(), dto.getPassword());
        return Result.success("登录成功！", user, 200);
    }

    // 注册
    @PostMapping
    public Result<Void> register(@RequestBody UserRegisterDto dto)
    {
        userService.register(dto.getUserNumber(), dto.getPassword(), dto.getRole());
        return Result.success("注册成功！", 201);
    }

    // 绑定宿舍
    @PostMapping("/{userId}/dormitories")
    public Result<Void> bindDormitory(@PathVariable int userId, @RequestBody BindDormitoryDto dto)
    {
        userService.bindDormitory(userId, dto.getDormRoom());

        return Result.success("绑定成功！", 200);
    }

    // 修改密码
    @PutMapping("/{userId}/password")
    public Result<Void> modifyPassword(@PathVariable int userId, @RequestBody ModifyPasswordDto dto)
    {
        userService.modifyPassword(userId, dto.getPassword());

        return Result.success("修改成功！", 200);
    }
}
