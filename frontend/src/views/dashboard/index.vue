<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="welcome-card">
          <h2>欢迎使用错题智析宝</h2>
          <p>智能错题管理，助力高效学习</p>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card class="stat-card" @click="$router.push('/error-questions')">
          <el-icon class="stat-icon" :size="40" color="#409EFF"><Document /></el-icon>
          <div class="stat-info">
            <div class="stat-value">错题管理</div>
            <div class="stat-label">录入、查看、管理错题</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card" @click="$router.push('/review')">
          <el-icon class="stat-icon" :size="40" color="#67C23A"><Calendar /></el-icon>
          <div class="stat-info">
            <div class="stat-value">复习计划</div>
            <div class="stat-label">
              今日待复习: <el-tag v-if="todayCount > 0" type="danger">{{ todayCount }}道</el-tag>
              <el-tag v-else type="success">已完成</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card" @click="$router.push('/analysis')">
          <el-icon class="stat-icon" :size="40" color="#E6A23C"><TrendCharts /></el-icon>
          <div class="stat-info">
            <div class="stat-value">学情分析</div>
            <div class="stat-label">查看错题统计与分析</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="quick-actions">
      <el-col :span="24">
        <el-card>
          <template #header>快速操作</template>
          <el-button type="primary" size="large" @click="$router.push('/error-question/create')">
            <el-icon><Plus /></el-icon>录入新错题
          </el-button>
          <el-button type="success" size="large" @click="$router.push('/review')">
            <el-icon><Calendar /></el-icon>开始复习
          </el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Document, Calendar, TrendCharts, Plus } from '@element-plus/icons-vue'
import { countTodayPlans } from '@/api/review'

const todayCount = ref(0)

onMounted(async () => {
  try {
    const count = await countTodayPlans()
    todayCount.value = count
  } catch (error) {
    console.error(error)
  }
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.welcome-card {
  text-align: center;
  margin-bottom: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.welcome-card h2 {
  margin: 0 0 10px 0;
}

.welcome-card p {
  margin: 0;
  opacity: 0.9;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  margin-right: 15px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.quick-actions {
  text-align: center;
}

.quick-actions .el-button {
  margin: 0 10px;
}
</style>
