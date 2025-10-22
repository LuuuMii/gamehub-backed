package com.cmc.service;

import com.cmc.common.R;
import com.cmc.entity.UserFollowRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author C
 * @since 2025-10-21
 */
public interface UserFollowRecordService extends IService<UserFollowRecord> {

    R syncUserFollowRecord(UserFollowRecord userFollowRecord);

    R getUserFollowRecord(Long followerId ,Long followeeId);
}
