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
/* 全局样式 - Pinterest/Airbnb 温馨风格 */
:root {
  /* 主色调 - 暖色系（参考 Airbnb 的珊瑚红/暖橙） */
  --primary-color: #FF5A5F;
  --primary-dark: #E04A4F;
  --primary-light: #FF7A7F;
  --primary-hover: #FF6B70;
  --primary-bg: #FFF5F5;
  
  /* 辅助色 - 温馨暖色调 */
  --success-color: #00A699;
  --warning-color: #FCB900;
  --danger-color: #FF5A5F;
  --info-color: #00A699;
  
  /* 文字颜色 - 柔和深色 */
  --text-primary: #2C2C2C;
  --text-secondary: #717171;
  --text-light: #B0B0B0;
  --text-disabled: #D0D0D0;
  
  /* 背景色 - 暖白色系（参考 Airbnb 的米白色） */
  --bg-page: #FEFEFE;
  --bg-white: #FFFFFF;
  --bg-gray: #F7F7F7;
  --bg-warm: #FFFBF8;
  --bg-card: #FFFFFF;
  
  /* 边框和分割线 - 更柔和的颜色 */
  --border-color: #EBEBEB;
  --divider-color: #F0F0F0;
  
  /* 阴影 - 更柔和、更轻的阴影（Airbnb 风格） */
  --shadow-sm: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
  --shadow-md: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
  --shadow-lg: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
  --shadow-card: 0 2rpx 16rpx rgba(0, 0, 0, 0.05);
  
  /* 圆角 - 更大的圆角（Pinterest/Airbnb 风格） */
  --radius-sm: 12rpx;
  --radius-md: 16rpx;
  --radius-lg: 24rpx;
  --radius-xl: 32rpx;
  --radius-full: 9999rpx;
  
  /* 间距 - 更大的留白（Airbnb 风格） */
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

/* 全局容器 - 增加留白 */
.container {
  padding: var(--spacing-lg);
}

/* 全局按钮 - 更圆润的设计 */
.btn-primary {
  background: var(--primary-color);
  color: #fff;
  border-radius: var(--radius-full);
  padding: 20rpx 48rpx;
  text-align: center;
  font-weight: 500;
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
  border-radius: var(--radius-full);
  padding: 20rpx 48rpx;
  text-align: center;
  font-weight: 400;
  font-size: 30rpx;
  transition: all 0.3s ease;
  
  &:active {
    background: var(--bg-gray);
    border-color: var(--primary-color);
  }
}

/* 全局卡片样式 - Pinterest 风格 */
.card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-card);
  margin-bottom: var(--spacing-md);
  border: 1rpx solid var(--border-color);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:active {
    transform: translateY(-2rpx);
    box-shadow: var(--shadow-md);
  }
}

/* 全局标题 - 更好的行高和间距 */
.title {
  font-size: 40rpx;
  font-weight: 600;
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

/* 图片加载错误处理 - 更温馨的设计 */
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
</style>
