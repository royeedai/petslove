<template>
  <view class="page-container">
    <!-- Hero Section -->
    <view class="hero">
      <image class="hero-bg" :src="currentBanner.image" mode="aspectFill"></image>
      <view class="hero-overlay"></view>
      <view class="hero-content">
        <text class="hero-title">{{ currentBanner.title }}</text>
        <text class="hero-subtitle">{{ currentBanner.subtitle }}</text>
      </view>
    </view>

    <!-- Main CTA -->
    <view class="main-actions">
      <view class="action-card adopt" @click="navTo('/pages/animal/list?type=adopt')">
        <view class="action-text">
          <text class="action-title">领养动物</text>
          <text class="action-desc">带它回家</text>
        </view>
        <view class="action-icon-bg">🐶</view>
      </view>
      <view class="action-card sponsor" @click="navTo('/pages/animal/list?type=sponsor')">
        <view class="action-text">
          <text class="action-title">云助养</text>
          <text class="action-desc">爱心投喂</text>
        </view>
        <view class="action-icon-bg">🧡</view>
      </view>
    </view>

    <!-- Quick Entry -->
    <view class="section-entries">
      <view class="entry-item" @click="navTo('/pages/animal/list')">
        <view class="entry-icon">🐾</view>
        <text>全部萌宠</text>
      </view>
      <view class="entry-item" @click="navTo('/pages/rescue/list')">
        <view class="entry-icon">📖</view>
        <text>救助故事</text>
      </view>
      <view class="entry-item" @click="navTo('/pages/user/index')">
        <view class="entry-icon">🛡️</view>
        <text>志愿者</text>
      </view>
    </view>

    <!-- Transparency Data -->
    <view class="data-section">
      <view class="section-header">
        <text class="title">公益透明化</text>
        <text class="more">查看报告 ></text>
      </view>
      <view class="data-grid">
        <view class="data-item">
          <text class="number">{{ stats.rescued }}</text>
          <text class="label">累计救助</text>
        </view>
        <view class="data-item">
          <text class="number">{{ stats.adopted }}</text>
          <text class="label">成功领养</text>
        </view>
        <view class="data-item">
          <text class="number">¥{{ (stats.sponsored / 1000).toFixed(1) }}k</text>
          <text class="label">助养基金</text>
        </view>
      </view>
    </view>

    <!-- Featured Pets -->
    <view class="featured-section">
      <view class="section-header">
        <text class="title">等待回家的它们</text>
      </view>
      <scroll-view scroll-x class="featured-scroll">
        <view class="featured-card" v-for="pet in featuredPets" :key="pet.id" @click="navTo('/pages/animal/detail?id=' + pet.id)">
          <image :src="pet.image" mode="aspectFill" class="card-img"></image>
          <view class="card-info">
            <text class="name">{{ pet.name }}</text>
            <text class="tags">{{ pet.age }} · {{ pet.breed }}</text>
          </view>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue';
import { banners, stats, pets } from '@/utils/mockData.js';

const currentBanner = computed(() => banners[0]);
const featuredPets = computed(() => pets.slice(0, 3));

const navTo = (url) => {
  uni.navigateTo({ url });
};
</script>

<style lang="scss" scoped>
.page-container {
  padding-bottom: 40px;
}

.hero {
  position: relative;
  width: 100%;
  height: 260px;
  
  .hero-bg {
    width: 100%;
    height: 100%;
  }
  
  .hero-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(to bottom, rgba(0,0,0,0.1), rgba(0,0,0,0.4));
  }
  
  .hero-content {
    position: absolute;
    bottom: 40px;
    left: 20px;
    color: $color-white;
    z-index: 2;
    
    .hero-title {
      font-size: 28px;
      font-weight: bold;
      display: block;
      margin-bottom: 8px;
      text-shadow: 0 2px 4px rgba(0,0,0,0.2);
    }
    
    .hero-subtitle {
      font-size: 16px;
      opacity: 0.9;
    }
  }
}

.main-actions {
  display: flex;
  padding: 0 20px;
  margin-top: -30px;
  position: relative;
  z-index: 3;
  gap: 15px;
  
  .action-card {
    flex: 1;
    background: $color-white;
    border-radius: 16px;
    padding: 20px;
    box-shadow: 0 8px 20px rgba(0,0,0,0.08);
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: relative;
    overflow: hidden;
    
    &.adopt {
      background: linear-gradient(135deg, #ffffff 0%, #FFF7ED 100%);
      .action-title { color: $color-primary; }
    }
    
    &.sponsor {
      background: linear-gradient(135deg, #ffffff 0%, #F0FDF4 100%);
      .action-title { color: #059669; } // Custom Green
      .action-icon-bg { background: rgba(5, 150, 105, 0.1); color: #059669; }
    }

    .action-text {
      z-index: 2;
      .action-title {
        font-size: 18px;
        font-weight: bold;
        display: block;
        margin-bottom: 4px;
      }
      .action-desc {
        font-size: 12px;
        color: $color-text-sub;
      }
    }
    
    .action-icon-bg {
      width: 40px;
      height: 40px;
      background: rgba(246, 169, 91, 0.1);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 20px;
    }
  }
}

.section-entries {
  display: flex;
  justify-content: space-around;
  padding: 30px 20px;
  
  .entry-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    
    .entry-icon {
      width: 50px;
      height: 50px;
      background: $color-white;
      border-radius: 16px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      box-shadow: $shadow-sm;
    }
    
    text {
      font-size: 13px;
      color: $color-text-main;
      font-weight: 500;
    }
  }
}

.data-section {
  margin: 0 20px;
  background: $color-white;
  border-radius: $radius-card;
  padding: 20px;
  box-shadow: $shadow-card;
  
  .section-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20px;
    
    .title {
      font-size: 16px;
      font-weight: bold;
      color: $color-text-main;
    }
    .more {
      font-size: 12px;
      color: $color-text-light;
    }
  }
  
  .data-grid {
    display: flex;
    justify-content: space-between;
    
    .data-item {
      text-align: center;
      
      .number {
        display: block;
        font-size: 20px;
        font-weight: 800;
        color: $color-primary;
        margin-bottom: 4px;
      }
      .label {
        font-size: 12px;
        color: $color-text-sub;
      }
    }
  }
}

.featured-section {
  padding: 30px 20px;
  
  .section-header {
    margin-bottom: 15px;
    .title {
      font-size: 18px;
      font-weight: bold;
      color: $color-text-main;
    }
  }
  
  .featured-scroll {
    white-space: nowrap;
    width: 100%;
    
    .featured-card {
      display: inline-block;
      width: 140px;
      margin-right: 15px;
      background: $color-white;
      border-radius: $radius-card;
      overflow: hidden;
      box-shadow: $shadow-sm;
      
      .card-img {
        width: 140px;
        height: 140px;
      }
      
      .card-info {
        padding: 10px;
        
        .name {
          display: block;
          font-size: 14px;
          font-weight: bold;
          color: $color-text-main;
        }
        .tags {
          font-size: 11px;
          color: $color-text-sub;
        }
      }
    }
  }
}
</style>
