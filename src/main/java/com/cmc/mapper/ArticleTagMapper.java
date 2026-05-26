package com.cmc.mapper;

import com.cmc.entity.ArticleTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author C
 * @since 2025-09-20
 */
@Repository
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {


    List<ArticleTag> searchByKeyword(@Param("keyword") String keyword);
}
