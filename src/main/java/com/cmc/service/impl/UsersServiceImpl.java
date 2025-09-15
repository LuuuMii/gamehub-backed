package com.cmc.service.impl;

import com.cmc.common.R;
import com.cmc.entity.Users;
import com.cmc.mapper.UsersMapper;
import com.cmc.service.UsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmc.utils.JwtUtil;
import com.cmc.utils.PasswordUtil;
import com.cmc.utils.RedisUtil;
import com.cmc.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

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

    @Value("${token.prefix}")
    private String tokenPrefix;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UsersMapper usersMapper;


    /**
     *
     * @param user
     * @return
     */
    @Override
    public R loginByAccount(Users user) {
        Users userInfo = usersMapper.getUserInfoById(user.getId());
        if (userInfo != null && PasswordUtil.checkPassword(userInfo.getPassword(), user.getPassword())) {
            //生成token
            String token = JwtUtil.generateToken(user.getUsername());
            //将token 存储在redis
            if(!ObjectUtils.isEmpty(token)){
                redisUtil.set(tokenPrefix + ":" + user.getUsername(), token, 30 , TimeUnit.MINUTES);
                return R.ok("登录成功",new LoginVO(token,user.getId()));
            }
        }
        return R.error("登录失败");
    }
}
