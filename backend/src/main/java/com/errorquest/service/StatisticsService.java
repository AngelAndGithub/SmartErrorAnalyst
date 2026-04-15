package com.errorquest.service;

import com.errorquest.dto.StatisticsDTO;

public interface StatisticsService {
    
    StatisticsDTO getUserStatistics(Long userId);
}

