<template>
  <div class="math-input-wrapper">
    <!-- 输入区域 -->
    <el-input
      v-model="inputValue"
      type="textarea"
      :rows="rows"
      :placeholder="placeholder"
      @input="handleInput"
    />
    
    <!-- 工具栏 -->
    <div class="math-toolbar">
      <el-dropdown @command="insertFormula">
        <el-button type="primary" size="small">
          <el-icon><Plus /></el-icon>
          插入公式
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="frac">分数 $\frac{a}{b}$</el-dropdown-item>
            <el-dropdown-item command="sqrt">平方根 $\sqrt{x}$</el-dropdown-item>
            <el-dropdown-item command="sqrt3">立方根 $\sqrt[3]{x}$</el-dropdown-item>
            <el-dropdown-item command="power">上标 $x^{n}$</el-dropdown-item>
            <el-dropdown-item command="sub">下标 $x_{n}$</el-dropdown-item>
            <el-dropdown-item command="sum">求和 $\sum_{i=1}^{n}$</el-dropdown-item>
            <el-dropdown-item command="prod">乘积 $\prod_{i=1}^{n}$</el-dropdown-item>
            <el-dropdown-item command="int">积分 $\int_{a}^{b}$</el-dropdown-item>
            <el-dropdown-item command="lim">极限 $\lim_{x \to 0}$</el-dropdown-item>
            <el-dropdown-item command="alpha">α (alpha)</el-dropdown-item>
            <el-dropdown-item command="beta">β (beta)</el-dropdown-item>
            <el-dropdown-item command="theta">θ (theta)</el-dropdown-item>
            <el-dropdown-item command="pi">π (pi)</el-dropdown-item>
            <el-dropdown-item command="infty">∞ (无穷)</el-dropdown-item>
            <el-dropdown-item command="pm">± (正负)</el-dropdown-item>
            <el-dropdown-item command="times">× (乘号)</el-dropdown-item>
            <el-dropdown-item command="div">÷ (除号)</el-dropdown-item>
            <el-dropdown-item command="neq">≠ (不等于)</el-dropdown-item>
            <el-dropdown-item command="leq">≤ (小于等于)</el-dropdown-item>
            <el-dropdown-item command="geq">≥ (大于等于)</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      
      <el-button 
        type="info" 
        size="small" 
        @click="showPreview = !showPreview"
      >
        <el-icon><View /></el-icon>
        {{ showPreview ? '隐藏预览' : '预览公式' }}
      </el-button>
    </div>
    
    <!-- 预览区域 -->
    <div v-if="showPreview" class="math-preview">
      <div class="preview-title">公式预览</div>
      <div class="preview-content" v-html="renderedMath"></div>
    </div>
    
    <!-- 使用提示 -->
    <div class="math-tips">
      <el-icon><InfoFilled /></el-icon>
      <span>支持 LaTeX 语法，用 $...$ 包裹行内公式，$$...$$ 包裹独立公式</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import katex from 'katex'
import 'katex/dist/katex.min.css'

const props = defineProps<{
  modelValue: string
  rows?: number
  placeholder?: string
}>()

const emit = defineEmits<['update:modelValue']>()

const inputValue = ref(props.modelValue || '')
const showPreview = ref(false)

// 同步父组件值
watch(() => props.modelValue, (val) => {
  inputValue.value = val || ''
})

// 输入处理
const handleInput = () => {
  emit('update:modelValue', inputValue.value)
}

// 渲染数学公式
const renderedMath = computed(() => {
  if (!inputValue.value) return '<span style="color: #999;">暂无内容</span>'
  
  try {
    // 将 $...$ 和 $$...$$ 替换为渲染后的公式
    let text = inputValue.value
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
    
    // 处理独立公式 $$...$$
    text = text.replace(/\$\$([^$]+)\$\$/g, (match, formula) => {
      try {
        return katex.renderToString(formula.trim(), {
          throwOnError: false,
          displayMode: true
        })
      } catch (e) {
        return `<span style="color: red;">公式错误: ${formula}</span>`
      }
    })
    
    // 处理行内公式 $...$
    text = text.replace(/\$([^$]+)\$/g, (match, formula) => {
      try {
        return katex.renderToString(formula.trim(), {
          throwOnError: false,
          displayMode: false
        })
      } catch (e) {
        return `<span style="color: red;">公式错误: ${formula}</span>`
      }
    })
    
    // 将换行符转为 <br>
    text = text.replace(/\n/g, '<br>')
    
    return text
  } catch (e) {
    return '<span style="color: red;">渲染失败</span>'
  }
})

// 插入公式模板
const insertFormula = (command: string) => {
  const templates: Record<string, string> = {
    frac: '$\\frac{}{}$',
    sqrt: '$\\sqrt{}$',
    sqrt3: '$\\sqrt[3]{}$',
    power: '$^{}$',
    sub: '$_{}$',
    sum: '$\\sum_{i=1}^{n}$',
    prod: '$\\prod_{i=1}^{n}$',
    int: '$\\int_{a}^{b}$',
    lim: '$\\lim_{x \\to 0}$',
    alpha: '$\\alpha$',
    beta: '$\\beta$',
    theta: '$\\theta$',
    pi: '$\\pi$',
    infty: '$\\infty$',
    pm: '$\\pm$',
    times: '$\\times$',
    div: '$\\div$',
    neq: '$\\neq$',
    leq: '$\\leq$',
    geq: '$\\geq$'
  }
  
  const template = templates[command]
  if (template) {
    inputValue.value += template
    emit('update:modelValue', inputValue.value)
  }
}
</script>

<style scoped>
.math-input-wrapper {
  width: 100%;
}

.math-toolbar {
  display: flex;
  gap: 10px;
  margin-top: 8px;
  margin-bottom: 8px;
}

.math-preview {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 12px;
  margin-top: 8px;
  background-color: #f5f7fa;
}

.preview-title {
  font-size: 12px;
  color: #606266;
  margin-bottom: 8px;
  font-weight: bold;
}

.preview-content {
  font-size: 14px;
  line-height: 1.6;
  min-height: 40px;
}

.math-tips {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

:deep(.katex) {
  font-size: 1.1em;
}

:deep(.katex-display) {
  margin: 0.5em 0;
}
</style>
