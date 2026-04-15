package com.errorquest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.errorquest.entity.Clazz;

import java.util.List;

public interface ClassService extends IService<Clazz> {
    
    List<Clazz> getTeacherClasses(Long teacherId);
    
    Clazz createClass(Long teacherId, String className, String grade, String subject);
}
