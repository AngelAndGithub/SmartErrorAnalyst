package com.errorquest.service.impl;

import com.errorquest.dto.StatisticsDTO;
import com.errorquest.mapper.ErrorQuestionMapper;
import com.errorquest.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    
    @Autowired
    private ErrorQuestionMapper errorQuestionMapper;
    
    @Override
    public StatisticsDTO getUserStatistics(Long userId) {
        StatisticsDTO statistics = new StatisticsDTO();
        
        // 基础统计
        Long totalCount = errorQuestionMapper.countByUserId(userId);
        Long masteredCount = errorQuestionMapper.countMasteredByUserId(userId);
        Long unmasteredCount = totalCount - masteredCount;
        
        statistics.setTotalCount(totalCount);
        statistics.setMasteredCount(masteredCount);
        statistics.setUnmasteredCount(unmasteredCount);
        
        // 分布统计
        statistics.setSubjectDistribution(errorQuestionMapper.countBySubject(userId));
        statistics.setKnowledgeDistribution(errorQuestionMapper.countByKnowledge(userId));
        statistics.setErrorReasonDistribution(errorQuestionMapper.countByErrorReason(userId));
        
        // 薄弱点分�?
        List<String> weakPoints = analyzeWeakPoints(statistics);
        statistics.setWeakPoints(weakPoints);
        
        return statistics;
    }
    
    private List<String> analyzeWeakPoints(StatisticsDTO statistics) {
        List<String> weakPoints = new ArrayList<>();
        
        // 分析错误原因最多的类型
        if (statistics.getErrorReasonDistribution() != null && !statistics.getErrorReasonDistribution().isEmpty()) {
            Map<String, Object> topErrorReason = statistics.getErrorReasonDistribution().get(0);
            String reasonName = (String) topErrorReason.get("name");
            Long count = ((Number) topErrorReason.get("value")).longValue();
            if (count > 0) {
                weakPoints.add(reasonName + "(" + count + "道)");
            }
        }
        
        // 分析错误最多的知识点
        if (statistics.getKnowledgeDistribution() != null && !statistics.getKnowledgeDistribution().isEmpty()) {
            Map<String, Object> topKnowledge = statistics.getKnowledgeDistribution().get(0);
            Long knowledgeId = ((Number) topKnowledge.get("name")).longValue();
            Long count = ((Number) topKnowledge.get("value")).longValue();
            if (count > 0) {
                weakPoints.add("知识点ID:" + knowledgeId + "(" + count + "道)");
            }
        }
        
        return weakPoints;
    }
}

