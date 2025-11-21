<template>
  <view class="page">
    <!-- 搜索和筛选栏 -->
    <view class="search-bar">
      <view class="search-input-wrapper">
        <text class="search-icon">🔍</text>
        <input 
          v-model="keyword" 
          type="text" 
          placeholder="搜索动物名称或品种" 
          class="search-input"
          @confirm="handleSearch"
        />
      </view>
      <view class="filter-btn" @click="showFilter = true">
        <text class="filter-icon">⚙️</text>
      </view>
    </view>

    <!-- 分类标签 -->
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

    <!-- 动物列表 -->
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
              紧急
            </view>
            <view v-if="item.healthStatus === 'injured'" class="badge health">
              受伤
            </view>
          </view>
        </view>

        <view class="animal-info">
          <view class="info-header">
            <text class="animal-name">{{ item.name || '待命名' }}</text>
            <view class="gender-badge" :class="item.gender">
              {{ item.gender === 'male' ? '♂' : '♀' }}
            </view>
          </view>

          <view class="info-row">
            <view class="info-item">
              <text class="info-label">品种</text>
              <text class="info-value">{{ item.breed || '未知' }}</text>
            </view>
            <view class="info-item">
              <text class="info-label">年龄</text>
              <text class="info-value">{{ item.age || '未知' }}</text>
            </view>
          </view>

          <view class="info-row location-row">
            <text class="location-icon">📍</text>
            <text class="location-text">{{ item.location || '位置待确认' }}</text>
          </view>

          <view class="info-desc" v-if="item.description">
            {{ item.description }}
          </view>

          <view class="card-footer">
            <view class="status-tag" :class="item.status">
              {{ getStatusText(item.status) }}
            </view>
            <view class="action-btns">
              <button class="btn-action" @click.stop="handleLike(item)">
                <text class="btn-icon">{{ item.isLiked ? '❤️' : '🤍' }}</text>
              </button>
              <button class="btn-action primary" @click.stop="handleAdopt(item)">
                <text class="btn-text">领养</text>
              </button>
            </view>
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

    <!-- 筛选弹窗 -->
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
  padding-bottom: 20rpx;
}

/* 搜索栏 */
.search-bar {
  display: flex;
  gap: 16rpx;
  padding: 24rpx;
  background: var(--bg-white);
  box-shadow: var(--shadow-sm);
  border-bottom: 1rpx solid var(--border-color);
}

.search-input-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 20rpx 24rpx;
  background: var(--bg-gray);
  border-radius: var(--radius-md);
  border: 1rpx solid var(--border-color);
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

.filter-btn {
  width: 88rpx;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-color);
  border-radius: var(--radius-md);
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

/* 分类滚动 */
.category-scroll {
  white-space: nowrap;
  background: var(--bg-white);
  padding: 20rpx 0;
  border-bottom: 1rpx solid var(--border-color);
}

.category-list {
  display: inline-flex;
  gap: 16rpx;
  padding: 0 24rpx;
}

.category-item {
  display: inline-flex;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx 32rpx;
  background: var(--bg-gray);
  border-radius: 40rpx;
  border: 1rpx solid var(--border-color);
  transition: all 0.3s ease;
  white-space: nowrap;

  &.active {
    background: var(--primary-color);
    border-color: var(--primary-color);
    
    .category-text {
      color: #fff;
      font-weight: 600;
    }
    
    .category-emoji {
      filter: brightness(1.2);
    }
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

/* 动物列表 */
.animal-list {
  padding: 24rpx;
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.animal-card {
  background: var(--bg-white);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  border: 1rpx solid var(--border-color);
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.98);
    box-shadow: var(--shadow-md);
  }
}

.animal-image-wrapper {
  position: relative;
  width: 100%;
  height: 400rpx;
  overflow: hidden;
}

.animal-image {
  width: 100%;
  height: 100%;
  background: var(--bg-gray);
}

.badge-wrapper {
  position: absolute;
  top: 16rpx;
  left: 16rpx;
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.badge {
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  font-size: 22rpx;
  font-weight: 500;
  backdrop-filter: blur(10rpx);
  color: #fff;

  &.urgent {
    background: rgba(220, 53, 69, 0.9);
  }

  &.health {
    background: rgba(255, 193, 7, 0.9);
  }
}

.animal-info {
  padding: 28rpx;
}

.info-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
}

.animal-name {
  font-size: 36rpx;
  font-weight: 600;
  color: var(--text-primary);
  flex: 1;
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

  &.male {
    background: #E3F2FD;
    color: #1976D2;
  }

  &.female {
    background: #FCE4EC;
    color: #C2185B;
  }
}

.info-row {
  display: flex;
  align-items: center;
  gap: 32rpx;
  margin-bottom: 16rpx;
  
  &.location-row {
    gap: 8rpx;
  }
}

.info-item {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.info-label {
  font-size: 24rpx;
  color: var(--text-light);
}

.info-value {
  font-size: 26rpx;
  color: var(--text-primary);
  font-weight: 500;
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
  margin: 20rpx 0;
  font-size: 26rpx;
  color: var(--text-secondary);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid var(--divider-color);
}

.status-tag {
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  font-weight: 500;

  &.available {
    background: #E8F5E9;
    color: var(--success-color);
  }

  &.pending {
    background: #FFF3E0;
    color: var(--warning-color);
  }

  &.adopted {
    background: #E3F2FD;
    color: var(--primary-color);
  }

  &.rescued {
    background: #FCE4EC;
    color: var(--danger-color);
  }
}

.action-btns {
  display: flex;
  gap: 16rpx;
  align-items: center;
}

.btn-action {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  padding: 12rpx 24rpx;
  border-radius: 40rpx;
  border: none;
  background: var(--bg-gray);
  font-size: 26rpx;
  color: var(--text-secondary);
  transition: all 0.3s ease;
  min-width: 80rpx;

  &.primary {
    background: var(--primary-color);
    color: #fff;
    box-shadow: var(--shadow-sm);
    
    &:active {
      background: var(--primary-dark);
    }
  }
  
  &:active {
    background: #E0E0E0;
  }
}

.btn-icon {
  font-size: 28rpx;
}

.btn-text {
  font-size: 26rpx;
  font-weight: 500;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 0;
  gap: 20rpx;
}

.empty-icon {
  font-size: 120rpx;
  opacity: 0.3;
}

.empty-text {
  font-size: 28rpx;
  color: var(--text-secondary);
  font-weight: 500;
}

.empty-desc {
  font-size: 24rpx;
  color: var(--text-light);
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
}

.filter-content {
  width: 100%;
  max-height: 80vh;
  background: var(--bg-white);
  border-radius: 40rpx 40rpx 0 0;
  padding: 40rpx 32rpx;
}

.filter-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 40rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid var(--divider-color);
}

.filter-title {
  font-size: 32rpx;
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
}

.filter-section {
  margin-bottom: 40rpx;
}

.filter-label {
  display: block;
  font-size: 28rpx;
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 20rpx;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.filter-option {
  padding: 16rpx 32rpx;
  background: var(--bg-gray);
  border-radius: 40rpx;
  font-size: 26rpx;
  color: var(--text-secondary);
  border: 1rpx solid var(--border-color);
  transition: all 0.3s ease;

  &.active {
    background: var(--primary-color);
    color: #fff;
    border-color: var(--primary-color);
  }
}

.filter-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 40rpx;
}

.btn-reset,
.btn-confirm {
  flex: 1;
  height: 88rpx;
  border-radius: var(--radius-md);
  font-size: 30rpx;
  border: none;
  font-weight: 500;
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
  }
}
</style>
