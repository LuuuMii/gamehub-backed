package com.cmc.service;

import com.cmc.common.R;
import com.cmc.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author C
 * @since 2025-10-06
 */
public interface ArticleService extends IService<Article> {

    R addDraftArticle(Article article);

    R publishArticle(Article article);

    R updateDraftArticle(Article article);

    R getArticleById(Long id);

    R getAllDraftByUsername(String username);

    R scheduledReleaseArticle(Article article);

    R testRocketMQ(String msg);

    R getArticlePageDetailsById(Long articleId);
}
