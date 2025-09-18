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
        return usersService.getUserInfoById(Integer.valueOf(id));
    }

    @GetMapping("/getUserInfoByToken")
    public R getUserInfoByToken(@RequestParam("token") String token){
        return usersService.getUserInfoByToken(token);
    }

}

