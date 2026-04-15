package com.errorquest.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DatabaseInitConfig implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.info("开始检查数据库初始化...");
        
        try {
            // 检查数据库是否存在，如果不存在则创建
            createDatabaseIfNotExists();
            
            // 检查用户表是否存在，如果存在则跳过初始化
            if (isTableExists("user")) {
                log.info("数据库表已存在，跳过初始化");
                return;
            }
            
            // 执行初始化脚本
            log.info("开始执行初始化脚本...");
            executeInitScript();
            log.info("数据库初始化完成！");
        } catch (Exception e) {
            log.error("数据库初始化失败: {}", e.getMessage(), e);
        }
    }
    
    private void dropAllTables() {
        try {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
            jdbcTemplate.execute("DROP TABLE IF EXISTS exercise_submit");
            jdbcTemplate.execute("DROP TABLE IF EXISTS exercise");
            jdbcTemplate.execute("DROP TABLE IF EXISTS review_plan");
            jdbcTemplate.execute("DROP TABLE IF EXISTS error_question");
            jdbcTemplate.execute("DROP TABLE IF EXISTS knowledge");
            jdbcTemplate.execute("DROP TABLE IF EXISTS subject");
            jdbcTemplate.execute("DROP TABLE IF EXISTS class");
            jdbcTemplate.execute("DROP TABLE IF EXISTS user");
            jdbcTemplate.execute("DROP TABLE IF EXISTS system_log");
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
            log.info("已清理所有旧表");
        } catch (Exception e) {
            log.warn("清理旧表失败: {}", e.getMessage());
        }
    }

    private void createDatabaseIfNotExists() {
        try {
            // 获取连接信息
            Connection conn = dataSource.getConnection();
            String url = conn.getMetaData().getURL();
            
            // 如果连接的是 errorquest 数据库，检查是否可连接
            if (url.contains("errorquest")) {
                log.info("已连接到 errorquest 数据库");
            }
        } catch (SQLException e) {
            log.error("数据库连接失败: {}", e.getMessage());
        }
    }

    private boolean isTableExists(String tableName) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?",
                Integer.class,
                tableName
            );
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void executeInitScript() {
        try {
            // 设置连接字符集为 UTF-8
            jdbcTemplate.execute("SET NAMES utf8mb4");
            
            // 读取 SQL 文件并使用 UTF-8 编码执行
            Resource resource = new ClassPathResource("db/init.sql");
            if (resource.exists()) {
                // 使用 ScriptUtils 执行 SQL 脚本，它会正确处理多行 SQL
                Connection conn = dataSource.getConnection();
                ScriptUtils.executeSqlScript(conn, new EncodedResource(resource, StandardCharsets.UTF_8));
                log.info("SQL 脚本执行成功");
            } else {
                log.warn("未找到数据库初始化脚本文件");
            }
        } catch (Exception e) {
            log.error("执行 SQL 脚本失败: {}", e.getMessage(), e);
        }
    }
}
