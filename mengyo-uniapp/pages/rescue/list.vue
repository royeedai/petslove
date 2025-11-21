<template>
  <view class="page">
    <!-- 顶部统计栏 - 更温馨的设计 -->
    <view class="stats-section">
      <view class="stats-card">
        <view class="stat-item">
          <view class="stat-icon-wrapper rescue">
            <text class="stat-icon">🆘</text>
          </view>
          <view class="stat-info">
            <text class="stat-value">{{ totalRescue }}</text>
            <text class="stat-label">救助中</text>
          </view>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <view class="stat-icon-wrapper completed">
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
    </view>

    <!-- 状态筛选 - 更圆润的设计 -->
    <scroll-view scroll-x class="status-scroll" :show-scrollbar="false">
      <view class="status-list">
        <view 
          v-for="(item, index) in statusTabs" 
          :key="index"
          class="status-item"
          :class="{ active: currentStatus === item.value }"
          @click="currentStatus = item.value"
        >
          <text class="status-emoji">{{ item.emoji }}</text>
          <text class="status-text">{{ item.label }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 救助任务列表 - Pinterest 风格卡片 -->
    <view class="rescue-list">
      <view 
        v-for="item in rescueList" 
        :key="item.id" 
        class="rescue-card"
        @click="handleDetail(item)"
      >
        <!-- 紧急标签 -->
        <view v-if="item.urgencyLevel === 'urgent'" class="urgent-tag">
          <text class="urgent-icon">🚨</text>
          <text class="urgent-text">紧急求助</text>
        </view>

        <view class="rescue-image-wrapper">
          <image 
            :src="item.cover" 
            mode="aspectFill" 
            class="rescue-image"
            @error="handleImageError($event, item.id)"
          ></image>
        </view>
        
        <view class="rescue-content">
          <text class="rescue-title">{{ item.title }}</text>
          
          <view class="rescue-meta">
            <view class="meta-item">
              <text class="meta-icon">📍</text>
              <text class="meta-text">{{ item.address || '位置待确认' }}</text>
            </view>
            
            <view class="meta-row">
              <view class="meta-item">
                <text class="meta-icon">⏰</text>
                <text class="meta-text">{{ formatTime(item.createTime) }}</text>
              </view>
              <view class="meta-item">
                <text class="meta-icon">👤</text>
                <text class="meta-text">{{ item.reporterName || '匿名' }}</text>
              </view>
            </view>
          </view>

          <view class="rescue-desc" v-if="item.description">
            {{ item.description }}
          </view>

          <view class="rescue-footer">
            <view class="status-badge" :class="item.status">
              <text class="status-dot"></text>
              <text class="status-text">{{ getStatusText(item.status) }}</text>
            </view>
            
            <view class="action-group">
              <button 
                v-if="item.status === 'pending'" 
                class="btn-participate"
                @click.stop="handleParticipate(item)"
              >
                <text class="btn-text">我要参与</text>
              </button>
              <button 
                v-else-if="item.status === 'processing'" 
                class="btn-detail"
                @click.stop="handleProgress(item)"
              >
                <text class="btn-text">查看进度</text>
              </button>
            </view>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-if="rescueList.length === 0" class="empty-state">
        <text class="empty-icon">🔍</text>
        <text class="empty-text">暂无救助任务</text>
        <text class="empty-desc">期待您的爱心参与</text>
      </view>
    </view>

    <!-- 发布救助按钮 - 更温馨的设计 -->
    <view class="fab-btn" @click="handlePublish">
      <text class="fab-icon">+</text>
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

const statusTabs = ref([
  { label: '全部', value: 'all', emoji: '📋' },
  { label: '待救助', value: 'pending', emoji: '🆘' },
  { label: '救助中', value: 'processing', emoji: '🏃' },
  { label: '已完成', value: 'completed', emoji: '✅' }
])

const rescueList = ref([])

const defaultRescueImage = 'https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?w=600&q=80'

onMounted(() => {
  loadData()
  loadStats()
})

watch(currentStatus, () => {
  loadData()
})

const loadData = async () => {
  try {
    const params = {
      page: 1,
      size: 20
    }
    
    if (currentStatus.value !== 'all') {
      params.status = currentStatus.value
    }
    
    const res = await rescueApi.getTaskList(params)
    if (res.data && res.data.records) {
      rescueList.value = res.data.records.map(item => ({
        ...item,
        cover: item.cover || defaultRescueImage
      }))
    }
  } catch (error) {
    console.error('加载救助任务列表失败', error)
    uni.showToast({
      title: '加载失败，请重试',
      icon: 'none'
    })
  }
}

const loadStats = async () => {
  try {
    const res = await rescueApi.getTaskList({ page: 1, size: 100 })
    if (res.data && res.data.records) {
      const records = res.data.records
      totalRescue.value = records.filter(r => r.status === 'pending' || r.status === 'processing').length
      completedRescue.value = records.filter(r => r.status === 'completed').length
      totalVolunteer.value = 128
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

const handleImageError = (e, id) => {
  console.log('图片加载失败', id)
  const item = rescueList.value.find(r => r.id === id)
  if (item) {
    item.cover = defaultRescueImage
  }
}

const handleDetail = (item) => {
  uni.navigateTo({
    url: `/pages/rescue/detail?id=${item.id}`
  })
}

const handleParticipate = async (item) => {
  uni.showModal({
    title: '确认参与',
    content: '确定要参与这个救助任务吗？',
    confirmColor: '#FF5A5F',
    success: async (res) => {
      if (res.confirm) {
        try {
          uni.showLoading({ title: '提交中...' })
          await rescueApi.acceptTask(item.id)
          uni.hideLoading()
          uni.showToast({
            title: '参与成功',
            icon: 'success'
          })
          loadData()
        } catch (error) {
          uni.hideLoading()
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
  uni.navigateTo({
    url: `/pages/rescue/progress?id=${item.id}`
  })
}

const handlePublish = () => {
  uni.navigateTo({
    url: '/pages/rescue/publish'
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
  background: var(--bg-page);
  padding-bottom: 160rpx;
}

/* 统计栏 - 更温馨的设计 */
.stats-section {
  padding: var(--spacing-lg);
}

.stats-card {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: var(--spacing-xl);
  background: var(--bg-white);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-card);
  border: 1rpx solid var(--border-color);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  flex: 1;
}

.stat-icon-wrapper {
  width: 80rpx;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  
  &.rescue {
    background: linear-gradient(135deg, #FF6B6B 0%, #FF8787 100%);
  }
  
  &.completed {
    background: linear-gradient(135deg, #00A699 0%, #00C4B3 100%);
  }
  
  &.volunteer {
    background: linear-gradient(135deg, #FF5A5F 0%, #FF7A7F 100%);
  }
}

.stat-icon {
  font-size: 44rpx;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
  flex: 1;
}

.stat-value {
  font-size: 36rpx;
  font-weight: 600;
  color: var(--text-primary);
  letter-spacing: -0.01em;
}

.stat-label {
  font-size: 24rpx;
  color: var(--text-light);
}

.stat-divider {
  width: 2rpx;
  height: 60rpx;
  background: var(--divider-color);
}

/* 状态筛选 - 更圆润的设计 */
.status-scroll {
  white-space: nowrap;
  background: var(--bg-white);
  padding: var(--spacing-lg) 0;
  margin-bottom: var(--spacing-md);
  border-bottom: 1rpx solid var(--border-color);
}

.status-list {
  display: inline-flex;
  gap: var(--spacing-md);
  padding: 0 var(--spacing-lg);
}

.status-item {
  display: inline-flex;
  align-items: center;
  gap: 8rpx;
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--bg-gray);
  border-radius: var(--radius-full);
  border: 1rpx solid var(--border-color);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  white-space: nowrap;

  &.active {
    background: var(--primary-color);
    border-color: var(--primary-color);
    box-shadow: var(--shadow-sm);
    
    .status-text {
      color: #fff;
      font-weight: 600;
    }
    
    .status-emoji {
      filter: brightness(1.1);
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
  color: var(--text-secondary);
  white-space: nowrap;
}

/* 救助任务列表 - Pinterest 风格卡片 */
.rescue-list {
  padding: 0 var(--spacing-lg);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.rescue-card {
  position: relative;
  background: var(--bg-white);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-card);
  border: 1rpx solid var(--border-color);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:active {
    transform: translateY(-4rpx);
    box-shadow: var(--shadow-md);
  }
}

.urgent-tag {
  position: absolute;
  top: var(--spacing-lg);
  right: var(--spacing-lg);
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 10rpx 24rpx;
  background: rgba(255, 90, 95, 0.95);
  backdrop-filter: blur(10rpx);
  border-radius: var(--radius-full);
  z-index: 10;
  box-shadow: var(--shadow-sm);
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

.urgent-icon {
  font-size: 28rpx;
}

.urgent-text {
  font-size: 24rpx;
  font-weight: 500;
  color: #fff;
}

.rescue-image-wrapper {
  width: 100%;
  height: 400rpx;
  overflow: hidden;
}

.rescue-image {
  width: 100%;
  height: 100%;
  background: var(--bg-warm);
  transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.rescue-card:active .rescue-image {
  transform: scale(1.05);
}

.rescue-content {
  padding: var(--spacing-xl);
}

.rescue-title {
  font-size: 36rpx;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.5;
  margin-bottom: var(--spacing-lg);
  letter-spacing: -0.01em;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.rescue-meta {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-lg);
}

.meta-row {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  flex: 1;
  min-width: 0;
}

.meta-icon {
  font-size: 28rpx;
  flex-shrink: 0;
}

.meta-text {
  font-size: 26rpx;
  color: var(--text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.rescue-desc {
  font-size: 28rpx;
  color: var(--text-secondary);
  line-height: 1.7;
  margin-bottom: var(--spacing-lg);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.rescue-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: var(--spacing-lg);
  border-top: 1rpx solid var(--divider-color);
}

.status-badge {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 8rpx 20rpx;
  border-radius: var(--radius-full);
  font-size: 24rpx;
  font-weight: 500;

  &.pending {
    background: #FFF3E0;
    
    .status-dot {
      background: var(--warning-color);
    }
    
    .status-text {
      color: var(--warning-color);
    }
  }

  &.processing {
    background: #E8F5E9;
    
    .status-dot {
      background: var(--success-color);
    }
    
    .status-text {
      color: var(--success-color);
    }
  }

  &.completed {
    background: #E3F2FD;
    
    .status-dot {
      background: var(--primary-color);
    }
    
    .status-text {
      color: var(--primary-color);
    }
  }
}

.status-dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
}

.action-group {
  display: flex;
  gap: var(--spacing-md);
}

.btn-participate,
.btn-detail {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-sm) var(--spacing-lg);
  border-radius: var(--radius-full);
  border: none;
  font-size: 28rpx;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-participate {
  background: var(--primary-color);
  color: #fff;
  box-shadow: var(--shadow-sm);
  
  &:active {
    background: var(--primary-dark);
    transform: scale(0.95);
  }
}

.btn-detail {
  background: var(--bg-gray);
  color: var(--text-secondary);
  
  &:active {
    background: #E0E0E0;
  }
}

.btn-text {
  font-size: 28rpx;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xxl) 0;
  gap: var(--spacing-md);
}

.empty-icon {
  font-size: 140rpx;
  opacity: 0.25;
}

.empty-text {
  font-size: 30rpx;
  color: var(--text-secondary);
  font-weight: 500;
}

.empty-desc {
  font-size: 26rpx;
  color: var(--text-light);
}

/* 发布按钮 - 更温馨的设计 */
.fab-btn {
  position: fixed;
  right: var(--spacing-xl);
  bottom: 160rpx;
  width: 120rpx;
  height: 120rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-color);
  border-radius: 50%;
  box-shadow: var(--shadow-lg);
  z-index: 100;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:active {
    transform: scale(0.95);
    background: var(--primary-dark);
  }
}

.fab-icon {
  font-size: 56rpx;
  color: #fff;
  font-weight: 300;
  line-height: 1;
}
</style>
