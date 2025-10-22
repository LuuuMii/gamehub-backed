package com.cmc.service;

import com.cmc.common.R;
import com.cmc.entity.UserLikeRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author C
 * @since 2025-10-18
 */
public interface UserLikeRecordService extends IService<UserLikeRecord> {

    R syncLikeRecord(UserLikeRecord userLikeRecord, Long userId, Long targetId, String targetType);

    R getUserLikeRecord(Long userId, Long targetId, String targetType);
}
