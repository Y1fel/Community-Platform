import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/login/index.vue')
    },
    {
        path: '/',
        component: () => import('@/layouts/MainLayout.vue'),
        redirect: '/dashboard',
        children: [
            {
                path: 'dashboard',
                component: () => import('@/views/dashboard/index.vue'),
                meta: { title: '首页' }
            },
            {
                path: 'workorder',
                meta: { title: '事件中心' },
                children: [
                    { path: 'todo', component: () => import('@/views/workorder/todo/index.vue'), meta: { title: '待办工单' } },
                    { path: 'audit', component: () => import('@/views/workorder/audit/index.vue'), meta: { title: '审核工单' } },
                    { path: 'done', component: () => import('@/views/workorder/done/index.vue'), meta: { title: '已办工单' } },
                    { path: 'query', component: () => import('@/views/workorder/query/index.vue'), meta: { title: '工单查询' } },
                    { path: 'handle/:id', component: () => import('@/views/workorder/handle/index.vue'), meta: { title: '处理工单' } }
                ]
            }
            // 系统管理后端未实现，暂时隐藏
            // {
            //     path: 'system',
            //     meta: { title: '系统管理' },
            //     children: [
            //         { path: 'org', component: () => import('@/views/system/org/index.vue'), meta: { title: '组织管理' } },
            //         { path: 'user', component: () => import('@/views/system/user/index.vue'), meta: { title: '用户管理' } },
            //         { path: 'dict', component: () => import('@/views/system/dict/index.vue'), meta: { title: '数据字典' } },
            //         { path: 'log', component: () => import('@/views/system/log/index.vue'), meta: { title: '日志管理' } }
            //     ]
            // }
        ]
    }
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    if (to.path !== '/login' && !token) {
        next('/login')
    } else {
        next()
    }
})

export default router