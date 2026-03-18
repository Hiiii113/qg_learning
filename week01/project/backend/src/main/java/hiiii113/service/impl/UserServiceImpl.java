package hiiii113.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hiiii113.entity.User;
import hiiii113.exception.ServiceException;
import hiiii113.mapper.UserMapper;
import hiiii113.service.UserService;
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
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getUserNumber, userNumber).eq(User::getPassword, password));
        if (user == null)
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
        newUser.setPassword(password);
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

        //调用update函数更新
        User user = new User();
        user.setId(userId);
        user.setDormRoom(dormRoom);
        updateById(user);
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
        user.setPassword(password);
        updateById(user);
    }
}
