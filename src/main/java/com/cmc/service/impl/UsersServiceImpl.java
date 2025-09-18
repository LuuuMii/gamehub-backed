package com.cmc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cmc.common.R;
import com.cmc.entity.Users;
import com.cmc.mapper.UsersMapper;
import com.cmc.service.UsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmc.utils.JwtUtil;
import com.cmc.utils.PasswordUtil;
import com.cmc.utils.RedisUtil;
import com.cmc.vo.LoginVO;
import com.cmc.vo.UsersVO;
import org.springframework.beans.BeanUtils;
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

    @Autowired
    private JwtUtil jwtUtil;


    /**
     *
     * @param user
     * @return
     */
    @Override
    public R loginByUsername(Users user) {
        QueryWrapper<Users> usersQueryWrapper = new QueryWrapper<>();
        usersQueryWrapper.eq("username", user.getUsername());
        Users userInfo = usersMapper.selectOne(usersQueryWrapper);
        if (userInfo != null && PasswordUtil.checkPassword(user.getPassword(), userInfo.getPassword())) {
            //生成token
            String token = jwtUtil.generateToken(user.getUsername());
            //将token 存储在redis
            if(!ObjectUtils.isEmpty(token)){
                redisUtil.set(tokenPrefix + ":" + user.getUsername(), token, 30 , TimeUnit.MINUTES);
                return R.ok("登录成功",new LoginVO(token,userInfo.getId()));
            }
        }else if (userInfo == null){
            return R.error("账号错误");
        }else if (!PasswordUtil.checkPassword(user.getPassword(), userInfo.getPassword())){
            return R.error("密码错误");
        }
        return R.error("登录失败");
    }

    @Override
    public R register(Users users) {
        users.setPassword(PasswordUtil.hashPassword(users.getPassword()));
        int i = usersMapper.insert(users);
        if (i > 0) {
            return R.ok("添加成功");
        }
        return R.error("注册失败");
    }

    @Override
    public R getUserInfoById(Integer id) {
        QueryWrapper<Users> usersQueryWrapper = new QueryWrapper<>();
        usersQueryWrapper.eq("id", id);
        Users users = usersMapper.selectOne(usersQueryWrapper);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(users, usersVO);
        return R.ok(usersVO);
    }

    @Override
    public R getUserInfoByToken(String token) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", jwtUtil.getUsername(token));
        Users users = usersMapper.selectOne(queryWrapper);
        //判断redis中有没有
        if (!ObjectUtils.isEmpty(token) && token.equals(redisUtil.get(tokenPrefix + ":" + jwtUtil.getUsername(token)))){
            if (!ObjectUtils.isEmpty(users)) {
                UsersVO usersVO = new UsersVO();
                BeanUtils.copyProperties(users, usersVO);
                return R.ok(usersVO);
            }
        }

        return R.error("查询失败");
    }
}
