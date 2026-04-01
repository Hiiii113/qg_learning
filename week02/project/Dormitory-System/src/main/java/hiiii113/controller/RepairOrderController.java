package hiiii113.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import hiiii113.dto.CreateRepairOrderDto;
import hiiii113.dto.ModifyRepairOrderDto;
import hiiii113.dto.UpdateRepairOrderDto;
import hiiii113.dto.UserRatingDto;
import hiiii113.entity.RepairOrder;
import hiiii113.log.LogAnnotation;
import hiiii113.service.RepairOrderService;
import hiiii113.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/repair-orders")
@RequiredArgsConstructor
@SaCheckLogin
public class RepairOrderController
{
    // 单构造器自动注入
    private final RepairOrderService repairOrderService;

    // 创建报修单
    @LogAnnotation(module = "repair-orders", operator = "创建报修单")
    @PostMapping
    @SaCheckPermission("order:add")
    public Result<Integer> createRepairOrder(@RequestBody CreateRepairOrderDto dto)
    {
        // 从 Token 获取 userId
        int userId = StpUtil.getLoginIdAsInt();
        Integer repairOrderId = repairOrderService.createRepairOrder(userId, dto.getProblem());
        return Result.created("提交成功！", repairOrderId);
    }

    // 提交报修单图片
    @LogAnnotation(module = "repair-orders", operator = "提交报修单图片")
    @PostMapping("/upload")
    @SaCheckPermission("order:upload")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file,
                                      @RequestParam("id") Integer repairOrderId) throws IOException
    {
        // 访问 service 层
        String imageUrl = repairOrderService.uploadImage(file, repairOrderId);

        // 返回访问路径
        return Result.ok("提交成功！", imageUrl);
    }

    // 用户评价
    @LogAnnotation(module = "repair-orders", operator = "用户评价")
    @PostMapping("/{repairOrderId}/rating")
    @SaCheckPermission("order:rating")
    public Result<Void> rating(@PathVariable int repairOrderId, @RequestBody UserRatingDto dto)
    {
        repairOrderService.userRating(repairOrderId, dto.getRating());
        return Result.ok("评价成功！");
    }

    // 查看单个用户报修单（分页）
    @LogAnnotation(module = "repair-orders", operator = "查看单个用户报修单")
    @GetMapping("/user/repair-orders")
    @SaCheckPermission("order:listMe")
    public Result<IPage<RepairOrder>> getUserRepairOrders(@RequestParam(defaultValue = "1") Integer page,
                                                          @RequestParam(defaultValue = "10") Integer size)
    {
        // 从 Token 获取 userId
        int userId = StpUtil.getLoginIdAsInt();
        IPage<RepairOrder> res = repairOrderService.getUserRepairOrderByPage(userId, page, size);
        return Result.ok("查询成功！", res);
    }

    // 查看报修单列表
    @LogAnnotation(module = "repair-orders", operator = "查看报修单列表")
    @GetMapping
    @SaCheckPermission("order:listAll")
    public Result<IPage<RepairOrder>> getRepairOrders(@RequestParam(defaultValue = "1") Integer page, // 默认第一页
                                                      @RequestParam(defaultValue = "10") Integer size, // 默认一页10条
                                                      @RequestParam(required = false) Integer status)
    {
        IPage<RepairOrder> res;
        // 根据 status 是否存在判断是否需要根据状态获取 repair-orders
        if (status != null)
        {
            res = repairOrderService.getRepairOrderByStatus(page, size, status);
        }
        else
        {
            res = repairOrderService.getAllRepairOrder(page, size);
        }
        return Result.ok("查询成功！", res);
    }

    // 查看单个报修单
    @LogAnnotation(module = "repair-orders", operator = "查看单个报修单")
    @GetMapping("/{repairOrderId}")
    public Result<RepairOrder> getRepairOrderInfo(@PathVariable int repairOrderId)
    {
        RepairOrder res = repairOrderService.getRepairOrderInfo(repairOrderId);
        return Result.ok("查询成功！", res);
    }

    // 修改单个报修单
    @LogAnnotation(module = "repair-orders", operator = "修改单个报修单")
    @PutMapping("/{repairOrderId}")
    @SaCheckPermission("order:modify")
    public Result<Void> modifyRepairOrder(@PathVariable int repairOrderId, @RequestBody ModifyRepairOrderDto dto)
    {
        repairOrderService.modifyRepairOrder(repairOrderId, dto.getProblem());

        return Result.ok("更新成功！");
    }

    // 更新单个报修单状态
    @LogAnnotation(module = "repair-orders", operator = "更新报修单状态")
    @PatchMapping("/{repairOrderId}/status")
    @SaCheckPermission("order:update")
    public Result<Void> updateRepairOrder(@PathVariable int repairOrderId, @RequestBody UpdateRepairOrderDto dto)
    {
        repairOrderService.updateRepairOrder(repairOrderId, dto.getStatus(), dto.getStaffNumber(), dto.getProblem());

        return Result.ok("修改成功！");
    }

    // 删除报修单
    @LogAnnotation(module = "repair-orders", operator = "删除报修单")
    @DeleteMapping("/{repairOrderId}")
    @SaCheckPermission("order:delete")
    public Result<Void> deleteRepairOrder(@PathVariable int repairOrderId)
    {
        repairOrderService.deleteRepairOrder(repairOrderId);

        return Result.ok("删除成功！");
    }

    // 取消报修单
    @LogAnnotation(module = "repair-orders", operator = "取消报修单")
    @PutMapping("/{repairOrderId}/cancel")
    public Result<Void> cancelRepairOrder(@PathVariable int repairOrderId)
    {
        repairOrderService.cancelRepairOrder(repairOrderId);
        return Result.ok("取消成功！");
    }
}
