package com.errorquest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.errorquest.entity.Knowledge;
import com.errorquest.entity.Subject;

import java.util.List;

public interface SubjectService extends IService<Subject> {
    
    List<Subject> listActiveSubjects();
    
    List<Knowledge> getKnowledgeBySubjectId(Long subjectId);
    
    String getSubjectNameById(Long subjectId);
}

