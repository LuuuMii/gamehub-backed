package com.cmc.utils;

import com.cmc.dto.UserDTO;

public class UserContext {

    private static final ThreadLocal<UserDTO> USER_THREAD_LOCAL = new ThreadLocal<>();

    public static void setUser(UserDTO user) {
        USER_THREAD_LOCAL.set(user);
    }

    public static UserDTO getUser(){
        return USER_THREAD_LOCAL.get();
    }

    public static void remove(){
        USER_THREAD_LOCAL.remove();
    }

}
