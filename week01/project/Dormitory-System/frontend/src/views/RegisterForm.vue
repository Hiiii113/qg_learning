<template>
    <div class="register-container">
        <div class="register-box">
            <h2>注册</h2>
            <div v-if="msg" class="msg" :class="msgType">{{ msg }}</div>
            <!-- 注册表单 -->
            <form @submit.prevent="handleRegister">
                <div class="form-item">
                    <input v-model="form.userNumber" type="text" placeholder="学号/工号" required />
                </div>
                <div class="form-item">
                    <select v-model="form.role" class="role-select">
                        <option :value="0" disabled hidden>身份</option>
                        <option :value="1">学生</option>
                        <option :value="2">管理员</option>
                    </select>
                </div>
                <div class="form-item">
                    <input v-model="form.password" type="password" placeholder="密码" required />
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { post } from '../utils/request.js'

const router = useRouter()

// 绑定用户输入
const form = ref({
    userNumber: '',
    password: '',
    confirmPassword: '',
    role: 0,
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

// 处理注册操作
const handleRegister = async () => {
    // 验证
    if (form.value.password !== form.value.confirmPassword) {
        alert('两次密码输入不一致')
        return
    }
    if (!form.value.userNumber || !form.value.password) {
        alert('请填写完整信息')
        return
    }
    if (form.value.role === 0) {
        alert('请选择身份！')
        return
    }

    // 封装返回对象
    const registerData = {
        userNumber: form.value.userNumber,
        password: form.value.password,
        role: form.value.role,
    }

    post('/users', registerData)
        .then((res) => {
            if (res.code === 201) {
                showMsg('注册成功！正在跳转...')
                setTimeout(() => {
                    router.push('/login')
                }, 3000)
            } else {
                showMsg('注册失败！' + res.msg || '请稍后再试', 'error')
            }
        })
        .catch((err) => {
            showMsg('注册失败！' + err.msg || err.message || '请稍后再试', 'error')
            console.log(err)
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

.role-select {
    width: 100%;
    padding: 12px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
    box-sizing: border-box;
    background: #fff;
    cursor: pointer;
}

.role-select:focus {
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
