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
 * 文件上传任务表（分片上传管理）
 * </p>
 *
 * @author C
 * @since 2026-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UploadTaskMultipart对象", description="文件上传任务表（分片上传管理）")
public class UploadTaskMultipart implements Serializable {

    private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "主键ID")
        @TableId(value = "id", type = IdType.AUTO)
        private Long id;

        @ApiModelProperty(value = "上传用户ID")
        private Long userId;

        @ApiModelProperty(value = "原始文件名（用户上传的文件名）")
        private String fileName;

        @ApiModelProperty(value = "OSS存储路径（唯一标识）")
        private String objectName;

        @ApiModelProperty(value = "文件MD5值（用于秒传判断）")
        private String fileMd5;

        @ApiModelProperty(value = "OSS分片上传ID")
        private String uploadId;

        @ApiModelProperty(value = "总分片数量")
        private Integer totalChunks;

        @ApiModelProperty(value = "已上传分片数量")
        private Integer uploadedChunks;

        @ApiModelProperty(value = "文件大小（字节）")
        private Long fileSize;

        @ApiModelProperty(value = "上传状态：0初始化 1上传中 2已完成 3失败 4取消")
        private Integer status;

        @ApiModelProperty(value = "创建时间")
        @TableField(fill = FieldFill.INSERT)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date createTime;

        @ApiModelProperty(value = "更新时间")
        @TableField(fill = FieldFill.INSERT_UPDATE)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date updateTime;


}
