<template>
    <div class="page-container">
        <div class="page-box">
            <h2>管理员端</h2>

            <!-- 顶部导航栏，点击跳转 -->
            <div class="nav-bar">
                <button :class="{ active: currentView === 'list' }" @click="switchView('list')">
                    所有报修单
                </button>
                <button
                    :class="{ active: currentView === 'password' }"
                    @click="switchView('password')"
                >
                    修改密码
                </button>
            </div>

            <!-- 消息 -->
            <div v-if="message" class="message" :class="messageType">{{ message }}</div>

            <!-- 刷新按钮和筛选下拉框 -->
            <div v-if="currentView === 'list'" class="list-view">
                <div class="toolbar">
                    <button class="btn-primary" @click="loadRepairs" :disabled="loading">
                        <span v-if="loading">加载中...</span>
                        <span v-else>↻ 刷新</span>
                    </button>
                    <select v-model="filterStatus" @change="currentPage = 1; loadRepairs()" class="filter-select">
                        <option :value="0">全部状态</option>
                        <option :value="1">待处理</option>
                        <option :value="2">处理中</option>
                        <option :value="3">已完成</option>
                        <option :value="4">已取消</option>
                    </select>
                </div>

                <!-- 报修单表格 -->
                <div v-if="repairs.length === 0" class="empty">暂无报修单</div>
                <table v-else class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>用户ID</th>
                            <th>问题描述</th>
                            <th>图片</th>
                            <th>状态</th>
                            <th>创建时间</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="item in repairs" :key="item.id" @click="openDetail(item.id)">
                            <td>{{ item.id }}</td>
                            <td>{{ item.userId }}</td>
                            <td class="problem-cell">{{ item.problem }}</td>
                            <td>
                                <img
                                    v-if="item.imageUrl"
                                    :src="getImageUrl(item.imageUrl)"
                                    alt="报修图片"
                                    class="table-img"
                                    @click.stop="previewImage(item.imageUrl)"
                                />
                                <span v-else class="no-image">-</span>
                            </td>
                            <td>
                                <span class="status-badge" :class="'status-' + item.status">{{
                                    statusText(item.status)
                                }}</span>
                            </td>
                            <td>{{ item.createTime }}</td>
                            <td>
                                <button class="btn-action" @click.stop="openDetail(item.id)">
                                    处理
                                </button>
                            </td>
                        </tr>
                    </tbody>
                </table>

                <!-- 分页 -->
                <div class="pagination" v-if="totalPages > 0">
                    <button class="btn-page" :disabled="currentPage <= 1" @click="changePage(currentPage - 1)">
                        上一页
                    </button>
                    <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
                    <button class="btn-page" :disabled="currentPage >= totalPages" @click="changePage(currentPage + 1)">
                        下一页
                    </button>
                </div>
            </div>

            <!-- 修改密码 -->
            <div v-if="currentView === 'password'" class="password-view">
                <div class="form-item">
                    <label>新密码</label>
                    <input
                        v-model="newPassword"
                        type="password"
                        placeholder="请输入新密码"
                        @keyup.enter="updatePassword"
                    />
                </div>
                <div class="actions">
                    <button class="btn-primary" @click="updatePassword" :disabled="submitting">
                        保存
                    </button>
                    <button class="btn-ghost" @click="newPassword = ''">清空</button>
                </div>
            </div>

            <!-- 详情页面 -->
            <div v-if="detailVisible" class="modal-mask" @click.self="closeDetail">
                <div class="modal">
                    <div class="modal-header">
                        <strong>报修单详情</strong>
                        <button class="btn-close" @click="closeDetail">×</button>
                    </div>

                    <div v-if="detailData" class="modal-content">
                        <div class="info-grid">
                            <div class="info-item">
                                <span class="label">ID</span
                                ><span class="value">{{ detailData.id }}</span>
                            </div>
                            <div class="info-item">
                                <span class="label">用户ID</span
                                ><span class="value">{{ detailData.userId }}</span>
                            </div>
                            <div class="info-item">
                                <span class="label">当前状态</span
                                ><span
                                    class="value status-badge"
                                    :class="'status-' + detailData.status"
                                    >{{ statusText(detailData.status) }}</span
                                >
                            </div>
                            <div class="info-item">
                                <span class="label">创建时间</span
                                ><span class="value">{{ detailData.createTime }}</span>
                            </div>
                        </div>

                        <div class="form-item">
                            <label>修改状态</label>
                            <select v-model="editStatus" :disabled="detailData.status >= 3">
                                <option :value="1">待处理</option>
                                <option :value="2">处理中</option>
                                <option :value="3">已完成</option>
                            </select>
                        </div>

                        <div class="form-item">
                            <label>问题描述</label>
                            <textarea
                                v-model="editProblem"
                                rows="3"
                                placeholder="请输入问题描述"
                            ></textarea>
                        </div>

                        <div class="form-item" v-if="detailData.imageUrl">
                            <label>报修图片</label>
                            <img
                                :src="getImageUrl(detailData.imageUrl)"
                                class="detail-img"
                                @click="previewImage(detailData.imageUrl)"
                            />
                        </div>

                        <div class="modal-actions">
                            <button
                                class="btn-primary"
                                @click="saveAll"
                                :disabled="submitting || !editProblem.trim()"
                            >
                                {{ submitting ? '保存中...' : '保存修改' }}
                            </button>
                            <button
                                v-if="detailData.status < 3"
                                class="btn-warning"
                                @click="cancelOrder"
                                :disabled="submitting"
                            >
                                取消报修
                            </button>
                            <button class="btn-danger" @click="deleteOrder" :disabled="submitting">
                                删除
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <div v-if="imageVisible" class="modal-mask" @click.self="closeImage">
                <div class="image-modal">
                    <img :src="imageSrc" class="preview-img" />
                    <button class="btn-close" @click="closeImage">×</button>
                </div>
            </div>

            <div class="footer">
                <span class="user-info">管理员: {{ userInfo.id }}</span>
                <button class="btn-logout" @click="logout">退出登录</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { del, get, patch, put } from '@/utils/request.js'

const router = useRouter()
const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

const currentView = ref('list')
const message = ref('')
const messageType = ref('success')
const loading = ref(false)
const submitting = ref(false)

const repairs = ref([])
const filterStatus = ref(0)
const newPassword = ref('')

const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(0)
const totalCount = ref(0)

const detailVisible = ref(false)
const detailData = ref(null)
const editStatus = ref(1)
const editProblem = ref('')

const imageVisible = ref(false)
const imageSrc = ref('')

const statusMap = { 1: '待处理', 2: '处理中', 3: '已完成', 4: '已取消' }
const statusText = (status) => statusMap[status] || '未知'

const showMessage = (text, type = 'success') => {
    message.value = text
    messageType.value = type
    setTimeout(() => {
        message.value = ''
    }, 3000)
}

const getImageUrl = (url) =>
    url?.startsWith('http') ? url : `http://localhost:8081/${url?.replace(/^\/+/, '')}`

const switchView = (view) => {
    currentView.value = view
}

const loadRepairs = () => {
    loading.value = true
    let api = `/repair-orders?page=${currentPage.value}&size=${pageSize.value}`
    if (filterStatus.value !== 0) {
        api += `&status=${filterStatus.value}`
    }
    get(api)
        .then((res) => {
            const pageData = res.data
            repairs.value = pageData.records || []
            totalCount.value = pageData.total || 0
            totalPages.value = pageData.pages || 0
        })
        .catch(() => showMessage('加载失败', 'error'))
        .finally(() => {
            loading.value = false
        })
}

const changePage = (page) => {
    if (page < 1 || page > totalPages.value) return
    currentPage.value = page
    loadRepairs()
}

const openDetail = (id) => {
    if (!id) return
    get(`/repair-orders/${id}`)
        .then((res) => {
            detailData.value = res.data
            editStatus.value = res.data.status || 1
            editProblem.value = res.data.problem || ''
            detailVisible.value = true
        })
        .catch(() => showMessage('加载详情失败', 'error'))
}

const closeDetail = () => {
    detailVisible.value = false
    detailData.value = null
    editStatus.value = 1
    editProblem.value = ''
}

const previewImage = (url) => {
    imageSrc.value = getImageUrl(url)
    imageVisible.value = true
}

const closeImage = () => {
    imageVisible.value = false
}

const saveAll = async () => {
    if (!detailData.value) return
    const problem = editProblem.value.trim()
    if (!problem) {
        showMessage('问题描述不能为空', 'error')
        return
    }

    submitting.value = true
    try {
        await patch(`/repair-orders/${detailData.value.id}`, {
            status: editStatus.value,
            staffNumber: userInfo.id,
            problem: problem,
        })
        showMessage('保存成功')
        closeDetail()
        loadRepairs()
    } catch (err) {
        showMessage('保存失败！' + err.msg, 'error')
    } finally {
        submitting.value = false
    }
}

const cancelOrder = async () => {
    if (!detailData.value || !confirm('确定要取消此报修单吗？')) return
    submitting.value = true
    try {
        await put(`/repair-orders/${detailData.value.id}/cancel`, {})
        showMessage('取消成功')
        closeDetail()
        loadRepairs()
    } catch (err) {
        showMessage('取消失败！' + err.msg, 'error')
    } finally {
        submitting.value = false
    }
}

const deleteOrder = async () => {
    if (!detailData.value || !confirm('确定要删除此报修单吗？此操作不可恢复！')) return
    submitting.value = true
    try {
        await del(`/repair-orders/${detailData.value.id}`)
        showMessage('删除成功')
        closeDetail()
        loadRepairs()
    } catch (err) {
        showMessage('删除失败！' + err.msg, 'error')
    } finally {
        submitting.value = false
    }
}

const updatePassword = () => {
    if (!newPassword.value) {
        showMessage('请输入新密码', 'error')
        return
    }
    submitting.value = true
    put(`/users/password`, { password: newPassword.value })
        .then(() => {
            showMessage('修改成功')
            newPassword.value = ''
        })
        .catch(() => showMessage('修改失败', 'error'))
        .finally(() => {
            submitting.value = false
        })
}

const logout = () => {
    localStorage.removeItem('userInfo')
    get('/users/logout').finally(() => router.push('/login'))
}

onMounted(() => {
    loadRepairs()
})
</script>

<style scoped>
.page-container {
    min-height: 100vh;
    display: flex;
    justify-content: center;
    background: #f0f2f5;
    padding: 40px 20px;
}
.page-box {
    width: 900px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    padding: 30px 40px;
}
h2 {
    margin: 0 0 24px;
    text-align: center;
    color: #1a1a1a;
    font-size: 24px;
}

.nav-bar {
    display: flex;
    gap: 8px;
    margin-bottom: 20px;
}
.nav-bar button {
    padding: 10px 20px;
    border: 1px solid #e0e0e0;
    border-radius: 6px;
    background: #fff;
    color: #666;
    cursor: pointer;
    font-size: 14px;
    transition: all 0.2s;
}
.nav-bar button:hover {
    border-color: #409eff;
    color: #409eff;
}
.nav-bar button.active {
    background: #409eff;
    color: #fff;
    border-color: #409eff;
}

.message {
    padding: 12px 16px;
    border-radius: 6px;
    margin-bottom: 16px;
    font-size: 14px;
}
.message.success {
    background: #f0f9eb;
    color: #67c23a;
    border: 1px solid #e1f3d8;
}
.message.error {
    background: #fef0f0;
    color: #f56c6c;
    border: 1px solid #fde2e2;
}

.toolbar {
    display: flex;
    gap: 12px;
    margin-bottom: 20px;
    align-items: center;
}
.filter-select {
    padding: 8px 12px;
    border: 1px solid #dcdfe6;
    border-radius: 6px;
    width: 140px;
    cursor: pointer;
    font-size: 14px;
}

.table {
    width: 100%;
    border-collapse: collapse;
}
.table th,
.table td {
    padding: 12px;
    text-align: left;
    font-size: 13px;
    border-bottom: 1px solid #f0f0f0;
}
.table th {
    background: #fafafa;
    color: #666;
    font-weight: 600;
}
.table tr:hover td {
    background: #f8f9fa;
}
.table tr {
    cursor: pointer;
    transition: background 0.2s;
}
.problem-cell {
    max-width: 180px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
.table-img {
    width: 50px;
    height: 40px;
    object-fit: cover;
    border-radius: 4px;
    cursor: pointer;
    border: 1px solid #eee;
}
.no-image {
    color: #999;
}

.status-badge {
    display: inline-block;
    padding: 2px 8px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 500;
}
.status-1 {
    background: #fff3e0;
    color: #ff9800;
}
.status-2 {
    background: #e3f2fd;
    color: #2196f3;
}
.status-3 {
    background: #e8f5e9;
    color: #4caf50;
}
.status-4 {
    background: #f5f5f5;
    color: #999;
}

.btn-action {
    padding: 6px 14px;
    background: #409eff;
    color: #fff;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 12px;
}
.btn-action:hover {
    background: #66b1ff;
}

.empty {
    padding: 40px;
    text-align: center;
    color: #999;
    border: 1px dashed #e0e0e0;
    border-radius: 8px;
}

.form-item {
    margin-bottom: 16px;
}
.form-item label {
    display: block;
    margin-bottom: 6px;
    color: #555;
    font-size: 14px;
    font-weight: 500;
}
.form-item input,
.form-item select,
.form-item textarea {
    width: 100%;
    padding: 10px 12px;
    border: 1px solid #dcdfe6;
    border-radius: 6px;
    font-size: 14px;
    box-sizing: border-box;
    transition: border-color 0.2s;
}
.form-item input:focus,
.form-item select:focus,
.form-item textarea:focus {
    outline: none;
    border-color: #409eff;
}
.form-item textarea {
    resize: vertical;
    min-height: 80px;
}

.actions {
    display: flex;
    gap: 10px;
    margin-top: 20px;
}
.btn-primary {
    padding: 10px 24px;
    background: #409eff;
    color: #fff;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-size: 14px;
    transition: background 0.2s;
}
.btn-primary:hover:not(:disabled) {
    background: #66b1ff;
}
.btn-primary:disabled {
    background: #a0cfff;
    cursor: not-allowed;
}
.btn-ghost {
    padding: 10px 24px;
    background: #fff;
    color: #666;
    border: 1px solid #dcdfe6;
    border-radius: 6px;
    cursor: pointer;
    font-size: 14px;
}
.btn-ghost:hover {
    border-color: #409eff;
    color: #409eff;
}
.btn-danger {
    padding: 10px 24px;
    background: #fff;
    color: #f56c6c;
    border: 1px solid #f56c6c;
    border-radius: 6px;
    cursor: pointer;
    font-size: 14px;
}
.btn-danger:hover:not(:disabled) {
    background: #fef0f0;
}
.btn-warning {
    padding: 10px 24px;
    background: #fff;
    color: #e6a23c;
    border: 1px solid #e6a23c;
    border-radius: 6px;
    cursor: pointer;
    font-size: 14px;
}
.btn-warning:hover:not(:disabled) {
    background: #fdf6ec;
}

.modal-mask {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999;
}
.modal {
    width: 560px;
    max-width: 95vw;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
    overflow: hidden;
}
.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 20px;
    border-bottom: 1px solid #f0f0f0;
    background: #fafafa;
}
.modal-header strong {
    font-size: 16px;
    color: #333;
}
.btn-close {
    width: 32px;
    height: 32px;
    border: none;
    background: transparent;
    font-size: 24px;
    color: #999;
    cursor: pointer;
    border-radius: 4px;
}
.btn-close:hover {
    background: #f0f0f0;
    color: #333;
}
.modal-content {
    padding: 20px;
}
.info-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px;
    margin-bottom: 20px;
}
.info-item {
    display: flex;
    gap: 8px;
    font-size: 13px;
}
.info-item .label {
    color: #888;
    min-width: 60px;
}
.info-item .value {
    color: #333;
}
.detail-img {
    max-width: 100%;
    max-height: 200px;
    border-radius: 6px;
    cursor: pointer;
    border: 1px solid #eee;
}
.modal-actions {
    display: flex;
    gap: 10px;
    margin-top: 20px;
    flex-wrap: wrap;
}

.image-modal {
    max-width: 90vw;
    max-height: 90vh;
    position: relative;
}
.preview-img {
    max-width: 100%;
    max-height: 85vh;
    border-radius: 8px;
}
.image-modal .btn-close {
    position: absolute;
    top: -40px;
    right: 0;
    color: #fff;
}

.footer {
    margin-top: 30px;
    padding-top: 20px;
    border-top: 1px solid #f0f0f0;
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.user-info {
    color: #888;
    font-size: 13px;
}
.btn-logout {
    padding: 8px 16px;
    background: #fff;
    border: 1px solid #f56c6c;
    color: #f56c6c;
    border-radius: 6px;
    cursor: pointer;
    font-size: 13px;
}
.btn-logout:hover {
    background: #fef0f0;
}

.pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 12px;
    margin-top: 20px;
    padding: 16px 0;
}
.btn-page {
    padding: 8px 16px;
    background: #fff;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    cursor: pointer;
    font-size: 13px;
    color: #606266;
}
.btn-page:hover:not(:disabled) {
    border-color: #409eff;
    color: #409eff;
}
.btn-page:disabled {
    color: #c0c4cc;
    cursor: not-allowed;
}
.page-info {
    font-size: 13px;
    color: #606266;
}
</style>
