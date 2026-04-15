package com.errorquest.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("class")
public class Clazz {
    
    @TableId(type = IdType.AUTO)
    private Long classId;
    
    private String className;
    
    private String grade;
    
    private String subject;
    
    private Long teacherId;
    
    private Integer status;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

