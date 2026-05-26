package com.cmc.controller;


import com.cmc.common.R;
import com.cmc.entity.ArticleTag;
import com.cmc.service.ArticleTagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author C
 * @since 2025-09-20
 */
@RestController
@RequestMapping("/article-tag")
@Api(tags = "文章标签管理接口")
public class ArticleTagController {

    @Autowired
    private ArticleTagService articleTagService;
    @Value("${project.search.type}")
    private String searchType;

    @GetMapping("/getAllArticleTag")
    public R getAllArticleTag(){
        return articleTagService.getAllArticleTag();
    }

    @GetMapping("/getArticleTagByES")
    public R getArticleTagByES(@RequestParam("keyword") String keyword){
        if("es".equals(searchType)){
            return articleTagService.getArticleTagByES(keyword);
        }else{
            return articleTagService.getArticleTagByMysql(keyword);
        }

    }


    @PostMapping("/addArticleTagByUser")
    public R addArticleTagByUser(@RequestBody ArticleTag articleTag){
        return articleTagService.addArticleTagByUser(articleTag);
    }

    @PostMapping("/deleteUserArticleTag")
    public R deleteUserArticleTag(@RequestBody ArticleTag articleTag){
        return articleTagService.deleteUserArticleTag(articleTag);
    }

}

