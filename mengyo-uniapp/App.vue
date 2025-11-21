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
page {
  background-color: #f5f5f5;
  font-size: 14px;
}

/* 全局容器 */
.container {
  padding: 20rpx;
}

/* 全局按钮 */
.btn-primary {
  background-color: #3cc51f;
  color: #fff;
  border-radius: 8rpx;
  padding: 20rpx 40rpx;
  text-align: center;
}

.btn-secondary {
  background-color: #fff;
  color: #333;
  border: 2rpx solid #e5e5e5;
  border-radius: 8rpx;
  padding: 20rpx 40rpx;
  text-align: center;
}
</style>
