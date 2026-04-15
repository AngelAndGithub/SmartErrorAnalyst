package com.errorquest.controller;

import com.errorquest.dto.AIAnalysisRequestDTO;
import com.errorquest.dto.AIAnalysisResultDTO;
import com.errorquest.dto.Result;
import com.errorquest.service.AIService;
import com.errorquest.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:5173")
public class AIController {

    @Autowired
    private AIService aiService;

    @Autowired
    private SubjectService subjectService;

    @PostMapping("/analyze")
    public Result<AIAnalysisResultDTO> analyzeErrorQuestion(@RequestBody AIAnalysisRequestDTO request) {
        try {
            // 获取学科名称
            String subjectName = null;
            if (request.getSubjectId() != null) {
                subjectName = subjectService.getSubjectNameById(request.getSubjectId());
            }

            // 获取AI提供商（如果请求中指定了）
            String provider = request.getProvider();

            AIAnalysisResultDTO result = aiService.analyzeErrorQuestion(
                    request.getQuestionContent(),
                    request.getCorrectAnswer(),
                    request.getUserAnswer(),
                    subjectName,
                    request.getErrorReason(),
                    provider
            );

            return Result.success(result);
        } catch (Exception e) {
            return Result.error("AI解析失败：" + e.getMessage());
        }
    }

    @GetMapping("/providers")
    public Result<List<String>> getAvailableProviders() {
        // 只返回已配置API Key的提供商
        return Result.success(aiService.getEnabledProviders());
    }

    @GetMapping("/providers/info")
    public Result<Map<String, Object>> getProvidersInfo() {
        Map<String, Object> info = new HashMap<>();
        
        Map<String, String> zhipu = new HashMap<>();
        zhipu.put("name", "智谱 GLM-4-Flash");
        zhipu.put("description", "完全免费，中文理解优秀");
        zhipu.put("url", "https://open.bigmodel.cn");
        
        Map<String, String> deepseek = new HashMap<>();
        deepseek.put("name", "DeepSeek-V2");
        deepseek.put("description", "免费额度超大，数学推理强");
        deepseek.put("url", "https://platform.deepseek.com");
        
        Map<String, String> mock = new HashMap<>();
        mock.put("name", "模拟模式");
        mock.put("description", "本地模拟，无需API Key");
        mock.put("url", "");
        
        info.put("zhipu", zhipu);
        info.put("deepseek", deepseek);
        info.put("mock", mock);
        
        return Result.success(info);
    }
}
