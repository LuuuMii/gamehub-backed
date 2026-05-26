package com.cmc.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmc.common.PageResult;
import com.cmc.common.R;
import com.cmc.constans.article.article.ArticleStatusConstant;
import com.cmc.constans.users.column.UserColumnStatusConstant;
import com.cmc.constans.users.column.UserColumnTypeConstant;
import com.cmc.dto.query.ArticleFromEsQueryDto;
import com.cmc.dto.query.ArticleQueryDto;
import com.cmc.entity.*;
import com.cmc.mapper.*;
import com.cmc.service.ArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmc.utils.RedisUtil;
import com.cmc.utils.article.CategoryUtil;
import com.cmc.vo.ArticlePageDetailsVO;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;


import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author C
 * @since 2025-10-06
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Value("${myBlog.column.defaultCoverImg}")
    private String defaultColumnCoverImg;

    @Autowired
    @Lazy
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private UserColumnMapper userColumnMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private ArticleColumnMapper articleColumnMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CategoryUtil categoryUtil;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public R addDraftArticle(Article article) {

        article.setStatus(ArticleStatusConstant.DRAFT);

        int i = articleMapper.insert(article);
        if (i > 0) {
            return R.ok("添加成功", article);
        }
        return R.error("添加失败");
    }

    @Override
    @Transactional
    public R publishArticle(Article article) {

        article.setStatus(ArticleStatusConstant.NORMAL);
        article.setPublishTime(new Date());
        article.setViewCount(0)
                .setLikeCount(0)
                .setUnlikeCount(0)
                .setCollectCount(0)
                .setCommentCount(0);

        int i;

        if (ObjectUtils.isEmpty(article.getId())) {
            //添加操作
            i = articleMapper.insert(article);
        } else {
            //修改操作
            i = articleMapper.updateById(article);
        }
        if (i <= 0) { return R.error("操作失败"); }

        Gson gson = new Gson();
        Type tagListType = new TypeToken<List<ArticleTag>>(){}.getType();
        List<ArticleTag> tagList = gson.fromJson(article.getTags(), tagListType);

        // 操作 标签
        //查询 数据库中 是否有这个tag 如果没有 则添加
        if(tagList !=null && !tagList.isEmpty()){
            for (ArticleTag articleTag : tagList) {
                //查询数据库
                QueryWrapper<ArticleTag> articleTagQueryWrapper = new QueryWrapper<>();
                articleTagQueryWrapper.eq("name",articleTag.getName());
                articleTagQueryWrapper.isNotNull("p_id");
                List<ArticleTag> tags = articleTagMapper.selectList(articleTagQueryWrapper);
                if(tags==null || tags.isEmpty()){
                    articleTag.setPId(0L);
                    articleTag.setIsUserInsert("1");
                    articleTag.setDescription("用户添加");
                    articleTagMapper.insert(articleTag);
                }
            }
        }

        Type columnListType = new TypeToken<List<UserColumn>>(){}.getType();
        List<UserColumn> columnList = gson.fromJson(article.getColumns(),columnListType);

        //操作 专栏
        // 查询这个用户是否有这个column  如果 没有则添加column  并且将 column 和 article 相关联
        if(columnList != null && !columnList.isEmpty()){
            for (UserColumn userColumn : columnList) {
                //判断  数据库中 是否有这个人的 userColumn  如果没有 则添加
                QueryWrapper<UserColumn> userColumnQueryWrapper = new QueryWrapper<>();
                userColumnQueryWrapper.eq("name",userColumn.getName());
                userColumnQueryWrapper.eq("create_by",userColumn.getCreateBy());
                List<UserColumn> columns = userColumnMapper.selectList(userColumnQueryWrapper);
                if(columns==null || columns.isEmpty()){
                    //查询不到这个 column  添加操作
                    userColumn.setStatus(UserColumnStatusConstant.NORMAL);
                    userColumn.setType(UserColumnTypeConstant.FREE);
                    userColumn.setCreateBy(article.getCreateBy());
                    userColumn.setDescription("暂无介绍");
                    userColumn.setCoverImg(defaultColumnCoverImg);
                    userColumnMapper.insert(userColumn);

                    //关联操作
                    ArticleColumn articleColumn = new ArticleColumn();
                    articleColumn.setArticleId(article.getId());
                    articleColumn.setColumnId(userColumn.getId());
                    //查询是否有 如果有 则 添加 否则 无需管理
                    QueryWrapper<ArticleColumn> articleColumnQueryWrapper = new QueryWrapper<>();
                    articleColumnQueryWrapper.eq("article_id",article.getId());
                    articleColumnQueryWrapper.eq("column_id",userColumn.getId());
                    List<ArticleColumn> selectedColumns = articleColumnMapper.selectList(articleColumnQueryWrapper);
                    if(selectedColumns==null || selectedColumns.isEmpty()){
                        articleColumnMapper.insert(articleColumn);
                    }

                }else{
                    // 如果查询到了这个 column 则 无需添加操作 直接 关联 article 和 column  操作
                    ArticleColumn articleColumn = new ArticleColumn();
                    articleColumn.setArticleId(article.getId());
                    articleColumn.setColumnId(userColumn.getId());
                    //查询是否有 如果有 则 添加 否则 无需管理
                    QueryWrapper<ArticleColumn> articleColumnQueryWrapper = new QueryWrapper<>();
                    articleColumnQueryWrapper.eq("article_id",article.getId());
                    //这个时候前端没有传输ID 过来 需要自己从数据库中查询ID

                    QueryWrapper<UserColumn>  userColumnForIdQueryWrapper = new QueryWrapper<>();
                    userColumnForIdQueryWrapper.eq("name",userColumn.getName());
                    userColumnForIdQueryWrapper.eq("create_by",userColumn.getCreateBy());
                    Long columnId = userColumnMapper.selectList(userColumnForIdQueryWrapper).get(0).getId();

                    articleColumnQueryWrapper.eq("column_id",String.valueOf(columnId));
                    List<ArticleColumn> selectedColumns = articleColumnMapper.selectList(articleColumnQueryWrapper);
                    if(selectedColumns==null || selectedColumns.isEmpty()){
                        articleColumnMapper.insert(articleColumn);
                    }
                }

            }
        }

        return R.ok("操作成功",article);
    }

    @Override
    public R updateDraftArticle(Article article) {

        int i = articleMapper.updateById(article);
        if (i > 0) {
            return R.ok("修改成功", article);
        }

        return R.error("修改失败");
    }

    @Override
    public R getArticleById(Long id) {
        Article article = articleMapper.selectById(id);
        if (!ObjectUtils.isEmpty(article)) {
            return R.ok("查询成功", article);
        }
        return R.error("查询失败");
    }

    @Override
    public R getAllDraftByUsername(String username) {

        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.eq("create_by",username);
        articleQueryWrapper.eq("status",ArticleStatusConstant.DRAFT);
        articleQueryWrapper.orderByDesc("create_time");
        List<Article> articleList = articleMapper.selectList(articleQueryWrapper);

        return R.ok("查询成功",articleList);
    }

    @Override
    @Transactional
    public R scheduledReleaseArticle(Article article) {

        article.setStatus(ArticleStatusConstant.TIMING);

        int i = articleMapper.insert(article);
        if (i > 0) {
            return R.ok("操作成功",article);
        }

        return R.error("操作失败");
    }

    @Override
    public R testRocketMQ(String msg) {
        rocketMQTemplate.convertAndSend("test-topic",msg);

        System.out.println("发送消息:" + msg);
        return R.ok();
    }

    @Transactional
    @Override
    public R getArticlePageDetailsById(Long articleId) {
        ArticlePageDetailsVO vo = new ArticlePageDetailsVO();
        // 根据 articleId 获取 文章内容
        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.eq("article_id",articleId);
        Article article = articleMapper.selectList(articleQueryWrapper).stream().findFirst().orElse(null);
        if(article!=null){
            vo.setArticle(article);
            // 根据文章create_by 获取 作者信息
            QueryWrapper<Users> usersQueryWrapper = new QueryWrapper<>();
            usersQueryWrapper.eq("username",article.getCreateBy());
            Users author = usersMapper.selectList(usersQueryWrapper).stream().findFirst().orElse(null);
            if(author!=null){
                vo.setUser(author);
            }
        }
        // 查询该作者的所有文章
        List<Article> authorArticleList = articleMapper.selectList(new QueryWrapper<Article>().eq("create_by", article.getCreateBy()));
        // 将 各个文章数量进行 分类
        if (!authorArticleList.isEmpty()){
            vo.setArticleNum(authorArticleList.size());
            vo.setOriginalArticleNum((int) authorArticleList.stream()
                    .filter(item -> item.getType().equals("0")).count());
            vo.setReprintedArticleNum((int)authorArticleList.stream()
                    .filter(item -> item.getType().equals("1")).count());
            vo.setTranslateArticleNum((int)authorArticleList.stream()
                    .filter(item -> item.getType().equals("2")).count());
        }

        return null;
    }

    /**
     * 获取论坛主页的热门文章(轮播图)
     * @return
     */
    @Override
    public R getHotArticle() {

        String hotArticleKey = "hotArticle";
        // 1. 查缓存
        Object cache = redisUtil.get(hotArticleKey);
        if (cache != null) {
            // JSON --> List
            List<Article> list = JSONUtil.toList(cache.toString(), Article.class);
            return R.ok(list);
        }

        // 2. 查数据库
        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.eq("status",ArticleStatusConstant.NORMAL)
                .eq("is_hot","1")
                .orderByDesc("create_time")
                .last("limit 50");
        List<Article> list = articleMapper.selectList(articleQueryWrapper);

        List<Article> top5 = list.stream()
                .sorted((a, b) -> Double.compare(calcScore(b), calcScore(a)))
                .limit(5)
                .collect(Collectors.toList());

        // 3. 存入缓存
        String jsonStr = JSONUtil.toJsonStr(top5);
        redisUtil.set(hotArticleKey,jsonStr,60, TimeUnit.MINUTES);

        return R.ok(top5);
    }

    /**
     * 根据条件分页查询
     * @param query
     * @return
     */
    @Override
    public R getArticleList(ArticleQueryDto query) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(StringUtils.isNotBlank(query.getCategory()),
                Article::getCategory,
                query.getCategory());

        wrapper.eq(Article::getStatus,
                ArticleStatusConstant.NORMAL);

        wrapper.orderByDesc(Article::getCreateTime);

        Page<Article> page = new Page<>(query.getPageNum(), query.getPageSize());
        page(page,wrapper);

        return R.ok(page);
    }

    /**
     * 根据Category获取热门帖子
     * @param articleQueryDto 查询条件
     * @return 热门帖子
     */
    @Override
    public R getHotArticleByCategory(ArticleQueryDto articleQueryDto) {
        if (StringUtils.isBlank(articleQueryDto.getCategory())){
            throw new RuntimeException("请添加查询参数");
        }
        String redisKey = "hotArticleByCategory:categoryId:" + categoryUtil.getCategoryId(articleQueryDto.getCategory());
        // 从redis 中获取 key
        Object cache = redisUtil.get(redisKey);
        if(cache != null){
            // JSON --> List
            List<Article> list = JSONUtil.toList(cache.toString(), Article.class);
            return R.ok(list);
        }
        // redis中没有值 从数据库中获取并且设置到redis
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus,ArticleStatusConstant.NORMAL);
        wrapper.eq(Article::getCategory,articleQueryDto.getCategory());
        wrapper.orderByDesc(Article::getCreateTime);
        Page<Article> page = new Page<>(articleQueryDto.getPageNum(), articleQueryDto.getPageSize());
        page(page,wrapper);
        List<Article> records = page.getRecords();
        List<Article> list = records.stream()
                .sorted((a, b) -> Double.compare(calcScore(a), calcScore(b)))
                .limit(10)
                .collect(Collectors.toList());

        // 设置值到redis
        redisUtil.set(redisKey,JSONUtil.toJsonStr(list),24,TimeUnit.HOURS);

        return R.ok(list);
    }

    /**
     * 从ES中获取数据  条件查询
     * @param queryDto 条件 带分页
     * @return 数据
     */
    @Override
    public R getArticleFromEs(ArticleFromEsQueryDto queryDto) {
        SearchRequest request = new SearchRequest("article_index");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 1.构建查询条件
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 关键词搜索
        if(queryDto.getKeyword() != null && !queryDto.getKeyword().isEmpty()){
            MultiMatchQueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery(queryDto.getKeyword())
                    .field("title",3.0f)
                    .field("summary",2.0f);
            boolQuery.must(multiMatchQuery);
        }
        // 时间过滤
        if(queryDto.getPublishBeginTime() !=null && queryDto.getPublishEndTime() !=null){
            boolQuery.filter(
                    QueryBuilders.rangeQuery("createTime")
                            .gte(queryDto.getPublishBeginTime())
                            .lte(queryDto.getPublishEndTime())
            );
        }
        sourceBuilder.query(boolQuery);

        // 2.排序
        if ("view".equals(queryDto.getOrder())){
            sourceBuilder.sort("viewCount", SortOrder.DESC);
        }else if("comment".equals(queryDto.getOrder())){
            sourceBuilder.sort("commentCount", SortOrder.DESC);
        }else if("time".equals(queryDto.getOrder())){
            sourceBuilder.sort("createTime", SortOrder.DESC);
        }

        // 3.分页
        sourceBuilder.from((queryDto.getPageNum() - 1) * queryDto.getPageSize());
        sourceBuilder.size(queryDto.getPageSize());

        // 4.高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.field("summary");

        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");

        sourceBuilder.highlighter(highlightBuilder);

        request.source(sourceBuilder);

        // 5.执行查询
        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<Map<String,Object>> result = new ArrayList<>();

        for (SearchHit hit : response.getHits().getHits()) {
            Map<String,Object> source = hit.getSourceAsMap();

            // 处理高亮
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields.get("title") != null){
                source.put("title",highlightFields.get("title").fragments()[0].string());
            }
            if(highlightFields.get("summary") != null){
                source.put("summary",highlightFields.get("summary").fragments()[0].string());
            }

            result.add(source);
        }

        PageResult<Map<String,Object>> pageResult = new PageResult<>();
        pageResult.setTotal(response.getHits().getTotalHits().value);
        pageResult.setPageNum(queryDto.getPageNum());
        pageResult.setPageSize(queryDto.getPageSize());
        pageResult.setRecords(result);

        return R.ok(pageResult);
    }

    @Override
    public R getArticleFromMysql(ArticleFromEsQueryDto queryDto) {
        String keyword = queryDto.getKeyword();
        queryDto.setOffset(queryDto.getPageNum() - 1);
        List<Article> articleList = articleMapper.getArticleFromMysql(queryDto);
        int totalCount = articleMapper.countArticleFromMysql(queryDto);

        List<Map<String,Object>> result = new ArrayList<>();

        for (Article article : articleList) {
            if (keyword != null && !keyword.isEmpty()
                    && article.getTitle() != null) {

                String title = article.getTitle();

                title = title.replace(
                        keyword,
                        "<span style='color:red'>" + keyword + "</span>"
                );

                article.setTitle(title);
            }
            ObjectMapper objectMapper = new ObjectMapper();

            Map<String, Object> map = objectMapper.convertValue(article, Map.class);
            result.add(map);
        }


        PageResult<Map<String,Object>> pageResult = new PageResult<>();
        pageResult.setTotal(totalCount);
        pageResult.setPageNum(queryDto.getPageNum());
        pageResult.setPageSize(queryDto.getPageSize());
        pageResult.setRecords(result);

        return R.ok(pageResult);

    }


    /**
     * 文章分数计算  目前根据点赞和浏览量
     * @param a
     * @return
     */
    private double calcScore(Article a){
        long hours = (System.currentTimeMillis() - a.getCreateTime().getTime()) / (1000 * 60 * 60);

        double score = a.getViewCount() * 0.7 + a.getLikeCount() * 0.8;

        return score / (hours + 2);
    }


}
