package com.cmc.service;

import com.cmc.common.R;
import com.cmc.entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
