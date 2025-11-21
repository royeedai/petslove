<template>
  <view class="page">
    <!-- 轮播图 -->
    <view class="banner-wrapper">
      <swiper 
        :indicator-dots="true" 
        :autoplay="true" 
        :interval="4000" 
        :duration="500"
        :circular="true"
        indicator-color="rgba(255, 255, 255, 0.5)"
        indicator-active-color="#0066CC"
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
            <view class="banner-mask"></view>
          </view>
        </swiper-item>
      </swiper>
    </view>

    <!-- 功能入口 -->
    <view class="features-card">
      <view class="features-grid">
        <view class="feature-item" @click="navigateTo('/pages/rescue/list')">
          <view class="feature-icon-wrapper rescue">
            <text class="feature-icon">🆘</text>
          </view>
          <text class="feature-text">救助任务</text>
        </view>
        
        <view class="feature-item" @click="navigateTo('/pages/animal/list')">
          <view class="feature-icon-wrapper animal">
            <text class="feature-icon">🐾</text>
          </view>
          <text class="feature-text">流浪动物</text>
        </view>
        
        <view class="feature-item" @click="navigateTo('/pages/donation/index')">
          <view class="feature-icon-wrapper donation">
            <text class="feature-icon">❤️</text>
          </view>
          <text class="feature-text">爱心捐赠</text>
        </view>
        
        <view class="feature-item" @click="navigateTo('/pages/adoption/index')">
          <view class="feature-icon-wrapper adoption">
            <text class="feature-icon">🏠</text>
          </view>
          <text class="feature-text">领养申请</text>
        </view>
      </view>
    </view>

    <!-- 最新救助任务 -->
    <view class="section">
      <view class="section-header">
        <view class="header-left">
          <text class="section-title">最新救助</text>
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
              紧急
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

    <!-- 社区动态 -->
    <view class="section">
      <view class="section-header">
        <view class="header-left">
          <text class="section-title">社区动态</text>
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
  padding-bottom: 20rpx;
}

/* 轮播图 */
.banner-wrapper {
  margin-bottom: 24rpx;
  overflow: hidden;
}

.banner-swiper {
  width: 100%;
  height: 360rpx;
}

.banner-item {
  position: relative;
  width: 100%;
  height: 100%;
}

.banner-image {
  width: 100%;
  height: 100%;
  background: var(--bg-gray);
}

.banner-mask {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 120rpx;
  background: linear-gradient(180deg, transparent 0%, rgba(0, 0, 0, 0.2) 100%);
}

/* 功能入口卡片 */
.features-card {
  margin: 0 24rpx 24rpx;
  background: var(--bg-white);
  border-radius: var(--radius-lg);
  padding: 32rpx 24rpx;
  box-shadow: var(--shadow-sm);
  border: 1rpx solid var(--border-color);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24rpx;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
}

.feature-icon-wrapper {
  width: 96rpx;
  height: 96rpx;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-sm);
  transition: all 0.3s ease;
  
  &.rescue {
    background: linear-gradient(135deg, #FF6B6B 0%, #EE5A6F 100%);
  }
  
  &.animal {
    background: linear-gradient(135deg, #0066CC 0%, #004C99 100%);
  }
  
  &.donation {
    background: linear-gradient(135deg, #FFB84D 0%, #FF9500 100%);
  }
  
  &.adoption {
    background: linear-gradient(135deg, #28A745 0%, #1E7E34 100%);
  }
  
  &:active {
    transform: scale(0.95);
  }
}

.feature-icon {
  font-size: 48rpx;
}

.feature-text {
  font-size: 24rpx;
  color: var(--text-primary);
  font-weight: 500;
}

/* 通用区块样式 */
.section {
  margin: 0 24rpx 24rpx;
  background: var(--bg-white);
  border-radius: var(--radius-lg);
  padding: 32rpx;
  box-shadow: var(--shadow-sm);
  border: 1rpx solid var(--border-color);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid var(--divider-color);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--text-primary);
}

.section-more {
  display: flex;
  align-items: center;
  gap: 4rpx;
}

.more-text {
  font-size: 26rpx;
  color: var(--primary-color);
}

.more-arrow {
  font-size: 28rpx;
  color: var(--primary-color);
  font-weight: 300;
}

/* 救助列表 */
.rescue-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.rescue-card {
  display: flex;
  gap: 20rpx;
  padding: 20rpx;
  background: var(--bg-gray);
  border-radius: var(--radius-md);
  transition: all 0.3s ease;
  border: 1rpx solid var(--border-color);
  
  &:active {
    background: #F0F0F0;
    transform: scale(0.98);
  }
}

.rescue-image-wrapper {
  position: relative;
  flex-shrink: 0;
}

.rescue-image {
  width: 200rpx;
  height: 200rpx;
  border-radius: var(--radius-md);
  background: var(--bg-gray);
}

.urgent-badge {
  position: absolute;
  top: 8rpx;
  right: 8rpx;
  padding: 6rpx 16rpx;
  background: var(--danger-color);
  color: #fff;
  border-radius: 20rpx;
  font-size: 20rpx;
  font-weight: 500;
}

.rescue-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0;
}

.rescue-title {
  font-size: 30rpx;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.4;
  margin-bottom: 12rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.rescue-meta {
  display: flex;
  align-items: center;
  margin-bottom: 12rpx;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6rpx;
  flex: 1;
  min-width: 0;
}

.meta-icon {
  font-size: 24rpx;
  flex-shrink: 0;
}

.meta-text {
  font-size: 24rpx;
  color: var(--text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.rescue-desc {
  font-size: 24rpx;
  color: var(--text-light);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 社区动态 */
.post-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.post-card {
  padding: 24rpx;
  background: var(--bg-gray);
  border-radius: var(--radius-md);
  border: 1rpx solid var(--border-color);
  transition: all 0.3s ease;
  
  &:active {
    background: #F0F0F0;
    transform: scale(0.98);
  }
}

.post-header {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 16rpx;
}

.user-avatar {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  border: 2rpx solid var(--border-color);
  background: var(--bg-gray);
  flex-shrink: 0;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
  flex: 1;
  min-width: 0;
}

.user-nickname {
  font-size: 28rpx;
  font-weight: 500;
  color: var(--text-primary);
}

.post-time {
  font-size: 22rpx;
  color: var(--text-light);
}

.post-content {
  font-size: 28rpx;
  color: var(--text-primary);
  line-height: 1.6;
  margin-bottom: 16rpx;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-actions {
  display: flex;
  gap: 32rpx;
  padding-top: 16rpx;
  border-top: 1rpx solid var(--divider-color);
}

.action-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.action-icon {
  font-size: 28rpx;
}

.action-text {
  font-size: 24rpx;
  color: var(--text-secondary);
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx 0;
  gap: 16rpx;
}

.empty-icon {
  font-size: 80rpx;
  opacity: 0.3;
}

.empty-text {
  font-size: 26rpx;
  color: var(--text-light);
}
</style>
