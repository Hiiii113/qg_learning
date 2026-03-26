package hiiii113.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import hiiii113.dto.UserRatingDto;
import hiiii113.logAop.LogAnnotation;
import hiiii113.dto.CreateRepairOrderDto;
import hiiii113.dto.UpdateRepairOrderDto;
import hiiii113.dto.ModifyRepairOrderDto;
import hiiii113.entity.RepairOrder;
import hiiii113.service.RepairOrderService;
import hiiii113.util.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/repair-orders")
public class RepairOrderController
{
    // 单构造器自动注入
    private final RepairOrderService repairOrderService;
    public RepairOrderController(RepairOrderService repairOrderService)
    {
        this.repairOrderService = repairOrderService;
    }

    // 创建报修单
    @LogAnnotation(module = "repair-orders", operator = "创建报修单")
    @PostMapping
    @SaCheckLogin
    public Result<Integer> createRepairOrder(@RequestBody CreateRepairOrderDto dto)
    {
        Integer repairOrderId = repairOrderService.createRepairOrder(dto.getUserId(), dto.getProblem());
        return Result.success("提交成功！", repairOrderId, 201);
    }

    // 提交报修单图片
    @LogAnnotation(module = "repair-orders", operator = "提交报修单图片")
    @PostMapping("/upload")
    @SaCheckLogin
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file,
                                 @RequestParam("id") Integer repairOrderId) throws IOException
    {
        // 访问 service 层
        String imageUrl = repairOrderService.uploadImage(file, repairOrderId);

        // 返回访问路径
        return Result.success("提交成功！", imageUrl, 200);
    }

    // 用户评价
    @LogAnnotation(module = "repair-orders", operator = "用户评价")
    @PostMapping("/rating/{rating}")
    @SaCheckLogin
    public Result<Void> rating(@PathVariable int rating, @RequestBody UserRatingDto dto)
    {
        repairOrderService.userRating(dto.getRepairOrderId(), rating);
        return Result.success("评价成功！", 200);
    }

    // 查看单个用户报修单
    @LogAnnotation(module = "repair-orders", operator = "查看单个用户报修单")
    @GetMapping("/user/{userId}")
    @SaCheckLogin
    public Result<List<RepairOrder>> getUserRepairOrders(@PathVariable int userId)
    {
        List<RepairOrder> res = repairOrderService.getUserRepairOrder(userId);
        return Result.success("查询成功！", res, 200);
    }

    // 查看所有用户报修单
    @LogAnnotation(module = "repair-orders", operator = "查看所有用户报修单")
    @GetMapping
    @SaCheckLogin
    public Result<List<RepairOrder>> getAllRepairOrder()
    {
        List<RepairOrder> res = repairOrderService.getAllRepairOrder();
        return Result.success("查询成功！", res, 200);
    }

    // 查看特定状态的报修单
    @LogAnnotation(module = "repair-orders", operator = "查看特定状态的报修单")
    @GetMapping("/status/{status}")
    @SaCheckLogin
    public Result<List<RepairOrder>> getRepairOrderByStatus(@PathVariable int status)
    {
        List<RepairOrder> res = repairOrderService.getRepairOrderByStatus(status);
        return Result.success("查询成功！", res, 200);
    }

    // 查看单个报修单
    @LogAnnotation(module = "repair-orders", operator = "查看单个报修单")
    @GetMapping("/{repairOrderId}")
    @SaCheckLogin
    public Result<RepairOrder> getRepairOrderInfo(@PathVariable int repairOrderId)
    {
        RepairOrder res = repairOrderService.getRepairOrderInfo(repairOrderId);
        return Result.success("查询成功！", res, 200);
    }

    // 修改单个报修单
    @LogAnnotation(module = "repair-orders", operator = "修改单个报修单")
    @PutMapping("/{repairOrderId}")
    @SaCheckLogin
    public Result<Void> modifyRepairOrder(@PathVariable int repairOrderId, @RequestBody ModifyRepairOrderDto dto)
    {
        repairOrderService.modifyRepairOrder(repairOrderId, dto.getProblem());

        return Result.success("更新成功！", 200);
    }

    // 更新单个报修单状态
    @LogAnnotation(module = "repair-orders", operator = "更新报修单状态")
    @PatchMapping("/{repairOrderId}")
    @SaCheckLogin
    public Result<Void> updateRepairOrder(@PathVariable int repairOrderId, @RequestBody UpdateRepairOrderDto dto)
    {
        repairOrderService.updateRepairOrder(repairOrderId, dto.getStatus(), dto.getStaffNumber(), dto.getProblem());

        return Result.success("修改成功！", 200);
    }

    // 删除报修单
    @LogAnnotation(module = "repair-orders", operator = "删除报修单")
    @DeleteMapping("/{repairOrderId}")
    @SaCheckLogin
    public Result<Void> deleteRepairOrder(@PathVariable int repairOrderId)
    {
        repairOrderService.deleteRepairOrder(repairOrderId);

        return Result.success("删除成功！", 200);
    }

    // 取消报修单
    @LogAnnotation(module = "repair-orders", operator = "取消报修单")
    @PutMapping("/{repairOrderId}/cancel")
    @SaCheckLogin
    public Result<Void> cancelRepairOrder(@PathVariable int repairOrderId)
    {
        repairOrderService.cancelRepairOrder(repairOrderId);
        return Result.success("取消成功！", 200);
    }
}
