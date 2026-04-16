<template>
  <div class="math-preview" v-html="renderedHtml"></div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import katex from 'katex'
import 'katex/dist/katex.min.css'

const props = defineProps<{
  content: string
}>()

// 渲染数学公式
const renderedHtml = computed(() => {
  if (!props.content) return ''
  
  try {
    // 处理行内公式: $...$ 或 \(...\)
    // 处理块级公式: $$...$$ 或 \[...\]
    let html = props.content
    
    // 先处理块级公式 $$...$$
    html = html.replace(/\$\$([\s\S]+?)\$\$/g, (match, formula) => {
      try {
        return katex.renderToString(formula.trim(), {
          displayMode: true,
          throwOnError: false,
          output: 'html'
        })
      } catch (e) {
        console.error('块级公式渲染失败:', formula, e)
        return `<span class="katex-error">${formula}</span>`
      }
    })
    
    // 处理块级公式 \[...\]
    html = html.replace(/\\\[([\s\S]+?)\\\]/g, (match, formula) => {
      try {
        return katex.renderToString(formula.trim(), {
          displayMode: true,
          throwOnError: false,
          output: 'html'
        })
      } catch (e) {
        console.error('块级公式渲染失败:', formula, e)
        return `<span class="katex-error">${formula}</span>`
      }
    })
    
    // 处理行内公式 $...$（但不匹配$$）
    html = html.replace(/(?<!\$)\$(?!\$)([\s\S]+?)(?<!\$)\$(?!\$)/g, (match, formula) => {
      try {
        return katex.renderToString(formula.trim(), {
          displayMode: false,
          throwOnError: false,
          output: 'html'
        })
      } catch (e) {
        console.error('行内公式渲染失败:', formula, e)
        return `<span class="katex-error">$${formula}$</span>`
      }
    })
    
    // 处理行内公式 \(...\)
    html = html.replace(/\\\(([\s\S]+?)\\\)/g, (match, formula) => {
      try {
        return katex.renderToString(formula.trim(), {
          displayMode: false,
          throwOnError: false,
          output: 'html'
        })
      } catch (e) {
        console.error('行内公式渲染失败:', formula, e)
        return `<span class="katex-error">\\(${formula}\\)</span>`
      }
    })
    
    // 将换行符转换为<br>
    html = html.replace(/\n/g, '<br>')
    
    return html
  } catch (error) {
    console.error('内容渲染失败:', error)
    return props.content
  }
})
</script>

<style scoped>
.math-preview {
  line-height: 1.8;
}

.math-preview :deep(.katex) {
  font-size: 1.1em;
}

.math-preview :deep(.katex-display) {
  margin: 1em 0;
  overflow-x: auto;
  overflow-y: hidden;
}

.math-preview :deep(.katex-error) {
  color: #f56c6c;
  background: #fef0f0;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 0.9em;
}
</style>
