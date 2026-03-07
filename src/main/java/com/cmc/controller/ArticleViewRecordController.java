package com.cmc.controller;


import com.cmc.common.R;
import com.cmc.entity.ArticleViewRecord;
import com.cmc.service.ArticleViewRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author C
 * @since 2026-03-06
 */
@RestController
@RequestMapping("/article-view-record")
public class ArticleViewRecordController {


    @Autowired
    private ArticleViewRecordService articleViewRecordService;

    /**
     * 用户观看帖子记录
     * @param record
     * @return
     */
    @PostMapping("/addViewRecord")
    public R addViewRecord(@RequestBody ArticleViewRecord record) {
        return articleViewRecordService.addViewRecord(record);
    }


}

