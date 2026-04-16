package com.errorquest.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("error_question")
public class ErrorQuestion {
    
    @TableId(type = IdType.AUTO)
    private Long questionId;
    
    private Long userId;
    
    private Long subjectId;
    
    private Long knowledgeId;
    
    private Integer questionType;
    
    private Integer difficulty;
    
    private String errorReason;
    
    private String questionContent;
    
    private String questionImages;
    
    private String options;
    
    private String correctAnswer;
    
    private String answerImages;
    
    private String userAnswer;
    
    private String userAnswerImages;
    
    private String analysis;
    
    private String notes;
    
    private String tags;
    
    private Integer masteryStatus;
    
    @TableLogic
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

