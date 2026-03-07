package com.cmc.enums.article;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum UserBehaviorType {

    VIEW_BEHAVIOR(1,1),
    LIKE_BEHAVIOR(2,3),
    COLLECT_BEHAVIOR(3,5);

    private Integer type;
    private Integer score;

    public static Integer getScore(Integer type){
        for (UserBehaviorType userBehaviorType : UserBehaviorType.values()) {
            if (userBehaviorType.getType().equals(type)) {
                return userBehaviorType.getScore();
            }
        }
        return null;
    }

}
