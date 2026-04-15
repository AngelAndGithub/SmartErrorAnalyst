package com.errorquest.controller;

import com.errorquest.dto.Result;
import com.errorquest.entity.Clazz;
import com.errorquest.service.ClassService;
import com.errorquest.utils.JwtUtil;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@CrossOrigin(origins = "http://localhost:5173")
public class ClassController {
    
    @Autowired
    private ClassService classService;
    
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
    
    @GetMapping("/my")
    public Result<List<Clazz>> getMyClasses(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            List<Clazz> classes = classService.getTeacherClasses(userId);
            return Result.success(classes);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping
    public Result<Clazz> createClass(@RequestParam String className,
                                      @RequestParam String grade,
                                      @RequestParam String subject,
                                      HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            Clazz clazz = classService.createClass(userId, className, grade, subject);
            return Result.success(clazz);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}

