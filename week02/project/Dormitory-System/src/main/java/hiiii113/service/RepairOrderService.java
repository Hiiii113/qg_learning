package hiiii113.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import hiiii113.entity.RepairOrder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * repairOrder 的 service 层，继承使用 IService
 */
public interface RepairOrderService extends IService<RepairOrder>
{
    // 创建报修单
    Integer createRepairOrder(Integer userId, String problem);

    // 创建报修单和 imageUrl 的关联
    String uploadImage(MultipartFile file, Integer repairOrderId) throws IOException;

    // 用户评分
    void userRating(Integer repairOrderId, Integer rating);

    // 查看自己的报修单
    List<RepairOrder> getUserRepairOrder(Integer userId);

    // 分页查看自己的报修单
    IPage<RepairOrder> getUserRepairOrderByPage(Integer userId, Integer currentPage, Integer size);

    // 获取所有的报修单（管理员）
    IPage<RepairOrder> getAllRepairOrder(Integer currentPage, Integer size);

    // 查看特定状态的报修单
    IPage<RepairOrder> getRepairOrderByStatus(Integer currentPage, Integer size, Integer status);

    // 获取报修单详情
    RepairOrder getRepairOrderInfo(Integer id);

    // 修改报修单信息
    void modifyRepairOrder(Integer repairOrderId, String problem);

    // 更新报修单
    void updateRepairOrder(Integer repairOrderId, Integer status, Integer staffId);

    // 更新报修单（支持同时更新状态、员工和问题）
    void updateRepairOrder(Integer repairOrderId, Integer status, Integer staffNumber, String problem);

    // 删除报修单
    void deleteRepairOrder(Integer repairOrderId);

    // 取消报修单
    void cancelRepairOrder(Integer repairOrderId);
}
