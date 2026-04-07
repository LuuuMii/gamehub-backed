package com.cmc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmc.common.R;
import com.cmc.entity.UserSearchHistory;
import com.cmc.mapper.UserSearchHistoryMapper;
import com.cmc.service.UserSearchHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author C
 * @since 2026-04-07
 */
@Slf4j
@Service
public class UserSearchHistoryServiceImpl extends ServiceImpl<UserSearchHistoryMapper, UserSearchHistory> implements UserSearchHistoryService {
    @Autowired
    private UserSearchHistoryMapper userSearchHistoryMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final int MAX_HISTORY = 30;

    /**
     * 插入一条用户搜索的历史记录
     * @param userSearchHistory  历史记录
     * @return R
     */
    @Override
    public R insertUserSearchHistory(UserSearchHistory userSearchHistory) {
        String redisKey = "search:history:user:" + userSearchHistory.getUserId();
        // 1. 写入mysql数据
        userSearchHistoryMapper.insertUserSearchHistoryOrUpdate(userSearchHistory);

        // 2. 写Redis  List结构
        stringRedisTemplate.opsForList().remove(redisKey,0,userSearchHistory.getKeyword());
        stringRedisTemplate.opsForList().leftPush(redisKey,userSearchHistory.getKeyword());
        stringRedisTemplate.opsForList().trim(redisKey,0,MAX_HISTORY - 1);


        return R.ok();
    }

    /**
     * 根据用户ID 获取  用户的历史查询数据
     * @param userId 用户ID
     * @return List
     */
    @Override
    public R getUserSearchHistory(Long userId) {
        String redisKey = "search:history:user:" + userId;
        List<String> history = stringRedisTemplate.opsForList().range(redisKey, 0, MAX_HISTORY - 1);
        if (history != null && !history.isEmpty()) {
            return R.ok(history);
        }

        // 缓存未命中 查询MySQL
        Page<UserSearchHistory> page = new Page<>(1, MAX_HISTORY);
        LambdaQueryWrapper<UserSearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserSearchHistory::getUserId, userId);
        wrapper.orderByDesc(UserSearchHistory::getLastSearchTime);
        page(page,wrapper);
        List<UserSearchHistory> records = page.getRecords();
        if (records != null && !records.isEmpty()) {
            // 填写Redis
            for (UserSearchHistory record : records) {
                stringRedisTemplate.opsForList().remove(redisKey,0,record.getKeyword());// 去重
                stringRedisTemplate.opsForList().rightPush(redisKey,record.getKeyword()); // 放到队尾保持顺序
            }
            stringRedisTemplate.expire(redisKey, Duration.ofDays(7));
            return R.ok(records.stream().map(UserSearchHistory::getKeyword).collect(Collectors.toList()));
        }


        return R.ok();
    }

    /**
     * 删除一条记录
     * @param userSearchHistory 记录
     * @return 删除记录
     */
    @Override
    public R deleteUserSearchHistory(UserSearchHistory userSearchHistory) {
        String redisKey = "search:history:user:" + userSearchHistory.getUserId();

        try{
            // 第一次删除缓存
            stringRedisTemplate.opsForList().remove(redisKey,0,userSearchHistory.getKeyword());
        }catch (Exception e){}
        // 删除数据库
        try{
            LambdaQueryWrapper<UserSearchHistory> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserSearchHistory::getKeyword,userSearchHistory.getKeyword());
            wrapper.eq(UserSearchHistory::getUserId,userSearchHistory.getUserId());
            userSearchHistoryMapper.delete(wrapper);
        }catch (Exception e){
            System.err.println("MySQL删除失败: " + e.getMessage());
            return R.error();
        }
        //延迟删除
        delayedDeleteListMember(redisKey,userSearchHistory.getKeyword());

        return R.ok();
    }


    @Override
    @Transactional
    public R deleteAllHistory(Long userId) {
        String redisKey = "search:history:user:" + userId;
        // 1. 删除数据库
        LambdaQueryWrapper<UserSearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserSearchHistory::getUserId,userId);
        userSearchHistoryMapper.delete(wrapper);

        // 2.删除缓存
        try{
            stringRedisTemplate.delete(redisKey);
        }catch (Exception e){
            log.error(e.getMessage());
        }

        delayedDeleteKey(redisKey);

        return R.ok();
    }

    @Async("asyncExecutor")
    public void delayedDeleteKey(String redisKey){
        int retry = 3;
        while (retry-- > 0) {
            try{
                Thread.sleep(100);
                stringRedisTemplate.delete(redisKey);
                break; // 删除成功，退出
            }catch (Exception e){
                log.error("延迟删除Redis失败: {}, 剩余重试: {}", e.getMessage(), retry);
            }
        }
    }

    @Async("asyncExecutor")
    public void delayedDeleteListMember(String redisKey,String member){
        try{
            Thread.sleep(100);
            stringRedisTemplate.opsForList().remove(redisKey,0,member);
        }catch (Exception e){
            log.error("延迟删除Redis失败: {}",  e.getMessage());
        }
    }
}
