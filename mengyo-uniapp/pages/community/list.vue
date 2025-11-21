<template>
  <view class="page">
    <!-- 顶部发布入口 - 更温馨的设计 -->
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

    <!-- 分类标签 - 更圆润的设计 -->
    <scroll-view scroll-x class="category-scroll" :show-scrollbar="false">
      <view class="category-list">
        <view 
          v-for="(item, index) in categories" 
          :key="index"
          class="category-item"
          :class="{ active: currentCategory === item.value }"
          @click="currentCategory = item.value"
        >
          <text class="category-emoji">{{ item.emoji }}</text>
          <text class="category-text">{{ item.label }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 社区动态列表 - Pinterest 风格卡片 -->
    <view class="post-list">
      <view 
        v-for="item in postList" 
        :key="item.id" 
        class="post-card"
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

        <!-- 动态内容 -->
        <view class="post-content" v-if="item.content">
          <text class="content-text">{{ item.content }}</text>
        </view>

        <!-- 图片列表 - Pinterest 风格网格 -->
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

        <!-- 话题标签 -->
        <view v-if="item.tags && item.tags.length > 0" class="tags-wrapper">
          <view 
            v-for="(tag, tagIndex) in item.tags" 
            :key="tagIndex"
            class="tag-item"
            @click.stop="handleTag(tag)"
          >
            #{{ tag }}
          </view>
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

        <!-- 评论预览 -->
        <view v-if="item.hotComments && item.hotComments.length > 0" class="comments-preview">
          <view 
            v-for="(comment, commentIndex) in item.hotComments" 
            :key="commentIndex"
            class="comment-item"
          >
            <text class="comment-user">{{ comment.userNickname }}：</text>
            <text class="comment-content">{{ comment.content }}</text>
          </view>
          <view v-if="item.commentCount > 2" class="view-all-comments" @click.stop="handleComment(item)">
            查看全部 {{ item.commentCount }} 条评论 ›
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-if="postList.length === 0" class="empty-state">
        <text class="empty-icon">💬</text>
        <text class="empty-text">暂无社区动态</text>
        <text class="empty-desc">快来发布第一条动态吧</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { communityApi } from '@/utils/api'

const userInfo = ref(null)
const currentCategory = ref('all')

const defaultAvatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=default'

const categories = ref([
  { label: '全部', value: 'all', emoji: '📋' },
  { label: '救助故事', value: 'rescue', emoji: '🆘' },
  { label: '领养日记', value: 'adoption', emoji: '🏠' },
  { label: '萌宠日常', value: 'daily', emoji: '🐾' },
  { label: '求助', value: 'help', emoji: '❓' }
])

const postList = ref([])

onMounted(() => {
  loadUserInfo()
  loadData()
})

watch(currentCategory, () => {
  loadData()
})

const loadUserInfo = () => {
  const info = uni.getStorageSync('userInfo')
  if (info) {
    userInfo.value = info
  }
}

const loadData = async () => {
  try {
    const params = {
      page: 1,
      size: 20
    }
    
    if (currentCategory.value !== 'all') {
      params.category = currentCategory.value
    }
    
    const res = await communityApi.getPostList(params)
    if (res.data && res.data.records) {
      postList.value = res.data.records.map(item => ({
        ...item,
        userAvatar: item.userAvatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${item.userId || 'default'}`,
        isLiked: false,
        likeCount: item.likeCount || 0,
        commentCount: item.commentCount || 0
      }))
    }
  } catch (error) {
    console.error('加载社区动态失败', error)
    uni.showToast({
      title: '加载失败，请重试',
      icon: 'none'
    })
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
    const res = await communityApi.toggleLike('post', item.id)
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

const handleTag = (tag) => {
  console.log('点击标签：', tag)
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
  background: var(--bg-page);
  padding-bottom: var(--spacing-lg);
}

/* 发布入口 - 更温馨的设计 */
.publish-section {
  padding: var(--spacing-lg);
}

.publish-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  background: var(--bg-white);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-card);
  border: 1rpx solid var(--border-color);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:active {
    transform: translateY(-2rpx);
    box-shadow: var(--shadow-md);
  }
}

.user-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  border: 2rpx solid var(--border-color);
  background: var(--bg-warm);
  flex-shrink: 0;
}

.publish-input {
  flex: 1;
  height: 80rpx;
  display: flex;
  align-items: center;
  padding: 0 var(--spacing-lg);
  background: var(--bg-gray);
  border-radius: var(--radius-full);
  border: 1rpx solid var(--border-color);
}

.publish-placeholder {
  font-size: 28rpx;
  color: var(--text-light);
}

.publish-btn {
  width: 80rpx;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-color);
  border-radius: 50%;
  box-shadow: var(--shadow-sm);
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.95);
    background: var(--primary-dark);
  }
}

.publish-icon {
  font-size: 40rpx;
}

/* 分类滚动 - 更圆润的设计 */
.category-scroll {
  white-space: nowrap;
  background: var(--bg-white);
  padding: var(--spacing-lg) 0;
  margin-bottom: var(--spacing-md);
  border-bottom: 1rpx solid var(--border-color);
}

.category-list {
  display: inline-flex;
  gap: var(--spacing-md);
  padding: 0 var(--spacing-lg);
}

.category-item {
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
    
    .category-text {
      color: #fff;
      font-weight: 600;
    }
    
    .category-emoji {
      filter: brightness(1.1);
    }
  }
  
  &:active {
    transform: scale(0.95);
  }
}

.category-emoji {
  font-size: 32rpx;
}

.category-text {
  font-size: 26rpx;
  color: var(--text-secondary);
  white-space: nowrap;
}

/* 动态列表 - Pinterest 风格卡片 */
.post-list {
  padding: 0 var(--spacing-lg);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.post-card {
  background: var(--bg-white);
  border-radius: var(--radius-xl);
  padding: var(--spacing-xl);
  box-shadow: var(--shadow-card);
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
  margin-bottom: var(--spacing-lg);
}

.avatar {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  border: 2rpx solid var(--border-color);
  background: var(--bg-warm);
  flex-shrink: 0;
}

.user-info {
  flex: 1;
  margin-left: var(--spacing-md);
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  min-width: 0;
}

.nickname {
  font-size: 30rpx;
  font-weight: 500;
  color: var(--text-primary);
}

.time {
  font-size: 24rpx;
  color: var(--text-light);
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
    background: var(--bg-gray);
  }
}

.more-icon {
  font-size: 40rpx;
  color: var(--text-light);
  line-height: 1;
}

/* 动态内容 */
.post-content {
  margin-bottom: var(--spacing-lg);
}

.content-text {
  font-size: 30rpx;
  color: var(--text-primary);
  line-height: 1.7;
  word-break: break-all;
  letter-spacing: 0.01em;
}

/* 图片网格 - Pinterest 风格 */
.image-grid {
  display: grid;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-lg);
  border-radius: var(--radius-lg);
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
  background: var(--bg-warm);
  transition: transform 0.3s ease;
  
  &:active {
    transform: scale(1.05);
  }
}

/* 标签 */
.tags-wrapper {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-lg);
}

.tag-item {
  padding: 8rpx 20rpx;
  background: var(--primary-bg);
  border-radius: var(--radius-full);
  font-size: 26rpx;
  color: var(--primary-color);
  border: 1rpx solid rgba(255, 90, 95, 0.2);
  transition: all 0.3s ease;
  
  &:active {
    background: var(--primary-color);
    color: #fff;
  }
}

/* 互动区域 */
.post-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-xl);
  padding-top: var(--spacing-lg);
  border-top: 1rpx solid var(--divider-color);
}

.action-item {
  display: flex;
  align-items: center;
  gap: 10rpx;
  padding: var(--spacing-xs) var(--spacing-md);
  background: var(--bg-gray);
  border-radius: var(--radius-full);
  transition: all 0.3s ease;
  
  &:active {
    background: #E0E0E0;
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
  color: var(--text-secondary);
  
  &.active {
    color: var(--primary-color);
    font-weight: 500;
  }
}

/* 评论预览 */
.comments-preview {
  margin-top: var(--spacing-lg);
  padding-top: var(--spacing-lg);
  border-top: 1rpx solid var(--divider-color);
}

.comment-item {
  font-size: 28rpx;
  line-height: 1.7;
  margin-bottom: var(--spacing-sm);
}

.comment-user {
  color: var(--primary-color);
  font-weight: 500;
}

.comment-content {
  color: var(--text-primary);
}

.view-all-comments {
  font-size: 26rpx;
  color: var(--text-light);
  margin-top: var(--spacing-sm);
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
</style>
