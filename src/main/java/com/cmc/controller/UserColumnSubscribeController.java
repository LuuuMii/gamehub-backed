package com.cmc.controller;


import com.cmc.common.R;
import com.cmc.entity.UserColumnSubscribe;
import com.cmc.service.UserColumnSubscribeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户订阅专栏关联表 前端控制器
 * </p>
 *
 * @author C
 * @since 2025-10-16
 */
@RestController
@RequestMapping("/user-column-subscribe")
public class UserColumnSubscribeController {
    @Autowired
    private UserColumnSubscribeService userColumnSubscribeService;

    @GetMapping("/getSubscribeDetail/{userId}/{columnId}")
    public R getSubscribeDetail(@PathVariable("userId") String userId,@PathVariable("columnId") String columnId){
        return userColumnSubscribeService.getSubscribeDetail(userId,columnId);
    }

    @PostMapping("/subscribeColumn")
    public R subscribeColumn(@RequestBody UserColumnSubscribe userColumnSubscribe){
        return userColumnSubscribeService.subscribeColumn(userColumnSubscribe);
    }

    @PostMapping("/unsubscribeColumn")
    public R unsubscribeColumn(@RequestBody UserColumnSubscribe userColumnSubscribe){
        return userColumnSubscribeService.unsubscribeColumn(userColumnSubscribe);
    }



}

