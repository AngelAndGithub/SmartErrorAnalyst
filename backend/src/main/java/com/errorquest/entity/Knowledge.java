package com.errorquest.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("knowledge")
public class Knowledge {
    
    @TableId(type = IdType.AUTO)
    private Long knowledgeId;
    
    private String knowledgeName;
    
    private Long subjectId;
    
    private Long parentId;
    
    private Integer level;
    
    private Integer status;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

