package hiiii113.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("user")
public class User
{
    // 用户id 主键
    @TableId(type = IdType.AUTO)
    private int id;

    // 用户编号（学号、工号）
    @TableField("user_number")
    private String userNumber;

    // 用户名
    private String username;

    // 密码（加密后）
    private String password;

    // 身份: 1-学生 2-维修人员
    private int role;

    // 宿舍地址
    private String dormRoom;

    // 创建时间
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Date createTime;

    // 更新时间
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Date updateTime;
}
