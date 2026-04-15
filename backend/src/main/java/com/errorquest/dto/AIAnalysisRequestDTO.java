package com.errorquest.dto;

import lombok.Data;

@Data
public class AIAnalysisRequestDTO {
    
    private String questionContent;
    private String correctAnswer;
    private String userAnswer;
    private Long subjectId;
    private String errorReason;
    private String provider; // AI提供商: zhipu/deepseek/mock
}
