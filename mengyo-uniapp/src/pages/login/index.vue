<template>
  <view class="page">
    <!-- 装饰背景 -->
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

    <!-- 登录表单卡片 -->
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
          <view class="input-wrapper" :class="{ focused: phoneFocused }">
            <text class="input-icon">📱</text>
            <input 
              v-model="phone" 
              type="number" 
              placeholder="请输入手机号" 
              maxlength="11"
              class="form-input"
              placeholder-class="input-placeholder"
              @focus="phoneFocused = true"
              @blur="phoneFocused = false"
            />
          </view>
        </view>
        
        <view class="form-group">
          <view class="input-wrapper" :class="{ focused: codeFocused }">
            <text class="input-icon">🔐</text>
            <input 
              v-model="code" 
              type="number" 
              placeholder="请输入验证码" 
              maxlength="6"
              class="form-input code-input"
              placeholder-class="input-placeholder"
              @focus="codeFocused = true"
              @blur="codeFocused = false"
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
        <label class="agreement-label" @click="toggleAgree">
          <view class="checkbox-wrapper">
            <view class="checkbox" :class="{ checked: agreeTerms }">
              <text v-if="agreeTerms" class="check-icon">✓</text>
            </view>
          </view>
          <text class="agreement-text">
            我已阅读并同意
            <text class="agreement-link" @click.stop="handleProtocol('user')">《用户协议》</text>
            和
            <text class="agreement-link" @click.stop="handleProtocol('privacy')">《隐私政策》</text>
          </text>
        </label>
      </view>
    </view>

    <!-- 其他登录方式 -->
    <view class="other-login">
      <view class="divider-wrapper">
        <view class="divider-line"></view>
        <text class="divider-text">其他登录方式</text>
        <view class="divider-line"></view>
      </view>
      
      <view class="other-methods">
        <view class="method-item" @click="handleAppleLogin">
          <view class="method-icon apple">
            <text class="icon-symbol"></text>
          </view>
          <text class="method-name">Apple</text>
        </view>
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
const phoneFocused = ref(false)
const codeFocused = ref(false)

// 切换协议同意状态
const toggleAgree = () => {
  agreeTerms.value = !agreeTerms.value
}

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

const handleProtocol = (type) => {
  const urlMap = {
    'user': '/pages/protocol/user',
    'privacy': '/pages/protocol/privacy'
  }
  
  uni.showToast({
    title: '协议页面开发中',
    icon: 'none'
  })
}

const handleAppleLogin = () => {
  uni.showToast({
    title: '暂未开放',
    icon: 'none'
  })
}
</script>

<style lang="scss" scoped>
.page {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(180deg, #FFFBF5 0%, #FEF7F0 100%);
  padding: 60rpx 40rpx;
  padding-top: calc(env(safe-area-inset-top) + 60rpx);
  overflow: hidden;
}

/* ===================================
   装饰背景
   =================================== */

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
  opacity: 0.1;
  animation: float 8s ease-in-out infinite;
}

.circle-1 {
  width: 400rpx;
  height: 400rpx;
  background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
  top: -100rpx;
  right: -100rpx;
  animation-delay: 0s;
}

.circle-2 {
  width: 300rpx;
  height: 300rpx;
  background: linear-gradient(135deg, #FFB88C 0%, #FF9D5C 100%);
  bottom: 100rpx;
  left: -80rpx;
  animation-delay: 2s;
}

.circle-3 {
  width: 200rpx;
  height: 200rpx;
  background: linear-gradient(135deg, #FFDCC5 0%, #FFB88C 100%);
  top: 50%;
  right: 40rpx;
  animation-delay: 4s;
}

/* ===================================
   Logo 区域
   =================================== */

.logo-section {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 80rpx;
}

.logo-wrapper {
  width: 160rpx;
  height: 160rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
  border-radius: 40rpx;
  box-shadow: 0 16rpx 48rpx rgba(255, 140, 66, 0.25);
  margin-bottom: 32rpx;
  animation: bounce 2s infinite;
}

.logo-emoji {
  font-size: 88rpx;
}

.app-name {
  font-size: 48rpx;
  font-weight: 700;
  color: #57534E;
  margin-bottom: 16rpx;
}

.app-slogan {
  font-size: 26rpx;
  color: #78716C;
  letter-spacing: 2rpx;
}

/* ===================================
   登录卡片
   =================================== */

.login-card {
  position: relative;
  z-index: 1;
  background: linear-gradient(to bottom right, #FFFFFF 0%, #FEF7F0 100%);
  border-radius: 40rpx;
  padding: 48rpx 40rpx;
  border: 1rpx solid #F5F5F4;
  box-shadow: 0 16rpx 64rpx rgba(255, 140, 66, 0.12);
  margin-bottom: 40rpx;
}

.login-tabs {
  display: flex;
  gap: 20rpx;
  margin-bottom: 48rpx;
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  padding: 28rpx;
  background: #FEF7F0;
  border-radius: 24rpx;
  border: 3rpx solid transparent;
  transition: all 0.3s ease;

  &.active {
    background: linear-gradient(135deg, #FFE5D9 0%, #FFDCC5 100%);
    border-color: #FF8C42;
    transform: scale(1.05);

    .tab-icon {
      transform: scale(1.2);
    }

    .tab-text {
      color: #FF8C42;
      font-weight: 700;
    }
  }
  
  &:active {
    transform: scale(0.95);
  }
}

.tab-icon {
  font-size: 44rpx;
  transition: all 0.3s ease;
}

.tab-text {
  font-size: 26rpx;
  color: #78716C;
  transition: all 0.3s ease;
}

/* 微信登录 */
.wechat-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  padding: 32rpx;
  margin-bottom: 32rpx;
}

.info-icon {
  font-size: 36rpx;
}

.info-text {
  font-size: 26rpx;
  color: #78716C;
}

.btn-wechat {
  width: 100%;
  height: 96rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  background: linear-gradient(135deg, #09BB07 0%, #07A600 100%);
  border-radius: 24rpx;
  border: none;
  box-shadow: 0 8rpx 24rpx rgba(9, 187, 7, 0.25);
  transition: all 0.3s ease;

  &:active {
    transform: scale(0.98);
  }
}

.btn-wechat .btn-icon {
  font-size: 44rpx;
}

.btn-wechat .btn-text {
  font-size: 32rpx;
  font-weight: 600;
  color: #FFFFFF;
}

/* 手机登录 */
.form-group {
  margin-bottom: 32rpx;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 24rpx;
  background: #FEF7F0;
  border-radius: 24rpx;
  border: 2rpx solid transparent;
  transition: all 0.3s ease;

  &.focused {
    background: #FFFFFF;
    border-color: #FF8C42;
    box-shadow: 0 4rpx 20rpx rgba(255, 140, 66, 0.15);
  }
}

.input-icon {
  font-size: 36rpx;
}

.form-input {
  flex: 1;
  font-size: 28rpx;
  color: #57534E;
}

.input-placeholder {
  color: #D6D3D1;
}

.code-input {
  padding-right: 180rpx;
}

.btn-code {
  position: absolute;
  right: 20rpx;
  padding: 16rpx 28rpx;
  font-size: 24rpx;
  color: #FFFFFF;
  background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
  border-radius: 20rpx;
  border: none;
  font-weight: 600;
  transition: all 0.3s ease;

  &.disabled {
    color: #A8A29E;
    background: #E7E5E4;
  }
  
  &:active {
    transform: scale(0.95);
  }
}

.btn-login {
  width: 100%;
  height: 96rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
  border-radius: 24rpx;
  border: none;
  box-shadow: 0 8rpx 24rpx rgba(255, 140, 66, 0.25);
  margin-top: 40rpx;
  transition: all 0.3s ease;

  &:active {
    transform: scale(0.98);
  }
}

.btn-login .btn-text {
  font-size: 32rpx;
  font-weight: 600;
  color: #FFFFFF;
}

/* 协议 */
.agreement {
  margin-top: 32rpx;
}

.agreement-label {
  display: flex;
  align-items: flex-start;
  gap: 12rpx;
}

.checkbox-wrapper {
  padding-top: 4rpx;
}

.checkbox {
  width: 36rpx;
  height: 36rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #FEF7F0;
  border: 2rpx solid #E7E5E4;
  border-radius: 8rpx;
  transition: all 0.3s ease;
  
  &.checked {
    background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
    border-color: #FF8C42;
  }
}

.check-icon {
  font-size: 24rpx;
  color: #FFFFFF;
  font-weight: bold;
}

.agreement-text {
  flex: 1;
  font-size: 22rpx;
  color: #78716C;
  line-height: 1.7;
}

.agreement-link {
  color: #FF8C42;
  font-weight: 600;
}

/* ===================================
   其他登录方式
   =================================== */

.other-login {
  position: relative;
  z-index: 1;
}

.divider-wrapper {
  display: flex;
  align-items: center;
  gap: 20rpx;
  margin-bottom: 40rpx;
}

.divider-line {
  flex: 1;
  height: 2rpx;
  background: #E7E5E4;
}

.divider-text {
  font-size: 24rpx;
  color: #A8A29E;
}

.other-methods {
  display: flex;
  justify-content: center;
  gap: 60rpx;
}

.method-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.95);
  }
}

.method-icon {
  width: 88rpx;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
  
  &.apple {
    background: linear-gradient(135deg, #57534E 0%, #44403C 100%);
  }
}

.icon-symbol {
  font-size: 44rpx;
  color: #FFFFFF;
}

.method-name {
  font-size: 24rpx;
  color: #78716C;
  font-weight: 500;
}
</style>
