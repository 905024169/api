package com.test.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;


import com.test.core.bean.*;
import com.test.core.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @program: api
 * @description:
 * @author: duanwei
 * @create: 2019-09-03 16:18
 **/
@Component
public class UserDetailsServiceImpl implements UserDetailsService {


    @Autowired
    IUserService iUserService;

    @Autowired
    IRoleUserService iRoleUserService;

    @Autowired
    IRoleService iRoleService;

    @Autowired
    IPermissionRoleService iPermissionRoleService;

    @Autowired
    IPermissionService iPermissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        //查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User userInfo = iUserService.getOne(queryWrapper);

        if (userInfo != null) {
            //查询角色关联
            QueryWrapper<RoleUser> roleUserQueryWrapper = new QueryWrapper<>();
            roleUserQueryWrapper.eq("sys_user_id", userInfo.getId());
            List<RoleUser> roleUser = iRoleUserService.list(roleUserQueryWrapper);

            Set<Long> roles = new HashSet<>();
            Set<Long> permissions = new HashSet<>();
            Set<Long> permissionRoles = new HashSet<>();
            for (RoleUser user : roleUser) {
                roles.add(user.getSysRoleId());
            }
            //查询角色
            QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
            roleQueryWrapper.in("id", roles);
            List<Role> role = iRoleService.list(roleQueryWrapper);
            //添加角色
            for (Role r : role) {
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(r.getName());
                grantedAuthorities.add(grantedAuthority);
                permissions.add(r.getId());
            }
            //查询角色权限关联
            QueryWrapper<PermissionRole> permissionsRoleWapper = new QueryWrapper<>();
            permissionsRoleWapper.in("role_id", permissions);
            List<PermissionRole> permissionsList = iPermissionRoleService.list(permissionsRoleWapper);
            for (PermissionRole permission : permissionsList) {
                permissionRoles.add(permission.getPermissionId());
            }
            //查询角色
            QueryWrapper<Permission> permissionWapper = new QueryWrapper<>();
            permissionWapper.in("id", permissionRoles);
            List<Permission> permissionList = iPermissionService.list(permissionWapper);

            //添加权限
            for (Permission permission : permissionList) {
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(permission.getName());
                grantedAuthorities.add(grantedAuthority);
            }
            return new org.springframework.security.core.userdetails.User(userInfo.getUsername(), userInfo.getPassword(), grantedAuthorities);
        } else {
            throw new UsernameNotFoundException("user: " + username + " do not exist!");
        }
    }
}
