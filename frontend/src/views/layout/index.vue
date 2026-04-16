<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="sidebar">
      <div class="logo">
        <span>错题智析宝</span>
      </div>
      <el-menu
        :default-active="$route.path"
        class="sidebar-menu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/error-questions">
          <el-icon><Document /></el-icon>
          <span>错题管理</span>
        </el-menu-item>
        <el-menu-item index="/shares">
          <el-icon><Share /></el-icon>
          <span>错题分享</span>
        </el-menu-item>
        <el-menu-item index="/review">
          <el-icon><Calendar /></el-icon>
          <span>复习计划</span>
        </el-menu-item>
        <el-menu-item index="/analysis">
          <el-icon><TrendCharts /></el-icon>
          <span>学情分析</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              {{ userStore.userInfo?.name }}
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { HomeFilled, Document, Calendar, TrendCharts, ArrowDown, Share } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = (command: string) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
    })
  } else if (command === 'profile') {
    // 个人中心功能待实现
    ElMessage.info('个人中心功能开发中')
  }
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  width: 100%;
  display: flex;
}

.layout-container :deep(.el-container) {
  width: 100%;
}

.sidebar {
  background-color: #304156;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  z-index: 10;
  flex-shrink: 0;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 1px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.sidebar-menu {
  border-right: none;
  width: 200px !important;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  font-size: 14px;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background-color: #263445 !important;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: #1890ff !important;
  border-right: 3px solid #fff;
}

.header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: flex-end;
  height: 64px;
  padding: 0 24px;
  flex-shrink: 0;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  cursor: pointer;
  color: #606266;
  font-size: 14px;
  padding: 8px 12px;
  border-radius: 4px;
  transition: all 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
  color: #409EFF;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
  min-height: calc(100vh - 64px);
  width: 100%;
  box-sizing: border-box;
}
</style>
