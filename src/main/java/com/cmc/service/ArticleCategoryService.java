package com.cmc.service;

import com.cmc.common.R;
import com.cmc.entity.ArticleCategory;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author C
 * @since 2025-09-20
 */
public interface ArticleCategoryService extends IService<ArticleCategory> {

    R getAllArticleCategory();
}
