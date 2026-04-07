package com.cmc.mapper;

import com.cmc.entity.UserSearchHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author C
 * @since 2026-04-07
 */
public interface UserSearchHistoryMapper extends BaseMapper<UserSearchHistory> {

    /**
     * 插入数据 如果重复 则修改 count + 1
     * @param userSearchHistory 记录
     * @return int
     */
    public int insertUserSearchHistoryOrUpdate(@Param("history") UserSearchHistory userSearchHistory);

}
