import request from '@/utils/request'

export interface QuestionShare {
  shareId?: number
  shareNo?: string
  questionId: number
  sharerId?: number
  shareType: number // 1-公开 2-指定用户
  shareTitle?: string
  shareMessage?: string
  includeAnswer?: number
  includeAnalysis?: number
  includeNotes?: number
  viewCount?: number
  likeCount?: number
  status?: number
  createTime?: string
}

export interface ShareTarget {
  id?: number
  shareId: number
  targetUserId: number
  hasViewed?: number
  viewTime?: string
}

export interface ShareSolution {
  solutionId?: number
  shareId: number
  userId: number
  solutionContent: string
  solutionImages?: string
  likeCount?: number
  isBest?: number
  createTime?: string
}

export interface ShareDTO {
  questionId: number
  shareTitle?: string
  shareMessage?: string
  shareType: number
  targetUserIds?: number[]
  includeAnswer?: number
  includeAnalysis?: number
  includeNotes?: number
}

export interface ShareResultDTO {
  share: QuestionShare
  sharerName?: string
  questionContent?: string
  subjectName?: string
  solutionCount?: number
  hasLiked?: boolean
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 创建分享
export function createShare(data: ShareDTO): Promise<string> {
  return request.post('/shares', data)
}

// 获取我分享的列表
export function getMyShares(params: { pageNum: number; pageSize: number }): Promise<PageResult<ShareResultDTO>> {
  return request.get('/shares/my', { params })
}

// 获取收到的分享列表
export function getReceivedShares(params: { pageNum: number; pageSize: number }): Promise<PageResult<ShareResultDTO>> {
  return request.get('/shares/received', { params })
}

// 获取分享详情
export function getShareDetail(shareId: number): Promise<ShareResultDTO> {
  return request.get(`/shares/${shareId}`)
}

// 添加解题思路
export function addSolution(shareId: number, data: { content: string; images?: string }): Promise<number> {
  return request.post(`/shares/${shareId}/solution`, data)
}

// 获取解题思路列表
export function getSolutions(shareId: number): Promise<ShareSolution[]> {
  return request.get(`/shares/${shareId}/solutions`)
}

// 点赞分享
export function likeShare(shareId: number): Promise<void> {
  return request.post(`/shares/${shareId}/like`)
}

// 撤回分享
export function revokeShare(shareId: number): Promise<void> {
  return request.delete(`/shares/${shareId}`)
}

// 获取用户列表（用于选择分享对象）
export function getUserList(keyword?: string): Promise<Array<{ userId: number; username: string; nickname?: string }>> {
  return request.get('/users/list', { params: { keyword } })
}
