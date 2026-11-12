import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
    baseURL: '/api',
    timeout: 10000
})

// 请求拦截器：自动加 token
request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers['Authorization'] = 'Bearer ' + token
        }
        return config
    },
    error => Promise.reject(error)
)

// 响应拦截器：解包 Result 结构
request.interceptors.response.use(
    response => {
        const res = response.data
        if (res.code !== 200) {
            ElMessage.error(res.message || '请求失败')
            return Promise.reject(res)
        }
        return res.data
    },
    error => {
        const status = error.response?.status
        switch (status) {
            case 401:
                ElMessage.error('登录已过期，请重新登录')
                localStorage.removeItem('token')
                window.location.href = '/login'
                break
            case 403:
                ElMessage.error('没有权限')
                break
            case 404:
                ElMessage.error('资源不存在')
                break
            case 409:
                ElMessage.error('请求冲突，请稍后再试')
                break
            case 429:
                ElMessage.error('请求过于频繁，请稍后再试')
                break
            case 500:
                ElMessage.error('服务器错误')
                break
            default:
                ElMessage.error(error.response?.data?.message || '网络错误')
        }
        return Promise.reject(error)
    }
)

export default request