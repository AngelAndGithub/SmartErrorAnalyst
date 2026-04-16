package com.errorquest.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * 数据库表结构检查和修复工具
 */
@Slf4j
@Component
public class DatabaseSchemaFixer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void checkAndFixSchema() {
        log.info("开始检查数据库表结构...");
        
        try {
            // 检查 error_question 表的字段
            checkAndAddColumn("error_question", "question_images", 
                "ALTER TABLE error_question ADD COLUMN question_images TEXT COMMENT '题目图片URL列表，JSON数组' AFTER question_content");
            
            checkAndAddColumn("error_question", "answer_images", 
                "ALTER TABLE error_question ADD COLUMN answer_images TEXT COMMENT '答案图片URL列表，JSON数组' AFTER correct_answer");
            
            checkAndAddColumn("error_question", "user_answer_images", 
                "ALTER TABLE error_question ADD COLUMN user_answer_images TEXT COMMENT '用户答案图片URL列表，JSON数组' AFTER user_answer");
            
            log.info("数据库表结构检查完成");
        } catch (Exception e) {
            log.error("数据库表结构检查失败", e);
        }
    }

    /**
     * 检查并添加缺失的列
     */
    private void checkAndAddColumn(String tableName, String columnName, String alterSql) {
        try {
            // 检查列是否存在
            String checkSql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                            "WHERE TABLE_SCHEMA = DATABASE() " +
                            "AND TABLE_NAME = ? " +
                            "AND COLUMN_NAME = ?";
            
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, tableName, columnName);
            
            if (count != null && count == 0) {
                log.info("表 {} 缺少列 {}，正在添加...", tableName, columnName);
                jdbcTemplate.execute(alterSql);
                log.info("成功添加列 {}", columnName);
            } else {
                log.debug("表 {} 的列 {} 已存在", tableName, columnName);
            }
        } catch (Exception e) {
            log.error("检查/添加列 {} 失败", columnName, e);
        }
    }
}
