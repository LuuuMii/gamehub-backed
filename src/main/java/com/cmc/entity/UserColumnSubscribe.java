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
 * 用户订阅专栏关联表
 * </p>
 *
 * @author C
 * @since 2025-10-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserColumnSubscribe对象", description="用户订阅专栏关联表")
public class UserColumnSubscribe implements Serializable {

    private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "主键ID")
        @TableId(value = "id", type = IdType.AUTO)
        private Long id;

        @ApiModelProperty(value = "用户ID")
        private Long userId;

        @ApiModelProperty(value = "专栏ID")
        private Long columnId;

        @ApiModelProperty(value = "订阅时间")
        @TableField(fill = FieldFill.INSERT)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date createTime;

        @ApiModelProperty(value = "修改时间")
        @TableField(fill = FieldFill.INSERT_UPDATE)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date updateTime;

        @ApiModelProperty(value = "订阅状态（1=已订阅，0=已取消）")
        private String status;


}
