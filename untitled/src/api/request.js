import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    timeout: 10000
})

// 请求拦截器
request.interceptors.request.use((config) => {
    const token = localStorage.getItem('token')
    if (token) config.headers.Authorization = `Bearer ${token}`
    return config
})

// 响应拦截器
request.interceptors.response.use(
    (res) => {
        if (res.data.code !== 200) {
            ElMessage.error(res.data.message || '请求失败')
            return Promise.reject(res.data)
        }
        return res.data.data
    },
    (err) => {
        ElMessage.error(err.response?.data?.message || '网络错误')
        if (err.response?.status === 401) {
            localStorage.removeItem('token')
            window.location.href = '/#/login'
        }
        return Promise.reject(err)
    }
)

export default request
