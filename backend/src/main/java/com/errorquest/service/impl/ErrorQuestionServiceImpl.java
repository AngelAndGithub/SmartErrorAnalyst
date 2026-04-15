package com.errorquest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.errorquest.dto.ErrorQuestionDTO;
import com.errorquest.dto.ErrorQuestionQueryDTO;
import com.errorquest.entity.ErrorQuestion;
import com.errorquest.entity.ReviewPlan;
import com.errorquest.mapper.ErrorQuestionMapper;
import com.errorquest.mapper.ReviewPlanMapper;
import com.errorquest.service.ErrorQuestionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class ErrorQuestionServiceImpl extends ServiceImpl<ErrorQuestionMapper, ErrorQuestion> 
    implements ErrorQuestionService {
    
    @Autowired
    private ErrorQuestionMapper errorQuestionMapper;
    
    @Autowired
    private ReviewPlanMapper reviewPlanMapper;
    
    // 艾宾浩斯遗忘曲线复习间隔（天�?
    private static final List<Integer> REVIEW_INTERVALS = Arrays.asList(1, 3, 7, 15, 30);
    
    @Override
    @Transactional
    public void addErrorQuestion(Long userId, ErrorQuestionDTO dto) {
        ErrorQuestion question = new ErrorQuestion();
        BeanUtils.copyProperties(dto, question);
        question.setUserId(userId);
        question.setMasteryStatus(0);
        
        errorQuestionMapper.insert(question);
        
        // 创建复习计划
        createReviewPlans(userId, question.getQuestionId());
    }
    
    @Override
    @Transactional
    public void updateErrorQuestion(Long userId, Long questionId, ErrorQuestionDTO dto) {
        ErrorQuestion question = errorQuestionMapper.selectById(questionId);
        if (question == null || !question.getUserId().equals(userId)) {
            throw new RuntimeException("错题不存在或无权限");
        }
        
        BeanUtils.copyProperties(dto, question);
        question.setQuestionId(questionId);
        question.setUserId(userId);
        
        errorQuestionMapper.updateById(question);
    }
    
    @Override
    @Transactional
    public void deleteErrorQuestion(Long userId, Long questionId) {
        ErrorQuestion question = errorQuestionMapper.selectById(questionId);
        if (question == null || !question.getUserId().equals(userId)) {
            throw new RuntimeException("错题不存在或无权限");
        }
        
        errorQuestionMapper.deleteById(questionId);
    }
    
    @Override
    public ErrorQuestion getErrorQuestionDetail(Long userId, Long questionId) {
        ErrorQuestion question = errorQuestionMapper.selectById(questionId);
        if (question == null || !question.getUserId().equals(userId)) {
            throw new RuntimeException("错题不存在或无权限");
        }
        return question;
    }
    
    @Override
    public IPage<ErrorQuestion> listErrorQuestions(Long userId, ErrorQuestionQueryDTO queryDTO) {
        Page<ErrorQuestion> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        LambdaQueryWrapper<ErrorQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ErrorQuestion::getUserId, userId);
        
        if (queryDTO.getSubjectId() != null) {
            wrapper.eq(ErrorQuestion::getSubjectId, queryDTO.getSubjectId());
        }
        if (queryDTO.getKnowledgeId() != null) {
            wrapper.eq(ErrorQuestion::getKnowledgeId, queryDTO.getKnowledgeId());
        }
        if (queryDTO.getQuestionType() != null) {
            wrapper.eq(ErrorQuestion::getQuestionType, queryDTO.getQuestionType());
        }
        if (queryDTO.getDifficulty() != null) {
            wrapper.eq(ErrorQuestion::getDifficulty, queryDTO.getDifficulty());
        }
        if (StringUtils.isNotBlank(queryDTO.getErrorReason())) {
            wrapper.eq(ErrorQuestion::getErrorReason, queryDTO.getErrorReason());
        }
        if (queryDTO.getMasteryStatus() != null) {
            wrapper.eq(ErrorQuestion::getMasteryStatus, queryDTO.getMasteryStatus());
        }
        if (StringUtils.isNotBlank(queryDTO.getKeyword())) {
            wrapper.like(ErrorQuestion::getQuestionContent, queryDTO.getKeyword())
                   .or()
                   .like(ErrorQuestion::getTags, queryDTO.getKeyword());
        }
        
        wrapper.orderByDesc(ErrorQuestion::getCreateTime);
        
        return errorQuestionMapper.selectPage(page, wrapper);
    }
    
    @Override
    public void updateMasteryStatus(Long userId, Long questionId, Integer masteryStatus) {
        ErrorQuestion question = errorQuestionMapper.selectById(questionId);
        if (question == null || !question.getUserId().equals(userId)) {
            throw new RuntimeException("错题不存在或无权限");
        }
        
        question.setMasteryStatus(masteryStatus);
        errorQuestionMapper.updateById(question);
    }
    
    private void createReviewPlans(Long userId, Long questionId) {
        LocalDate now = LocalDate.now();
        
        for (Integer interval : REVIEW_INTERVALS) {
            ReviewPlan plan = new ReviewPlan();
            plan.setQuestionId(questionId);
            plan.setUserId(userId);
            plan.setPlanTime(now.plusDays(interval));
            plan.setReviewResult(0);
            plan.setReviewCount(0);
            reviewPlanMapper.insert(plan);
        }
    }
}

