import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
    const token = ref(localStorage.getItem('token') || '')
    const userInfo = ref({})

    const setToken = (val) => {
        token.value = val
        localStorage.setItem('token', val)
    }

    const setUserInfo = (val) => {
        userInfo.value = val
    }

    const clearToken = () => {
        token.value = ''
        userInfo.value = {}
        localStorage.removeItem('token')
    }

    return { token, userInfo, setToken, setUserInfo, clearToken }
})
