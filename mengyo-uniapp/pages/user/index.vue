<template>
  <view class="page">
    <!-- 用户信息卡片 - Petfinder风格 -->
    <view class="user-section">
      <view class="user-card">
        <view class="user-bg">
          <view class="bg-pattern"></view>
        </view>
        
        <view class="user-info-wrapper">
          <image 
            :src="userInfo?.avatar || defaultAvatar" 
            mode="aspectFill" 
            class="user-avatar"
            @error="handleAvatarError"
            @click="handleEditProfile"
          ></image>
          
          <view class="user-detail">
            <view class="user-name-row">
              <text class="user-nickname">{{ userInfo?.nickname || '未登录' }}</text>
              <view v-if="userInfo?.isVerified" class="verified-badge">
                <text class="verified-icon">✓</text>
              </view>
            </view>
            <text class="user-phone">{{ userInfo?.phone || '点击登录' }}</text>
          </view>

          <view v-if="userInfo" class="points-card" @click="navigateTo('/pages/user/points')">
            <text class="points-label">积分</text>
            <text class="points-value">{{ userInfo?.points || 0 }}</text>
            <text class="points-arrow">›</text>
          </view>
        </view>

        <!-- 数据统计 -->
        <view v-if="userInfo" class="stats-wrapper">
          <view class="stat-item" @click="navigateTo('/pages/user/rescue')">
            <text class="stat-value">{{ userStats.rescueCount || 0 }}</text>
            <text class="stat-label">救助</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item" @click="navigateTo('/pages/user/adoption')">
            <text class="stat-value">{{ userStats.adoptionCount || 0 }}</text>
            <text class="stat-label">领养</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item" @click="navigateTo('/pages/user/donation')">
            <text class="stat-value">{{ userStats.donationCount || 0 }}</text>
            <text class="stat-label">捐赠</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 快捷功能 -->
    <view class="quick-actions-section">
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
          <view class="action-icon-wrapper favorites">
            <text class="action-icon">❤️</text>
          </view>
          <text class="action-text">我的收藏</text>
        </view>

        <view class="action-item" @click="navigateTo('/pages/user/messages')">
          <view class="action-icon-wrapper messages">
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
    </view>

    <!-- 菜单列表 -->
    <view class="menu-section">
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

const defaultAvatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=default'

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
  console.log('加载用户统计数据')
}

const handleAvatarError = () => {
  if (userInfo.value) {
    userInfo.value.avatar = defaultAvatar
  }
}

const handleEditProfile = () => {
  if (userInfo.value) {
    navigateTo('/pages/user/profile-edit')
  }
}

const navigateTo = (url) => {
  if (!userInfo.value && url !== '/pages/login/index') {
    uni.navigateTo({
      url: '/pages/login/index'
    })
    return
  }
  uni.navigateTo({ url })
}

const handleLogout = () => {
  uni.showModal({
    title: '退出登录',
    content: '确定要退出登录吗？',
    confirmColor: '#FF5A5F',
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
  background: #F5F5F5;
  padding-bottom: 40rpx;
}

/* 用户信息卡片 */
.user-section {
  padding: 24rpx 32rpx;
}

.user-card {
  position: relative;
  background: #FFFFFF;
  border-radius: 32rpx;
  overflow: hidden;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
}

.user-bg {
  height: 240rpx;
  background: linear-gradient(135deg, #FF5A5F 0%, #FF7A7F 100%);
  position: relative;
  overflow: hidden;
}

.bg-pattern {
  position: absolute;
  top: -50%;
  right: -20%;
  width: 500rpx;
  height: 500rpx;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.15) 0%, transparent 70%);
  border-radius: 50%;
}

.user-info-wrapper {
  position: relative;
  padding: 0 40rpx 40rpx;
  margin-top: -80rpx;
}

.user-avatar {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  border: 8rpx solid #FFFFFF;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.15);
  background: #F5F5F5;
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.95);
  }
}

.user-detail {
  margin-top: 24rpx;
}

.user-name-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 12rpx;
}

.user-nickname {
  font-size: 44rpx;
  font-weight: 700;
  color: #2C2C2C;
  letter-spacing: -0.02em;
}

.verified-badge {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #FF5A5F;
  border-radius: 50%;
  box-shadow: 0 4rpx 12rpx rgba(255, 90, 95, 0.3);
}

.verified-icon {
  font-size: 28rpx;
  color: #FFFFFF;
  font-weight: bold;
}

.user-phone {
  font-size: 28rpx;
  color: #666;
}

.points-card {
  position: absolute;
  top: 24rpx;
  right: 40rpx;
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 16rpx 32rpx;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10rpx);
  border-radius: 50rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.95);
  }
}

.points-label {
  font-size: 26rpx;
  color: #666;
}

.points-value {
  font-size: 36rpx;
  font-weight: 700;
  color: #FF5A5F;
}

.points-arrow {
  font-size: 36rpx;
  color: #FF5A5F;
  font-weight: 300;
}

/* 数据统计 */
.stats-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 32rpx 0 24rpx;
  margin-top: 24rpx;
  border-top: 1rpx solid #F0F0F0;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  flex: 1;
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.95);
  }
}

.stat-value {
  font-size: 40rpx;
  font-weight: 700;
  color: #2C2C2C;
  letter-spacing: -0.02em;
}

.stat-label {
  font-size: 26rpx;
  color: #666;
}

.stat-divider {
  width: 2rpx;
  height: 60rpx;
  background: #F0F0F0;
}

/* 快捷功能 */
.quick-actions-section {
  padding: 0 32rpx 32rpx;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24rpx;
  padding: 32rpx;
  background: #FFFFFF;
  border-radius: 32rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
}

.action-item {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
}

.action-icon-wrapper {
  width: 120rpx;
  height: 120rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 24rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  
  &.verify {
    background: linear-gradient(135deg, #FF5A5F 0%, #FF7A7F 100%);
  }
  
  &.favorites {
    background: linear-gradient(135deg, #FF6B6B 0%, #FF8787 100%);
  }
  
  &.messages {
    background: linear-gradient(135deg, #00A699 0%, #00C4B3 100%);
  }
  
  &.wallet {
    background: linear-gradient(135deg, #FFB84D 0%, #FFC970 100%);
  }
  
  &:active {
    transform: scale(0.95);
  }
}

.action-icon {
  font-size: 56rpx;
}

.action-text {
  font-size: 26rpx;
  color: #2C2C2C;
  font-weight: 500;
}

.action-status {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  padding: 6rpx 16rpx;
  font-size: 20rpx;
  border-radius: 50rpx;
  
  &.verified {
    background: #00A699;
    color: #FFFFFF;
  }
}

.badge-count {
  position: absolute;
  top: -8rpx;
  right: 0;
  min-width: 40rpx;
  height: 40rpx;
  padding: 0 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #FF5A5F;
  border-radius: 50rpx;
  font-size: 20rpx;
  color: #FFFFFF;
  font-weight: 600;
  box-shadow: 0 2rpx 8rpx rgba(255, 90, 95, 0.3);
}

/* 菜单组 */
.menu-section {
  padding: 0 32rpx;
}

.menu-group {
  margin-bottom: 32rpx;
}

.group-title {
  display: block;
  padding: 16rpx 16rpx 12rpx;
  font-size: 28rpx;
  font-weight: 600;
  color: #2C2C2C;
  letter-spacing: -0.01em;
}

.menu-list {
  background: #FFFFFF;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx;
  border-bottom: 1rpx solid #F0F0F0;
  transition: all 0.3s ease;
  
  &:last-child {
    border-bottom: none;
  }
  
  &:active {
    background: #F5F5F5;
  }
}

.menu-left {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.menu-icon {
  font-size: 48rpx;
}

.menu-text {
  font-size: 30rpx;
  color: #2C2C2C;
}

.menu-arrow {
  font-size: 40rpx;
  color: #999;
  font-weight: 300;
}

/* 登录/退出按钮 */
.action-section {
  padding: 32rpx;
}

.btn-login,
.btn-logout {
  width: 100%;
  height: 96rpx;
  line-height: 96rpx;
  text-align: center;
  font-size: 32rpx;
  font-weight: 600;
  border-radius: 16rpx;
  border: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.btn-login {
  color: #FFFFFF;
  background: linear-gradient(135deg, #FF5A5F 0%, #FF7A7F 100%);
  box-shadow: 0 8rpx 24rpx rgba(255, 90, 95, 0.4);
  
  &:active {
    background: linear-gradient(135deg, #E04A4F 0%, #FF6B70 100%);
    transform: scale(0.98);
  }
}

.btn-logout {
  color: #666;
  background: #FFFFFF;
  border: 2rpx solid #F0F0F0;
  
  &:active {
    background: #F5F5F5;
  }
}
</style>
