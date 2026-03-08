package com.cmc.config.login;

import cn.hutool.core.bean.BeanUtil;
import com.cmc.dto.UserDTO;
import com.cmc.utils.RedisUtil;
import com.cmc.utils.UserContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("satoken");
        if (StringUtils.isBlank(token)){
            return true;
        }

        // 从redis中取用户
        String key = "login:token:" + token;

        Map<Object, Object> userMap = redisUtil.getHash(key);

        if(userMap.isEmpty()){
            return true;
        }

        // 转换成对象
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);

        UserContext.setUser(userDTO);

        System.out.println("UserContext:"+userDTO);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("UserContext Remove:");
        UserContext.remove();
    }
}
