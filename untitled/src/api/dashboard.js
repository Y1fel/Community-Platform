import request from '@/utils/request'

export const getDashboardSummary = () => {
    return request({
        url: '/dashboard/summary',
        method: 'get'
    })
}

export const getRecentPending = () => {
    return request({
        url: '/dashboard/recent/pending',
        method: 'get'
    })
}

export const getRecentAudit = () => {
    return request({
        url: '/dashboard/recent/audit',
        method: 'get'
    })
}

export const getRecentCompleted = () => {
    return request({
        url: '/dashboard/recent/completed',
        method: 'get'
    })
}