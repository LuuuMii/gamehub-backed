package com.cmc.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.List;

import com.cmc.constans.article.tag.ArticleTagIsUserInsertConstants;
import com.cmc.constans.article.tag.ArticleTagStatusConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author C
 * @since 2025-09-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ArticleTag对象", description="")
public class ArticleTag implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long pId;

    private String name;

    private String description;

    @TableField(fill = FieldFill.INSERT)
    private String status;

    private String isUserInsert;

    @TableField(exist = false)
    private String isUserInsertDesc;

    public String getIsUserInsertDesc(){
        return ArticleTagIsUserInsertConstants.getDesc(this.isUserInsert);
    }

    @TableField(exist = false)
    private String statusDesc;

    public String getStatusDesc(){
        return ArticleTagStatusConstants.getDesc(this.status);
    }

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;



    @TableField(exist = false)
    private List<ArticleTag> articleTagList;

}
