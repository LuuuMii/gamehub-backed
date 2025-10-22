package com.cmc.service;

import com.cmc.common.R;
import com.cmc.entity.UserCollectionFolder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author C
 * @since 2025-10-17
 */
public interface UserCollectionFolderService extends IService<UserCollectionFolder> {

    R getUserCollectionFoldersByUserId(Long userId);

    R getUserCollectionFoldersByUserIdForTarget(Long userId, Long targetId, String targetType);

    R addUserCollectionFolder(UserCollectionFolder userCollectionFolder);
}
