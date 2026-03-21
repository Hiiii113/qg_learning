<template>
    <div class="page-container">
        <div class="page-box">
            <h2>学生端</h2>

            <!-- 导航按钮 -->
            <div class="nav-bar">
                <button :class="{ active: currentView === 'bind' }" @click="currentView = 'bind'">
                    绑定宿舍
                </button>
                <button
                    :class="{ active: currentView === 'create' }"
                    @click="currentView = 'create'"
                >
                    创建报修单
                </button>
                <button :class="{ active: currentView === 'repairs' }" @click="loadMyRepairs">
                    我的报修单
                </button>
                <button
                    :class="{ active: currentView === 'cancel' }"
                    @click="currentView = 'cancel'"
                >
                    取消报修单
                </button>
                <button
                    :class="{ active: currentView === 'password' }"
                    @click="currentView = 'password'"
                >
                    修改密码
                </button>
            </div>

            <!-- 提示信息 -->
            <div v-if="msg" class="msg" :class="msgType">{{ msg }}</div>

            <!-- 绑定宿舍 -->
            <div v-if="currentView === 'bind'">
                <div class="form-item">
                    <label>宿舍号</label>
                    <input v-model="dormRoom" placeholder="例如：西1-001" />
                </div>
                <div class="actions">
                    <button class="btn-primary" @click="handleBindDormitory">保存</button>
                    <button class="btn-ghost" @click="dormRoom = ''">清空</button>
                </div>
            </div>

            <!-- 创建报修单 -->
            <div v-if="currentView === 'create'">
                <div class="form-item">
                    <label>问题描述</label>
                    <textarea
                        v-model="repairProblem"
                        placeholder="请描述清楚：位置/现象/是否紧急"
                        rows="5"
                    ></textarea>
                </div>
                <div class="actions">
                    <button class="btn-primary" @click="handleCreateRepair">提交报修</button>
                    <button class="btn-ghost" @click="repairProblem = ''">清空</button>
                </div>
            </div>

            <!-- 我的报修单 -->
            <div v-if="currentView === 'repairs'">
                <button class="btn-refresh" @click="loadMyRepairs">刷新</button>
                <div v-if="myRepairs.length === 0" class="empty">暂无报修记录</div>
                <table v-else class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>问题</th>
                            <th>状态</th>
                            <th>创建时间</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="item in myRepairs" :key="item.id">
                            <td>{{ item.id }}</td>
                            <td>{{ item.problem }}</td>
                            <td>{{ statusMap[item.status] }}</td>
                            <td>{{ item.createTime }}</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- 取消报修单 -->
            <div v-if="currentView === 'cancel'">
                <div class="form-item">
                    <label>报修单 ID</label>
                    <input v-model="cancelId" type="number" placeholder="请输入报修单ID" />
                </div>
                <div class="actions">
                    <button class="btn-danger" @click="handleCancelRepair">确认取消</button>
                    <button class="btn-ghost" @click="cancelId = ''">清空</button>
                </div>
            </div>

            <!-- 修改密码 -->
            <div v-if="currentView === 'password'">
                <div class="form-item">
                    <label>新密码</label>
                    <input v-model="newPassword" type="password" placeholder="请输入新密码" />
                </div>
                <div class="actions">
                    <button class="btn-primary" @click="handleChangePassword">保存</button>
                    <button class="btn-ghost" @click="newPassword = ''">清空</button>
                </div>
            </div>

            <div class="footer">
                <span class="user-info">学生 ID: {{ userInfo.id }}</span>
                <span class="user-info">宿舍: {{ userInfo.dormRoom }}</span>
                <button class="btn-logout" @click="handleLogout">退出登录</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { get, post, put } from '@/utils/request.js'

const router = useRouter()

// 将存储的用户信息反序列化
let userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

// 默认是查看自己的报修单
const currentView = ref('repairs')

// 提示消息
const msg = ref('')
const msgType = ref('success')

// 展示提示消息
const showMsg = (text, type = 'success') => {
    msg.value = text
    msgType.value = type
    setTimeout(() => {
        msg.value = ''
    }, 3000)
}

// 状态映射
const statusMap = { 1: '待处理', 2: '处理中', 3: '已完成', 4: '已取消' }

// 数据
const dormRoom = ref('')
const repairProblem = ref('')
const myRepairs = ref([])
const cancelId = ref('')
const newPassword = ref('')

// 绑定宿舍
const handleBindDormitory = () => {
    if (dormRoom.value === '') {
        showMsg('宿舍地址不能为空！', 'error')
        return
    }
    const data = { dormRoom: dormRoom.value }
    post(`/users/${userInfo.value.id}/dormitories`, data)
        .then(() => {
            showMsg('绑定成功！')
            dormRoom.value = ''
            // 重新加载用户数据
            loadUserInfo()
        })
        .catch(() => showMsg('绑定失败！', 'error'))
}

// 创建报修单
const handleCreateRepair = () => {
    if (repairProblem.value === '') {
        showMsg('问题描述不能为空！', 'error')
        return
    }
    const data = { userId: userInfo.value.id, problem: repairProblem.value }
    post('/repair-orders', data)
        .then(() => {
            showMsg('提交成功！')
            repairProblem.value = ''
            loadMyRepairs()
        })
        .catch(() => showMsg('提交失败！', 'error'))
}

// 加载我的报修单
const loadMyRepairs = () => {
    currentView.value = 'repairs'
    get(`/repair-orders/user/${userInfo.value.id}`)
        .then((res) => {
            myRepairs.value = res.data || []
        })
        .catch(() => showMsg('查询失败！', 'error'))
}

// 取消报修单
const handleCancelRepair = () => {
    put(`/repair-orders/${cancelId.value}/cancel`, {})
        .then(() => {
            showMsg('取消成功！')
            cancelId.value = ''
            loadMyRepairs()
        })
        .catch(() => showMsg('取消失败！', 'error'))
}

// 修改密码
const handleChangePassword = () => {
    if (newPassword.value === '') {
        showMsg('新密码不能为空！', 'error')
        return
    }
    const data = { password: newPassword.value }
    put(`/users/${userInfo.value.id}/password`, data)
        .then(() => {
            showMsg('修改成功！')
            newPassword.value = ''
        })
        .catch(() => showMsg('修改失败！', 'error'))
}

// 退出登录
const handleLogout = () => {
    localStorage.removeItem('userInfo')
    router.push('/login')
}

// 加载用户数据
const loadUserInfo = () => {
    get(`/users/${userInfo.value.id}`)
        .then((res) => {
            localStorage.setItem('userInfo', JSON.stringify(res.data))
            // 将存储的用户信息反序列化
            userInfo.value = res.data
        })
        .catch((err) => console.log(err))
}

// 检查是否绑定了宿舍
const checkDorm = () => {
    if (!userInfo.value.dormRoom) {
        showMsg('请先绑定宿舍', 'error')
        currentView.value = 'bind'
    }
}

// 进入页面加载数据
loadMyRepairs()
loadUserInfo()
// 检查是否绑定了宿舍
checkDorm()
</script>

<style scoped>
.page-container {
    min-height: 100vh;
    display: flex;
    align-items: flex-start;
    justify-content: center;
    background: #f5f5f5;
    padding: 40px 20px;
}

.page-box {
    width: 700px;
    padding: 30px 40px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

h2 {
    margin: 0 0 20px;
    text-align: center;
    color: #333;
}

.nav-bar {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
    margin-bottom: 20px;
}

.nav-bar button {
    padding: 8px 14px;
    border: 1px solid #ddd;
    border-radius: 4px;
    background: #fff;
    color: #666;
    cursor: pointer;
    font-size: 13px;
}

.nav-bar button.active {
    background: #409eff;
    color: #fff;
    border-color: #409eff;
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

.form-item {
    margin-bottom: 16px;
}

.form-item label {
    display: block;
    margin-bottom: 6px;
    color: #555;
    font-size: 14px;
    font-weight: 600;
}

input,
textarea {
    width: 100%;
    padding: 10px 12px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
    box-sizing: border-box;
}

input:focus,
textarea:focus {
    outline: none;
    border-color: #409eff;
}

textarea {
    resize: vertical;
    min-height: 100px;
}

.actions {
    margin-top: 10px;
    display: flex;
    gap: 10px;
}

.btn-primary {
    padding: 10px 20px;
    background: #409eff;
    color: #fff;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
}

.btn-primary:hover {
    background: #66b1ff;
}

.btn-danger {
    padding: 10px 20px;
    background: #f56c6c;
    color: #fff;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
}

.btn-danger:hover {
    background: #f78989;
}

.btn-ghost {
    padding: 10px 20px;
    background: #fff;
    color: #666;
    border: 1px solid #ddd;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
}

.btn-ghost:hover {
    color: #333;
}

.btn-refresh {
    padding: 6px 14px;
    background: #fff;
    border: 1px solid #ddd;
    border-radius: 4px;
    cursor: pointer;
    font-size: 13px;
    margin-bottom: 12px;
}

.btn-refresh:hover {
    color: #409eff;
    border-color: #409eff;
}

.table {
    width: 100%;
    border-collapse: collapse;
}

.table th,
.table td {
    padding: 10px 12px;
    border-bottom: 1px solid #eee;
    text-align: left;
    font-size: 13px;
}

.table th {
    background: #fafafa;
    color: #555;
    font-weight: 600;
}

.table tr:hover td {
    background: #f5f7fa;
}

.empty {
    padding: 30px;
    text-align: center;
    color: #999;
    border: 1px dashed #ddd;
    border-radius: 6px;
}

.footer {
    margin-top: 30px;
    padding-top: 16px;
    border-top: 1px solid #eee;
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.user-info {
    color: #999;
    font-size: 13px;
}

.btn-logout {
    padding: 8px 16px;
    background: #fff;
    border: 1px solid #f56c6c;
    color: #f56c6c;
    border-radius: 4px;
    cursor: pointer;
    font-size: 13px;
}

.btn-logout:hover {
    background: #fef0f0;
}
</style>
