package com.errorquest.service;

import com.errorquest.dto.AIAnalysisResultDTO;

public interface AIService {
    
    /**
     * 智能解析错题
     * @param questionContent 题目内容
     * @param correctAnswer 正确答案
     * @param userAnswer 用户答案
     * @param subjectName 学科名称
     * @param errorReason 错误原因
     * @param provider AI提供商 (zhipu/deepseek/mock)
     * @return AI解析结果
     */
    AIAnalysisResultDTO analyzeErrorQuestion(String questionContent, String correctAnswer, 
                                              String userAnswer, String subjectName, String errorReason,
                                              String provider);
    
    /**
     * 使用默认提供商智能解析错题
     */
    AIAnalysisResultDTO analyzeErrorQuestion(String questionContent, String correctAnswer, 
                                              String userAnswer, String subjectName, String errorReason);
    
    /**
     * 获取所有可用的AI提供商列表
     */
    java.util.List<String> getAvailableProviders();
    
    /**
     * 获取已启用（配置了API Key）的AI提供商列表
     */
    java.util.List<String> getEnabledProviders();
}
