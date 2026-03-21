package hiiii113.service;

import com.baomidou.mybatisplus.extension.service.IService;
import hiiii113.entity.RepairOrder;

import java.util.List;

public interface RepairOrderService extends IService<RepairOrder>
{
    // 创建报修单
    void createRepairOrder(Integer userId, String problem);

    // 查看自己的报修单
    List<RepairOrder> getUserRepairOrder(Integer userId);

    // 获取所有的报修单（管理员）
    List<RepairOrder> getAllRepairOrder();

    // 查看特定状态的报修单
    List<RepairOrder> getRepairOrderByStatus(Integer status);

    // 获取报修单详情
    RepairOrder getRepairOrderInfo(Integer id);

    // 修改报修单信息
    void modifyRepairOrder(Integer repairOrderId, String problem);

    // 处理报修单
    void updateRepairOrder(Integer repairOrderId, Integer status, Integer staffId);

    // 删除报修单
    void deleteRepairOrder(Integer repairOrderId);

    // 取消报修单
    void cancelRepairOrder(Integer repairOrderId);
}
