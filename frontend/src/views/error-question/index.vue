<template>
  <div class="error-question">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>错题管理</span>
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>录入错题
          </el-button>
        </div>
      </template>
      
      <!-- 搜索筛选 -->
      <el-form :model="queryForm" inline class="search-form">
        <el-form-item label="学科">
          <el-select v-model="queryForm.subjectId" placeholder="请选择" clearable style="width: 120px">
            <el-option
              v-for="subject in subjects"
              :key="subject.subjectId"
              :label="subject.subjectName"
              :value="subject.subjectId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="题目类型">
          <el-select v-model="queryForm.questionType" placeholder="请选择" clearable style="width: 120px">
            <el-option label="单选题" :value="1" />
            <el-option label="多选题" :value="2" />
            <el-option label="填空题" :value="3" />
            <el-option label="解答题" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="queryForm.difficulty" placeholder="请选择" clearable style="width: 120px">
            <el-option label="简单" :value="1" />
            <el-option label="中等" :value="2" />
            <el-option label="困难" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="掌握状态">
          <el-select v-model="queryForm.masteryStatus" placeholder="请选择" clearable style="width: 120px">
            <el-option label="未掌握" :value="0" />
            <el-option label="已掌握" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="搜索题目内容" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 错题列表 -->
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column type="index" width="50" />
        <el-table-column prop="questionContent" label="题目内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="subjectId" label="学科" width="100">
          <template #default="{ row }">
            {{ getSubjectName(row.subjectId) }}
          </template>
        </el-table-column>
        <el-table-column prop="questionType" label="题型" width="80">
          <template #default="{ row }">
            <el-tag :type="getQuestionTypeType(row.questionType)">
              {{ getQuestionTypeText(row.questionType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="difficulty" label="难度" width="80">
          <template #default="{ row }">
            <el-tag :type="getDifficultyType(row.difficulty)">
              {{ getDifficultyText(row.difficulty) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="errorReason" label="错误原因" width="100" />
        <el-table-column prop="masteryStatus" label="掌握状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.masteryStatus === 1 ? 'success' : 'warning'">
              {{ row.masteryStatus === 1 ? '已掌握' : '未掌握' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="录入时间" width="150" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handleReview(row)">复习</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="queryForm.pageNum"
          v-model:page-size="queryForm.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { listErrorQuestions, deleteErrorQuestion } from '@/api/error-question'
import { listSubjects } from '@/api/subject'
import type { ErrorQuestion, ErrorQuestionQuery } from '@/api/error-question'
import type { Subject } from '@/api/subject'

const router = useRouter()
const loading = ref(false)
const tableData = ref<ErrorQuestion[]>([])
const total = ref(0)
const subjects = ref<Subject[]>([])

const queryForm = reactive<ErrorQuestionQuery>({
  subjectId: undefined,
  questionType: undefined,
  difficulty: undefined,
  masteryStatus: undefined,
  keyword: '',
  pageNum: 1,
  pageSize: 10
})

onMounted(() => {
  loadSubjects()
  loadData()
})

const loadSubjects = async () => {
  const res = await listSubjects()
  subjects.value = res
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await listErrorQuestions(queryForm)
    tableData.value = res.records
    total.value = res.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getSubjectName = (subjectId: number) => {
  const subject = subjects.value.find(s => s.subjectId === subjectId)
  return subject?.subjectName || '-'
}

const getQuestionTypeText = (type: number) => {
  const map: Record<number, string> = { 1: '单选', 2: '多选', 3: '填空', 4: '解答' }
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

const handleSearch = () => {
  queryForm.pageNum = 1
  loadData()
}

const handleReset = () => {
  queryForm.subjectId = undefined
  queryForm.questionType = undefined
  queryForm.difficulty = undefined
  queryForm.masteryStatus = undefined
  queryForm.keyword = ''
  queryForm.pageNum = 1
  loadData()
}

const handleSizeChange = (val: number) => {
  queryForm.pageSize = val
  loadData()
}

const handleCurrentChange = (val: number) => {
  queryForm.pageNum = val
  loadData()
}

const handleCreate = () => {
  router.push('/error-question/create')
}

const handleEdit = (row: ErrorQuestion) => {
  router.push(`/error-question/create?id=${row.questionId}`)
}

const handleReview = (row: ErrorQuestion) => {
  router.push(`/review?questionId=${row.questionId}`)
}

const handleDelete = (row: ErrorQuestion) => {
  ElMessageBox.confirm('确定要删除这道错题吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteErrorQuestion(row.questionId!)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error(error)
    }
  })
}
</script>

<style scoped>
.error-question {
  padding: 0;
}

.error-question :deep(.el-card) {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  border: none;
}

.error-question :deep(.el-card__header) {
  padding: 18px 24px;
  border-bottom: 1px solid #ebeef5;
  background: linear-gradient(to right, #fafbfc, #f5f7fa);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #fafbfc;
  border-radius: 6px;
  border: 1px solid #ebeef5;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 16px;
}

.search-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: 6px;
  overflow: hidden;
}

:deep(.el-table th) {
  background-color: #f5f7fa !important;
  font-weight: 600;
  color: #303133;
}

:deep(.el-table--border) {
  border: 1px solid #ebeef5;
}
</style>
