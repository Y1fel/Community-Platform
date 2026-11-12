import request from '../utils/request'

export const login = (data) => request.post('/auth/login', data)
export const getUserInfo = () => request.get('/auth/profile')
export const logout = () => request.post('/auth/logout')