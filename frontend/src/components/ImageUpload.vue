<template>
  <div class="image-upload">
    <el-dialog
      v-model="dialogVisible"
      :title="title"
      width="700px"
      :close-on-click-modal="false"
    >
      <div class="upload-area">
        <el-upload
          drag
          :action="uploadAction"
          :headers="uploadHeaders"
          :before-upload="beforeUpload"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError"
          :show-file-list="false"
          accept="image/jpeg,image/png,image/jpg"
          multiple
        >
          <div class="upload-content">
            <el-icon class="upload-icon"><UploadFilled /></el-icon>
            <div class="upload-text">
              <p>将图片拖到此处，或<em>点击上传</em></p>
              <p class="upload-hint">支持 jpg、png 格式，单张不超过 10MB</p>
            </div>
          </div>
        </el-upload>
      </div>

      <!-- 已上传的图片列表 -->
      <div v-if="imageList.length > 0" class="image-list">
        <h4>已上传的图片 ({{ imageList.length }})</h4>
        <div class="image-grid">
          <div v-for="(img, index) in imageList" :key="index" class="image-item">
            <el-image
              :src="img.url"
              fit="cover"
              class="image-thumb"
              @click="previewImage(img.url)"
            >
              <template #error>
                <div class="image-error">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div class="image-actions">
              <el-button size="small" @click="previewImage(img.url)">
                <el-icon><ZoomIn /></el-icon>
              </el-button>
              <el-button size="small" type="danger" @click="removeImage(index)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmUpload" :disabled="imageList.length === 0">
            确定 ({{ imageList.length }})
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog v-model="previewVisible" width="80%" center>
      <el-image :src="previewUrl" fit="contain" style="width: 100%" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled, ZoomIn, Delete, Picture } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

interface ImageInfo {
  url: string
  fileName: string
  originalName: string
}

const props = defineProps<{
  title?: string
}>()

const emit = defineEmits<{
  (e: 'confirm', images: ImageInfo[]): void
}>()

const userStore = useUserStore()
const dialogVisible = ref(false)
const previewVisible = ref(false)
const previewUrl = ref('')
const imageList = ref<ImageInfo[]>([])

const uploadAction = computed(() => 'http://localhost:8080/api/files/upload')
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${userStore.token}`
}))

const title = computed(() => props.title || '上传图片')

// 打开对话框
const open = () => {
  dialogVisible.value = true
  imageList.value = []
}

// 上传前验证
const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!')
    return false
  }
  return true
}

// 上传成功
const handleUploadSuccess = (response: any, file: File) => {
  if (response.code === 200) {
    imageList.value.push({
      url: response.data.url,
      fileName: response.data.fileName,
      originalName: response.data.originalName
    })
    ElMessage.success(`上传成功: ${response.data.originalName}`)
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 上传失败
const handleUploadError = () => {
  ElMessage.error('上传失败，请重试')
}

// 预览图片
const previewImage = (url: string) => {
  previewUrl.value = url
  previewVisible.value = true
}

// 删除图片
const removeImage = (index: number) => {
  imageList.value.splice(index, 1)
}

// 确认上传
const confirmUpload = () => {
  emit('confirm', [...imageList.value])
  dialogVisible.value = false
}

// 暴露方法给父组件
defineExpose({
  open
})
</script>

<style scoped>
.upload-area {
  margin-bottom: 20px;
}

.upload-content {
  padding: 40px 20px;
  text-align: center;
}

.upload-icon {
  font-size: 67px;
  color: #c0c4cc;
  margin-bottom: 16px;
}

.upload-text {
  color: #606266;
  font-size: 14px;
}

.upload-text em {
  color: #409EFF;
  font-style: normal;
}

.upload-hint {
  color: #909399;
  font-size: 12px;
  margin-top: 8px;
}

.image-list h4 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 16px;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 15px;
}

.image-item {
  position: relative;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
}

.image-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.image-thumb {
  width: 100%;
  height: 150px;
  cursor: pointer;
}

.image-error {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 150px;
  background-color: #f5f7fa;
  color: #c0c4cc;
  font-size: 30px;
}

.image-actions {
  display: flex;
  justify-content: space-around;
  padding: 8px 0;
  background-color: #fff;
  border-top: 1px solid #ebeef5;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
