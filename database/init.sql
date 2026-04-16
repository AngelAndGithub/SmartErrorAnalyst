-- 错题智析宝数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS errorquest 
    DEFAULT CHARACTER SET utf8mb4 
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE errorquest;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    role TINYINT DEFAULT 0 COMMENT '角色：0-学生 1-教师 2-管理员',
    name VARCHAR(50) COMMENT '姓名',
    grade VARCHAR(20) COMMENT '年级',
    class_id BIGINT COMMENT '班级ID',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_class_id (class_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 班级表
CREATE TABLE IF NOT EXISTS class (
    class_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '班级ID',
    class_name VARCHAR(50) NOT NULL COMMENT '班级名称',
    grade VARCHAR(20) COMMENT '年级',
    subject VARCHAR(20) COMMENT '学科',
    teacher_id BIGINT COMMENT '班主任ID',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_teacher_id (teacher_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- 学科表
CREATE TABLE IF NOT EXISTS subject (
    subject_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学科ID',
    subject_name VARCHAR(50) NOT NULL COMMENT '学科名称',
    grade VARCHAR(20) COMMENT '适用年级',
    status TINYINT DEFAULT 1 COMMENT '状态：0-未启用 1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学科表';

-- 知识点表
CREATE TABLE IF NOT EXISTS knowledge (
    knowledge_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '知识点ID',
    knowledge_name VARCHAR(100) NOT NULL COMMENT '知识点名称',
    subject_id BIGINT NOT NULL COMMENT '所属学科ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父知识点ID，0表示顶级',
    level TINYINT DEFAULT 1 COMMENT '层级：1-一级 2-二级 3-三级',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_subject_id (subject_id),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点表';

-- 错题表
CREATE TABLE IF NOT EXISTS error_question (
    question_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '错题ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    subject_id BIGINT NOT NULL COMMENT '学科ID',
    knowledge_id BIGINT COMMENT '知识点ID',
    question_type TINYINT COMMENT '题目类型：1-单选 2-多选 3-填空 4-解答',
    difficulty TINYINT COMMENT '难度：1-简单 2-中等 3-困难',
    error_reason VARCHAR(100) COMMENT '错误原因',
    question_content TEXT COMMENT '题目内容',
    question_images TEXT COMMENT '题目图片URL列表，JSON数组',
    options JSON COMMENT '选项（单选/多选）',
    correct_answer TEXT COMMENT '正确答案',
    answer_images TEXT COMMENT '答案图片URL列表，JSON数组',
    user_answer TEXT COMMENT '用户答案',
    user_answer_images TEXT COMMENT '用户答案图片URL列表，JSON数组',
    analysis TEXT COMMENT '解析',
    notes TEXT COMMENT '笔记',
    tags VARCHAR(255) COMMENT '标签，逗号分隔',
    mastery_status TINYINT DEFAULT 0 COMMENT '掌握状态：0-未掌握 1-已掌握',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_subject_id (subject_id),
    INDEX idx_knowledge_id (knowledge_id),
    INDEX idx_question_type (question_type),
    INDEX idx_difficulty (difficulty),
    INDEX idx_error_reason (error_reason),
    INDEX idx_mastery_status (mastery_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='错题表';

-- 复习计划表
CREATE TABLE IF NOT EXISTS review_plan (
    plan_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '计划ID',
    question_id BIGINT NOT NULL COMMENT '错题ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    plan_time DATE NOT NULL COMMENT '计划复习日期',
    actual_time DATETIME COMMENT '实际复习时间',
    review_result TINYINT DEFAULT 0 COMMENT '复习结果：0-未复习 1-正确 2-错误',
    review_count TINYINT DEFAULT 0 COMMENT '复习次数',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_question_id (question_id),
    INDEX idx_plan_time (plan_time),
    INDEX idx_review_result (review_result)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='复习计划表';

-- 练习表
CREATE TABLE IF NOT EXISTS exercise (
    exercise_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '练习ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    class_id BIGINT NOT NULL COMMENT '班级ID',
    subject_id BIGINT COMMENT '学科ID',
    knowledge_id BIGINT COMMENT '知识点ID',
    exercise_name VARCHAR(100) NOT NULL COMMENT '练习名称',
    deadline DATETIME COMMENT '截止时间',
    status TINYINT DEFAULT 1 COMMENT '状态：0-已结束 1-进行中',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_class_id (class_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='练习表';

-- 练习提交表
CREATE TABLE IF NOT EXISTS exercise_submit (
    submit_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '提交ID',
    exercise_id BIGINT NOT NULL COMMENT '练习ID',
    user_id BIGINT NOT NULL COMMENT '学生ID',
    answer_content TEXT COMMENT '答案内容',
    submit_time DATETIME COMMENT '提交时间',
    score INT COMMENT '得分',
    feedback TEXT COMMENT '教师反馈',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_exercise_id (exercise_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='练习提交表';

-- 系统日志表
CREATE TABLE IF NOT EXISTS system_log (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT COMMENT '用户ID',
    operation VARCHAR(50) COMMENT '操作类型',
    operation_content TEXT COMMENT '操作内容',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    status TINYINT DEFAULT 1 COMMENT '状态：0-失败 1-成功',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_operation (operation),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志表';

-- 初始化基础数据

-- 插入学科数据
INSERT INTO subject (subject_name, grade, status) VALUES
('数学', '初中', 1),
('语文', '初中', 1),
('英语', '初中', 1),
('物理', '初中', 1),
('化学', '初中', 1),
('数学', '高中', 1),
('语文', '高中', 1),
('英语', '高中', 1),
('物理', '高中', 1),
('化学', '高中', 1);

-- 插入数学知识点（初中）
INSERT INTO knowledge (knowledge_name, subject_id, parent_id, level, status) VALUES
-- 初中数学一级知识点
('数与代数', 1, 0, 1, 1),
('图形与几何', 1, 0, 1, 1),
('统计与概率', 1, 0, 1, 1),
-- 初中数学二级知识点
('有理数', 1, 1, 2, 1),
('整式', 1, 1, 2, 1),
('分式', 1, 1, 2, 1),
('方程与不等式', 1, 1, 2, 1),
('函数', 1, 1, 2, 1),
('三角形', 1, 2, 2, 1),
('四边形', 1, 2, 2, 1),
('圆', 1, 2, 2, 1),
-- 初中数学三级知识点
('一次函数', 1, 8, 3, 1),
('二次函数', 1, 8, 3, 1),
('反比例函数', 1, 8, 3, 1);

-- 插入默认管理员账号（密码：admin123）
INSERT INTO user (username, password, role, name, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', 2, '系统管理员', 1);

-- 插入测试教师账号（密码：teacher123）
INSERT INTO user (username, password, role, name, status) VALUES
('teacher', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', 1, '测试教师', 1);

-- 插入测试学生账号（密码：student123）
INSERT INTO user (username, password, role, name, grade, status) VALUES
('student', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', 0, '测试学生', '初三', 1);
