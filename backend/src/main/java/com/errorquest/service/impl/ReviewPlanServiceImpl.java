package com.errorquest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.errorquest.dto.ReviewResultDTO;
import com.errorquest.entity.ErrorQuestion;
import com.errorquest.entity.ReviewPlan;
import com.errorquest.mapper.ErrorQuestionMapper;
import com.errorquest.mapper.ReviewPlanMapper;
import com.errorquest.service.ReviewPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewPlanServiceImpl extends ServiceImpl<ReviewPlanMapper, ReviewPlan> 
    implements ReviewPlanService {
    
    @Autowired
    private ReviewPlanMapper reviewPlanMapper;
    
    @Autowired
    private ErrorQuestionMapper errorQuestionMapper;
    
    @Override
    public List<ReviewPlan> getTodayPlans(Long userId) {
        return reviewPlanMapper.selectTodayPlans(userId, LocalDate.now());
    }
    
    @Override
    public Long countTodayPlans(Long userId) {
        return reviewPlanMapper.countTodayPlans(userId, LocalDate.now());
    }
    
    @Override
    public ErrorQuestion getReviewQuestion(Long userId, Long planId) {
        ReviewPlan plan = reviewPlanMapper.selectById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new RuntimeException("复习计划不存在或无权限");
        }
        
        ErrorQuestion question = errorQuestionMapper.selectById(plan.getQuestionId());
        if (question == null) {
            throw new RuntimeException("错题不存在");
        }
        
        return question;
    }
    
    @Override
    @Transactional
    public void submitReviewResult(Long userId, ReviewResultDTO dto) {
        ReviewPlan plan = reviewPlanMapper.selectById(dto.getPlanId());
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new RuntimeException("复习计划不存在或无权限");
        }
        
        plan.setActualTime(LocalDateTime.now());
        plan.setReviewResult(dto.getReviewResult());
        plan.setReviewCount(plan.getReviewCount() + 1);
        reviewPlanMapper.updateById(plan);
        
        // 如果复习正确，更新错题掌握状态
        if (dto.getReviewResult() == 1) {
            ErrorQuestion question = errorQuestionMapper.selectById(plan.getQuestionId());
            if (question != null) {
                question.setMasteryStatus(1);
                errorQuestionMapper.updateById(question);
            }
        }
    }
    
    @Override
    public List<ReviewPlan> getReviewHistory(Long userId, Long questionId) {
        return reviewPlanMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ReviewPlan>()
                .eq(ReviewPlan::getUserId, userId)
                .eq(ReviewPlan::getQuestionId, questionId)
                .orderByDesc(ReviewPlan::getCreateTime)
        );
    }
}

