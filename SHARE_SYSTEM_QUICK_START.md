# 错题分享系统 - 快速实现指南

## 📦 已完成的工作

### ✅ 数据库
- [x] 4个数据表设计完成 (`database/question_share.sql`)

### ✅ 后端基础
- [x] 实体类：`QuestionShare`, `QuestionShareSolution`
- [x] Mapper：`QuestionShareMapper`, `QuestionShareSolutionMapper`
- [x] DTO：`QuestionShareDTO`, `QuestionShareResultDTO`, `QuestionShareSolutionDTO`

### ✅ 前端基础
- [x] API接口定义 (`frontend/src/api/share.ts`)

## 🚀 快速实现步骤

### 第一步：初始化数据库

```bash
# 进入数据库
mysql -u root -p your_database

# 执行SQL脚本
source e:/workspace/SmartErrorAnalyst/database/question_share.sql
```

### 第二步：创建后端Service

创建文件：`backend/src/main/java/com/errorquest/service/QuestionShareService.java`

```java
package com.errorquest.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.errorquest.dto.QuestionShareDTO;
import com.errorquest.dto.QuestionShareResultDTO;
import com.errorquest.dto.QuestionShareSolutionDTO;

import java.util.List;

public interface QuestionShareService {
    String createShare(Long userId, QuestionShareDTO dto);
    IPage<QuestionShareResultDTO> getMyShares(Long userId, int pageNum, int pageSize);
    IPage<QuestionShareResultDTO> getSharesForMe(Long userId, int pageNum, int pageSize);
    QuestionShareResultDTO getShareDetail(Long shareId, Long userId);
    Long addSolution(Long shareId, Long userId, String content, String images);
    List<QuestionShareSolutionDTO> getSolutions(Long shareId);
    void likeShare(Long shareId, Long userId);
    void revokeShare(Long shareId, Long userId);
}
```

创建文件：`backend/src/main/java/com/errorquest/service/impl/QuestionShareServiceImpl.java`

由于代码较长，关键方法实现如下：

```java
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
    
    @Override
    @Transactional
    public String createShare(Long userId, QuestionShareDTO dto) {
        // 1. 创建分享记录
        QuestionShare share = new QuestionShare();
        share.setShareNo(generateShareNo());
        share.setQuestionId(dto.getQuestionId());
        share.setSharerId(userId);
        share.setShareType(dto.getShareType());
        share.setShareTitle(dto.getShareTitle());
        share.setShareMessage(dto.getShareMessage());
        share.setIncludeAnswer(dto.getIncludeAnswer() != null ? dto.getIncludeAnswer() : 1);
        share.setIncludeAnalysis(dto.getIncludeAnalysis() != null ? dto.getIncludeAnalysis() : 1);
        share.setIncludeNotes(dto.getIncludeNotes() != null ? dto.getIncludeNotes() : 0);
        share.setViewCount(0);
        share.setLikeCount(0);
        share.setStatus(1);
        
        shareMapper.insert(share);
        
        // 2. 如果是指定分享，添加目标用户
        if (dto.getShareType() == 2 && dto.getTargetUserIds() != null) {
            for (Long targetUserId : dto.getTargetUserIds()) {
                // 插入到question_share_target表
                // 使用JdbcTemplate或创建对应的Mapper
            }
        }
        
        return share.getShareNo();
    }
    
    @Override
    public IPage<QuestionShareResultDTO> getMyShares(Long userId, int pageNum, int pageSize) {
        Page<QuestionShare> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<QuestionShare> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionShare::getSharerId, userId)
               .eq(QuestionShare::getStatus, 1)
               .orderByDesc(QuestionShare::getCreateTime);
        
        IPage<QuestionShare> sharePage = shareMapper.selectPage(page, wrapper);
        
        // 转换为DTO
        return sharePage.convert(share -> {
            QuestionShareResultDTO resultDTO = new QuestionShareResultDTO();
            resultDTO.setShare(share);
            
            // 获取错题信息
            ErrorQuestion question = questionMapper.selectById(share.getQuestionId());
            resultDTO.setQuestion(question);
            
            // 获取分享者信息
            User sharer = userMapper.selectById(userId);
            resultDTO.setSharer(sharer);
            
            // 统计解题思路数量
            LambdaQueryWrapper<QuestionShareSolution> solutionWrapper = new LambdaQueryWrapper<>();
            solutionWrapper.eq(QuestionShareSolution::getShareId, share.getShareId());
            Integer count = solutionMapper.selectCount(solutionWrapper);
            resultDTO.setSolutionCount(count);
            
            return resultDTO;
        });
    }
    
    // 其他方法实现...
    
    private String generateShareNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int)(Math.random() * 10000);
        return "SHARE" + dateStr + String.format("%04d", random);
    }
}
```

### 第三步：创建后端Controller

创建文件：`backend/src/main/java/com/errorquest/controller/QuestionShareController.java`

```java
package com.errorquest.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.errorquest.dto.*;
import com.errorquest.service.QuestionShareService;
import com.errorquest.utils.JwtUtil;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shares")
@CrossOrigin(origins = "http://localhost:5173")
public class QuestionShareController {
    
    @Autowired
    private QuestionShareService shareService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        throw new RuntimeException("未登录");
    }
    
    @PostMapping
    public Result<String> createShare(@RequestBody QuestionShareDTO dto, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            String shareNo = shareService.createShare(userId, dto);
            return Result.success(shareNo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/my")
    public Result<IPage<QuestionShareResultDTO>> getMyShares(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            return Result.success(shareService.getMyShares(userId, pageNum, pageSize));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/received")
    public Result<IPage<QuestionShareResultDTO>> getReceivedShares(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            return Result.success(shareService.getSharesForMe(userId, pageNum, pageSize));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/{shareId}")
    public Result<QuestionShareResultDTO> getShareDetail(
            @PathVariable Long shareId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            return Result.success(shareService.getShareDetail(shareId, userId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/{shareId}/solution")
    public Result<Long> addSolution(
            @PathVariable Long shareId,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            String content = body.get("content");
            String images = body.get("images");
            Long solutionId = shareService.addSolution(shareId, userId, content, images);
            return Result.success(solutionId);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/{shareId}/solutions")
    public Result<List<QuestionShareSolutionDTO>> getSolutions(@PathVariable Long shareId) {
        try {
            return Result.success(shareService.getSolutions(shareId));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/{shareId}/like")
    public Result<Void> likeShare(
            @PathVariable Long shareId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            shareService.likeShare(shareId, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{shareId}")
    public Result<Void> revokeShare(
            @PathVariable Long shareId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            shareService.revokeShare(shareId, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
```

## 📝 下一步

由于完整的后端和前端代码量很大（预计5000+行），建议分阶段实现：

### Phase 1: 核心功能（1-2天）
1. ✅ 数据库表
2. ✅ 实体类、Mapper、DTO
3. ⏳ 完成Service实现
4. ⏳ 完成Controller实现
5. ⏳ 创建基础前端页面

### Phase 2: 前端页面（2-3天）
1. 分享对话框组件
2. 我的分享页面
3. 收到的分享页面
4. 分享详情页面

### Phase 3: 互动功能（1-2天）
1. 解题思路提交
2. 点赞功能
3. 用户搜索和选择

## 💡 建议

鉴于这是一个大型功能，我建议：

1. **先实现最小可用版本**
   - 只支持公开分享
   - 基本的分享列表和详情
   - 简单的解题思路功能

2. **逐步增强**
   - 添加指定分享
   - 添加互动功能
   - 优化UI/UX

3. **测试驱动**
   - 每个功能完成后立即测试
   - 确保前后端联调正常

需要我继续实现哪个部分？我可以：
- A. 完成完整的后端Service实现
- B. 创建前端分享对话框组件
- C. 创建前端分享列表页面
- D. 其他特定功能

请告诉我您希望先从哪个部分开始！
