package com.cmc.enums.file;

public enum FileStatus {

    TEMP("0", "临时文件"),
    USED("1", "已使用");

    private final String code;
    private final String desc;

    FileStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}