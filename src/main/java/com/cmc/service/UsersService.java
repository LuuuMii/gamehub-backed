package com.cmc.service;

import com.cmc.common.R;
import com.cmc.entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author C
 * @since 2025-09-10
 */
public interface UsersService extends IService<Users> {

    R loginByUsername(Users user);

    R register(Users users);

    R getUserInfoById(Long id);

    R getUserInfoByToken(String token);

    R getUserInfoByUsername(String username);

    R getAuthorDataForArticlePage(String username);

    R logout(HttpServletRequest request);

    R isLogin(Long userId);
}
