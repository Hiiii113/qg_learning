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

import java.util.regex.Pattern;

/**
 * user 的 service 层实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService
{

    // 登录
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
            throw new ServiceException("该账号未注册！", 401);
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

    // 注册
    @Override
    public void register(String userNumber, String password, Integer role)
    {
        // 判空
        if (userNumber == null || password == null || role == null)
        {
            throw new ServiceException("学号/工号和密码和身份不能为空！", 400);
        }

        // 先查看一下有没有重复用户名
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUserNumber, userNumber));
        if (user != null)
        {
            throw new ServiceException("学号/工号已存在！", 409);
        }

        // 是否超过长度
        if (userNumber.length() > 20)
        {
            throw new ServiceException("学号/工号不能超过20位！", 400);
        }

        // 正则校验
        if (role == 1) // 学生: 前缀3125或3225
        {
            if (!Pattern.matches("^(3125|3225).*", userNumber))
            {
                throw new ServiceException("学号必须以3125或者3225开头！", 400);
            }
        }
        else if (role == 2) // 管理员：前缀0025
        {
            if (!Pattern.matches("^0025.*", userNumber))
            {
                throw new ServiceException("工号必须以0025开头！", 400);
            }
        }
        else
        {
            throw new ServiceException("身份选择有误！", 400);
        }

        // 没有的话就直接调用save方法
        User newUser = new User();
        newUser.setUserNumber(userNumber);
        newUser.setPassword(PasswordUtil.encode(password));
        newUser.setRole(role);

        boolean res = save(newUser);

        if (!res)
        {
            throw new ServiceException("注册失败！", 500);
        }
    }

    // 绑定宿舍
    @Override
    public void bindDormitory(Integer userId, String dormRoom)
    {
        // 判空
        if (userId == null || dormRoom == null)
        {
            throw new ServiceException("用户id和宿舍地址不能为空！", 400);
        }

        boolean res = update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getDormRoom, dormRoom));

        if (!res)
        {
            throw new ServiceException("账户异常！", 500);
        }
    }

    // 修改密码
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

        boolean res = updateById(user);

        if (!res)
        {
            throw new ServiceException("账户异常！", 500);
        }
    }
}
