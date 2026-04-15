package com.errorquest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.errorquest.entity.Clazz;
import com.errorquest.mapper.ClassMapper;
import com.errorquest.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassServiceImpl extends ServiceImpl<ClassMapper, Clazz> implements ClassService {
    
    @Autowired
    private ClassMapper classMapper;
    
    @Override
    public List<Clazz> getTeacherClasses(Long teacherId) {
        return classMapper.selectByTeacherId(teacherId);
    }
    
    @Override
    public Clazz createClass(Long teacherId, String className, String grade, String subject) {
        Clazz clazz = new Clazz();
        clazz.setClassName(className);
        clazz.setGrade(grade);
        clazz.setSubject(subject);
        clazz.setTeacherId(teacherId);
        clazz.setStatus(1);
        
        classMapper.insert(clazz);
        return clazz;
    }
}

