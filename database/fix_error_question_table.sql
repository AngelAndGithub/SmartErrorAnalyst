-- ============================================
-- 手动修复 error_question 表结构
-- 请在MySQL客户端中执行此脚本
-- ============================================

USE errorquest;

-- 1. 查看当前表结构
SELECT '当前表结构：' as info;
DESCRIBE error_question;

-- 2. 检查缺失的字段
SELECT '检查缺失字段：' as info;
SELECT COLUMN_NAME as '字段名', 
       DATA_TYPE as '数据类型',
       COLUMN_COMMENT as '注释'
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'errorquest' 
  AND TABLE_NAME = 'error_question'
  AND COLUMN_NAME IN ('question_images', 'answer_images', 'user_answer_images')
ORDER BY ORDINAL_POSITION;

-- 3. 添加缺失的字段（如果不存在）
-- 添加 question_images 字段
ALTER TABLE error_question 
ADD COLUMN question_images TEXT COMMENT '题目图片URL列表，JSON数组' AFTER question_content;

-- 添加 answer_images 字段
ALTER TABLE error_question 
ADD COLUMN answer_images TEXT COMMENT '答案图片URL列表，JSON数组' AFTER correct_answer;

-- 添加 user_answer_images 字段
ALTER TABLE error_question 
ADD COLUMN user_answer_images TEXT COMMENT '用户答案图片URL列表，JSON数组' AFTER user_answer;

-- 4. 验证修复结果
SELECT '修复后的表结构：' as info;
DESCRIBE error_question;

-- 5. 确认所有图片相关字段都已添加
SELECT '图片相关字段检查：' as info;
SELECT COLUMN_NAME as '字段名', 
       DATA_TYPE as '数据类型',
       COLUMN_COMMENT as '注释'
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'errorquest' 
  AND TABLE_NAME = 'error_question'
  AND COLUMN_NAME LIKE '%images%'
ORDER BY ORDINAL_POSITION;

SELECT '修复完成！' as info;
