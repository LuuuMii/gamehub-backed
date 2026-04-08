package com.cmc.service.impl;

import com.cmc.common.R;
import com.cmc.common.SuggestResult;
import com.cmc.entity.SearchKeywordPool;
import com.cmc.mapper.SearchKeywordPoolMapper;
import com.cmc.service.SearchKeywordPoolService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <p>
 * 搜索关键词池 服务实现类
 * </p>
 *
 * @author C
 * @since 2026-04-08
 */
@Service
public class SearchKeywordPoolServiceImpl extends ServiceImpl<SearchKeywordPoolMapper, SearchKeywordPool> implements SearchKeywordPoolService {

    @Autowired
    private RestHighLevelClient  restHighLevelClient;

    @Override
    public R suggestSearch(SearchKeywordPool searchKeywordPool) {
        String prefix = searchKeywordPool.getKeyword();
        SearchRequest searchRequest = new SearchRequest("search_keyword_index");
        System.out.println("prefix: " + prefix);

        CompletionSuggestionBuilder suggestBuilder = SuggestBuilders
                .completionSuggestion("suggest")
                .prefix(prefix)
                .size(10);

        SuggestBuilder suggest = new SuggestBuilder();
        suggest.addSuggestion("keyword_suggest", suggestBuilder);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.suggest(suggest);
        searchRequest.source(sourceBuilder);

        SearchResponse response;
        try {
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException("ES查询失败", e);
        }

        List<SuggestResult> resultList = new ArrayList<>();

        Suggest suggestResponse = response.getSuggest();
        if (suggestResponse != null) {
            Suggest.Suggestion<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> suggestion =
                    suggestResponse.getSuggestion("keyword_suggest");

            if (suggestion instanceof CompletionSuggestion) {
                CompletionSuggestion completionSuggestion = (CompletionSuggestion) suggestion;

                for (CompletionSuggestion.Entry.Option option : completionSuggestion.getOptions()) {
                    String rawKeyword = option.getText().string();
                    long weight = (long) option.getScore();

                    String highlighted = rawKeyword.replaceAll("(?i)(" + Pattern.quote(prefix) + ")", "<em>$1</em>");
                    resultList.add(new SuggestResult(highlighted, rawKeyword, weight));
                }
            }
        }

        return R.ok(resultList);
    }
}
