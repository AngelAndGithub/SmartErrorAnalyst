package com.errorquest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.errorquest.dto.LoginDTO;
import com.errorquest.dto.RegisterDTO;
import com.errorquest.dto.UserInfoDTO;
import com.errorquest.entity.User;

public interface UserService extends IService<User> {
    
    UserInfoDTO login(LoginDTO loginDTO);
    
    UserInfoDTO register(RegisterDTO registerDTO);
    
    User getByUsername(String username);
    
    UserInfoDTO getUserInfo(Long userId);
}

