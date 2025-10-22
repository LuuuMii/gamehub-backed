package com.cmc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cmc.common.R;
import com.cmc.entity.UserFollowRecord;
import com.cmc.entity.Users;
import com.cmc.mapper.ArticleMapper;
import com.cmc.mapper.UserFollowRecordMapper;
import com.cmc.mapper.UsersMapper;
import com.cmc.service.UserFollowRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author C
 * @since 2025-10-21
 */
@Service
@Transactional
public class UserFollowRecordServiceImpl extends ServiceImpl<UserFollowRecordMapper, UserFollowRecord> implements UserFollowRecordService {

    @Autowired
    private UserFollowRecordMapper userFollowRecordMapper;
    @Autowired
    private UsersMapper usersMapper;

    /**
     * 用户关注操作
     * @param userFollowRecord 实体
     * @return R
     */
    @Override
    public R syncUserFollowRecord(UserFollowRecord userFollowRecord) {
        // 1. 查询表中是否有这条记录
        UserFollowRecord existRecord = userFollowRecordMapper.selectOne(new QueryWrapper<UserFollowRecord>()
                .eq("follower_id", userFollowRecord.getFollowerId())
                .eq("followee_id", userFollowRecord.getFolloweeId()));
        if (existRecord != null) {
            // 不为空的情况 判断状态 is_deleted
            if ("0".equals(existRecord.getIsDeleted())){
                existRecord.setIsDeleted("1");
            }else{
                existRecord.setIsDeleted("0");
            }
            userFollowRecordMapper.updateById(existRecord);
        }else{
            // 没有这条记录 添加关注
            userFollowRecord.setIsDeleted("0");
            userFollowRecord.setStatus("0");
            userFollowRecordMapper.insert(userFollowRecord);
        }

        // 2. 修改 user表中的 fans_count
        Users users = usersMapper.selectOne(new QueryWrapper<Users>()
                .eq("id", userFollowRecord.getFolloweeId()));

        // 计算数量
        if(users!=null){
            users.setFansCount(userFollowRecordMapper.selectCount(new QueryWrapper<UserFollowRecord>()
                    .eq("followee_id", userFollowRecord.getFolloweeId())));
            usersMapper.updateById(users);
        }

        return R.ok("success",userFollowRecord);
    }

    @Override
    public R getUserFollowRecord(Long followerId, Long followeeId) {

        UserFollowRecord userFollowRecord = userFollowRecordMapper.selectOne(new QueryWrapper<UserFollowRecord>()
                .eq("follower_id", followerId)
                .eq("followee_id", followeeId));
        if (userFollowRecord != null) {
            return R.ok("success",userFollowRecord);
        }

        return R.error("fail");
    }
}
