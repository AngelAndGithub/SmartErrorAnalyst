<template>
  <div class="question-detail">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
            <span class="title">错题详情</span>
          </div>
          <div class="header-right">
            <el-button type="primary" @click="handleShare" :icon="Share">
              分享
            </el-button>
            <el-button type="warning" @click="handleEdit" :icon="Edit">
              编辑
            </el-button>
          </div>
        </div>
      </template>

      <div v-if="question" class="detail-content">
        <!-- 基本信息 -->
        <el-descriptions :column="2" border class="basic-info">
          <el-descriptions-item label="学科">
            {{ getSubjectName(question.subjectId) }}
          </el-descriptions-item>
          <el-descriptions-item label="知识点">
            {{ getKnowledgeName(question.knowledgeId) || '未设置' }}
          </el-descriptions-item>
          <el-descriptions-item label="题目类型">
            <el-tag :type="getQuestionTypeType(question.questionType)">
              {{ getQuestionTypeText(question.questionType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="难度">
            <el-tag :type="getDifficultyType(question.difficulty)">
              {{ getDifficultyText(question.difficulty) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="错误原因">
            {{ question.errorReason || '未记录' }}
          </el-descriptions-item>
          <el-descriptions-item label="掌握状态">
            <el-tag :type="question.masteryStatus === 1 ? 'success' : 'warning'">
              {{ question.masteryStatus === 1 ? '已掌握' : '未掌握' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="录入时间" :span="2">
            {{ question.createTime }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 题目内容 -->
        <div class="section">
          <h3 class="section-title">
            <el-icon><Document /></el-icon>
            题目内容
          </h3>
          <div class="content-box">
            <MathPreview :content="question.questionContent" />
          </div>
          <!-- 题目图片 -->
          <div v-if="questionImages.length > 0" class="image-gallery">
            <el-image
              v-for="(img, index) in questionImages"
              :key="index"
              :src="img"
              fit="cover"
              :preview-src-list="questionImages"
              :initial-index="index"
              class="gallery-image"
            />
          </div>
        </div>

        <!-- 正确答案 -->
        <div class="section">
          <h3 class="section-title correct">
            <el-icon><CircleCheck /></el-icon>
            正确答案
          </h3>
          <div class="content-box correct-answer">
            <MathPreview :content="question.correctAnswer" />
          </div>
          <!-- 答案图片 -->
          <div v-if="answerImages.length > 0" class="image-gallery">
            <el-image
              v-for="(img, index) in answerImages"
              :key="index"
              :src="img"
              fit="cover"
              :preview-src-list="answerImages"
              :initial-index="index"
              class="gallery-image"
            />
          </div>
        </div>

        <!-- 用户答案 -->
        <div class="section">
          <h3 class="section-title wrong">
            <el-icon><CircleClose /></el-icon>
            我的答案
          </h3>
          <div class="content-box user-answer">
            <MathPreview :content="question.userAnswer" />
          </div>
          <!-- 用户答案图片 -->
          <div v-if="userAnswerImages.length > 0" class="image-gallery">
            <el-image
              v-for="(img, index) in userAnswerImages"
              :key="index"
              :src="img"
              fit="cover"
              :preview-src-list="userAnswerImages"
              :initial-index="index"
              class="gallery-image"
            />
          </div>
        </div>

        <!-- 解析 -->
        <div v-if="question.analysis" class="section">
          <h3 class="section-title">
            <el-icon><Reading /></el-icon>
            解析
          </h3>
          <div class="content-box">
            <MathPreview :content="question.analysis" />
          </div>
        </div>

        <!-- 笔记 -->
        <div v-if="question.notes" class="section">
          <h3 class="section-title">
            <el-icon><Notebook /></el-icon>
            笔记
          </h3>
          <div class="content-box notes">
            <MathPreview :content="question.notes" />
          </div>
        </div>

        <!-- 标签 -->
        <div v-if="question.tags" class="section">
          <h3 class="section-title">
            <el-icon><PriceTag /></el-icon>
            标签
          </h3>
          <div class="tags">
            <el-tag
              v-for="tag in question.tags.split(',')"
              :key="tag"
              class="tag-item"
              type="info"
            >
              {{ tag.trim() }}
            </el-tag>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 分享对话框 -->
    <el-dialog v-model="shareDialogVisible" title="分享错题" width="500px">
      <div class="share-content">
        <p class="share-tip">
          <el-icon><InfoFilled /></el-icon>
          复制以下链接分享给他人：
        </p>
        <div class="share-link-box">
          <el-input v-model="shareLink" readonly>
            <template #append>
              <el-button @click="copyShareLink">
                <el-icon><CopyDocument /></el-icon>
                复制
              </el-button>
            </template>
          </el-input>
        </div>
        
        <div class="share-options">
          <h4>分享选项</h4>
          <el-checkbox v-model="shareOptions.includeAnswer">包含答案</el-checkbox>
          <el-checkbox v-model="shareOptions.includeAnalysis">包含解析</el-checkbox>
          <el-checkbox v-model="shareOptions.includeNotes">包含笔记</el-checkbox>
        </div>

        <el-alert
          title="注意"
          type="warning"
          description="分享链接仅包含题目基本信息，他人查看时不会记录到您的账号。"
          :closable="false"
          show-icon
          class="share-notice"
        />
      </div>
      <template #footer>
        <el-button @click="shareDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Share, Edit, Document, CircleCheck, CircleClose, Reading, Notebook, PriceTag, InfoFilled, CopyDocument } from '@element-plus/icons-vue'
import { getErrorQuestionDetail } from '@/api/error-question'
import { listSubjects, getKnowledgeBySubject } from '@/api/subject'
import MathPreview from '@/components/MathPreview.vue'
import type { ErrorQuestion } from '@/api/error-question'
import type { Subject, Knowledge } from '@/api/subject'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const question = ref<ErrorQuestion | null>(null)
const subjects = ref<Subject[]>([])
const knowledgeList = ref<Knowledge[]>([])

// 图片数组
const questionImages = ref<string[]>([])
const answerImages = ref<string[]>([])
const userAnswerImages = ref<string[]>([])

// 分享相关
const shareDialogVisible = ref(false)
const shareLink = ref('')
const shareOptions = ref({
  includeAnswer: true,
  includeAnalysis: true,
  includeNotes: false
})

onMounted(async () => {
  const questionId = route.params.id as string
  if (!questionId) {
    ElMessage.error('错题ID不存在')
    goBack()
    return
  }

  await loadSubjects()
  await loadQuestionDetail(Number(questionId))
})

const loadSubjects = async () => {
  try {
    subjects.value = await listSubjects()
  } catch (error) {
    console.error('加载学科列表失败:', error)
  }
}

const loadQuestionDetail = async (id: number) => {
  loading.value = true
  try {
    const detail = await getErrorQuestionDetail(id)
    question.value = detail

    // 加载对应学科的知识点
    if (detail.subjectId) {
      knowledgeList.value = await getKnowledgeBySubject(detail.subjectId)
    }

    // 解析图片
    if (detail.questionImages) {
      try {
        questionImages.value = JSON.parse(detail.questionImages)
      } catch (e) {
        questionImages.value = detail.questionImages ? [detail.questionImages] : []
      }
    }

    if (detail.answerImages) {
      try {
        answerImages.value = JSON.parse(detail.answerImages)
      } catch (e) {
        answerImages.value = detail.answerImages ? [detail.answerImages] : []
      }
    }

    if (detail.userAnswerImages) {
      try {
        userAnswerImages.value = JSON.parse(detail.userAnswerImages)
      } catch (e) {
        userAnswerImages.value = detail.userAnswerImages ? [detail.userAnswerImages] : []
      }
    }
  } catch (error) {
    ElMessage.error('加载错题详情失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getSubjectName = (subjectId: number) => {
  const subject = subjects.value.find(s => s.subjectId === subjectId)
  return subject?.subjectName || '-'
}

const getKnowledgeName = (knowledgeId: number | undefined) => {
  if (!knowledgeId) return ''
  const knowledge = knowledgeList.value.find(k => k.knowledgeId === knowledgeId)
  return knowledge?.knowledgeName || ''
}

const getQuestionTypeText = (type: number) => {
  const map: Record<number, string> = { 1: '单选题', 2: '多选题', 3: '填空题', 4: '解答题' }
  return map[type] || '-'
}

const getQuestionTypeType = (type: number) => {
  const map: Record<number, string> = { 1: 'primary', 2: 'success', 3: 'warning', 4: 'info' }
  return map[type] || ''
}

const getDifficultyText = (difficulty: number) => {
  const map: Record<number, string> = { 1: '简单', 2: '中等', 3: '困难' }
  return map[difficulty] || '-'
}

const getDifficultyType = (difficulty: number) => {
  const map: Record<number, string> = { 1: 'success', 2: 'warning', 3: 'danger' }
  return map[difficulty] || ''
}

const goBack = () => {
  router.back()
}

const handleEdit = () => {
  if (question.value?.questionId) {
    router.push(`/error-question/create?id=${question.value.questionId}`)
  }
}

const handleShare = () => {
  // 生成分享链接
  const questionId = question.value?.questionId
  if (!questionId) return

  // 构建分享链接（包含分享选项）
  const params = new URLSearchParams({
    id: String(questionId),
    answer: String(shareOptions.value.includeAnswer ? 1 : 0),
    analysis: String(shareOptions.value.includeAnalysis ? 1 : 0),
    notes: String(shareOptions.value.includeNotes ? 1 : 0)
  })

  shareLink.value = `${window.location.origin}/shared-question?${params.toString()}`
  shareDialogVisible.value = true
}

const copyShareLink = async () => {
  try {
    await navigator.clipboard.writeText(shareLink.value)
    ElMessage.success('链接已复制到剪贴板')
  } catch (error) {
    ElMessage.error('复制失败，请手动复制')
  }
}
</script>

<style scoped>
.question-detail {
  padding: 0;
}

.question-detail :deep(.el-card) {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  border: none;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-left .title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  display: flex;
  gap: 12px;
}

.detail-content {
  max-width: 1000px;
  margin: 0 auto;
}

.basic-info {
  margin-bottom: 24px;
}

.section {
  margin-bottom: 32px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 2px solid #409EFF;
}

.section-title.correct {
  border-bottom-color: #67C23A;
  color: #67C23A;
}

.section-title.wrong {
  border-bottom-color: #F56C6C;
  color: #F56C6C;
}

.content-box {
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 6px;
  border: 1px solid #ebeef5;
  line-height: 1.8;
  font-size: 15px;
}

.content-box.correct-answer {
  background-color: #f0f9ff;
  border-color: #b3d8ff;
}

.content-box.user-answer {
  background-color: #fef0f0;
  border-color: #fbc4c4;
}

.content-box.notes {
  background-color: #fdf6ec;
  border-color: #faecd8;
}

.image-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.gallery-image {
  width: 100%;
  height: 200px;
  border-radius: 6px;
  cursor: pointer;
  border: 1px solid #ebeef5;
}

.gallery-image :deep(.el-image__inner) {
  object-fit: cover;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  margin: 0;
}

/* 分享对话框样式 */
.share-content {
  padding: 8px 0;
}

.share-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  color: #606266;
}

.share-link-box {
  margin-bottom: 20px;
}

.share-options {
  margin-bottom: 20px;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 6px;
}

.share-options h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #303133;
}

.share-options .el-checkbox {
  display: block;
  margin-bottom: 8px;
}

.share-notice {
  margin-top: 16px;
}

@media (max-width: 768px) {
  .image-gallery {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }

  .gallery-image {
    height: 150px;
  }

  .card-header {
    flex-direction: column;
    gap: 12px;
  }

  .header-right {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
