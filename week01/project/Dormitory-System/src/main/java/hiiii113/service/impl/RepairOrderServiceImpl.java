package hiiii113.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hiiii113.entity.RepairOrder;
import hiiii113.exception.ServiceException;
import hiiii113.mapper.RepairOrderMapper;
import hiiii113.service.RepairOrderService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepairOrderServiceImpl extends ServiceImpl<RepairOrderMapper, RepairOrder> implements RepairOrderService
{
    @Override
    public void createRepairOrder(Integer userId, String problem)
    {
        // 判空
        if (userId == null || problem == null)
        {
            throw new ServiceException("用户id和问题描述不能为空！", 400);
        }

        // 新建一个报修单
        RepairOrder repairOrder = new RepairOrder();
        repairOrder.setUserId(userId);
        repairOrder.setProblem(problem);
        save(repairOrder);
    }

    @Override
    public List<RepairOrder> getUserRepairOrder(Integer userId)
    {
        // 判空
        if (userId == null)
        {
            throw new ServiceException("用户id不能为空！", 400);
        }

        return lambdaQuery()
                .eq(RepairOrder::getUserId, userId)
                .orderByDesc(RepairOrder::getCreateTime)
                .list();
    }

    @Override
    public List<RepairOrder> getAllRepairOrder()
    {
        return  lambdaQuery()
                .orderByDesc(RepairOrder::getCreateTime)
                .list();
    }

    @Override
    public List<RepairOrder> getRepairOrderByStatus(Integer status)
    {
        return lambdaQuery()
                .eq(RepairOrder::getStatus, status)
                .orderByDesc(RepairOrder::getCreateTime)
                .list();
    }

    @Override
    public RepairOrder getRepairOrderInfo(Integer id)
    {
        return getById(id);
    }

    @Override
    public void modifyRepairOrder(Integer repairOrderId, String problem)
    {
        // 判空
        if (repairOrderId == null || problem == null)
        {
            throw new ServiceException("报修单id和问题描述不能为空！", 400);
        }

        RepairOrder repairOrder = new RepairOrder();
        repairOrder.setId(repairOrderId);
        repairOrder.setProblem(problem);
        updateById(repairOrder);
    }

    @Override
    public void updateRepairOrder(Integer repairOrderId, Integer status, Integer staffId)
    {
        // 判空
        if (repairOrderId == null || status == null || staffId == null)
        {
            throw new ServiceException("报修单id和问题描述和员工id不能为空！", 400);
        }

        update(new LambdaUpdateWrapper<RepairOrder>()
                .eq(RepairOrder::getId, repairOrderId)
                .set(RepairOrder::getStatus, status)
                .set(RepairOrder::getStaffId, staffId));
    }

    @Override
    public void deleteRepairOrder(Integer repairOrderId)
    {
        // 判空
        if (repairOrderId == null)
        {
            throw new ServiceException("报修单id不能为空！", 400);
        }

        removeById(repairOrderId);
    }

    @Override
    public void cancelRepairOrder(Integer repairOrderId)
    {
        // 判空
        if (repairOrderId == null)
        {
            throw new ServiceException("报修单id不能为空！", 400);
        }

        update(new LambdaUpdateWrapper<RepairOrder>()
                .eq(RepairOrder::getId, repairOrderId)
                .set(RepairOrder::getStatus, 4));
    }
}
