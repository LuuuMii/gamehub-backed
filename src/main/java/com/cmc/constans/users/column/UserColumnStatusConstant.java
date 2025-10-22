package com.cmc.constans.users.column;

import java.util.HashMap;
import java.util.Map;

public class UserColumnStatusConstant {

    public static final String NORMAL = "0";
    public static final String INVALID = "1";

    private static final Map<String,String> USER_COLUMN_STATUS_MAP = new HashMap<String,String>();
    static {
        USER_COLUMN_STATUS_MAP.put(NORMAL,"正常");
        USER_COLUMN_STATUS_MAP.put(INVALID,"不正常");
    }

    public static String getDesc(String code){ return USER_COLUMN_STATUS_MAP.getOrDefault(code,"未知状态"); }

}
