package com.cmc.quartz.es;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmc.entity.SearchKeywordPool;
import com.cmc.mapper.SearchKeywordPoolMapper;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(
        name = "project.es.enable",
        havingValue = "true"
)
public class SearchKeywordToEsSyncJob extends QuartzJobBean {
    @Autowired
    private SearchKeywordPoolMapper searchKeywordPoolMapper;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        int pageNum = 1;
        int pageSize = 500;

        try {
            while(true) {
                // 查询数据
                Page<SearchKeywordPool> page = new Page<>(pageNum, pageSize);
                LambdaQueryWrapper<SearchKeywordPool> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(SearchKeywordPool::getStatus,1);
                List<SearchKeywordPool> poolList = searchKeywordPoolMapper.selectPage(page, wrapper).getRecords();
                if(poolList == null || poolList.isEmpty()) {
                    break;
                }
                // 构建bulk
                BulkRequest bulkRequest = new BulkRequest();
                for (SearchKeywordPool searchKeyword : poolList) {
                    Map<String, Object> jsonMap = new HashMap<>();
                    jsonMap.put("keyword", searchKeyword.getKeyword());
                    jsonMap.put("search_count", searchKeyword.getSearchCount());
                    jsonMap.put("weight", searchKeyword.getWeight());
                    jsonMap.put("status", searchKeyword.getStatus());
                    jsonMap.put("last_search_time", searchKeyword.getLastSearchTime());

                    Map<String, Object> suggestMap = new HashMap<>();
                    List<String> suggestList = new ArrayList<>();
                    suggestList.add(searchKeyword.getKeyword());
                    suggestMap.put("input", suggestList);
                    suggestMap.put("weight", searchKeyword.getSearchCount());

                    jsonMap.put("suggest", suggestMap);

                    bulkRequest.add(new IndexRequest("search_keyword_index")
                            .id(String.valueOf(searchKeyword.getId()))
                            .source(jsonMap));
                }
                BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                System.out.println("导入完成，成功条数：" + bulkResponse.getItems().length);
                if(poolList.size() < pageSize) break;
                pageNum++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public List<String> generateSuggestInput(String keyword) {
        List<String> input = new ArrayList<>();
        input.add(keyword); // 原词

        // 自动生成前缀
        for (int i = keyword.length() - 1; i >= 1; i--) {
            input.add(keyword.substring(0, i));
        }

        return input;
    }
}
