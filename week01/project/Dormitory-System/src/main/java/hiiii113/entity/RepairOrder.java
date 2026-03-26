package hiiii113.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * RepairOrder 实体类
 */
@Data
@TableName("repair_order")
public class RepairOrder
{
    // 报修单id
    @TableId(type = IdType.AUTO)
    private Integer id;

    // 报修人id
    private Integer userId;

    // 报修人学号
    private String userNumber;

    // 报修人的宿舍地址
    private String dormRoom;

    // 问题描述
    private String problem;

    // 问题描述的图片地址
    private String imageUrl;

    // 优先级
    private Integer priority;

    // 状态
    private Integer status;

    // 维修人员id
    private Integer staffNumber;

    // 用户评价
    private int rating;

    // 完成时间
    private Date completedTime;

    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
