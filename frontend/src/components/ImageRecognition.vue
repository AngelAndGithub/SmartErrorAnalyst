<template>
  <el-tooltip :content="tooltip" placement="top">
    <el-button 
      :type="type" 
      :size="size"
      :loading="loading"
      :disabled="disabled"
      @click="handleRecognize"
    >
      <el-icon v-if="!loading"><Aim /></el-icon>
      {{ loading ? '识别中...' : buttonText }}
    </el-button>
  </el-tooltip>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Aim } from '@element-plus/icons-vue'
import { recognizeImage } from '@/api/image-recognition'

const props = defineProps<{
  imageUrl: string
  type?: 'question' | 'answer' | 'userAnswer'
  buttonText?: string
  tooltip?: string
  size?: 'large' | 'default' | 'small'
  disabled?: boolean
}>()

const emit = defineEmits<{
  (e: 'success', result: string): void
  (e: 'error', error: string): void
}>()

const loading = ref(false)

const buttonType = computed(() => {
  if (props.type === 'question') return 'primary'
  if (props.type === 'answer') return 'success'
  return 'warning'
})

const buttonText = computed(() => props.buttonText || '识别图片')
const tooltip = computed(() => props.tooltip || '使用AI识别图片中的文字')

// 将图片URL转换为base64
const imageUrlToBase64 = async (url: string): Promise<string> => {
  const response = await fetch(url)
  const blob = await response.blob()
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onloadend = () => resolve(reader.result as string)
    reader.onerror = reject
    reader.readAsDataURL(blob)
  })
}

// 执行识别
const handleRecognize = async () => {
  if (!props.imageUrl) {
    ElMessage.warning('请先上传图片')
    return
  }

  loading.value = true
  try {
    // 将图片URL转换为base64
    const base64 = await imageUrlToBase64(props.imageUrl)
    
    // 调用识别API
    const result = await recognizeImage({
      imageBase64: base64,
      type: props.type || 'question'
    })

    ElMessage.success('图片识别成功')
    emit('success', result as unknown as string)
  } catch (error: any) {
    console.error('图片识别失败:', error)
    const errorMsg = error.message || '识别失败，请重试'
    ElMessage.error(errorMsg)
    emit('error', errorMsg)
  } finally {
    loading.value = false
  }
}
</script>
