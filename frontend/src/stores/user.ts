import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface UserInfo {
  userId: number
  username: string
  name: string
  role: number
  grade?: string
  classId?: number
}

export const useUserStore = defineStore('user', () => {
  // State
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)

  // Getters
  const isLoggedIn = computed(() => !!token.value)
  const isStudent = computed(() => userInfo.value?.role === 0)
  const isTeacher = computed(() => userInfo.value?.role === 1)
  const isAdmin = computed(() => userInfo.value?.role === 2)

  // Actions
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  const initUserInfo = () => {
    const stored = localStorage.getItem('userInfo')
    if (stored) {
      userInfo.value = JSON.parse(stored)
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isStudent,
    isTeacher,
    isAdmin,
    setToken,
    setUserInfo,
    logout,
    initUserInfo
  }
})
