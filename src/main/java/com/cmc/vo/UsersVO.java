package com.cmc.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UsersVO", description="")
public class UsersVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String username;

    private String email;

    private String phone;

    private String nickname;

    private String avatar;

    private String gender;

    private Date birthday;

    private String status;

    private Date lastLoginTime;

    private String lastLoginIp;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
