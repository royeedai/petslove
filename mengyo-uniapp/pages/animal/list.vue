<template>
  <view class="container">
    <!-- Filter Tabs -->
    <scroll-view scroll-x class="filter-bar">
      <view 
        v-for="cat in categories" 
        :key="cat.id" 
        class="filter-item" 
        :class="{ active: currentCategory === cat.id }"
        @click="currentCategory = cat.id"
      >
        <text class="icon">{{ cat.icon }}</text>
        <text>{{ cat.name }}</text>
      </view>
    </scroll-view>

    <!-- Sub Filter -->
    <view class="sub-filter">
      <view class="tag active">全部区域</view>
      <view class="tag">年龄</view>
      <view class="tag">性别</view>
    </view>

    <!-- Pet List -->
    <view class="pet-list">
      <view class="pet-card" v-for="pet in filteredPets" :key="pet.id" @click="navToDetail(pet.id)">
        <view class="card-image-box">
          <image :src="pet.image" mode="aspectFill" class="card-img"></image>
          <view class="status-badge" :class="pet.status">
            {{ getStatusText(pet.status) }}
          </view>
          <view class="distance-badge">📍 {{ pet.distance }}</view>
        </view>
        
        <view class="card-content">
          <view class="header">
            <text class="name">{{ pet.name }}</text>
            <view class="gender-icon" :class="pet.gender === '公' ? 'male' : 'female'">
              {{ pet.gender === '公' ? '♂' : '♀' }}
            </view>
          </view>
          
          <text class="info-text">{{ pet.breed }} · {{ pet.age }}</text>
          
          <view class="tags-row">
            <text class="mini-tag" v-for="(tag, idx) in pet.tags" :key="idx">{{ tag }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue';
import { categories, pets } from '@/utils/mockData.js';

const currentCategory = ref(4); // Default All

const filteredPets = computed(() => {
  // Mock filtering logic
  if (currentCategory.value === 4) return pets;
  // In real app, filter by type
  return pets;
});

const getStatusText = (status) => {
  const map = {
    'adoptable': '待领养',
    'sponsored': '已助养',
    'adopted': '已领养'
  };
  return map[status] || status;
};

const navToDetail = (id) => {
  uni.navigateTo({ url: `/pages/animal/detail?id=${id}` });
};
</script>

<style lang="scss" scoped>
.container {
  padding: 16px;
}

.filter-bar {
  white-space: nowrap;
  margin-bottom: 20px;
  
  .filter-item {
    display: inline-flex;
    align-items: center;
    padding: 8px 16px;
    background: $color-white;
    border-radius: 20px;
    margin-right: 12px;
    font-size: 14px;
    color: $color-text-sub;
    box-shadow: $shadow-sm;
    transition: all 0.3s;
    
    .icon {
      margin-right: 4px;
    }
    
    &.active {
      background: $color-primary;
      color: $color-white;
    }
  }
}

.sub-filter {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  
  .tag {
    font-size: 12px;
    padding: 4px 12px;
    background: rgba(0,0,0,0.05);
    color: $color-text-sub;
    border-radius: 4px;
    
    &.active {
      color: $color-primary;
      background: rgba(246, 169, 91, 0.1);
      font-weight: 500;
    }
  }
}

.pet-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.pet-card {
  background: $color-white;
  border-radius: $radius-card;
  overflow: hidden;
  box-shadow: $shadow-card;
  display: flex;
  flex-direction: column;
  
  .card-image-box {
    position: relative;
    width: 100%;
    padding-bottom: 133%; // 3:4 ratio for vertical cards or 75% for 4:3
    padding-bottom: 100%; // Square for grid
    height: 0;
    
    .card-img {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
    }
    
    .status-badge {
      position: absolute;
      top: 10px;
      left: 10px;
      padding: 4px 8px;
      border-radius: 4px;
      font-size: 10px;
      font-weight: bold;
      color: white;
      text-transform: uppercase;
      
      &.adoptable { background: rgba($color-primary, 0.9); }
      &.sponsored { background: rgba($color-success, 0.9); }
      &.adopted { background: rgba($color-text-light, 0.9); }
    }
    
    .distance-badge {
      position: absolute;
      bottom: 10px;
      right: 10px;
      background: rgba(0,0,0,0.5);
      color: white;
      font-size: 10px;
      padding: 2px 6px;
      border-radius: 4px;
      backdrop-filter: blur(4px);
    }
  }
  
  .card-content {
    padding: 12px;
    
    .header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 4px;
      
      .name {
        font-size: 16px;
        font-weight: bold;
        color: $color-text-main;
      }
      
      .gender-icon {
        font-size: 12px;
        &.male { color: #1890FF; }
        &.female { color: #FF4D4F; }
      }
    }
    
    .info-text {
      font-size: 12px;
      color: $color-text-sub;
      display: block;
      margin-bottom: 8px;
    }
    
    .tags-row {
      display: flex;
      flex-wrap: wrap;
      gap: 4px;
      
      .mini-tag {
        font-size: 10px;
        padding: 2px 6px;
        background: #F3F4F6;
        color: $color-text-light;
        border-radius: 4px;
      }
    }
  }
}
</style>
