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
        indicator-active-color="#FF8C42"
        class="banner-swiper"
      >
        <swiper-item v-for="(item, index) in banners" :key="index">
          <view class="banner-item">
            <image :src="item.imageUrl" mode="aspectFill" class="banner-image"></image>
            <view class="banner-mask"></view>
          </view>
        </swiper-item>
      </swiper>
    </view>

    <!-- 功能入口 -->
    <view class="features-card">
      <view class="features-grid">
        <view class="feature-item" @click="navigateTo('/pages/rescue/list')">
          <view class="feature-icon-wrapper" style="background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);">
            <text class="feature-icon">🆘</text>
          </view>
          <text class="feature-text">救助任务</text>
        </view>
        
        <view class="feature-item" @click="navigateTo('/pages/animal/list')">
          <view class="feature-icon-wrapper" style="background: linear-gradient(135deg, #36D1DC 0%, #5B86E5 100%);">
            <text class="feature-icon">🐾</text>
          </view>
          <text class="feature-text">流浪动物</text>
        </view>
        
        <view class="feature-item" @click="navigateTo('/pages/donation/index')">
          <view class="feature-icon-wrapper" style="background: linear-gradient(135deg, #F093FB 0%, #F5576C 100%);">
            <text class="feature-icon">❤️</text>
          </view>
          <text class="feature-text">爱心捐赠</text>
        </view>
        
        <view class="feature-item" @click="navigateTo('/pages/adoption/index')">
          <view class="feature-icon-wrapper" style="background: linear-gradient(135deg, #4FACFE 0%, #00F2FE 100%);">
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
          <view class="section-icon">🆘</view>
          <text class="section-title">最新救助</text>
        </view>
        <view class="section-more" @click="navigateTo('/pages/rescue/list')">
          <text class="more-text">更多</text>
          <text class="more-arrow">›</text>
        </view>
      </view>
      
      <view class="rescue-list" v-if="rescueList.length > 0">
        <view class="rescue-card" v-for="item in rescueList" :key="item.id">
          <image :src="item.cover" mode="aspectFill" class="rescue-image"></image>
          <view class="rescue-content">
            <text class="rescue-title">{{ item.title }}</text>
            <view class="rescue-meta">
              <view class="meta-item">
                <text class="meta-icon">📍</text>
                <text class="meta-text">{{ item.address }}</text>
              </view>
              <view class="rescue-status urgent">
                紧急
              </view>
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
          <view class="section-icon">💬</view>
          <text class="section-title">社区动态</text>
        </view>
        <view class="section-more" @click="navigateTo('/pages/community/list')">
          <text class="more-text">更多</text>
          <text class="more-arrow">›</text>
        </view>
      </view>
      
      <view class="post-list" v-if="postList.length > 0">
        <view class="post-card" v-for="item in postList" :key="item.id">
          <view class="post-header">
            <image :src="item.userAvatar" mode="aspectFill" class="user-avatar"></image>
            <view class="user-info">
              <text class="user-nickname">{{ item.userNickname }}</text>
              <text class="post-time">刚刚</text>
            </view>
          </view>
          <text class="post-content">{{ item.content }}</text>
          <view class="post-actions">
            <view class="action-item">
              <text class="action-icon">👍</text>
              <text class="action-text">点赞</text>
            </view>
            <view class="action-item">
              <text class="action-icon">💬</text>
              <text class="action-text">评论</text>
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
        cover: item.cover || 'https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?w=400&q=80'
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
        userAvatar: item.userAvatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=' + item.userId
      }))
    }
  } catch (error) {
    console.error('加载社区动态失败', error)
  }
}

const navigateTo = (url) => {
  uni.navigateTo({ url })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: linear-gradient(180deg, #FFF5EE 0%, #F8F9FA 100%);
  padding-bottom: 20rpx;
}

/* 轮播图 */
.banner-wrapper {
  margin-bottom: 20rpx;
  overflow: hidden;
}

.banner-swiper {
  width: 100%;
  height: 380rpx;
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

.banner-mask {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 160rpx;
  background: linear-gradient(180deg, transparent 0%, rgba(0, 0, 0, 0.3) 100%);
}

/* 功能入口卡片 */
.features-card {
  margin: 0 24rpx 20rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx 20rpx;
  box-shadow: 0 8rpx 32rpx rgba(255, 140, 66, 0.08);
}

.features-grid {
  display: flex;
  justify-content: space-around;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
}

.feature-icon-wrapper {
  width: 100rpx;
  height: 100rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(255, 140, 66, 0.15);
  transition: all 0.3s ease;
}

.feature-icon {
  font-size: 48rpx;
}

.feature-text {
  font-size: 26rpx;
  color: #2C3E50;
  font-weight: 500;
}

/* 通用区块样式 */
.section {
  margin: 0 24rpx 20rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.04);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.section-icon {
  font-size: 36rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #2C3E50;
}

.section-more {
  display: flex;
  align-items: center;
  gap: 4rpx;
}

.more-text {
  font-size: 26rpx;
  color: #95A5A6;
}

.more-arrow {
  font-size: 32rpx;
  color: #95A5A6;
  font-weight: 300;
}

/* 救助列表 */
.rescue-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.rescue-card {
  display: flex;
  gap: 20rpx;
  padding: 20rpx;
  background: #F8F9FA;
  border-radius: 16rpx;
  transition: all 0.3s ease;
}

.rescue-image {
  width: 180rpx;
  height: 180rpx;
  border-radius: 12rpx;
  flex-shrink: 0;
}

.rescue-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.rescue-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #2C3E50;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.rescue-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
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
  color: #7F8C8D;
}

.rescue-status {
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  font-size: 22rpx;
  font-weight: 500;
  
  &.urgent {
    background: linear-gradient(135deg, #FFE5D9 0%, #FFDCC5 100%);
    color: #FF6B35;
  }
}

/* 社区动态 */
.post-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.post-card {
  padding: 24rpx;
  background: #F8F9FA;
  border-radius: 16rpx;
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
  border: 3rpx solid #fff;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.08);
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.user-nickname {
  font-size: 28rpx;
  font-weight: 500;
  color: #2C3E50;
}

.post-time {
  font-size: 22rpx;
  color: #95A5A6;
}

.post-content {
  font-size: 28rpx;
  color: #34495E;
  line-height: 1.6;
  margin-bottom: 16rpx;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-actions {
  display: flex;
  gap: 40rpx;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 8rpx 16rpx;
  background: #fff;
  border-radius: 20rpx;
}

.action-icon {
  font-size: 28rpx;
}

.action-text {
  font-size: 24rpx;
  color: #7F8C8D;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx 0;
  gap: 20rpx;
}

.empty-icon {
  font-size: 80rpx;
  opacity: 0.4;
}

.empty-text {
  font-size: 26rpx;
  color: #95A5A6;
}
</style>
