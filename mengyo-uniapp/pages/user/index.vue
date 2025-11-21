<template>
  <view class="page">
    <!-- 用户信息卡片 - Airbnb 风格留白设计 -->
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
            <text class="points-label">我的积分</text>
            <text class="points-value">{{ userInfo?.points || 0 }}</text>
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
    </view>

    <!-- 快捷功能 - 更温馨的设计 -->
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

    <!-- 菜单列表 - Airbnb 风格留白 -->
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
  background: var(--bg-page);
  padding-bottom: var(--spacing-xl);
}

/* 用户信息卡片 - Airbnb 风格留白 */
.user-section {
  padding: var(--spacing-lg);
}

.user-card {
  position: relative;
  background: var(--bg-white);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-card);
  border: 1rpx solid var(--border-color);
}

.user-bg {
  height: 240rpx;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-light) 100%);
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
  padding: 0 var(--spacing-xl) var(--spacing-xl);
  margin-top: -80rpx;
}

.user-avatar {
  width: 140rpx;
  height: 140rpx;
  border-radius: 50%;
  border: 8rpx solid var(--bg-white);
  box-shadow: var(--shadow-md);
  background: var(--bg-warm);
}

.user-detail {
  margin-top: var(--spacing-lg);
}

.user-name-row {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-xs);
}

.user-nickname {
  font-size: 40rpx;
  font-weight: 600;
  color: var(--text-primary);
  letter-spacing: -0.01em;
}

.verified-badge {
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-color);
  border-radius: 50%;
  box-shadow: var(--shadow-sm);
}

.verified-icon {
  font-size: 22rpx;
  color: #fff;
  font-weight: bold;
}

.user-phone {
  font-size: 28rpx;
  color: var(--text-secondary);
}

.points-card {
  position: absolute;
  top: var(--spacing-lg);
  right: var(--spacing-xl);
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md) var(--spacing-lg);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10rpx);
  border-radius: var(--radius-full);
  border: 1rpx solid rgba(255, 255, 255, 0.5);
  box-shadow: var(--shadow-sm);
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.95);
  }
}

.points-label {
  font-size: 24rpx;
  color: var(--text-secondary);
}

.points-value {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--primary-color);
}

.points-arrow {
  font-size: 32rpx;
  color: var(--primary-color);
  font-weight: 300;
}

/* 数据统计 */
.stats-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: var(--spacing-xl) 0 var(--spacing-md);
  margin-top: var(--spacing-lg);
  border-top: 1rpx solid var(--divider-color);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-xs);
  flex: 1;
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.95);
  }
}

.stat-value {
  font-size: 36rpx;
  font-weight: 600;
  color: var(--text-primary);
  letter-spacing: -0.01em;
}

.stat-label {
  font-size: 26rpx;
  color: var(--text-secondary);
}

.stat-divider {
  width: 2rpx;
  height: 60rpx;
  background: var(--divider-color);
}

/* 快捷功能 - 更温馨的设计 */
.quick-actions-section {
  padding: 0 var(--spacing-lg) var(--spacing-xl);
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-lg);
  padding: var(--spacing-xl);
  background: var(--bg-white);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-card);
  border: 1rpx solid var(--border-color);
}

.action-item {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
}

.action-icon-wrapper {
  width: 100rpx;
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  transition: all 0.3s ease;
  
  &.verify {
    background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-light) 100%);
  }
  
  &.favorites {
    background: linear-gradient(135deg, #FF6B6B 0%, #FF8787 100%);
  }
  
  &.messages {
    background: linear-gradient(135deg, var(--info-color) 0%, #00C4B3 100%);
  }
  
  &.wallet {
    background: linear-gradient(135deg, #FFB84D 0%, #FFC970 100%);
  }
  
  &:active {
    transform: scale(0.95);
  }
}

.action-icon {
  font-size: 48rpx;
}

.action-text {
  font-size: 26rpx;
  color: var(--text-primary);
  font-weight: 500;
}

.action-status {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  padding: 4rpx 12rpx;
  font-size: 20rpx;
  border-radius: var(--radius-full);
  
  &.verified {
    background: var(--success-color);
    color: #fff;
  }
}

.badge-count {
  position: absolute;
  top: -8rpx;
  right: 0;
  min-width: 36rpx;
  height: 36rpx;
  padding: 0 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--danger-color);
  border-radius: var(--radius-full);
  font-size: 20rpx;
  color: #fff;
  font-weight: 500;
  box-shadow: var(--shadow-sm);
}

/* 菜单组 - Airbnb 风格留白 */
.menu-section {
  padding: 0 var(--spacing-lg);
}

.menu-group {
  margin-bottom: var(--spacing-xl);
}

.group-title {
  display: block;
  padding: var(--spacing-md) var(--spacing-sm) var(--spacing-sm);
  font-size: 28rpx;
  font-weight: 600;
  color: var(--text-primary);
  letter-spacing: -0.01em;
}

.menu-list {
  background: var(--bg-white);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-card);
  border: 1rpx solid var(--border-color);
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-xl);
  border-bottom: 1rpx solid var(--divider-color);
  transition: all 0.3s ease;

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background: var(--bg-gray);
  }
}

.menu-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.menu-icon {
  font-size: 44rpx;
}

.menu-text {
  font-size: 30rpx;
  color: var(--text-primary);
}

.menu-arrow {
  font-size: 40rpx;
  color: var(--text-light);
  font-weight: 300;
}

/* 登录/退出按钮 */
.action-section {
  padding: var(--spacing-xl) var(--spacing-lg);
}

.btn-login,
.btn-logout {
  width: 100%;
  height: 96rpx;
  line-height: 96rpx;
  text-align: center;
  font-size: 32rpx;
  font-weight: 500;
  border-radius: var(--radius-full);
  border: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.btn-login {
  color: #fff;
  background: var(--primary-color);
  box-shadow: var(--shadow-md);
  
  &:active {
    background: var(--primary-dark);
    transform: scale(0.98);
  }
}

.btn-logout {
  color: var(--text-secondary);
  background: var(--bg-white);
  border: 2rpx solid var(--border-color);
  
  &:active {
    background: var(--bg-gray);
  }
}
</style>
