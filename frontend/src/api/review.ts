import request from '@/utils/request'

export interface ReviewPlan {
  planId: number
  questionId: number
  userId: number
  planTime: string
  actualTime?: string
  reviewResult: number
  reviewCount: number
}

export interface ReviewResultParams {
  planId: number
  reviewResult: number
}

export function getTodayPlans(): Promise<ReviewPlan[]> {
  return request.get('/review-plans/today')
}

export function countTodayPlans(): Promise<number> {
  return request.get('/review-plans/today/count')
}

export function getReviewQuestion(planId: number): Promise<any> {
  return request.get(`/review-plans/${planId}/question`)
}

export function submitReviewResult(data: ReviewResultParams): Promise<void> {
  return request.post('/review-plans/result', data)
}

export function getReviewHistory(questionId: number): Promise<ReviewPlan[]> {
  return request.get(`/review-plans/history/${questionId}`)
}
