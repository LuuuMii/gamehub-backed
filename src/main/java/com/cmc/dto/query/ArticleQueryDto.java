package com.cmc.dto.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleQueryDto {

    private Long pageNum;
    private Long pageSize;

    private String category;

}
