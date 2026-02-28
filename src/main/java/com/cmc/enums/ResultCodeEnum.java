package com.cmc.enums;

public enum ResultCodeEnum {

    SUCCESS(200,"成功"),
    ERROR(500, "系统异常"),
    PARAM_ERROR(400, "参数错误"),

    NOT_LOGIN(401, "请先登录"),
    NO_PERMISSION(403, "没有权限"),

    BUSINESS_ERROR(5000, "业务异常");

    private final int code;
    private final String msg;

    ResultCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode(){
        return code;
    }

    public String getMsg(){
        return msg;
    }

}
