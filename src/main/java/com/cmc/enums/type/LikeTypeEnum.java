package com.cmc.enums.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LikeTypeEnum {
    ARTICLE("0","文章点赞"),
    VIDEO("1","视频点赞"),
    ARTICLE_COMMENT("2","文章评论点赞");

    private final String code;
    private final String description;

    /**
     * 根据 code 获取枚举对象
     */
    public static LikeTypeEnum fromCode(String code) {
        for (LikeTypeEnum type : LikeTypeEnum.values()) {
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
        LikeTypeEnum type = fromCode(code);
        return type != null ? type.getDescription() : "未知类型";
    }

}
