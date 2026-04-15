package com.errorquest.dto;

import lombok.Data;

@Data
public class UserInfoDTO {
    
    private Long userId;
    private String username;
    private String name;
    private Integer role;
    private String grade;
    private Long classId;
    private String token;
}

