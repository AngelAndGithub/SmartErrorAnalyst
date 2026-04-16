package com.errorquest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-path:./uploads}")
    private String uploadPath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置文件上传路径映射
        String os = System.getProperty("os.name").toLowerCase();
        String absolutePath;
        
        // 处理相对路径转换为绝对路径
        if (uploadPath.startsWith(".")) {
            absolutePath = System.getProperty("user.dir") + uploadPath.substring(1);
        } else {
            absolutePath = uploadPath;
        }
        
        // Windows路径需要特殊处理
        if (os.contains("win")) {
            absolutePath = absolutePath.replace("/", "\\");
        }
        
        registry.addResourceHandler("/api/files/**")
                .addResourceLocations("file:" + absolutePath + "/");
    }
}
