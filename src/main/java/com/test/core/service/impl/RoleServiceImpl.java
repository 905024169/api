package com.test.core.service.impl;

import com.test.core.bean.Role;
import com.test.core.mapper.RoleMapper;
import com.test.core.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *  服务实现类
 *
 * @author black
 * @date 2019-09-04
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

}
