<template>
  <view class="container">
    <view class="logo">
      <image src="/static/logo.png" mode="aspectFit" class="logo-image"></image>
      <text class="app-name">萌友益站</text>
    </view>

    <view class="login-tabs">
      <view 
        class="tab-item" 
        :class="{ active: loginType === 'wechat' }"
        @click="loginType = 'wechat'"
      >
        微信登录
      </view>
      <view 
        class="tab-item" 
        :class="{ active: loginType === 'phone' }"
        @click="loginType = 'phone'"
      >
        手机登录
      </view>
    </view>

    <!-- 微信登录 -->
    <view v-if="loginType === 'wechat'" class="login-form">
      <button class="btn-wechat" @click="handleWxLogin">
        <image src="/static/icons/wechat.png" class="btn-icon"></image>
        微信一键登录
      </button>
    </view>

    <!-- 手机登录 -->
    <view v-else class="login-form">
      <view class="form-item">
        <input 
          v-model="phone" 
          type="number" 
          placeholder="请输入手机号" 
          maxlength="11"
          class="form-input"
        />
      </view>
      
      <view class="form-item">
        <input 
          v-model="code" 
          type="number" 
          placeholder="请输入验证码" 
          maxlength="6"
          class="form-input code-input"
        />
        <button 
          class="btn-code" 
          :disabled="countdown > 0"
          @click="handleSendCode"
        >
          {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
        </button>
      </view>

      <button class="btn-login" @click="handlePhoneLogin">登录</button>
    </view>

    <view class="tips">
      登录即表示同意《用户协议》和《隐私政策》
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { userApi } from '@/utils/api'

const loginType = ref('wechat')
const phone = ref('')
const code = ref('')
const countdown = ref(0)

// 微信登录
const handleWxLogin = () => {
  uni.login({
    provider: 'weixin',
    success: async (res) => {
      try {
        const result = await userApi.wxLogin({
          code: res.code
        })
        
        // 保存token和用户信息
        uni.setStorageSync('token', result.data.token)
        uni.setStorageSync('userInfo', result.data.userInfo)
        
        uni.showToast({
          title: '登录成功',
          icon: 'success'
        })
        
        // 返回上一页或跳转到首页
        setTimeout(() => {
          uni.switchTab({
            url: '/pages/index/index'
          })
        }, 1500)
      } catch (error) {
        console.error('登录失败', error)
      }
    },
    fail: (err) => {
      console.error('微信授权失败', err)
      uni.showToast({
        title: '授权失败',
        icon: 'none'
      })
    }
  })
}

// 发送验证码
const handleSendCode = async () => {
  if (!phone.value) {
    uni.showToast({
      title: '请输入手机号',
      icon: 'none'
    })
    return
  }
  
  if (!/^1[3-9]\d{9}$/.test(phone.value)) {
    uni.showToast({
      title: '手机号格式不正确',
      icon: 'none'
    })
    return
  }
  
  try {
    await userApi.sendCode(phone.value)
    uni.showToast({
      title: '验证码已发送',
      icon: 'success'
    })
    
    // 倒计时
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    console.error('发送验证码失败', error)
  }
}

// 手机号登录
const handlePhoneLogin = async () => {
  if (!phone.value) {
    uni.showToast({
      title: '请输入手机号',
      icon: 'none'
    })
    return
  }
  
  if (!code.value) {
    uni.showToast({
      title: '请输入验证码',
      icon: 'none'
    })
    return
  }
  
  try {
    const result = await userApi.phoneLogin({
      phone: phone.value,
      code: code.value
    })
    
    // 保存token和用户信息
    uni.setStorageSync('token', result.data.token)
    uni.setStorageSync('userInfo', result.data.userInfo)
    
    uni.showToast({
      title: '登录成功',
      icon: 'success'
    })
    
    // 返回上一页或跳转到首页
    setTimeout(() => {
      uni.switchTab({
        url: '/pages/index/index'
      })
    }, 1500)
  } catch (error) {
    console.error('登录失败', error)
  }
}
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  padding: 80rpx 60rpx;
  background-color: #fff;
}

.logo {
  text-align: center;
  margin-bottom: 80rpx;
  
  .logo-image {
    width: 160rpx;
    height: 160rpx;
  }
  
  .app-name {
    display: block;
    margin-top: 20rpx;
    font-size: 40rpx;
    font-weight: bold;
    color: #333;
  }
}

.login-tabs {
  display: flex;
  margin-bottom: 60rpx;
  
  .tab-item {
    flex: 1;
    text-align: center;
    padding-bottom: 20rpx;
    font-size: 32rpx;
    color: #999;
    border-bottom: 4rpx solid transparent;
    
    &.active {
      color: #3cc51f;
      border-bottom-color: #3cc51f;
    }
  }
}

.login-form {
  .form-item {
    position: relative;
    margin-bottom: 40rpx;
    
    .form-input {
      width: 100%;
      height: 88rpx;
      padding: 0 30rpx;
      font-size: 28rpx;
      border: 2rpx solid #e5e5e5;
      border-radius: 8rpx;
    }
    
    .code-input {
      padding-right: 200rpx;
    }
    
    .btn-code {
      position: absolute;
      right: 20rpx;
      top: 50%;
      transform: translateY(-50%);
      padding: 10rpx 20rpx;
      font-size: 24rpx;
      color: #3cc51f;
      background-color: transparent;
      border: none;
      
      &:disabled {
        color: #999;
      }
    }
  }
  
  .btn-wechat {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 88rpx;
    margin-top: 60rpx;
    font-size: 32rpx;
    color: #fff;
    background-color: #09bb07;
    border-radius: 8rpx;
    border: none;
    
    .btn-icon {
      width: 40rpx;
      height: 40rpx;
      margin-right: 15rpx;
    }
  }
  
  .btn-login {
    width: 100%;
    height: 88rpx;
    margin-top: 60rpx;
    font-size: 32rpx;
    color: #fff;
    background-color: #3cc51f;
    border-radius: 8rpx;
    border: none;
  }
}

.tips {
  margin-top: 80rpx;
  text-align: center;
  font-size: 24rpx;
  color: #999;
}
</style>
