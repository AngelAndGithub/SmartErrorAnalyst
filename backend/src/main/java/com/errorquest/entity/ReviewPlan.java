package com.errorquest.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("review_plan")
public class ReviewPlan {
    
    @TableId(type = IdType.AUTO)
    private Long planId;
    
    private Long questionId;
    
    private Long userId;
    
    private LocalDate planTime;
    
    private LocalDateTime actualTime;
    
    private Integer reviewResult;
    
    private Integer reviewCount;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

