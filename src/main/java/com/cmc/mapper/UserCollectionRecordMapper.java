package com.cmc.mapper;

import com.cmc.entity.UserCollectionRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author C
 * @since 2025-10-17
 */
public interface UserCollectionRecordMapper extends BaseMapper<UserCollectionRecord> {

    int selectCountDistinctUser(@Param("targetId") Long targetId,
                                @Param("targetType") String targetType);
}
