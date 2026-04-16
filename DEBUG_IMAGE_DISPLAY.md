# 图片回显问题调试指南

## 🔍 调试步骤

### 第一步：检查浏览器控制台

1. 打开浏览器开发者工具（F12）
2. 进入 Console（控制台）标签
3. 进入编辑错题页面
4. 查看控制台输出，应该看到类似以下内容：

```
=== 加载错题详情 ===
detail: { questionId: 1, subjectId: 1, ..., questionImages: '["url1", "url2"]', ... }
questionImages: '["http://localhost:8080/api/files/2026/04/16/abc123.png"]'
answerImages: null
userAnswerImages: null
题目图片解析成功: ['http://localhost:8080/api/files/2026/04/16/abc123.png']
最终图片数组:
questionImages: ['http://localhost:8080/api/files/2026/04/16/abc123.png']
answerImages: []
userAnswerImages: []
```

### 第二步：根据控制台输出判断问题

#### 情况1：detail中没有questionImages字段
```
detail: { questionId: 1, ..., questionContent: '...' }  // 没有questionImages
questionImages: undefined
```

**原因**：后端没有返回图片字段
**解决**：
1. 检查数据库是否有数据
2. 检查后端Controller是否正确返回

#### 情况2：questionImages为null或空字符串
```
questionImages: null
```
或
```
questionImages: ''
```

**原因**：数据库中没有保存图片数据
**解决**：
1. 确认保存时是否成功保存图片
2. 检查数据库中的实际数据

#### 情况3：JSON解析失败
```
题目图片作为单个URL处理: ['http://...']
```

**原因**：数据不是JSON数组格式，是单个URL字符串
**解决**：代码已经做了兼容处理，应该能正常显示

#### 情况4：解析成功但页面不显示
```
题目图片解析成功: ['http://localhost:8080/api/files/2026/04/16/abc123.png']
最终图片数组:
questionImages: ['http://localhost:8080/api/files/2026/04/16/abc123.png']
```
但页面看不到图片

**原因**：可能是图片URL无效或CSS样式问题
**解决**：
1. 在浏览器地址栏直接访问图片URL，看能否打开
2. 检查 Elements（元素）标签，看`.uploaded-images`是否存在
3. 检查是否有CSS样式导致图片隐藏

### 第三步：检查数据库

运行以下SQL查询，查看实际存储的数据：

```sql
SELECT 
    question_id,
    question_content,
    question_images,
    answer_images,
    user_answer_images
FROM error_question 
WHERE question_id = 你的错题ID;
```

**期望结果**：
- `question_images` 应该是类似 `["http://localhost:8080/api/files/2026/04/16/abc.png"]` 的JSON字符串
- 如果是 `NULL` 或空字符串，说明保存时没有保存图片

### 第四步：检查网络请求

1. 打开开发者工具的 Network（网络）标签
2. 刷新编辑页面
3. 找到 `GET /api/error-questions/{id}` 请求
4. 查看 Response（响应）内容
5. 确认是否包含图片字段

**期望响应**：
```json
{
  "code": 200,
  "data": {
    "questionId": 1,
    "questionContent": "...",
    "questionImages": "[\"http://localhost:8080/api/files/2026/04/16/abc.png\"]",
    "answerImages": null,
    "userAnswerImages": null,
    ...
  }
}
```

## 🐛 常见问题和解决方案

### 问题1：数据库中没有图片字段

**症状**：
- SQL查询显示 `question_images` 为 NULL
- 控制台输出 `questionImages: undefined` 或 `null`

**解决方案**：
运行数据库修复脚本：
```bash
# 方法1：使用自动修复（重启后端服务）
# DatabaseSchemaFixer 会自动添加缺失的字段

# 方法2：手动执行SQL
mysql -u root -p your_database < database/fix_error_question_table.sql
```

### 问题2：图片URL格式不正确

**症状**：
- 控制台显示解析成功
- 但图片显示为空白或404

**解决方案**：
1. 检查URL是否可访问：直接在浏览器中打开URL
2. 检查URL格式：
   - 正确：`http://localhost:8080/api/files/2026/04/16/abc.png`
   - 错误：`files/2026/04/16/abc.png`（缺少协议和域名）
   - 错误：`/files/2026/04/16/abc.png`（缺少协议和域名）

### 问题3：JSON格式错误

**症状**：
- 控制台显示解析失败
- 错误信息：`SyntaxError: Unexpected token ... in JSON`

**解决方案**：
代码已经做了容错处理，会尝试作为单个URL处理。如果仍然不行：
1. 检查数据库中存储的格式
2. 手动修正为正确的JSON格式：
```sql
UPDATE error_question 
SET question_images = '["http://localhost:8080/api/files/2026/04/16/abc.png"]'
WHERE question_id = 你的ID;
```

### 问题4：图片显示但样式不对

**症状**：
- 图片存在但看不到
- 或者显示很小/很大

**解决方案**：
检查CSS样式，确保以下样式存在：
```css
.uploaded-images {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

.thumb-item {
  width: 100px;
  height: 100px;
  position: relative;
}

.thumb-item .el-image {
  width: 100%;
  height: 100%;
  border-radius: 4px;
  cursor: pointer;
}
```

## 📋 完整检查清单

- [ ] 1. 数据库中存在图片字段（question_images等）
- [ ] 2. 数据库字段中有正确的JSON数据
- [ ] 3. 后端API返回了图片字段
- [ ] 4. 前端控制台显示解析成功
- [ ] 5. 图片数组中有URL
- [ ] 6. 图片URL可以直接访问
- [ ] 7. 页面DOM中存在 `.uploaded-images` 元素
- [ ] 8. CSS样式正常，图片没有被隐藏

## 🎯 快速测试

在浏览器控制台中执行以下代码，手动检查图片数据：

```javascript
// 1. 检查全局变量
console.log('questionImages:', window.questionImages)

// 2. 检查DOM元素
console.log('图片容器:', document.querySelector('.uploaded-images'))

// 3. 检查图片元素
console.log('图片元素:', document.querySelectorAll('.thumb-item'))

// 4. 检查图片src
document.querySelectorAll('.thumb-item .el-image img').forEach(img => {
  console.log('图片URL:', img.src)
})
```

## 📞 如果问题仍然存在

请提供以下信息：

1. **控制台输出**（完整的日志）
2. **数据库查询结果**（question_images字段的值）
3. **网络请求响应**（API返回的JSON数据）
4. **页面截图**（显示问题和开发者工具）
5. **错题ID**（方便排查具体数据）

---

**创建时间**: 2026-04-16
