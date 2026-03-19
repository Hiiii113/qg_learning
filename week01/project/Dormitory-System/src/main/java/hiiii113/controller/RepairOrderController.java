package hiiii113.controller;

import hiiii113.dto.*;
import hiiii113.entity.RepairOrder;
import hiiii113.service.RepairOrderService;
import hiiii113.util.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/repair-orders")
public class RepairOrderController
{
    private final RepairOrderService repairOrderService;

    public RepairOrderController(RepairOrderService repairOrderService)
    {
        this.repairOrderService = repairOrderService;
    }

    // 创建报修单
    @PostMapping
    public Result<Void> createRepairOrder(@RequestBody CreateRepairOrderDto dto)
    {
        repairOrderService.createRepairOrder(dto.getUserId(), dto.getProblem());
        return Result.success("提交成功！", 201);
    }

    // 查看单个用户报修单
    @GetMapping("/user/{userId}")
    public Result<List<RepairOrder>> getUserRepairOrders(@PathVariable int userId)
    {
        List<RepairOrder> res = repairOrderService.getUserRepairOrder(userId);
        return Result.success("查询成功！", res, 200);
    }

    // 查看所有用户报修单
    @GetMapping
    public Result<List<RepairOrder>> getAllRepairOrder()
    {
        List<RepairOrder> res = repairOrderService.getAllRepairOrder();
        return Result.success("查询成功！", res, 200);
    }

    // 查看单个报修单
    @GetMapping("/{repairOrderId}")
    public Result<RepairOrder> getRepairOrderInfo(@PathVariable int repairOrderId)
    {
        RepairOrder res = repairOrderService.getRepairOrderInfo(repairOrderId);
        return Result.success("查询成功！", res, 200);
    }

    // 修改单个报修单
    @PutMapping("/{repairOrderId}")
    public Result<Void> updateRepairOrder(@PathVariable int repairOrderId, @RequestBody UpdateRepairOrderDto dto)
    {
        repairOrderService.updateRepairOrder(repairOrderId, dto.getProblem());

        return Result.success("更新成功！", 200);
    }

    // 更新单个报修单状态
    @PatchMapping("/{repairOrderId}")
    public Result<Void> modifyRepairOrder(@PathVariable int repairOrderId, @RequestBody ModifyRepairOrderDto dto)
    {
        repairOrderService.modifyRepairOrder(repairOrderId, dto.getStatus(), dto.getStaffId());

        return Result.success("修改成功！", 200);
    }

    // 删除报修单
    @DeleteMapping("/{repairOrderId}")
    public Result<Void> deleteRepairOrder(@PathVariable int repairOrderId)
    {
        repairOrderService.deleteRepairOrder(repairOrderId);

        return Result.success("删除成功！", 200);
    }

    // 取消报修单
    @PutMapping("/{repairOrderId}/cancel")
    public Result<Void> cancelRepairOrder(@PathVariable int repairOrderId)
    {
        repairOrderService.cancelRepairOrder(repairOrderId);
        return Result.success("取消成功！", 200);
    }
}
