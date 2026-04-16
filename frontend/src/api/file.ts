import request from '@/utils/request'

export interface UploadResult {
  url: string
  fileName: string
  originalName: string
  size: string
}

// 上传图片文件
export const uploadImage = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<UploadResult>('/files/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
