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

    @Autowired
    private UsersService usersService;

    @PostMapping("/loginByUsername")
    public R loginByUsername(@RequestBody Users user){
        return usersService.loginByUsername(user);
    }

    @PostMapping("/register")
    public R register(@RequestBody Users users){
        return usersService.register(users);
    }

    @GetMapping("/getUserInfoById/{id}")
    public R getUserInfoById(@PathVariable String id){
        return usersService.getUserInfoById(Long.valueOf(id));
    }

    @GetMapping("/getUserInfoByUsername/{username}")
    public R getUserInfoByUsername(@PathVariable String username){
        return usersService.getUserInfoByUsername(username);
    }

    @GetMapping("/getUserInfoByToken")
    public R getUserInfoByToken(@RequestParam("token") String token){
        return usersService.getUserInfoByToken(token);
    }

    @GetMapping("/getAuthorDataForArticlePage/{username}")
    public R getAuthorDataForArticlePage(@PathVariable String username){
        return usersService.getAuthorDataForArticlePage(username);
    }

}

