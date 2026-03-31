package hiiii113.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface
{
    private final JdbcTemplate jdbcTemplate; // 直接用 JdbcTemplate 查数据库最快

    // 返回一个账号所拥有的权限码集合
    @Override
    public List<String> getPermissionList(Object loginId, String loginType)
    {
        // 将 loginId 转为 int
        int userId = Integer.parseInt(loginId.toString());

        // 编写 SQL，查询该用户所有的 permission_code
        String sql = "SELECT p.permission_code FROM sys_permission p " +
                "JOIN sys_role_permission rp ON p.id = rp.permission_id " +
                "JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
                "WHERE ur.user_id = ?";

        // 查询并返回
        return jdbcTemplate.queryForList(sql, String.class, userId);
    }

    // 返回一个账号所拥有的角色标识集合
    @Override
    public List<String> getRoleList(Object loginId, String loginType)
    {
        // 编写 SQL，查询该用户所有的 role_id
        String sql = "SELECT r.role_code FROM sys_role r " +
                "JOIN sys_user_role ur ON r.id = ur.role_id " +
                "WHERE ur.user_id = ?";
        // 返回
        return jdbcTemplate.queryForList(sql, String.class, loginId);
    }
}
