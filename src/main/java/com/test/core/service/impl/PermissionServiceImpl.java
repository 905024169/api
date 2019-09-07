package com.test.core.service.impl;

import com.test.core.bean.Permission;
import com.test.core.mapper.PermissionMapper;
import com.test.core.service.IPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *  服务实现类
 *
 * @author black
 * @date 2019-09-04
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {

}
