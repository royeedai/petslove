<template>
  <view class="page">
    <!-- 用户信息卡片 -->
    <view class="user-card">
      <view class="user-bg">
        <view class="bg-pattern-1"></view>
        <view class="bg-pattern-2"></view>
        <view class="bg-pattern-3"></view>
      </view>
      
      <view class="user-info-wrapper">
        <view class="avatar-wrapper" @click="handleAvatarClick">
          <image 
            :src="userInfo?.avatar || 'https://api.dicebear.com/7.x/avataaars/svg?seed=default'" 
            mode="aspectFill" 
            class="user-avatar"
          ></image>
          <view v-if="userInfo?.isVerified" class="verified-badge">
            <text class="verified-icon">✓</text>
          </view>
        </view>
        
        <view class="user-detail">
          <view class="user-name-row">
            <text class="user-nickname">{{ userInfo?.nickname || '未登录' }}</text>
          </view>
          <text class="user-phone">{{ userInfo?.phone || '点击登录' }}</text>
        </view>

        <view v-if="userInfo" class="points-card" @click="navigateTo('/pages/user/points')">
          <view class="points-icon-wrapper">
            <text class="points-icon">💰</text>
          </view>
          <view class="points-info">
            <text class="points-label">我的积分</text>
            <text class="points-value">{{ userInfo?.points || 0 }}</text>
          </view>
          <text class="points-arrow">›</text>
        </view>
      </view>

      <!-- 数据统计 -->
      <view v-if="userInfo" class="stats-wrapper">
        <view class="stat-item" @click="navigateTo('/pages/user/rescue')">
          <text class="stat-value">{{ userStats.rescueCount || 0 }}</text>
          <text class="stat-label">救助次数</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item" @click="navigateTo('/pages/user/adoption')">
          <text class="stat-value">{{ userStats.adoptionCount || 0 }}</text>
          <text class="stat-label">领养记录</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item" @click="navigateTo('/pages/user/donation')">
          <text class="stat-value">{{ userStats.donationCount || 0 }}</text>
          <text class="stat-label">捐赠次数</text>
        </view>
      </view>
    </view>

    <!-- 快捷功能 -->
    <view class="quick-actions">
      <view class="action-item" @click="navigateTo('/pages/user/verify')">
        <view class="action-icon-wrapper verify">
          <text class="action-icon">✓</text>
        </view>
        <text class="action-text">实名认证</text>
        <view v-if="userInfo?.isVerified" class="action-status verified">
          已认证
        </view>
      </view>

      <view class="action-item" @click="navigateTo('/pages/user/favorites')">
        <view class="action-icon-wrapper favorite">
          <text class="action-icon">❤️</text>
        </view>
        <text class="action-text">我的收藏</text>
      </view>

      <view class="action-item" @click="navigateTo('/pages/user/messages')">
        <view class="action-icon-wrapper message">
          <text class="action-icon">📧</text>
        </view>
        <text class="action-text">消息通知</text>
        <view v-if="unreadCount > 0" class="badge-count">
          {{ unreadCount > 99 ? '99+' : unreadCount }}
        </view>
      </view>

      <view class="action-item" @click="navigateTo('/pages/user/wallet')">
        <view class="action-icon-wrapper wallet">
          <text class="action-icon">💰</text>
        </view>
        <text class="action-text">我的钱包</text>
      </view>
    </view>

    <!-- 菜单列表 -->
    <view class="menu-group">
      <text class="group-title">我的活动</text>
      <view class="menu-list">
        <view class="menu-item" @click="navigateTo('/pages/user/rescue')">
          <view class="menu-left">
            <text class="menu-icon">🆘</text>
            <text class="menu-text">我的救助</text>
          </view>
          <text class="menu-arrow">›</text>
        </view>

        <view class="menu-item" @click="navigateTo('/pages/user/adoption')">
          <view class="menu-left">
            <text class="menu-icon">🏠</text>
            <text class="menu-text">我的领养</text>
          </view>
          <text class="menu-arrow">›</text>
        </view>

        <view class="menu-item" @click="navigateTo('/pages/user/donation')">
          <view class="menu-left">
            <text class="menu-icon">💝</text>
            <text class="menu-text">我的捐赠</text>
          </view>
          <text class="menu-arrow">›</text>
        </view>

        <view class="menu-item" @click="navigateTo('/pages/user/posts')">
          <view class="menu-left">
            <text class="menu-icon">📝</text>
            <text class="menu-text">我的动态</text>
          </view>
          <text class="menu-arrow">›</text>
        </view>
      </view>
    </view>

    <view class="menu-group">
      <text class="group-title">设置</text>
      <view class="menu-list">
        <view class="menu-item" @click="navigateTo('/pages/user/profile-edit')">
          <view class="menu-left">
            <text class="menu-icon">👤</text>
            <text class="menu-text">个人资料</text>
          </view>
          <text class="menu-arrow">›</text>
        </view>

        <view class="menu-item" @click="navigateTo('/pages/user/settings')">
          <view class="menu-left">
            <text class="menu-icon">⚙️</text>
            <text class="menu-text">账号设置</text>
          </view>
          <text class="menu-arrow">›</text>
        </view>

        <view class="menu-item" @click="navigateTo('/pages/user/about')">
          <view class="menu-left">
            <text class="menu-icon">ℹ️</text>
            <text class="menu-text">关于我们</text>
          </view>
          <text class="menu-arrow">›</text>
        </view>
      </view>
    </view>

    <!-- 登录/退出按钮 -->
    <view class="action-section">
      <button v-if="!userInfo" class="btn-login" @click="navigateTo('/pages/login/index')">
        立即登录
      </button>
      <button v-else class="btn-logout" @click="handleLogout">
        退出登录
      </button>
    </view>
    
    <!-- 底部间距 -->
    <view class="page-footer"></view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { userApi } from '@/utils/api'

const userInfo = ref(null)
const userStats = ref({
  rescueCount: 0,
  adoptionCount: 0,
  donationCount: 0
})
const unreadCount = ref(0)

onMounted(() => {
  loadUserInfo()
})

const loadUserInfo = async () => {
  const info = uni.getStorageSync('userInfo')
  if (info) {
    userInfo.value = info
    
    // 刷新最新用户信息
    try {
      const result = await userApi.getUserInfo(info.id)
      if (result.data) {
        userInfo.value = result.data
        uni.setStorageSync('userInfo', result.data)
      }
    } catch (error) {
      console.error('获取用户信息失败', error)
    }

    // 加载用户统计
    loadUserStats()
  }
}

const loadUserStats = async () => {
  // TODO: 加载用户统计数据
  userStats.value = {
    rescueCount: 5,
    adoptionCount: 2,
    donationCount: 8
  }
}

const handleAvatarClick = () => {
  if (!userInfo.value) {
    uni.navigateTo({
      url: '/pages/login/index'
    })
    return
  }
  
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: (res) => {
      console.log('选择的图片', res.tempFilePaths)
      uni.showToast({
        title: '头像上传功能开发中',
        icon: 'none'
      })
    }
  })
}

const navigateTo = (url) => {
  if (!userInfo.value && url !== '/pages/login/index') {
    uni.navigateTo({
      url: '/pages/login/index'
    })
    return
  }
  
  uni.showToast({
    title: '页面开发中',
    icon: 'none'
  })
}

const handleLogout = () => {
  uni.showModal({
    title: '退出登录',
    content: '确定要退出登录吗？',
    confirmColor: '#FF8C42',
    success: (res) => {
      if (res.confirm) {
        uni.removeStorageSync('token')
        uni.removeStorageSync('userInfo')
        userInfo.value = null
        uni.showToast({
          title: '已退出登录',
          icon: 'success'
        })
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: linear-gradient(180deg, #FFFBF5 0%, #FEF7F0 100%);
  padding-top: env(safe-area-inset-top);
}

/* ===================================
   用户信息卡片
   =================================== */

.user-card {
  position: relative;
  margin: 24rpx 24rpx 20rpx;
  background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
  border-radius: 40rpx;
  overflow: hidden;
  box-shadow: 0 8rpx 32rpx rgba(255, 140, 66, 0.25);
}

.user-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
}

.bg-pattern-1 {
  position: absolute;
  top: -50rpx;
  right: -50rpx;
  width: 300rpx;
  height: 300rpx;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.15) 0%, transparent 70%);
  border-radius: 50%;
  animation: float 6s ease-in-out infinite;
}

.bg-pattern-2 {
  position: absolute;
  bottom: -100rpx;
  left: -100rpx;
  width: 400rpx;
  height: 400rpx;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  border-radius: 50%;
  animation: float 8s ease-in-out infinite;
}

.bg-pattern-3 {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 200rpx;
  height: 200rpx;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.08) 0%, transparent 70%);
  border-radius: 50%;
  animation: float 7s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-20rpx) scale(1.05);
  }
}

.user-info-wrapper {
  position: relative;
  padding: 40rpx 32rpx 32rpx;
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.avatar-wrapper {
  position: relative;
  align-self: center;
}

.user-avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  border: 6rpx solid #FFFFFF;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.15);
}

.verified-badge {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #FFFFFF;
  border-radius: 50%;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.15);
}

.verified-icon {
  font-size: 24rpx;
  color: #059669;
  font-weight: bold;
}

.user-detail {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.user-name-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.user-nickname {
  font-size: 36rpx;
  font-weight: 700;
  color: #FFFFFF;
}

.user-phone {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.85);
}

.points-card {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 20rpx 28rpx;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(20rpx);
  border-radius: 24rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.98);
  }
}

.points-icon-wrapper {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 50%;
}

.points-icon {
  font-size: 32rpx;
}

.points-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.points-label {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.85);
}

.points-value {
  font-size: 32rpx;
  font-weight: 700;
  color: #FFFFFF;
}

.points-arrow {
  font-size: 32rpx;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 300;
}

/* 数据统计 */
.stats-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 32rpx 0 8rpx;
  margin-top: 20rpx;
  border-top: 1rpx solid rgba(255, 255, 255, 0.3);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.stat-value {
  font-size: 36rpx;
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
   快捷功能
   =================================== */

.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24rpx;
  padding: 32rpx;
  margin: 0 24rpx 20rpx;
  background: linear-gradient(to bottom right, #FFFFFF 0%, #FEF7F0 100%);
  border-radius: 32rpx;
  border: 1rpx solid #F5F5F4;
  box-shadow: 0 4rpx 20rpx rgba(255, 140, 66, 0.08);
}

.action-item {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.95);
  }
}

.action-icon-wrapper {
  width: 88rpx;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(255, 140, 66, 0.12);
  
  &.verify {
    background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
  }
  
  &.favorite {
    background: linear-gradient(135deg, #F093FB 0%, #F5576C 100%);
  }
  
  &.message {
    background: linear-gradient(135deg, #36D1DC 0%, #5B86E5 100%);
  }
  
  &.wallet {
    background: linear-gradient(135deg, #4FACFE 0%, #00F2FE 100%);
  }
}

.action-icon {
  font-size: 44rpx;
}

.action-text {
  font-size: 24rpx;
  color: #57534E;
  font-weight: 500;
}

.action-status {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  padding: 4rpx 12rpx;
  font-size: 20rpx;
  border-radius: 20rpx;
  font-weight: 600;
  
  &.verified {
    background: linear-gradient(135deg, #D1FAE5 0%, #A7F3D0 100%);
    color: #059669;
  }
}

.badge-count {
  position: absolute;
  top: -8rpx;
  right: 0;
  min-width: 32rpx;
  height: 32rpx;
  padding: 0 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #EF4444;
  border-radius: 20rpx;
  font-size: 20rpx;
  color: #FFFFFF;
  font-weight: 600;
  box-shadow: 0 2rpx 8rpx rgba(239, 68, 68, 0.35);
}

/* ===================================
   菜单组
   =================================== */

.menu-group {
  margin: 0 24rpx 20rpx;
}

.group-title {
  display: block;
  padding: 20rpx 16rpx 16rpx;
  font-size: 28rpx;
  font-weight: 700;
  color: #57534E;
}

.menu-list {
  background: linear-gradient(to bottom right, #FFFFFF 0%, #FEF7F0 100%);
  border-radius: 32rpx;
  overflow: hidden;
  border: 1rpx solid #F5F5F4;
  box-shadow: 0 4rpx 20rpx rgba(255, 140, 66, 0.08);
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx;
  border-bottom: 1rpx solid #F5F5F4;
  transition: all 0.3s ease;

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background: #FEF7F0;
  }
}

.menu-left {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.menu-icon {
  font-size: 40rpx;
}

.menu-text {
  font-size: 28rpx;
  color: #57534E;
  font-weight: 500;
}

.menu-arrow {
  font-size: 36rpx;
  color: #A8A29E;
  font-weight: 300;
}

/* ===================================
   登录/退出按钮
   =================================== */

.action-section {
  padding: 40rpx 24rpx;
}

.btn-login,
.btn-logout {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  text-align: center;
  font-size: 30rpx;
  font-weight: 600;
  border-radius: 24rpx;
  border: none;
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.98);
  }
}

.btn-login {
  color: #FFFFFF;
  background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
  box-shadow: 0 8rpx 24rpx rgba(255, 140, 66, 0.25);
}

.btn-logout {
  color: #78716C;
  background: #FFFFFF;
  border: 2rpx solid #F5F5F4;
}

.page-footer {
  height: 40rpx;
}
</style>
