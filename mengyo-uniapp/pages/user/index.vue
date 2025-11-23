<template>
  <view class="container">
    <!-- User Card -->
    <view class="user-card">
      <image :src="user.avatar" mode="aspectFill" class="avatar"></image>
      <view class="user-info">
        <text class="name">{{ user.name }}</text>
        <text class="badge">🏅 爱心大使 Lv.3</text>
      </view>
    </view>

    <!-- Stats Row -->
    <view class="stats-row">
      <view class="stat-item">
        <text class="num">{{ user.stats.adopted }}</text>
        <text class="label">我的领养</text>
      </view>
      <view class="stat-item">
        <text class="num">{{ user.stats.sponsored }}</text>
        <text class="label">我的助养</text>
      </view>
      <view class="stat-item">
        <text class="num">{{ user.stats.donated }}</text>
        <text class="label">爱心值</text>
      </view>
    </view>

    <!-- Menu List -->
    <view class="menu-list">
      <view class="menu-item">
        <view class="left">
          <text class="icon">📅</text>
          <text>申请记录</text>
        </view>
        <text class="arrow">></text>
      </view>
      <view class="menu-item">
        <view class="left">
          <text class="icon">💌</text>
          <text>我的消息</text>
        </view>
        <view class="right">
          <view class="red-dot"></view>
          <text class="arrow">></text>
        </view>
      </view>
      <view class="menu-item">
        <view class="left">
          <text class="icon">📊</text>
          <text>公益透明报告</text>
        </view>
        <text class="arrow">></text>
      </view>
    </view>

    <view class="menu-list">
      <view class="menu-item">
        <view class="left">
          <text class="icon">⚙️</text>
          <text>设置</text>
        </view>
        <text class="arrow">></text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { user } from '@/utils/mockData.js';
</script>

<style lang="scss" scoped>
.container {
  padding: 20px;
}

.user-card {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  
  .avatar {
    width: 70px;
    height: 70px;
    border-radius: 50%;
    border: 3px solid $color-white;
    box-shadow: $shadow-sm;
    margin-right: 16px;
  }
  
  .user-info {
    display: flex;
    flex-direction: column;
    gap: 6px;
    
    .name {
      font-size: 20px;
      font-weight: bold;
      color: $color-text-main;
    }
    
    .badge {
      align-self: flex-start;
      background: #FEF3C7;
      color: #D97706;
      font-size: 11px;
      padding: 2px 8px;
      border-radius: 10px;
      font-weight: bold;
    }
  }
}

.stats-row {
  display: flex;
  justify-content: space-between;
  background: $color-white;
  padding: 20px;
  border-radius: $radius-card;
  box-shadow: $shadow-card;
  margin-bottom: 24px;
  
  .stat-item {
    flex: 1;
    text-align: center;
    border-right: 1px solid #F3F4F6;
    
    &:last-child { border-right: none; }
    
    .num {
      display: block;
      font-size: 18px;
      font-weight: 800;
      color: $color-text-main;
      margin-bottom: 4px;
    }
    .label {
      font-size: 12px;
      color: $color-text-sub;
    }
  }
}

.menu-list {
  background: $color-white;
  border-radius: $radius-card;
  box-shadow: $shadow-sm;
  overflow: hidden;
  margin-bottom: 20px;
  
  .menu-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 20px;
    border-bottom: 1px solid #F9FAFB;
    
    &:active { background: #F9FAFB; }
    
    &:last-child { border-bottom: none; }
    
    .left {
      display: flex;
      align-items: center;
      font-size: 15px;
      color: $color-text-main;
      
      .icon { margin-right: 12px; font-size: 18px; }
    }
    
    .right {
      display: flex;
      align-items: center;
    }
    
    .arrow {
      color: #D1D5DB;
      font-size: 14px;
    }
    
    .red-dot {
      width: 8px;
      height: 8px;
      background: $color-error;
      border-radius: 50%;
      margin-right: 8px;
    }
  }
}
</style>
