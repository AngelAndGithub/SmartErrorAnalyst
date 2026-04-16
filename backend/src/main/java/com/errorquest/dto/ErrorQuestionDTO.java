package com.errorquest.dto;

import lombok.Data;

@Data
public class ErrorQuestionDTO {
    
    private Long questionId;
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
}

