package com.cmc.controller;


import com.cmc.common.R;
import com.cmc.dto.query.ArticleFromEsQueryDto;
import com.cmc.dto.query.ArticleQueryDto;
import com.cmc.entity.Article;
import com.cmc.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${project.search.type}")
    private String searchType;
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

    @GetMapping("/getHotArticle")
    public R getHotArticle(){
        return articleService.getHotArticle();
    }

    /**
     * 根据条件分页查询数据
     * @param articleQueryDto
     * @return
     */
    @PostMapping("/getArticleList")
    public R getArticleList(@RequestBody ArticleQueryDto articleQueryDto){
        return articleService.getArticleList(articleQueryDto);
    }

    /**
     * 根据Category获取热门帖子
     * @param articleQueryDto 查询条件
     * @return 热门帖子
     */
    @PostMapping("/getHotArticleByCategory")
    public R getHotArticleByCategory(@RequestBody ArticleQueryDto articleQueryDto){
        return articleService.getHotArticleByCategory(articleQueryDto);
    }


    /**
     * 从ES中获取数据  条件查询
     * @param queryDto 条件 带分页
     * @return 数据
     */
    @PostMapping("/getArticleFromEs")
    public R getArticleFromEs(@RequestBody ArticleFromEsQueryDto queryDto){
        if("es".equals(searchType)){
            return  articleService.getArticleFromEs(queryDto);
        }else{
            return articleService.getArticleFromMysql(queryDto);
        }
    }

    @GetMapping("/testRocketMQ/{msg}")
    public R testRocketMQ(@PathVariable String msg){
        return articleService.testRocketMQ(msg);
    }



}

