package com.cmc.mapper;

import com.cmc.dto.query.ArticleFromEsQueryDto;
import com.cmc.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author C
 * @since 2025-10-06
 */
public interface ArticleMapper extends BaseMapper<Article> {


    int increaseViewCount(@Param("articleId") Long articleId);

    List<Article> getArticleFromMysql(@Param("dto") ArticleFromEsQueryDto queryDto);

    int countArticleFromMysql(@Param("dto") ArticleFromEsQueryDto queryDto);
}
