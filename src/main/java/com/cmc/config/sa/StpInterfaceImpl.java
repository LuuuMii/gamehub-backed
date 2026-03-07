package com.cmc.config.sa;

import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cmc.entity.SysRole;
import com.cmc.entity.SysUserRole;
import com.cmc.mapper.SysRoleMapper;
import com.cmc.mapper.SysUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<SysUserRole> list = sysUserRoleMapper.selectList(new QueryWrapper<SysUserRole>().eq("user_id", loginId));
        List<Long> roleIds = list.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
        List<SysRole> sysRoles = sysRoleMapper.selectBatchIds(roleIds);
        List<String> roleKeys =  sysRoles.stream()
                .map(SysRole::getRoleKey)
                .collect(Collectors.toList());
        System.out.println(roleKeys.toString());
        return roleKeys;
    }
}
