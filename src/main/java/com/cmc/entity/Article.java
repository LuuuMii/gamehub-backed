package com.cmc.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.cmc.constans.article.article.ArticleStatusConstant;
import com.cmc.constans.article.article.ArticleVisibleRangeConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author C
 * @since 2025-10-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Article对象", description="")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    private String category;

    private String tags;

    @TableField("coverImg")
    private String coverImg;

    private String summary;

    private String columns;

    private String type;

    private String visibleRange;

    @TableField(exist = false)
    private String visibleRangeDesc;

    public String getVisibleRangeDesc(){
        return ArticleVisibleRangeConstant.getDesc(this.visibleRange);
    }

    private String createBy;

    @TableField(fill = FieldFill.DEFAULT)
    private String status;

    @TableField(exist = false)
    private String statusDesc;

    public String getStatusDesc(){
        return ArticleStatusConstant.getDesc(this.status);
    }
    private Integer likeCount;

    private Integer unlikeCount;

    private Integer viewCount;

    private Integer collectCount;

    private Integer commentCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date publishTime;


}
