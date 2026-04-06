package com.cmc.quartz.es;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmc.constans.article.article.ArticleStatusConstant;
import com.cmc.entity.Article;
import com.cmc.mapper.ArticleMapper;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArticleToEsSyncJob extends QuartzJobBean {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        int pageNum = 1;
        int pageSize = 500;

        try {
            while (true) {
                // 1. 分页查询文章
                Page<Article> page = new Page<>(pageNum, pageSize);
                LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Article::getStatus, ArticleStatusConstant.NORMAL);
                List<Article> articles = articleMapper.selectPage(page, wrapper).getRecords();

                if (articles == null || articles.isEmpty()) {
                    break; // 没有数据了
                }

                // 2. 构建 BulkRequest
                BulkRequest bulkRequest = new BulkRequest();

                for (Article article : articles) {
                    // 使用 IndexRequest 或 UpdateRequest
                    IndexRequest indexRequest = new IndexRequest("article_index")
                            .id(article.getId().toString())
                            .source(JSON.toJSONString(article), XContentType.JSON);

                    bulkRequest.add(indexRequest);
                }

                // 3. 执行批量请求
                BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

                if (bulkResponse.hasFailures()) {
                    System.err.println("批量更新ES失败: " + bulkResponse.buildFailureMessage());
                } else {
                    System.out.println("第 " + pageNum + " 页文章已同步到 ES，共 " + articles.size() + " 条");
                }

                pageNum++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new JobExecutionException(e);
        }
    }
}
