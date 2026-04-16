package com.errorquest.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionShareDTO {
    
    private Long questionId;
    
    private String shareTitle;
    
    private String shareMessage;
    
    private Integer shareType; // 1-公开 2-指定用户
    
    private List<Long> targetUserIds; // 指定分享的用户ID列表
    
    private Integer includeAnswer;
    
    private Integer includeAnalysis;
    
    private Integer includeNotes;
}
