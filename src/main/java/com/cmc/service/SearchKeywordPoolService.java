package com.cmc.service;

import com.cmc.common.R;
import com.cmc.entity.SearchKeywordPool;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 搜索关键词池 服务类
 * </p>
 *
 * @author C
 * @since 2026-04-08
 */
public interface SearchKeywordPoolService extends IService<SearchKeywordPool> {

    R suggestSearch(SearchKeywordPool searchKeywordPool);
}
