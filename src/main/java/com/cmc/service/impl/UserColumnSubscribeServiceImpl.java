package com.cmc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cmc.common.R;
import com.cmc.entity.UserColumnSubscribe;
import com.cmc.mapper.UserColumnSubscribeMapper;
import com.cmc.service.UserColumnSubscribeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 用户订阅专栏关联表 服务实现类
 * </p>
 *
 * @author C
 * @since 2025-10-16
 */
@Service
@Transactional
public class UserColumnSubscribeServiceImpl extends ServiceImpl<UserColumnSubscribeMapper, UserColumnSubscribe> implements UserColumnSubscribeService {

    @Autowired
    private UserColumnSubscribeMapper userColumnSubscribeMapper;

    @Override
    public R subscribeColumn(UserColumnSubscribe userColumnSubscribe) {
        // 判断之前是否订阅过  如果订阅过  则 修改status 为 1
        List<UserColumnSubscribe> userColumnSubscribeList = userColumnSubscribeMapper.selectList(new QueryWrapper<UserColumnSubscribe>()
                .eq("user_id", userColumnSubscribe.getUserId())
                .eq("column_id", userColumnSubscribe.getColumnId()));
        if(!userColumnSubscribeList.isEmpty()){
            // update操作
            userColumnSubscribeList.get(0).setStatus("1");
            int i = userColumnSubscribeMapper.updateById(userColumnSubscribeList.get(0));
            if(i > 0){
                return R.ok("success", userColumnSubscribe);
            }

        }else{
            userColumnSubscribe.setStatus("1");
            int i = userColumnSubscribeMapper.insert(userColumnSubscribe);
            if (i > 0) {
                return R.ok("success", userColumnSubscribe);
            }
        }

        return R.error("error");
    }

    @Override
    public R unsubscribeColumn(UserColumnSubscribe userColumnSubscribe) {
        // 根据 columnId 和 userId 查询记录
        List<UserColumnSubscribe> userColumnSubscribeList = userColumnSubscribeMapper.selectList(new QueryWrapper<UserColumnSubscribe>()
                .eq("user_id", userColumnSubscribe.getUserId())
                .eq("column_id", userColumnSubscribe.getColumnId()));
        if(!userColumnSubscribeList.isEmpty()){
            UserColumnSubscribe subscribe = userColumnSubscribeList.get(0);
            subscribe.setStatus("0");
            //修改操作
            int i = userColumnSubscribeMapper.updateById(subscribe);
            if(i > 0){
                return R.ok("success", userColumnSubscribe);
            }
        }
        return R.error("error");
    }

    @Override
    public R getSubscribeDetail(String userId, String columnId) {
        List<UserColumnSubscribe> list = userColumnSubscribeMapper.selectList(new QueryWrapper<UserColumnSubscribe>()
                .eq("user_id", userId)
                .eq("column_id", columnId));
        if(!list.isEmpty()){
            return R.ok("success", list.get(0));
        }
        return R.error("error");
    }
}
