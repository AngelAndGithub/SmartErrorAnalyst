package com.errorquest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.errorquest.dto.QuestionShareDTO;
import com.errorquest.dto.QuestionShareResultDTO;
import com.errorquest.dto.QuestionShareSolutionDTO;
import com.errorquest.entity.*;
import com.errorquest.mapper.*;
import com.errorquest.service.QuestionShareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class QuestionShareServiceImpl implements QuestionShareService {
    
    @Autowired
    private QuestionShareMapper shareMapper;
    
    @Autowired
    private QuestionShareSolutionMapper solutionMapper;
    
    @Autowired
    private ErrorQuestionMapper questionMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    @Transactional
    public String createShare(Long userId, QuestionShareDTO dto) {
        log.info("创建分享: userId={}, questionId={}", userId, dto.getQuestionId());
        
        // 1. 验证错题是否存在且属于当前用户
        ErrorQuestion question = questionMapper.selectById(dto.getQuestionId());
        if (question == null || !question.getUserId().equals(userId)) {
            throw new RuntimeException("错题不存在或无权限");
        }
        
        // 2. 创建分享记录
        QuestionShare share = new QuestionShare();
        share.setShareNo(generateShareNo());
        share.setQuestionId(dto.getQuestionId());
        share.setSharerId(userId);
        share.setShareType(dto.getShareType() != null ? dto.getShareType() : 1);
        share.setShareTitle(dto.getShareTitle());
        share.setShareMessage(dto.getShareMessage());
        share.setIncludeAnswer(dto.getIncludeAnswer() != null ? dto.getIncludeAnswer() : 1);
        share.setIncludeAnalysis(dto.getIncludeAnalysis() != null ? dto.getIncludeAnalysis() : 1);
        share.setIncludeNotes(dto.getIncludeNotes() != null ? dto.getIncludeNotes() : 0);
        share.setViewCount(0);
        share.setLikeCount(0);
        share.setStatus(1);
        
        shareMapper.insert(share);
        log.info("分享记录创建成功: shareNo={}", share.getShareNo());
        
        // 3. 如果是指定分享，添加目标用户
        if (share.getShareType() == 2 && dto.getTargetUserIds() != null && !dto.getTargetUserIds().isEmpty()) {
            String sql = "INSERT INTO question_share_target (share_id, target_user_id, has_viewed) VALUES (?, ?, 0)";
            List<Object[]> batchArgs = new ArrayList<>();
            
            for (Long targetUserId : dto.getTargetUserIds()) {
                if (!targetUserId.equals(userId)) { // 不能分享给自己
                    batchArgs.add(new Object[]{share.getShareId(), targetUserId});
                }
            }
            
            if (!batchArgs.isEmpty()) {
                jdbcTemplate.batchUpdate(sql, batchArgs);
                log.info("添加目标用户成功: count={}", batchArgs.size());
            }
        }
        
        return share.getShareNo();
    }
    
    @Override
    public IPage<QuestionShareResultDTO> getMyShares(Long userId, int pageNum, int pageSize) {
        log.info("获取我分享的列表: userId={}", userId);
        
        Page<QuestionShare> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<QuestionShare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionShare::getSharerId, userId)
               .eq(QuestionShare::getDeleted, 0)
               .orderByDesc(QuestionShare::getCreateTime);
        
        IPage<QuestionShare> sharePage = shareMapper.selectPage(page, wrapper);
        
        return sharePage.convert(share -> buildShareResultDTO(share, userId));
    }
    
    @Override
    public IPage<QuestionShareResultDTO> getSharesForMe(Long userId, int pageNum, int pageSize) {
        log.info("获取分享给我的列表: userId={}", userId);
        
        // 查询分享给当前用户的记录
        String countSql = "SELECT COUNT(*) FROM question_share qs " +
                         "INNER JOIN question_share_target qst ON qs.share_id = qst.share_id " +
                         "WHERE qst.target_user_id = ? AND qs.deleted = 0 AND qs.status = 1";
        
        String dataSql = "SELECT qs.* FROM question_share qs " +
                        "INNER JOIN question_share_target qst ON qs.share_id = qst.share_id " +
                        "WHERE qst.target_user_id = ? AND qs.deleted = 0 AND qs.status = 1 " +
                        "ORDER BY qs.create_time DESC LIMIT ? OFFSET ?";
        
        Long total = jdbcTemplate.queryForObject(countSql, Long.class, userId);
        if (total == null || total == 0) {
            return new Page<>(pageNum, pageSize);
        }
        
        int offset = (pageNum - 1) * pageSize;
        List<QuestionShare> shares = jdbcTemplate.query(dataSql, 
            (rs, rowNum) -> {
                QuestionShare share = new QuestionShare();
                share.setShareId(rs.getLong("share_id"));
                share.setShareNo(rs.getString("share_no"));
                share.setQuestionId(rs.getLong("question_id"));
                share.setSharerId(rs.getLong("sharer_id"));
                share.setShareType(rs.getInt("share_type"));
                share.setShareTitle(rs.getString("share_title"));
                share.setShareMessage(rs.getString("share_message"));
                share.setIncludeAnswer(rs.getInt("include_answer"));
                share.setIncludeAnalysis(rs.getInt("include_analysis"));
                share.setIncludeNotes(rs.getInt("include_notes"));
                share.setViewCount(rs.getInt("view_count"));
                share.setLikeCount(rs.getInt("like_count"));
                share.setStatus(rs.getInt("status"));
                share.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
                return share;
            },
            userId, pageSize, offset);
        
        Page<QuestionShareResultDTO> resultPage = new Page<>(pageNum, pageSize, total);
        List<QuestionShareResultDTO> resultDTOs = shares.stream()
            .map(share -> buildShareResultDTO(share, userId))
            .collect(Collectors.toList());
        resultPage.setRecords(resultDTOs);
        
        return resultPage;
    }
    
    @Override
    public QuestionShareResultDTO getShareDetail(Long shareId, Long userId) {
        log.info("获取分享详情: shareId={}, userId={}", shareId, userId);
        
        QuestionShare share = shareMapper.selectById(shareId);
        if (share == null || share.getDeleted() == 1) {
            throw new RuntimeException("分享不存在");
        }
        
        // 检查权限
        if (!share.getSharerId().equals(userId)) {
            // 不是分享者，检查是否是目标用户
            if (share.getShareType() == 2) {
                String sql = "SELECT COUNT(*) FROM question_share_target WHERE share_id = ? AND target_user_id = ?";
                Long count = jdbcTemplate.queryForObject(sql, Long.class, shareId, userId);
                if (count == null || count == 0) {
                    throw new RuntimeException("无权查看此分享");
                }
            }
            // 公开分享所有人都可以查看
        }
        
        // 增加浏览次数
        if (share.getShareType() == 2) {
            String sql = "UPDATE question_share_target SET has_viewed = 1, view_time = NOW() WHERE share_id = ? AND target_user_id = ?";
            jdbcTemplate.update(sql, shareId, userId);
        }
        share.setViewCount(share.getViewCount() + 1);
        shareMapper.updateById(share);
        
        return buildShareResultDTO(share, userId);
    }
    
    @Override
    @Transactional
    public Long addSolution(Long shareId, Long userId, String content, String images) {
        log.info("添加解题思路: shareId={}, userId={}", shareId, userId);
        
        QuestionShare share = shareMapper.selectById(shareId);
        if (share == null || share.getDeleted() == 1) {
            throw new RuntimeException("分享不存在");
        }
        
        QuestionShareSolution solution = new QuestionShareSolution();
        solution.setShareId(shareId);
        solution.setUserId(userId);
        solution.setSolutionContent(content);
        solution.setSolutionImages(images);
        solution.setLikeCount(0);
        solution.setIsBest(0);
        
        solutionMapper.insert(solution);
        log.info("解题思路添加成功: solutionId={}", solution.getSolutionId());
        
        return solution.getSolutionId();
    }
    
    @Override
    public List<QuestionShareSolutionDTO> getSolutions(Long shareId) {
        log.info("获取解题思路列表: shareId={}", shareId);
        
        LambdaQueryWrapper<QuestionShareSolution> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionShareSolution::getShareId, shareId)
               .eq(QuestionShareSolution::getDeleted, 0)
               .orderByDesc(QuestionShareSolution::getIsBest)
               .orderByDesc(QuestionShareSolution::getLikeCount)
               .orderByAsc(QuestionShareSolution::getCreateTime);
        
        List<QuestionShareSolution> solutions = solutionMapper.selectList(wrapper);
        
        return solutions.stream().map(solution -> {
            QuestionShareSolutionDTO dto = new QuestionShareSolutionDTO();
            dto.setSolution(solution);
            
            User user = userMapper.selectById(solution.getUserId());
            dto.setUser(user);
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void likeShare(Long shareId, Long userId) {
        log.info("点赞分享: shareId={}, userId={}", shareId, userId);
        
        // 检查是否已点赞
        String checkSql = "SELECT COUNT(*) FROM question_share_interaction WHERE share_id = ? AND user_id = ? AND interaction_type = 1";
        Long count = jdbcTemplate.queryForObject(checkSql, Long.class, shareId, userId);
        
        if (count != null && count > 0) {
            throw new RuntimeException("已经点赞过");
        }
        
        // 添加点赞记录
        String insertSql = "INSERT INTO question_share_interaction (share_id, user_id, interaction_type) VALUES (?, ?, 1)";
        jdbcTemplate.update(insertSql, shareId, userId);
        
        // 更新点赞数
        shareMapper.incrementLikeCount(shareId);
        
        log.info("点赞成功");
    }
    
    @Override
    @Transactional
    public void revokeShare(Long shareId, Long userId) {
        log.info("撤回分享: shareId={}, userId={}", shareId, userId);
        
        QuestionShare share = shareMapper.selectById(shareId);
        if (share == null || !share.getSharerId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }
        
        share.setStatus(0); // 0-已撤回
        shareMapper.updateById(share);
        
        log.info("分享已撤回");
    }
    
    @Override
    public List<User> getUserList(String keyword, Long excludeUserId) {
        log.info("获取用户列表: keyword={}, excludeUserId={}", keyword, excludeUserId);
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(User::getUserId, excludeUserId);
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(User::getUsername, keyword)
                   .or()
                   .like(User::getNickname, keyword);
        }
        
        wrapper.last("LIMIT 50");
        
        return userMapper.selectList(wrapper);
    }
    
    /**
     * 构建分享结果DTO
     */
    private QuestionShareResultDTO buildShareResultDTO(QuestionShare share, Long currentUserId) {
        QuestionShareResultDTO dto = new QuestionShareResultDTO();
        dto.setShare(share);
        
        // 获取分享者信息
        User sharer = userMapper.selectById(share.getSharerId());
        dto.setSharer(sharer);
        
        // 获取错题信息
        ErrorQuestion question = questionMapper.selectById(share.getQuestionId());
        dto.setQuestion(question);
        
        // 统计解题思路数量
        LambdaQueryWrapper<QuestionShareSolution> solutionWrapper = new LambdaQueryWrapper<>();
        solutionWrapper.eq(QuestionShareSolution::getShareId, share.getShareId())
                      .eq(QuestionShareSolution::getDeleted, 0);
        Long count = solutionMapper.selectCount(solutionWrapper);
        dto.setSolutionCount(count.intValue());
        
        // 检查当前用户是否已点赞
        String checkSql = "SELECT COUNT(*) FROM question_share_interaction WHERE share_id = ? AND user_id = ? AND interaction_type = 1";
        Long likeCount = jdbcTemplate.queryForObject(checkSql, Long.class, share.getShareId(), currentUserId);
        dto.setHasLiked(likeCount != null && likeCount > 0);
        
        // 获取解题思路列表
        dto.setSolutions(getSolutions(share.getShareId()));
        
        return dto;
    }
    
    /**
     * 生成分享编号
     */
    private String generateShareNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int)(Math.random() * 10000);
        return "SHARE" + dateStr + String.format("%04d", random);
    }
}
