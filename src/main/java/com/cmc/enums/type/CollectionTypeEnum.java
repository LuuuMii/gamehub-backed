package com.cmc.enums.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CollectionTypeEnum {

    ARTICLE("0","文章"),
    VIDEO("1","视频");

    private final String code;
    private final String description;

    /**
     * 根据 code 获取枚举对象
     */
    public static CollectionTypeEnum fromCode(String code) {
        for (CollectionTypeEnum type : CollectionTypeEnum.values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据 code 获取描述
     */
    public static String getDescriptionByCode(String code) {
        CollectionTypeEnum type = fromCode(code);
        return type != null ? type.getDescription() : "未知类型";
    }


}
