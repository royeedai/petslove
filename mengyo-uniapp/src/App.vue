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
/* ===================================
   萌友益站 - 温暖治愈风格全局样式
   采用 Style B: Warm & Cozy 设计规范
   =================================== */

:root {
  /* 主色调 - 温暖橙色系 */
  --primary-color: #FF8C42;
  --primary-light: #FFB88C;
  --primary-lighter: #FFDCC5;
  --primary-gradient-start: #FF9D5C;
  --primary-gradient-end: #FF7F29;
  
  /* 文字颜色 - 深褐色系 */
  --text-primary: #57534E;  /* stone-800 深褐色 */
  --text-secondary: #78716C; /* stone-500 */
  --text-tertiary: #A8A29E; /* stone-400 */
  --text-light: #D6D3D1; /* stone-300 */
  
  /* 背景色 - 暖白米色系 */
  --bg-page: #FFFBF5; /* 暖白/米色 */
  --bg-white: #FFFFFF;
  --bg-card: #FFFFFF;
  --bg-secondary: #FEF7F0; /* 浅米色 */
  
  /* 边框和分割线 */
  --border-color: #F5F5F4; /* stone-100 */
  --border-dark: #E7E5E4; /* stone-200 */
  
  /* 阴影 */
  --shadow-sm: 0 2rpx 8rpx rgba(255, 140, 66, 0.06);
  --shadow-md: 0 4rpx 16rpx rgba(255, 140, 66, 0.08);
  --shadow-lg: 0 8rpx 32rpx rgba(255, 140, 66, 0.12);
  --shadow-xl: 0 16rpx 48rpx rgba(255, 140, 66, 0.15);
  
  /* 圆角 - 超大圆角设计 */
  --radius-sm: 16rpx;
  --radius-md: 24rpx;
  --radius-lg: 32rpx;
  --radius-xl: 40rpx;
  --radius-full: 9999rpx;
  
  /* 辅助色 */
  --success-color: #22C55E;
  --warning-color: #F59E0B;
  --error-color: #EF4444;
  --info-color: #3B82F6;
}

/* 页面基础样式 */
page {
  background-color: var(--bg-page);
  font-size: 28rpx;
  color: var(--text-primary);
  line-height: 1.6;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

/* 全局容器 */
.container {
  padding: 24rpx;
}

/* ===================================
   全局按钮样式
   =================================== */

/* 主按钮 - 橙色渐变 */
.btn-primary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  background: linear-gradient(135deg, var(--primary-gradient-start) 0%, var(--primary-gradient-end) 100%);
  color: #fff;
  border-radius: var(--radius-xl);
  padding: 28rpx 56rpx;
  font-size: 30rpx;
  font-weight: 600;
  box-shadow: var(--shadow-lg);
  border: none;
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.96);
    box-shadow: var(--shadow-md);
  }
}

/* 次要按钮 - 白色描边 */
.btn-secondary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  background-color: var(--bg-white);
  color: var(--text-primary);
  border: 3rpx solid var(--border-dark);
  border-radius: var(--radius-xl);
  padding: 28rpx 56rpx;
  font-size: 30rpx;
  font-weight: 500;
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.96);
    background-color: var(--bg-secondary);
  }
}

/* 轻量按钮 - 浅色背景 */
.btn-light {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  background: linear-gradient(135deg, #FFE5D9 0%, #FFDCC5 100%);
  color: var(--primary-color);
  border-radius: var(--radius-xl);
  padding: 20rpx 40rpx;
  font-size: 26rpx;
  font-weight: 500;
  border: none;
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.96);
  }
}

/* ===================================
   全局卡片样式
   =================================== */

/* 基础卡片 - 白色带柔和渐变 */
.card {
  background: linear-gradient(to bottom right, #FFFFFF 0%, #FEF7F0 100%);
  border-radius: var(--radius-lg);
  padding: 32rpx;
  box-shadow: var(--shadow-md);
  border: 1rpx solid var(--border-color);
  margin-bottom: 24rpx;
  transition: all 0.3s ease;
  
  &:active {
    transform: translateY(-4rpx);
    box-shadow: var(--shadow-lg);
  }
}

/* 浮动卡片 - 更强阴影 */
.card-elevated {
  background: var(--bg-white);
  border-radius: var(--radius-lg);
  padding: 32rpx;
  box-shadow: var(--shadow-lg);
  border: none;
  margin-bottom: 24rpx;
}

/* 柔和卡片 - 米色背景 */
.card-soft {
  background: linear-gradient(135deg, #FEF7F0 0%, #FFF5EB 100%);
  border-radius: var(--radius-lg);
  padding: 32rpx;
  box-shadow: var(--shadow-sm);
  border: 1rpx solid var(--border-color);
  margin-bottom: 24rpx;
}

/* ===================================
   全局文字样式
   =================================== */

/* 主标题 */
.title {
  font-size: 36rpx;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 16rpx;
  line-height: 1.3;
}

/* 副标题 */
.subtitle {
  font-size: 28rpx;
  font-weight: 500;
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: 12rpx;
}

/* 说明文字 */
.description {
  font-size: 26rpx;
  color: var(--text-tertiary);
  line-height: 1.7;
}

/* ===================================
   全局徽章和标签
   =================================== */

/* 状态徽章 */
.badge {
  display: inline-flex;
  align-items: center;
  gap: 8rpx;
  padding: 8rpx 20rpx;
  border-radius: var(--radius-full);
  font-size: 22rpx;
  font-weight: 500;
}

.badge-primary {
  background: linear-gradient(135deg, #FFE5D9 0%, #FFDCC5 100%);
  color: var(--primary-color);
}

.badge-success {
  background: linear-gradient(135deg, #D1FAE5 0%, #A7F3D0 100%);
  color: #059669;
}

.badge-warning {
  background: linear-gradient(135deg, #FEF3C7 0%, #FDE68A 100%);
  color: #D97706;
}

.badge-error {
  background: linear-gradient(135deg, #FEE2E2 0%, #FECACA 100%);
  color: #DC2626;
}

/* ===================================
   全局分割线
   =================================== */

.divider {
  height: 1rpx;
  background: var(--border-color);
  margin: 24rpx 0;
}

.divider-thick {
  height: 2rpx;
  background: var(--border-dark);
  margin: 32rpx 0;
}

/* ===================================
   全局动画
   =================================== */

/* 淡入动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 弹跳动画 */
@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-12rpx);
  }
}

/* 脉冲动画 */
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

/* 摇摆动画 */
@keyframes shake {
  0%, 100% {
    transform: translateX(0);
  }
  25% {
    transform: translateX(-8rpx);
  }
  75% {
    transform: translateX(8rpx);
  }
}

/* ===================================
   全局空状态
   =================================== */

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 40rpx;
  text-align: center;
}

.empty-icon {
  font-size: 120rpx;
  opacity: 0.25;
  margin-bottom: 24rpx;
}

.empty-title {
  font-size: 28rpx;
  font-weight: 500;
  color: var(--text-secondary);
  margin-bottom: 12rpx;
}

.empty-description {
  font-size: 24rpx;
  color: var(--text-tertiary);
  line-height: 1.6;
}

/* ===================================
   全局加载状态
   =================================== */

.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 80rpx 0;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid var(--border-color);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* ===================================
   工具类
   =================================== */

/* 文字截断 */
.text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.text-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.text-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* Flex 布局 */
.flex {
  display: flex;
}

.flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
}

.flex-between {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* 间距 */
.gap-xs {
  gap: 8rpx;
}

.gap-sm {
  gap: 12rpx;
}

.gap-md {
  gap: 16rpx;
}

.gap-lg {
  gap: 24rpx;
}

/* ===================================
   响应式适配
   =================================== */

/* 安全区域适配 */
.safe-area-inset-bottom {
  padding-bottom: env(safe-area-inset-bottom);
}

.safe-area-inset-top {
  padding-top: env(safe-area-inset-top);
}
</style>
