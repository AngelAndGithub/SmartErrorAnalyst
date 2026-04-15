package com.errorquest.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterDTO {
    
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    private Integer role = 0;
    
    private String grade;
}

