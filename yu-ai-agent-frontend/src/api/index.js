import axios from 'axios'

// 根据环境变量设置 API 基础 URL
// 在 Vite 中，使用相对路径，通过 Vite 代理转发到后端
const API_BASE_URL = '/api'

// 创建 axios 实例
const request = axios.create({
  baseURL: API_BASE_URL,
  timeout: 60000
})

// 封装 SSE 连接
export const connectSSE = (url, params, onMessage, onError) => {
  // 构建带参数的 URL
  const queryString = Object.keys(params)
    .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
    .join('&')
  
  const fullUrl = `${url}?${queryString}`
  
  console.log('SSE 连接 URL:', fullUrl)
  
  // 创建 EventSource
  const eventSource = new EventSource(fullUrl)
  
  eventSource.onmessage = event => {
    let data = event.data
    console.log('收到 SSE 消息:', data)
    
    // 检查是否是特殊标记
    if (data === '[DONE]') {
      if (onMessage) onMessage('[DONE]')
    } else {
      // 处理普通消息
      if (onMessage) onMessage(data)
    }
  }
  
  eventSource.onerror = error => {
    console.error('SSE 错误:', error)
    if (onError) onError(error)
    eventSource.close()
  }
  
  // 返回 eventSource 实例，以便后续可以关闭连接
  return eventSource
}

// AI 恋爱大师聊天
export const chatWithLoveApp = (message, chatId) => {
  return connectSSE('/ai/love_app/chat/sse', { message, chatId })
}

// AI 超级智能体聊天
export const chatWithManus = (message) => {
  return connectSSE('/ai/manus/chat', { message })
}

export default {
  chatWithLoveApp,
  chatWithManus
}
