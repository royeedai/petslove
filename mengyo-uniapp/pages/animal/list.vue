<template>
  <view class="page">
    <!-- 顶部搜索和筛选栏 - Petfinder风格 -->
    <view class="header-section">
      <view class="search-bar" @click="handleSearch">
        <text class="search-icon">🔍</text>
        <text class="search-placeholder">搜索宠物、品种或位置...</text>
      </view>
      <view class="filter-btn" @click="showFilter = true">
        <text class="filter-icon">⚙️</text>
        <view v-if="hasActiveFilters" class="filter-badge"></view>
      </view>
    </view>

    <!-- 快速筛选标签 - Petfinder风格 -->
    <scroll-view scroll-x class="filter-tags-scroll" :show-scrollbar="false">
      <view class="filter-tags">
        <view 
          v-for="tag in quickFilters" 
          :key="tag.value"
          class="filter-tag"
          :class="{ active: activeQuickFilter === tag.value }"
          @click="handleQuickFilter(tag.value)"
        >
          <text class="tag-icon">{{ tag.icon }}</text>
          <text class="tag-text">{{ tag.label }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 动物列表 - Petfinder风格网格 -->
    <view class="animals-container">
      <view class="list-header">
        <text class="result-count">{{ totalCount }} 只宠物可领养</text>
        <view class="view-toggle">
          <view 
            class="toggle-item"
            :class="{ active: viewMode === 'grid' }"
            @click="viewMode = 'grid'"
          >
            <text class="toggle-icon">⊞</text>
          </view>
          <view 
            class="toggle-item"
            :class="{ active: viewMode === 'list' }"
            @click="viewMode = 'list'"
          >
            <text class="toggle-icon">☰</text>
          </view>
        </view>
      </view>

      <!-- 网格视图 -->
      <view v-if="viewMode === 'grid'" class="animals-grid">
        <view 
          v-for="(item, index) in animalList" 
          :key="item.id"
          class="animal-card"
          :style="{ animationDelay: `${index * 0.05}s` }"
          @click="handleDetail(item)"
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
              <text class="status-text">{{ getStatusText(item.status) }}</text>
            </view>

            <!-- 收藏按钮 -->
            <view class="favorite-btn" @click.stop="handleFavorite(item)">
              <text class="favorite-icon" :class="{ active: item.isFavorite }">
                {{ item.isFavorite ? '❤️' : '🤍' }}
              </text>
            </view>

            <!-- 紧急标记 -->
            <view v-if="item.healthStatus === 'INJURED' || item.healthStatus === 'SICK'" class="urgent-badge">
              <text class="urgent-icon">⚠️</text>
            </view>
          </view>

          <view class="animal-info">
            <view class="animal-name-row">
              <text class="animal-name">{{ item.name || '待命名' }}</text>
              <view class="gender-badge" :class="item.gender">
                {{ item.gender === 'MALE' ? '♂' : '♀' }}
              </view>
            </view>

            <view class="animal-meta">
              <view class="meta-item">
                <text class="meta-label">品种</text>
                <text class="meta-value">{{ item.breed || '未知' }}</text>
              </view>
              <view class="meta-item">
                <text class="meta-label">年龄</text>
                <text class="meta-value">{{ item.ageEstimate || '未知' }}</text>
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

      <!-- 列表视图 -->
      <view v-else class="animals-list">
        <view 
          v-for="(item, index) in animalList" 
          :key="item.id"
          class="animal-list-item"
          :style="{ animationDelay: `${index * 0.05}s` }"
          @click="handleDetail(item)"
        >
          <view class="list-image-wrapper">
            <image 
              :src="getAnimalImage(item)" 
              mode="aspectFill" 
              class="list-image"
              @error="handleImageError($event, item)"
            ></image>
            <view class="status-badge-small" :class="getStatusClass(item.status)">
              <text class="status-text">{{ getStatusText(item.status) }}</text>
            </view>
          </view>

          <view class="list-info">
            <view class="list-name-row">
              <text class="list-name">{{ item.name || '待命名' }}</text>
              <view class="gender-badge-small" :class="item.gender">
                {{ item.gender === 'MALE' ? '♂' : '♀' }}
              </view>
            </view>

            <view class="list-details">
              <text class="list-breed">{{ item.breed || '未知品种' }}</text>
              <text class="list-age">{{ item.ageEstimate || '未知年龄' }}</text>
            </view>

            <view class="list-location">
              <text class="location-icon">📍</text>
              <text class="location-text">{{ getLocationText(item) }}</text>
            </view>

            <button class="btn-adopt-small" @click.stop="handleAdopt(item)">
              <text class="btn-text">领养</text>
            </button>
          </view>

          <view class="favorite-btn-small" @click.stop="handleFavorite(item)">
            <text class="favorite-icon" :class="{ active: item.isFavorite }">
              {{ item.isFavorite ? '❤️' : '🤍' }}
            </text>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-if="animalList.length === 0 && !loading" class="empty-state">
        <text class="empty-icon">🐾</text>
        <text class="empty-text">暂无可领养宠物</text>
        <text class="empty-desc">试试调整筛选条件</text>
      </view>

      <!-- 加载更多 -->
      <view v-if="hasMore && animalList.length > 0" class="load-more" @click="loadMore">
        <text class="load-more-text">{{ loading ? '加载中...' : '加载更多' }}</text>
      </view>
    </view>

    <!-- 筛选弹窗 -->
    <view v-if="showFilter" class="filter-modal" @click="showFilter = false">
      <view class="filter-content" @click.stop>
        <view class="filter-header">
          <text class="filter-title">筛选条件</text>
          <text class="filter-close" @click="showFilter = false">✕</text>
        </view>
        
        <scroll-view scroll-y class="filter-body">
          <view class="filter-group">
            <text class="filter-group-title">动物类型</text>
            <view class="filter-options">
              <view 
                v-for="type in animalTypes" 
                :key="type.value"
                class="filter-option"
                :class="{ active: filters.animalType === type.value }"
                @click="filters.animalType = type.value"
              >
                {{ type.label }}
              </view>
            </view>
          </view>

          <view class="filter-group">
            <text class="filter-group-title">性别</text>
            <view class="filter-options">
              <view 
                v-for="gender in genders" 
                :key="gender.value"
                class="filter-option"
                :class="{ active: filters.gender === gender.value }"
                @click="filters.gender = gender.value"
              >
                {{ gender.label }}
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
                :class="{ active: filters.healthStatus === health.value }"
                @click="filters.healthStatus = health.value"
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
                :class="{ active: filters.size === size.value }"
                @click="filters.size = size.value"
              >
                {{ size.label }}
              </view>
            </view>
          </view>

          <view class="filter-group">
            <text class="filter-group-title">位置</text>
            <view class="location-input-wrapper">
              <input 
                v-model="filters.location"
                type="text"
                placeholder="输入城市或地区"
                class="location-input"
                placeholder-class="input-placeholder"
              />
            </view>
          </view>
        </scroll-view>

        <view class="filter-actions">
          <button class="btn-reset" @click="handleResetFilter">重置</button>
          <button class="btn-apply" @click="handleApplyFilter">应用筛选</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { rescueApi } from '@/utils/api'

const showFilter = ref(false)
const viewMode = ref('grid')
const activeQuickFilter = ref('all')
const loading = ref(false)
const hasMore = ref(true)
const totalCount = ref(0)
const currentPage = ref(1)
const animalList = ref([])

const filters = ref({
  animalType: 'all',
  gender: 'all',
  healthStatus: 'all',
  size: 'all',
  location: ''
})

const quickFilters = ref([
  { label: '全部', value: 'all', icon: '🐾' },
  { label: '可领养', value: 'RESCUED', icon: '🏠' },
  { label: '紧急', value: 'urgent', icon: '🚨' },
  { label: '幼崽', value: 'young', icon: '🐣' },
  { label: '大型', value: 'LARGE', icon: '🐕' },
  { label: '小型', value: 'SMALL', icon: '🐈' }
])

const animalTypes = ref([
  { label: '全部', value: 'all' },
  { label: '狗', value: 'DOG' },
  { label: '猫', value: 'CAT' },
  { label: '其他', value: 'OTHER' }
])

const genders = ref([
  { label: '全部', value: 'all' },
  { label: '公', value: 'MALE' },
  { label: '母', value: 'FEMALE' }
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

const hasActiveFilters = computed(() => {
  return filters.value.animalType !== 'all' ||
         filters.value.gender !== 'all' ||
         filters.value.healthStatus !== 'all' ||
         filters.value.size !== 'all' ||
         filters.value.location !== ''
})

onMounted(() => {
  loadAnimals()
})

const loadAnimals = async (reset = false) => {
  if (loading.value) return
  
  loading.value = true
  
  if (reset) {
    currentPage.value = 1
    animalList.value = []
  }
  
  try {
    const params = {
      page: currentPage.value,
      size: 20,
      status: 'RESCUED'
    }
    
    if (filters.value.animalType !== 'all') {
      params.animalType = filters.value.animalType
    }
    
    if (filters.value.healthStatus !== 'all') {
      params.healthStatus = filters.value.healthStatus
    }
    
    if (filters.value.size !== 'all') {
      params.size = filters.value.size
    }
    
    if (filters.value.location) {
      params.city = filters.value.location
    }
    
    const res = await rescueApi.getAnimalList(params)
    if (res.data && res.data.records) {
      const newList = res.data.records.map(item => ({
        ...item,
        isFavorite: false,
        images: item.images ? (typeof item.images === 'string' ? JSON.parse(item.images) : item.images) : []
      }))
      
      if (reset) {
        animalList.value = newList
      } else {
        animalList.value = [...animalList.value, ...newList]
      }
      
      totalCount.value = res.data.total || 0
      hasMore.value = res.data.records.length === 20
      currentPage.value++
    }
  } catch (error) {
    console.error('加载动物列表失败', error)
    uni.showToast({
      title: '加载失败，请重试',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  if (!loading.value && hasMore.value) {
    loadAnimals()
  }
}

const handleQuickFilter = (value) => {
  activeQuickFilter.value = value
  
  if (value === 'all') {
    filters.value = {
      animalType: 'all',
      gender: 'all',
      healthStatus: 'all',
      size: 'all',
      location: ''
    }
  } else if (value === 'RESCUED') {
    filters.value.status = 'RESCUED'
  } else if (value === 'urgent') {
    filters.value.healthStatus = 'INJURED'
  } else if (value === 'young') {
    // 年龄筛选逻辑
  } else {
    filters.value.size = value
  }
  
  loadAnimals(true)
}

const handleResetFilter = () => {
  filters.value = {
    animalType: 'all',
    gender: 'all',
    healthStatus: 'all',
    size: 'all',
    location: ''
  }
  activeQuickFilter.value = 'all'
}

const handleApplyFilter = () => {
  showFilter.value = false
  activeQuickFilter.value = 'all'
  loadAnimals(true)
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

const handleDetail = (item) => {
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
  // 搜索功能
  uni.showToast({
    title: '搜索功能开发中',
    icon: 'none'
  })
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #F5F5F5;
  padding-bottom: 40rpx;
}

/* 顶部搜索栏 */
.header-section {
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
  position: relative;
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

.filter-badge {
  position: absolute;
  top: 8rpx;
  right: 8rpx;
  width: 16rpx;
  height: 16rpx;
  background: #FFFFFF;
  border-radius: 50%;
  border: 2rpx solid #FF5A5F;
}

/* 快速筛选标签 */
.filter-tags-scroll {
  white-space: nowrap;
  background: #FFFFFF;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #EEEEEE;
}

.filter-tags {
  display: inline-flex;
  gap: 16rpx;
  padding: 0 32rpx;
}

.filter-tag {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 24rpx;
  background: #F5F5F5;
  border-radius: 50rpx;
  transition: all 0.3s ease;
  
  &.active {
    background: #FF5A5F;
    box-shadow: 0 2rpx 8rpx rgba(255, 90, 95, 0.3);
    
    .tag-text {
      color: #FFFFFF;
      font-weight: 600;
    }
  }
  
  &:active {
    transform: scale(0.95);
  }
}

.tag-icon {
  font-size: 28rpx;
}

.tag-text {
  font-size: 26rpx;
  color: #666;
  white-space: nowrap;
}

/* 列表头部 */
.animals-container {
  padding: 24rpx 32rpx;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.result-count {
  font-size: 28rpx;
  color: #666;
  font-weight: 500;
}

.view-toggle {
  display: flex;
  gap: 8rpx;
  background: #F5F5F5;
  border-radius: 12rpx;
  padding: 4rpx;
}

.toggle-item {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8rpx;
  transition: all 0.3s ease;
  
  &.active {
    background: #FFFFFF;
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
    
    .toggle-icon {
      color: #FF5A5F;
    }
  }
}

.toggle-icon {
  font-size: 32rpx;
  color: #999;
}

/* 网格视图 */
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
  padding: 8rpx 20rpx;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10rpx);
  border-radius: 50rpx;
  font-size: 22rpx;
  font-weight: 500;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
  
  &.rescued {
    .status-text {
      color: #00A699;
    }
  }
  
  &.rescuing {
    .status-text {
      color: #FCB900;
    }
  }
}

.status-text {
  font-size: 22rpx;
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

.urgent-badge {
  position: absolute;
  bottom: 16rpx;
  left: 16rpx;
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 90, 95, 0.95);
  backdrop-filter: blur(10rpx);
  border-radius: 50%;
  box-shadow: 0 2rpx 8rpx rgba(255, 90, 95, 0.3);
}

.urgent-icon {
  font-size: 32rpx;
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

.animal-meta {
  display: flex;
  gap: 24rpx;
  margin-bottom: 16rpx;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.meta-label {
  font-size: 24rpx;
  color: #999;
}

.meta-value {
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

/* 列表视图 */
.animals-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.animal-list-item {
  display: flex;
  gap: 24rpx;
  padding: 24rpx;
  background: #FFFFFF;
  border-radius: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  animation: fadeInUp 0.6s ease-out both;
  
  &:active {
    transform: translateY(-2rpx);
    box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.12);
  }
}

.list-image-wrapper {
  position: relative;
  width: 240rpx;
  height: 240rpx;
  border-radius: 16rpx;
  overflow: hidden;
  flex-shrink: 0;
}

.list-image {
  width: 100%;
  height: 100%;
  background: #F5F5F5;
}

.status-badge-small {
  position: absolute;
  top: 12rpx;
  left: 12rpx;
  padding: 6rpx 16rpx;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10rpx);
  border-radius: 50rpx;
  font-size: 20rpx;
  font-weight: 500;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
  
  &.rescued {
    .status-text {
      color: #00A699;
    }
  }
}

.list-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  min-width: 0;
}

.list-name-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.list-name {
  flex: 1;
  font-size: 32rpx;
  font-weight: 600;
  color: #2C2C2C;
  letter-spacing: -0.01em;
}

.gender-badge-small {
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: 20rpx;
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

.list-details {
  display: flex;
  gap: 24rpx;
}

.list-breed,
.list-age {
  font-size: 26rpx;
  color: #666;
}

.list-location {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.btn-adopt-small {
  width: 120rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #FF5A5F;
  color: #FFFFFF;
  border-radius: 12rpx;
  border: none;
  font-size: 26rpx;
  font-weight: 600;
  box-shadow: 0 2rpx 8rpx rgba(255, 90, 95, 0.3);
  transition: all 0.3s ease;
  
  &:active {
    background: #E04A4F;
    transform: scale(0.95);
  }
}

.favorite-btn-small {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.9);
  }
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

/* 加载更多 */
.load-more {
  margin-top: 32rpx;
  padding: 32rpx;
  text-align: center;
  transition: all 0.3s ease;
  
  &:active {
    opacity: 0.7;
  }
}

.load-more-text {
  font-size: 28rpx;
  color: #FF5A5F;
  font-weight: 500;
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
  display: flex;
  flex-direction: column;
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
  margin-bottom: 32rpx;
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
  flex: 1;
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

.location-input-wrapper {
  padding: 20rpx 32rpx;
  background: #F5F5F5;
  border-radius: 16rpx;
}

.location-input {
  width: 100%;
  font-size: 28rpx;
  color: #2C2C2C;
}

.input-placeholder {
  color: #999;
}

.filter-actions {
  display: flex;
  gap: 24rpx;
  margin-top: 32rpx;
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
