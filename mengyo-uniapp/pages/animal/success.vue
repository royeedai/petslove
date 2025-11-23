<template>
  <view class="success-page">
    <view class="content">
      <view class="icon-circle">
        🎉
      </view>
      <text class="title">感谢您的爱心</text>
      <text class="subtitle">您的申请已提交，我们将尽快与您联系。</text>
      
      <image class="illustration" src="https://images.unsplash.com/photo-1513360371669-4adf3dd7dff8?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80" mode="aspectFit"></image>
      
      <view class="btn-group">
        <button class="btn primary" @click="goHome">返回首页</button>
        <button class="btn outline">分享给朋友</button>
      </view>
    </view>
  </view>
</template>

<script setup>
const goHome = () => {
  uni.switchTab({ url: '/pages/index/index' });
};
</script>

<style lang="scss" scoped>
.success-page {
  height: 100vh;
  background: $color-primary;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.content {
  background: $color-white;
  width: 100%;
  border-radius: 24px;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  box-shadow: 0 20px 40px rgba(0,0,0,0.1);
  
  .icon-circle {
    width: 80px;
    height: 80px;
    background: rgba($color-primary, 0.1);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 40px;
    margin-bottom: 20px;
  }
  
  .title {
    font-size: 24px;
    font-weight: 800;
    color: $color-text-main;
    margin-bottom: 10px;
  }
  
  .subtitle {
    font-size: 14px;
    color: $color-text-sub;
    margin-bottom: 30px;
    max-width: 80%;
  }
  
  .illustration {
    width: 200px;
    height: 150px;
    margin-bottom: 30px;
    border-radius: 12px;
  }
  
  .btn-group {
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 12px;
    
    .btn {
      width: 100%;
      height: 48px;
      line-height: 48px;
      border-radius: $radius-btn;
      font-size: 16px;
      font-weight: bold;
      
      &.primary {
        background: $color-primary;
        color: white;
      }
      
      &.outline {
        background: transparent;
        border: 1px solid #E5E7EB;
        color: $color-text-sub;
      }
    }
  }
}
</style>
