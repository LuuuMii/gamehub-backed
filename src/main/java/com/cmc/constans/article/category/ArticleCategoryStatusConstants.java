package com.cmc.constans.article.category;

import java.util.HashMap;
import java.util.Map;

public class ArticleCategoryStatusConstants {
    public static final String NORMAL = "0";
    public static final String INVALID = "1";

    private static final Map<String, String> STATUS_DESC_MAP = new HashMap<>();
    static {
        STATUS_DESC_MAP.put(NORMAL, "正常");
        STATUS_DESC_MAP.put(INVALID, "不正常");
    }

    public static String getDesc(String code) {
        return STATUS_DESC_MAP.getOrDefault(code, "未知状态");
    }
}
