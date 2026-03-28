import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            alias: '/login',
            name: 'login',
            component: () => import('../views/LoginForm.vue')
        },
        {
            path: '/register',
            name: 'register',
            component: () => import('../views/RegisterForm.vue')
        },
        {
            path: '/student',
            name: 'student',
            component: () => import('../views/StudentMenu.vue')
        },
        {
            path: '/admin',
            name: 'admin',
            component: () => import('../views/AdminMenu.vue')
        },
    ],
})

export default router
