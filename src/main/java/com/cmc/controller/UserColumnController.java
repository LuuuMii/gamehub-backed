package com.cmc.controller;


import com.cmc.common.R;
import com.cmc.entity.UserColumn;
import com.cmc.service.UserColumnService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author C
 * @since 2025-10-06
 */
@RestController
@RequestMapping("/user-column")
@Api(tags = "用户专栏管理")
public class UserColumnController {

    @Autowired
    private UserColumnService userColumnService;

    @GetMapping("/getAllUserColumnsByUsername/{username}")
    public R getAllUserColumnsByUsername(@PathVariable String username) {
        return userColumnService.getAllUserColumnsByUsername(username);
    }

    @PostMapping("addUserColumn")
    public R addUserColumn(@RequestBody UserColumn userColumn){
        return userColumnService.addUserColumn(userColumn);
    }

    @GetMapping("/getColumnByArticleId/{articleId}")
    public R getColumnByArticleId(@PathVariable String articleId){
        return userColumnService.getColumnByArticleId(Long.valueOf(articleId));
    }

}

