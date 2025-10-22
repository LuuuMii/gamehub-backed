package com.cmc.controller;


import com.cmc.common.R;
import com.cmc.entity.UserUnlikeRecord;
import com.cmc.service.UserUnlikeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author C
 * @since 2025-10-20
 */
@RestController
@RequestMapping("/user-unlike-record")
public class UserUnlikeRecordController {

    @Autowired
    private UserUnlikeRecordService userUnlikeRecordService;

    @PostMapping("/syncUnlikeRecord")
    public R syncUnlikeRecord(@RequestBody UserUnlikeRecord userUnlikeRecord,
                              @RequestParam("userId") String userId,
                              @RequestParam("targetId") String targetId,
                              @RequestParam("targetType") String targetType){
        return userUnlikeRecordService.syncUnlikeRecord(userUnlikeRecord,Long.valueOf(userId),Long.valueOf(targetId),targetType);
    }

    @GetMapping("/getUserUnlikeRecord/{userId}/{targetId}/{targetType}")
    public R getUserUnlikeRecord(@PathVariable("userId") String userId,
                               @PathVariable("targetId") String targetId,
                               @PathVariable("targetType")String targetType){
        return userUnlikeRecordService.getUserUnlikeRecord(Long.valueOf(userId),Long.valueOf(targetId),targetType);
    }

}



