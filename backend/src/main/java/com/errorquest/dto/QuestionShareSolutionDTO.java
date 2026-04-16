package com.errorquest.dto;

import com.errorquest.entity.QuestionShareSolution;
import com.errorquest.entity.User;
import lombok.Data;

@Data
public class QuestionShareSolutionDTO {
    
    private QuestionShareSolution solution;
    
    private User user;
}
