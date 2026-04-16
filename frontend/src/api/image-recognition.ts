import request from '@/utils/request'

export interface ImageRecognitionRequest {
  imageBase64: string
  type: 'question' | 'answer' | 'userAnswer'
}

// 识别图片中的文字
export const recognizeImage = (data: ImageRecognitionRequest) => {
  return request.post<string>('/image/recognize', data)
}
