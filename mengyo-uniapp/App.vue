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
/* 全局样式 - Petfinder 风格 */
:root {
  /* 主色调 - 专业蓝色系 */
  --primary-color: #0066CC;
  --primary-dark: #004C99;
  --primary-light: #3399FF;
  --primary-hover: #0052A3;
  
  /* 辅助色 */
  --success-color: #28A745;
  --warning-color: #FFC107;
  --danger-color: #DC3545;
  --info-color: #17A2B8;
  
  /* 文字颜色 */
  --text-primary: #1A1A1A;
  --text-secondary: #666666;
  --text-light: #999999;
  --text-disabled: #CCCCCC;
  
  /* 背景色 */
  --bg-page: #F5F5F5;
  --bg-white: #FFFFFF;
  --bg-gray: #FAFAFA;
  
  /* 边框和分割线 */
  --border-color: #E0E0E0;
  --divider-color: #E8E8E8;
  
  /* 阴影 */
  --shadow-sm: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
  --shadow-md: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
  --shadow-lg: 0 8rpx 24rpx rgba(0, 0, 0, 0.12);
  
  /* 圆角 */
  --radius-sm: 8rpx;
  --radius-md: 12rpx;
  --radius-lg: 16rpx;
  --radius-xl: 24rpx;
}

page {
  background-color: var(--bg-page);
  font-size: 14px;
  color: var(--text-primary);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

/* 全局容器 */
.container {
  padding: 24rpx;
}

/* 全局按钮 */
.btn-primary {
  background: var(--primary-color);
  color: #fff;
  border-radius: var(--radius-md);
  padding: 24rpx 48rpx;
  text-align: center;
  font-weight: 500;
  font-size: 28rpx;
  border: none;
  transition: all 0.3s ease;
  
  &:active {
    background: var(--primary-dark);
    transform: scale(0.98);
  }
}

.btn-secondary {
  background-color: var(--bg-white);
  color: var(--text-primary);
  border: 2rpx solid var(--border-color);
  border-radius: var(--radius-md);
  padding: 24rpx 48rpx;
  text-align: center;
  font-weight: 400;
  font-size: 28rpx;
  transition: all 0.3s ease;
  
  &:active {
    background: var(--bg-gray);
  }
}

/* 全局卡片样式 */
.card {
  background: var(--bg-white);
  border-radius: var(--radius-lg);
  padding: 32rpx;
  box-shadow: var(--shadow-sm);
  margin-bottom: 24rpx;
  border: 1rpx solid var(--border-color);
}

/* 全局标题 */
.title {
  font-size: 36rpx;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 24rpx;
  line-height: 1.4;
}

/* 全局副标题 */
.subtitle {
  font-size: 28rpx;
  color: var(--text-secondary);
  line-height: 1.6;
}

/* 图片加载错误处理 */
.image-error {
  background: var(--bg-gray);
  display: flex;
  align-items: center;
  justify-content: center;
  
  &::after {
    content: '🐾';
    font-size: 60rpx;
    opacity: 0.3;
  }
}
</style>
