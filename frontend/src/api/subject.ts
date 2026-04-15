import request from '@/utils/request'

export interface Subject {
  subjectId: number
  subjectName: string
  grade: string
  status: number
}

export interface Knowledge {
  knowledgeId: number
  knowledgeName: string
  subjectId: number
  parentId: number
  level: number
}

export function listSubjects(): Promise<Subject[]> {
  return request.get('/subjects')
}

export function getKnowledgeBySubject(subjectId: number): Promise<Knowledge[]> {
  return request.get(`/subjects/${subjectId}/knowledge`)
}
