package com.cmc.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 搜索关键词池
 * </p>
 *
 * @author C
 * @since 2026-04-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SearchKeywordPool对象", description="搜索关键词池")
public class SearchKeywordPool implements Serializable {

    private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "主键ID")
        @TableId(value = "id", type = IdType.AUTO)
    private Long id;

        @ApiModelProperty(value = "关键词")
        private String keyword;

        @ApiModelProperty(value = "搜索次数（核心排序依据）")
        private Long searchCount;

        @ApiModelProperty(value = "点击次数（用于优化排序）")
        private Long clickCount;

        @ApiModelProperty(value = "权重（人工干预用，默认1）")
        private Integer weight;

        @ApiModelProperty(value = "状态：1-启用 0-禁用")
        private Integer status;

        @ApiModelProperty(value = "来源：USER用户搜索 ARTICLE文章 HOT热搜 SYSTEM系统预置")
        private String source;

        @ApiModelProperty(value = "最后搜索时间（用于时间衰减）")
        @TableField(fill = FieldFill.INSERT_UPDATE)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date lastSearchTime;

        @ApiModelProperty(value = "创建时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        @TableField(fill = FieldFill.INSERT)
        private Date createTime;

        @ApiModelProperty(value = "更新时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        @TableField(fill = FieldFill.INSERT_UPDATE)
        private Date updateTime;


}
