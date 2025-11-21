<template>
  <view class="page">
    <!-- 图片轮播 - Petfinder风格 -->
    <view class="image-section">
      <swiper 
        :indicator-dots="true" 
        :autoplay="false" 
        :circular="true"
        indicator-color="rgba(255, 255, 255, 0.5)"
        indicator-active-color="#FFFFFF"
        class="image-swiper"
      >
        <swiper-item v-for="(img, index) in animalImages" :key="index">
          <image 
            :src="img" 
            mode="aspectFill" 
            class="swiper-image"
            @error="handleImageError($event, index)"
            @click="previewImages(index)"
          ></image>
        </swiper-item>
      </swiper>
      
      <!-- 收藏按钮 -->
      <view class="favorite-btn-header" @click="handleFavorite">
        <text class="favorite-icon" :class="{ active: isFavorite }">
          {{ isFavorite ? '❤️' : '🤍' }}
        </text>
      </view>
    </view>

    <!-- 基本信息卡片 - Petfinder风格 -->
    <view class="info-card">
      <view class="name-section">
        <view class="name-row">
          <text class="animal-name">{{ animalInfo.name || '待命名' }}</text>
          <view class="gender-badge" :class="animalInfo.gender">
            {{ animalInfo.gender === 'MALE' ? '♂' : '♀' }}
          </view>
        </view>
        <view class="status-badge-large" :class="getStatusClass(animalInfo.status)">
          <text class="status-dot"></text>
          <text class="status-text">{{ getStatusText(animalInfo.status) }}</text>
        </view>
      </view>

      <view class="details-grid">
        <view class="detail-item">
          <text class="detail-label">品种</text>
          <text class="detail-value">{{ animalInfo.breed || '未知' }}</text>
        </view>
        <view class="detail-item">
          <text class="detail-label">年龄</text>
          <text class="detail-value">{{ animalInfo.ageEstimate || '未知' }}</text>
        </view>
        <view class="detail-item">
          <text class="detail-label">体型</text>
          <text class="detail-value">{{ getSizeText(animalInfo.size) }}</text>
        </view>
        <view class="detail-item">
          <text class="detail-label">健康状况</text>
          <text class="detail-value">{{ getHealthText(animalInfo.healthStatus) }}</text>
        </view>
      </view>

      <view class="location-section">
        <text class="location-icon">📍</text>
        <view class="location-info">
          <text class="location-text">{{ getLocationText() }}</text>
          <text class="location-address">{{ animalInfo.locationAddress || '' }}</text>
        </view>
      </view>
    </view>

    <!-- 描述信息 -->
    <view class="description-card" v-if="animalInfo.description">
      <text class="card-title">关于我</text>
      <text class="description-text">{{ animalInfo.description }}</text>
    </view>

    <!-- 详细信息 -->
    <view class="details-card">
      <text class="card-title">详细信息</text>
      <view class="detail-list">
        <view class="detail-row">
          <text class="row-label">颜色</text>
          <text class="row-value">{{ animalInfo.color || '未知' }}</text>
        </view>
        <view class="detail-row">
          <text class="row-label">发现时间</text>
          <text class="row-value">{{ formatDate(animalInfo.createTime) }}</text>
        </view>
        <view class="detail-row">
          <text class="row-label">报备人</text>
          <text class="row-value">{{ animalInfo.reporterName || '匿名' }}</text>
        </view>
      </view>
    </view>

    <!-- 行动按钮 - Petfinder风格，特别突出 -->
    <view class="action-bar">
      <button 
        v-if="animalInfo.status === 'RESCUED'"
        class="btn-adopt-primary"
        @click="handleAdopt"
      >
        <text class="btn-icon">🏠</text>
        <text class="btn-text">立即领养</text>
      </button>
      <button 
        v-else-if="animalInfo.status === 'RESCUING'"
        class="btn-sponsor"
        @click="handleSponsor"
      >
        <text class="btn-icon">💝</text>
        <text class="btn-text">资助救助</text>
      </button>
      <button 
        v-else
        class="btn-disabled"
        disabled
      >
        <text class="btn-text">暂不可领养</text>
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { rescueApi } from '@/utils/api'

const animalId = ref('')
const animalInfo = ref({})
const animalImages = ref([])
const isFavorite = ref(false)

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  animalId.value = currentPage.options.id || ''
  
  if (animalId.value) {
    loadAnimalDetail()
  }
})

const loadAnimalDetail = async () => {
  try {
    uni.showLoading({ title: '加载中...' })
    const res = await rescueApi.getAnimalDetail(animalId.value)
    if (res.data) {
      animalInfo.value = res.data
      
      // 处理图片
      if (res.data.images) {
        if (typeof res.data.images === 'string') {
          try {
            animalImages.value = JSON.parse(res.data.images)
          } catch (e) {
            animalImages.value = [res.data.images]
          }
        } else {
          animalImages.value = res.data.images
        }
      }
      
      // 如果没有图片，使用默认图片
      if (animalImages.value.length === 0) {
        const defaultImages = {
          'DOG': 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=800&q=80',
          'CAT': 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=800&q=80',
          'OTHER': 'https://images.unsplash.com/photo-1425082661705-1834bfd09dca?w=800&q=80'
        }
        animalImages.value = [defaultImages[res.data.animalType] || defaultImages['OTHER']]
      }
    }
  } catch (error) {
    console.error('加载动物详情失败', error)
    uni.showToast({
      title: '加载失败，请重试',
      icon: 'none'
    })
  } finally {
    uni.hideLoading()
  }
}

const handleImageError = (e, index) => {
  const defaultImages = {
    'DOG': 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=800&q=80',
    'CAT': 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=800&q=80',
    'OTHER': 'https://images.unsplash.com/photo-1425082661705-1834bfd09dca?w=800&q=80'
  }
  animalImages.value[index] = defaultImages[animalInfo.value.animalType] || defaultImages['OTHER']
}

const previewImages = (current) => {
  uni.previewImage({
    urls: animalImages.value,
    current: current
  })
}

const handleFavorite = () => {
  isFavorite.value = !isFavorite.value
  uni.showToast({
    title: isFavorite.value ? '已收藏' : '已取消收藏',
    icon: 'success',
    duration: 1500
  })
}

const handleAdopt = () => {
  if (animalInfo.value.status === 'RESCUED') {
    uni.navigateTo({
      url: `/pages/adoption/apply?animalId=${animalInfo.value.id}`
    })
  } else {
    uni.showToast({
      title: '该宠物暂不可领养',
      icon: 'none'
    })
  }
}

const handleSponsor = () => {
  uni.navigateTo({
    url: `/pages/donation/index?animalId=${animalInfo.value.id}`
  })
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

const getSizeText = (size) => {
  const sizeMap = {
    'SMALL': '小型',
    'MEDIUM': '中型',
    'LARGE': '大型'
  }
  return sizeMap[size] || '未知'
}

const getHealthText = (health) => {
  const healthMap = {
    'GOOD': '健康',
    'INJURED': '受伤',
    'SICK': '生病'
  }
  return healthMap[health] || '未知'
}

const getLocationText = () => {
  const parts = []
  if (animalInfo.value.locationProvince) parts.push(animalInfo.value.locationProvince)
  if (animalInfo.value.locationCity) parts.push(animalInfo.value.locationCity)
  if (animalInfo.value.locationDistrict) parts.push(animalInfo.value.locationDistrict)
  return parts.length > 0 ? parts.join(' ') : '位置待确认'
}

const formatDate = (dateStr) => {
  if (!dateStr) return '未知'
  const date = new Date(dateStr)
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #F5F5F5;
  padding-bottom: 180rpx;
}

/* 图片轮播 */
.image-section {
  position: relative;
  width: 100%;
  height: 600rpx;
  background: #000;
}

.image-swiper {
  width: 100%;
  height: 100%;
}

.swiper-image {
  width: 100%;
  height: 100%;
  background: #F5F5F5;
}

.favorite-btn-header {
  position: absolute;
  top: 40rpx;
  right: 32rpx;
  width: 88rpx;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10rpx);
  border-radius: 50%;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.2);
  z-index: 10;
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.9);
  }
}

.favorite-icon {
  font-size: 44rpx;
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

/* 基本信息卡片 */
.info-card {
  margin: -40rpx 32rpx 24rpx;
  padding: 40rpx;
  background: #FFFFFF;
  border-radius: 32rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 5;
}

.name-section {
  margin-bottom: 32rpx;
  padding-bottom: 32rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.name-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 16rpx;
}

.animal-name {
  flex: 1;
  font-size: 48rpx;
  font-weight: 700;
  color: #2C2C2C;
  letter-spacing: -0.02em;
}

.gender-badge {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: 32rpx;
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

.status-badge-large {
  display: inline-flex;
  align-items: center;
  gap: 12rpx;
  padding: 12rpx 32rpx;
  border-radius: 50rpx;
  font-size: 26rpx;
  font-weight: 500;
  
  &.rescued {
    background: #E8F5E9;
    
    .status-dot {
      background: #00A699;
    }
    
    .status-text {
      color: #00A699;
    }
  }
  
  &.rescuing {
    background: #FFF3E0;
    
    .status-dot {
      background: #FCB900;
    }
    
    .status-text {
      color: #FCB900;
    }
  }
}

.status-dot {
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
}

.status-text {
  font-size: 26rpx;
}

.details-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24rpx;
  margin-bottom: 32rpx;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.detail-label {
  font-size: 24rpx;
  color: #999;
}

.detail-value {
  font-size: 30rpx;
  color: #2C2C2C;
  font-weight: 500;
}

.location-section {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
  padding: 24rpx;
  background: #F5F5F5;
  border-radius: 16rpx;
}

.location-icon {
  font-size: 36rpx;
  flex-shrink: 0;
  margin-top: 4rpx;
}

.location-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.location-text {
  font-size: 30rpx;
  color: #2C2C2C;
  font-weight: 500;
}

.location-address {
  font-size: 26rpx;
  color: #666;
}

/* 描述卡片 */
.description-card,
.details-card {
  margin: 0 32rpx 24rpx;
  padding: 40rpx;
  background: #FFFFFF;
  border-radius: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
}

.card-title {
  display: block;
  font-size: 32rpx;
  font-weight: 600;
  color: #2C2C2C;
  margin-bottom: 24rpx;
  letter-spacing: -0.01em;
}

.description-text {
  display: block;
  font-size: 30rpx;
  color: #666;
  line-height: 1.8;
  letter-spacing: 0.01em;
}

.detail-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 24rpx;
  border-bottom: 1rpx solid #F0F0F0;
  
  &:last-child {
    border-bottom: none;
    padding-bottom: 0;
  }
}

.row-label {
  font-size: 28rpx;
  color: #999;
}

.row-value {
  font-size: 28rpx;
  color: #2C2C2C;
  font-weight: 500;
}

/* 行动按钮 - Petfinder风格，特别突出 */
.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 24rpx 32rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  background: #FFFFFF;
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.btn-adopt-primary,
.btn-sponsor,
.btn-disabled {
  width: 100%;
  height: 96rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  border-radius: 16rpx;
  border: none;
  font-size: 32rpx;
  font-weight: 700;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.btn-adopt-primary {
  background: linear-gradient(135deg, #FF5A5F 0%, #FF7A7F 100%);
  color: #FFFFFF;
  box-shadow: 0 8rpx 24rpx rgba(255, 90, 95, 0.4);
  animation: pulse 2s infinite;
  
  &:active {
    background: linear-gradient(135deg, #E04A4F 0%, #FF6B70 100%);
    transform: scale(0.98);
    box-shadow: 0 4rpx 16rpx rgba(255, 90, 95, 0.3);
  }
}

@keyframes pulse {
  0%, 100% {
    box-shadow: 0 8rpx 24rpx rgba(255, 90, 95, 0.4);
  }
  50% {
    box-shadow: 0 8rpx 32rpx rgba(255, 90, 95, 0.6);
  }
}

.btn-sponsor {
  background: linear-gradient(135deg, #00A699 0%, #00C4B3 100%);
  color: #FFFFFF;
  box-shadow: 0 8rpx 24rpx rgba(0, 166, 153, 0.4);
  
  &:active {
    background: linear-gradient(135deg, #008A7D 0%, #00B3A3 100%);
    transform: scale(0.98);
    box-shadow: 0 4rpx 16rpx rgba(0, 166, 153, 0.3);
  }
}

.btn-disabled {
  background: #F5F5F5;
  color: #999;
}

.btn-icon {
  font-size: 36rpx;
}

.btn-text {
  font-size: 32rpx;
  font-weight: 700;
}
</style>
