package com.errorquest.dto;

import lombok.Data;

@Data
public class ErrorQuestionQueryDTO {
    
    private Long subjectId;
    private Long knowledgeId;
    private Integer questionType;
    private Integer difficulty;
    private String errorReason;
    private String keyword;
    private Integer masteryStatus;
    
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}

