<template>
    <div class="page-container">
        <div class="page-box">
            <h2>管理员端</h2>

            <!-- 导航按钮 -->
            <div class="nav-bar">
                <button :class="{ active: currentView === 'list' }" @click="currentView = 'list'">
                    所有报修单
                </button>
                <button
                    :class="{ active: currentView === 'detail' }"
                    @click="currentView = 'detail'"
                >
                    查看详情
                </button>
                <button
                    :class="{ active: currentView === 'update' }"
                    @click="currentView = 'update'"
                >
                    更新状态
                </button>
                <button
                    :class="{ active: currentView === 'delete' }"
                    @click="currentView = 'delete'"
                >
                    删除报修单
                </button>
                <button
                    :class="{ active: currentView === 'password' }"
                    @click="currentView = 'password'"
                >
                    修改密码
                </button>
            </div>

            <!-- 提示信息msg -->
            <div v-if="msg" class="msg" :class="msgType">{{ msg }}</div>

            <!-- 所有报修单 -->
            <div v-if="currentView === 'list'">
                <div class="list-toolbar">
                    <button class="btn-refresh" @click="selectRepairByStatus">↻ 刷新</button>
                    <select
                        v-model="selectStatus"
                        @change="selectRepairByStatus"
                        class="filter-select"
                    >
                        <option :value="0">全部状态</option>
                        <option :value="1">待处理</option>
                        <option :value="2">处理中</option>
                        <option :value="3">已完成</option>
                        <option :value="4">已取消</option>
                    </select>
                </div>
                <div v-if="repairs.length === 0" class="empty">暂无报修单</div>
                <table v-else class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>用户ID</th>
                            <th>问题</th>
                            <th>状态</th>
                            <th>创建时间</th>
                        </tr>
                    </thead>
                    <tbody>
                        <!-- 点击跳转详情页面 -->
                        <tr v-for="item in repairs" :key="item.id" @click="viewDetail(item.id)">
                            <td>{{ item.id }}</td>
                            <td>{{ item.userId }}</td>
                            <td>{{ item.problem }}</td>
                            <td>{{ statusMap[item.status] }}</td>
                            <td>{{ item.createTime }}</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- 查看详情 -->
            <div v-if="currentView === 'detail'">
                <div class="form-item">
                    <label>报修单 ID</label>
                    <input v-model="detailId" type="number" placeholder="请输入报修单ID" />
                </div>
                <div class="actions">
                    <button class="btn-primary" @click="loadDetail">查询</button>
                    <button class="btn-ghost" @click="((updateId = ''), (updateStatus = 1))">
                        清空
                    </button>
                </div>
                <div v-if="detailInfo" class="detail-card">
                    <p><strong>ID：</strong>{{ detailInfo.id }}</p>
                    <p><strong>用户ID：</strong>{{ detailInfo.userId }}</p>
                    <p><strong>状态：</strong>{{ statusMap[detailInfo.status] }}</p>
                    <p><strong>问题：</strong>{{ detailInfo.problem }}</p>
                    <p v-if="detailInfo.staffId !== null">
                        <strong>维修员工id ：</strong>{{ detailInfo.staffId }}
                    </p>
                    <p><strong>创建时间：</strong>{{ detailInfo.createTime }}</p>
                    <p><strong>更新时间：</strong>{{ detailInfo.updateTime }}</p>
                </div>
            </div>

            <!-- 更新状态 -->
            <div v-if="currentView === 'update'">
                <div class="form-item">
                    <label>报修单 ID</label>
                    <input v-model="updateId" type="number" placeholder="请输入报修单ID" />
                </div>
                <div class="form-item">
                    <label>目标状态</label>
                    <select v-model="updateStatus">
                        <option :value="1">待处理</option>
                        <option :value="2">处理中</option>
                        <option :value="3">已完成</option>
                    </select>
                </div>
                <div class="actions">
                    <button class="btn-primary" @click="handleUpdateStatus">保存</button>
                    <button class="btn-ghost" @click="((updateId = ''), (updateStatus = 1))">
                        清空
                    </button>
                </div>
            </div>

            <!-- 删除报修单 -->
            <div v-if="currentView === 'delete'">
                <div class="form-item">
                    <label>报修单 ID</label>
                    <input v-model="deleteId" type="number" placeholder="请输入报修单ID" />
                </div>
                <div class="actions">
                    <button class="btn-danger" @click="handleDelete">确认删除</button>
                    <button class="btn-ghost" @click="deleteId = ''">清空</button>
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
                <span class="user-info">管理员 ID: {{ userInfo.id }}</span>
                <button class="btn-logout" @click="handleLogout">退出登录</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { del, get, patch, put } from '@/utils/request.js'

const router = useRouter()
// 反序列化JSON字符串
const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

// 默认查看list
const currentView = ref('list')

// 提示消息
const msg = ref('')
const msgType = ref('success')

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
const repairs = ref([])
const detailId = ref('')
const detailInfo = ref(null)
const updateId = ref('')
const updateStatus = ref(1)
const deleteId = ref('')
const newPassword = ref('')
const selectStatus = ref(0)

// 加载所有报修单
const loadAllRepairs = () => {
    get('/repair-orders')
        .then((res) => {
            repairs.value = res.data || []
        })
        .catch(() => showMsg('查询失败！', 'error'))
}

// 根据状态筛选报修单
const selectRepairByStatus = () => {
    if (selectStatus.value === 0) {
        loadAllRepairs()
        return
    } else {
        get(`/repair-orders/status/${selectStatus.value}`)
            .then((res) => {
                repairs.value = res.data || []
            })
            .catch(() => showMsg('查询失败！', 'error'))
    }
}

// 查看详情
const loadDetail = () => {
    if (detailId.value === '') {
        showMsg('id不能为空！', 'error')
        return
    }
    get(`/repair-orders/${detailId.value}`)
        .then((res) => {
            detailInfo.value = res.data
            showMsg('查询成功！')
        })
        .catch(() => showMsg('查询失败！', 'error'))
}

// 点击表格行跳转查看详情
const viewDetail = (id) => {
    detailId.value = id
    currentView.value = 'detail'
    loadDetail()
}

// 更新状态
const handleUpdateStatus = () => {
    if (updateId.value === '') {
        showMsg('id不能为空！', 'error')
        return
    }
    const data = { status: updateStatus.value, staffId: userInfo.id }
    patch(`/repair-orders/${updateId.value}`, data)
        .then(() => {
            showMsg('更新成功！')
            updateId.value = ''
            updateStatus.value = 1
            loadAllRepairs()
        })
        .catch(() => showMsg('更新失败！', 'error'))
}

// 删除
const handleDelete = () => {
    if (deleteId.value === '') {
        showMsg('id不能为空！', 'error')
        return
    }
    del(`/repair-orders/${deleteId.value}`)
        .then(() => {
            showMsg('删除成功！')
            deleteId.value = ''
            loadAllRepairs()
        })
        .catch(() => showMsg('删除失败！', 'error'))
}

// 修改密码
const handleChangePassword = () => {
    if (newPassword.value === '') {
        showMsg('新密码不能为空！', 'error')
        return
    }
    const data = { password: newPassword.value }
    put(`/users/${userInfo.id}/password`, data)
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

// 进入页面加载数据
loadAllRepairs()
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

/* 导航栏 */
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

/* 提示消息 */
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

/* 表单 */
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
select {
    width: 100%;
    padding: 10px 12px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
    box-sizing: border-box;
}

input:focus,
select:focus {
    outline: none;
    border-color: #409eff;
}

/* 按钮 */
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

/* 表格 */
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

.table tr {
    cursor: pointer;
}

.table tr:hover td {
    background: #f5f7fa;
}

/* 详情卡片 */
.detail-card {
    margin-top: 16px;
    padding: 16px;
    background: #fafafa;
    border-radius: 6px;
    border: 1px solid #eee;
}

.detail-card p {
    margin: 6px 0;
    font-size: 14px;
    color: #333;
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
    justify-content: space-between;
    align-items: center;
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

.list-toolbar {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 20px;
}

.btn-refresh {
    padding: 8px 16px;
    background: #409eff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
}

.filter-select {
    width: 200px;
    padding: 8px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    cursor: pointer;
}
</style>
