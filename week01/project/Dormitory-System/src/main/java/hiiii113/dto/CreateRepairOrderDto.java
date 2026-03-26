package hiiii113.dto;

import lombok.Data;

/**
 * 创建报修单
 */
@Data
public class CreateRepairOrderDto
{
    private Integer userId;
    private String problem;
}
