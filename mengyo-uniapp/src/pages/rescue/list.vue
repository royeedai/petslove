<template>
  <view class="page">
    <!-- 顶部统计栏 -->
    <view class="stats-bar">
      <view class="stat-item">
        <text class="stat-icon">🆘</text>
        <view class="stat-info">
          <text class="stat-value">{{ totalRescue }}</text>
          <text class="stat-label">救助中</text>
        </view>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <text class="stat-icon">✅</text>
        <view class="stat-info">
          <text class="stat-value">{{ completedRescue }}</text>
          <text class="stat-label">已完成</text>
        </view>
      </view>
      <view class="stat-divider"></view>
      <view class="stat-item">
        <text class="stat-icon">👥</text>
        <view class="stat-info">
          <text class="stat-value">{{ totalVolunteer }}</text>
          <text class="stat-label">志愿者</text>
        </view>
      </view>
    </view>

    <!-- 状态筛选 -->
    <scroll-view scroll-x class="status-scroll">
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

    <!-- 救助任务列表 -->
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
            
            <view class="action-group">
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
        cover: item.cover || 'https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?w=600&q=80'
      }))
    }
  } catch (error) {
    console.error('加载救助任务列表失败', error)
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
      // 志愿者数量需要从后端获取，这里暂时设置为固定值
      totalVolunteer.value = 128
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

const handleDetail = (item) => {
  uni.showToast({
    title: '详情页面开发中',
    icon: 'none'
  })
}

const handleParticipate = async (item) => {
  uni.showModal({
    title: '确认参与',
    content: '确定要参与这个救助任务吗？',
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
  background: #F8F9FA;
  padding-bottom: 120rpx;
}

/* 统计栏 */
.stats-bar {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 32rpx 24rpx;
  margin: 24rpx;
  background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
  border-radius: 24rpx;
  box-shadow: 0 8rpx 32rpx rgba(255, 140, 66, 0.25);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.stat-icon {
  font-size: 48rpx;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.stat-value {
  font-size: 32rpx;
  font-weight: 600;
  color: #fff;
}

.stat-label {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.8);
}

.stat-divider {
  width: 2rpx;
  height: 60rpx;
  background: rgba(255, 255, 255, 0.3);
}

/* 状态筛选 */
.status-scroll {
  white-space: nowrap;
  background: #fff;
  padding: 20rpx 0;
  margin-bottom: 16rpx;
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
  background: #F8F9FA;
  border-radius: 40rpx;
  transition: all 0.3s ease;

  &.active {
    background: linear-gradient(135deg, #FFE5D9 0%, #FFDCC5 100%);
    
    .status-text {
      color: #FF8C42;
      font-weight: 600;
    }
  }
}

.status-emoji {
  font-size: 32rpx;
}

.status-text {
  font-size: 26rpx;
  color: #7F8C8D;
  white-space: nowrap;
}

/* 救助任务列表 */
.rescue-list {
  padding: 0 24rpx;
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.rescue-card {
  position: relative;
  background: #fff;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.urgent-tag {
  position: absolute;
  top: 20rpx;
  right: 20rpx;
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 24rpx;
  background: rgba(255, 71, 87, 0.95);
  border-radius: 40rpx;
  z-index: 10;
  backdrop-filter: blur(10rpx);
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

.rescue-image {
  width: 100%;
  height: 360rpx;
  background: linear-gradient(135deg, #F8F9FA 0%, #E8EAED 100%);
}

.rescue-content {
  padding: 28rpx;
}

.rescue-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #2C3E50;
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
  font-size: 28rpx;
}

.meta-text {
  font-size: 24rpx;
  color: #7F8C8D;
}

.rescue-desc {
  font-size: 26rpx;
  color: #7F8C8D;
  line-height: 1.6;
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
  border-top: 1rpx solid #E8EAED;
}

.status-badge {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  font-weight: 500;

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
    background: linear-gradient(135deg, #D4EDDA 0%, #C3E6CB 100%);
    
    .status-dot {
      background: #28A745;
    }
    
    .status-text {
      color: #28A745;
    }
  }

  &.completed {
    background: linear-gradient(135deg, #D1ECF1 0%, #BEE5EB 100%);
    
    .status-dot {
      background: #17A2B8;
    }
    
    .status-text {
      color: #17A2B8;
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
  gap: 16rpx;
}

.btn-participate,
.btn-detail {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 28rpx;
  border-radius: 40rpx;
  border: none;
  font-size: 26rpx;
  transition: all 0.3s ease;
}

.btn-participate {
  background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
  color: #fff;
  box-shadow: 0 4rpx 12rpx rgba(255, 140, 66, 0.3);
}

.btn-detail {
  background: #F8F9FA;
  color: #7F8C8D;
}

.btn-icon {
  font-size: 28rpx;
}

.btn-text {
  font-size: 26rpx;
  font-weight: 500;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 0;
  gap: 20rpx;
}

.empty-icon {
  font-size: 120rpx;
  opacity: 0.3;
}

.empty-text {
  font-size: 28rpx;
  color: #7F8C8D;
  font-weight: 500;
}

.empty-desc {
  font-size: 24rpx;
  color: #95A5A6;
}

/* 发布按钮 */
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
  box-shadow: 0 8rpx 32rpx rgba(255, 140, 66, 0.4);
  z-index: 100;
  transition: all 0.3s ease;
}

.fab-icon {
  font-size: 52rpx;
}
</style>
