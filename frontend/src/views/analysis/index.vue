<template>
  <div class="analysis">
    <!-- 统计概览卡片 -->
    <el-row :gutter="20" class="statistics-cards">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-value">{{ statistics.totalCount }}</div>
          <div class="stat-label">总错题数</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card success">
          <div class="stat-value">{{ statistics.masteredCount }}</div>
          <div class="stat-label">已掌握</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card warning">
          <div class="stat-value">{{ statistics.unmasteredCount }}</div>
          <div class="stat-label">未掌握</div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card>
          <template #header>学科分布</template>
          <div ref="subjectChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>错误原因分布</template>
          <div ref="reasonChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 薄弱点分析 -->
    <el-card class="weak-points">
      <template #header>
        <span>薄弱点分析</span>
      </template>
      <el-empty v-if="!statistics.weakPoints || statistics.weakPoints.length === 0" description="暂无薄弱点数据" />
      <div v-else>
        <el-tag
          v-for="(point, index) in statistics.weakPoints"
          :key="index"
          type="danger"
          class="weak-point-tag"
        >
          {{ point }}
        </el-tag>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getUserStatistics } from '@/api/statistics'
import type { StatisticsData } from '@/api/statistics'

const subjectChartRef = ref<HTMLElement>()
const reasonChartRef = ref<HTMLElement>()

const statistics = reactive<StatisticsData>({
  totalCount: 0,
  masteredCount: 0,
  unmasteredCount: 0,
  subjectDistribution: [],
  knowledgeDistribution: [],
  errorReasonDistribution: [],
  difficultyDistribution: [],
  weakPoints: []
})

onMounted(async () => {
  await loadStatistics()
  nextTick(() => {
    initCharts()
  })
})

const loadStatistics = async () => {
  try {
    const res = await getUserStatistics()
    Object.assign(statistics, res)
  } catch (error) {
    console.error(error)
  }
}

const initCharts = () => {
  // 学科分布饼图
  if (subjectChartRef.value && statistics.subjectDistribution.length > 0) {
    const subjectChart = echarts.init(subjectChartRef.value)
    subjectChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: '5%' },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: { show: false },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        data: statistics.subjectDistribution.map(item => ({
          name: '学科' + item.name,
          value: item.value
        }))
      }]
    })
  }
  
  // 错误原因柱状图
  if (reasonChartRef.value && statistics.errorReasonDistribution.length > 0) {
    const reasonChart = echarts.init(reasonChartRef.value)
    reasonChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: statistics.errorReasonDistribution.map(item => item.name),
        axisLabel: { rotate: 30 }
      },
      yAxis: { type: 'value' },
      series: [{
        type: 'bar',
        data: statistics.errorReasonDistribution.map(item => item.value),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ])
        }
      }]
    })
  }
}
</script>

<style scoped>
.analysis {
  padding: 20px;
}

.statistics-cards {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  padding: 20px;
}

.stat-card .stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 10px;
}

.stat-card.success .stat-value {
  color: #67C23A;
}

.stat-card.warning .stat-value {
  color: #E6A23C;
}

.stat-card .stat-label {
  font-size: 14px;
  color: #666;
}

.charts-row {
  margin-bottom: 20px;
}

.chart {
  height: 300px;
}

.weak-points {
  margin-top: 20px;
}

.weak-point-tag {
  margin: 5px;
  font-size: 14px;
}
</style>
