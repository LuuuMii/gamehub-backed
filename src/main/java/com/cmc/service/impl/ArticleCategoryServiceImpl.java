package com.cmc.service.impl;

import com.cmc.common.R;
import com.cmc.entity.ArticleCategory;
import com.cmc.mapper.ArticleCategoryMapper;
import com.cmc.service.ArticleCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author C
 * @since 2025-09-20
 */
@Service
public class ArticleCategoryServiceImpl extends ServiceImpl<ArticleCategoryMapper, ArticleCategory> implements ArticleCategoryService {

    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;

    @Override
    public R getAllArticleCategory() {
        List<ArticleCategory> articleCategories = articleCategoryMapper.selectList(null);
        return R.ok("查询成功",articleCategories);
    }
}
