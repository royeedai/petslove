<template>
  <view class="page">
    <!-- 搜索和筛选栏 - 更温馨的设计 -->
    <view class="search-section">
      <view class="search-bar">
        <view class="search-input-wrapper">
          <text class="search-icon">🔍</text>
          <input 
            v-model="keyword" 
            type="text" 
            placeholder="搜索动物名称或品种" 
            class="search-input"
            placeholder-class="input-placeholder"
            @confirm="handleSearch"
          />
        </view>
        <view class="filter-btn" @click="showFilter = true">
          <text class="filter-icon">⚙️</text>
        </view>
      </view>
    </view>

    <!-- 分类标签 - 更圆润的设计 -->
    <scroll-view scroll-x class="category-scroll" :show-scrollbar="false">
      <view class="category-list">
        <view 
          v-for="(item, index) in categories" 
          :key="index"
          class="category-item"
          :class="{ active: currentCategory === item.value }"
          @click="currentCategory = item.value"
        >
          <text class="category-emoji">{{ item.emoji }}</text>
          <text class="category-text">{{ item.label }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 动物列表 - Pinterest 风格卡片 -->
    <view class="animal-list">
      <view 
        v-for="item in animalList" 
        :key="item.id" 
        class="animal-card"
        @click="handleDetail(item)"
      >
        <view class="animal-image-wrapper">
          <image 
            :src="item.photo" 
            mode="aspectFill" 
            class="animal-image"
            @error="handleImageError($event, item.id)"
          ></image>
          
          <!-- 徽章标签 -->
          <view class="badge-wrapper">
            <view v-if="item.isUrgent" class="badge urgent">
              <text class="badge-icon">🚨</text>
              <text class="badge-text">紧急</text>
            </view>
            <view v-if="item.healthStatus === 'injured'" class="badge health">
              <text class="badge-icon">⚠️</text>
              <text class="badge-text">受伤</text>
            </view>
          </view>
          
          <!-- 收藏按钮 -->
          <view class="like-btn" @click.stop="handleLike(item)">
            <text class="like-icon">{{ item.isLiked ? '❤️' : '🤍' }}</text>
          </view>
        </view>

        <view class="animal-info">
          <view class="info-header">
            <text class="animal-name">{{ item.name || '待命名' }}</text>
            <view class="gender-badge" :class="item.gender">
              {{ item.gender === 'male' ? '♂' : '♀' }}
            </view>
          </view>

          <view class="info-tags">
            <view class="info-tag">
              <text class="tag-label">品种</text>
              <text class="tag-value">{{ item.breed || '未知' }}</text>
            </view>
            <view class="info-tag">
              <text class="tag-label">年龄</text>
              <text class="tag-value">{{ item.age || '未知' }}</text>
            </view>
          </view>

          <view class="location-row">
            <text class="location-icon">📍</text>
            <text class="location-text">{{ item.location || '位置待确认' }}</text>
          </view>

          <view class="info-desc" v-if="item.description">
            {{ item.description }}
          </view>

          <view class="card-footer">
            <view class="status-tag" :class="item.status">
              <text class="status-dot"></text>
              <text class="status-text">{{ getStatusText(item.status) }}</text>
            </view>
            <button class="btn-adopt" @click.stop="handleAdopt(item)">
              <text class="btn-text">领养</text>
            </button>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-if="animalList.length === 0" class="empty-state">
        <text class="empty-icon">🐾</text>
        <text class="empty-text">暂无流浪动物信息</text>
        <text class="empty-desc">敬请期待更多小可爱</text>
      </view>
    </view>

    <!-- 筛选弹窗 - 更温馨的设计 -->
    <view v-if="showFilter" class="filter-modal" @click="showFilter = false">
      <view class="filter-content" @click.stop>
        <view class="filter-header">
          <text class="filter-title">筛选条件</text>
          <text class="filter-close" @click="showFilter = false">✕</text>
        </view>
        
        <view class="filter-section">
          <text class="filter-label">动物类型</text>
          <view class="filter-options">
            <view 
              v-for="item in animalTypes" 
              :key="item.value"
              class="filter-option"
              :class="{ active: filterType === item.value }"
              @click="filterType = item.value"
            >
              {{ item.label }}
            </view>
          </view>
        </view>

        <view class="filter-section">
          <text class="filter-label">健康状况</text>
          <view class="filter-options">
            <view 
              v-for="item in healthOptions" 
              :key="item.value"
              class="filter-option"
              :class="{ active: filterHealth === item.value }"
              @click="filterHealth = item.value"
            >
              {{ item.label }}
            </view>
          </view>
        </view>

        <view class="filter-actions">
          <button class="btn-reset" @click="handleReset">重置</button>
          <button class="btn-confirm" @click="handleConfirmFilter">确定</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { rescueApi } from '@/utils/api'

const keyword = ref('')
const currentCategory = ref('all')
const showFilter = ref(false)
const filterType = ref('all')
const filterHealth = ref('all')

const categories = ref([
  { label: '全部', value: 'all', emoji: '🐾' },
  { label: '狗狗', value: 'dog', emoji: '🐕' },
  { label: '猫咪', value: 'cat', emoji: '🐈' },
  { label: '其他', value: 'other', emoji: '🦊' }
])

const animalTypes = ref([
  { label: '全部', value: 'all' },
  { label: '狗', value: 'dog' },
  { label: '猫', value: 'cat' },
  { label: '其他', value: 'other' }
])

const healthOptions = ref([
  { label: '全部', value: 'all' },
  { label: '健康', value: 'healthy' },
  { label: '受伤', value: 'injured' },
  { label: '患病', value: 'sick' }
])

const animalList = ref([])

const defaultAnimalImage = (type) => {
  const imageMap = {
    'dog': 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=600&q=80',
    'cat': 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=600&q=80',
    'other': 'https://images.unsplash.com/photo-1425082661705-1834bfd09dca?w=600&q=80'
  }
  return imageMap[type] || imageMap['other']
}

onMounted(() => {
  loadData()
})

watch(currentCategory, () => {
  loadData()
})

const loadData = async () => {
  try {
    const params = {
      page: 1,
      size: 20
    }
    
    if (currentCategory.value !== 'all') {
      params.animalType = currentCategory.value
    }
    
    if (filterType.value !== 'all') {
      params.animalType = filterType.value
    }
    
    if (filterHealth.value !== 'all') {
      params.healthStatus = filterHealth.value
    }
    
    if (keyword.value) {
      params.keyword = keyword.value
    }
    
    const res = await rescueApi.getAnimalList(params)
    if (res.data && res.data.records) {
      animalList.value = res.data.records.map(item => ({
        ...item,
        photo: item.photo || getDefaultAnimalImage(item.animalType),
        isLiked: false
      }))
    }
  } catch (error) {
    console.error('加载动物列表失败', error)
    uni.showToast({
      title: '加载失败，请重试',
      icon: 'none'
    })
  }
}

const getDefaultAnimalImage = (type) => {
  return defaultAnimalImage(type)
}

const handleImageError = (e, id) => {
  console.log('图片加载失败', id)
  const item = animalList.value.find(a => a.id === id)
  if (item) {
    item.photo = getDefaultAnimalImage(item.animalType)
  }
}

const handleSearch = () => {
  loadData()
}

const handleDetail = (item) => {
  uni.navigateTo({
    url: `/pages/animal/detail?id=${item.id}`
  })
}

const handleLike = (item) => {
  item.isLiked = !item.isLiked
  uni.showToast({
    title: item.isLiked ? '已收藏' : '已取消收藏',
    icon: 'success',
    duration: 1500
  })
}

const handleAdopt = (item) => {
  if (item.status === 'available') {
    uni.navigateTo({
      url: `/pages/adoption/apply?animalId=${item.id}`
    })
  } else {
    uni.showToast({
      title: '该动物暂不可领养',
      icon: 'none'
    })
  }
}

const handleReset = () => {
  filterType.value = 'all'
  filterHealth.value = 'all'
}

const handleConfirmFilter = () => {
  showFilter.value = false
  loadData()
}

const getStatusText = (status) => {
  const statusMap = {
    'available': '可领养',
    'pending': '待审核',
    'adopted': '已领养',
    'rescued': '救助中'
  }
  return statusMap[status] || '未知'
}
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: var(--bg-page);
  padding-bottom: var(--spacing-lg);
}

/* 搜索栏 - 更温馨的设计 */
.search-section {
  padding: var(--spacing-lg);
  background: var(--bg-white);
  border-bottom: 1rpx solid var(--border-color);
}

.search-bar {
  display: flex;
  gap: var(--spacing-md);
  align-items: center;
}

.search-input-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--bg-gray);
  border-radius: var(--radius-full);
  border: 1rpx solid var(--border-color);
  transition: all 0.3s ease;
  
  &:focus-within {
    background: var(--bg-white);
    border-color: var(--primary-color);
    box-shadow: var(--shadow-sm);
  }
}

.search-icon {
  font-size: 32rpx;
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: var(--text-primary);
}

.input-placeholder {
  color: var(--text-light);
}

.filter-btn {
  width: 88rpx;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-color);
  border-radius: var(--radius-full);
  box-shadow: var(--shadow-sm);
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.95);
    background: var(--primary-dark);
  }
}

.filter-icon {
  font-size: 36rpx;
  color: #fff;
}

/* 分类滚动 - 更圆润的设计 */
.category-scroll {
  white-space: nowrap;
  background: var(--bg-white);
  padding: var(--spacing-lg) 0;
  border-bottom: 1rpx solid var(--border-color);
}

.category-list {
  display: inline-flex;
  gap: var(--spacing-md);
  padding: 0 var(--spacing-lg);
}

.category-item {
  display: inline-flex;
  align-items: center;
  gap: 8rpx;
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--bg-gray);
  border-radius: var(--radius-full);
  border: 1rpx solid var(--border-color);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  white-space: nowrap;

  &.active {
    background: var(--primary-color);
    border-color: var(--primary-color);
    box-shadow: var(--shadow-sm);
    
    .category-text {
      color: #fff;
      font-weight: 600;
    }
    
    .category-emoji {
      filter: brightness(1.1);
    }
  }
  
  &:active {
    transform: scale(0.95);
  }
}

.category-emoji {
  font-size: 32rpx;
}

.category-text {
  font-size: 26rpx;
  color: var(--text-secondary);
  white-space: nowrap;
}

/* 动物列表 - Pinterest 风格卡片 */
.animal-list {
  padding: var(--spacing-lg);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.animal-card {
  background: var(--bg-white);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-card);
  border: 1rpx solid var(--border-color);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:active {
    transform: translateY(-4rpx);
    box-shadow: var(--shadow-md);
  }
}

.animal-image-wrapper {
  position: relative;
  width: 100%;
  height: 480rpx;
  overflow: hidden;
}

.animal-image {
  width: 100%;
  height: 100%;
  background: var(--bg-warm);
  transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.animal-card:active .animal-image {
  transform: scale(1.05);
}

.badge-wrapper {
  position: absolute;
  top: var(--spacing-md);
  left: var(--spacing-md);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.badge {
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 8rpx 16rpx;
  border-radius: var(--radius-full);
  font-size: 22rpx;
  font-weight: 500;
  backdrop-filter: blur(10rpx);
  color: #fff;
  box-shadow: var(--shadow-sm);

  &.urgent {
    background: rgba(255, 90, 95, 0.9);
  }

  &.health {
    background: rgba(255, 193, 0, 0.9);
  }
}

.badge-icon {
  font-size: 20rpx;
}

.like-btn {
  position: absolute;
  top: var(--spacing-md);
  right: var(--spacing-md);
  width: 72rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10rpx);
  border-radius: 50%;
  box-shadow: var(--shadow-sm);
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.9);
  }
}

.like-icon {
  font-size: 36rpx;
}

.animal-info {
  padding: var(--spacing-xl);
}

.info-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-lg);
}

.animal-name {
  font-size: 38rpx;
  font-weight: 600;
  color: var(--text-primary);
  flex: 1;
  letter-spacing: -0.01em;
}

.gender-badge {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: 28rpx;
  font-weight: 600;
  flex-shrink: 0;

  &.male {
    background: #E3F2FD;
    color: #1976D2;
  }

  &.female {
    background: #FCE4EC;
    color: #C2185B;
  }
}

.info-tags {
  display: flex;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-md);
}

.info-tag {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: var(--spacing-xs) var(--spacing-md);
  background: var(--bg-warm);
  border-radius: var(--radius-full);
}

.tag-label {
  font-size: 24rpx;
  color: var(--text-light);
}

.tag-value {
  font-size: 26rpx;
  color: var(--text-primary);
  font-weight: 500;
}

.location-row {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-bottom: var(--spacing-md);
  padding: var(--spacing-sm) 0;
}

.location-icon {
  font-size: 28rpx;
  flex-shrink: 0;
}

.location-text {
  font-size: 26rpx;
  color: var(--text-secondary);
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.info-desc {
  margin: var(--spacing-md) 0;
  font-size: 28rpx;
  color: var(--text-secondary);
  line-height: 1.7;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: var(--spacing-lg);
  padding-top: var(--spacing-lg);
  border-top: 1rpx solid var(--divider-color);
}

.status-tag {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 8rpx 20rpx;
  border-radius: var(--radius-full);
  font-size: 24rpx;
  font-weight: 500;

  &.available {
    background: #E8F5E9;
    
    .status-dot {
      background: var(--success-color);
    }
    
    .status-text {
      color: var(--success-color);
    }
  }

  &.pending {
    background: #FFF3E0;
    
    .status-dot {
      background: var(--warning-color);
    }
    
    .status-text {
      color: var(--warning-color);
    }
  }

  &.adopted {
    background: #E3F2FD;
    
    .status-dot {
      background: var(--primary-color);
    }
    
    .status-text {
      color: var(--primary-color);
    }
  }

  &.rescued {
    background: #FCE4EC;
    
    .status-dot {
      background: var(--danger-color);
    }
    
    .status-text {
      color: var(--danger-color);
    }
  }
}

.status-dot {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
}

.btn-adopt {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-sm) var(--spacing-lg);
  background: var(--primary-color);
  color: #fff;
  border-radius: var(--radius-full);
  border: none;
  font-size: 28rpx;
  font-weight: 500;
  box-shadow: var(--shadow-sm);
  transition: all 0.3s ease;
  
  &:active {
    background: var(--primary-dark);
    transform: scale(0.95);
  }
}

.btn-text {
  font-size: 28rpx;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xxl) 0;
  gap: var(--spacing-md);
}

.empty-icon {
  font-size: 140rpx;
  opacity: 0.25;
}

.empty-text {
  font-size: 30rpx;
  color: var(--text-secondary);
  font-weight: 500;
}

.empty-desc {
  font-size: 26rpx;
  color: var(--text-light);
}

/* 筛选弹窗 - 更温馨的设计 */
.filter-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: flex-end;
  z-index: 999;
  backdrop-filter: blur(4rpx);
}

.filter-content {
  width: 100%;
  max-height: 80vh;
  background: var(--bg-white);
  border-radius: var(--radius-xl) var(--radius-xl) 0 0;
  padding: var(--spacing-xl);
}

.filter-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-lg);
  border-bottom: 1rpx solid var(--divider-color);
}

.filter-title {
  font-size: 36rpx;
  font-weight: 600;
  color: var(--text-primary);
}

.filter-close {
  font-size: 40rpx;
  color: var(--text-light);
  line-height: 1;
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s ease;
  
  &:active {
    background: var(--bg-gray);
  }
}

.filter-section {
  margin-bottom: var(--spacing-xl);
}

.filter-label {
  display: block;
  font-size: 30rpx;
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: var(--spacing-lg);
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-md);
}

.filter-option {
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--bg-gray);
  border-radius: var(--radius-full);
  font-size: 28rpx;
  color: var(--text-secondary);
  border: 1rpx solid var(--border-color);
  transition: all 0.3s ease;

  &.active {
    background: var(--primary-color);
    color: #fff;
    border-color: var(--primary-color);
    box-shadow: var(--shadow-sm);
  }
  
  &:active {
    transform: scale(0.95);
  }
}

.filter-actions {
  display: flex;
  gap: var(--spacing-md);
  margin-top: var(--spacing-xl);
}

.btn-reset,
.btn-confirm {
  flex: 1;
  height: 96rpx;
  border-radius: var(--radius-full);
  font-size: 32rpx;
  border: none;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-reset {
  background: var(--bg-gray);
  color: var(--text-secondary);
  
  &:active {
    background: #E0E0E0;
  }
}

.btn-confirm {
  background: var(--primary-color);
  color: #fff;
  box-shadow: var(--shadow-sm);
  
  &:active {
    background: var(--primary-dark);
    transform: scale(0.98);
  }
}
</style>
