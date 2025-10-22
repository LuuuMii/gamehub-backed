package com.cmc.constans.article.article;

import java.util.HashMap;
import java.util.Map;

public class ArticleStatusConstant {

    public static final String NORMAL = "0";
    public static final String TIMING = "1";
    public static final String DRAFT = "2";
    public static final String PENDING_REVIEW = "3";
    public static final String INVALID = "4";

    private static final Map<String,String> ARTICLE_STATUS_MAP = new HashMap<String,String>();
    static {
        ARTICLE_STATUS_MAP.put(NORMAL,"已发布文章");
        ARTICLE_STATUS_MAP.put(TIMING,"定时发布文章");
        ARTICLE_STATUS_MAP.put(DRAFT,"草稿文章");
        ARTICLE_STATUS_MAP.put(PENDING_REVIEW,"待审核文章");
        ARTICLE_STATUS_MAP.put(INVALID,"无效文章");
    }

    public static String getDesc(String code){return ARTICLE_STATUS_MAP.getOrDefault(code,"未知状态");}


}
