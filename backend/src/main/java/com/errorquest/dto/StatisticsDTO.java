package com.errorquest.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class StatisticsDTO {
    
    private Long totalCount;
    private Long masteredCount;
    private Long unmasteredCount;
    
    private List<Map<String, Object>> subjectDistribution;
    private List<Map<String, Object>> knowledgeDistribution;
    private List<Map<String, Object>> errorReasonDistribution;
    private List<Map<String, Object>> difficultyDistribution;
    
    private List<String> weakPoints;
}

