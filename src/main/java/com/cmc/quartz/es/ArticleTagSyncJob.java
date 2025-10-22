package com.cmc.quartz.es;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cmc.entity.ArticleTag;
import com.cmc.mapper.ArticleTagMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ArticleTagSyncJob extends QuartzJobBean {

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    protected void executeInternal(JobExecutionContext context) {

        try {
            //查询所有的子标签
            QueryWrapper<ArticleTag> queryWrapper = new QueryWrapper<>();
            queryWrapper.isNotNull("p_id");
            queryWrapper.eq("status","0");
            queryWrapper.eq("is_user_insert",0);
            List<ArticleTag> articleTagList = articleTagMapper.selectList(queryWrapper);

            //创建index
            createIndexIfNotExist(restHighLevelClient,"article_tag_index");

            ObjectMapper objectMapper = new ObjectMapper();
            BulkRequest bulkRequest = new BulkRequest();

            for (ArticleTag articleTag : articleTagList) {
                Map<String, Object> jsonMap = new HashMap<>();
                jsonMap.put("id",articleTag.getId());
                jsonMap.put("name",articleTag.getName());
                jsonMap.put("description",articleTag.getDescription());

                bulkRequest.add(new IndexRequest("article_tag_index")
                        .id(String.valueOf(articleTag.getId()))
                        .source(jsonMap));

            }
            if (bulkRequest.numberOfActions() > 0) {
                restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("正在执行定时任务");
    }

    public void createIndexIfNotExist(RestHighLevelClient client, String indexName) throws IOException {
        boolean exists = client.indices().exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT);
        if (!exists) {
            CreateIndexRequest request = new CreateIndexRequest(indexName);
            // 定义映射，只存 id, name, description
            Map<String, Object> properties = new HashMap<>();
            Map<String, Object> id = new HashMap<>();
            id.put("type", "integer");
            Map<String, Object> name = new HashMap<>();
            name.put("type", "text");
            name.put("analyzer", "ik_max_word"); // 索引时使用ik分词
            name.put("search_analyzer", "ik_smart"); // 搜索时使用ik分词（可选）

            Map<String, Object> description = new HashMap<>();
            description.put("type", "text");
            description.put("analyzer", "ik_max_word");
            description.put("search_analyzer", "ik_smart");

            properties.put("id", id);
            properties.put("name", name);
            properties.put("description", description);

            Map<String, Object> mapping = new HashMap<>();
            mapping.put("properties", properties);

            request.mapping(mapping);
            CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
            System.out.println("创建 index：" + indexName + "，是否成功：" + createIndexResponse.isAcknowledged());
        }
    }
}
