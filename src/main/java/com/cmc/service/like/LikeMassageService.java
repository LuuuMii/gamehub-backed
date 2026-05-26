package com.cmc.service.like;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cmc.common.R;
import com.cmc.entity.UserLikeRecord;

public interface LikeMassageService extends IService<UserLikeRecord> {
    R insertLikeRecord(Boolean liked,UserLikeRecord userLikeRecord, Long userId, Long targetId, String targetType);
}
