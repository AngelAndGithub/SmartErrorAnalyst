package com.errorquest.service.impl;

import com.errorquest.dto.AIAnalysisResultDTO;
import com.errorquest.service.AIService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AIServiceImpl implements AIService {

    // 智谱 AI 配置
    @Value("${ai.zhipu.api-key:}")
    private String zhipuApiKey;

    @Value("${ai.zhipu.model:glm-4-flash}")
    private String zhipuModel;

    @Value("${ai.zhipu.base-url:https://open.bigmodel.cn/api/paas/v4}")
    private String zhipuBaseUrl;

    // DeepSeek 配置
    @Value("${ai.deepseek.api-key:}")
    private String deepseekApiKey;

    @Value("${ai.deepseek.model:deepseek-chat}")
    private String deepseekModel;

    @Value("${ai.deepseek.base-url:https://api.deepseek.com}")
    private String deepseekBaseUrl;

    // 默认提供商
    @Value("${ai.default-provider:mock}")
    private String defaultProvider;

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AIAnalysisResultDTO analyzeErrorQuestion(String questionContent, String correctAnswer, 
                                                     String userAnswer, String subjectName, String errorReason) {
        return analyzeErrorQuestion(questionContent, correctAnswer, userAnswer, subjectName, errorReason, defaultProvider);
    }

    @Override
    public AIAnalysisResultDTO analyzeErrorQuestion(String questionContent, String correctAnswer, 
                                                     String userAnswer, String subjectName, String errorReason,
                                                     String provider) {
        
        String actualProvider = provider != null ? provider : defaultProvider;
        log.info("使用AI提供商: {}", actualProvider);

        try {
            String prompt = buildPrompt(questionContent, correctAnswer, userAnswer, subjectName, errorReason);
            
            switch (actualProvider.toLowerCase()) {
                case "zhipu":
                    return callZhipuAPI(prompt);
                case "deepseek":
                    return callDeepSeekAPI(prompt);
                case "mock":
                default:
                    log.info("使用模拟AI解析");
                    return generateMockResult(questionContent, correctAnswer, userAnswer, subjectName, errorReason);
            }
        } catch (Exception e) {
            log.error("调用{} API失败，降级到模拟模式", actualProvider, e);
            return generateMockResult(questionContent, correctAnswer, userAnswer, subjectName, errorReason);
        }
    }

    @Override
    public List<String> getAvailableProviders() {
        return Arrays.asList("mock", "zhipu", "deepseek");
    }
    
    @Override
    public List<String> getEnabledProviders() {
        List<String> enabled = new ArrayList<>();
        // 返回所有已配置API Key的AI提供商
        
        log.info("Checking AI providers - zhipuApiKey: {}, deepseekApiKey: {}", 
                 zhipuApiKey != null && !zhipuApiKey.isEmpty() ? "configured" : "not configured",
                 deepseekApiKey != null && !deepseekApiKey.isEmpty() ? "configured" : "not configured");
        
        if (zhipuApiKey != null && !zhipuApiKey.isEmpty()) {
            enabled.add("zhipu");
        }
        if (deepseekApiKey != null && !deepseekApiKey.isEmpty()) {
            enabled.add("deepseek");
        }
        
        log.info("Enabled providers: {}", enabled);
        return enabled;
    }

    private String buildPrompt(String questionContent, String correctAnswer, String userAnswer, 
                               String subjectName, String errorReason) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位专业的教育辅导老师，擅长分析学生错题并提供详细解析。\n\n");
        prompt.append("请对以下错题进行智能分析，以JSON格式返回结果：\n\n");
        prompt.append("题目内容：").append(questionContent).append("\n");
        
        if (correctAnswer != null && !correctAnswer.isEmpty()) {
            prompt.append("正确答案：").append(correctAnswer).append("\n");
        }
        if (userAnswer != null && !userAnswer.isEmpty()) {
            prompt.append("学生答案：").append(userAnswer).append("\n");
        }
        if (subjectName != null && !subjectName.isEmpty()) {
            prompt.append("学科：").append(subjectName).append("\n");
        }
        if (errorReason != null && !errorReason.isEmpty()) {
            prompt.append("错误原因：").append(errorReason).append("\n");
        }
        
        prompt.append("\n请返回以下JSON格式（不要包含markdown标记）：\n");
        prompt.append("{\n");
        prompt.append("  \"analysis\": \"详细的题目解析，包括解题思路、步骤和正确答案的推导过程\",\n");
        prompt.append("  \"knowledgePoint\": \"本题涉及的核心知识点和相关概念\",\n");
        prompt.append("  \"suggestions\": \"针对该学生的具体学习建议，如何避免类似错误\"\n");
        prompt.append("}");
        
        return prompt.toString();
    }

    private AIAnalysisResultDTO callZhipuAPI(String prompt) throws IOException {
        // 检查API Key
        if (zhipuApiKey == null || zhipuApiKey.isEmpty()) {
            throw new IOException("智谱API Key未配置");
        }

        String url = zhipuBaseUrl + "/chat/completions";
        
        // 使用Map构建请求体，避免字符串转义问题
        Map<String, Object> requestBodyMap = new LinkedHashMap<>();
        requestBodyMap.put("model", zhipuModel);
        
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMessage = new LinkedHashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是一个专业的教育辅导助手");
        messages.add(systemMessage);
        
        Map<String, String> userMessage = new LinkedHashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);
        
        requestBodyMap.put("messages", messages);
        requestBodyMap.put("temperature", 0.7);
        requestBodyMap.put("max_tokens", 2000);
        
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);
        log.info("智谱API请求体: {}", requestBody);

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + zhipuApiKey)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            log.info("智谱API响应: {}", responseBody);
            
            if (!response.isSuccessful()) {
                throw new IOException("API调用失败: " + response + ", 响应: " + responseBody);
            }

            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String content = jsonNode.path("choices").get(0).path("message").path("content").asText();
            
            return parseAIResponse(content);
        }
    }

    private AIAnalysisResultDTO callDeepSeekAPI(String prompt) throws IOException {
        // 检查API Key
        if (deepseekApiKey == null || deepseekApiKey.isEmpty()) {
            throw new IOException("DeepSeek API Key未配置");
        }

        String url = deepseekBaseUrl + "/chat/completions";
        
        // 使用ObjectMapper构建JSON请求体，避免特殊字符问题
        Map<String, Object> requestBodyMap = new LinkedHashMap<>();
        requestBodyMap.put("model", deepseekModel);
        requestBodyMap.put("temperature", 0.7);
        requestBodyMap.put("max_tokens", 2000);
        
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMessage = new LinkedHashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是一个专业的教育辅导助手");
        messages.add(systemMessage);
        
        Map<String, String> userMessage = new LinkedHashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);
        
        requestBodyMap.put("messages", messages);
        
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);
        log.info("DeepSeek API请求体: {}", requestBody);

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + deepseekApiKey)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            log.info("DeepSeek API响应: {}", responseBody);
            
            if (!response.isSuccessful()) {
                throw new IOException("API调用失败: " + response + ", 响应: " + responseBody);
            }

            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String content = jsonNode.path("choices").get(0).path("message").path("content").asText();
            
            return parseAIResponse(content);
        }
    }

    private AIAnalysisResultDTO parseAIResponse(String content) {
        AIAnalysisResultDTO result = new AIAnalysisResultDTO();
        
        try {
            // 清理可能的markdown标记
            String cleanContent = content.replaceAll("```json\\s*", "")
                                        .replaceAll("```\\s*", "")
                                        .trim();
            
            JsonNode jsonNode = objectMapper.readTree(cleanContent);
            result.setAnalysis(jsonNode.path("analysis").asText("暂无解析"));
            result.setKnowledgePoint(jsonNode.path("knowledgePoint").asText(""));
            result.setSuggestions(jsonNode.path("suggestions").asText(""));
        } catch (Exception e) {
            log.error("解析AI响应失败", e);
            result.setAnalysis(content);
            result.setKnowledgePoint("");
            result.setSuggestions("");
        }
        
        return result;
    }

    private AIAnalysisResultDTO generateMockResult(String questionContent, String correctAnswer, 
                                                    String userAnswer, String subjectName, String errorReason) {
        AIAnalysisResultDTO result = new AIAnalysisResultDTO();
        
        StringBuilder analysis = new StringBuilder();
        analysis.append("【题目分析】\n");
        analysis.append("本题考查的是").append(subjectName != null ? subjectName : "相关知识").append("。\n\n");
        
        if (correctAnswer != null && userAnswer != null) {
            analysis.append("【答案对比】\n");
            analysis.append("正确答案：").append(correctAnswer).append("\n");
            analysis.append("你的答案：").append(userAnswer).append("\n");
            
            if (correctAnswer.trim().equals(userAnswer.trim())) {
                analysis.append("✓ 回答正确！\n\n");
            } else {
                analysis.append("✗ 回答错误\n\n");
            }
        }
        
        analysis.append("【详细解析】\n");
        analysis.append("这道题目主要考察对").append(errorReason != null ? errorReason : "相关概念").append("的理解。\n");
        
        if ("概念混淆".equals(errorReason)) {
            analysis.append("建议：仔细区分相关概念的定义和应用场景，多做类似题目加深理解。");
        } else if ("计算失误".equals(errorReason)) {
            analysis.append("建议：加强计算能力训练，做题时要仔细审题，计算过程要规范。");
        } else if ("审题不清".equals(errorReason)) {
            analysis.append("建议：养成仔细审题的习惯，圈画关键词，理解题意后再作答。");
        } else if ("不会做".equals(errorReason)) {
            analysis.append("建议：回归课本复习相关知识点，从基础例题开始练习。");
        } else {
            analysis.append("建议：总结错题规律，建立错题本，定期复习巩固。");
        }
        
        result.setAnalysis(analysis.toString());
        result.setKnowledgePoint("本题的考点是" + (subjectName != null ? subjectName : "该学科") + "中的核心概念，建议系统复习相关章节内容。");
        result.setSuggestions("1. 整理错题本，定期复习\n2. 针对薄弱环节进行专项训练\n3. 总结解题方法和技巧\n4. 建立知识体系框架");
        
        return result;
    }

    @Override
    public String recognizeImage(String imageBase64, String prompt) {
        log.info("开始调用智谱AI视觉模型进行图片识别");
        log.debug("接收到的图片数据长度: {}, 开头50字符: {}", 
            imageBase64 != null ? imageBase64.length() : 0,
            imageBase64 != null && imageBase64.length() > 50 ? imageBase64.substring(0, 50) : imageBase64);
        
        try {
            // 检查API Key
            if (zhipuApiKey == null || zhipuApiKey.isEmpty()) {
                throw new IOException("智谱AI API Key未配置");
            }

            String url = zhipuBaseUrl + "/chat/completions";
            
            // 处理base64图片URL格式
            // 前端FileReader.readAsDataURL返回的已经是完整的data URI格式
            String imageUrlValue = imageBase64;
            
            // 清理base64数据中的换行符和空格
            if (imageBase64.contains("\n") || imageBase64.contains("\r") || imageBase64.contains(" ")) {
                log.warn("检测到base64数据中包含换行符或空格,进行清理");
                imageUrlValue = imageBase64.replaceAll("\\s+", "");
            }
            
            if (!imageUrlValue.startsWith("data:image/")) {
                // 如果不是data URI格式,需要添加前缀
                log.warn("图片数据不是data URI格式,自动添加前缀");
                imageUrlValue = "data:image/jpeg;base64," + imageUrlValue;
            }
            
            log.info("图片数据长度: {}, 前80字符: {}", 
                imageUrlValue.length(), 
                imageUrlValue.length() > 80 ? imageUrlValue.substring(0, 80) : imageUrlValue);
            
            // 构建多模态请求体 - 使用LinkedHashMap保证字段顺序
            Map<String, Object> requestBodyMap = new LinkedHashMap<>();
            requestBodyMap.put("model", "glm-4v-flash"); // 使用智谱视觉模型
            requestBodyMap.put("max_tokens", 2000);
            
            // 构建消息内容
            List<Map<String, Object>> messages = new ArrayList<>();
            Map<String, Object> userMessage = new LinkedHashMap<>();
            userMessage.put("role", "user");
            
            // 构建content数组（支持图文混合）
            List<Map<String, Object>> contentList = new ArrayList<>();
            
            // 先添加文本提示（某些API要求text在前）
            Map<String, Object> textContent = new LinkedHashMap<>();
            textContent.put("type", "text");
            textContent.put("text", prompt != null ? prompt : "请识别图片中的文字内容，包括数学公式");
            contentList.add(textContent);
            
            // 再添加图片
            Map<String, Object> imageContent = new LinkedHashMap<>();
            imageContent.put("type", "image_url");
            Map<String, String> imageUrl = new LinkedHashMap<>();
            imageUrl.put("url", imageUrlValue);
            imageContent.put("image_url", imageUrl);
            contentList.add(imageContent);
            
            userMessage.put("content", contentList);
            messages.add(userMessage);
            
            requestBodyMap.put("messages", messages);
            
            // 转换为JSON
            String requestBody = objectMapper.writeValueAsString(requestBodyMap);
            // 直接打印完整请求体到控制台
            System.out.println("========== 智谱视觉API请求 ==========");
            System.out.println(requestBody);
            System.out.println("=====================================");
            log.info("智谱视觉API请求体长度: {}", requestBody.length());
            
            // 构建HTTP请求
            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer " + zhipuApiKey)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                    .build();
            
            // 发送请求
            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body().string();
                log.info("智谱视觉API响应: {}", responseBody);
                
                if (!response.isSuccessful()) {
                    throw new IOException("API调用失败: " + response + ", 响应: " + responseBody);
                }

                JsonNode jsonNode = objectMapper.readTree(responseBody);
                String content = jsonNode.path("choices").get(0).path("message").path("content").asText();
                
                return content;
            }
        } catch (Exception e) {
            log.error("调用智谱AI视觉模型失败", e);
            return "图片识别失败: " + e.getMessage();
        }
    }
}
