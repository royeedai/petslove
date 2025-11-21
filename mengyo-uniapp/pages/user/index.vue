<template>
  <view class="container">
    <!-- 用户信息 -->
    <view class="user-header">
      <image :src="userInfo?.avatar || '/static/default-avatar.png'" mode="aspectFill" class="user-avatar"></image>
      <view class="user-info">
        <text class="user-nickname">{{ userInfo?.nickname || '未登录' }}</text>
        <text class="user-phone">{{ userInfo?.phone || '请先登录' }}</text>
      </view>
      <view v-if="userInfo" class="user-points">
        <text class="points-num">{{ userInfo?.points || 0 }}</text>
        <text class="points-text">积分</text>
      </view>
    </view>

    <!-- 菜单列表 -->
    <view class="menu-section">
      <view class="menu-item" @click="navigateTo('/pages/user/verify')">
        <image src="/static/icons/verify.png" class="menu-icon"></image>
        <text class="menu-text">实名认证</text>
        <text class="menu-badge" v-if="userInfo?.isVerified">已认证</text>
        <text class="arrow">></text>
      </view>
      
      <view class="menu-item" @click="navigateTo('/pages/user/points')">
        <image src="/static/icons/points.png" class="menu-icon"></image>
        <text class="menu-text">我的积分</text>
        <text class="arrow">></text>
      </view>
      
      <view class="menu-item" @click="navigateTo('/pages/user/rescue')">
        <image src="/static/icons/my-rescue.png" class="menu-icon"></image>
        <text class="menu-text">我的救助</text>
        <text class="arrow">></text>
      </view>
      
      <view class="menu-item" @click="navigateTo('/pages/user/adoption')">
        <image src="/static/icons/my-adoption.png" class="menu-icon"></image>
        <text class="menu-text">我的领养</text>
        <text class="arrow">></text>
      </view>
      
      <view class="menu-item" @click="navigateTo('/pages/user/donation')">
        <image src="/static/icons/my-donation.png" class="menu-icon"></image>
        <text class="menu-text">我的捐赠</text>
        <text class="arrow">></text>
      </view>
    </view>

    <!-- 设置 -->
    <view class="menu-section">
      <view class="menu-item" @click="navigateTo('/pages/user/settings')">
        <image src="/static/icons/settings.png" class="menu-icon"></image>
        <text class="menu-text">设置</text>
        <text class="arrow">></text>
      </view>
    </view>

    <!-- 登录/退出按钮 -->
    <view class="action-section">
      <button v-if="!userInfo" class="btn-login" @click="navigateTo('/pages/login/index')">
        登录
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
    title: '提示',
    content: '确定要退出登录吗？',
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
.container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.user-header {
  display: flex;
  align-items: center;
  padding: 60rpx 40rpx;
  background: linear-gradient(135deg, #3cc51f 0%, #28a816 100%);
  
  .user-avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: 50%;
    border: 4rpx solid #fff;
  }
  
  .user-info {
    flex: 1;
    margin-left: 30rpx;
    
    .user-nickname {
      display: block;
      font-size: 32rpx;
      font-weight: bold;
      color: #fff;
      margin-bottom: 10rpx;
    }
    
    .user-phone {
      font-size: 24rpx;
      color: rgba(255, 255, 255, 0.8);
    }
  }
  
  .user-points {
    text-align: center;
    
    .points-num {
      display: block;
      font-size: 36rpx;
      font-weight: bold;
      color: #fff;
    }
    
    .points-text {
      font-size: 24rpx;
      color: rgba(255, 255, 255, 0.8);
    }
  }
}

.menu-section {
  margin-top: 20rpx;
  background-color: #fff;
  
  .menu-item {
    display: flex;
    align-items: center;
    padding: 30rpx 40rpx;
    border-bottom: 1rpx solid #f5f5f5;
    
    &:last-child {
      border-bottom: none;
    }
    
    .menu-icon {
      width: 40rpx;
      height: 40rpx;
      margin-right: 20rpx;
    }
    
    .menu-text {
      flex: 1;
      font-size: 28rpx;
      color: #333;
    }
    
    .menu-badge {
      font-size: 24rpx;
      color: #3cc51f;
      margin-right: 10rpx;
    }
    
    .arrow {
      font-size: 28rpx;
      color: #999;
    }
  }
}

.action-section {
  padding: 40rpx;
  
  .btn-login,
  .btn-logout {
    width: 100%;
    height: 88rpx;
    line-height: 88rpx;
    text-align: center;
    font-size: 32rpx;
    border-radius: 8rpx;
    border: none;
  }
  
  .btn-login {
    color: #fff;
    background-color: #3cc51f;
  }
  
  .btn-logout {
    color: #999;
    background-color: #fff;
    border: 2rpx solid #e5e5e5;
  }
}
</style>
