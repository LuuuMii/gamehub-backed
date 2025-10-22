package com.cmc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cmc.common.R;
import com.cmc.entity.UserCollectionFolder;
import com.cmc.entity.UserCollectionRecord;
import com.cmc.mapper.UserCollectionFolderMapper;
import com.cmc.mapper.UserCollectionRecordMapper;
import com.cmc.service.UserCollectionFolderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author C
 * @since 2025-10-17
 */
@Service
@Transactional
public class UserCollectionFolderServiceImpl extends ServiceImpl<UserCollectionFolderMapper, UserCollectionFolder> implements UserCollectionFolderService {

    @Autowired
    private UserCollectionFolderMapper userCollectionFolderMapper;
    @Autowired
    private UserCollectionRecordMapper userCollectionRecordMapper;

    @Override
    public R getUserCollectionFoldersByUserId(Long userId) {
        List<UserCollectionFolder> list = userCollectionFolderMapper.selectList(new QueryWrapper<UserCollectionFolder>().eq("user_id", userId));

        return R.ok("success",list);
    }

    /**
     * 根据用户ID 查询 收藏夹文件  并且判断收藏夹中是否有这个文件
     * @param userId  用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return R
     */
    @Override
    public R getUserCollectionFoldersByUserIdForTarget(Long userId, Long targetId, String targetType) {
        // 先查询该用户所有的文件夹
        List<UserCollectionFolder> folderList = userCollectionFolderMapper.selectList(new QueryWrapper<UserCollectionFolder>().eq("user_id", userId));
        if(!folderList.isEmpty()){
            List<UserCollectionRecord> records = userCollectionRecordMapper.selectList(
                    new QueryWrapper<UserCollectionRecord>()
                            .eq("target_id", targetId)
                            .eq("target_type", targetType)
                            .eq("is_deleted", "0")
                            .in("folder_id", folderList.stream().map(UserCollectionFolder::getId).collect(Collectors.toList()))
            );
            Set<Long> collectedFolderIds = records.stream()
                    .map(UserCollectionRecord::getFolderId)
                    .collect(Collectors.toSet());

            for (UserCollectionFolder folder : folderList) {
                folder.setIsCollected(collectedFolderIds.contains(folder.getId()) ? "1" : "0");
            }
        }
        return R.ok("success",folderList);
    }

    @Override
    public R addUserCollectionFolder(UserCollectionFolder userCollectionFolder) {
        if (userCollectionFolder.getIsDefault()==null || !userCollectionFolder.getIsDefault().equals("1")){
            userCollectionFolder.setIsDefault("0");
        }
        // 判断该用户的是否有同名文件夹
        List<UserCollectionFolder> folderList = userCollectionFolderMapper.selectList(new QueryWrapper<UserCollectionFolder>()
                .eq("user_id", userCollectionFolder.getUserId())
                .eq("name",userCollectionFolder.getName()));
        if(!folderList.isEmpty()){
            return R.error("存在"+userCollectionFolder.getName()+"该文件夹,添加失败");
        }
        int i = userCollectionFolderMapper.insert(userCollectionFolder);
        if(i>0){
            return R.ok("success",userCollectionFolder);
        }

        return R.error("fail");
    }
}
