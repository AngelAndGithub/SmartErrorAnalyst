package com.errorquest.controller;

import com.errorquest.dto.Result;
import com.errorquest.dto.StatisticsDTO;
import com.errorquest.service.StatisticsService;
import com.errorquest.utils.JwtUtil;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "http://localhost:5173")
public class StatisticsController {
    
    @Autowired
    private StatisticsService statisticsService;
    
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
    
    @GetMapping("/user")
    public Result<StatisticsDTO> getUserStatistics(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            StatisticsDTO statistics = statisticsService.getUserStatistics(userId);
            return Result.success(statistics);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}

