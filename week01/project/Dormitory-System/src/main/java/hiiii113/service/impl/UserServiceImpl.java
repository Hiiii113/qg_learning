package hiiii113.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hiiii113.entity.User;
import hiiii113.exception.ServiceException;
import hiiii113.mapper.UserMapper;
import hiiii113.service.UserService;
import hiiii113.util.PasswordUtil;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService
{

    @Override
    public User login(String userNumber, String password)
    {
        // 判空
        if (userNumber == null || password == null)
        {
            throw new ServiceException("用户码和密码不能为空！", 400);
        }

        // 查找是否有对应的账户
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUserNumber, userNumber));
        if (user == null)
        {
            throw new ServiceException("用户名或密码错误！", 401);
        }

        // 用 BCrypt 比对密码
        if (!PasswordUtil.matches(password, user.getPassword()))
        {
            throw new ServiceException("用户名或密码错误！", 401);
        }
        else
        {
            // 给前端传相关的数据
            return user;
        }
    }

    @Override
    public void register(String userNumber, String password, Integer role)
    {
        // 判空
        if (userNumber == null || password == null || role == null)
        {
            throw new ServiceException("用户码和密码和身份不能为空！", 400);
        }

        // 先查看一下有没有重复用户名
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUserNumber, userNumber));
        if (user != null)
        {
            throw new ServiceException("用户名已存在！", 409);
        }

        // 没有的话就直接调用save方法
        User newUser = new User();
        newUser.setUserNumber(userNumber);
        newUser.setPassword(PasswordUtil.encode(password));
        newUser.setRole(role);
        save(newUser);
    }

    @Override
    public void bindDormitory(Integer userId, String dormRoom)
    {
        // 判空
        if (userId == null || dormRoom == null)
        {
            throw new ServiceException("用户id和宿舍地址不能为空！", 400);
        }

        update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getDormRoom, dormRoom));
    }

    @Override
    public void modifyPassword(Integer userId, String password)
    {
        // 判空
        if (userId == null || password == null)
        {
            throw new ServiceException("用户id和密码不能为空！", 400);
        }

        // 更新
        User user = new User();
        user.setId(userId);
        user.setPassword(PasswordUtil.encode(password));
        updateById(user);
    }
}
