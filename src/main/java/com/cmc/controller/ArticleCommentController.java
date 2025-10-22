package com.cmc.controller;


import com.cmc.common.R;
import com.cmc.entity.ArticleComment;
import com.cmc.service.ArticleCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author C
 * @since 2025-10-22
 */
@RestController
@RequestMapping("/article-comment")
public class ArticleCommentController {

    @Autowired
    private ArticleCommentService articleCommentService;

    @GetMapping("/getArticleCommentByArticleId/{articleId}")
    public R getArticleCommentByArticleId(@PathVariable String articleId){
        return articleCommentService.getArticleCommentByArticleId(Long.valueOf(articleId));
    }

    @PostMapping("/addArticleComment")
    public R addArticleComment(@RequestBody ArticleComment articleComment){
        return articleCommentService.addArticleComment(articleComment);
    }

}

