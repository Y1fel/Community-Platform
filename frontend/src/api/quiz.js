import request from '@/utils/request'

export function getQuestions(count = 5) {
  return request({
    url: '/quiz/questions',
    method: 'get',
    params: { count }
  })
}

export function submitAnswer(data) {
  return request({
    url: '/quiz/submit',
    method: 'post',
    data
  })
}

export function getUserScore(phone) {
  return request({
    url: '/quiz/score',
    method: 'get',
    params: { phone }
  })
}

export function getRanking(count = 10) {
  return request({
    url: '/quiz/ranking',
    method: 'get',
    params: { count }
  })
}

export function getPrizes() {
  return request({
    url: '/quiz/prizes',
    method: 'get'
  })
}

export function exchangePrize(data) {
  return request({
    url: '/quiz/exchange',
    method: 'post',
    data
  })
}

export function getExchangeRecords(phone) {
  return request({
    url: '/quiz/exchanges',
    method: 'get',
    params: { phone }
  })
}