<template>
  <view class="page">
    <!-- 用户信息卡片 -->
    <view class="user-card">
      <view class="user-bg">
        <view class="bg-pattern"></view>
      </view>
      
      <view class="user-info-wrapper">
        <image 
          :src="userInfo?.avatar || '/static/default-avatar.png'" 
          mode="aspectFill" 
          class="user-avatar"
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

    <!-- 快捷功能 -->
    <view class="quick-actions">
      <view class="action-item" @click="navigateTo('/pages/user/verify')">
        <view class="action-icon-wrapper" style="background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);">
          <text class="action-icon">✓</text>
        </view>
        <text class="action-text">实名认证</text>
        <view v-if="userInfo?.isVerified" class="action-status verified">
          已认证
        </view>
      </view>

      <view class="action-item" @click="navigateTo('/pages/user/favorites')">
        <view class="action-icon-wrapper" style="background: linear-gradient(135deg, #F093FB 0%, #F5576C 100%);">
          <text class="action-icon">❤️</text>
        </view>
        <text class="action-text">我的收藏</text>
      </view>

      <view class="action-item" @click="navigateTo('/pages/user/messages')">
        <view class="action-icon-wrapper" style="background: linear-gradient(135deg, #36D1DC 0%, #5B86E5 100%);">
          <text class="action-icon">📧</text>
        </view>
        <text class="action-text">消息通知</text>
        <view v-if="unreadCount > 0" class="badge-count">
          {{ unreadCount > 99 ? '99+' : unreadCount }}
        </view>
      </view>

      <view class="action-item" @click="navigateTo('/pages/user/wallet')">
        <view class="action-icon-wrapper" style="background: linear-gradient(135deg, #4FACFE 0%, #00F2FE 100%);">
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
  console.log('加载用户统计数据')
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
  background: #F8F9FA;
  padding-bottom: 40rpx;
}

/* 用户信息卡片 */
.user-card {
  position: relative;
  margin: 24rpx 24rpx 20rpx;
  background: #fff;
  border-radius: 32rpx;
  overflow: hidden;
  box-shadow: 0 8rpx 32rpx rgba(255, 140, 66, 0.12);
}

.user-bg {
  height: 200rpx;
  background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
  position: relative;
  overflow: hidden;
}

.bg-pattern {
  position: absolute;
  top: -50%;
  right: -20%;
  width: 400rpx;
  height: 400rpx;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
  border-radius: 50%;
}

.user-info-wrapper {
  position: relative;
  padding: 0 32rpx 32rpx;
  margin-top: -60rpx;
}

.user-avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  border: 6rpx solid #fff;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
}

.user-detail {
  margin-top: 20rpx;
}

.user-name-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 8rpx;
}

.user-nickname {
  font-size: 36rpx;
  font-weight: 600;
  color: #2C3E50;
}

.verified-badge {
  width: 36rpx;
  height: 36rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
  border-radius: 50%;
}

.verified-icon {
  font-size: 20rpx;
  color: #fff;
  font-weight: bold;
}

.user-phone {
  font-size: 26rpx;
  color: #7F8C8D;
}

.points-card {
  position: absolute;
  top: 20rpx;
  right: 32rpx;
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 16rpx 24rpx;
  background: linear-gradient(135deg, rgba(255, 157, 92, 0.15) 0%, rgba(255, 127, 41, 0.15) 100%);
  border-radius: 40rpx;
  border: 2rpx solid rgba(255, 140, 66, 0.3);
}

.points-label {
  font-size: 22rpx;
  color: #7F8C8D;
}

.points-value {
  font-size: 28rpx;
  font-weight: 600;
  color: #FF8C42;
}

.points-arrow {
  font-size: 28rpx;
  color: #FF8C42;
  font-weight: 300;
}

/* 数据统计 */
.stats-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 32rpx 0 8rpx;
  margin-top: 20rpx;
  border-top: 1rpx solid #E8EAED;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.stat-value {
  font-size: 32rpx;
  font-weight: 600;
  color: #2C3E50;
}

.stat-label {
  font-size: 24rpx;
  color: #7F8C8D;
}

.stat-divider {
  width: 2rpx;
  height: 60rpx;
  background: #E8EAED;
}

/* 快捷功能 */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24rpx;
  padding: 32rpx 24rpx;
  margin: 0 24rpx;
  background: #fff;
  border-radius: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.04);
}

.action-item {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
}

.action-icon-wrapper {
  width: 88rpx;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
}

.action-icon {
  font-size: 44rpx;
}

.action-text {
  font-size: 24rpx;
  color: #2C3E50;
}

.action-status {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  padding: 4rpx 12rpx;
  font-size: 20rpx;
  border-radius: 20rpx;
  
  &.verified {
    background: linear-gradient(135deg, #D4EDDA 0%, #C3E6CB 100%);
    color: #28A745;
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
  background: #FF4757;
  border-radius: 20rpx;
  font-size: 20rpx;
  color: #fff;
  font-weight: 500;
  box-shadow: 0 2rpx 8rpx rgba(255, 71, 87, 0.3);
}

/* 菜单组 */
.menu-group {
  margin: 20rpx 24rpx 0;
}

.group-title {
  display: block;
  padding: 20rpx 16rpx 16rpx;
  font-size: 28rpx;
  font-weight: 600;
  color: #2C3E50;
}

.menu-list {
  background: #fff;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.04);
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx;
  border-bottom: 1rpx solid #F8F9FA;
  transition: all 0.3s ease;

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background: #F8F9FA;
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
  color: #2C3E50;
}

.menu-arrow {
  font-size: 36rpx;
  color: #95A5A6;
  font-weight: 300;
}

/* 登录/退出按钮 */
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
  font-weight: 500;
  border-radius: 16rpx;
  border: none;
  transition: all 0.3s ease;
}

.btn-login {
  color: #fff;
  background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
  box-shadow: 0 8rpx 24rpx rgba(255, 140, 66, 0.3);
}

.btn-logout {
  color: #7F8C8D;
  background: #fff;
  border: 2rpx solid #E8EAED;
}
</style>
