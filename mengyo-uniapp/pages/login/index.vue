<template>
  <view class="page">
    <!-- 装饰背景 - 更温馨的设计 -->
    <view class="bg-decoration">
      <view class="circle circle-1"></view>
      <view class="circle circle-2"></view>
      <view class="circle circle-3"></view>
    </view>

    <!-- Logo 区域 -->
    <view class="logo-section">
      <view class="logo-wrapper">
        <text class="logo-emoji">🐾</text>
      </view>
      <text class="app-name">萌友益站</text>
      <text class="app-slogan">用爱守护每一个生命</text>
    </view>

    <!-- 登录表单卡片 - 温馨暖色调 -->
    <view class="login-card">
      <!-- 登录方式切换 -->
      <view class="login-tabs">
        <view 
          class="tab-item" 
          :class="{ active: loginType === 'wechat' }"
          @click="loginType = 'wechat'"
        >
          <text class="tab-icon">💚</text>
          <text class="tab-text">微信登录</text>
        </view>
        <view 
          class="tab-item" 
          :class="{ active: loginType === 'phone' }"
          @click="loginType = 'phone'"
        >
          <text class="tab-icon">📱</text>
          <text class="tab-text">手机登录</text>
        </view>
      </view>

      <!-- 微信登录 -->
      <view v-if="loginType === 'wechat'" class="login-form">
        <view class="wechat-info">
          <text class="info-icon">🔒</text>
          <text class="info-text">使用微信账号快速登录</text>
        </view>
        
        <button class="btn-wechat" @click="handleWxLogin">
          <text class="btn-icon">💚</text>
          <text class="btn-text">微信一键登录</text>
        </button>
      </view>

      <!-- 手机登录 -->
      <view v-else class="login-form">
        <view class="form-group">
          <view class="input-wrapper">
            <text class="input-icon">📱</text>
            <input 
              v-model="phone" 
              type="number" 
              placeholder="请输入手机号" 
              maxlength="11"
              class="form-input"
              placeholder-class="input-placeholder"
            />
          </view>
        </view>
        
        <view class="form-group">
          <view class="input-wrapper">
            <text class="input-icon">🔐</text>
            <input 
              v-model="code" 
              type="number" 
              placeholder="请输入验证码" 
              maxlength="6"
              class="form-input code-input"
              placeholder-class="input-placeholder"
            />
            <button 
              class="btn-code" 
              :class="{ disabled: countdown > 0 }"
              :disabled="countdown > 0"
              @click="handleSendCode"
            >
              {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
            </button>
          </view>
        </view>

        <button class="btn-login" @click="handlePhoneLogin">
          <text class="btn-text">立即登录</text>
        </button>
      </view>

      <!-- 协议条款 -->
      <view class="agreement">
        <checkbox-group @change="handleAgreeChange">
          <label class="agreement-label">
            <checkbox 
              :checked="agreeTerms" 
              color="#FF5A5F"
              class="agreement-checkbox"
            />
            <text class="agreement-text">
              我已阅读并同意
              <text class="agreement-link" @click.stop="handleProtocol('user')">《用户协议》</text>
              和
              <text class="agreement-link" @click.stop="handleProtocol('privacy')">《隐私政策》</text>
            </text>
          </label>
        </checkbox-group>
      </view>
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
const agreeTerms = ref(false)

// 微信登录
const handleWxLogin = () => {
  if (!agreeTerms.value) {
    uni.showToast({
      title: '请先同意用户协议和隐私政策',
      icon: 'none',
      duration: 2000
    })
    return
  }

  uni.showLoading({
    title: '登录中...'
  })

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
        
        uni.hideLoading()
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
        uni.hideLoading()
        console.error('登录失败', error)
        uni.showToast({
          title: '登录失败，请重试',
          icon: 'none'
        })
      }
    },
    fail: (err) => {
      uni.hideLoading()
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
    uni.showLoading({
      title: '发送中...'
    })
    
    await userApi.sendCode(phone.value)
    
    uni.hideLoading()
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
    uni.hideLoading()
    console.error('发送验证码失败', error)
    uni.showToast({
      title: '发送失败，请重试',
      icon: 'none'
    })
  }
}

// 手机号登录
const handlePhoneLogin = async () => {
  if (!agreeTerms.value) {
    uni.showToast({
      title: '请先同意用户协议和隐私政策',
      icon: 'none',
      duration: 2000
    })
    return
  }

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
    uni.showLoading({
      title: '登录中...'
    })
    
    const result = await userApi.phoneLogin({
      phone: phone.value,
      code: code.value
    })
    
    // 保存token和用户信息
    uni.setStorageSync('token', result.data.token)
    uni.setStorageSync('userInfo', result.data.userInfo)
    
    uni.hideLoading()
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
    uni.hideLoading()
    console.error('登录失败', error)
    uni.showToast({
      title: '登录失败，请重试',
      icon: 'none'
    })
  }
}

const handleAgreeChange = (e) => {
  agreeTerms.value = e.detail.value.length > 0
}

const handleProtocol = (type) => {
  const urlMap = {
    'user': '/pages/protocol/user',
    'privacy': '/pages/protocol/privacy'
  }
  
  uni.navigateTo({
    url: urlMap[type]
  })
}
</script>

<style lang="scss" scoped>
.page {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(180deg, #FFFBF8 0%, #FEFEFE 100%);
  padding: var(--spacing-xxl) var(--spacing-xl);
  overflow: hidden;
}

/* 装饰背景 - 更温馨的设计 */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  z-index: 0;
}

.circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.08;
}

.circle-1 {
  width: 500rpx;
  height: 500rpx;
  background: var(--primary-color);
  top: -150rpx;
  right: -150rpx;
}

.circle-2 {
  width: 400rpx;
  height: 400rpx;
  background: var(--primary-color);
  bottom: 150rpx;
  left: -100rpx;
}

.circle-3 {
  width: 300rpx;
  height: 300rpx;
  background: var(--primary-color);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

/* Logo 区域 */
.logo-section {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: var(--spacing-xxl);
}

.logo-wrapper {
  width: 180rpx;
  height: 180rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-light) 100%);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-lg);
  margin-bottom: var(--spacing-lg);
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-12rpx);
  }
}

.logo-emoji {
  font-size: 100rpx;
}

.app-name {
  font-size: 52rpx;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: var(--spacing-sm);
  letter-spacing: -0.02em;
}

.app-slogan {
  font-size: 28rpx;
  color: var(--text-secondary);
  letter-spacing: 2rpx;
}

/* 登录卡片 - 温馨暖色调 */
.login-card {
  position: relative;
  z-index: 1;
  background: var(--bg-white);
  border-radius: var(--radius-xl);
  padding: var(--spacing-xxl) var(--spacing-xl);
  box-shadow: var(--shadow-lg);
  border: 1rpx solid var(--border-color);
}

.login-tabs {
  display: flex;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xxl);
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-lg);
  background: var(--bg-gray);
  border-radius: var(--radius-lg);
  border: 2rpx solid transparent;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &.active {
    background: var(--primary-bg);
    border-color: var(--primary-color);
    box-shadow: var(--shadow-sm);

    .tab-icon {
      transform: scale(1.1);
    }

    .tab-text {
      color: var(--primary-color);
      font-weight: 600;
    }
  }
  
  &:active {
    transform: scale(0.98);
  }
}

.tab-icon {
  font-size: 44rpx;
  transition: all 0.3s ease;
}

.tab-text {
  font-size: 28rpx;
  color: var(--text-secondary);
  transition: all 0.3s ease;
}

/* 微信登录 */
.wechat-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-xl);
  margin-bottom: var(--spacing-xl);
}

.info-icon {
  font-size: 40rpx;
}

.info-text {
  font-size: 28rpx;
  color: var(--text-secondary);
}

.btn-wechat {
  width: 100%;
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-md);
  background: #09BB07;
  border-radius: var(--radius-full);
  border: none;
  box-shadow: var(--shadow-sm);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &:active {
    transform: scale(0.98);
    background: #07A600;
  }
}

.btn-wechat .btn-icon {
  font-size: 48rpx;
}

.btn-wechat .btn-text {
  font-size: 34rpx;
  font-weight: 500;
  color: #fff;
}

/* 手机登录 */
.form-group {
  margin-bottom: var(--spacing-xl);
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  background: var(--bg-gray);
  border-radius: var(--radius-lg);
  border: 2rpx solid transparent;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &:focus-within {
    background: var(--bg-white);
    border-color: var(--primary-color);
    box-shadow: var(--shadow-sm);
  }
}

.input-icon {
  font-size: 40rpx;
  flex-shrink: 0;
}

.form-input {
  flex: 1;
  font-size: 30rpx;
  color: var(--text-primary);
}

.input-placeholder {
  color: var(--text-light);
}

.code-input {
  padding-right: 200rpx;
}

.btn-code {
  position: absolute;
  right: var(--spacing-md);
  padding: var(--spacing-sm) var(--spacing-lg);
  font-size: 26rpx;
  color: var(--primary-color);
  background: var(--primary-bg);
  border-radius: var(--radius-full);
  border: none;
  font-weight: 500;
  transition: all 0.3s ease;

  &.disabled {
    color: var(--text-light);
    background: var(--bg-gray);
  }
  
  &:active {
    transform: scale(0.95);
  }
}

.btn-login {
  width: 100%;
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-color);
  border-radius: var(--radius-full);
  border: none;
  box-shadow: var(--shadow-sm);
  margin-top: var(--spacing-xl);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &:active {
    transform: scale(0.98);
    background: var(--primary-dark);
  }
}

.btn-login .btn-text {
  font-size: 34rpx;
  font-weight: 500;
  color: #fff;
}

/* 协议 */
.agreement {
  margin-top: var(--spacing-xl);
}

.agreement-label {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-sm);
}

.agreement-checkbox {
  margin-top: 6rpx;
  transform: scale(0.9);
}

.agreement-text {
  flex: 1;
  font-size: 24rpx;
  color: var(--text-secondary);
  line-height: 1.7;
}

.agreement-link {
  color: var(--primary-color);
  font-weight: 500;
}
</style>
