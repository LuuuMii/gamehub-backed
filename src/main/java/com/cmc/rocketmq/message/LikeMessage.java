package com.cmc.rocketmq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeMessage {

    private Long targetId;
    private String targetType;
    private Long userId;
    private String action;


}
