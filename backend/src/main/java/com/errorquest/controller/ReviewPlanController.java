package com.errorquest.controller;

import com.errorquest.dto.Result;
import com.errorquest.dto.ReviewResultDTO;
import com.errorquest.entity.ErrorQuestion;
import com.errorquest.entity.ReviewPlan;
import com.errorquest.service.ReviewPlanService;
import com.errorquest.utils.JwtUtil;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review-plans")
@CrossOrigin(origins = "http://localhost:5173")
public class ReviewPlanController {
    
    @Autowired
    private ReviewPlanService reviewPlanService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        throw new RuntimeException("未登录");
    }
    
    @GetMapping("/today")
    public Result<List<ReviewPlan>> getTodayPlans(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            List<ReviewPlan> plans = reviewPlanService.getTodayPlans(userId);
            return Result.success(plans);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/today/count")
    public Result<Long> countTodayPlans(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            Long count = reviewPlanService.countTodayPlans(userId);
            return Result.success(count);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/{planId}/question")
    public Result<ErrorQuestion> getReviewQuestion(@PathVariable Long planId, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            ErrorQuestion question = reviewPlanService.getReviewQuestion(userId, planId);
            return Result.success(question);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/result")
    public Result<Void> submitResult(@RequestBody ReviewResultDTO dto, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            reviewPlanService.submitReviewResult(userId, dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/history/{questionId}")
    public Result<List<ReviewPlan>> getHistory(@PathVariable Long questionId, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            List<ReviewPlan> history = reviewPlanService.getReviewHistory(userId, questionId);
            return Result.success(history);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}

