package com.errorquest.controller;

import com.errorquest.dto.Result;
import com.errorquest.service.AIService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/image")
public class ImageRecognitionController {

    @Autowired
    private AIService aiService;

    @PostMapping("/recognize")
    public Result<String> recognizeImage(@RequestBody ImageRecognitionRequest request) {
        if (request.getImageBase64() == null || request.getImageBase64().isEmpty()) {
            return Result.error("图片数据不能为空");
        }

        try {
            // 根据类型构建不同的提示词
            String prompt = buildPromptByType(request.getType());
            
            log.info("开始识别图片，类型: {}", request.getType());
            String result = aiService.recognizeImage(request.getImageBase64(), prompt);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("图片识别失败", e);
            return Result.error("图片识别失败: " + e.getMessage());
        }
    }

    private String buildPromptByType(String type) {
        if (type == null) {
            return "请识别图片中的文字内容，包括数学公式";
        }

        switch (type.toLowerCase()) {
            case "question":
                return "请识别这张题目图片中的所有文字内容，包括题目描述、选项、数学公式等。保持原有格式，数学公式用LaTeX格式表示（如 $x^2 + y^2 = 1$）。只输出识别的内容，不要添加额外解释。";
            case "answer":
                return "请识别这张答案图片中的所有文字内容，包括答案、解题步骤、数学公式等。保持原有格式，数学公式用LaTeX格式表示。只输出识别的内容，不要添加额外解释。";
            case "useranswer":
                return "请识别这张用户答案图片中的所有文字内容，包括手写或打印的答案内容、数学公式等。保持原有格式，数学公式用LaTeX格式表示。只输出识别的内容，不要添加额外解释。";
            default:
                return "请识别图片中的文字内容，包括数学公式";
        }
    }

    @Data
    public static class ImageRecognitionRequest {
        private String imageBase64;
        private String type; // question, answer, userAnswer
    }
}
