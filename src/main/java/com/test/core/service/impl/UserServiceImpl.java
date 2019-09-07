package com.test.core.service.impl;

import com.test.core.bean.User;
import com.test.core.mapper.UserMapper;
import com.test.core.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *  服务实现类
 *
 * @author black
 * @date 2019-09-04
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
