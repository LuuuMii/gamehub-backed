package com.cmc.enums.video;

import lombok.Data;
import lombok.Getter;

@Getter
public enum UploadTaskStatus {

    INIT(0, "初始化"),
    UPLOADING(1, "上传中"),
    SUCCESS(2, "已完成"),
    FAIL(3, "失败"),
    CANCEL(4, "取消");

    private final Integer code;
    private final String desc;

    UploadTaskStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }



}
