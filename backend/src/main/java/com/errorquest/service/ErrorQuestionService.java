package com.errorquest.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.errorquest.dto.ErrorQuestionDTO;
import com.errorquest.dto.ErrorQuestionQueryDTO;
import com.errorquest.entity.ErrorQuestion;

public interface ErrorQuestionService extends IService<ErrorQuestion> {
    
    void addErrorQuestion(Long userId, ErrorQuestionDTO dto);
    
    void updateErrorQuestion(Long userId, Long questionId, ErrorQuestionDTO dto);
    
    void deleteErrorQuestion(Long userId, Long questionId);
    
    ErrorQuestion getErrorQuestionDetail(Long userId, Long questionId);
    
    IPage<ErrorQuestion> listErrorQuestions(Long userId, ErrorQuestionQueryDTO queryDTO);
    
    void updateMasteryStatus(Long userId, Long questionId, Integer masteryStatus);
}

