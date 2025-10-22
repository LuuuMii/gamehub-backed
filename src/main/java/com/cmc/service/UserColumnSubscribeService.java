package com.cmc.service;

import com.cmc.common.R;
import com.cmc.entity.UserColumnSubscribe;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户订阅专栏关联表 服务类
 * </p>
 *
 * @author C
 * @since 2025-10-16
 */
public interface UserColumnSubscribeService extends IService<UserColumnSubscribe> {

    R subscribeColumn(UserColumnSubscribe userColumnSubscribe);

    R unsubscribeColumn(UserColumnSubscribe userColumnSubscribe);

    R getSubscribeDetail(String userId, String columnId);
}
