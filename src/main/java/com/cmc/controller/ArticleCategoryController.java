package com.cmc.controller;


import com.cmc.common.R;
import com.cmc.service.ArticleCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author C
 * @since 2025-09-20
 */
@RestController
@RequestMapping("/article-category")
@Api(tags = "文章类型接口")
public class ArticleCategoryController {

    @Autowired
    private ArticleCategoryService articleCategoryService;

    @GetMapping("/getAllArticleCategory")
    @ApiOperation("获取所有文章类型")
    public R getAllArticleCategory(){
        return articleCategoryService.getAllArticleCategory();
    }

}

