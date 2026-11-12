import request from '../utils/request'

// 首页统计
export const getDashboardSummary = () => request.get('/dashboard/summary')

// 工单列表
export const getTodoList = (params) => request.get('/workorder/todo', { params })
export const getAuditList = (params) => request.get('/workorder/audit', { params })
export const getDoneList = (params) => request.get('/workorder/done', { params })
export const queryWorkorderList = (params) => request.get('/workorder/query', { params })

// 工单详情 & 操作
export const getWorkorderDetail = (id) => request.get(`/workorder/${id}`, { timeout: 30000 })
export const handleWorkorder = (id, data) => request.post(`/workorder/${id}/handle`, data)
export const auditPass = (id) => request.post(`/workorder/${id}/audit-pass`)
export const auditReject = (id, data) => request.post(`/workorder/${id}/audit-reject`, data)
