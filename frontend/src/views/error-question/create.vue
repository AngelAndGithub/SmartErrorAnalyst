<template>
  <div class="create-error-question">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑错题' : '录入错题' }}</span>
          <el-button type="warning" @click="openAIAnalysis" :disabled="!form.questionContent">
            <el-icon><MagicStick /></el-icon>
            AI智能解析
          </el-button>
        </div>
      </template>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="question-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学科" prop="subjectId">
              <el-select v-model="form.subjectId" placeholder="请选择学科" style="width: 100%" @change="handleSubjectChange">
                <el-option
                  v-for="subject in subjects"
                  :key="subject.subjectId"
                  :label="subject.subjectName"
                  :value="subject.subjectId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="知识点" prop="knowledgeId">
              <el-select v-model="form.knowledgeId" placeholder="请选择知识点" style="width: 100%">
                <el-option
                  v-for="knowledge in knowledgeList"
                  :key="knowledge.knowledgeId"
                  :label="knowledge.knowledgeName"
                  :value="knowledge.knowledgeId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="题目类型" prop="questionType">
              <el-select v-model="form.questionType" placeholder="请选择题目类型" style="width: 100%">
                <el-option label="单选题" :value="1" />
                <el-option label="多选题" :value="2" />
                <el-option label="填空题" :value="3" />
                <el-option label="解答题" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="难度" prop="difficulty">
              <el-select v-model="form.difficulty" placeholder="请选择难度" style="width: 100%">
                <el-option label="简单" :value="1" />
                <el-option label="中等" :value="2" />
                <el-option label="困难" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="错误原因" prop="errorReason">
          <el-select v-model="form.errorReason" placeholder="请选择错误原因" style="width: 100%">
            <el-option label="概念混淆" value="概念混淆" />
            <el-option label="计算失误" value="计算失误" />
            <el-option label="审题不清" value="审题不清" />
            <el-option label="不会做" value="不会做" />
            <el-option label="粗心大意" value="粗心大意" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="题目内容" prop="questionContent">
          <div class="form-item-with-image">
            <MathInput
              v-model="form.questionContent"
              :rows="4"
              placeholder="请输入题目内容，支持数学公式（如 $x^2 + y^2 = 1$）"
            />
            <div class="image-actions-bar">
              <el-button size="small" @click="openImageUpload('question')">
                <el-icon><Upload /></el-icon>
                上传图片
              </el-button>
              <ImageRecognition
                v-if="questionImages.length > 0"
                :image-url="questionImages[questionImages.length - 1]"
                type="question"
                button-text="识别最后一张图"
                @success="handleRecognitionSuccess('question', $event)"
              />
            </div>
            <!-- 已上传的图片缩略图 -->
            <div v-if="questionImages.length > 0" class="uploaded-images">
              <div v-for="(img, index) in questionImages" :key="index" class="thumb-item">
                <el-image :src="img" fit="cover" @click="previewImage(img)" />
                <el-icon class="remove-icon" @click="removeImage('question', index)"><Close /></el-icon>
              </div>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item label="正确答案" prop="correctAnswer">
          <div class="form-item-with-image">
            <MathInput
              v-model="form.correctAnswer"
              :rows="2"
              placeholder="请输入正确答案，支持数学公式"
            />
            <div class="image-actions-bar">
              <el-button size="small" @click="openImageUpload('answer')">
                <el-icon><Upload /></el-icon>
                上传图片
              </el-button>
              <ImageRecognition
                v-if="answerImages.length > 0"
                :image-url="answerImages[answerImages.length - 1]"
                type="answer"
                button-text="识别最后一张图"
                @success="handleRecognitionSuccess('answer', $event)"
              />
            </div>
            <div v-if="answerImages.length > 0" class="uploaded-images">
              <div v-for="(img, index) in answerImages" :key="index" class="thumb-item">
                <el-image :src="img" fit="cover" @click="previewImage(img)" />
                <el-icon class="remove-icon" @click="removeImage('answer', index)"><Close /></el-icon>
              </div>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item label="你的答案" prop="userAnswer">
          <div class="form-item-with-image">
            <MathInput
              v-model="form.userAnswer"
              :rows="2"
              placeholder="请输入你的答案，支持数学公式"
            />
            <div class="image-actions-bar">
              <el-button size="small" @click="openImageUpload('userAnswer')">
                <el-icon><Upload /></el-icon>
                上传图片
              </el-button>
              <ImageRecognition
                v-if="userAnswerImages.length > 0"
                :image-url="userAnswerImages[userAnswerImages.length - 1]"
                type="userAnswer"
                button-text="识别最后一张图"
                @success="handleRecognitionSuccess('userAnswer', $event)"
              />
            </div>
            <div v-if="userAnswerImages.length > 0" class="uploaded-images">
              <div v-for="(img, index) in userAnswerImages" :key="index" class="thumb-item">
                <el-image :src="img" fit="cover" @click="previewImage(img)" />
                <el-icon class="remove-icon" @click="removeImage('userAnswer', index)"><Close /></el-icon>
              </div>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item label="解析">
          <div class="analysis-input-wrapper">
            <el-input
              v-model="form.analysis"
              type="textarea"
              :rows="4"
              placeholder="请输入题目解析（可选），支持LaTeX公式，如：$x^2 + y^2 = 1$"
            />
            <div v-if="form.analysis" class="formula-preview">
              <div class="preview-header">
                <span class="preview-title">公式预览</span>
                <el-tag size="small" type="info">实时预览</el-tag>
              </div>
              <div class="preview-content">
                <MathPreview :content="form.analysis" />
              </div>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item label="笔记">
          <div class="analysis-input-wrapper">
            <el-input
              v-model="form.notes"
              type="textarea"
              :rows="2"
              placeholder="请输入笔记（可选），支持LaTeX公式"
            />
            <div v-if="form.notes" class="formula-preview">
              <div class="preview-header">
                <span class="preview-title">笔记预览</span>
                <el-tag size="small" type="info">实时预览</el-tag>
              </div>
              <div class="preview-content">
                <MathPreview :content="form.notes" />
              </div>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item label="标签">
          <el-input
            v-model="form.tags"
            placeholder="请输入标签，多个标签用逗号分隔"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">保存</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- AI智能解析对话框 -->
    <el-dialog
      v-model="aiDialogVisible"
      title="AI智能解析"
      width="650px"
      :close-on-click-modal="false"
    >
      <!-- AI提供商选择 -->
      <div v-if="!aiResult && !aiLoading" class="ai-provider-section">
        <el-form label-width="100px">
          <el-form-item label="AI模型">
            <el-radio-group v-model="selectedProvider">
              <el-radio-button 
                v-for="key in availableProviders" 
                :key="key" 
                :label="key"
              >
                {{ providerOptions[key]?.name || key }}
                <el-tooltip :content="providerOptions[key]?.description || ''" placement="top">
                  <el-icon><InfoFilled /></el-icon>
                </el-tooltip>
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <el-divider />
      </div>

      <div class="ai-analysis-content">
        <div v-if="aiLoading" class="ai-loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>AI正在分析题目，请稍候...</span>
          <p class="ai-provider-hint">正在使用 {{ providerOptions[selectedProvider]?.name }}</p>
        </div>
        <div v-else-if="aiResult" class="ai-result">
          <el-alert
            :title="'AI解析结果 - ' + (providerOptions[selectedProvider]?.name || selectedProvider)"
            type="success"
            :closable="false"
            class="ai-alert"
          />
          <div class="ai-section">
            <h4>题目解析</h4>
            <div class="math-content">
              <MathPreview :content="aiResult.analysis" />
            </div>
          </div>
          <div class="ai-section" v-if="aiResult.knowledgePoint">
            <h4>相关知识点</h4>
            <div class="math-content">
              <MathPreview :content="aiResult.knowledgePoint" />
            </div>
          </div>
          <div class="ai-section" v-if="aiResult.suggestions">
            <h4>学习建议</h4>
            <div class="math-content">
              <MathPreview :content="aiResult.suggestions" />
            </div>
          </div>
        </div>
        <div v-else class="ai-placeholder">
          <el-icon><MagicStick /></el-icon>
          <p>选择AI模型后，点击"开始解析"让AI帮你分析这道题目</p>
          <div class="provider-hints">
            <el-tag v-for="key in availableProviders" :key="key" size="small" class="provider-tag">
              {{ providerOptions[key]?.name || key }}: {{ providerOptions[key]?.description || '' }}
            </el-tag>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="aiDialogVisible = false">取消</el-button>
          <el-button v-if="!aiResult && !aiLoading" type="primary" @click="startAIAnalysis" :loading="aiLoading">
            开始解析
          </el-button>
          <el-button v-if="aiResult" type="success" @click="applyAIResult">
            应用到解析
          </el-button>
          <el-button v-if="aiResult" @click="resetAIAnalysis">
            重新解析
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 图片上传对话框 -->
    <ImageUpload
      ref="imageUploadRef"
      :title="`上传${currentUploadType === 'question' ? '题目' : currentUploadType === 'answer' ? '答案' : '用户答案'}图片`"
      @confirm="handleImageUploadConfirm"
    />

    <!-- 图片预览对话框 -->
    <el-dialog v-model="imagePreviewVisible" width="80%" center>
      <el-image :src="previewImageUrl" fit="contain" style="width: 100%" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { MagicStick, Loading, InfoFilled, Upload, Close } from '@element-plus/icons-vue'
import { addErrorQuestion, updateErrorQuestion, getErrorQuestionDetail } from '@/api/error-question'
import { listSubjects, getKnowledgeBySubject } from '@/api/subject'
import { analyzeErrorQuestion, getProvidersInfo, getAvailableProviders, type AIProviderInfo } from '@/api/ai'
import type { Subject, Knowledge } from '@/api/subject'
import type { ErrorQuestion } from '@/api/error-question'
import type { AIAnalysisResult } from '@/api/ai'
import MathInput from '@/components/MathInput.vue'
import ImageUpload from '@/components/ImageUpload.vue'
import ImageRecognition from '@/components/ImageRecognition.vue'
import MathPreview from '@/components/MathPreview.vue'

const route = useRoute()
const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const isEdit = ref(false)
const questionId = ref<number>()

// AI相关状态
const aiDialogVisible = ref(false)
const aiLoading = ref(false)
const aiResult = ref<AIAnalysisResult | null>(null)
const selectedProvider = ref('mock')
const providerOptions = ref<Record<string, AIProviderInfo>>({
  mock: { name: '模拟模式', description: '本地模拟，无需配置', url: '' }
})
const availableProviders = ref<string[]>(['mock'])

// 图片相关状态
const imageUploadRef = ref()
const currentUploadType = ref<'question' | 'answer' | 'userAnswer'>('question')
const questionImages = ref<string[]>([])
const answerImages = ref<string[]>([])
const userAnswerImages = ref<string[]>([])
const imagePreviewVisible = ref(false)
const previewImageUrl = ref('')

const subjects = ref<Subject[]>([])
const knowledgeList = ref<Knowledge[]>([])

const form = reactive<ErrorQuestion>({
  subjectId: undefined as any,
  knowledgeId: undefined,
  questionType: undefined as any,
  difficulty: undefined as any,
  errorReason: '',
  questionContent: '',
  correctAnswer: '',
  userAnswer: '',
  analysis: '',
  notes: '',
  tags: ''
})

const rules: FormRules = {
  subjectId: [{ required: true, message: '请选择学科', trigger: 'change' }],
  questionType: [{ required: true, message: '请选择题目类型', trigger: 'change' }],
  difficulty: [{ required: true, message: '请选择难度', trigger: 'change' }],
  errorReason: [{ required: true, message: '请选择错误原因', trigger: 'change' }],
  questionContent: [{ required: true, message: '请输入题目内容', trigger: 'blur' }],
  correctAnswer: [{ required: true, message: '请输入正确答案', trigger: 'blur' }],
  userAnswer: [{ required: true, message: '请输入你的答案', trigger: 'blur' }]
}

onMounted(async () => {
  // 加载学科列表
  const res = await listSubjects()
  subjects.value = res
  
  // 加载AI提供商信息
  try {
    const [providersRes, infoRes] = await Promise.all([
      getAvailableProviders(),
      getProvidersInfo()
    ])
    console.log('AI providers response:', providersRes)
    console.log('AI info response:', infoRes)
    if (providersRes && Array.isArray(providersRes)) {
      availableProviders.value = providersRes
    }
    if (infoRes && typeof infoRes === 'object') {
      // request.ts 响应拦截器已经返回了 res.data，所以 infoRes 本身就是数据
      providerOptions.value = (infoRes as unknown) as Record<string, AIProviderInfo>
    }
  } catch (error) {
    console.error('加载AI提供商信息失败:', error)
  }
  
  // 检查是否为编辑模式
  const id = route.query.id
  if (id) {
    isEdit.value = true
    questionId.value = Number(id)
    await loadQuestionDetail(Number(id))
  }
})

const loadQuestionDetail = async (id: number) => {
  try {
    const detail = await getErrorQuestionDetail(id)
    console.log('=== 加载错题详情 ===')
    console.log('detail:', detail)
    console.log('questionImages:', detail.questionImages)
    console.log('answerImages:', detail.answerImages)
    console.log('userAnswerImages:', detail.userAnswerImages)
    
    // 先保存知识点ID，因为handleSubjectChange会清空它
    const savedKnowledgeId = detail.knowledgeId
    
    Object.assign(form, detail)
    
    // 回显图片
    if (detail.questionImages) {
      try {
        questionImages.value = JSON.parse(detail.questionImages)
        console.log('题目图片解析成功:', questionImages.value)
      } catch (e) {
        // 如果不是JSON格式，尝试作为单个URL处理
        questionImages.value = detail.questionImages ? [detail.questionImages] : []
        console.log('题目图片作为单个URL处理:', questionImages.value)
      }
    }
    
    if (detail.answerImages) {
      try {
        answerImages.value = JSON.parse(detail.answerImages)
        console.log('答案图片解析成功:', answerImages.value)
      } catch (e) {
        answerImages.value = detail.answerImages ? [detail.answerImages] : []
        console.log('答案图片作为单个URL处理:', answerImages.value)
      }
    }
    
    if (detail.userAnswerImages) {
      try {
        userAnswerImages.value = JSON.parse(detail.userAnswerImages)
        console.log('用户答案图片解析成功:', userAnswerImages.value)
      } catch (e) {
        userAnswerImages.value = detail.userAnswerImages ? [detail.userAnswerImages] : []
        console.log('用户答案图片作为单个URL处理:', userAnswerImages.value)
      }
    }
    
    console.log('最终图片数组:')
    console.log('questionImages:', questionImages.value)
    console.log('answerImages:', answerImages.value)
    console.log('userAnswerImages:', userAnswerImages.value)
    
    if (detail.subjectId) {
      await handleSubjectChange(detail.subjectId)
      // 在知识点列表加载完成后，恢复知识点ID
      if (savedKnowledgeId) {
        form.knowledgeId = savedKnowledgeId
      }
    }
  } catch (error) {
    console.error('加载错题详情失败:', error)
    ElMessage.error('加载错题详情失败')
  }
}

const handleSubjectChange = async (subjectId: number) => {
  form.knowledgeId = undefined
  if (subjectId) {
    const res = await getKnowledgeBySubject(subjectId)
    knowledgeList.value = res
  } else {
    knowledgeList.value = []
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 准备提交数据，将图片数组转换为JSON字符串
        const submitData = {
          ...form,
          questionImages: questionImages.value.length > 0 ? JSON.stringify(questionImages.value) : undefined,
          answerImages: answerImages.value.length > 0 ? JSON.stringify(answerImages.value) : undefined,
          userAnswerImages: userAnswerImages.value.length > 0 ? JSON.stringify(userAnswerImages.value) : undefined
        }
        
        if (isEdit.value && questionId.value) {
          await updateErrorQuestion(questionId.value, submitData)
          ElMessage.success('更新成功')
        } else {
          await addErrorQuestion(submitData)
          ElMessage.success('录入成功')
        }
        router.push('/error-questions')
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }
  })
}

const handleCancel = () => {
  router.back()
}

// AI智能解析相关方法
const openAIAnalysis = () => {
  if (!form.questionContent) {
    ElMessage.warning('请先输入题目内容')
    return
  }
  aiDialogVisible.value = true
  aiResult.value = null
}

const startAIAnalysis = async () => {
  aiLoading.value = true
  try {
    // 调用后端AI接口，传入选择的提供商
    const result = await analyzeErrorQuestion({
      questionContent: form.questionContent,
      correctAnswer: form.correctAnswer,
      userAnswer: form.userAnswer,
      subjectId: form.subjectId,
      errorReason: form.errorReason,
      provider: selectedProvider.value
    })
    
    aiResult.value = (result as unknown) as AIAnalysisResult
    const providerName = providerOptions.value[selectedProvider.value]?.name || selectedProvider.value
    ElMessage.success(`${providerName} 解析完成`)
  } catch (error) {
    console.error(error)
    ElMessage.error('AI解析失败，请重试')
  } finally {
    aiLoading.value = false
  }
}

const resetAIAnalysis = () => {
  aiResult.value = null
}

const applyAIResult = () => {
  if (aiResult.value) {
    form.analysis = aiResult.value.analysis
    aiDialogVisible.value = false
    ElMessage.success('已应用到解析字段')
  }
}

// 图片上传相关方法
const openImageUpload = (type: 'question' | 'answer' | 'userAnswer') => {
  currentUploadType.value = type
  imageUploadRef.value?.open()
}

const handleImageUploadConfirm = (images: Array<{ url: string }>) => {
  const urls = images.map(img => img.url)
  
  if (currentUploadType.value === 'question') {
    questionImages.value = [...questionImages.value, ...urls]
  } else if (currentUploadType.value === 'answer') {
    answerImages.value = [...answerImages.value, ...urls]
  } else {
    userAnswerImages.value = [...userAnswerImages.value, ...urls]
  }
  
  ElMessage.success(`成功上传 ${urls.length} 张图片`)
}

// 图片识别成功回调
const handleRecognitionSuccess = (type: 'question' | 'answer' | 'userAnswer', result: string) => {
  if (type === 'question') {
    form.questionContent = form.questionContent ? form.questionContent + '\n' + result : result
  } else if (type === 'answer') {
    form.correctAnswer = form.correctAnswer ? form.correctAnswer + '\n' + result : result
  } else {
    form.userAnswer = form.userAnswer ? form.userAnswer + '\n' + result : result
  }
  ElMessage.success('识别结果已填入对应字段')
}

// 图片预览
const previewImage = (url: string) => {
  previewImageUrl.value = url
  imagePreviewVisible.value = true
}

// 删除图片
const removeImage = (type: 'question' | 'answer' | 'userAnswer', index: number) => {
  if (type === 'question') {
    questionImages.value.splice(index, 1)
  } else if (type === 'answer') {
    answerImages.value.splice(index, 1)
  } else {
    userAnswerImages.value.splice(index, 1)
  }
}


</script>

<style scoped>
.create-error-question {
  padding: 0;
  width: 100%;
}

.form-item-with-image {
  width: 100%;
}

.image-actions-bar {
  display: flex;
  gap: 10px;
  margin-top: 10px;
  padding: 8px;
  background-color: #f5f7fa;
  border-radius: 6px;
}

.uploaded-images {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

.thumb-item {
  position: relative;
  width: 100px;
  height: 100px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.thumb-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.thumb-item .el-image {
  width: 100%;
  height: 100%;
}

.remove-icon {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 20px;
  height: 20px;
  background-color: rgba(245, 108, 108, 0.9);
  color: white;
  border-radius: 50%;
  display: none;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.thumb-item:hover .remove-icon {
  display: flex;
}

.remove-icon:hover {
  background-color: rgba(245, 108, 108, 1);
}

.create-error-question :deep(.el-card) {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  border: none;
}

.create-error-question :deep(.el-card__header) {
  padding: 18px 24px;
  border-bottom: 1px solid #ebeef5;
  background: linear-gradient(to right, #fafbfc, #f5f7fa);
}

.card-header {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.question-form {
  max-width: 100%;
  padding: 20px;
}

.question-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

.question-form :deep(.el-textarea__inner) {
  font-family: inherit;
}

/* AI智能解析样式 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.ai-analysis-content {
  min-height: 200px;
  padding: 20px;
}

.ai-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #409EFF;
}

.ai-loading .el-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.ai-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #909399;
}

.ai-placeholder .el-icon {
  font-size: 48px;
  margin-bottom: 16px;
  color: #E6A23C;
}

.ai-result {
  padding: 10px;
}

.ai-alert {
  margin-bottom: 20px;
}

.ai-section {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
  border-left: 4px solid #409EFF;
}

.ai-section h4 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 16px;
}

.ai-section p {
  margin: 0;
  color: #606266;
  line-height: 1.8;
  white-space: pre-line;
}

/* 解析输入框和预览样式 */
.analysis-input-wrapper {
  width: 100%;
}

.formula-preview {
  margin-top: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  overflow: hidden;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  background: rgba(64, 158, 255, 0.1);
  border-bottom: 1px solid #dcdfe6;
}

.preview-title {
  font-size: 13px;
  font-weight: 600;
  color: #409EFF;
  display: flex;
  align-items: center;
  gap: 6px;
}

.preview-title::before {
  content: '📐';
  font-size: 16px;
}

.preview-content {
  padding: 15px;
  min-height: 60px;
  max-height: 300px;
  overflow-y: auto;
  background: rgba(255, 255, 255, 0.6);
}

.preview-content :deep(.katex) {
  font-size: 1.05em;
}

.preview-content :deep(.katex-display) {
  margin: 1em 0;
  overflow-x: auto;
  overflow-y: hidden;
  padding: 0.8em;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 6px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.math-content {
  color: #606266;
  line-height: 1.8;
}

.math-content :deep(.katex) {
  font-size: 1.05em;
}

.math-content :deep(.katex-display) {
  margin: 0.8em 0;
  overflow-x: auto;
  overflow-y: hidden;
  padding: 0.5em;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 4px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* AI提供商选择样式 */
.ai-provider-section {
  padding: 0 20px;
}

.ai-provider-section :deep(.el-radio-button__inner) {
  display: flex;
  align-items: center;
  gap: 5px;
}

.ai-provider-hint {
  margin-top: 10px;
  color: #909399;
  font-size: 14px;
}

.provider-hints {
  margin-top: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
}

.provider-tag {
  margin: 5px;
}
</style>
