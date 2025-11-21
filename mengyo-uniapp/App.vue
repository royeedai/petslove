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
/* 全局样式 */
:root {
  --primary-color: #FF8C42;
  --primary-gradient-start: #FF9D5C;
  --primary-gradient-end: #FF7F29;
  --text-primary: #2C3E50;
  --text-secondary: #7F8C8D;
  --text-light: #95A5A6;
  --bg-page: #F8F9FA;
  --bg-white: #FFFFFF;
  --border-color: #E8EAED;
  --shadow-light: rgba(255, 140, 66, 0.1);
  --shadow-normal: rgba(255, 140, 66, 0.15);
}

page {
  background-color: var(--bg-page);
  font-size: 14px;
  color: var(--text-primary);
}

/* 全局容器 */
.container {
  padding: 20rpx;
}

/* 全局按钮 */
.btn-primary {
  background: linear-gradient(135deg, var(--primary-gradient-start) 0%, var(--primary-gradient-end) 100%);
  color: #fff;
  border-radius: 16rpx;
  padding: 24rpx 48rpx;
  text-align: center;
  font-weight: 500;
  box-shadow: 0 8rpx 24rpx var(--shadow-light);
  transition: all 0.3s ease;
}

.btn-secondary {
  background-color: var(--bg-white);
  color: var(--text-primary);
  border: 2rpx solid var(--border-color);
  border-radius: 16rpx;
  padding: 24rpx 48rpx;
  text-align: center;
  font-weight: 400;
  transition: all 0.3s ease;
}

/* 全局卡片样式 */
.card {
  background: var(--bg-white);
  border-radius: 20rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.04);
  margin-bottom: 20rpx;
}

/* 全局标题 */
.title {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 20rpx;
}

/* 全局副标题 */
.subtitle {
  font-size: 28rpx;
  color: var(--text-secondary);
  line-height: 1.6;
}
</style>
