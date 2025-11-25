<template>
  <view class="page">
    <!-- 顶部统计栏 -->
    <view class="stats-bar">
      <view class="stat-item">
        <view class="stat-icon-wrapper urgent">
          <text class="stat-icon">🆘</text>
        </view>
        <view class="stat-info">
          <text class="stat-value">{{ totalRescue }}</text>
          <text class="stat-label">救助中</text>
        </view>
      </view>
      
      <view class="stat-divider"></view>
      
      <view class="stat-item">
        <view class="stat-icon-wrapper success">
          <text class="stat-icon">✅</text>
        </view>
        <view class="stat-info">
          <text class="stat-value">{{ completedRescue }}</text>
          <text class="stat-label">已完成</text>
        </view>
      </view>
      
      <view class="stat-divider"></view>
      
      <view class="stat-item">
        <view class="stat-icon-wrapper volunteer">
          <text class="stat-icon">👥</text>
        </view>
        <view class="stat-info">
          <text class="stat-value">{{ totalVolunteer }}</text>
          <text class="stat-label">志愿者</text>
        </view>
      </view>
    </view>

    <!-- 状态筛选 -->
    <scroll-view scroll-x class="status-scroll" show-scrollbar="false">
      <view class="status-list">
        <view 
          v-for="(item, index) in statusTabs" 
          :key="index"
          class="status-item"
          :class="{ active: currentStatus === item.value }"
          @click="handleStatusChange(item.value)"
        >
          <text class="status-emoji">{{ item.emoji }}</text>
          <text class="status-text">{{ item.label }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 救助任务列表 -->
    <scroll-view scroll-y class="rescue-scroll" @scrolltolower="loadMore">
      <view class="rescue-list">
        <view 
          v-for="item in rescueList" 
          :key="item.id" 
          class="rescue-card"
          @click="handleDetail(item)"
        >
          <!-- 紧急标签 -->
          <view v-if="item.urgencyLevel === 'urgent'" class="urgent-badge">
            <text class="badge-pulse"></text>
            <text class="urgent-icon">🚨</text>
            <text class="urgent-text">紧急求助</text>
          </view>

          <image :src="item.cover" mode="aspectFill" class="rescue-image"></image>
          
          <view class="rescue-content">
            <text class="rescue-title">{{ item.title }}</text>
            
            <view class="rescue-meta">
              <view class="meta-row">
                <view class="meta-item">
                  <text class="meta-icon">📍</text>
                  <text class="meta-text">{{ item.address }}</text>
                </view>
              </view>
              
              <view class="meta-row">
                <view class="meta-item">
                  <text class="meta-icon">⏰</text>
                  <text class="meta-text">{{ formatTime(item.createTime) }}</text>
                </view>
                <view class="meta-item">
                  <text class="meta-icon">👤</text>
                  <text class="meta-text">{{ item.reporterName }}</text>
                </view>
              </view>
            </view>

            <view class="rescue-desc">
              {{ item.description }}
            </view>

            <view class="rescue-footer">
              <view class="status-badge" :class="item.status">
                <text class="status-dot"></text>
                <text class="status-text">{{ getStatusText(item.status) }}</text>
              </view>
              
              <button 
                v-if="item.status === 'pending'" 
                class="btn-participate"
                @click.stop="handleParticipate(item)"
              >
                <text class="btn-icon">🙋</text>
                <text class="btn-text">我要参与</text>
              </button>
              
              <button 
                v-else-if="item.status === 'processing'" 
                class="btn-detail"
                @click.stop="handleProgress(item)"
              >
                <text class="btn-icon">📊</text>
                <text class="btn-text">查看进度</text>
              </button>
              
              <view v-else class="completion-badge">
                <text class="completion-icon">🎉</text>
                <text class="completion-text">已完成</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-if="rescueList.length === 0 && !loading" class="empty-state">
        <text class="empty-icon">🔍</text>
        <text class="empty-title">暂无救助任务</text>
        <text class="empty-description">期待您的爱心参与</text>
      </view>
      
      <!-- 加载状态 -->
      <view v-if="loading" class="loading-state">
        <view class="loading-spinner"></view>
        <text class="loading-text">加载中...</text>
      </view>
      
      <!-- 加载完成提示 -->
      <view v-if="noMore && rescueList.length > 0" class="no-more">
        <text class="no-more-text">没有更多了~</text>
      </view>
      
      <view class="list-footer"></view>
    </scroll-view>

    <!-- 发布救助按钮 -->
    <view class="fab-btn" @click="handlePublish">
      <text class="fab-icon">📝</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { rescueApi } from '@/utils/api'

const totalRescue = ref(0)
const completedRescue = ref(0)
const totalVolunteer = ref(0)

const currentStatus = ref('all')
const loading = ref(false)
const noMore = ref(false)
const page = ref(1)
const pageSize = 10

const statusTabs = ref([
  { label: '全部', value: 'all', emoji: '📋' },
  { label: '待救助', value: 'pending', emoji: '🆘' },
  { label: '救助中', value: 'processing', emoji: '🏃' },
  { label: '已完成', value: 'completed', emoji: '✅' }
])

const rescueList = ref([])

onMounted(() => {
  loadData()
  loadStats()
})

watch(currentStatus, () => {
  resetList()
  loadData()
})

const resetList = () => {
  page.value = 1
  rescueList.value = []
  noMore.value = false
}

const loadData = async () => {
  if (loading.value || noMore.value) return
  
  loading.value = true
  
  try {
    const params = {
      page: page.value,
      size: pageSize
    }
    
    if (currentStatus.value !== 'all') {
      params.status = currentStatus.value
    }
    
    const res = await rescueApi.getTaskList(params)
    
    if (res.data && res.data.records) {
      const newList = res.data.records.map(item => ({
        ...item,
        cover: item.cover || 'https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?w=600&q=80'
      }))
      
      if (page.value === 1) {
        rescueList.value = newList
      } else {
        rescueList.value = [...rescueList.value, ...newList]
      }
      
      if (newList.length < pageSize) {
        noMore.value = true
      }
    }
  } catch (error) {
    console.error('加载救助任务列表失败', error)
    uni.showToast({
      title: '加载失败，请重试',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  if (!loading.value && !noMore.value) {
    page.value++
    loadData()
  }
}

const loadStats = async () => {
  try {
    // 加载统计数据（这里使用列表数据统计）
    const res = await rescueApi.getTaskList({ page: 1, size: 100 })
    if (res.data && res.data.records) {
      const records = res.data.records
      totalRescue.value = records.filter(r => r.status === 'pending' || r.status === 'processing').length
      completedRescue.value = records.filter(r => r.status === 'completed').length
      totalVolunteer.value = 128 // 志愿者数量需要从后端获取
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
    // 使用默认值
    totalRescue.value = 15
    completedRescue.value = 89
    totalVolunteer.value = 128
  }
}

const handleStatusChange = (value) => {
  currentStatus.value = value
}

const handleDetail = (item) => {
  uni.showToast({
    title: '详情页面开发中',
    icon: 'none'
  })
}

const handleParticipate = async (item) => {
  const userInfo = uni.getStorageSync('userInfo')
  if (!userInfo) {
    uni.navigateTo({
      url: '/pages/login/index'
    })
    return
  }
  
  uni.showModal({
    title: '确认参与',
    content: '确定要参与这个救助任务吗？',
    confirmColor: '#FF8C42',
    success: async (res) => {
      if (res.confirm) {
        try {
          await rescueApi.acceptTask(item.id)
          uni.showToast({
            title: '参与成功',
            icon: 'success'
          })
          loadData()
        } catch (error) {
          console.error('参与救助失败', error)
          uni.showToast({
            title: '参与失败，请重试',
            icon: 'none'
          })
        }
      }
    }
  })
}

const handleProgress = (item) => {
  uni.showToast({
    title: '进度页面开发中',
    icon: 'none'
  })
}

const handlePublish = () => {
  const userInfo = uni.getStorageSync('userInfo')
  if (!userInfo) {
    uni.navigateTo({
      url: '/pages/login/index'
    })
    return
  }
  
  uni.showToast({
    title: '发布页面开发中',
    icon: 'none'
  })
}

const getStatusText = (status) => {
  const statusMap = {
    'pending': '待救助',
    'processing': '救助中',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return statusMap[status] || '未知'
}

const formatTime = (time) => {
  if (!time) return '刚刚'
  
  const now = new Date()
  const createTime = new Date(time)
  const diff = now - createTime
  
  const minutes = Math.floor(diff / 1000 / 60)
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours}小时前`
  
  const days = Math.floor(hours / 24)
  if (days < 7) return `${days}天前`
  
  return createTime.toLocaleDateString()
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #FFFBF5;
  display: flex;
  flex-direction: column;
  padding-bottom: 120rpx;
}

/* ===================================
   统计栏
   =================================== */

.stats-bar {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 32rpx 24rpx;
  margin: 24rpx;
  background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
  border-radius: 32rpx;
  box-shadow: 0 8rpx 32rpx rgba(255, 140, 66, 0.25);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.stat-icon-wrapper {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10rpx);
  
  &.urgent {
    background: rgba(255, 255, 255, 0.25);
  }
  
  &.success {
    background: rgba(255, 255, 255, 0.25);
  }
  
  &.volunteer {
    background: rgba(255, 255, 255, 0.25);
  }
}

.stat-icon {
  font-size: 36rpx;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.stat-value {
  font-size: 36rpx;
  font-weight: 700;
  color: #FFFFFF;
  line-height: 1;
}

.stat-label {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.85);
}

.stat-divider {
  width: 2rpx;
  height: 60rpx;
  background: rgba(255, 255, 255, 0.3);
}

/* ===================================
   状态筛选
   =================================== */

.status-scroll {
  white-space: nowrap;
  background: rgba(255, 251, 245, 0.95);
  backdrop-filter: blur(20rpx);
  padding: 20rpx 0;
  border-bottom: 1rpx solid #F5F5F4;
}

.status-list {
  display: inline-flex;
  gap: 16rpx;
  padding: 0 24rpx;
}

.status-item {
  display: inline-flex;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx 32rpx;
  background: #FFFFFF;
  border-radius: 40rpx;
  border: 2rpx solid transparent;
  transition: all 0.3s ease;

  &.active {
    background: linear-gradient(135deg, #FFE5D9 0%, #FFDCC5 100%);
    border-color: #FF8C42;
    transform: scale(1.05);
    
    .status-text {
      color: #FF8C42;
      font-weight: 700;
    }
  }
  
  &:active {
    transform: scale(0.95);
  }
}

.status-emoji {
  font-size: 32rpx;
}

.status-text {
  font-size: 26rpx;
  color: #78716C;
  white-space: nowrap;
  transition: all 0.3s ease;
}

/* ===================================
   救助任务列表
   =================================== */

.rescue-scroll {
  flex: 1;
  padding: 0 24rpx;
}

.rescue-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
  padding: 24rpx 0;
}

.rescue-card {
  position: relative;
  background: linear-gradient(to bottom right, #FFFFFF 0%, #FEF7F0 100%);
  border-radius: 32rpx;
  overflow: hidden;
  border: 1rpx solid #F5F5F4;
  box-shadow: 0 4rpx 20rpx rgba(255, 140, 66, 0.08);
  transition: all 0.3s ease;
  
  &:active {
    transform: translateY(-4rpx);
    box-shadow: 0 8rpx 32rpx rgba(255, 140, 66, 0.15);
  }
}

.urgent-badge {
  position: absolute;
  top: 20rpx;
  right: 20rpx;
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 24rpx;
  background: rgba(239, 68, 68, 0.95);
  border-radius: 40rpx;
  z-index: 10;
  backdrop-filter: blur(10rpx);
  box-shadow: 0 4rpx 16rpx rgba(239, 68, 68, 0.3);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.badge-pulse {
  width: 12rpx;
  height: 12rpx;
  background: #FFFFFF;
  border-radius: 50%;
  animation: blink 1.5s infinite;
}

@keyframes blink {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.3;
  }
}

.urgent-icon {
  font-size: 24rpx;
}

.urgent-text {
  font-size: 24rpx;
  font-weight: 600;
  color: #FFFFFF;
}

.rescue-image {
  width: 100%;
  height: 380rpx;
  background: linear-gradient(135deg, #FEF7F0 0%, #FFF5EB 100%);
}

.rescue-content {
  padding: 28rpx;
}

.rescue-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #57534E;
  line-height: 1.4;
  margin-bottom: 20rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.rescue-meta {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  margin-bottom: 16rpx;
}

.meta-row {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.meta-icon {
  font-size: 24rpx;
}

.meta-text {
  font-size: 24rpx;
  color: #78716C;
}

.rescue-desc {
  font-size: 26rpx;
  color: #78716C;
  line-height: 1.7;
  margin-bottom: 20rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.rescue-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 20rpx;
  border-top: 1rpx solid #F5F5F4;
}

.status-badge {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 24rpx;
  border-radius: 40rpx;
  font-size: 24rpx;
  font-weight: 600;

  &.pending {
    background: linear-gradient(135deg, #FFE5D9 0%, #FFDCC5 100%);
    
    .status-dot {
      background: #FF8C42;
    }
    
    .status-text {
      color: #FF8C42;
    }
  }

  &.processing {
    background: linear-gradient(135deg, #D1FAE5 0%, #A7F3D0 100%);
    
    .status-dot {
      background: #059669;
    }
    
    .status-text {
      color: #059669;
    }
  }

  &.completed {
    background: linear-gradient(135deg, #DBEAFE 0%, #BFDBFE 100%);
    
    .status-dot {
      background: #2563EB;
    }
    
    .status-text {
      color: #2563EB;
    }
  }
}

.status-dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
}

.btn-participate,
.btn-detail {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx 32rpx;
  border-radius: 40rpx;
  border: none;
  font-size: 26rpx;
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.95);
  }
}

.btn-participate {
  background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
  color: #FFFFFF;
  box-shadow: 0 4rpx 12rpx rgba(255, 140, 66, 0.25);
}

.btn-detail {
  background: #FEF7F0;
  color: #78716C;
  border: 2rpx solid #F5F5F4;
}

.completion-badge {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 24rpx;
  background: linear-gradient(135deg, #D1FAE5 0%, #A7F3D0 100%);
  border-radius: 40rpx;
}

.completion-icon {
  font-size: 24rpx;
}

.completion-text {
  font-size: 24rpx;
  font-weight: 600;
  color: #059669;
}

.btn-icon {
  font-size: 28rpx;
}

.btn-text {
  font-size: 26rpx;
  font-weight: 600;
}

/* ===================================
   发布按钮
   =================================== */

.fab-btn {
  position: fixed;
  right: 40rpx;
  bottom: 140rpx;
  width: 112rpx;
  height: 112rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
  border-radius: 50%;
  box-shadow: 0 8rpx 32rpx rgba(255, 140, 66, 0.35);
  z-index: 100;
  transition: all 0.3s ease;
  animation: bounce 2s infinite;
  
  &:active {
    transform: scale(0.9);
  }
}

.fab-icon {
  font-size: 52rpx;
}

/* 加载状态复用之前的样式 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx 0;
  gap: 20rpx;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid #F5F5F4;
  border-top-color: #FF8C42;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: 24rpx;
  color: #A8A29E;
}

.no-more {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40rpx 0;
}

.no-more-text {
  font-size: 24rpx;
  color: #D6D3D1;
}

.list-footer {
  height: 40rpx;
}
</style>
