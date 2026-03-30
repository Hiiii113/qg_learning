<template>
    <div class="page-container">
        <div class="page-box">
            <h2>学生端</h2>

            <!-- 顶部选择导航栏 -->
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
                    :class="{ active: currentView === 'password' }"
                    @click="currentView = 'password'"
                >
                    修改密码
                </button>
            </div>

            <!-- 消息 -->
            <div v-if="message" class="message" :class="messageType">{{ message }}</div>

            <!-- 绑定宿舍 -->
            <div v-if="currentView === 'bind'" class="bind-view">
                <div class="form-item">
                    <label>宿舍号</label>
                    <input v-model="dormRoom" placeholder="例如：西1-001" @keyup.enter="bindDorm" />
                </div>
                <div class="actions">
                    <button class="btn-primary" @click="bindDorm" :disabled="submitting">
                        保存
                    </button>
                    <button class="btn-ghost" @click="dormRoom = ''">清空</button>
                </div>
            </div>

            <!-- 创建报修单 -->
            <div v-if="currentView === 'create'" class="create-view">
                <div class="form-item">
                    <label>问题描述</label>
                    <!-- 文本输入框 -->
                    <textarea
                        v-model="repairProblem"
                        placeholder="请描述清楚：位置/现象/是否紧急"
                        rows="4"
                    ></textarea>
                </div>
                <div class="form-item">
                    <label>上传图片（可选）</label>
                    <input
                        type="file"
                        ref="fileInputRef"
                        accept="image/*"
                        @change="handleImageChange"
                    />
                    <div v-if="imagePreview" class="image-preview">
                        <img :src="imagePreview" alt="预览" @click="previewImage(imagePreview)" />
                        <button class="btn-remove" @click="clearImage">移除</button>
                    </div>
                </div>
                <div class="actions">
                    <!-- 当正在提交或者没有内容时按钮禁用 -->
                    <button
                        class="btn-primary"
                        @click="submitRepair"
                        :disabled="submitting || !repairProblem.trim()"
                    >
                        {{ submitting ? '提交中...' : '提交报修' }}
                    </button>
                    <button class="btn-ghost" @click="clearForm">清空</button>
                </div>
            </div>

            <!-- 我的报修单 -->
            <div v-if="currentView === 'repairs'" class="repairs-view">
                <div class="toolbar">
                    <!-- 正在加载时不可选中 -->
                    <button class="btn-primary" @click="loadMyRepairs" :disabled="loading">
                        {{ loading ? '加载中...' : '↻ 刷新' }}
                    </button>
                </div>
                <!-- 没有保修记录时显示 -->
                <div v-if="repairs.length === 0" class="empty">暂无报修记录</div>
                <!-- 否则显示 -->
                <table v-else class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>问题</th>
                            <th>图片</th>
                            <th>状态</th>
                            <th>处理人</th>
                            <th>创建时间</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="item in repairs" :key="item.id">
                            <td>{{ item.id }}</td>
                            <td class="problem-cell">{{ item.problem }}</td>
                            <td>
                                <img
                                    v-if="item.imageUrl"
                                    :src="getImageUrl(item.imageUrl)"
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
                            <td>{{ item.status === 3 ? item.staffId : '-' }}</td>
                            <td>{{ item.createTime }}</td>
                            <td>
                                <button class="btn-action" @click.stop="openDetail(item.id)">
                                    详情
                                </button>
                            </td>
                        </tr>
                    </tbody>
                </table>

                <!-- 分页 -->
                <div class="pagination" v-if="totalPages > 0">
                    <button
                        class="btn-page"
                        :disabled="currentPage <= 1"
                        @click="changePage(currentPage - 1)"
                    >
                        上一页
                    </button>
                    <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
                    <button
                        class="btn-page"
                        :disabled="currentPage >= totalPages"
                        @click="changePage(currentPage + 1)"
                    >
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

            <!-- 查看详情 -->
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
                                <span class="label">状态</span
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
                            <div class="info-item" v-if="detailData.status === 3">
                                <span class="label">处理人</span
                                ><span class="value">{{ detailData.staffId }}</span>
                            </div>
                            <div class="info-item" v-if="detailData.status === 3">
                                <span class="label">完成时间</span
                                ><span class="value">{{ detailData.completedTime }}</span>
                            </div>
                        </div>

                        <div class="form-item">
                            <label>问题描述</label>
                            <!-- 文本输入框，已完成时不可选中 -->
                            <textarea
                                v-model="editProblem"
                                rows="3"
                                :disabled="detailData.status >= 3"
                            ></textarea>
                        </div>

                        <div class="form-item">
                            <label>图片</label>
                            <div v-if="detailData.imageUrl" class="img-box">
                                <img
                                    :src="getImageUrl(detailData.imageUrl)"
                                    class="detail-img"
                                    @click="previewImage(detailData.imageUrl)"
                                />
                            </div>
                            <div v-else class="no-img">暂无图片</div>
                        </div>

                        <div v-if="detailData.status < 3" class="form-item">
                            <label>更换图片（可选）</label>
                            <input
                                type="file"
                                ref="detailFileRef"
                                accept="image/*"
                                @change="handleDetailImageChange"
                            />
                            <div v-if="detailImagePreview" class="image-preview">
                                <img :src="detailImagePreview" alt="新图片预览" />
                                <button class="btn-remove" @click="clearDetailImage">移除</button>
                            </div>
                        </div>

                        <div v-if="detailData.status === 3" class="form-item">
                            <label>服务评价</label>
                            <div class="rating-box">
                                <button
                                    v-for="r in [1, 2, 3, 4, 5]"
                                    :key="r"
                                    class="btn-star"
                                    :class="{ active: detailData.rating >= r }"
                                    :disabled="detailData.rating > 0"
                                    @click="submitRating(r)"
                                >
                                    {{ r }}★
                                </button>
                            </div>
                            <div class="rating-text" v-if="detailData.rating > 0">
                                已评价：{{ detailData.rating }}分
                            </div>
                            <div class="rating-text" v-else>点击星星评分</div>
                        </div>

                        <div class="modal-actions" v-if="detailData.status < 3">
                            <button class="btn-primary" @click="saveChanges" :disabled="submitting">
                                保存修改
                            </button>
                            <button class="btn-warning" @click="cancelOrder" :disabled="submitting">
                                取消报修
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 图片预览 -->
            <div v-if="imageVisible" class="modal-mask" @click.self="closeImage">
                <div class="image-modal">
                    <img :src="imageSrc" class="preview-img" />
                    <button class="btn-close" @click="closeImage">×</button>
                </div>
            </div>

            <div class="footer">
                <div class="user-info">
                    <span>学生: {{ userInfo.id }}</span>
                    <span v-if="userInfo.dormRoom"> | 宿舍: {{ userInfo.dormRoom }}</span>
                </div>
                <button class="btn-logout" @click="logout">退出登录</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { get, post, put } from '@/utils/request.js'

const router = useRouter()
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}')) // 获取用户信息

const currentView = ref('repairs') // 当前模式
const message = ref('') // 提示信息
const messageType = ref('success') // 提示信息类型(success or error)
const loading = ref(false) // 是否正在加载
const submitting = ref(false) // 是否正在提交

const dormRoom = ref('') // 输入的宿舍地址
const repairProblem = ref('') // 输入的报修单内容
const imageFile = ref(null) // 当前的 image
const imagePreview = ref('') // 当前的预览的图片
const fileInputRef = ref(null) // 上传图片的 DOM 元素
const repairs = ref([]) // 报修单列表
const newPassword = ref('') // 输入的新密码

const currentPage = ref(1) // 当前的页码(默认1)
const pageSize = ref(10) // 当前页面大小(默认10)
const totalPages = ref(0) // 总共的页数大小
const totalCount = ref(0) // 总共多少数据

const detailVisible = ref(false) // 预览
const detailData = ref(null) // 报修单详情
const editProblem = ref('') // 编辑 problem
const detailImageFile = ref(null) // 详情里面的图片
const detailImagePreview = ref('') // 详情里面的预览图片
const detailFileRef = ref(null) // 详情里面的上传图片的 DOM 元素

const imageVisible = ref(false) //
const imageSrc = ref('') //

// 映射
const statusMap = { 1: '待处理', 2: '处理中', 3: '已完成', 4: '已取消' }
const statusText = (s) => statusMap[s] || '未知'

// 提示信息
const showMessage = (text, type = 'success') => {
    message.value = text
    messageType.value = type
    setTimeout(() => {
        message.value = ''
    }, 3000)
}

// 获取照片地址
const getImageUrl = (url) =>
    url?.startsWith('http') ? url : `http://localhost:8081/${url?.replace(/^\/+/, '')}`

// 绑定宿舍
const bindDorm = () => {
    if (!dormRoom.value.trim()) {
        showMessage('请输入宿舍号', 'error')
        return
    }
    submitting.value = true // 正在提交
    post(`/users/dormitories`, { dormRoom: dormRoom.value })
        .then(() => {
            showMessage('绑定成功')
            dormRoom.value = ''
            refreshUserInfo()
        })
        .catch(() => showMessage('绑定失败', 'error'))
        .finally(() => {
            submitting.value = false // 结束提交
        })
}

// 刷新用户信息
const refreshUserInfo = () => {
    get('/users/me')
        .then((res) => {
            localStorage.setItem('userInfo', JSON.stringify(res.data))
            userInfo.value = res.data
        })
        .catch(() => {})
}

// 图片更新
const handleImageChange = (e) => {
    const file = e.target.files?.[0]
    if (!file) {
        clearImage()
        return
    }
    if (!file.type.startsWith('image/')) {
        showMessage('只能上传图片', 'error')
        clearImage()
        return
    }
    if (file.size > 2 * 1024 * 1024) {
        showMessage('图片不能超过2MB', 'error')
        clearImage()
        return
    }
    imageFile.value = file
    imagePreview.value = URL.createObjectURL(file)
    e.target.value = ''
}

// 清除 image
const clearImage = () => {
    imageFile.value = null
    imagePreview.value = ''
    if (fileInputRef.value) fileInputRef.value.value = ''
}

// 清除准备提交的报修单
const clearForm = () => {
    repairProblem.value = ''
    clearImage()
}

// 提交报修单
const submitRepair = async () => {
    if (!repairProblem.value.trim()) {
        showMessage('请输入问题描述', 'error')
        return
    }
    submitting.value = true // 开始提交
    try {
        const res = await post('/repair-orders', {
            problem: repairProblem.value,
        })
        const id = res.data
        if (imageFile.value) {
            const formData = new FormData()
            formData.append('file', imageFile.value)
            formData.append('id', id)
            await post('/repair-orders/upload', formData)
        }
        showMessage('提交成功')
        clearForm() // 清除
        loadMyRepairs() // 重新加载
    } catch (err) {
        showMessage('提交失败！' + err.msg, 'error')
    } finally {
        submitting.value = false // 结束提交
    }
}

// 加载报修单
const loadMyRepairs = () => {
    currentView.value = 'repairs'
    loading.value = true
    get(`/repair-orders/user/repair-orders?page=${currentPage.value}&size=${pageSize.value}`)
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

// 更换 page
const changePage = (page) => {
    if (page < 1 || page > totalPages.value) return
    currentPage.value = page
    loadMyRepairs()
}

// 查看详情
const openDetail = (id) => {
    get(`/repair-orders/${id}`)
        .then((res) => {
            detailData.value = res.data
            editProblem.value = res.data.problem || ''
            clearDetailImage()
            detailVisible.value = true
        })
        .catch(() => showMessage('加载失败', 'error'))
}

// 关闭详情页面
const closeDetail = () => {
    detailVisible.value = false
    detailData.value = null
    editProblem.value = ''
    clearDetailImage()
}

// 详情页面更换图片
const handleDetailImageChange = (e) => {
    const file = e.target.files?.[0]
    if (!file) {
        clearDetailImage()
        return
    }
    if (!file.type.startsWith('image/')) {
        showMessage('只能上传图片', 'error')
        clearDetailImage()
        return
    }
    if (file.size > 2 * 1024 * 1024) {
        showMessage('图片不能超过2MB', 'error')
        clearDetailImage()
        return
    }
    detailImageFile.value = file
    detailImagePreview.value = URL.createObjectURL(file)
    e.target.value = ''
}

// 清空详情页面的图片
const clearDetailImage = () => {
    detailImageFile.value = null
    detailImagePreview.value = ''
    if (detailFileRef.value) detailFileRef.value.value = ''
}

// 保存详情页面的修改
const saveChanges = async () => {
    if (!detailData.value || !editProblem.value.trim()) {
        showMessage('问题描述不能为空', 'error')
        return
    }
    submitting.value = true // 开始提交
    try {
        await put(`/repair-orders/${detailData.value.id}`, { problem: editProblem.value })
        if (detailImageFile.value) {
            const formData = new FormData()
            formData.append('file', detailImageFile.value)
            formData.append('id', detailData.value.id)
            await post('/repair-orders/upload', formData)
        }
        showMessage('保存成功')
        closeDetail()
        loadMyRepairs()
    } catch (err) {
        showMessage('保存失败！' + err.msg, 'error')
    } finally {
        submitting.value = false
    }
}

// 取消报修单
const cancelOrder = async () => {
    if (!detailData.value || !confirm('确定要取消此报修单吗？')) return
    submitting.value = true
    try {
        await put(`/repair-orders/${detailData.value.id}/cancel`, {})
        showMessage('取消成功')
        closeDetail()
        loadMyRepairs()
    } catch (err) {
        showMessage('取消失败！' + err.msg, 'error')
    } finally {
        submitting.value = false
    }
}

// 提交评分
const submitRating = async (rating) => {
    if (!detailData.value?.id || detailData.value.rating > 0) return
    submitting.value = true
    try {
        await post(`/repair-orders/${detailData.value.id}/rating`, {
            rating: rating,
        })
        detailData.value.rating = rating
        showMessage('评价成功')
        loadMyRepairs()
    } catch (err) {
        showMessage('评价失败！' + err.msg, 'error')
    } finally {
        submitting.value = false // 结束提交
    }
}

// 更新密码
const updatePassword = () => {
    if (!newPassword.value) {
        showMessage('请输入新密码', 'error')
        return
    }
    submitting.value = true // 开始提交
    put(`/users/password`, { password: newPassword.value })
        .then(() => {
            showMessage('修改成功')
            newPassword.value = ''
        })
        .catch(() => showMessage('修改失败', 'error'))
        .finally(() => {
            submitting.value = false // 结束提交
        })
}

// 登出
const logout = () => {
    localStorage.removeItem('userInfo') // 清空
    get('/users/logout').finally(() => router.push('/login')) // 注销 Token
}

// 预览
const previewImage = (url) => {
    imageSrc.value = getImageUrl(url)
    imageVisible.value = true // 预览界面可见
}

// 关闭 image
const closeImage = () => {
    imageVisible.value = false // 预览界面不可见
}

// 开始时加载
onMounted(() => {
    loadMyRepairs()
    if (!userInfo.value.dormRoom) {
        showMessage('请先绑定宿舍', 'error')
        currentView.value = 'bind' // 跳转到绑定页面
    }
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
    flex-wrap: wrap;
}
.nav-bar button {
    padding: 10px 18px;
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
    margin-bottom: 16px;
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
}
.form-item input:focus,
.form-item textarea:focus {
    outline: none;
    border-color: #409eff;
}
.form-item textarea {
    resize: vertical;
    min-height: 90px;
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
}
.btn-warning {
    padding: 10px 24px;
    background: #fff;
    color: #e6a23c;
    border: 1px solid #e6a23c;
    border-radius: 6px;
    cursor: pointer;
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
.problem-cell {
    max-width: 160px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
.table-img {
    width: 48px;
    height: 38px;
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

.empty {
    padding: 40px;
    text-align: center;
    color: #999;
    border: 1px dashed #e0e0e0;
    border-radius: 8px;
}

.image-preview {
    margin-top: 10px;
    display: flex;
    align-items: center;
    gap: 10px;
}
.image-preview img {
    max-width: 150px;
    max-height: 100px;
    border-radius: 4px;
    border: 1px solid #eee;
    object-fit: cover;
}
.btn-remove {
    padding: 4px 10px;
    background: #f56c6c;
    color: #fff;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 12px;
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
    min-width: 50px;
}
.info-item .value {
    color: #333;
}
.img-box {
    display: flex;
}
.detail-img {
    max-width: 200px;
    max-height: 150px;
    border-radius: 6px;
    cursor: pointer;
    border: 1px solid #eee;
}
.no-img {
    padding: 20px;
    text-align: center;
    color: #999;
    border: 1px dashed #ddd;
    border-radius: 6px;
    font-size: 13px;
}

.rating-box {
    display: flex;
    gap: 8px;
    margin-bottom: 8px;
}
.btn-star {
    padding: 6px 12px;
    border: 1px solid #ddd;
    background: #fff;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
    color: #ddd;
    transition: all 0.2s;
}
.btn-star.active,
.btn-star:hover {
    color: #ff9800;
    border-color: #ff9800;
}
.btn-star:disabled {
    cursor: not-allowed;
}
.rating-text {
    font-size: 13px;
    color: #888;
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
    display: flex;
    gap: 8px;
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
