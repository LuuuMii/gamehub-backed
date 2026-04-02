package com.cmc.utils.article;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cmc.entity.ArticleCategory;
import com.cmc.mapper.ArticleCategoryMapper;
import com.cmc.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class CategoryUtil {

    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;
    @Autowired
    private RedisUtil redisUtil;

    public Long getCategoryId(String categoryName) {
        String redisKey = "articleCategory";
        Object cache = redisUtil.get(redisKey);
        if (cache != null) {
            // 获取数据
            List<ArticleCategory> list = JSONUtil.toList(cache.toString(), ArticleCategory.class);
            Optional<ArticleCategory> optional = list.stream()
                    .filter(c -> Objects.equals(c.getName(), categoryName))
                    .findFirst();
            ArticleCategory articleCategory = optional.orElse(null);
            if (articleCategory != null) {
                return articleCategory.getId();
            }
        }
        // 没有数据  从数据库中存入redis
        List<ArticleCategory> articleCategories = articleCategoryMapper.selectList(new LambdaQueryWrapper<>());

        redisUtil.set(redisKey,JSONUtil.toJsonStr(articleCategories),60, TimeUnit.MINUTES);

        Optional<ArticleCategory> optional = articleCategories.stream()
                .filter(c -> Objects.equals(c.getName(), categoryName))
                .findFirst();
        ArticleCategory articleCategory = optional.orElse(null);
        if (articleCategory != null) {
            return articleCategory.getId();
        }
        return null;
    }


}
