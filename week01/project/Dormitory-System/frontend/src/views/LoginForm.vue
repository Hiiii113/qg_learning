<template>
    <div class="login-container">
        <div class="login-box">
            <h2>登录</h2>
            <!-- 登录表单 -->
            <form @submit.prevent="handleLogin">
                <div class="form-item">
                    <input
                        v-model="form.userNumber"
                        type="text"
                        placeholder="学号/工号"
                        required
                    />
                </div>
                <div class="form-item">
                    <input
                        v-model="form.password"
                        type="password"
                        placeholder="密码"
                        required
                    />
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
import {ref} from 'vue'
import {useRouter} from 'vue-router'
import {post} from "@/utils/request.js";

const router = useRouter()

// 绑定用户输入
const form = ref({
    userNumber: '',
    password: ''
})

const handleLogin = async () => {
    if (!form.value.userNumber || !form.value.password) {
        alert('请输入用户名和密码')
        return
    }

    // 封装返回对象
    const loginData = {
        userNumber: form.value.userNumber,
        password: form.value.password
    }

    post('/users/login', loginData)
        .then(res => {
            localStorage.setItem('userInfo', JSON.stringify(res.data))
            if (res.code === 200) {
                alert('登录成功！正在跳转...')
                if (res.data.role === 1) {
                    router.push('/student')
                } else {
                    router.push('/admin')
                }
            } else {
                alert('登录失败！' + res.msg || '请稍候重试')
                console.log(res)
            }

        })
        .catch(err => {
            alert('登录失败！' + err.msg || err.message || '请稍后重试')
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
</style>
