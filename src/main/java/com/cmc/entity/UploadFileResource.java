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
 * 文件资源表
 * </p>
 *
 * @author C
 * @since 2026-06-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UploadFileResource对象", description="文件资源表")
public class UploadFileResource implements Serializable {

    private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "主键ID")
        @TableId(value = "id", type = IdType.AUTO)
    private Long id;

        @ApiModelProperty(value = "文件原始名称")
        private String fileName;

        @ApiModelProperty(value = "文件访问地址")
        private String fileUrl;

        @ApiModelProperty(value = "OSS对象名称")
        private String objectName;

        @ApiModelProperty(value = "文件大小(字节)")
        private Long fileSize;

        @ApiModelProperty(value = "文件类型")
        private String fileType;

        @ApiModelProperty(value = "文件分类(OSS文件夹)")
        private String fileCategory;

        @ApiModelProperty(value = "文件状态 0-临时文件 1-已使用")
        private String status;

        @ApiModelProperty(value = "创建时间")
        @TableField(fill = FieldFill.INSERT)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date createTime;


}
