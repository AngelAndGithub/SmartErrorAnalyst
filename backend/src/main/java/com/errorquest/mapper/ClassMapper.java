package com.errorquest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.errorquest.entity.Clazz;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClassMapper extends BaseMapper<Clazz> {
    
    @Select("SELECT * FROM class WHERE teacher_id = #{teacherId} AND deleted = 0")
    List<Clazz> selectByTeacherId(Long teacherId);
}

