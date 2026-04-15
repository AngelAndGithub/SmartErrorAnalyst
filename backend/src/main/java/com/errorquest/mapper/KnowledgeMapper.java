package com.errorquest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.errorquest.entity.Knowledge;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface KnowledgeMapper extends BaseMapper<Knowledge> {
    
    @Select("SELECT * FROM knowledge WHERE subject_id = #{subjectId} AND deleted = 0 ORDER BY level, knowledge_id")
    List<Knowledge> selectBySubjectId(Long subjectId);
}

