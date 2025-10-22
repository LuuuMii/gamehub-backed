package com.cmc.constans.article.tag;

import java.util.HashMap;
import java.util.Map;

public class ArticleTagIsUserInsertConstants {
    public static final String NORMAL = "0";
    public static final String IS_USER_INSERT = "1";

    private static final Map<String,String> IS_USER_INSERT_MAP = new HashMap<String,String>();
    static {
        IS_USER_INSERT_MAP.put(NORMAL,"系统自带标签");
        IS_USER_INSERT_MAP.put(IS_USER_INSERT,"用户插入标签");
    }

    public static String getDesc(String code){ return IS_USER_INSERT_MAP.getOrDefault(code,"未知状态"); }

}
