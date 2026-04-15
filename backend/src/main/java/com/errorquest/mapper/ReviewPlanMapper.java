package com.errorquest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.errorquest.entity.ReviewPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ReviewPlanMapper extends BaseMapper<ReviewPlan> {
    
    @Select("SELECT * FROM review_plan WHERE user_id = #{userId} AND plan_time = #{planTime} AND review_result = 0 AND deleted = 0")
    List<ReviewPlan> selectTodayPlans(Long userId, LocalDate planTime);
    
    @Select("SELECT COUNT(*) FROM review_plan WHERE user_id = #{userId} AND plan_time = #{planTime} AND review_result = 0 AND deleted = 0")
    Long countTodayPlans(Long userId, LocalDate planTime);
}

