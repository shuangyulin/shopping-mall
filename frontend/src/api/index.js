import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 12000
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('mall_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => {
    const result = response.data
    if (result.code !== 0) {
      return Promise.reject(new Error(result.message || '请求失败'))
    }
    return result.data
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('mall_token')
      localStorage.removeItem('mall_user')
    }
    const message = error.response?.data?.message || error.message || '网络请求失败'
    return Promise.reject(new Error(message))
  }
)

export default api
