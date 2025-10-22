package com.cmc.vo;

import com.cmc.entity.Article;
import com.cmc.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticlePageDetailsVO {
    private Article article;

    private Users user;

    private Integer articleNum;

    private Integer originalArticleNum;

    private Integer reprintedArticleNum;

    private Integer translateArticleNum;

}
