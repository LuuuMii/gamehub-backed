package com.cmc.service;

import com.cmc.common.R;
import com.cmc.entity.ArticleComment;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author C
 * @since 2025-10-22
 */
public interface ArticleCommentService extends IService<ArticleComment> {

    R getArticleCommentByArticleId(Long articleId);

    R addArticleComment(ArticleComment articleComment);
}
