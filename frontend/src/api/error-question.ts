import request from '@/utils/request'

export interface ErrorQuestion {
  questionId?: number
  subjectId: number
  knowledgeId?: number
  questionType: number
  difficulty: number
  errorReason: string
  questionContent: string
  questionImages?: string
  options?: string
  correctAnswer: string
  answerImages?: string
  userAnswer: string
  userAnswerImages?: string
  analysis?: string
  notes?: string
  tags?: string
  masteryStatus?: number
  createTime?: string
}

export interface ErrorQuestionQuery {
  subjectId?: number
  knowledgeId?: number
  questionType?: number
  difficulty?: number
  errorReason?: string
  keyword?: string
  masteryStatus?: number
  pageNum?: number
  pageSize?: number
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export function addErrorQuestion(data: ErrorQuestion): Promise<void> {
  return request.post('/error-questions', data)
}

export function updateErrorQuestion(id: number, data: ErrorQuestion): Promise<void> {
  return request.put(`/error-questions/${id}`, data)
}

export function deleteErrorQuestion(id: number): Promise<void> {
  return request.delete(`/error-questions/${id}`)
}

export function getErrorQuestionDetail(id: number): Promise<ErrorQuestion> {
  return request.get(`/error-questions/${id}`)
}

export function listErrorQuestions(params: ErrorQuestionQuery): Promise<PageResult<ErrorQuestion>> {
  return request.get('/error-questions', { params })
}

export function updateMasteryStatus(id: number, status: number): Promise<void> {
  return request.put(`/error-questions/${id}/mastery`, null, { params: { status } })
}
