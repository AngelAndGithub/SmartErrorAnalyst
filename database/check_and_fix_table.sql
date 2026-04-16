-- 检查并修复 error_question 表结构
USE errorquest;

-- 查看当前表结构
DESCRIBE error_question;

-- 检查是否存在 question_images 字段
SELECT COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'errorquest' 
  AND TABLE_NAME = 'error_question' 
  AND COLUMN_NAME = 'question_images';

-- 如果字段不存在，添加缺失的字段
-- 请根据实际情况取消下面的注释来执行

-- 添加 question_images 字段
ALTER TABLE error_question 
ADD COLUMN IF NOT EXISTS question_images TEXT COMMENT '题目图片URL列表，JSON数组' AFTER question_content;

-- 添加 answer_images 字段
ALTER TABLE error_question 
ADD COLUMN IF NOT EXISTS answer_images TEXT COMMENT '答案图片URL列表，JSON数组' AFTER correct_answer;

-- 添加 user_answer_images 字段
ALTER TABLE error_question 
ADD COLUMN IF NOT EXISTS user_answer_images TEXT COMMENT '用户答案图片URL列表，JSON数组' AFTER user_answer;

-- 再次查看表结构确认
DESCRIBE error_question;
