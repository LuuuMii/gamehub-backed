package com.cmc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cmc.common.R;
import com.cmc.entity.Article;
import com.cmc.entity.UserCollectionRecord;
import com.cmc.enums.type.CollectionTypeEnum;
import com.cmc.mapper.ArticleMapper;
import com.cmc.mapper.UserCollectionRecordMapper;
import com.cmc.service.UserCollectionRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
public class UserCollectionRecordServiceImpl extends ServiceImpl<UserCollectionRecordMapper, UserCollectionRecord> implements UserCollectionRecordService {

    @Autowired
    private UserCollectionRecordMapper userCollectionRecordMapper;
    @Autowired
    private ArticleMapper articleMapper;


    @Override
    public R syncCollectionRecords(Long userId,Long targetId,String targetType, List<UserCollectionRecord> records) {
        if (records ==null || records.isEmpty()) {
            records = Collections.emptyList();
        }

        // 1. 前端传来的folderId集合
        Set<Long> selectedFolderIds = records.stream()
                .map(UserCollectionRecord::getFolderId)
                .collect(Collectors.toSet());

        // 2. 查询数据库中该用户该目标所有的收藏记录(包括逻辑删除的)
        List<UserCollectionRecord> existingRecords = this.list(
                new QueryWrapper<UserCollectionRecord>()
                        .eq("user_id", userId)
                        .eq("target_id", targetId)
                        .eq("target_type", targetType)
        );
        Set<Long> existingFolderIds = existingRecords.stream()
                .map(UserCollectionRecord::getFolderId)
                .collect(Collectors.toSet());

        // 3. 找出需要新增的记录(前端有,数据库没有)
        List<UserCollectionRecord> toAdd = new ArrayList<>();
        for (Long folderId : selectedFolderIds) {
            if (!existingFolderIds.contains(folderId)){
                UserCollectionRecord record = new UserCollectionRecord();
                record.setFolderId(folderId);
                record.setUserId(userId);
                record.setTargetId(targetId);
                record.setTargetType(targetType);
                record.setIsDeleted("0");
                record.setStatus("0");
                toAdd.add(record);
            }
        }

        if (!toAdd.isEmpty()){
            this.saveBatch(toAdd);
        }

        // 4. 找出需要恢复的记录(前端有，但数据库记录已逻辑删除)
        List<UserCollectionRecord> toRecover = existingRecords.stream()
                .filter(r -> selectedFolderIds.contains(r.getFolderId()) && "1".equals(r.getIsDeleted()))
                .collect(Collectors.toList());

        for (UserCollectionRecord r : toRecover) {
            r.setIsDeleted("0");
        }
        if(!toRecover.isEmpty()){
            this.updateBatchById(toRecover);
        }

        // 5. 找出需要逻辑删除的记录(数据库有,但前端没有)
        List<UserCollectionRecord> toRemove = existingRecords.stream()
                .filter(r -> !selectedFolderIds.contains(r.getFolderId()) && "0".equals(r.getIsDeleted()))
                .collect(Collectors.toList());

        if (!toRemove.isEmpty()) {
            toRemove.forEach( r -> r.setIsDeleted("1"));
            this.updateBatchById(toRemove);
        }

        // 6.计算出该文章的收藏数 并且修改表中数据 collect_count
        int collectionCount = userCollectionRecordMapper.selectCountDistinctUser(targetId,targetType);
        // 根据枚举类型判断  然后使用对应的mapper
        CollectionTypeEnum typeEnum = CollectionTypeEnum.fromCode(targetType);
        if(typeEnum!=null){
            switch (typeEnum){
                case ARTICLE:
                    List<Article> articleList = articleMapper.selectList(new QueryWrapper<Article>()
                            .eq("id", targetId));
                    if (!articleList.isEmpty()){
                        Article article = articleList.get(0);
                        article.setCollectCount(collectionCount);
                        articleMapper.updateById(article);
                    }
                    break;
                case VIDEO:
                    System.out.println("关于video操作~");
                default:
                    break;
            }
        }

        return R.ok("收藏同步成功");
    }
}
