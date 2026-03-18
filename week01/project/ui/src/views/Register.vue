<template>
    <div class="register-container">
        <div class="register-box">
            <h2>注册</h2>
            <form @submit.prevent="handleRegister">
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
                <div class="form-item">
                    <input
                        v-model="form.confirmPassword"
                        type="password"
                        placeholder="确认密码"
                        required
                    />
                </div>
                <button type="submit" class="btn-primary">注册</button>
            </form>
            <p class="switch-text">
                已有账号？
                <router-link to="/login">立即登录</router-link>
            </p>
        </div>
    </div>
</template>

<script setup>
import {ref} from 'vue'
import {useRouter} from 'vue-router'
import { post, get, put, del, patch } from '../utils/request.js'

const router = useRouter()

const form = ref({
    userNumber: '',
    password: '',
    confirmPassword: '',
    role: 1
})
const loading = ref(false)

const handleRegister = async () => {
    if (form.value.password !== form.value.confirmPassword) {
        alert('两次密码输入不一致')
        return
    }
    if (!form.value.userNumber || !form.value.password) {
        alert('请填写完整信息')
        return
    }

    const registerData = {
        userNumber: form.value.userNumber,
        password: form.value.password,
        role: form.value.role,
    }

    post('/users', registerData)
        .then(res => {
            alert('注册成功！正在跳转...')
            console.log('注册成功返回信息：' + res.data)
        })
}
</script>

<style scoped>
.register-container {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f5f5;
}

.register-box {
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
