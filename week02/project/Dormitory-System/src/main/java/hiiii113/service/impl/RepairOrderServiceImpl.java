package hiiii113.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hiiii113.entity.RepairOrder;
import hiiii113.entity.User;
import hiiii113.exception.ServiceException;
import hiiii113.mapper.RepairOrderMapper;
import hiiii113.mapper.UserMapper;
import hiiii113.service.RepairOrderService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * repairOrder 的 service 层实现类
 */
@Service
public class RepairOrderServiceImpl extends ServiceImpl<RepairOrderMapper, RepairOrder> implements RepairOrderService
{
    private final RepairOrderMapper repairOrderMapper;
    private final UserMapper userMapper;

    public RepairOrderServiceImpl(RepairOrderMapper repairOrderMapper, UserMapper userMapper)
    {
        this.repairOrderMapper = repairOrderMapper;
        this.userMapper = userMapper;
    }

    // 创建报修单
    @Override
    public Integer createRepairOrder(Integer userId, String problem)
    {
        // 判空
        if (userId == null || problem == null)
        {
            throw new ServiceException("用户id和问题描述不能为空！", ServiceException.CODE_BAD_REQUEST);
        }

        // 查找到这个用户信息并填入
        User user = userMapper.selectById(userId);
        if (user == null)
        {
            throw new ServiceException("该用户不存在！", ServiceException.CODE_BAD_REQUEST);
        }
        String userNumber = user.getUserNumber();
        String dormRoom = user.getDormRoom();

        // 新建一个报修单
        RepairOrder repairOrder = new RepairOrder();
        repairOrder.setUserId(userId);
        repairOrder.setProblem(problem);
        repairOrder.setUserNumber(userNumber);
        repairOrder.setDormRoom(dormRoom);
        boolean res = save(repairOrder);

        if (!res)
        {
            throw new ServiceException("提交失败！", ServiceException.CODE_ERROR);
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
            throw new ServiceException("报修单id和图片不能为空！", ServiceException.CODE_BAD_REQUEST);
        }

        // 看是否存在这个报修单
        RepairOrder ro = repairOrderMapper.selectById(repairOrderId);
        if (ro == null)
        {
            throw new ServiceException("报修单不存在！", ServiceException.CODE_BAD_REQUEST);
        }

        // 生成文件名，防止重复
        String originalName = file.getOriginalFilename(); // 原先的图片名
        // 获取后缀
        String ext = null;
        if (originalName != null && originalName.contains("."))
        {
            ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        }
        else
        {
            throw new ServiceException("文件名不合法！", ServiceException.CODE_BAD_REQUEST);
        }

        // 检验图片格式(利用正则表达式)
        if (!ext.matches("\\.(jpg|jpeg|png|gif|webp|bmp)$"))
        {
            throw new ServiceException("只支持 jpg/jpeg/png/gif/webp/bmp 格式的图片！",
                    ServiceException.CODE_BAD_REQUEST);
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
            throw new ServiceException("提交失败！", ServiceException.CODE_ERROR);
        }

        return imageUrl;
    }

    // 用户评价
    @Override
    public void userRating(Integer repairOrderId, Integer rating)
    {
        // 获取报修单并判断是否已完成，没完成不给评价
        RepairOrder ro = repairOrderMapper.selectById(repairOrderId);
        // 看订单是否存在
        if (ro == null)
        {
            throw new ServiceException("报修单不存在！", ServiceException.CODE_BAD_REQUEST);
        }
        // 看订单是否已完成
        if (ro.getStatus() != 3)
        {
            throw new ServiceException("报修单未完成，不能评价！", ServiceException.CODE_BAD_REQUEST);
        }

        // 看看订单的评分是否已存在
        if (ro.getRating() != null)
        {
            throw new ServiceException("报修单已评价！", ServiceException.CODE_BAD_REQUEST);
        }

        RepairOrder repairOrder = new RepairOrder();
        repairOrder.setId(repairOrderId);
        repairOrder.setRating(rating);
        boolean res = updateById(repairOrder);

        if (!res)
        {
            throw new ServiceException("报修单不存在！", ServiceException.CODE_ERROR);
        }
    }

    // 获取用户的报修单
    @Override
    public List<RepairOrder> getUserRepairOrder(Integer userId)
    {
        // 判空
        if (userId == null)
        {
            throw new ServiceException("用户id不能为空！", ServiceException.CODE_BAD_REQUEST);
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
            throw new ServiceException("用户id不能为空！", ServiceException.CODE_BAD_REQUEST);
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
            throw new ServiceException("报修单id和问题描述不能为空！", ServiceException.CODE_BAD_REQUEST);
        }

        RepairOrder ro = repairOrderMapper.selectById(repairOrderId);
        if (ro == null)
        {
            throw new ServiceException("报修单不存在！", ServiceException.CODE_BAD_REQUEST);
        }

        RepairOrder repairOrder = new RepairOrder();
        repairOrder.setId(repairOrderId);
        repairOrder.setProblem(problem);
        boolean res = updateById(repairOrder);

        if (!res)
        {
            throw new ServiceException("报修单不存在！", ServiceException.CODE_ERROR);
        }
    }

    // 更新报修单
    @Override
    public void updateRepairOrder(Integer repairOrderId, Integer status, Integer staffNumber)
    {
        // 判空
        if (repairOrderId == null || status == null || staffNumber == null)
        {
            throw new ServiceException("报修单id和状态和员工id不能为空！", ServiceException.CODE_BAD_REQUEST);
        }

        RepairOrder ro = repairOrderMapper.selectById(repairOrderId);
        if (ro == null)
        {
            throw new ServiceException("报修单不存在！", ServiceException.CODE_BAD_REQUEST);
        }

        boolean res = update(new LambdaUpdateWrapper<RepairOrder>()
                .eq(RepairOrder::getId, repairOrderId)
                .set(RepairOrder::getStatus, status)
                .set(RepairOrder::getStaffNumber, staffNumber));

        if (!res)
        {
            throw new ServiceException("报修单不存在！", ServiceException.CODE_ERROR);
        }
    }

    // 更新报修单（支持同时更新状态、员工和问题）
    @Override
    public void updateRepairOrder(Integer repairOrderId, Integer status, Integer staffNumber, String problem)
    {
        // 判空
        if (repairOrderId == null || status == null || staffNumber == null)
        {
            throw new ServiceException("报修单id和状态和员工id不能为空！", ServiceException.CODE_BAD_REQUEST);
        }

        RepairOrder ro = repairOrderMapper.selectById(repairOrderId);
        if (ro == null)
        {
            throw new ServiceException("报修单不存在！", ServiceException.CODE_BAD_REQUEST);
        }

        LambdaUpdateWrapper<RepairOrder> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(RepairOrder::getId, repairOrderId)
                .set(RepairOrder::getStatus, status)
                .set(RepairOrder::getStaffNumber, staffNumber)
                .set(RepairOrder::getCompletedTime, LocalDateTime.now());

        if (problem != null && !problem.isEmpty())
        {
            wrapper.set(RepairOrder::getProblem, problem);
        }

        boolean res = update(wrapper);

        if (!res)
        {
            throw new ServiceException("报修单不存在！", ServiceException.CODE_ERROR);
        }
    }

    // 删除报修单
    @Override
    public void deleteRepairOrder(Integer repairOrderId)
    {
        // 判空
        if (repairOrderId == null)
        {
            throw new ServiceException("报修单id不能为空！", ServiceException.CODE_BAD_REQUEST);
        }

        RepairOrder ro = repairOrderMapper.selectById(repairOrderId);
        if (ro == null)
        {
            throw new ServiceException("报修单不存在！", ServiceException.CODE_BAD_REQUEST);
        }

        boolean res = removeById(repairOrderId);

        if (!res)
        {
            throw new ServiceException("报修单不存在！", ServiceException.CODE_ERROR);
        }
    }

    // 取消报修单
    @Override
    public void cancelRepairOrder(Integer repairOrderId)
    {
        // 判空
        if (repairOrderId == null)
        {
            throw new ServiceException("报修单id不能为空！", ServiceException.CODE_BAD_REQUEST);
        }

        RepairOrder ro = repairOrderMapper.selectById(repairOrderId);
        if (ro == null)
        {
            throw new ServiceException("报修单不存在！", ServiceException.CODE_BAD_REQUEST);
        }

        boolean res = update(new LambdaUpdateWrapper<RepairOrder>()
                .eq(RepairOrder::getId, repairOrderId)
                .set(RepairOrder::getStatus, 4));

        if (!res)
        {
            throw new ServiceException("报修单不存在！", ServiceException.CODE_ERROR);
        }
    }
}
