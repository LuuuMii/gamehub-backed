package com.cmc.service;

import com.cmc.common.R;
import com.cmc.entity.UserSearchHistory;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author C
 * @since 2026-04-07
 */
public interface UserSearchHistoryService extends IService<UserSearchHistory> {

    R insertUserSearchHistory(UserSearchHistory userSearchHistory);

    R getUserSearchHistory(Long userId);

    R deleteUserSearchHistory(UserSearchHistory userSearchHistory);

    R deleteAllHistory(Long userId);
}
