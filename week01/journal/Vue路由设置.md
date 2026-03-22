# Vue路由设置

在前端写好了几个 Vue 组件并需要相互跳转时，需要手动设置路由

示例：

```javascript
import {createRouter, createWebHistory} from 'vue-router'  
  
const router = createRouter({  
    history: createWebHistory(import.meta.env.BASE_URL),  
    routes: [  
        {   path: '/',  		// 地址
            alias: '/login',  	// 别名，和地址的作用一样
            name: 'login',  	// 名字
            component: () => import('../views/Login.vue') // 懒加载
        },  
        {  
            path: '/register',  
            name: 'register',  
            component: () => import('../views/Register.vue')  
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

// 导出
export default router
