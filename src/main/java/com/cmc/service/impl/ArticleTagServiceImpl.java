package com.cmc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cmc.common.R;
import com.cmc.entity.ArticleTag;
import com.cmc.mapper.ArticleTagMapper;
import com.cmc.service.ArticleTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author C
 * @since 2025-09-20
 */
@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements ArticleTagService {

    private static final String INDEX = "article_tag_index";

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public R getAllArticleTag() {
        QueryWrapper<ArticleTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("p_id");
        List<ArticleTag> parentList = articleTagMapper.selectList(queryWrapper);
        parentList.stream().forEach(parent -> {
            parent.setArticleTagList(articleTagMapper.selectList(new QueryWrapper<ArticleTag>().eq("p_id", parent.getId())));
        });
        return R.ok("查询成功",parentList);
    }

    @Override
    public R getArticleTagByES(String keyword) {
        List<Map<String,Object>> results = new ArrayList<>();

        try {

            SearchRequest searchRequest = new SearchRequest(INDEX);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            // 使用match查询 + IK分词器
            sourceBuilder.query(QueryBuilders.matchQuery("name", keyword));
            sourceBuilder.size(50);

            searchRequest.source(sourceBuilder);

            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            for (SearchHit hit : response.getHits().getHits()) {
                results.add(hit.getSourceAsMap());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return R.ok(results);
    }

    @Override
    public R addArticleTagByUser(ArticleTag articleTag) {
        //判断数据库中是否有这个标签 如果有 则不添加
        QueryWrapper<ArticleTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",articleTag.getName().trim());
        if (articleTagMapper.selectOne(queryWrapper)==null){
            articleTag.setPId(0L);
            articleTag.setStatus("0");
            articleTag.setIsUserInsert("1");
            int i = articleTagMapper.insert(articleTag);
            if (i > 0) {
                return R.ok("添加成功");
            }
        }

        return R.error("添加失败");
    }

    @Override
    public R deleteUserArticleTag(ArticleTag articleTag) {
        QueryWrapper<ArticleTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",articleTag.getName().trim());
        if (articleTagMapper.selectOne(queryWrapper).getIsUserInsert().equals("1")){
            int i = articleTagMapper.deleteById(articleTag.getId());
            if (i > 0) {
                return R.ok("删除成功");
            }
        }
        return R.error("删除失败");
    }
}
