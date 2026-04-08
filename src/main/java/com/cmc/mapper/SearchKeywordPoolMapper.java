package com.cmc.mapper;

import com.cmc.entity.SearchKeywordPool;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 搜索关键词池 Mapper 接口
 * </p>
 *
 * @author C
 * @since 2026-04-08
 */
public interface SearchKeywordPoolMapper extends BaseMapper<SearchKeywordPool> {

    int insertSearchWordOrUpdate(@Param("keyword") SearchKeywordPool searchKeyword);
}
