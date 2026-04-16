package com.errorquest.dto;

import com.errorquest.entity.ErrorQuestion;
import com.errorquest.entity.QuestionShare;
import com.errorquest.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class QuestionShareResultDTO {
    
    private QuestionShare share;
    
    private User sharer;
    
    private ErrorQuestion question;
    
    private Integer solutionCount; // 解题思路数量
    
    private Boolean hasLiked; // 当前用户是否已点赞
    
    private List<QuestionShareSolutionDTO> solutions; // 解题思路列表
}
