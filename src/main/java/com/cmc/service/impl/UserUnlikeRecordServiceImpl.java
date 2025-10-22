package com.cmc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cmc.common.R;
import com.cmc.entity.Article;
import com.cmc.entity.UserUnlikeRecord;
import com.cmc.enums.type.UnlikeTypeEnum;
import com.cmc.mapper.ArticleMapper;
import com.cmc.mapper.UserUnlikeRecordMapper;
import com.cmc.service.UserUnlikeRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author C
 * @since 2025-10-20
 */
@Service
@Transactional
public class UserUnlikeRecordServiceImpl extends ServiceImpl<UserUnlikeRecordMapper, UserUnlikeRecord> implements UserUnlikeRecordService {

    @Autowired
    private UserUnlikeRecordMapper userUnlikeRecordMapper;
    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public R syncUnlikeRecord(UserUnlikeRecord userUnlikeRecord, Long userId, Long targetId, String targetType) {
        // 查询数据库中是否有这条记录
        List<UserUnlikeRecord> records = userUnlikeRecordMapper.selectList(new QueryWrapper<UserUnlikeRecord>().eq("user_id", userId)
                .eq("target_id", targetId)
                .eq("target_type", targetType));
        if(!records.isEmpty()) {
            // 查询到这条数据
            UserUnlikeRecord record = records.get(0);
            //判断is_deleted的状态
            if("0".equals(record.getIsDeleted())){
                record.setIsDeleted("1");
                userUnlikeRecordMapper.updateById(record);
            }else{
                record.setIsDeleted("0");
                userUnlikeRecordMapper.updateById(record);
            }
        }else{
            // 没有这条记录 添加记录
            userUnlikeRecord.setUserId(userId);
            userUnlikeRecord.setTargetId(targetId);
            userUnlikeRecord.setTargetType(targetType);
            userUnlikeRecord.setStatus("0");
            userUnlikeRecord.setIsDeleted("0");
            userUnlikeRecordMapper.insert(userUnlikeRecord);
        }
        // 处理对应表中的数据  (根据枚举类型)

        UnlikeTypeEnum typeEnum = UnlikeTypeEnum.fromCode(targetType);
        if(typeEnum != null){
            switch (typeEnum){
                case ARTICLE:
                    int unlikeCount = 0;
                    unlikeCount = userUnlikeRecordMapper.selectCount(new QueryWrapper<UserUnlikeRecord>()
                            .eq("user_id",userId)
                            .eq("target_id",targetId)
                            .eq("target_type",targetType)
                            .eq("is_deleted","0"));
                    Article article = articleMapper.selectOne(new QueryWrapper<Article>()
                            .eq("id", targetId));
                    article.setUnlikeCount(unlikeCount);
                    articleMapper.updateById(article);
                    break;
                case VIDEO:
                    System.out.println("处理视频的踩记录");
                    break;
                default:
                    break;
            }
        }

        return R.ok("success");
    }

    @Override
    public R getUserUnlikeRecord(Long userId, Long targetId, String targetType) {
        List<UserUnlikeRecord> records = userUnlikeRecordMapper.selectList(new QueryWrapper<UserUnlikeRecord>()
                .eq("user_id", userId)
                .eq("target_id", targetId)
                .eq("target_type", targetType));
        if(!records.isEmpty()) {
            return R.ok("success",records.get(0));
        }
        return R.error("fail");
    }


}
