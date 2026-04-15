package com.errorquest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.errorquest.entity.Knowledge;
import com.errorquest.entity.Subject;
import com.errorquest.mapper.KnowledgeMapper;
import com.errorquest.mapper.SubjectMapper;
import com.errorquest.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {
    
    @Autowired
    private SubjectMapper subjectMapper;
    
    @Autowired
    private KnowledgeMapper knowledgeMapper;
    
    @Override
    public List<Subject> listActiveSubjects() {
        LambdaQueryWrapper<Subject> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Subject::getStatus, 1)
               .eq(Subject::getDeleted, 0)
               .orderByAsc(Subject::getSubjectId);
        return subjectMapper.selectList(wrapper);
    }
    
    @Override
    public List<Knowledge> getKnowledgeBySubjectId(Long subjectId) {
        return knowledgeMapper.selectBySubjectId(subjectId);
    }
    
    @Override
    public String getSubjectNameById(Long subjectId) {
        Subject subject = subjectMapper.selectById(subjectId);
        return subject != null ? subject.getSubjectName() : null;
    }
}

