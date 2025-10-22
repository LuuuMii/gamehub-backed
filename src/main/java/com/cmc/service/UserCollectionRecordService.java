package com.cmc.service;

import com.cmc.common.R;
import com.cmc.entity.UserCollectionRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author C
 * @since 2025-10-17
 */
public interface UserCollectionRecordService extends IService<UserCollectionRecord> {

    R syncCollectionRecords(Long userId,Long targetId,String targetType, List<UserCollectionRecord> records);
}
