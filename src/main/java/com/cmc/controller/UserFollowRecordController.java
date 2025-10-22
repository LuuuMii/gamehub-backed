package com.cmc.controller;


import com.cmc.common.R;
import com.cmc.entity.UserFollowRecord;
import com.cmc.service.UserFollowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author C
 * @since 2025-10-21
 */
@RestController
@RequestMapping("/user-follow-record")
public class UserFollowRecordController {

    @Autowired
    private UserFollowRecordService userFollowRecordService;

    @PostMapping("/syncUserFollowRecord")
    public R syncUserFollowRecord(@RequestBody UserFollowRecord userFollowRecord) {
        return userFollowRecordService.syncUserFollowRecord(userFollowRecord);
    }

    @GetMapping("/getUserFollowRecord/{followerId}/{followeeId}")
    public R getUserFollowRecord(@PathVariable("followerId") String followerId,
                                 @PathVariable("followeeId") String followeeId) {
        return userFollowRecordService.getUserFollowRecord(Long.valueOf(followerId),Long.valueOf(followeeId));
    }

}

