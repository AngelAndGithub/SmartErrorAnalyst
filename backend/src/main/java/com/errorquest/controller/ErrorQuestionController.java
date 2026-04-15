package com.errorquest.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.errorquest.dto.ErrorQuestionDTO;
import com.errorquest.dto.ErrorQuestionQueryDTO;
import com.errorquest.dto.Result;
import com.errorquest.entity.ErrorQuestion;
import com.errorquest.service.ErrorQuestionService;
import com.errorquest.utils.JwtUtil;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/error-questions")
@CrossOrigin(origins = "http://localhost:5173")
public class ErrorQuestionController {
    
    @Autowired
    private ErrorQuestionService errorQuestionService;
    
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
    
    @PostMapping
    public Result<Void> add(@RequestBody ErrorQuestionDTO dto, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            errorQuestionService.addErrorQuestion(userId, dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ErrorQuestionDTO dto, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            errorQuestionService.updateErrorQuestion(userId, id, dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            errorQuestionService.deleteErrorQuestion(userId, id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public Result<ErrorQuestion> getById(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            ErrorQuestion question = errorQuestionService.getErrorQuestionDetail(userId, id);
            return Result.success(question);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping
    public Result<IPage<ErrorQuestion>> list(ErrorQuestionQueryDTO queryDTO, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            IPage<ErrorQuestion> page = errorQuestionService.listErrorQuestions(userId, queryDTO);
            return Result.success(page);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/mastery")
    public Result<Void> updateMastery(@PathVariable Long id, @RequestParam Integer status, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            errorQuestionService.updateMasteryStatus(userId, id, status);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
