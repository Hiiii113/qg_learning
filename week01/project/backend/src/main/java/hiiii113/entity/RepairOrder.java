package hiiii113.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("repair_order")
public class RepairOrder
{
    // 报修单id
    @TableId(type = IdType.AUTO)
    private int id;

    // 报修人id
    private int userId;

    // 报修人学号
    private String userNumber;

    // 报修人的宿舍地址
    private String dormRoom;

    // 问题描述
    private String problem;

    // 优先级
    private int priority;

    // 状态
    private int status;

    // 维修人员id
    private int staffId;

    // 维修人员姓名
    private String staffName;

    // 完成时间
    private Date completedTime;

    // 创建时间
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Date createTime;

    // 更新时间
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Date updateTime;
}
