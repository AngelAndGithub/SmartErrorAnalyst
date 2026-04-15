package com.errorquest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.errorquest.dto.ReviewResultDTO;
import com.errorquest.entity.ErrorQuestion;
import com.errorquest.entity.ReviewPlan;

import java.util.List;

public interface ReviewPlanService extends IService<ReviewPlan> {
    
    List<ReviewPlan> getTodayPlans(Long userId);
    
    Long countTodayPlans(Long userId);
    
    ErrorQuestion getReviewQuestion(Long userId, Long planId);
    
    void submitReviewResult(Long userId, ReviewResultDTO dto);
    
    List<ReviewPlan> getReviewHistory(Long userId, Long questionId);
}

