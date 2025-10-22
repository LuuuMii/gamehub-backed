package com.cmc.service;

import com.cmc.common.R;
import com.cmc.entity.ArticleTag;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author C
 * @since 2025-09-20
 */
public interface ArticleTagService extends IService<ArticleTag> {

    R getAllArticleTag();

    R getArticleTagByES(String keyword);

    R addArticleTagByUser(ArticleTag articleTag);

    R deleteUserArticleTag(ArticleTag articleTag);
}
