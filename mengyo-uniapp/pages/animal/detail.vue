<template>
  <view class="page-container" v-if="pet">
    <!-- Header Image -->
    <view class="image-header">
      <image :src="pet.image" mode="aspectFill" class="header-img"></image>
      <view class="back-btn" @click="goBack">←</view>
    </view>

    <view class="content-body">
      <!-- Basic Info -->
      <view class="info-card main">
        <view class="header-row">
          <text class="name">{{ pet.name }}</text>
          <view class="status-tag" :class="pet.status">{{ getStatusText(pet.status) }}</view>
        </view>
        <view class="meta-row">
          <text>{{ pet.breed }}</text>
          <text class="dot">·</text>
          <text>{{ pet.age }}</text>
          <text class="dot">·</text>
          <text>{{ pet.gender }}</text>
          <text class="dot">·</text>
          <text>📍 {{ pet.distance }}</text>
        </view>
      </view>

      <!-- Story & Desc -->
      <view class="info-card">
        <view class="section-title">关于它</view>
        <text class="desc-text">{{ pet.description }}</text>
        <view class="story-box">
          <text class="story-title">它的故事</text>
          <text class="story-text">{{ pet.story }}</text>
        </view>
      </view>

      <!-- Health -->
      <view class="info-card">
        <view class="section-title">健康状况</view>
        <view class="tags-wrap">
          <view class="health-tag" v-for="(tag, idx) in pet.tags" :key="idx">✅ {{ tag }}</view>
        </view>
        <text class="health-detail">{{ pet.health }}</text>
      </view>

      <!-- Sponsorship (Transparency) -->
      <view class="info-card sponsor-card" v-if="pet.status !== 'adopted'">
        <view class="section-title">助养透明化</view>
        <view class="progress-box">
          <view class="progress-labels">
            <text>已筹集 ¥{{ pet.sponsorship.current }}</text>
            <text>目标 ¥{{ pet.sponsorship.target }}</text>
          </view>
          <view class="progress-track">
            <view class="progress-bar" :style="{ width: (pet.sponsorship.current / pet.sponsorship.target * 100) + '%' }"></view>
          </view>
        </view>

        <view class="cost-list">
          <view class="cost-item" v-for="(item, idx) in pet.sponsorship.items" :key="idx">
            <text class="item-name">{{ item.name }}</text>
            <text class="item-cost">¥{{ item.cost }}/月</text>
          </view>
        </view>
      </view>
    </view>

    <!-- Sticky Footer -->
    <view class="footer-action">
      <view class="icon-btn">
        <text class="icon">❤️</text>
        <text class="label">收藏</text>
      </view>
      <view class="icon-btn">
        <text class="icon">📤</text>
        <text class="label">分享</text>
      </view>
      <button class="main-btn" @click="handleAction">
        {{ pet.status === 'adoptable' ? '申请领养' : '我要助养' }}
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { pets } from '@/utils/mockData.js';

const pet = ref(null);

onLoad((option) => {
  const id = parseInt(option.id);
  pet.value = pets.find(p => p.id === id) || pets[0];
});

const goBack = () => {
  uni.navigateBack();
};

const getStatusText = (status) => {
  const map = { 'adoptable': '待领养', 'sponsored': '助养中', 'adopted': '已领养' };
  return map[status];
};

const handleAction = () => {
  // Navigate to success page for demo
  uni.navigateTo({ url: '/pages/animal/success' });
};
</script>

<style lang="scss" scoped>
.page-container {
  padding-bottom: 100px;
  background: $color-bg;
  min-height: 100vh;
}

.image-header {
  position: relative;
  width: 100%;
  height: 300px;
  
  .header-img {
    width: 100%;
    height: 100%;
  }
  
  .back-btn {
    position: absolute;
    top: 40px;
    left: 20px;
    width: 36px;
    height: 36px;
    background: rgba(255,255,255,0.9);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: bold;
    z-index: 10;
    box-shadow: $shadow-sm;
  }
}

.content-body {
  position: relative;
  top: -20px;
  padding: 0 20px;
  
  .info-card {
    background: $color-white;
    border-radius: $radius-card;
    padding: 20px;
    margin-bottom: 16px;
    box-shadow: $shadow-card;
    
    &.main {
      padding-top: 24px;
    }
    
    .section-title {
      font-size: 17px;
      font-weight: bold;
      margin-bottom: 12px;
      color: $color-text-main;
    }
  }
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  
  .name {
    font-size: 24px;
    font-weight: 800;
    color: $color-text-main;
  }
  
  .status-tag {
    padding: 4px 12px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: bold;
    
    &.adoptable { background: rgba($color-primary, 0.1); color: $color-primary; }
    &.sponsored { background: rgba($color-success, 0.1); color: $color-success; }
    &.adopted { background: #F3F4F6; color: $color-text-light; }
  }
}

.meta-row {
  font-size: 14px;
  color: $color-text-sub;
  display: flex;
  align-items: center;
  gap: 6px;
  
  .dot { color: $color-text-light; }
}

.desc-text {
  font-size: 15px;
  line-height: 1.6;
  color: $color-text-sub;
  display: block;
  margin-bottom: 16px;
}

.story-box {
  background: #F9FAFB;
  padding: 16px;
  border-radius: 8px;
  
  .story-title {
    font-size: 13px;
    font-weight: bold;
    color: $color-text-main;
    display: block;
    margin-bottom: 4px;
  }
  
  .story-text {
    font-size: 13px;
    color: $color-text-sub;
    line-height: 1.5;
  }
}

.tags-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
  
  .health-tag {
    font-size: 13px;
    padding: 6px 12px;
    background: rgba(5, 150, 105, 0.05);
    color: #059669;
    border-radius: 6px;
  }
}

.health-detail {
  font-size: 13px;
  color: $color-text-sub;
}

.sponsor-card {
  border: 1px solid rgba($color-primary, 0.2);
  
  .progress-box {
    margin-bottom: 20px;
    
    .progress-labels {
      display: flex;
      justify-content: space-between;
      font-size: 12px;
      color: $color-text-sub;
      margin-bottom: 8px;
    }
    
    .progress-track {
      height: 8px;
      background: #F3F4F6;
      border-radius: 4px;
      overflow: hidden;
      
      .progress-bar {
        height: 100%;
        background: $color-primary;
        border-radius: 4px;
      }
    }
  }
  
  .cost-list {
    .cost-item {
      display: flex;
      justify-content: space-between;
      padding: 10px 0;
      border-bottom: 1px dashed #E5E7EB;
      font-size: 14px;
      
      &:last-child { border-bottom: none; }
      
      .item-name { color: $color-text-sub; }
      .item-cost { font-weight: bold; color: $color-text-main; }
    }
  }
}

.footer-action {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background: $color-white;
  padding: 16px 20px;
  box-shadow: 0 -4px 16px rgba(0,0,0,0.05);
  display: flex;
  align-items: center;
  gap: 20px;
  padding-bottom: calc(16px + env(safe-area-inset-bottom));
  
  .icon-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    
    .icon { font-size: 20px; }
    .label { font-size: 10px; color: $color-text-sub; }
  }
  
  .main-btn {
    flex: 1;
    background: $color-primary;
    color: white;
    border-radius: $radius-btn;
    font-size: 16px;
    font-weight: bold;
    border: none;
    height: 44px;
    line-height: 44px;
    
    &:active { opacity: 0.9; }
  }
}
</style>
