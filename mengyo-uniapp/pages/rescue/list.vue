<template>
  <view class="page">
    <!-- 顶部搜索栏 -->
    <view class="header-section">
      <view class="search-bar" @click="handleSearch">
        <text class="search-icon">🔍</text>
        <text class="search-placeholder">搜索救助任务...</text>
      </view>
    </view>

    <!-- 状态筛选标签 -->
    <scroll-view scroll-x class="status-scroll" :show-scrollbar="false">
      <view class="status-list">
        <view 
          v-for="(item, index) in statusTabs" 
          :key="index"
          class="status-item"
          :class="{ active: currentStatus === item.value }"
          @click="currentStatus = item.value"
        >
          <text class="status-icon">{{ item.icon }}</text>
          <text class="status-text">{{ item.label }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 救助任务列表 - Petfinder风格 -->
    <view class="tasks-container">
      <view 
        v-for="(item, index) in taskList" 
        :key="item.id"
        class="task-card"
        :style="{ animationDelay: `${index * 0.1}s` }"
        @click="handleDetail(item)"
      >
        <!-- 紧急标记 -->
        <view v-if="item.urgency === 'URGENT'" class="urgent-banner">
          <text class="urgent-icon">🚨</text>
          <text class="urgent-text">紧急求助</text>
        </view>

        <view class="task-image-wrapper">
          <image 
            :src="getTaskImage(item)" 
            mode="aspectFill" 
            class="task-image"
            @error="handleImageError($event, item)"
          ></image>
          
          <view class="status-badge" :class="getStatusClass(item.status)">
            <text class="status-text">{{ getStatusText(item.status) }}</text>
          </view>
        </view>

        <view class="task-content">
          <text class="task-title">{{ item.title }}</text>
          
          <view class="task-meta">
            <view class="meta-item">
              <text class="meta-icon">📍</text>
              <text class="meta-text">{{ getLocationText(item) }}</text>
            </view>
            <view class="meta-item">
              <text class="meta-icon">⏰</text>
              <text class="meta-text">{{ formatTime(item.createTime) }}</text>
            </view>
          </view>

          <view class="task-desc" v-if="item.description">
            {{ item.description }}
          </view>

          <view class="task-footer">
            <view class="animal-info">
              <text class="animal-type-icon">{{ getAnimalIcon(item.animalType) }}</text>
              <text class="animal-type-text">{{ getAnimalTypeText(item.animalType) }}</text>
            </view>
            
            <button 
              v-if="item.status === 'PUBLISHED'"
              class="btn-accept"
              @click.stop="handleAccept(item)"
            >
              <text class="btn-text">接受任务</text>
            </button>
            <button 
              v-else-if="item.status === 'ACCEPTED'"
              class="btn-progress"
              @click.stop="handleProgress(item)"
            >
              <text class="btn-text">查看进度</text>
            </button>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-if="taskList.length === 0 && !loading" class="empty-state">
        <text class="empty-icon">🆘</text>
        <text class="empty-text">暂无救助任务</text>
        <text class="empty-desc">期待您的爱心参与</text>
      </view>

      <!-- 加载更多 -->
      <view v-if="hasMore && taskList.length > 0" class="load-more" @click="loadMore">
        <text class="load-more-text">{{ loading ? '加载中...' : '加载更多' }}</text>
      </view>
    </view>

    <!-- 发布按钮 -->
    <view class="fab-btn" @click="handlePublish">
      <text class="fab-icon">+</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { rescueApi } from '@/utils/api'

const currentStatus = ref('all')
const loading = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)
const taskList = ref([])

const statusTabs = ref([
  { label: '全部', value: 'all', icon: '📋' },
  { label: '待救助', value: 'PUBLISHED', icon: '🆘' },
  { label: '救助中', value: 'ACCEPTED', icon: '🏃' },
  { label: '已完成', value: 'COMPLETED', icon: '✅' }
])

onMounted(() => {
  loadTasks()
})

watch(currentStatus, () => {
  loadTasks(true)
})

const loadTasks = async (reset = false) => {
  if (loading.value) return
  
  loading.value = true
  
  if (reset) {
    currentPage.value = 1
    taskList.value = []
  }
  
  try {
    const params = {
      page: currentPage.value,
      size: 20
    }
    
    if (currentStatus.value !== 'all') {
      params.status = currentStatus.value
    }
    
    const res = await rescueApi.getTaskList(params)
    if (res.data && res.data.records) {
      const newList = res.data.records.map(item => ({
        ...item,
        cover: item.cover || getDefaultImage()
      }))
      
      if (reset) {
        taskList.value = newList
      } else {
        taskList.value = [...taskList.value, ...newList]
      }
      
      hasMore.value = res.data.records.length === 20
      currentPage.value++
    }
  } catch (error) {
    console.error('加载救助任务失败', error)
    uni.showToast({
      title: '加载失败，请重试',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  if (!loading.value && hasMore.value) {
    loadTasks()
  }
}

const getTaskImage = (item) => {
  if (item.cover) return item.cover
  if (item.animalId && item.animalImages) {
    const images = typeof item.animalImages === 'string' ? JSON.parse(item.animalImages) : item.animalImages
    if (images && images.length > 0) return images[0]
  }
  return getDefaultImage()
}

const getDefaultImage = () => {
  return 'https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?w=600&q=80'
}

const handleImageError = (e, item) => {
  item.cover = getDefaultImage()
}

const getLocationText = (item) => {
  if (item.locationAddress) return item.locationAddress
  if (item.locationCity && item.locationDistrict) {
    return `${item.locationCity} ${item.locationDistrict}`
  }
  if (item.locationCity) return item.locationCity
  return '位置待确认'
}

const getStatusText = (status) => {
  const statusMap = {
    'PUBLISHED': '待救助',
    'ACCEPTED': '救助中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || '未知'
}

const getStatusClass = (status) => {
  const classMap = {
    'PUBLISHED': 'published',
    'ACCEPTED': 'accepted',
    'COMPLETED': 'completed',
    'CANCELLED': 'cancelled'
  }
  return classMap[status] || 'published'
}

const getAnimalIcon = (type) => {
  const iconMap = {
    'DOG': '🐕',
    'CAT': '🐈',
    'OTHER': '🦊'
  }
  return iconMap[type] || '🐾'
}

const getAnimalTypeText = (type) => {
  const textMap = {
    'DOG': '狗狗',
    'CAT': '猫咪',
    'OTHER': '其他'
  }
  return textMap[type] || '未知'
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

const handleDetail = (item) => {
  uni.navigateTo({
    url: `/pages/rescue/detail?id=${item.id}`
  })
}

const handleAccept = async (item) => {
  uni.showModal({
    title: '确认接受',
    content: '确定要接受这个救助任务吗？',
    confirmColor: '#FF5A5F',
    success: async (res) => {
      if (res.confirm) {
        try {
          uni.showLoading({ title: '提交中...' })
          await rescueApi.acceptTask(item.id)
          uni.hideLoading()
          uni.showToast({
            title: '接受成功',
            icon: 'success'
          })
          loadTasks(true)
        } catch (error) {
          uni.hideLoading()
          console.error('接受任务失败', error)
          uni.showToast({
            title: '操作失败，请重试',
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

const handleSearch = () => {
  uni.showToast({
    title: '搜索功能开发中',
    icon: 'none'
  })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #F5F5F5;
  padding-bottom: 180rpx;
}

/* 顶部搜索栏 */
.header-section {
  padding: 24rpx 32rpx;
  background: #FFFFFF;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 20rpx 32rpx;
  background: #F5F5F5;
  border-radius: 50rpx;
  transition: all 0.3s ease;
  
  &:active {
    background: #EEEEEE;
  }
}

.search-icon {
  font-size: 32rpx;
  color: #666;
}

.search-placeholder {
  flex: 1;
  font-size: 28rpx;
  color: #999;
}

/* 状态筛选 */
.status-scroll {
  white-space: nowrap;
  background: #FFFFFF;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #EEEEEE;
}

.status-list {
  display: inline-flex;
  gap: 16rpx;
  padding: 0 32rpx;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 24rpx;
  background: #F5F5F5;
  border-radius: 50rpx;
  transition: all 0.3s ease;
  
  &.active {
    background: #FF5A5F;
    box-shadow: 0 2rpx 8rpx rgba(255, 90, 95, 0.3);
    
    .status-text {
      color: #FFFFFF;
      font-weight: 600;
    }
  }
  
  &:active {
    transform: scale(0.95);
  }
}

.status-icon {
  font-size: 28rpx;
}

.status-text {
  font-size: 26rpx;
  color: #666;
  white-space: nowrap;
}

/* 任务列表 */
.tasks-container {
  padding: 24rpx 32rpx;
}

.task-card {
  position: relative;
  margin-bottom: 32rpx;
  background: #FFFFFF;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  animation: fadeInUp 0.6s ease-out both;
  
  &:active {
    transform: translateY(-4rpx);
    box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.12);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.urgent-banner {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  padding: 16rpx;
  background: linear-gradient(135deg, #FF5A5F 0%, #FF7A7F 100%);
  color: #FFFFFF;
  font-size: 28rpx;
  font-weight: 600;
  z-index: 10;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.9;
  }
}

.urgent-icon {
  font-size: 32rpx;
}

.urgent-text {
  font-size: 28rpx;
  font-weight: 600;
}

.task-image-wrapper {
  position: relative;
  width: 100%;
  height: 400rpx;
  overflow: hidden;
}

.task-image {
  width: 100%;
  height: 100%;
  background: #F5F5F5;
  transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.task-card:active .task-image {
  transform: scale(1.1);
}

.status-badge {
  position: absolute;
  top: 16rpx;
  right: 16rpx;
  padding: 8rpx 20rpx;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10rpx);
  border-radius: 50rpx;
  font-size: 22rpx;
  font-weight: 500;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
  
  &.published {
    .status-text {
      color: #FF5A5F;
    }
  }
  
  &.accepted {
    .status-text {
      color: #FCB900;
    }
  }
  
  &.completed {
    .status-text {
      color: #00A699;
    }
  }
}

.status-text {
  font-size: 22rpx;
}

.task-content {
  padding: 32rpx;
}

.task-title {
  display: block;
  font-size: 36rpx;
  font-weight: 600;
  color: #2C2C2C;
  margin-bottom: 24rpx;
  letter-spacing: -0.01em;
  line-height: 1.5;
}

.task-meta {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  margin-bottom: 24rpx;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.meta-icon {
  font-size: 28rpx;
  flex-shrink: 0;
}

.meta-text {
  flex: 1;
  font-size: 26rpx;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.task-desc {
  font-size: 28rpx;
  color: #666;
  line-height: 1.7;
  margin-bottom: 24rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.task-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 24rpx;
  border-top: 1rpx solid #F0F0F0;
}

.animal-info {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.animal-type-icon {
  font-size: 36rpx;
}

.animal-type-text {
  font-size: 28rpx;
  color: #666;
  font-weight: 500;
}

.btn-accept,
.btn-progress {
  padding: 16rpx 32rpx;
  border-radius: 16rpx;
  border: none;
  font-size: 28rpx;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-accept {
  background: #FF5A5F;
  color: #FFFFFF;
  box-shadow: 0 4rpx 12rpx rgba(255, 90, 95, 0.3);
  
  &:active {
    background: #E04A4F;
    transform: scale(0.95);
  }
}

.btn-progress {
  background: #F5F5F5;
  color: #666;
  
  &:active {
    background: #EEEEEE;
  }
}

.btn-text {
  font-size: 28rpx;
  font-weight: 600;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 0;
  gap: 24rpx;
}

.empty-icon {
  font-size: 120rpx;
  opacity: 0.3;
}

.empty-text {
  font-size: 32rpx;
  color: #666;
  font-weight: 500;
}

.empty-desc {
  font-size: 26rpx;
  color: #999;
}

/* 加载更多 */
.load-more {
  margin-top: 32rpx;
  padding: 32rpx;
  text-align: center;
  transition: all 0.3s ease;
  
  &:active {
    opacity: 0.7;
  }
}

.load-more-text {
  font-size: 28rpx;
  color: #FF5A5F;
  font-weight: 500;
}

/* 发布按钮 */
.fab-btn {
  position: fixed;
  right: 32rpx;
  bottom: 180rpx;
  width: 120rpx;
  height: 120rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #FF5A5F;
  border-radius: 50%;
  box-shadow: 0 8rpx 24rpx rgba(255, 90, 95, 0.4);
  z-index: 100;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:active {
    transform: scale(0.95);
    background: #E04A4F;
  }
}

.fab-icon {
  font-size: 64rpx;
  color: #FFFFFF;
  font-weight: 300;
  line-height: 1;
}
</style>
