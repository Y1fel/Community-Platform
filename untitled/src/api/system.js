import request from './request'

// 组织管理
export const getOrgList = () => request.get('/system/org')
export const addOrg = (data) => request.post('/system/org', data)
export const updateOrg = (id, data) => request.put(`/system/org/${id}`, data)
export const deleteOrg = (id) => request.delete(`/system/org/${id}`)

// 用户管理
export const getUserList = (params) => request.get('/system/user', { params })
export const addUser = (data) => request.post('/system/user', data)
export const updateUser = (id, data) => request.put(`/system/user/${id}`, data)
export const deleteUser = (id) => request.delete(`/system/user/${id}`)
export const batchDeleteUser = (ids) => request.post('/system/user/batch-delete', { ids })

// 数据字典
export const getDictList = (params) => request.get('/system/dict', { params })
export const addDict = (data) => request.post('/system/dict', data)
export const updateDict = (id, data) => request.put(`/system/dict/${id}`, data)
export const deleteDict = (id) => request.delete(`/system/dict/${id}`)

// 日志管理
export const getLogList = (params) => request.get('/system/log', { params })
