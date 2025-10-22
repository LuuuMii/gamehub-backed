package com.cmc.service;

import com.cmc.common.R;
import com.cmc.entity.UserUnlikeRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author C
 * @since 2025-10-20
 */
public interface UserUnlikeRecordService extends IService<UserUnlikeRecord> {

    R syncUnlikeRecord(UserUnlikeRecord userUnlikeRecord, Long userId, Long targetId, String targetType);

    R getUserUnlikeRecord(Long userId, Long targetId, String targetType);
}
