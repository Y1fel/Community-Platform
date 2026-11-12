import request from '@/utils/request'

export function submitReport(data) {
  return request({
    url: '/workorder/submit',
    method: 'post',
    data
  })
}

export function getReportDetail(id) {
  return request({
    url: `/workorder/${id}`,
    method: 'get'
  })
}
