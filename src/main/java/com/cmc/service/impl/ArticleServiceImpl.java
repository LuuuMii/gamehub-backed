package com.cmc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cmc.common.R;
import com.cmc.constans.article.article.ArticleStatusConstant;
import com.cmc.constans.users.column.UserColumnStatusConstant;
import com.cmc.constans.users.column.UserColumnTypeConstant;
import com.cmc.entity.*;
import com.cmc.mapper.*;
import com.cmc.service.ArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmc.vo.ArticlePageDetailsVO;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;


import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
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
}
