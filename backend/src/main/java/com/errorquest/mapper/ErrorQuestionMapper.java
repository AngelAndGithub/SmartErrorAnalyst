package com.errorquest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.errorquest.entity.ErrorQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ErrorQuestionMapper extends BaseMapper<ErrorQuestion> {
    
    @Select("SELECT COUNT(*) FROM error_question WHERE user_id = #{userId} AND deleted = 0")
    Long countByUserId(Long userId);
    
    @Select("SELECT COUNT(*) FROM error_question WHERE user_id = #{userId} AND mastery_status = 1 AND deleted = 0")
    Long countMasteredByUserId(Long userId);
    
    @Select("SELECT error_reason as name, COUNT(*) as value FROM error_question WHERE user_id = #{userId} AND deleted = 0 GROUP BY error_reason")
    List<Map<String, Object>> countByErrorReason(Long userId);
    
    @Select("SELECT subject_id as name, COUNT(*) as value FROM error_question WHERE user_id = #{userId} AND deleted = 0 GROUP BY subject_id")
    List<Map<String, Object>> countBySubject(Long userId);
    
    @Select("SELECT knowledge_id as name, COUNT(*) as value FROM error_question WHERE user_id = #{userId} AND deleted = 0 GROUP BY knowledge_id")
    List<Map<String, Object>> countByKnowledge(Long userId);
}

