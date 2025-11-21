<template>
  <view class="page">
    <!-- 顶部发布入口 -->
    <view class="publish-section">
      <view class="publish-card" @click="handlePublish">
        <image 
          :src="userInfo?.avatar || defaultAvatar" 
          mode="aspectFill" 
          class="user-avatar"
          @error="handleAvatarError"
        ></image>
        <view class="publish-input">
          <text class="publish-placeholder">分享你的爱宠故事...</text>
        </view>
        <view class="publish-btn">
          <text class="publish-icon">📝</text>
        </view>
      </view>
    </view>

    <!-- 分类标签 -->
    <scroll-view scroll-x class="category-scroll" :show-scrollbar="false">
      <view class="category-list">
        <view 
          v-for="(item, index) in categories" 
          :key="index"
          class="category-item"
          :class="{ active: currentCategory === item.value }"
          @click="currentCategory = item.value"
        >
          <text class="category-icon">{{ item.icon }}</text>
          <text class="category-text">{{ item.label }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 帖子列表 - Petfinder风格 -->
    <view class="posts-container">
      <view 
        v-for="(item, index) in postList" 
        :key="item.id"
        class="post-card"
        :style="{ animationDelay: `${index * 0.1}s` }"
        @click="handleDetail(item)"
      >
        <!-- 用户信息 -->
        <view class="post-header">
          <image 
            :src="item.userAvatar" 
            mode="aspectFill" 
            class="avatar"
            @error="handleImageError($event, 'avatar', item.userId)"
            @click.stop="handleUserProfile(item.userId)"
          ></image>
          <view class="user-info">
            <text class="nickname">{{ item.userNickname || '匿名用户' }}</text>
            <text class="time">{{ formatTime(item.createTime) }}</text>
          </view>
          <view class="more-btn" @click.stop="handleMore(item)">
            <text class="more-icon">⋯</text>
          </view>
        </view>

        <!-- 帖子内容 -->
        <view class="post-content" v-if="item.content">
          <text class="content-text">{{ item.content }}</text>
        </view>

        <!-- 图片网格 -->
        <view v-if="item.images && item.images.length > 0" class="image-grid" :class="getImageGridClass(item.images.length)">
          <image 
            v-for="(img, imgIndex) in item.images" 
            :key="imgIndex"
            :src="img" 
            mode="aspectFill" 
            class="post-image"
            @error="handleImageError($event, 'image', imgIndex)"
            @click.stop="previewImage(item.images, imgIndex)"
          ></image>
        </view>

        <!-- 互动区域 -->
        <view class="post-actions">
          <view class="action-item" @click.stop="handleLike(item)">
            <text class="action-icon" :class="{ liked: item.isLiked }">
              {{ item.isLiked ? '❤️' : '🤍' }}
            </text>
            <text class="action-text" :class="{ active: item.isLiked }">
              {{ item.likeCount > 0 ? item.likeCount : '点赞' }}
            </text>
          </view>

          <view class="action-item" @click.stop="handleComment(item)">
            <text class="action-icon">💬</text>
            <text class="action-text">
              {{ item.commentCount > 0 ? item.commentCount : '评论' }}
            </text>
          </view>

          <view class="action-item" @click.stop="handleShare(item)">
            <text class="action-icon">📤</text>
            <text class="action-text">分享</text>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-if="postList.length === 0 && !loading" class="empty-state">
        <text class="empty-icon">💬</text>
        <text class="empty-text">暂无社区动态</text>
        <text class="empty-desc">快来发布第一条动态吧</text>
      </view>

      <!-- 加载更多 -->
      <view v-if="hasMore && postList.length > 0" class="load-more" @click="loadMore">
        <text class="load-more-text">{{ loading ? '加载中...' : '加载更多' }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { communityApi } from '@/utils/api'

const userInfo = ref(null)
const currentCategory = ref('all')
const loading = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)
const postList = ref([])

const defaultAvatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=default'

const categories = ref([
  { label: '全部', value: 'all', icon: '📋' },
  { label: '日常分享', value: 'DAILY', icon: '🐾' },
  { label: '知识分享', value: 'KNOWLEDGE', icon: '📚' },
  { label: '求助', value: 'HELP', icon: '🆘' },
  { label: '故事', value: 'STORY', icon: '📖' }
])

onMounted(() => {
  loadUserInfo()
  loadPosts()
})

watch(currentCategory, () => {
  loadPosts(true)
})

const loadUserInfo = () => {
  const info = uni.getStorageSync('userInfo')
  if (info) {
    userInfo.value = info
  }
}

const loadPosts = async (reset = false) => {
  if (loading.value) return
  
  loading.value = true
  
  if (reset) {
    currentPage.value = 1
    postList.value = []
  }
  
  try {
    const params = {
      page: currentPage.value,
      size: 20
    }
    
    if (currentCategory.value !== 'all') {
      params.category = currentCategory.value
    }
    
    const res = await communityApi.getPostList(params)
    if (res.data && res.data.records) {
      const newList = res.data.records.map(item => ({
        ...item,
        userAvatar: item.userAvatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${item.userId || 'default'}`,
        isLiked: false,
        likeCount: item.likeCount || 0,
        commentCount: item.commentCount || 0,
        images: item.images ? (typeof item.images === 'string' ? JSON.parse(item.images) : item.images) : []
      }))
      
      if (reset) {
        postList.value = newList
      } else {
        postList.value = [...postList.value, ...newList]
      }
      
      hasMore.value = res.data.records.length === 20
      currentPage.value++
    }
  } catch (error) {
    console.error('加载社区动态失败', error)
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
    loadPosts()
  }
}

const handleAvatarError = () => {
  if (userInfo.value) {
    userInfo.value.avatar = defaultAvatar
  }
}

const handleImageError = (e, type, id) => {
  console.log('图片加载失败', type, id)
  if (type === 'avatar') {
    const item = postList.value.find(p => p.userId === id)
    if (item) item.userAvatar = defaultAvatar
  }
}

const handlePublish = () => {
  if (!userInfo.value) {
    uni.navigateTo({
      url: '/pages/login/index'
    })
    return
  }
  uni.navigateTo({
    url: '/pages/community/publish'
  })
}

const handleUserProfile = (userId) => {
  uni.navigateTo({
    url: `/pages/user/profile?userId=${userId}`
  })
}

const handleMore = (item) => {
  uni.showActionSheet({
    itemList: ['举报', '不感兴趣'],
    success: (res) => {
      console.log('选择了：' + res.tapIndex)
    }
  })
}

const handleLike = async (item) => {
  if (!userInfo.value) {
    uni.navigateTo({
      url: '/pages/login/index'
    })
    return
  }
  
  try {
    const res = await communityApi.toggleLike('POST', item.id)
    item.isLiked = res.data.liked
    if (item.isLiked) {
      item.likeCount = (item.likeCount || 0) + 1
    } else {
      item.likeCount = Math.max(0, (item.likeCount || 0) - 1)
    }
  } catch (error) {
    console.error('点赞失败', error)
    uni.showToast({
      title: '操作失败，请重试',
      icon: 'none'
    })
  }
}

const handleComment = (item) => {
  if (!userInfo.value) {
    uni.navigateTo({
      url: '/pages/login/index'
    })
    return
  }
  
  uni.navigateTo({
    url: `/pages/community/detail?id=${item.id}`
  })
}

const handleShare = (item) => {
  uni.showShareMenu({
    withShareTicket: true
  })
}

const handleDetail = (item) => {
  uni.navigateTo({
    url: `/pages/community/detail?id=${item.id}`
  })
}

const previewImage = (images, current) => {
  uni.previewImage({
    urls: images,
    current: current
  })
}

const getImageGridClass = (count) => {
  if (count === 1) return 'grid-1'
  if (count === 2) return 'grid-2'
  if (count === 4) return 'grid-4'
  return 'grid-3'
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
  background: #F5F5F5;
  padding-bottom: 40rpx;
}

/* 发布入口 */
.publish-section {
  padding: 24rpx 32rpx;
  background: #FFFFFF;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.publish-card {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 20rpx;
  background: #F5F5F5;
  border-radius: 24rpx;
  transition: all 0.3s ease;
  
  &:active {
    background: #EEEEEE;
    transform: scale(0.98);
  }
}

.user-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  border: 2rpx solid #FFFFFF;
  background: #F5F5F5;
  flex-shrink: 0;
}

.publish-input {
  flex: 1;
  height: 80rpx;
  display: flex;
  align-items: center;
  padding: 0 24rpx;
}

.publish-placeholder {
  font-size: 28rpx;
  color: #999;
}

.publish-btn {
  width: 80rpx;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #FF5A5F;
  border-radius: 50%;
  box-shadow: 0 4rpx 12rpx rgba(255, 90, 95, 0.3);
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.95);
    background: #E04A4F;
  }
}

.publish-icon {
  font-size: 40rpx;
}

/* 分类滚动 */
.category-scroll {
  white-space: nowrap;
  background: #FFFFFF;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #EEEEEE;
}

.category-list {
  display: inline-flex;
  gap: 16rpx;
  padding: 0 32rpx;
}

.category-item {
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
    
    .category-text {
      color: #FFFFFF;
      font-weight: 600;
    }
  }
  
  &:active {
    transform: scale(0.95);
  }
}

.category-icon {
  font-size: 28rpx;
}

.category-text {
  font-size: 26rpx;
  color: #666;
  white-space: nowrap;
}

/* 帖子列表 */
.posts-container {
  padding: 24rpx 32rpx;
}

.post-card {
  margin-bottom: 32rpx;
  padding: 32rpx;
  background: #FFFFFF;
  border-radius: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  animation: fadeInUp 0.6s ease-out both;
  
  &:active {
    transform: translateY(-2rpx);
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

.post-header {
  display: flex;
  align-items: center;
  margin-bottom: 24rpx;
}

.avatar {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  border: 2rpx solid #F0F0F0;
  background: #F5F5F5;
  flex-shrink: 0;
}

.user-info {
  flex: 1;
  margin-left: 16rpx;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  min-width: 0;
}

.nickname {
  font-size: 30rpx;
  font-weight: 500;
  color: #2C2C2C;
}

.time {
  font-size: 24rpx;
  color: #999;
}

.more-btn {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border-radius: 50%;
  transition: all 0.3s ease;
  
  &:active {
    background: #F5F5F5;
  }
}

.more-icon {
  font-size: 40rpx;
  color: #999;
  line-height: 1;
}

/* 帖子内容 */
.post-content {
  margin-bottom: 24rpx;
}

.content-text {
  font-size: 30rpx;
  color: #2C2C2C;
  line-height: 1.7;
  word-break: break-all;
  letter-spacing: 0.01em;
}

/* 图片网格 */
.image-grid {
  display: grid;
  gap: 12rpx;
  margin-bottom: 24rpx;
  border-radius: 16rpx;
  overflow: hidden;
  
  &.grid-1 {
    grid-template-columns: 1fr;
    
    .post-image {
      height: 500rpx;
    }
  }
  
  &.grid-2 {
    grid-template-columns: repeat(2, 1fr);
    
    .post-image {
      height: 320rpx;
    }
  }
  
  &.grid-3 {
    grid-template-columns: repeat(3, 1fr);
    
    .post-image {
      height: 240rpx;
    }
  }
  
  &.grid-4 {
    grid-template-columns: repeat(2, 1fr);
    
    .post-image {
      height: 320rpx;
    }
  }
}

.post-image {
  width: 100%;
  background: #F5F5F5;
  transition: transform 0.3s ease;
  
  &:active {
    transform: scale(1.05);
  }
}

/* 互动区域 */
.post-actions {
  display: flex;
  align-items: center;
  gap: 32rpx;
  padding-top: 24rpx;
  border-top: 1rpx solid #F0F0F0;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 12rpx 24rpx;
  background: #F5F5F5;
  border-radius: 50rpx;
  transition: all 0.3s ease;
  
  &:active {
    background: #EEEEEE;
  }
}

.action-icon {
  font-size: 36rpx;
  transition: all 0.3s ease;
  
  &.liked {
    animation: like 0.5s ease;
  }
}

@keyframes like {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.3);
  }
}

.action-text {
  font-size: 26rpx;
  color: #666;
  
  &.active {
    color: #FF5A5F;
    font-weight: 500;
  }
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
</style>
