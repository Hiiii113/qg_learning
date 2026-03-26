package hiiii113.dto;

import lombok.Data;

/**
 * 更新报修单
 */
@Data
public class UpdateRepairOrderDto
{
    private Integer status;
    private Integer staffNumber;
    private String problem;
}
