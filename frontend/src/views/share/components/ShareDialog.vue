<template>
  <el-dialog v-model="dialogVisible" title="分享错题" width="600px" @close="handleClose">
    <el-form :model="form" label-width="100px">
      <!-- 分享标题 -->
      <el-form-item label="分享标题">
        <el-input v-model="form.shareTitle" placeholder="请输入分享标题（可选）" />
      </el-form-item>

      <!-- 分享留言 -->
      <el-form-item label="分享留言">
        <el-input
          v-model="form.shareMessage"
          type="textarea"
          :rows="3"
          placeholder="写点什么吧..."
        />
      </el-form-item>

      <!-- 分享类型 -->
      <el-form-item label="分享方式">
        <el-radio-group v-model="form.shareType">
          <el-radio :label="1">公开分享（所有人可见）</el-radio>
          <el-radio :label="2">指定分享（仅选定用户）</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 选择分享对象（仅指定分享时显示） -->
      <el-form-item v-if="form.shareType === 2" label="分享对象" required>
        <el-select
          v-model="form.targetUserIds"
          multiple
          filterable
          remote
          placeholder="搜索并选择用户"
          :remote-method="searchUsers"
          :loading="userLoading"
          style="width: 100%"
        >
          <el-option
            v-for="user in userList"
            :key="user.userId"
            :label="user.nickname || user.username"
            :value="user.userId"
          />
        </el-select>
        <div v-if="!form.targetUserIds || form.targetUserIds.length === 0" class="tip">
          <el-icon><InfoFilled /></el-icon>
          请至少选择一个用户
        </div>
      </el-form-item>

      <!-- 分享内容包括 -->
      <el-form-item label="包含内容">
        <el-checkbox v-model="form.includeAnswer">正确答案</el-checkbox>
        <el-checkbox v-model="form.includeAnalysis">解析</el-checkbox>
        <el-checkbox v-model="form.includeNotes">笔记</el-checkbox>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        分享
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'
import { createShare, getUserList } from '@/api/share'
import type { ShareDTO } from '@/api/share'

const props = defineProps<{
  visible: boolean
  questionId: number
}>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'success'): void
}>()

const dialogVisible = ref(false)
const submitting = ref(false)
const userLoading = ref(false)
const userList = ref<Array<{ userId: number; username: string; nickname?: string }>>([])

const form = reactive<ShareDTO>({
  questionId: props.questionId,
  shareTitle: '',
  shareMessage: '',
  shareType: 1,
  targetUserIds: [],
  includeAnswer: 1,
  includeAnalysis: 1,
  includeNotes: 0
})

watch(() => props.visible, (val) => {
  dialogVisible.value = val
  if (val) {
    // 重置表单
    form.questionId = props.questionId
    form.shareTitle = ''
    form.shareMessage = ''
    form.shareType = 1
    form.targetUserIds = []
    form.includeAnswer = 1
    form.includeAnalysis = 1
    form.includeNotes = 0
    userList.value = []
  }
})

watch(dialogVisible, (val) => {
  emit('update:visible', val)
})

// 搜索用户
const searchUsers = async (keyword: string) => {
  if (!keyword) {
    userList.value = []
    return
  }

  userLoading.value = true
  try {
    const users = await getUserList(keyword)
    userList.value = users
  } catch (error) {
    console.error('搜索用户失败:', error)
  } finally {
    userLoading.value = false
  }
}

// 提交分享
const handleSubmit = async () => {
  // 验证
  if (form.shareType === 2 && (!form.targetUserIds || form.targetUserIds.length === 0)) {
    ElMessage.warning('请至少选择一个分享对象')
    return
  }

  submitting.value = true
  try {
    await createShare(form)
    ElMessage.success('分享成功')
    emit('success')
    dialogVisible.value = false
  } catch (error: any) {
    ElMessage.error(error.message || '分享失败')
  } finally {
    submitting.value = false
  }
}

const handleClose = () => {
  emit('update:visible', false)
}
</script>

<style scoped>
.tip {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}
</style>
