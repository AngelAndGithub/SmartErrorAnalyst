package com.errorquest.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("question_share_solution")
public class QuestionShareSolution {
    
    @TableId(type = IdType.AUTO)
    private Long solutionId;
    
    private Long shareId;
    
    private Long userId;
    
    private String solutionContent;
    
    private String solutionImages;
    
    private Integer likeCount;
    
    private Integer isBest;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
