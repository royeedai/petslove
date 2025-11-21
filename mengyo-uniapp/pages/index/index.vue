<template>
  <view class="page">
    <!-- 轮播图 - 更温馨的设计 -->
    <view class="banner-wrapper">
      <swiper 
        :indicator-dots="true" 
        :autoplay="true" 
        :interval="4000" 
        :duration="500"
        :circular="true"
        indicator-color="rgba(255, 255, 255, 0.4)"
        indicator-active-color="#FFFFFF"
        class="banner-swiper"
      >
        <swiper-item v-for="(item, index) in banners" :key="index">
          <view class="banner-item">
            <image 
              :src="item.imageUrl" 
              mode="aspectFill" 
              class="banner-image"
              @error="handleImageError($event, 'banner', index)"
            ></image>
            <view class="banner-overlay"></view>
          </view>
        </swiper-item>
      </swiper>
    </view>

    <!-- 功能入口 - Pinterest 风格卡片 -->
    <view class="features-section">
      <view class="features-grid">
        <view class="feature-card" @click="navigateTo('/pages/rescue/list')">
          <view class="feature-icon-wrapper rescue">
            <text class="feature-icon">🆘</text>
          </view>
          <text class="feature-text">救助任务</text>
        </view>
        
        <view class="feature-card" @click="navigateTo('/pages/animal/list')">
          <view class="feature-icon-wrapper animal">
            <text class="feature-icon">🐾</text>
          </view>
          <text class="feature-text">流浪动物</text>
        </view>
        
        <view class="feature-card" @click="navigateTo('/pages/donation/index')">
          <view class="feature-icon-wrapper donation">
            <text class="feature-icon">❤️</text>
          </view>
          <text class="feature-text">爱心捐赠</text>
        </view>
        
        <view class="feature-card" @click="navigateTo('/pages/adoption/index')">
          <view class="feature-icon-wrapper adoption">
            <text class="feature-icon">🏠</text>
          </view>
          <text class="feature-text">领养申请</text>
        </view>
      </view>
    </view>

    <!-- 最新救助任务 - Pinterest 卡片风格 -->
    <view class="section">
      <view class="section-header">
        <view class="header-left">
          <text class="section-title">最新救助</text>
          <text class="section-subtitle">用爱守护每一个生命</text>
        </view>
        <view class="section-more" @click="navigateTo('/pages/rescue/list')">
          <text class="more-text">查看全部</text>
          <text class="more-arrow">›</text>
        </view>
      </view>
      
      <view class="rescue-list" v-if="rescueList.length > 0">
        <view 
          class="rescue-card" 
          v-for="item in rescueList" 
          :key="item.id"
          @click="navigateTo(`/pages/rescue/detail?id=${item.id}`)"
        >
          <view class="rescue-image-wrapper">
            <image 
              :src="item.cover" 
              mode="aspectFill" 
              class="rescue-image"
              @error="handleImageError($event, 'rescue', item.id)"
            ></image>
            <view v-if="item.urgencyLevel === 'urgent'" class="urgent-badge">
              <text class="urgent-icon">🚨</text>
              <text class="urgent-text">紧急</text>
            </view>
          </view>
          <view class="rescue-content">
            <text class="rescue-title">{{ item.title }}</text>
            <view class="rescue-meta">
              <view class="meta-item">
                <text class="meta-icon">📍</text>
                <text class="meta-text">{{ item.address || '位置待确认' }}</text>
              </view>
            </view>
            <view class="rescue-desc" v-if="item.description">
              {{ item.description }}
            </view>
          </view>
        </view>
      </view>
      
      <view v-else class="empty-state">
        <text class="empty-icon">🔍</text>
        <text class="empty-text">暂无救助任务</text>
      </view>
    </view>

    <!-- 社区动态 - Pinterest 风格 -->
    <view class="section">
      <view class="section-header">
        <view class="header-left">
          <text class="section-title">社区动态</text>
          <text class="section-subtitle">分享温暖，传递爱心</text>
        </view>
        <view class="section-more" @click="navigateTo('/pages/community/list')">
          <text class="more-text">查看全部</text>
          <text class="more-arrow">›</text>
        </view>
      </view>
      
      <view class="post-list" v-if="postList.length > 0">
        <view 
          class="post-card" 
          v-for="item in postList" 
          :key="item.id"
          @click="navigateTo(`/pages/community/detail?id=${item.id}`)"
        >
          <view class="post-header">
            <image 
              :src="item.userAvatar" 
              mode="aspectFill" 
              class="user-avatar"
              @error="handleImageError($event, 'avatar', item.userId)"
            ></image>
            <view class="user-info">
              <text class="user-nickname">{{ item.userNickname || '匿名用户' }}</text>
              <text class="post-time">{{ formatTime(item.createTime) }}</text>
            </view>
          </view>
          <text class="post-content">{{ item.content }}</text>
          <view class="post-actions">
            <view class="action-item">
              <text class="action-icon">👍</text>
              <text class="action-text">{{ item.likeCount || 0 }}</text>
            </view>
            <view class="action-item">
              <text class="action-icon">💬</text>
              <text class="action-text">{{ item.commentCount || 0 }}</text>
            </view>
          </view>
        </view>
      </view>
      
      <view v-else class="empty-state">
        <text class="empty-icon">📝</text>
        <text class="empty-text">暂无社区动态</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { rescueApi, communityApi } from '@/utils/api'

const banners = ref([
  {
    imageUrl: 'https://images.unsplash.com/photo-1450778869180-41d0601e046e?w=800&q=80'
  },
  {
    imageUrl: 'https://images.unsplash.com/photo-1415369629372-26f2fe60c467?w=800&q=80'
  },
  {
    imageUrl: 'https://images.unsplash.com/photo-1425082661705-1834bfd09dca?w=800&q=80'
  }
])
const rescueList = ref([])
const postList = ref([])

// 默认图片
const defaultImages = {
  banner: 'https://images.unsplash.com/photo-1450778869180-41d0601e046e?w=800&q=80',
  rescue: 'https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?w=400&q=80',
  avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=default'
}

onMounted(() => {
  loadData()
})

const loadData = async () => {
  try {
    // 加载最新救助任务
    const rescueRes = await rescueApi.getTaskList({ page: 1, size: 3 })
    if (rescueRes.data && rescueRes.data.records) {
      rescueList.value = rescueRes.data.records.map(item => ({
        ...item,
        cover: item.cover || defaultImages.rescue
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
        userAvatar: item.userAvatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${item.userId || 'default'}`,
        likeCount: item.likeCount || 0,
        commentCount: item.commentCount || 0
      }))
    }
  } catch (error) {
    console.error('加载社区动态失败', error)
  }
}

const handleImageError = (e, type, id) => {
  console.log('图片加载失败', type, id)
  // 设置默认图片
  if (type === 'banner') {
    banners.value[id].imageUrl = defaultImages.banner
  } else if (type === 'rescue') {
    const item = rescueList.value.find(r => r.id === id)
    if (item) item.cover = defaultImages.rescue
  } else if (type === 'avatar') {
    const item = postList.value.find(p => p.userId === id)
    if (item) item.userAvatar = defaultImages.avatar
  }
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

const navigateTo = (url) => {
  uni.navigateTo({ url })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: var(--bg-page);
  padding-bottom: var(--spacing-lg);
}

/* 轮播图 - 更温馨的设计 */
.banner-wrapper {
  margin: var(--spacing-md) var(--spacing-lg) var(--spacing-xl);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-card);
}

.banner-swiper {
  width: 100%;
  height: 400rpx;
}

.banner-item {
  position: relative;
  width: 100%;
  height: 100%;
}

.banner-image {
  width: 100%;
  height: 100%;
  background: var(--bg-warm);
}

.banner-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 120rpx;
  background: linear-gradient(180deg, transparent 0%, rgba(0, 0, 0, 0.15) 100%);
}

/* 功能入口 - Pinterest 风格卡片 */
.features-section {
  margin: 0 var(--spacing-lg) var(--spacing-xl);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-md);
}

.feature-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-lg) var(--spacing-sm);
  background: var(--bg-white);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  border: 1rpx solid var(--border-color);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:active {
    transform: translateY(-4rpx);
    box-shadow: var(--shadow-md);
  }
}

.feature-icon-wrapper {
  width: 100rpx;
  height: 100rpx;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-sm);
  transition: all 0.3s ease;
  
  &.rescue {
    background: linear-gradient(135deg, #FF6B6B 0%, #FF8787 100%);
  }
  
  &.animal {
    background: linear-gradient(135deg, #FF5A5F 0%, #FF7A7F 100%);
  }
  
  &.donation {
    background: linear-gradient(135deg, #FFB84D 0%, #FFC970 100%);
  }
  
  &.adoption {
    background: linear-gradient(135deg, #00A699 0%, #00C4B3 100%);
  }
}

.feature-icon {
  font-size: 52rpx;
}

.feature-text {
  font-size: 26rpx;
  color: var(--text-primary);
  font-weight: 500;
  letter-spacing: 0.01em;
}

/* 通用区块样式 - 增加留白 */
.section {
  margin: 0 var(--spacing-lg) var(--spacing-xl);
  background: var(--bg-white);
  border-radius: var(--radius-xl);
  padding: var(--spacing-xl);
  box-shadow: var(--shadow-card);
  border: 1rpx solid var(--border-color);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-lg);
  border-bottom: 1rpx solid var(--divider-color);
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
  flex: 1;
}

.section-title {
  font-size: 36rpx;
  font-weight: 600;
  color: var(--text-primary);
  letter-spacing: -0.01em;
}

.section-subtitle {
  font-size: 24rpx;
  color: var(--text-secondary);
  margin-top: 4rpx;
}

.section-more {
  display: flex;
  align-items: center;
  gap: 4rpx;
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-full);
  transition: all 0.3s ease;
  
  &:active {
    background: var(--bg-gray);
  }
}

.more-text {
  font-size: 26rpx;
  color: var(--primary-color);
  font-weight: 500;
}

.more-arrow {
  font-size: 28rpx;
  color: var(--primary-color);
  font-weight: 300;
}

/* 救助列表 - Pinterest 卡片风格 */
.rescue-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.rescue-card {
  background: var(--bg-white);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-card);
  border: 1rpx solid var(--border-color);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:active {
    transform: translateY(-4rpx);
    box-shadow: var(--shadow-md);
  }
}

.rescue-image-wrapper {
  position: relative;
  width: 100%;
  height: 360rpx;
  overflow: hidden;
}

.rescue-image {
  width: 100%;
  height: 100%;
  background: var(--bg-warm);
  transition: transform 0.3s ease;
}

.rescue-card:active .rescue-image {
  transform: scale(1.05);
}

.urgent-badge {
  position: absolute;
  top: var(--spacing-md);
  right: var(--spacing-md);
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 8rpx 20rpx;
  background: rgba(255, 90, 95, 0.95);
  backdrop-filter: blur(10rpx);
  color: #fff;
  border-radius: var(--radius-full);
  font-size: 22rpx;
  font-weight: 500;
  box-shadow: var(--shadow-sm);
}

.urgent-icon {
  font-size: 24rpx;
}

.rescue-content {
  padding: var(--spacing-lg);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.rescue-title {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.5;
  letter-spacing: -0.01em;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.rescue-meta {
  display: flex;
  align-items: center;
  margin-top: var(--spacing-xs);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  flex: 1;
  min-width: 0;
}

.meta-icon {
  font-size: 26rpx;
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
  font-size: 26rpx;
  color: var(--text-secondary);
  line-height: 1.7;
  margin-top: var(--spacing-sm);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 社区动态 - Pinterest 风格 */
.post-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.post-card {
  padding: var(--spacing-lg);
  background: var(--bg-white);
  border-radius: var(--radius-lg);
  border: 1rpx solid var(--border-color);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:active {
    transform: translateY(-2rpx);
    box-shadow: var(--shadow-md);
  }
}

.post-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.user-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  border: 2rpx solid var(--border-color);
  background: var(--bg-warm);
  flex-shrink: 0;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  flex: 1;
  min-width: 0;
}

.user-nickname {
  font-size: 30rpx;
  font-weight: 500;
  color: var(--text-primary);
}

.post-time {
  font-size: 24rpx;
  color: var(--text-light);
}

.post-content {
  font-size: 30rpx;
  color: var(--text-primary);
  line-height: 1.7;
  margin-bottom: var(--spacing-lg);
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  letter-spacing: 0.01em;
}

.post-actions {
  display: flex;
  gap: var(--spacing-xl);
  padding-top: var(--spacing-md);
  border-top: 1rpx solid var(--divider-color);
}

.action-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-full);
  transition: all 0.3s ease;
  
  &:active {
    background: var(--bg-gray);
  }
}

.action-icon {
  font-size: 32rpx;
}

.action-text {
  font-size: 26rpx;
  color: var(--text-secondary);
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
  font-size: 100rpx;
  opacity: 0.25;
}

.empty-text {
  font-size: 28rpx;
  color: var(--text-light);
}
</style>
