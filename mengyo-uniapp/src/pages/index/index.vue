<template>
  <view class="page">
    <!-- 自定义导航栏 -->
    <view class="custom-navbar">
      <view class="navbar-content">
        <view class="navbar-left">
          <view class="logo-wrapper">
            <text class="logo-icon">🐾</text>
          </view>
          <view class="app-info">
            <text class="app-name">萌友益站</text>
            <text class="app-slogan">守护每个生命</text>
          </view>
        </view>
        <view class="navbar-right">
          <view class="icon-btn" @click="handleNotification">
            <text class="icon">🔔</text>
            <view v-if="hasUnread" class="badge-dot"></view>
          </view>
        </view>
      </view>
    </view>

    <!-- 滚动内容区 -->
    <scroll-view scroll-y class="scroll-content" :style="{ height: scrollHeight }">
      <!-- 轮播图 -->
      <view class="banner-section">
        <swiper 
          :indicator-dots="true" 
          :autoplay="true" 
          :interval="4000" 
          :duration="500"
          :circular="true"
          indicator-color="rgba(168, 162, 158, 0.3)"
          indicator-active-color="#FF8C42"
          class="banner-swiper"
        >
          <swiper-item v-for="(item, index) in banners" :key="index">
            <view class="banner-item" @click="handleBannerClick(item)">
              <image :src="item.imageUrl" mode="aspectFill" class="banner-image"></image>
              <view class="banner-overlay">
                <text class="banner-title">{{ item.title || '让爱心传递温暖' }}</text>
                <text class="banner-subtitle">{{ item.subtitle || '每一次救助都值得被记录' }}</text>
              </view>
            </view>
          </swiper-item>
        </swiper>
      </view>

      <!-- 功能入口卡片 -->
      <view class="features-section">
        <view class="features-grid">
          <view class="feature-item" @click="navigateTo('/pages/rescue/list')">
            <view class="feature-icon-wrapper urgent">
              <text class="feature-icon">🆘</text>
            </view>
            <text class="feature-label">救助任务</text>
            <text class="feature-desc">立即参与</text>
          </view>
          
          <view class="feature-item" @click="navigateTo('/pages/animal/list')">
            <view class="feature-icon-wrapper animal">
              <text class="feature-icon">🐾</text>
            </view>
            <text class="feature-label">流浪动物</text>
            <text class="feature-desc">等待领养</text>
          </view>
          
          <view class="feature-item" @click="handleDonation">
            <view class="feature-icon-wrapper donation">
              <text class="feature-icon">❤️</text>
            </view>
            <text class="feature-label">爱心捐赠</text>
            <text class="feature-desc">献出爱心</text>
          </view>
          
          <view class="feature-item" @click="handleAdoption">
            <view class="feature-icon-wrapper adoption">
              <text class="feature-icon">🏠</text>
            </view>
            <text class="feature-label">领养申请</text>
            <text class="feature-desc">给TA一个家</text>
          </view>
        </view>
      </view>

      <!-- 统计数据卡片 -->
      <view class="stats-section">
        <view class="stats-card">
          <view class="stat-item">
            <text class="stat-value">{{ stats.rescueCount }}</text>
            <text class="stat-label">救助次数</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-value">{{ stats.animalCount }}</text>
            <text class="stat-label">动物在库</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-value">{{ stats.adoptionCount }}</text>
            <text class="stat-label">成功领养</text>
          </view>
        </view>
      </view>

      <!-- 最新救助任务 -->
      <view class="section">
        <view class="section-header">
          <view class="header-left">
            <view class="section-icon-wrapper urgent-bg">
              <text class="section-icon">🆘</text>
            </view>
            <view class="header-text">
              <text class="section-title">紧急救助</text>
              <text class="section-subtitle">需要您的帮助</text>
            </view>
          </view>
          <view class="section-more" @click="navigateTo('/pages/rescue/list')">
            <text class="more-text">更多</text>
            <text class="more-arrow">›</text>
          </view>
        </view>
        
        <view class="rescue-list" v-if="rescueList.length > 0">
          <view 
            class="rescue-card" 
            v-for="item in rescueList" 
            :key="item.id"
            @click="handleRescueDetail(item)"
          >
            <image :src="item.cover" mode="aspectFill" class="rescue-image"></image>
            
            <!-- 紧急标签 -->
            <view v-if="item.urgencyLevel === 'urgent'" class="urgent-badge">
              <text class="badge-pulse"></text>
              <text class="badge-text">紧急</text>
            </view>
            
            <view class="rescue-content">
              <text class="rescue-title">{{ item.title }}</text>
              
              <view class="rescue-meta">
                <view class="meta-item">
                  <text class="meta-icon">📍</text>
                  <text class="meta-text">{{ item.address }}</text>
                </view>
                <view class="meta-item">
                  <text class="meta-icon">⏰</text>
                  <text class="meta-text">{{ formatTime(item.createTime) }}</text>
                </view>
              </view>

              <view class="rescue-footer">
                <view class="status-badge" :class="item.status">
                  {{ getStatusText(item.status) }}
                </view>
                <view class="rescue-action">
                  <text class="action-text">查看详情</text>
                  <text class="action-arrow">›</text>
                </view>
              </view>
            </view>
          </view>
        </view>
        
        <view v-else class="empty-state">
          <text class="empty-icon">🔍</text>
          <text class="empty-title">暂无救助任务</text>
          <text class="empty-description">当前没有紧急救助需求</text>
        </view>
      </view>

      <!-- 社区动态 -->
      <view class="section">
        <view class="section-header">
          <view class="header-left">
            <view class="section-icon-wrapper community-bg">
              <text class="section-icon">💬</text>
            </view>
            <view class="header-text">
              <text class="section-title">社区动态</text>
              <text class="section-subtitle">分享爱宠故事</text>
            </view>
          </view>
          <view class="section-more" @click="navigateTo('/pages/community/list')">
            <text class="more-text">更多</text>
            <text class="more-arrow">›</text>
          </view>
        </view>
        
        <view class="post-list" v-if="postList.length > 0">
          <view 
            class="post-card" 
            v-for="item in postList" 
            :key="item.id"
            @click="handlePostDetail(item)"
          >
            <view class="post-header">
              <image :src="item.userAvatar" mode="aspectFill" class="user-avatar"></image>
              <view class="user-info">
                <text class="user-nickname">{{ item.userNickname }}</text>
                <text class="post-time">{{ formatTime(item.createTime) }}</text>
              </view>
            </view>
            
            <text class="post-content">{{ item.content }}</text>
            
            <!-- 图片预览 -->
            <view v-if="item.images && item.images.length > 0" class="post-images">
              <image 
                v-for="(img, imgIdx) in item.images.slice(0, 3)" 
                :key="imgIdx"
                :src="img" 
                mode="aspectFill" 
                class="post-image"
                :class="{ 'single': item.images.length === 1 }"
                @click.stop="previewImage(item.images, imgIdx)"
              ></image>
              <view v-if="item.images.length > 3" class="more-images">
                <text class="more-text">+{{ item.images.length - 3 }}</text>
              </view>
            </view>
            
            <view class="post-actions">
              <view class="action-item">
                <text class="action-icon">❤️</text>
                <text class="action-count">{{ item.likeCount || 0 }}</text>
              </view>
              <view class="action-item">
                <text class="action-icon">💬</text>
                <text class="action-count">{{ item.commentCount || 0 }}</text>
              </view>
            </view>
          </view>
        </view>
        
        <view v-else class="empty-state">
          <text class="empty-icon">📝</text>
          <text class="empty-title">暂无社区动态</text>
          <text class="empty-description">快来发布第一条动态吧</text>
        </view>
      </view>

      <!-- 底部间距 -->
      <view class="page-footer"></view>
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { rescueApi, communityApi } from '@/utils/api'

const hasUnread = ref(false)
const banners = ref([
  {
    imageUrl: 'https://images.unsplash.com/photo-1450778869180-41d0601e046e?w=800&q=80',
    title: '让爱心传递温暖',
    subtitle: '每一次救助都值得被记录'
  },
  {
    imageUrl: 'https://images.unsplash.com/photo-1415369629372-26f2fe60c467?w=800&q=80',
    title: '给TA一个温暖的家',
    subtitle: '领养代替购买，让生命延续'
  },
  {
    imageUrl: 'https://images.unsplash.com/photo-1425082661705-1834bfd09dca?w=800&q=80',
    title: '每个生命都值得被珍视',
    subtitle: '一起守护流浪的小天使'
  }
])

const stats = ref({
  rescueCount: 0,
  animalCount: 0,
  adoptionCount: 0
})

const rescueList = ref([])
const postList = ref([])

// 计算滚动区域高度
const scrollHeight = computed(() => {
  const systemInfo = uni.getSystemInfoSync()
  const statusBarHeight = systemInfo.statusBarHeight || 0
  const navbarHeight = 88 // 自定义导航栏高度 (rpx)
  const tabbarHeight = 100 // 底部 tabbar 高度估算
  const windowHeight = systemInfo.windowHeight
  // 转换为 rpx
  const scrollH = windowHeight - (statusBarHeight + navbarHeight / 2 + tabbarHeight / 2)
  return scrollH + 'px'
})

onMounted(() => {
  loadData()
  loadStats()
})

const loadData = async () => {
  try {
    // 加载最新救助任务
    const rescueRes = await rescueApi.getTaskList({ page: 1, size: 3 })
    if (rescueRes.data && rescueRes.data.records) {
      rescueList.value = rescueRes.data.records.map(item => ({
        ...item,
        cover: item.cover || 'https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?w=600&q=80'
      }))
    }
  } catch (error) {
    console.error('加载救助任务失败', error)
  }

  try {
    // 加载社区动态
    const postRes = await communityApi.getPostList({ page: 1, size: 3 })
    if (postRes.data && postRes.data.records) {
      postList.value = postRes.data.records.map(item => ({
        ...item,
        userAvatar: item.userAvatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${item.userId}`
      }))
    }
  } catch (error) {
    console.error('加载社区动态失败', error)
  }
}

const loadStats = () => {
  // 模拟统计数据
  stats.value = {
    rescueCount: 156,
    animalCount: 42,
    adoptionCount: 89
  }
}

const handleNotification = () => {
  uni.navigateTo({
    url: '/pages/user/messages'
  })
}

const handleBannerClick = (item) => {
  console.log('点击轮播图:', item)
}

const navigateTo = (url) => {
  uni.navigateTo({ url })
}

const handleDonation = () => {
  uni.showToast({
    title: '捐赠功能开发中',
    icon: 'none'
  })
}

const handleAdoption = () => {
  uni.showToast({
    title: '领养申请功能开发中',
    icon: 'none'
  })
}

const handleRescueDetail = (item) => {
  uni.showToast({
    title: '救助详情页面开发中',
    icon: 'none'
  })
}

const handlePostDetail = (item) => {
  uni.showToast({
    title: '动态详情页面开发中',
    icon: 'none'
  })
}

const previewImage = (images, current) => {
  uni.previewImage({
    urls: images,
    current: current
  })
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

const getStatusText = (status) => {
  const statusMap = {
    'pending': '待救助',
    'processing': '救助中',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return statusMap[status] || '未知'
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: linear-gradient(180deg, #FFFBF5 0%, #FEF7F0 100%);
}

/* ===================================
   自定义导航栏
   =================================== */

.custom-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 999;
  background: rgba(255, 251, 245, 0.95);
  backdrop-filter: blur(20rpx);
  border-bottom: 1rpx solid #F5F5F4;
  padding-top: env(safe-area-inset-top);
}

.navbar-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 24rpx;
  height: 88rpx;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.logo-wrapper {
  width: 64rpx;
  height: 64rpx;
  background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba(255, 140, 66, 0.25);
}

.logo-icon {
  font-size: 36rpx;
}

.app-info {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.app-name {
  font-size: 32rpx;
  font-weight: 700;
  color: #57534E;
}

.app-slogan {
  font-size: 22rpx;
  color: #A8A29E;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.icon-btn {
  position: relative;
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #FFFFFF;
  border-radius: 16rpx;
  border: 1rpx solid #F5F5F4;
  transition: all 0.3s ease;
  
  .icon {
    font-size: 32rpx;
  }
  
  &:active {
    transform: scale(0.95);
  }
}

.badge-dot {
  position: absolute;
  top: 12rpx;
  right: 12rpx;
  width: 16rpx;
  height: 16rpx;
  background: #EF4444;
  border-radius: 50%;
  border: 2rpx solid #FFFBF5;
}

/* ===================================
   滚动内容区
   =================================== */

.scroll-content {
  padding-top: calc(env(safe-area-inset-top) + 108rpx);
}

/* ===================================
   轮播图区域
   =================================== */

.banner-section {
  padding: 0 24rpx 24rpx;
}

.banner-swiper {
  width: 100%;
  height: 380rpx;
  border-radius: 32rpx;
  overflow: hidden;
}

.banner-item {
  position: relative;
  width: 100%;
  height: 100%;
}

.banner-image {
  width: 100%;
  height: 100%;
}

.banner-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 40rpx 32rpx;
  background: linear-gradient(180deg, transparent 0%, rgba(0, 0, 0, 0.6) 100%);
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.banner-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #FFFFFF;
  line-height: 1.3;
}

.banner-subtitle {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.85);
  line-height: 1.5;
}

/* ===================================
   功能入口区域
   =================================== */

.features-section {
  padding: 0 24rpx 24rpx;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20rpx;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  padding: 24rpx 12rpx;
  background: linear-gradient(to bottom right, #FFFFFF 0%, #FEF7F0 100%);
  border-radius: 24rpx;
  border: 1rpx solid #F5F5F4;
  transition: all 0.3s ease;
  
  &:active {
    transform: translateY(-4rpx);
    box-shadow: 0 8rpx 24rpx rgba(255, 140, 66, 0.15);
  }
}

.feature-icon-wrapper {
  width: 88rpx;
  height: 88rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 16rpx rgba(255, 140, 66, 0.12);
  
  &.urgent {
    background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
  }
  
  &.animal {
    background: linear-gradient(135deg, #36D1DC 0%, #5B86E5 100%);
  }
  
  &.donation {
    background: linear-gradient(135deg, #F093FB 0%, #F5576C 100%);
  }
  
  &.adoption {
    background: linear-gradient(135deg, #4FACFE 0%, #00F2FE 100%);
  }
}

.feature-icon {
  font-size: 44rpx;
}

.feature-label {
  font-size: 26rpx;
  font-weight: 600;
  color: #57534E;
  line-height: 1.3;
  text-align: center;
}

.feature-desc {
  font-size: 20rpx;
  color: #A8A29E;
  text-align: center;
}

/* ===================================
   统计数据区域
   =================================== */

.stats-section {
  padding: 0 24rpx 24rpx;
}

.stats-card {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 32rpx;
  background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
  border-radius: 32rpx;
  box-shadow: 0 8rpx 32rpx rgba(255, 140, 66, 0.25);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.stat-value {
  font-size: 40rpx;
  font-weight: 700;
  color: #FFFFFF;
  line-height: 1;
}

.stat-label {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.85);
}

.stat-divider {
  width: 2rpx;
  height: 60rpx;
  background: rgba(255, 255, 255, 0.3);
}

/* ===================================
   通用区块样式
   =================================== */

.section {
  padding: 0 24rpx 40rpx;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24rpx;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.section-icon-wrapper {
  width: 64rpx;
  height: 64rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  
  &.urgent-bg {
    background: linear-gradient(135deg, #FFE5D9 0%, #FFDCC5 100%);
  }
  
  &.community-bg {
    background: linear-gradient(135deg, #E0F2FE 0%, #BAE6FD 100%);
  }
}

.section-icon {
  font-size: 36rpx;
}

.header-text {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #57534E;
  line-height: 1.2;
}

.section-subtitle {
  font-size: 22rpx;
  color: #A8A29E;
}

.section-more {
  display: flex;
  align-items: center;
  gap: 4rpx;
  padding: 12rpx 20rpx;
  background: #FFFFFF;
  border-radius: 40rpx;
  border: 1rpx solid #F5F5F4;
}

.more-text {
  font-size: 24rpx;
  color: #78716C;
}

.more-arrow {
  font-size: 28rpx;
  color: #78716C;
  font-weight: 300;
}

/* ===================================
   救助任务列表
   =================================== */

.rescue-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
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

.rescue-image {
  width: 100%;
  height: 340rpx;
  background: linear-gradient(135deg, #FEF7F0 0%, #FFF5EB 100%);
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
  backdrop-filter: blur(10rpx);
  box-shadow: 0 4rpx 16rpx rgba(239, 68, 68, 0.3);
}

.badge-pulse {
  width: 12rpx;
  height: 12rpx;
  background: #FFFFFF;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.badge-text {
  font-size: 24rpx;
  font-weight: 600;
  color: #FFFFFF;
}

.rescue-content {
  padding: 28rpx;
}

.rescue-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #57534E;
  line-height: 1.4;
  margin-bottom: 16rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.rescue-meta {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  margin-bottom: 20rpx;
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

.rescue-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 20rpx;
  border-top: 1rpx solid #F5F5F4;
}

.status-badge {
  padding: 8rpx 20rpx;
  border-radius: 40rpx;
  font-size: 22rpx;
  font-weight: 500;
  
  &.pending {
    background: linear-gradient(135deg, #FFE5D9 0%, #FFDCC5 100%);
    color: #FF8C42;
  }
  
  &.processing {
    background: linear-gradient(135deg, #D1FAE5 0%, #A7F3D0 100%);
    color: #059669;
  }
  
  &.completed {
    background: linear-gradient(135deg, #DBEAFE 0%, #BFDBFE 100%);
    color: #2563EB;
  }
}

.rescue-action {
  display: flex;
  align-items: center;
  gap: 4rpx;
}

.action-text {
  font-size: 24rpx;
  color: #FF8C42;
  font-weight: 500;
}

.action-arrow {
  font-size: 28rpx;
  color: #FF8C42;
}

/* ===================================
   社区动态列表
   =================================== */

.post-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.post-card {
  padding: 28rpx;
  background: linear-gradient(to bottom right, #FFFFFF 0%, #FEF7F0 100%);
  border-radius: 32rpx;
  border: 1rpx solid #F5F5F4;
  box-shadow: 0 4rpx 20rpx rgba(255, 140, 66, 0.08);
  transition: all 0.3s ease;
  
  &:active {
    transform: translateY(-4rpx);
    box-shadow: 0 8rpx 32rpx rgba(255, 140, 66, 0.15);
  }
}

.post-header {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 20rpx;
}

.user-avatar {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  border: 3rpx solid #F5F5F4;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.user-nickname {
  font-size: 28rpx;
  font-weight: 600;
  color: #57534E;
}

.post-time {
  font-size: 22rpx;
  color: #A8A29E;
}

.post-content {
  font-size: 28rpx;
  color: #57534E;
  line-height: 1.7;
  margin-bottom: 16rpx;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-images {
  position: relative;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12rpx;
  margin-bottom: 16rpx;
}

.post-image {
  width: 100%;
  height: 180rpx;
  border-radius: 16rpx;
  background: #FEF7F0;
  
  &.single {
    grid-column: 1 / -1;
    height: 400rpx;
  }
}

.more-images {
  position: absolute;
  bottom: 12rpx;
  right: 12rpx;
  width: 180rpx;
  height: 180rpx;
  border-radius: 16rpx;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(4rpx);
}

.more-text {
  font-size: 32rpx;
  font-weight: 600;
  color: #FFFFFF;
}

.post-actions {
  display: flex;
  align-items: center;
  gap: 40rpx;
  padding-top: 16rpx;
  border-top: 1rpx solid #F5F5F4;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.action-icon {
  font-size: 32rpx;
}

.action-count {
  font-size: 24rpx;
  color: #78716C;
}

/* ===================================
   底部间距
   =================================== */

.page-footer {
  height: 40rpx;
}
</style>
