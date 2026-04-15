import request from '@/utils/request'

export interface StatisticsData {
  totalCount: number
  masteredCount: number
  unmasteredCount: number
  subjectDistribution: Array<{ name: string; value: number }>
  knowledgeDistribution: Array<{ name: string; value: number }>
  errorReasonDistribution: Array<{ name: string; value: number }>
  difficultyDistribution: Array<{ name: string; value: number }>
  weakPoints: string[]
}

export function getUserStatistics(): Promise<StatisticsData> {
  return request.get('/statistics/user')
}
