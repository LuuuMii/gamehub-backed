package com.cmc.service;

import com.cmc.common.R;
import com.cmc.entity.ArticleViewRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author C
 * @since 2026-03-06
 */
public interface ArticleViewRecordService extends IService<ArticleViewRecord> {

    R addViewRecord(ArticleViewRecord record);
}
