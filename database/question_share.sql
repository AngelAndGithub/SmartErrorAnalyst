-- ========================================
-- 错题分享功能数据库表
-- 创建时间: 2026-04-16
-- ========================================

-- 分享记录表
CREATE TABLE IF NOT EXISTS question_share (
    share_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分享ID',
    share_no VARCHAR(32) NOT NULL UNIQUE COMMENT '分享编号（唯一标识）',
    question_id BIGINT NOT NULL COMMENT '错题ID',
    sharer_id BIGINT NOT NULL COMMENT '分享者ID',
    share_type TINYINT NOT NULL DEFAULT 1 COMMENT '分享类型：1-公开分享 2-指定用户',
    share_title VARCHAR(200) COMMENT '分享标题',
    share_message TEXT COMMENT '分享留言',
    include_answer TINYINT DEFAULT 1 COMMENT '是否包含答案：0-否 1-是',
    include_analysis TINYINT DEFAULT 1 COMMENT '是否包含解析：0-否 1-是',
    include_notes TINYINT DEFAULT 0 COMMENT '是否包含笔记：0-否 1-是',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 1 COMMENT '状态：0-已撤回 1-正常',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_question_id (question_id),
    INDEX idx_sharer_id (sharer_id),
    INDEX idx_share_no (share_no),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='错题分享记录表';

-- 分享目标用户表（指定用户分享）
CREATE TABLE IF NOT EXISTS question_share_target (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    share_id BIGINT NOT NULL COMMENT '分享ID',
    target_user_id BIGINT NOT NULL COMMENT '目标用户ID',
    has_viewed TINYINT DEFAULT 0 COMMENT '是否已查看：0-否 1-是',
    view_time DATETIME COMMENT '查看时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_share_target (share_id, target_user_id),
    INDEX idx_target_user (target_user_id),
    FOREIGN KEY (share_id) REFERENCES question_share(share_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分享目标用户表';

-- 分享互动表（点赞、评论等）
CREATE TABLE IF NOT EXISTS question_share_interaction (
    interaction_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '互动ID',
    share_id BIGINT NOT NULL COMMENT '分享ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    interaction_type TINYINT NOT NULL COMMENT '互动类型：1-点赞 2-收藏',
    interaction_content TEXT COMMENT '互动内容（评论内容）',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_share_user (share_id, user_id),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (share_id) REFERENCES question_share(share_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分享互动表';

-- 解题思路表（用户可以添加自己的解题思路）
CREATE TABLE IF NOT EXISTS question_share_solution (
    solution_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '解题思路ID',
    share_id BIGINT NOT NULL COMMENT '分享ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    solution_content TEXT NOT NULL COMMENT '解题思路内容',
    solution_images TEXT COMMENT '解题思路图片URL列表，JSON数组',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    is_best TINYINT DEFAULT 0 COMMENT '是否最佳解题：0-否 1-是',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_share_id (share_id),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (share_id) REFERENCES question_share(share_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='解题思路表';

-- 示例数据（可选）
-- INSERT INTO question_share (share_no, question_id, sharer_id, share_type, share_title, share_message)
-- VALUES ('SHARE20260416001', 1, 1, 1, '经典数学题分享', '这道题很有意思，大家一起讨论！');
