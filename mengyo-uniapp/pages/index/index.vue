<template>
  <view class="page">
    <!-- 顶部搜索栏 - Petfinder风格 -->
    <view class="search-header">
      <view class="search-bar" @click="handleSearch">
        <text class="search-icon">🔍</text>
        <text class="search-placeholder">搜索宠物、品种或位置...</text>
      </view>
      <view class="filter-btn" @click="showFilter = true">
        <text class="filter-icon">⚙️</text>
      </view>
    </view>

    <!-- 快速分类 - Petfinder风格 -->
    <scroll-view scroll-x class="category-scroll" :show-scrollbar="false">
      <view class="category-list">
        <view 
          v-for="(item, index) in categories" 
          :key="index"
          class="category-item"
          :class="{ active: currentCategory === item.value }"
          @click="handleCategoryChange(item.value)"
        >
          <view class="category-icon-wrapper" :class="item.value">
            <text class="category-icon">{{ item.icon }}</text>
          </view>
          <text class="category-text">{{ item.label }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 可领养动物展示 - Petfinder风格卡片 -->
    <view class="animals-section">
      <view class="section-header">
        <view class="header-left">
          <text class="section-title">寻找你的新伙伴</text>
          <text class="section-subtitle">{{ totalAnimals }} 只宠物等待领养</text>
        </view>
        <view class="view-all-btn" @click="navigateTo('/pages/animal/list')">
          <text class="view-all-text">查看全部</text>
          <text class="view-all-arrow">›</text>
        </view>
      </view>

      <view class="animals-grid" v-if="animalList.length > 0">
        <view 
          v-for="(item, index) in animalList" 
          :key="item.id"
          class="animal-card"
          :style="{ animationDelay: `${index * 0.1}s` }"
          @click="handleAnimalDetail(item)"
        >
          <view class="animal-image-wrapper">
            <image 
              :src="getAnimalImage(item)" 
              mode="aspectFill" 
              class="animal-image"
              @error="handleImageError($event, item)"
            ></image>
            
            <!-- 状态徽章 -->
            <view class="status-badge" :class="getStatusClass(item.status)">
              <text class="status-dot"></text>
              <text class="status-text">{{ getStatusText(item.status) }}</text>
            </view>

            <!-- 收藏按钮 -->
            <view class="favorite-btn" @click.stop="handleFavorite(item)">
              <text class="favorite-icon" :class="{ active: item.isFavorite }">
                {{ item.isFavorite ? '❤️' : '🤍' }}
              </text>
            </view>
          </view>

          <view class="animal-info">
            <view class="animal-name-row">
              <text class="animal-name">{{ item.name || '待命名' }}</text>
              <view class="gender-badge" :class="item.gender">
                {{ item.gender === 'MALE' ? '♂' : '♀' }}
              </view>
            </view>

            <view class="animal-details">
              <view class="detail-item">
                <text class="detail-label">品种</text>
                <text class="detail-value">{{ item.breed || '未知' }}</text>
              </view>
              <view class="detail-item">
                <text class="detail-label">年龄</text>
                <text class="detail-value">{{ item.ageEstimate || '未知' }}</text>
              </view>
            </view>

            <view class="location-row">
              <text class="location-icon">📍</text>
              <text class="location-text">{{ getLocationText(item) }}</text>
            </view>

            <view class="action-footer">
              <button class="btn-adopt" @click.stop="handleAdopt(item)">
                <text class="btn-text">立即领养</text>
              </button>
            </view>
          </view>
        </view>
      </view>

      <view v-else class="empty-state">
        <text class="empty-icon">🐾</text>
        <text class="empty-text">暂无可领养宠物</text>
        <text class="empty-desc">敬请期待更多小可爱</text>
      </view>
    </view>

    <!-- 筛选弹窗 -->
    <view v-if="showFilter" class="filter-modal" @click="showFilter = false">
      <view class="filter-content" @click.stop>
        <view class="filter-header">
          <text class="filter-title">筛选条件</text>
          <text class="filter-close" @click="showFilter = false">✕</text>
        </view>
        
        <view class="filter-body">
          <view class="filter-group">
            <text class="filter-group-title">动物类型</text>
            <view class="filter-options">
              <view 
                v-for="type in animalTypes" 
                :key="type.value"
                class="filter-option"
                :class="{ active: filterParams.animalType === type.value }"
                @click="filterParams.animalType = type.value"
              >
                {{ type.label }}
              </view>
            </view>
          </view>

          <view class="filter-group">
            <text class="filter-group-title">健康状况</text>
            <view class="filter-options">
              <view 
                v-for="health in healthStatuses" 
                :key="health.value"
                class="filter-option"
                :class="{ active: filterParams.healthStatus === health.value }"
                @click="filterParams.healthStatus = health.value"
              >
                {{ health.label }}
              </view>
            </view>
          </view>

          <view class="filter-group">
            <text class="filter-group-title">体型</text>
            <view class="filter-options">
              <view 
                v-for="size in sizes" 
                :key="size.value"
                class="filter-option"
                :class="{ active: filterParams.size === size.value }"
                @click="filterParams.size = size.value"
              >
                {{ size.label }}
              </view>
            </view>
          </view>
        </view>

        <view class="filter-actions">
          <button class="btn-reset" @click="handleResetFilter">重置</button>
          <button class="btn-apply" @click="handleApplyFilter">应用筛选</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { rescueApi } from '@/utils/api'

const currentCategory = ref('all')
const showFilter = ref(false)
const totalAnimals = ref(0)
const animalList = ref([])

const filterParams = ref({
  animalType: 'all',
  healthStatus: 'all',
  size: 'all'
})

const categories = ref([
  { label: '全部', value: 'all', icon: '🐾' },
  { label: '狗狗', value: 'DOG', icon: '🐕' },
  { label: '猫咪', value: 'CAT', icon: '🐈' },
  { label: '其他', value: 'OTHER', icon: '🦊' }
])

const animalTypes = ref([
  { label: '全部', value: 'all' },
  { label: '狗', value: 'DOG' },
  { label: '猫', value: 'CAT' },
  { label: '其他', value: 'OTHER' }
])

const healthStatuses = ref([
  { label: '全部', value: 'all' },
  { label: '健康', value: 'GOOD' },
  { label: '受伤', value: 'INJURED' },
  { label: '生病', value: 'SICK' }
])

const sizes = ref([
  { label: '全部', value: 'all' },
  { label: '小型', value: 'SMALL' },
  { label: '中型', value: 'MEDIUM' },
  { label: '大型', value: 'LARGE' }
])

onMounted(() => {
  loadAnimals()
})

const loadAnimals = async () => {
  try {
    const params = {
      page: 1,
      size: 6,
      status: 'RESCUED'
    }
    
    if (currentCategory.value !== 'all') {
      params.animalType = currentCategory.value
    }
    
    const res = await rescueApi.getAnimalList(params)
    if (res.data && res.data.records) {
      animalList.value = res.data.records.map(item => ({
        ...item,
        isFavorite: false,
        images: item.images ? (typeof item.images === 'string' ? JSON.parse(item.images) : item.images) : []
      }))
      totalAnimals.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载动物列表失败', error)
  }
}

const handleCategoryChange = (value) => {
  currentCategory.value = value
  loadAnimals()
}

const getAnimalImage = (item) => {
  if (item.images && item.images.length > 0) {
    return item.images[0]
  }
  const defaultImages = {
    'DOG': 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=600&q=80',
    'CAT': 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=600&q=80',
    'OTHER': 'https://images.unsplash.com/photo-1425082661705-1834bfd09dca?w=600&q=80'
  }
  return defaultImages[item.animalType] || defaultImages['OTHER']
}

const handleImageError = (e, item) => {
  const defaultImages = {
    'DOG': 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=600&q=80',
    'CAT': 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=600&q=80',
    'OTHER': 'https://images.unsplash.com/photo-1425082661705-1834bfd09dca?w=600&q=80'
  }
  if (item.images && item.images.length > 0) {
    item.images[0] = defaultImages[item.animalType] || defaultImages['OTHER']
  }
}

const getLocationText = (item) => {
  if (item.locationCity && item.locationDistrict) {
    return `${item.locationCity} ${item.locationDistrict}`
  }
  if (item.locationCity) {
    return item.locationCity
  }
  return '位置待确认'
}

const getStatusText = (status) => {
  const statusMap = {
    'REPORTED': '已报备',
    'RESCUING': '救助中',
    'RESCUED': '可领养',
    'ADOPTED': '已领养',
    'DEAD': '已死亡'
  }
  return statusMap[status] || '未知'
}

const getStatusClass = (status) => {
  const classMap = {
    'REPORTED': 'reported',
    'RESCUING': 'rescuing',
    'RESCUED': 'rescued',
    'ADOPTED': 'adopted',
    'DEAD': 'dead'
  }
  return classMap[status] || 'reported'
}

const handleAnimalDetail = (item) => {
  uni.navigateTo({
    url: `/pages/animal/detail?id=${item.id}`
  })
}

const handleFavorite = (item) => {
  item.isFavorite = !item.isFavorite
  uni.showToast({
    title: item.isFavorite ? '已收藏' : '已取消收藏',
    icon: 'success',
    duration: 1500
  })
}

const handleAdopt = (item) => {
  if (item.status === 'RESCUED') {
    uni.navigateTo({
      url: `/pages/adoption/apply?animalId=${item.id}`
    })
  } else {
    uni.showToast({
      title: '该宠物暂不可领养',
      icon: 'none'
    })
  }
}

const handleSearch = () => {
  uni.navigateTo({
    url: '/pages/animal/list'
  })
}

const handleResetFilter = () => {
  filterParams.value = {
    animalType: 'all',
    healthStatus: 'all',
    size: 'all'
  }
}

const handleApplyFilter = () => {
  showFilter.value = false
  loadAnimals()
}

const navigateTo = (url) => {
  uni.navigateTo({ url })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #F5F5F5;
  padding-bottom: 40rpx;
}

/* 搜索栏 - Petfinder风格 */
.search-header {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 24rpx 32rpx;
  background: #FFFFFF;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.search-bar {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 20rpx 32rpx;
  background: #F5F5F5;
  border-radius: 50rpx;
  transition: all 0.3s ease;
  
  &:active {
    background: #EEEEEE;
  }
}

.search-icon {
  font-size: 32rpx;
  color: #666;
}

.search-placeholder {
  flex: 1;
  font-size: 28rpx;
  color: #999;
}

.filter-btn {
  width: 88rpx;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #FF5A5F;
  border-radius: 50%;
  box-shadow: 0 4rpx 12rpx rgba(255, 90, 95, 0.3);
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.95);
    background: #E04A4F;
  }
}

.filter-icon {
  font-size: 36rpx;
  color: #FFFFFF;
}

/* 分类滚动 - Petfinder风格 */
.category-scroll {
  white-space: nowrap;
  background: #FFFFFF;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #EEEEEE;
}

.category-list {
  display: inline-flex;
  gap: 24rpx;
  padding: 0 32rpx;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  padding: 16rpx 24rpx;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &.active {
    .category-icon-wrapper {
      background: #FF5A5F;
      transform: scale(1.1);
      box-shadow: 0 4rpx 16rpx rgba(255, 90, 95, 0.3);
    }
    
    .category-text {
      color: #FF5A5F;
      font-weight: 600;
    }
  }
  
  &:active {
    transform: scale(0.95);
  }
}

.category-icon-wrapper {
  width: 96rpx;
  height: 96rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F5F5F5;
  border-radius: 24rpx;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.category-icon {
  font-size: 48rpx;
}

.category-text {
  font-size: 24rpx;
  color: #666;
  white-space: nowrap;
}

/* 动物展示区域 */
.animals-section {
  padding: 32rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32rpx;
}

.header-left {
  flex: 1;
}

.section-title {
  display: block;
  font-size: 40rpx;
  font-weight: 700;
  color: #2C2C2C;
  margin-bottom: 8rpx;
  letter-spacing: -0.02em;
}

.section-subtitle {
  display: block;
  font-size: 26rpx;
  color: #999;
}

.view-all-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 24rpx;
  transition: all 0.3s ease;
  
  &:active {
    opacity: 0.7;
  }
}

.view-all-text {
  font-size: 28rpx;
  color: #FF5A5F;
  font-weight: 500;
}

.view-all-arrow {
  font-size: 32rpx;
  color: #FF5A5F;
  font-weight: 300;
}

/* 动物网格 - Petfinder风格 */
.animals-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24rpx;
}

.animal-card {
  background: #FFFFFF;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  animation: fadeInUp 0.6s ease-out both;
  
  &:active {
    transform: translateY(-4rpx);
    box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.12);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animal-image-wrapper {
  position: relative;
  width: 100%;
  height: 360rpx;
  overflow: hidden;
}

.animal-image {
  width: 100%;
  height: 100%;
  background: #F5F5F5;
  transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.animal-card:active .animal-image {
  transform: scale(1.1);
}

.status-badge {
  position: absolute;
  top: 16rpx;
  left: 16rpx;
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 8rpx 20rpx;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10rpx);
  border-radius: 50rpx;
  font-size: 22rpx;
  font-weight: 500;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
  
  &.rescued {
    .status-dot {
      background: #00A699;
    }
    .status-text {
      color: #00A699;
    }
  }
  
  &.rescuing {
    .status-dot {
      background: #FCB900;
    }
    .status-text {
      color: #FCB900;
    }
  }
  
  &.adopted {
    .status-dot {
      background: #999;
    }
    .status-text {
      color: #999;
    }
  }
}

.status-dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: #00A699;
}

.status-text {
  font-size: 22rpx;
  color: #00A699;
}

.favorite-btn {
  position: absolute;
  top: 16rpx;
  right: 16rpx;
  width: 72rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10rpx);
  border-radius: 50%;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.9);
  }
}

.favorite-icon {
  font-size: 36rpx;
  transition: all 0.3s ease;
  
  &.active {
    animation: heartBeat 0.5s ease;
  }
}

@keyframes heartBeat {
  0%, 100% {
    transform: scale(1);
  }
  25% {
    transform: scale(1.3);
  }
  50% {
    transform: scale(1.1);
  }
}

.animal-info {
  padding: 24rpx;
}

.animal-name-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16rpx;
}

.animal-name {
  flex: 1;
  font-size: 32rpx;
  font-weight: 600;
  color: #2C2C2C;
  letter-spacing: -0.01em;
}

.gender-badge {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: 24rpx;
  font-weight: 600;
  flex-shrink: 0;
  
  &.MALE {
    background: #E3F2FD;
    color: #1976D2;
  }
  
  &.FEMALE {
    background: #FCE4EC;
    color: #C2185B;
  }
}

.animal-details {
  display: flex;
  gap: 24rpx;
  margin-bottom: 16rpx;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.detail-label {
  font-size: 24rpx;
  color: #999;
}

.detail-value {
  font-size: 26rpx;
  color: #666;
  font-weight: 500;
}

.location-row {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-bottom: 20rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.location-icon {
  font-size: 28rpx;
  flex-shrink: 0;
}

.location-text {
  flex: 1;
  font-size: 26rpx;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.action-footer {
  margin-top: 8rpx;
}

.btn-adopt {
  width: 100%;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #FF5A5F;
  color: #FFFFFF;
  border-radius: 16rpx;
  border: none;
  font-size: 28rpx;
  font-weight: 600;
  box-shadow: 0 4rpx 12rpx rgba(255, 90, 95, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:active {
    background: #E04A4F;
    transform: scale(0.98);
    box-shadow: 0 2rpx 8rpx rgba(255, 90, 95, 0.2);
  }
}

.btn-text {
  font-size: 28rpx;
  font-weight: 600;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 0;
  gap: 24rpx;
}

.empty-icon {
  font-size: 120rpx;
  opacity: 0.3;
}

.empty-text {
  font-size: 32rpx;
  color: #666;
  font-weight: 500;
}

.empty-desc {
  font-size: 26rpx;
  color: #999;
}

/* 筛选弹窗 */
.filter-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  z-index: 999;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.filter-content {
  width: 100%;
  max-height: 80vh;
  background: #FFFFFF;
  border-radius: 32rpx 32rpx 0 0;
  padding: 48rpx 32rpx;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 48rpx;
  padding-bottom: 24rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.filter-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #2C2C2C;
}

.filter-close {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  color: #999;
  border-radius: 50%;
  transition: all 0.3s ease;
  
  &:active {
    background: #F5F5F5;
  }
}

.filter-body {
  max-height: 60vh;
  overflow-y: auto;
}

.filter-group {
  margin-bottom: 48rpx;
}

.filter-group-title {
  display: block;
  font-size: 30rpx;
  font-weight: 600;
  color: #2C2C2C;
  margin-bottom: 24rpx;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.filter-option {
  padding: 16rpx 32rpx;
  background: #F5F5F5;
  border-radius: 50rpx;
  font-size: 28rpx;
  color: #666;
  border: 2rpx solid transparent;
  transition: all 0.3s ease;
  
  &.active {
    background: #FF5A5F;
    color: #FFFFFF;
    border-color: #FF5A5F;
    box-shadow: 0 2rpx 8rpx rgba(255, 90, 95, 0.3);
  }
  
  &:active {
    transform: scale(0.95);
  }
}

.filter-actions {
  display: flex;
  gap: 24rpx;
  margin-top: 48rpx;
  padding-top: 32rpx;
  border-top: 1rpx solid #F0F0F0;
}

.btn-reset,
.btn-apply {
  flex: 1;
  height: 96rpx;
  border-radius: 16rpx;
  font-size: 32rpx;
  font-weight: 600;
  border: none;
  transition: all 0.3s ease;
}

.btn-reset {
  background: #F5F5F5;
  color: #666;
  
  &:active {
    background: #EEEEEE;
  }
}

.btn-apply {
  background: #FF5A5F;
  color: #FFFFFF;
  box-shadow: 0 4rpx 12rpx rgba(255, 90, 95, 0.3);
  
  &:active {
    background: #E04A4F;
    transform: scale(0.98);
  }
}
</style>
