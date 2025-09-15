package com.cmc.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一返回结果封装类
 */
public class R<T> implements Serializable {
    private int code;       // 状态码
    private String message; // 提示信息
    private T data;         // 数据

    // 默认构造
    public R() {}

    // 构造方法
    public R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 成功返回，不带数据
    public static R ok() {
        return new R(200, "success", null);
    }

    // 成功返回，带数据
    public static <T> R ok(T data) {
        return new R(200, "success", data);
    }

    // 成功返回，自定义消息
    public static R ok(String message) {
        return new R(200, message, null);
    }

    // 成功返回，自定义消息和数据
    public static <T> R ok(String message, T data) {
        return new R(200, message, data);
    }

    // 失败返回，不带数据
    public static R error() {
        return new R(500, "error", null);
    }

    // 失败返回，自定义消息
    public static R error(String message) {
        return new R(500, message, null);
    }

    // 失败返回，自定义状态码和消息
    public static R error(int code, String message) {
        return new R(code, message, null);
    }

    // 失败返回，自定义状态码、消息和数据
    public static <T> R error(int code, String message, T data) {
        return new R<>(code, message, data);
    }

    // get / set 方法
    public int getCode() {
        return code;
    }

    public R<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public R<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public R<T> setData(T data) {
        this.data = data;
        return this;
    }

    // 链式操作示例
    public R<T> putData(T data) {
        this.data = data;
        return this;
    }
}
