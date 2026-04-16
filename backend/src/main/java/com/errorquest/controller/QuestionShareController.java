package com.errorquest.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.errorquest.dto.*;
import com.errorquest.entity.User;
import com.errorquest.service.QuestionShareService;
import com.errorquest.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shares")
@CrossOrigin(origins = "http://localhost:5173")
public class QuestionShareController {
    
    @Autowired
    private QuestionShareService shareService;
    
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
    
    /**
     * 创建分享
     */
    @PostMapping
    public Result<String> createShare(@RequestBody QuestionShareDTO dto, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            String shareNo = shareService.createShare(userId, dto);
            return Result.success(shareNo);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("创建分享失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取我分享的列表
     */
    @GetMapping("/my")
    public Result<IPage<QuestionShareResultDTO>> getMyShares(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            IPage<QuestionShareResultDTO> page = shareService.getMyShares(userId, pageNum, pageSize);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error("获取分享列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取分享给我的列表
     */
    @GetMapping("/received")
    public Result<IPage<QuestionShareResultDTO>> getReceivedShares(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            IPage<QuestionShareResultDTO> page = shareService.getSharesForMe(userId, pageNum, pageSize);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error("获取分享列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取分享详情
     */
    @GetMapping("/{shareId}")
    public Result<QuestionShareResultDTO> getShareDetail(
            @PathVariable Long shareId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            QuestionShareResultDTO detail = shareService.getShareDetail(shareId, userId);
            return Result.success(detail);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("获取分享详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加解题思路
     */
    @PostMapping("/{shareId}/solution")
    public Result<Long> addSolution(
            @PathVariable Long shareId,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            String content = body.get("content");
            String images = body.get("images");
            
            if (content == null || content.trim().isEmpty()) {
                return Result.error("解题思路不能为空");
            }
            
            Long solutionId = shareService.addSolution(shareId, userId, content, images);
            return Result.success(solutionId);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("添加解题思路失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取解题思路列表
     */
    @GetMapping("/{shareId}/solutions")
    public Result<List<QuestionShareSolutionDTO>> getSolutions(@PathVariable Long shareId) {
        try {
            List<QuestionShareSolutionDTO> solutions = shareService.getSolutions(shareId);
            return Result.success(solutions);
        } catch (Exception e) {
            return Result.error("获取解题思路失败: " + e.getMessage());
        }
    }
    
    /**
     * 点赞分享
     */
    @PostMapping("/{shareId}/like")
    public Result<Void> likeShare(
            @PathVariable Long shareId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            shareService.likeShare(shareId, userId);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("点赞失败: " + e.getMessage());
        }
    }
    
    /**
     * 撤回分享
     */
    @DeleteMapping("/{shareId}")
    public Result<Void> revokeShare(
            @PathVariable Long shareId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            shareService.revokeShare(shareId, userId);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("撤回分享失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户列表（用于搜索分享对象）
     */
    @GetMapping("/users")
    public Result<List<User>> getUserList(
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            List<User> users = shareService.getUserList(keyword, userId);
            return Result.success(users);
        } catch (Exception e) {
            return Result.error("获取用户列表失败: " + e.getMessage());
        }
    }
}
