package com.cmc.dto.query;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleFromEsQueryDto {

    private int pageNum;
    private int pageSize;
    private String keyword;
    private String type;
    private String order;
    private Long publishBeginTime;
    private Long publishEndTime;

}
