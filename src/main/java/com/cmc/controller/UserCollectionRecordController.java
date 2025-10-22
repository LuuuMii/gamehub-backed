package com.cmc.controller;


import com.cmc.common.R;
import com.cmc.entity.UserCollectionRecord;
import com.cmc.service.UserCollectionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author C
 * @since 2025-10-17
 */
@RestController
@RequestMapping("/user-collection-record")
public class UserCollectionRecordController {

    @Autowired
    private UserCollectionRecordService userCollectionRecordService;

    @PostMapping("/syncCollectionRecords")
    public R syncCollectionRecords(@RequestBody List<UserCollectionRecord> records,
                                   @RequestParam("userId") String userId,
                                   @RequestParam("targetId") String targetId,
                                   @RequestParam("targetType") String targetType) {
        return userCollectionRecordService.syncCollectionRecords(Long.valueOf(userId),Long.valueOf(targetId),targetType,records);
    }

}

