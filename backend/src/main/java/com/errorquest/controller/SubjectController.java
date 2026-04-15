package com.errorquest.controller;

import com.errorquest.dto.Result;
import com.errorquest.entity.Knowledge;
import com.errorquest.entity.Subject;
import com.errorquest.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@CrossOrigin(origins = "http://localhost:5173")
public class SubjectController {
    
    @Autowired
    private SubjectService subjectService;
    
    @GetMapping
    public Result<List<Subject>> list() {
        List<Subject> subjects = subjectService.listActiveSubjects();
        return Result.success(subjects);
    }
    
    @GetMapping("/{subjectId}/knowledge")
    public Result<List<Knowledge>> getKnowledge(@PathVariable Long subjectId) {
        List<Knowledge> knowledgeList = subjectService.getKnowledgeBySubjectId(subjectId);
        return Result.success(knowledgeList);
    }
}

