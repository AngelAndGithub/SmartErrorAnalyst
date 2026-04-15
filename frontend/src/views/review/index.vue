<template>
  <div class="review">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>今日复习计划</span>
          <el-tag v-if="todayCount > 0" type="danger">{{ todayCount }} 道待复习</el-tag>
          <el-tag v-else type="success">今日复习完成</el-tag>
        </div>
      </template>
      
      <!-- 复习提醒 -->
      <el-alert
        v-if="todayCount > 0"
        :title="`今天有 ${todayCount} 道错题需要复习`"
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 20px"
      />
      
      <!-- 今日复习列表 -->
      <el-empty v-if="todayPlans.length === 0" description="今天没有需要复习的错题" />
      
      <div v-else class="review-list">
        <el-card
          v-for="plan in todayPlans"
          :key="plan.planId"
          class="review-item"
          shadow="hover"
        >
          <div class="review-content">
            <div class="question-preview">
              <h4>错题 #{{ plan.questionId }}</h4>
              <p class="plan-time">计划复习时间: {{ plan.planTime }}</p>
            </div>
            <div class="review-actions">
              <el-button type="primary" @click="startReview(plan)">
                开始复习
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
    </el-card>
    
    <!-- 复习弹窗 -->
    <el-dialog
      v-model="reviewDialogVisible"
      title="错题复习"
      width="800px"
      :close-on-click-modal="false"
    >
      <div v-if="currentQuestion" class="review-dialog">
        <!-- 自测模式 -->
        <div v-if="reviewMode === 'test'" class="test-mode">
          <div class="question-section">
            <h4>题目内容</h4>
            <div class="content-box">{{ currentQuestion.questionContent }}</div>
          </div>
          
          <div class="answer-section">
            <h4>你的答案</h4>
            <div class="content-box">{{ currentQuestion.userAnswer }}</div>
          </div>
          
          <div class="action-section">
            <p>请回忆正确答案，然后点击查看答案</p>
            <el-button type="primary" @click="showAnswer">查看答案</el-button>
          </div>
        </div>
        
        <!-- 解析模式 -->
        <div v-else class="analysis-mode">
          <div class="question-section">
            <h4>题目内容</h4>
            <div class="content-box">{{ currentQuestion.questionContent }}</div>
          </div>
          
          <div class="answer-section">
            <h4>正确答案</h4>
            <div class="content-box correct">{{ currentQuestion.correctAnswer }}</div>
          </div>
          
          <div class="answer-section">
            <h4>你的答案</h4>
            <div class="content-box" :class="{ wrong: true }">{{ currentQuestion.userAnswer }}</div>
          </div>
          
          <div v-if="currentQuestion.analysis" class="analysis-section">
            <h4>解析</h4>
            <div class="content-box">{{ currentQuestion.analysis }}</div>
          </div>
          
          <div class="result-section">
            <p>这次复习结果如何？</p>
            <el-radio-group v-model="reviewResult">
              <el-radio :label="1">做对了</el-radio>
              <el-radio :label="2">还是错了</el-radio>
            </el-radio-group>
          </div>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button v-if="reviewMode === 'analysis'" @click="reviewMode = 'test'">返回</el-button>
          <el-button v-if="reviewMode === 'analysis'" type="primary" @click="submitReview" :disabled="!reviewResult">
            提交结果
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTodayPlans, countTodayPlans, getReviewQuestion, submitReviewResult } from '@/api/review'
import type { ReviewPlan } from '@/api/review'
import type { ErrorQuestion } from '@/api/error-question'

const todayPlans = ref<ReviewPlan[]>([])
const todayCount = ref(0)
const reviewDialogVisible = ref(false)
const reviewMode = ref<'test' | 'analysis'>('test')
const currentPlan = ref<ReviewPlan>()
const currentQuestion = ref<ErrorQuestion>()
const reviewResult = ref<number>()

onMounted(() => {
  loadTodayPlans()
  loadTodayCount()
})

const loadTodayPlans = async () => {
  try {
    const res = await getTodayPlans()
    todayPlans.value = res
  } catch (error) {
    console.error(error)
  }
}

const loadTodayCount = async () => {
  try {
    const res = await countTodayPlans()
    todayCount.value = res
  } catch (error) {
    console.error(error)
  }
}

const startReview = async (plan: ReviewPlan) => {
  currentPlan.value = plan
  reviewMode.value = 'test'
  reviewResult.value = undefined
  
  try {
    const question = await getReviewQuestion(plan.planId)
    currentQuestion.value = question
    reviewDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载错题失败')
  }
}

const showAnswer = () => {
  reviewMode.value = 'analysis'
}

const submitReview = async () => {
  if (!currentPlan.value || !reviewResult.value) return
  
  try {
    await submitReviewResult({
      planId: currentPlan.value.planId,
      reviewResult: reviewResult.value
    })
    
    ElMessage.success('复习结果已记录')
    reviewDialogVisible.value = false
    loadTodayPlans()
    loadTodayCount()
  } catch (error) {
    console.error(error)
  }
}
</script>

<style scoped>
.review {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.review-item {
  cursor: pointer;
}

.review-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.question-preview h4 {
  margin: 0 0 8px 0;
}

.plan-time {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.review-dialog {
  max-height: 500px;
  overflow-y: auto;
}

.question-section,
.answer-section,
.analysis-section,
.action-section,
.result-section {
  margin-bottom: 20px;
}

.question-section h4,
.answer-section h4,
.analysis-section h4 {
  margin-bottom: 10px;
  color: #333;
}

.content-box {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
  line-height: 1.6;
}

.content-box.correct {
  background: #f0f9eb;
  border: 1px solid #67c23a;
}

.content-box.wrong {
  background: #fef0f0;
  border: 1px solid #f56c6c;
}

.action-section {
  text-align: center;
  padding: 20px;
}

.result-section {
  text-align: center;
  padding: 20px;
  border-top: 1px solid #e4e7ed;
}

.result-section p {
  margin-bottom: 15px;
  font-weight: bold;
}
</style>
