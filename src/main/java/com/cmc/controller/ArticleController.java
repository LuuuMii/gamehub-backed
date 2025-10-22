package com.cmc.controller;


import com.cmc.common.R;
import com.cmc.entity.Article;
import com.cmc.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author C
 * @since 2025-10-06
 */
@RestController
@RequestMapping("/article")
@Api(tags = "文章操作接口")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping("/getArticleById/{id}")
    public R getArticleById(@PathVariable Long id){
        return articleService.getArticleById(id);
    }

    @PostMapping("/addDraftArticle")
    public R addDraftArticle(@RequestBody Article article) {
        return articleService.addDraftArticle(article);
    }

    @PostMapping("/updateDraftArticle")
    public R updateDraftArticle(@RequestBody Article article){
        return articleService.updateDraftArticle(article);
    }

    @PostMapping("/publishArticle")
    public R publishArticle(@RequestBody Article article){
        return articleService.publishArticle(article);
    }

    @PostMapping("/getAllDraftByUsername/{username}")
    public R getAllDraftByUsername(@PathVariable String username){
        return articleService.getAllDraftByUsername(username);
    }

    @PostMapping("/scheduledReleaseArticle")
    public R scheduledReleaseArticle(@RequestBody Article article){
        return articleService.scheduledReleaseArticle(article);
    }

    @GetMapping("/getArticlePageDetailsById/{id}")
    public R getArticlePageDetailsById(@PathVariable String id){
        return articleService.getArticlePageDetailsById(Long.valueOf(id));
    }

    @GetMapping("/testRocketMQ/{msg}")
    public R testRocketMQ(@PathVariable String msg){
        return articleService.testRocketMQ(msg);
    }



}

