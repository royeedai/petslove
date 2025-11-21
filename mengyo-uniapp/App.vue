<script setup>
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'

onLaunch(() => {
  console.log('App Launch')
})

onShow(() => {
  console.log('App Show')
})

onHide(() => {
  console.log('App Hide')
})
</script>

<style lang="scss">
/* 全局样式 - Petfinder风格 */
:root {
  /* 主色调 - Petfinder风格 */
  --primary-color: #FF5A5F;
  --primary-dark: #E04A4F;
  --primary-light: #FF7A7F;
  --primary-hover: #FF6B70;
  --primary-bg: #FFF5F5;
  
  /* 辅助色 */
  --success-color: #00A699;
  --warning-color: #FCB900;
  --danger-color: #FF5A5F;
  --info-color: #00A699;
  
  /* 文字颜色 */
  --text-primary: #2C2C2C;
  --text-secondary: #666;
  --text-light: #999;
  --text-disabled: #B0B0B0;
  
  /* 背景色 */
  --bg-page: #F5F5F5;
  --bg-white: #FFFFFF;
  --bg-gray: #F5F5F5;
  --bg-warm: #FFFBF8;
  --bg-card: #FFFFFF;
  
  /* 边框和分割线 */
  --border-color: #EEEEEE;
  --divider-color: #F0F0F0;
  
  /* 阴影 - Petfinder风格 */
  --shadow-sm: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
  --shadow-md: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
  --shadow-lg: 0 8rpx 32rpx rgba(0, 0, 0, 0.12);
  --shadow-card: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
  
  /* 圆角 */
  --radius-sm: 12rpx;
  --radius-md: 16rpx;
  --radius-lg: 24rpx;
  --radius-xl: 32rpx;
  --radius-full: 9999rpx;
  
  /* 间距 */
  --spacing-xs: 8rpx;
  --spacing-sm: 16rpx;
  --spacing-md: 24rpx;
  --spacing-lg: 32rpx;
  --spacing-xl: 48rpx;
  --spacing-xxl: 64rpx;
}

page {
  background-color: var(--bg-page);
  font-size: 15px;
  color: var(--text-primary);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', sans-serif;
  line-height: 1.6;
  letter-spacing: 0.01em;
}

/* 全局容器 */
.container {
  padding: var(--spacing-lg);
}

/* 全局按钮 */
.btn-primary {
  background: var(--primary-color);
  color: #fff;
  border-radius: var(--radius-md);
  padding: 24rpx 48rpx;
  text-align: center;
  font-weight: 600;
  font-size: 30rpx;
  border: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: var(--shadow-sm);
  
  &:active {
    background: var(--primary-dark);
    transform: scale(0.98);
    box-shadow: var(--shadow-md);
  }
}

.btn-secondary {
  background-color: var(--bg-white);
  color: var(--text-primary);
  border: 2rpx solid var(--border-color);
  border-radius: var(--radius-md);
  padding: 24rpx 48rpx;
  text-align: center;
  font-weight: 500;
  font-size: 30rpx;
  transition: all 0.3s ease;
  
  &:active {
    background: var(--bg-gray);
    border-color: var(--primary-color);
  }
}

/* 全局卡片样式 */
.card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-card);
  margin-bottom: var(--spacing-md);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:active {
    transform: translateY(-2rpx);
    box-shadow: var(--shadow-md);
  }
}

/* 全局标题 */
.title {
  font-size: 40rpx;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
  line-height: 1.5;
  letter-spacing: -0.02em;
}

/* 全局副标题 */
.subtitle {
  font-size: 30rpx;
  color: var(--text-secondary);
  line-height: 1.7;
  letter-spacing: 0.01em;
}

/* 图片加载错误处理 */
.image-error {
  background: var(--bg-warm);
  display: flex;
  align-items: center;
  justify-content: center;
  
  &::after {
    content: '🐾';
    font-size: 80rpx;
    opacity: 0.2;
  }
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
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

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-30rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(30rpx);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-30rpx);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.9;
  }
}

@keyframes heartBeat {
  0%, 100% {
    transform: scale(1);
  }
  25% {
    transform: scale(1.3);
  }
  50% {
    transform: scale(1.1);
  }
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10rpx);
  }
}

/* 过渡动画类 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-up-enter-from,
.slide-up-leave-to {
  opacity: 0;
  transform: translateY(30rpx);
}
</style>
