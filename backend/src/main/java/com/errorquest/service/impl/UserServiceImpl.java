package com.errorquest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.errorquest.dto.LoginDTO;
import com.errorquest.dto.RegisterDTO;
import com.errorquest.dto.UserInfoDTO;
import com.errorquest.entity.User;
import com.errorquest.mapper.UserMapper;
import com.errorquest.service.UserService;
import com.errorquest.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public UserInfoDTO login(LoginDTO loginDTO) {
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole());
        
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserId(user.getUserId());
        userInfoDTO.setUsername(user.getUsername());
        userInfoDTO.setName(user.getName());
        userInfoDTO.setRole(user.getRole());
        userInfoDTO.setGrade(user.getGrade());
        userInfoDTO.setClassId(user.getClassId());
        userInfoDTO.setToken(token);
        
        return userInfoDTO;
    }
    
    @Override
    public UserInfoDTO register(RegisterDTO registerDTO) {
        // 检查用户名是否已存�?
        User existUser = userMapper.selectByUsername(registerDTO.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 创建新用�?
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setName(registerDTO.getName());
        user.setRole(registerDTO.getRole());
        user.setGrade(registerDTO.getGrade());
        user.setStatus(1);
        
        userMapper.insert(user);
        
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole());
        
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserId(user.getUserId());
        userInfoDTO.setUsername(user.getUsername());
        userInfoDTO.setName(user.getName());
        userInfoDTO.setRole(user.getRole());
        userInfoDTO.setGrade(user.getGrade());
        userInfoDTO.setToken(token);
        
        return userInfoDTO;
    }
    
    @Override
    public User getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }
    
    @Override
    public UserInfoDTO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserId(user.getUserId());
        userInfoDTO.setUsername(user.getUsername());
        userInfoDTO.setName(user.getName());
        userInfoDTO.setRole(user.getRole());
        userInfoDTO.setGrade(user.getGrade());
        userInfoDTO.setClassId(user.getClassId());
        
        return userInfoDTO;
    }
}

