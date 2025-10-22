package com.cmc.controller;


import com.cmc.common.R;
import com.cmc.entity.UserCollectionFolder;
import com.cmc.service.UserCollectionFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author C
 * @since 2025-10-17
 */
@RestController
@RequestMapping("/user-collection-folder")
public class UserCollectionFolderController {

    @Autowired
    private UserCollectionFolderService userCollectionFolderService;

    @GetMapping("/getUserCollectionFoldersByUserId/{userId}")
    public R getUserCollectionFoldersByUserId(@PathVariable String userId){
        return userCollectionFolderService.getUserCollectionFoldersByUserId(Long.valueOf(userId));
    }

    @GetMapping("/getUserCollectionFoldersByUserIdForTarget/{userId}")
    public R getUserCollectionFoldersByUserIdForTarget(
            @PathVariable String userId,
            @RequestParam("targetId") String targetId,
            @RequestParam("targetType") String targetType){
        return userCollectionFolderService.getUserCollectionFoldersByUserIdForTarget(Long.valueOf(userId),Long.valueOf(targetId),targetType);
    }

        @PostMapping("/addUserCollectionFolder")
    public R addUserCollectionFolder(@RequestBody UserCollectionFolder userCollectionFolder){
        return userCollectionFolderService.addUserCollectionFolder(userCollectionFolder);
    }

}

