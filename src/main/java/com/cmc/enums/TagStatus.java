package com.cmc.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TagStatus {
    NORMAL("0", "正常"),
    INVALID("1", "不正常");

    @EnumValue  // 标记存储到数据库的值
    private final String code;
    private final String desc;

    TagStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TagStatus fromCode(String code) {
        if (code == null) return null;
        for (TagStatus s : TagStatus.values()) {
            if (s.code.equals(code)) {
                return s;
            }
        }
        throw new IllegalArgumentException("未知的状态码: " + code);
    }
}
