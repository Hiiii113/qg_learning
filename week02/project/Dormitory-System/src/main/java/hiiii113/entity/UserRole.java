package hiiii113.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_user_role")
public class UserRole
{
    // 用户 id
    private Integer userId;

    // 角色 id
    private Integer roleId;
}
