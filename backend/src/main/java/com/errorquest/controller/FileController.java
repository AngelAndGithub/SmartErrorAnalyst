package com.errorquest.controller;

import com.errorquest.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${file.upload-path:./uploads}")
    private String uploadPath;

    @Value("${file.access-url-prefix:http://localhost:8080/api/files}")
    private String accessUrlPrefix;

    @PostMapping("/upload")
    public Result<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isImageFile(originalFilename)) {
            return Result.error("仅支持 jpg、png、jpeg 格式的图片");
        }

        // 验证文件大小 (10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            return Result.error("文件大小不能超过10MB");
        }

        try {
            // 生成唯一文件名: 日期 + UUID + 扩展名
            String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
            
            // 创建目录
            String fullPath = uploadPath + File.separator + dateStr;
            File directory = new File(fullPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 保存文件
            String filePath = fullPath + File.separator + fileName;
            file.transferTo(new File(filePath));

            // 构建访问URL
            String fileUrl = accessUrlPrefix + "/" + dateStr + "/" + fileName;

            // 返回结果
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("fileName", fileName);
            result.put("originalName", originalFilename);
            result.put("size", String.valueOf(file.getSize()));

            log.info("文件上传成功: {}", fileUrl);
            return Result.success(result);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/{datePath}/**")
    public void serveFile(@PathVariable String datePath, javax.servlet.http.HttpServletResponse response) {
        // 从请求路径中提取文件名
        String requestPath = (String) org.springframework.web.context.request.RequestContextHolder
                .currentRequestAttributes()
                .getAttribute("org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping", 0);
        
        String fullPath = requestPath.replace("/api/files/", "");
        File file = new File(uploadPath + File.separator + fullPath);

        if (!file.exists()) {
            response.setStatus(404);
            return;
        }

        // 设置响应头
        String contentType = getContentType(file.getName());
        response.setContentType(contentType);
        response.setContentLengthLong(file.length());

        // 输出文件
        try {
            java.nio.file.Files.copy(file.toPath(), response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("文件读取失败", e);
            response.setStatus(500);
        }
    }

    private boolean isImageFile(String filename) {
        String lowerName = filename.toLowerCase();
        return lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") || 
               lowerName.endsWith(".png") || lowerName.endsWith(".gif");
    }

    private String getContentType(String filename) {
        String lowerName = filename.toLowerCase();
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerName.endsWith(".png")) {
            return "image/png";
        } else if (lowerName.endsWith(".gif")) {
            return "image/gif";
        }
        return "application/octet-stream";
    }
}
