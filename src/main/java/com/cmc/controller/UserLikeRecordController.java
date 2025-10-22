package com.cmc.controller;


import com.cmc.common.R;
import com.cmc.entity.UserLikeRecord;
import com.cmc.service.UserLikeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author C
 * @since 2025-10-18
 */
@RestController
@RequestMapping("/user-like-record")
public class UserLikeRecordController {

    @Autowired
    private UserLikeRecordService userLikeRecordService;

    @PostMapping("/syncLikeRecord")
    public R syncLikeRecord(@RequestBody UserLikeRecord userLikeRecord,
                            @RequestParam("userId") String userId,
                            @RequestParam("targetId") String targetId,
                            @RequestParam("targetType") String targetType) {
        return userLikeRecordService.syncLikeRecord(userLikeRecord,Long.valueOf(userId),Long.valueOf(targetId),targetType);
    }

    @GetMapping("/getUserLikeRecord/{userId}/{targetId}/{targetType}")
    public R getUserLikeRecord(@PathVariable("userId") String userId,
                               @PathVariable("targetId") String targetId,
                               @PathVariable("targetType")String targetType){
        return userLikeRecordService.getUserLikeRecord(Long.valueOf(userId),Long.valueOf(targetId),targetType);
    }

}

