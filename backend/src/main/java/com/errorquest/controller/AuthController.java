package com.errorquest.controller;

import com.errorquest.dto.LoginDTO;
import com.errorquest.dto.RegisterDTO;
import com.errorquest.dto.Result;
import com.errorquest.dto.UserInfoDTO;
import com.errorquest.service.UserService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public Result<UserInfoDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            UserInfoDTO userInfo = userService.login(loginDTO);
            return Result.success(userInfo);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/register")
    public Result<UserInfoDTO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        try {
            UserInfoDTO userInfo = userService.register(registerDTO);
            return Result.success(userInfo);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}

