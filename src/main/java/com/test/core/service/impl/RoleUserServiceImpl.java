package com.test.core.service.impl;

import com.test.core.bean.RoleUser;
import com.test.core.mapper.RoleUserMapper;
import com.test.core.service.IRoleUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *  服务实现类
 *
 * @author black
 * @date 2019-09-04
 */
@Service
public class RoleUserServiceImpl extends ServiceImpl<RoleUserMapper, RoleUser> implements IRoleUserService {

}
