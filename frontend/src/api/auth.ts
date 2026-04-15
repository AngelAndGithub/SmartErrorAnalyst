import request from '@/utils/request'

export interface LoginParams {
  username: string
  password: string
}

export interface RegisterParams {
  username: string
  password: string
  name: string
  role?: number
  grade?: string
}

export interface UserInfo {
  userId: number
  username: string
  name: string
  role: number
  grade?: string
  classId?: number
  token: string
}

export function login(data: LoginParams): Promise<UserInfo> {
  return request.post('/auth/login', data)
}

export function register(data: RegisterParams): Promise<UserInfo> {
  return request.post('/auth/register', data)
}
