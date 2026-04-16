package com.errorquest.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("question_share")
public class QuestionShare {
    
    @TableId(type = IdType.AUTO)
    private Long shareId;
    
    private String shareNo;
    
    private Long questionId;
    
    private Long sharerId;
    
    private Integer shareType;
    
    private String shareTitle;
    
    private String shareMessage;
    
    private Integer includeAnswer;
    
    private Integer includeAnalysis;
    
    private Integer includeNotes;
    
    private Integer viewCount;
    
    private Integer likeCount;
    
    private Integer status;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
