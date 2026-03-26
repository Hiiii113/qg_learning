<template>
    <div class="login-container">
        <div class="login-box">
            <h2>登录</h2>
            <div v-if="msg" class="msg" :class="msgType">{{ msg }}</div>
            <!-- 登录表单 -->
            <form @submit.prevent="handleLogin">
                <div class="form-item">
                    <input v-model="form.userNumber" type="text" placeholder="学号/工号" required />
                </div>
                <div class="form-item">
                    <input v-model="form.password" type="password" placeholder="密码" required />
                </div>
                <button type="submit" class="btn-primary">登录</button>
            </form>
            <p class="switch-text">
                还没有账号？
                <router-link to="/register">立即注册</router-link>
            </p>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { get, post } from '@/utils/request.js'

const router = useRouter()

// 绑定用户输入
const form = ref({
    userNumber: '',
    password: '',
})

const msg = ref('')
const msgType = ref('success')

const showMsg = (text, type = 'success') => {
    msg.value = text
    msgType.value = type
    // 三秒后清除
    setTimeout(() => {
        msg.value = ''
    }, 3000)
}

const handleLogin = async () => {
    if (!form.value.userNumber || !form.value.password) {
        alert('请输入用户名和密码')
        return
    }

    // 封装返回对象
    const loginData = {
        userNumber: form.value.userNumber,
        password: form.value.password,
    }

    post('/users/login', loginData)
        .then( async (res) => {
            if (res.code === 200) {
                localStorage.setItem('token', res.data)
                // 获取 userInfo 并等待请求完成
                const userRes = await get('/users/info')
                // 存在 localStorage 和变量里
                const userInfo = userRes.data
                localStorage.setItem('userInfo', JSON.stringify(userInfo))
                // 提示
                showMsg('登录成功！正在跳转...', 'success')
                // 跳转
                setTimeout(() => {
                    // 直接用 userInfo 判断角色
                    if (userInfo.role === 1) {
                        router.push('/student')
                    } else if (userInfo.role === 2) {
                        router.push('/admin')
                    } else {
                        showMsg('身份异常，请联系管理员！', 'error')
                    }
                }, 1500)
        }})
        .catch((err) => {
            showMsg('登录失败！' + err.msg || err.message || '请稍后重试', 'error')
            console.log(err)
        })
}
</script>

<style scoped>
.login-container {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f5f5;
}

.login-box {
    width: 360px;
    padding: 40px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

h2 {
    margin: 0 0 30px;
    text-align: center;
    color: #333;
}

.form-item {
    margin-bottom: 20px;
}

input {
    width: 100%;
    padding: 12px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
    box-sizing: border-box;
}

input:focus {
    outline: none;
    border-color: #409eff;
}

.btn-primary {
    width: 100%;
    padding: 12px;
    background: #409eff;
    color: #fff;
    border: none;
    border-radius: 4px;
    font-size: 16px;
    cursor: pointer;
}

.btn-primary:hover {
    background: #66b1ff;
}

.switch-text {
    margin-top: 20px;
    text-align: center;
    color: #666;
}

.switch-text a {
    color: #409eff;
    text-decoration: none;
}

.switch-text a:hover {
    text-decoration: underline;
}

.msg {
    padding: 10px 14px;
    border-radius: 4px;
    margin-bottom: 16px;
    font-size: 14px;
}

.msg.success {
    background: #f0f9eb;
    color: #67c23a;
    border: 1px solid #c2e7b0;
}

.msg.error {
    background: #fef0f0;
    color: #f56c6c;
    border: 1px solid #fbc4c4;
}
</style>
