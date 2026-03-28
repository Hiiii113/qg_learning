package hiiii113.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_user_role")
public class UserRole
{
    private Integer userId;
    private Integer roleId;
}
