package com.cmc.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDataVO {
    private Integer totalLikeNum;
    private Integer totalOriginArticleNum;
    private Integer totalCollectNum;
    private Integer totalFansNum;

}
