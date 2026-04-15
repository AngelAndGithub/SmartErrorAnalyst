import request from '@/utils/request'

export interface AIAnalysisRequest {
  questionContent: string
  correctAnswer?: string
  userAnswer?: string
  subjectId?: number
  errorReason?: string
  provider?: string // AI提供商: zhipu/deepseek/mock
}

export interface AIAnalysisResult {
  analysis: string
  knowledgePoint?: string
  suggestions?: string
}

export interface AIProviderInfo {
  name: string
  description: string
  url: string
}

// 智能解析错题
export const analyzeErrorQuestion = (data: AIAnalysisRequest) => {
  return request.post<AIAnalysisResult>('/ai/analyze', data)
}

// 获取可用的AI提供商列表
export const getAvailableProviders = () => {
  return request.get<string[]>('/ai/providers')
}

// 获取AI提供商详细信息
export const getProvidersInfo = () => {
  return request.get<Record<string, AIProviderInfo>>('/ai/providers/info')
}
