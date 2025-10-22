package com.cmc.constans.users.column;

import java.util.HashMap;
import java.util.Map;

public class UserColumnTypeConstant {

    public static final String FREE =  "0";
    public static final String PAID = "1";

    private static final Map<String,String> USER_COLUMN_TYPE_MAP = new HashMap<String,String>();
    static {
        USER_COLUMN_TYPE_MAP.put(FREE, "免费");
        USER_COLUMN_TYPE_MAP.put(PAID, "付费");
    }

    public static String getDesc(String code){
        return USER_COLUMN_TYPE_MAP.getOrDefault(code,"未知类型");
    }




}
