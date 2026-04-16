package com.errorquest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

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
        String absolutePath;
        
        // 处理相对路径转换为绝对路径
        if (uploadPath.startsWith(".")) {
            absolutePath = System.getProperty("user.dir") + File.separator + uploadPath.substring(1);
        } else {
            absolutePath = uploadPath;
        }
        
        // 确保路径以分隔符结尾
        if (!absolutePath.endsWith(File.separator)) {
            absolutePath += File.separator;
        }
        
        // 转换为file:// URL格式
        String resourceLocation = "file:" + absolutePath.replace("\\", "/");
        
        registry.addResourceHandler("/api/files/**")
                .addResourceLocations(resourceLocation);
    }
}
