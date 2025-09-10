package com.cmc.service.impl;

import com.cmc.entity.Users;
import com.cmc.mapper.UsersMapper;
import com.cmc.service.UsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author C
 * @since 2025-09-10
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

}
