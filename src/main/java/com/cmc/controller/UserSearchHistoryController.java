package com.cmc.controller;


import com.cmc.common.R;
import com.cmc.entity.UserSearchHistory;
import com.cmc.service.UserSearchHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author C
 * @since 2026-04-07
 */
@RestController
@RequestMapping("/user-search-history")
public class UserSearchHistoryController {

    @Autowired
    private UserSearchHistoryService userSearchHistoryService;
    /**
     * 插入一条用户搜索的历史记录
     * @param userSearchHistory  历史记录
     * @return R
     */
    @PostMapping("/insertUserSearchHistory")
    public R insertUserSearchHistory(@RequestBody UserSearchHistory userSearchHistory) {
        return userSearchHistoryService.insertUserSearchHistory(userSearchHistory);
    }

    @GetMapping("/getUserSearchHistory/{userId}")
    public R getUserSearchHistory(@PathVariable("userId") String userId){
        return userSearchHistoryService.getUserSearchHistory(Long.valueOf(userId));
    }

    @PostMapping("/deleteUserSearchHistory")
    public R deleteUserSearchHistory(@RequestBody UserSearchHistory userSearchHistory){
        return userSearchHistoryService.deleteUserSearchHistory(userSearchHistory);
    }

    @PostMapping("/deleteAllHistory/{userId}")
    public R deleteAllHistory(@PathVariable("userId") String userId){
        return userSearchHistoryService.deleteAllHistory(Long.valueOf(userId));
    }

}

