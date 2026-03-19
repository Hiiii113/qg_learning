package hiiii113.service;

import com.baomidou.mybatisplus.extension.service.IService;
import hiiii113.entity.User;

public interface UserService extends IService<User>
{
    // 登录
    User login(String userNumber, String password);

    // 注册
    void register(String userNumber, String password, Integer role);

    // 绑定宿舍
    void bindDormitory(Integer userId, String dormRoom);

    // 修改密码
    void modifyPassword(Integer userId, String password);
}
