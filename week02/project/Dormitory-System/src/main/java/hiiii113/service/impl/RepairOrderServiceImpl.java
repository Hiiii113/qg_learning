package hiiii113.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hiiii113.entity.RepairOrder;
import hiiii113.exception.ServiceException;
import hiiii113.mapper.RepairOrderMapper;
import hiiii113.service.RepairOrderService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * repairOrder 的 service 层实现类
 */
@Service
public class RepairOrderServiceImpl extends ServiceImpl<RepairOrderMapper, RepairOrder> implements RepairOrderService
{
    private final RepairOrderMapper repairOrderMapper;

    public RepairOrderServiceImpl(RepairOrderMapper repairOrderMapper)
    {
        this.repairOrderMapper = repairOrderMapper;
    }

    // 创建报修单
    @Override
    public Integer createRepairOrder(Integer userId, String problem)
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
        boolean res = save(repairOrder);

        if (!res)
        {
            throw new ServiceException("提交失败！", 500);
        }

        // 获取id并返回(MP的save会自动把自增的id回填到实体类)
        return repairOrder.getId();
    }

    // 更新报修单对应的 image
    @Override
    public String uploadImage(MultipartFile file, Integer repairOrderId) throws IOException
    {
        // 判空
        if (repairOrderId == null || file.isEmpty())
        {
            throw new ServiceException("报修单id和图片不能为空！", 400);
        }

        // 生成文件名，防止重复
        String originalName = file.getOriginalFilename(); // 原先的图片名
        // 获取后缀
        String ext = null;
        if (originalName != null)
        {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        // 生成最终的文件名
        String fileName = System.currentTimeMillis() + ext;

        // 保存到本地目录
        String path = System.getProperty("user.dir") + "/upload/"; // 项目根目录下的 /upload
        file.transferTo(new File(path + fileName));
        String imageUrl = "upload/" + fileName;

        // 更新数据库
        RepairOrder repairOrder = new RepairOrder();
        repairOrder.setId(repairOrderId);
        repairOrder.setImageUrl(imageUrl);
        boolean res = updateById(repairOrder);

        if (!res)
        {
            throw new ServiceException("提交失败！", 500);
        }

        return imageUrl;
    }

    // 用户评价
    @Override
    public void userRating(Integer repairOrderId, Integer rating)
    {
        RepairOrder repairOrder = new RepairOrder();
        repairOrder.setId(repairOrderId);
        repairOrder.setRating(rating);
        boolean res = updateById(repairOrder);

        if (!res)
        {
            throw new ServiceException("报修单不存在！", 500);
        }
    }

    // 获取用户的报修单
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

    // 分页获取用户的报修单
    @Override
    public IPage<RepairOrder> getUserRepairOrderByPage(Integer userId, Integer currentPage, Integer size)
    {
        if (userId == null)
        {
            throw new ServiceException("用户id不能为空！", 400);
        }

        Page<RepairOrder> page = new Page<>(currentPage, size);
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairOrder::getUserId, userId)
                .orderByDesc(RepairOrder::getCreateTime);

        return repairOrderMapper.selectPage(page, wrapper);
    }

    // 获取所有用户的报修单
    @Override
    public IPage<RepairOrder> getAllRepairOrder(Integer currentPage, Integer size)
    {
        // 构造分页对象
        Page<RepairOrder> page = new Page<>(currentPage, size);

        // 构造查询条件
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(RepairOrder::getCreateTime); // 排序

        // 调用分页查询方法并返回
        return repairOrderMapper.selectPage(page, wrapper);
    }

    // 获取对应状态的报修单
    @Override
    public IPage<RepairOrder> getRepairOrderByStatus(Integer currentPage, Integer size, Integer status)
    {
        // 构造分页对象
        Page<RepairOrder> page = new Page<>(currentPage, size);

        // 构造查询条件
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RepairOrder::getStatus, status) // 查询对应状态的
                .orderByDesc(RepairOrder::getCreateTime); // 排序

        // 调用分页查询方法并返回
        return repairOrderMapper.selectPage(page, wrapper);
    }

    // 获取单个报修单
    @Override
    public RepairOrder getRepairOrderInfo(Integer id)
    {
        return getById(id);
    }

    // 修改报修单
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
        boolean res = updateById(repairOrder);

        if (!res)
        {
            throw new ServiceException("报修单不存在！", 500);
        }
    }

    // 更新报修单
    @Override
    public void updateRepairOrder(Integer repairOrderId, Integer status, Integer staffNumber)
    {
        // 判空
        if (repairOrderId == null || status == null || staffNumber == null)
        {
            throw new ServiceException("报修单id和状态和员工id不能为空！", 400);
        }

        boolean res = update(new LambdaUpdateWrapper<RepairOrder>()
                .eq(RepairOrder::getId, repairOrderId)
                .set(RepairOrder::getStatus, status)
                .set(RepairOrder::getStaffNumber, staffNumber));

        if (!res)
        {
            throw new ServiceException("报修单不存在！", 500);
        }
    }

    // 更新报修单（支持同时更新状态、员工和问题）
    @Override
    public void updateRepairOrder(Integer repairOrderId, Integer status, Integer staffNumber, String problem)
    {
        // 判空
        if (repairOrderId == null || status == null || staffNumber == null)
        {
            throw new ServiceException("报修单id和状态和员工id不能为空！", 400);
        }

        LambdaUpdateWrapper<RepairOrder> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(RepairOrder::getId, repairOrderId)
                .set(RepairOrder::getStatus, status)
                .set(RepairOrder::getStaffNumber, staffNumber);

        if (problem != null && !problem.isEmpty())
        {
            wrapper.set(RepairOrder::getProblem, problem);
        }

        boolean res = update(wrapper);

        if (!res)
        {
            throw new ServiceException("报修单不存在！", 500);
        }
    }

    // 删除报修单
    @Override
    public void deleteRepairOrder(Integer repairOrderId)
    {
        // 判空
        if (repairOrderId == null)
        {
            throw new ServiceException("报修单id不能为空！", 400);
        }

        boolean res = removeById(repairOrderId);

        if (!res)
        {
            throw new ServiceException("报修单不存在！", 500);
        }
    }

    // 取消报修单
    @Override
    public void cancelRepairOrder(Integer repairOrderId)
    {
        // 判空
        if (repairOrderId == null)
        {
            throw new ServiceException("报修单id不能为空！", 400);
        }

        boolean res = update(new LambdaUpdateWrapper<RepairOrder>()
                .eq(RepairOrder::getId, repairOrderId)
                .set(RepairOrder::getStatus, 4));

        if (!res)
        {
            throw new ServiceException("报修单不存在！", 500);
        }
    }
}
