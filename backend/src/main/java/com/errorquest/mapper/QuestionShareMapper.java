package com.errorquest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.errorquest.entity.QuestionShare;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface QuestionShareMapper extends BaseMapper<QuestionShare> {
    
    @Update("UPDATE question_share SET like_count = like_count + 1 WHERE share_id = #{shareId}")
    int incrementLikeCount(Long shareId);
}
