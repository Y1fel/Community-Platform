import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Scan',
    component: () => import('@/views/Scan.vue')
  },
  {
    path: '/submit',
    name: 'Submit',
    component: () => import('@/views/Submit.vue')
  },
  {
    path: '/success',
    name: 'Success',
    component: () => import('@/views/Success.vue')
  },
  {
    path: '/quiz',
    name: 'Quiz',
    component: () => import('@/views/Quiz.vue')
  },
  {
    path: '/ranking',
    name: 'Ranking',
    component: () => import('@/views/Ranking.vue')
  },
  {
    path: '/prizes',
    name: 'Prizes',
    component: () => import('@/views/Prizes.vue')
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
