package com.cmc.constans.article.article;

import java.util.HashMap;
import java.util.Map;

public class ArticleVisibleRangeConstant {

    public static final String ALL = "0";         // 全部可见
    public static final String ONLY_ME = "1";     // 仅我可见
    public static final String FANS = "2";        // 粉丝可见
    public static final String VIP = "3";         // VIP可见

    private static final Map<String, String> ARTICLE_VISIBLE_RANGE_MAP = new HashMap<>();

    static {
        ARTICLE_VISIBLE_RANGE_MAP.put(ALL, "全部可见");
        ARTICLE_VISIBLE_RANGE_MAP.put(ONLY_ME, "仅我可见");
        ARTICLE_VISIBLE_RANGE_MAP.put(FANS, "粉丝可见");
        ARTICLE_VISIBLE_RANGE_MAP.put(VIP, "VIP可见");
    }

    public static String getDesc(String code){return ARTICLE_VISIBLE_RANGE_MAP.getOrDefault(code,"未知状态");}


}
