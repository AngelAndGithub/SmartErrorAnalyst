package com.errorquest.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.errorquest.dto.QuestionShareDTO;
import com.errorquest.dto.QuestionShareResultDTO;
import com.errorquest.dto.QuestionShareSolutionDTO;

import java.util.List;

public interface QuestionShareService {
    
    /**
     * 创建分享
     */
    String createShare(Long userId, QuestionShareDTO dto);
    
    /**
     * 获取我分享的列表
     */
    IPage<QuestionShareResultDTO> getMyShares(Long userId, int pageNum, int pageSize);
    
    /**
     * 获取分享给我的列表
     */
    IPage<QuestionShareResultDTO> getSharesForMe(Long userId, int pageNum, int pageSize);
    
    /**
     * 获取分享详情
     */
    QuestionShareResultDTO getShareDetail(Long shareId, Long userId);
    
    /**
     * 添加解题思路
     */
    Long addSolution(Long shareId, Long userId, String content, String images);
    
    /**
     * 获取解题思路列表
     */
    List<QuestionShareSolutionDTO> getSolutions(Long shareId);
    
    /**
     * 点赞分享
     */
    void likeShare(Long shareId, Long userId);
    
    /**
     * 撤回分享
     */
    void revokeShare(Long shareId, Long userId);
    
    /**
     * 获取用户列表（用于搜索）
     */
    List<com.errorquest.entity.User> getUserList(String keyword, Long excludeUserId);
}
