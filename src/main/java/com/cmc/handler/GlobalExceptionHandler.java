package com.cmc.handler;

import cn.dev33.satoken.exception.NotLoginException;
import com.cmc.common.R;
import com.cmc.enums.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public R handlerNotLoginException(NotLoginException e){
        return R.error(ResultCodeEnum.NOT_LOGIN);
    }

    @ExceptionHandler(Exception.class)
    public R handleException(Exception e) {
        // 打印日志，便于排查
        log.error("全局异常", e);
        // 返回统一格式
        return R.error("系统异常: " + e.getMessage());
    }

}
