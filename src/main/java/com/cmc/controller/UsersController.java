package com.cmc.controller;


import com.cmc.common.R;
import com.cmc.entity.Users;
import com.cmc.service.UsersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author C
 * @since 2025-09-10
 */
@RestController
@RequestMapping("/users")
@Api(tags = "用户管理接口")
public class UsersController {

    @Value("${aliyun.oss.file.keyid}")
    public String key;
    @Value("${aliyun.oss.file.keysecret}")
    public String keyValue;

    @Autowired
    private UsersService usersService;

    @GetMapping("/test")
    @ApiOperation(value = "测试" ,notes = "测试1123")
    public String test(){
        return key + "+" +keyValue;
    }

    @PostMapping("/login")
    public R loginByAccount(@RequestBody Users user){
        return usersService.loginByAccount(user);
    }

}

