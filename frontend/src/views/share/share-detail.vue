<template>
  <div class="share-detail" v-loading="loading">
    <el-card v-if="shareDetail">
      <template #header>
        <div class="card-header">
          <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
          <span class="title">分享详情</span>
        </div>
      </template>

      <!-- 分享信息 -->
      <el-descriptions :column="2" border class="share-info">
        <el-descriptions-item label="分享者">
          {{ shareDetail.sharer?.nickname || shareDetail.sharer?.username }}
        </el-descriptions-item>
        <el-descriptions-item label="分享时间">
          {{ shareDetail.share.createTime }}
        </el-descriptions-item>
        <el-descriptions-item label="浏览次数">
          {{ shareDetail.share.viewCount }}
        </el-descriptions-item>
        <el-descriptions-item label="点赞数">
          {{ shareDetail.share.likeCount }}
          <el-button type="primary" link size="small" @click="handleLike" :disabled="shareDetail.hasLiked">
            {{ shareDetail.hasLiked ? '已点赞' : '点赞' }}
          </el-button>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 题目内容 -->
      <div class="section">
        <h3 class="section-title">题目内容</h3>
        <div class="content-box">
          <MathPreview :content="shareDetail.question?.questionContent || ''" />
        </div>
      </div>

      <!-- 正确答案 -->
      <div v-if="shareDetail.share.includeAnswer === 1" class="section">
        <h3 class="section-title correct">正确答案</h3>
        <div class="content-box correct-answer">
          <MathPreview :content="shareDetail.question?.correctAnswer || ''" />
        </div>
      </div>

      <!-- 解析 -->
      <div v-if="shareDetail.share.includeAnalysis === 1 && shareDetail.question?.analysis" class="section">
        <h3 class="section-title">解析</h3>
        <div class="content-box">
          <MathPreview :content="shareDetail.question.analysis" />
        </div>
      </div>

      <!-- 解题思路 -->
      <div class="section">
        <div class="section-header">
          <h3 class="section-title">解题思路 ({{ solutions.length }})</h3>
          <el-button type="primary" size="small" @click="showSolutionDialog = true">
            添加我的解题思路
          </el-button>
        </div>

        <el-empty v-if="solutions.length === 0" description="暂无解题思路，快来添加吧！" />
        
        <div v-for="sol in solutions" :key="sol.solution.solutionId" class="solution-item">
          <el-card shadow="hover">
            <div class="solution-header">
              <div class="user-info">
                <el-avatar :size="40">{{ sol.user?.username?.charAt(0) }}</el-avatar>
                <div class="user-meta">
                  <div class="username">{{ sol.user?.nickname || sol.user?.username }}</div>
                  <div class="time">{{ sol.solution.createTime }}</div>
                </div>
              </div>
              <el-tag v-if="sol.solution.isBest === 1" type="success" size="small">最佳解题</el-tag>
            </div>
            <div class="solution-content">
              <MathPreview :content="sol.solution.solutionContent" />
            </div>
          </el-card>
        </div>
      </div>
    </el-card>

    <!-- 添加解题思路对话框 -->
    <el-dialog v-model="showSolutionDialog" title="添加解题思路" width="700px">
      <el-form>
        <el-form-item label="解题思路" required>
          <el-input
            v-model="solutionContent"
            type="textarea"
            :rows="8"
            placeholder="写下你的解题思路，支持LaTeX公式..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showSolutionDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitSolution">
          提交
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getShareDetail, addSolution, likeShare } from '@/api/share'
import type { ShareResultDTO, ShareSolution } from '@/api/share'
import MathPreview from '@/components/MathPreview.vue'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const shareDetail = ref<ShareResultDTO | null>(null)
const solutions = ref<any[]>([])
const showSolutionDialog = ref(false)
const solutionContent = ref('')
const submitting = ref(false)

onMounted(async () => {
  const shareId = Number(route.params.shareId)
  if (!shareId) {
    ElMessage.error('分享ID无效')
    goBack()
    return
  }
  await loadShareDetail(shareId)
})

const loadShareDetail = async (shareId: number) => {
  loading.value = true
  try {
    shareDetail.value = await getShareDetail(shareId)
    solutions.value = shareDetail.value?.solutions || []
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const handleLike = async () => {
  if (!shareDetail.value) return
  try {
    await likeShare(shareDetail.value.share.shareId!)
    ElMessage.success('点赞成功')
    shareDetail.value.hasLiked = true
    shareDetail.value.share.likeCount = (shareDetail.value.share.likeCount || 0) + 1
  } catch (error: any) {
    ElMessage.error(error.message || '点赞失败')
  }
}

const submitSolution = async () => {
  if (!solutionContent.value.trim()) {
    ElMessage.warning('请输入解题思路')
    return
  }

  submitting.value = true
  try {
    await addSolution(shareDetail.value!.share.shareId!, {
      content: solutionContent.value,
      images: undefined
    })
    ElMessage.success('提交成功')
    showSolutionDialog.value = false
    solutionContent.value = ''
    await loadShareDetail(shareDetail.value!.share.shareId!)
  } catch (error: any) {
    ElMessage.error(error.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.share-detail {
  padding: 0;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 16px;
}

.card-header .title {
  font-size: 18px;
  font-weight: 600;
}

.share-info {
  margin-bottom: 24px;
}

.section {
  margin-bottom: 32px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #409EFF;
}

.section-title.correct {
  border-bottom-color: #67C23A;
  color: #67C23A;
}

.content-box {
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 6px;
  line-height: 1.8;
}

.content-box.correct-answer {
  background-color: #f0f9ff;
  border: 1px solid #b3d8ff;
}

.solution-item {
  margin-bottom: 16px;
}

.solution-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.username {
  font-weight: 600;
  color: #303133;
}

.time {
  font-size: 12px;
  color: #909399;
}

.solution-content {
  padding: 12px;
  background-color: #fafbfc;
  border-radius: 4px;
  line-height: 1.8;
}
</style>
