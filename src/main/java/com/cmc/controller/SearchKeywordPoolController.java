package com.cmc.controller;


import com.cmc.common.R;
import com.cmc.entity.SearchKeywordPool;
import com.cmc.service.SearchKeywordPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 搜索关键词池 前端控制器
 * </p>
 *
 * @author C
 * @since 2026-04-08
 */
@RestController
@RequestMapping("/search-keyword-pool")
public class SearchKeywordPoolController {

    @Value("${project.search.type}")
    private String searchType;
    @Autowired
    private SearchKeywordPoolService searchKeywordPoolService;

    @PostMapping("/suggestSearch")
    public R suggestSearch(@RequestBody SearchKeywordPool searchKeywordPool) {
        if ("es".equals(searchType)) {
            return searchKeywordPoolService.suggestSearch(searchKeywordPool);
        }else{
            return R.ok();
        }

    }

}

