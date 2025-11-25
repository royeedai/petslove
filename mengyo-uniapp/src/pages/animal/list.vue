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
          placeholder-class="input-placeholder"
          @confirm="handleSearch"
        />
        <text v-if="keyword" class="clear-icon" @click="handleClear">✕</text>
      </view>
      <view class="filter-btn" @click="showFilter = true">
        <text class="filter-icon">⚙️</text>
      </view>
    </view>

    <!-- 分类标签 -->
    <scroll-view scroll-x class="category-scroll" show-scrollbar="false">
      <view class="category-list">
        <view 
          v-for="(item, index) in categories" 
          :key="index"
          class="category-item"
          :class="{ active: currentCategory === item.value }"
          @click="handleCategoryChange(item.value)"
        >
          <text class="category-emoji">{{ item.emoji }}</text>
          <text class="category-text">{{ item.label }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 动物列表 -->
    <scroll-view scroll-y class="animal-scroll" @scrolltolower="loadMore">
      <view class="animal-list">
        <view 
          v-for="item in animalList" 
          :key="item.id" 
          class="animal-card"
          @click="handleDetail(item)"
        >
          <view class="image-wrapper">
            <image :src="item.photo" mode="aspectFill" class="animal-image"></image>
            
            <!-- 徽章标签 -->
            <view class="badge-wrapper">
              <view v-if="item.isUrgent" class="badge urgent">
                <text class="badge-icon">🚨</text>
                <text class="badge-text">紧急</text>
              </view>
              <view v-if="item.healthStatus === 'injured'" class="badge health">
                <text class="badge-icon">🏥</text>
                <text class="badge-text">受伤</text>
              </view>
            </view>
            
            <!-- 性别标签 -->
            <view class="gender-badge" :class="item.gender">
              {{ item.gender === 'male' ? '♂' : '♀' }}
            </view>
          </view>

          <view class="animal-info">
            <view class="info-header">
              <text class="animal-name">{{ item.name || '待命名' }}</text>
              <button 
                class="like-btn" 
                :class="{ liked: item.isLiked }"
                @click.stop="handleLike(item)"
              >
                <text class="like-icon">{{ item.isLiked ? '❤️' : '🤍' }}</text>
              </button>
            </view>

            <view class="info-tags">
              <view class="info-tag">
                <text class="tag-label">品种</text>
                <text class="tag-value">{{ item.breed }}</text>
              </view>
              <view class="info-tag">
                <text class="tag-label">年龄</text>
                <text class="tag-value">{{ item.age }}</text>
              </view>
            </view>

            <view class="info-location">
              <text class="location-icon">📍</text>
              <text class="location-text">{{ item.location }}</text>
            </view>

            <view class="info-desc">
              {{ item.description }}
            </view>

            <view class="card-footer">
              <view class="status-tag" :class="item.status">
                {{ getStatusText(item.status) }}
              </view>
              <button class="btn-adopt" @click.stop="handleAdopt(item)">
                <text class="btn-icon">🏠</text>
                <text class="btn-text">立即领养</text>
              </button>
            </view>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-if="animalList.length === 0 && !loading" class="empty-state">
        <text class="empty-icon">🐾</text>
        <text class="empty-title">暂无流浪动物信息</text>
        <text class="empty-description">敬请期待更多小可爱</text>
      </view>
      
      <!-- 加载状态 -->
      <view v-if="loading" class="loading-state">
        <view class="loading-spinner"></view>
        <text class="loading-text">加载中...</text>
      </view>
      
      <!-- 加载完成提示 -->
      <view v-if="noMore && animalList.length > 0" class="no-more">
        <text class="no-more-text">没有更多了~</text>
      </view>
      
      <view class="list-footer"></view>
    </scroll-view>

    <!-- 筛选弹窗 -->
    <view v-if="showFilter" class="filter-modal" @click="showFilter = false">
      <view class="filter-content" @click.stop>
        <view class="filter-header">
          <text class="filter-title">筛选条件</text>
          <view class="filter-close" @click="showFilter = false">
            <text class="close-icon">✕</text>
          </view>
        </view>
        
        <scroll-view scroll-y class="filter-scroll">
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

          <view class="filter-section">
            <text class="filter-label">年龄范围</text>
            <view class="filter-options">
              <view 
                v-for="item in ageOptions" 
                :key="item.value"
                class="filter-option"
                :class="{ active: filterAge === item.value }"
                @click="filterAge = item.value"
              >
                {{ item.label }}
              </view>
            </view>
          </view>

          <view class="filter-section">
            <text class="filter-label">领养状态</text>
            <view class="filter-options">
              <view 
                v-for="item in statusOptions" 
                :key="item.value"
                class="filter-option"
                :class="{ active: filterStatus === item.value }"
                @click="filterStatus = item.value"
              >
                {{ item.label }}
              </view>
            </view>
          </view>
        </scroll-view>

        <view class="filter-actions">
          <button class="btn-reset" @click="handleReset">
            <text class="btn-text">重置</text>
          </button>
          <button class="btn-confirm" @click="handleConfirmFilter">
            <text class="btn-text">确定</text>
          </button>
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
const loading = ref(false)
const noMore = ref(false)
const page = ref(1)
const pageSize = 10

// 筛选条件
const filterType = ref('all')
const filterHealth = ref('all')
const filterAge = ref('all')
const filterStatus = ref('all')

const categories = ref([
  { label: '全部', value: 'all', emoji: '🐾' },
  { label: '狗狗', value: 'dog', emoji: '🐕' },
  { label: '猫咪', value: 'cat', emoji: '🐈' },
  { label: '兔子', value: 'rabbit', emoji: '🐰' },
  { label: '其他', value: 'other', emoji: '🦊' }
])

const animalTypes = ref([
  { label: '全部', value: 'all' },
  { label: '狗', value: 'dog' },
  { label: '猫', value: 'cat' },
  { label: '兔子', value: 'rabbit' },
  { label: '其他', value: 'other' }
])

const healthOptions = ref([
  { label: '全部', value: 'all' },
  { label: '健康', value: 'healthy' },
  { label: '受伤', value: 'injured' },
  { label: '患病', value: 'sick' }
])

const ageOptions = ref([
  { label: '全部', value: 'all' },
  { label: '幼年 (0-1岁)', value: 'young' },
  { label: '成年 (1-7岁)', value: 'adult' },
  { label: '老年 (7岁以上)', value: 'senior' }
])

const statusOptions = ref([
  { label: '全部', value: 'all' },
  { label: '可领养', value: 'available' },
  { label: '待审核', value: 'pending' },
  { label: '已领养', value: 'adopted' }
])

const animalList = ref([])

onMounted(() => {
  loadData()
})

watch(currentCategory, () => {
  resetList()
  loadData()
})

const resetList = () => {
  page.value = 1
  animalList.value = []
  noMore.value = false
}

const loadData = async () => {
  if (loading.value || noMore.value) return
  
  loading.value = true
  
  try {
    const params = {
      page: page.value,
      size: pageSize
    }
    
    if (keyword.value) {
      params.keyword = keyword.value
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
    
    if (filterAge.value !== 'all') {
      params.ageRange = filterAge.value
    }
    
    if (filterStatus.value !== 'all') {
      params.status = filterStatus.value
    }
    
    const res = await rescueApi.getAnimalList(params)
    
    if (res.data && res.data.records) {
      const newList = res.data.records.map(item => ({
        ...item,
        photo: item.photo || getDefaultAnimalImage(item.animalType),
        isLiked: false
      }))
      
      if (page.value === 1) {
        animalList.value = newList
      } else {
        animalList.value = [...animalList.value, ...newList]
      }
      
      if (newList.length < pageSize) {
        noMore.value = true
      }
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
  if (!loading.value && !noMore.value) {
    page.value++
    loadData()
  }
}

const getDefaultAnimalImage = (type) => {
  const imageMap = {
    'dog': 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=600&q=80',
    'cat': 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=600&q=80',
    'rabbit': 'https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?w=600&q=80',
    'other': 'https://images.unsplash.com/photo-1425082661705-1834bfd09dca?w=600&q=80'
  }
  return imageMap[type] || imageMap['other']
}

const handleCategoryChange = (value) => {
  currentCategory.value = value
}

const handleSearch = () => {
  resetList()
  loadData()
}

const handleClear = () => {
  keyword.value = ''
  resetList()
  loadData()
}

const handleDetail = (item) => {
  uni.showToast({
    title: '详情页面开发中',
    icon: 'none'
  })
}

const handleLike = async (item) => {
  item.isLiked = !item.isLiked
  uni.showToast({
    title: item.isLiked ? '已收藏' : '已取消收藏',
    icon: 'success',
    duration: 1500
  })
}

const handleAdopt = (item) => {
  const userInfo = uni.getStorageSync('userInfo')
  if (!userInfo) {
    uni.navigateTo({
      url: '/pages/login/index'
    })
    return
  }
  
  uni.showModal({
    title: '确认领养',
    content: `确定要申请领养${item.name || '这只小可爱'}吗？`,
    confirmColor: '#FF8C42',
    success: (res) => {
      if (res.confirm) {
        uni.showToast({
          title: '申请已提交',
          icon: 'success'
        })
      }
    }
  })
}

const handleReset = () => {
  filterType.value = 'all'
  filterHealth.value = 'all'
  filterAge.value = 'all'
  filterStatus.value = 'all'
}

const handleConfirmFilter = () => {
  showFilter.value = false
  resetList()
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
  background: #FFFBF5;
  display: flex;
  flex-direction: column;
}

/* ===================================
   搜索栏
   =================================== */

.search-bar {
  position: sticky;
  top: 0;
  z-index: 99;
  display: flex;
  gap: 16rpx;
  padding: 24rpx;
  background: rgba(255, 251, 245, 0.95);
  backdrop-filter: blur(20rpx);
  border-bottom: 1rpx solid #F5F5F4;
}

.search-input-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 20rpx 24rpx;
  background: #FFFFFF;
  border-radius: 40rpx;
  border: 2rpx solid #F5F5F4;
  transition: all 0.3s ease;
  
  &:focus-within {
    border-color: #FF8C42;
    box-shadow: 0 4rpx 16rpx rgba(255, 140, 66, 0.12);
  }
}

.search-icon {
  font-size: 32rpx;
  color: #A8A29E;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: #57534E;
}

.input-placeholder {
  color: #D6D3D1;
}

.clear-icon {
  font-size: 28rpx;
  color: #A8A29E;
  padding: 8rpx;
}

.filter-btn {
  width: 88rpx;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
  border-radius: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(255, 140, 66, 0.25);
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.95);
  }
}

.filter-icon {
  font-size: 40rpx;
}

/* ===================================
   分类滚动
   =================================== */

.category-scroll {
  white-space: nowrap;
  background: rgba(255, 251, 245, 0.95);
  backdrop-filter: blur(20rpx);
  padding: 20rpx 0;
  border-bottom: 1rpx solid #F5F5F4;
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
  background: #FFFFFF;
  border-radius: 40rpx;
  border: 2rpx solid transparent;
  transition: all 0.3s ease;

  &.active {
    background: linear-gradient(135deg, #FFE5D9 0%, #FFDCC5 100%);
    border-color: #FF8C42;
    transform: scale(1.05);
    
    .category-text {
      color: #FF8C42;
      font-weight: 700;
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
  color: #78716C;
  white-space: nowrap;
  transition: all 0.3s ease;
}

/* ===================================
   动物列表
   =================================== */

.animal-scroll {
  flex: 1;
  padding: 24rpx;
}

.animal-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.animal-card {
  background: linear-gradient(to bottom right, #FFFFFF 0%, #FEF7F0 100%);
  border-radius: 32rpx;
  overflow: hidden;
  border: 1rpx solid #F5F5F4;
  box-shadow: 0 4rpx 20rpx rgba(255, 140, 66, 0.08);
  transition: all 0.3s ease;
  
  &:active {
    transform: translateY(-4rpx);
    box-shadow: 0 8rpx 32rpx rgba(255, 140, 66, 0.15);
  }
}

.image-wrapper {
  position: relative;
  width: 100%;
  height: 420rpx;
  overflow: hidden;
}

.animal-image {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #FEF7F0 0%, #FFF5EB 100%);
}

.badge-wrapper {
  position: absolute;
  top: 20rpx;
  left: 20rpx;
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.badge {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 20rpx;
  border-radius: 40rpx;
  backdrop-filter: blur(10rpx);
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15);

  &.urgent {
    background: rgba(239, 68, 68, 0.95);
  }

  &.health {
    background: rgba(59, 130, 246, 0.95);
  }
}

.badge-icon {
  font-size: 24rpx;
}

.badge-text {
  font-size: 22rpx;
  font-weight: 600;
  color: #FFFFFF;
}

.gender-badge {
  position: absolute;
  bottom: 20rpx;
  right: 20rpx;
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: 32rpx;
  font-weight: 700;
  color: #FFFFFF;
  backdrop-filter: blur(10rpx);
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15);

  &.male {
    background: rgba(59, 130, 246, 0.95);
  }

  &.female {
    background: rgba(236, 72, 153, 0.95);
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
  flex: 1;
  font-size: 36rpx;
  font-weight: 700;
  color: #57534E;
  line-height: 1.2;
}

.like-btn {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #FFFFFF;
  border-radius: 50%;
  border: 2rpx solid #F5F5F4;
  padding: 0;
  transition: all 0.3s ease;
  
  &.liked {
    animation: like 0.5s ease;
  }
  
  &:active {
    transform: scale(0.9);
  }
}

@keyframes like {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
}

.like-icon {
  font-size: 36rpx;
}

.info-tags {
  display: flex;
  gap: 16rpx;
  margin-bottom: 16rpx;
}

.info-tag {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  padding: 16rpx 20rpx;
  background: linear-gradient(135deg, #FEF7F0 0%, #FFF5EB 100%);
  border-radius: 20rpx;
  border: 1rpx solid #F5F5F4;
}

.tag-label {
  font-size: 22rpx;
  color: #A8A29E;
}

.tag-value {
  font-size: 26rpx;
  font-weight: 600;
  color: #57534E;
}

.info-location {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-bottom: 16rpx;
}

.location-icon {
  font-size: 28rpx;
}

.location-text {
  font-size: 26rpx;
  color: #78716C;
}

.info-desc {
  font-size: 26rpx;
  color: #78716C;
  line-height: 1.7;
  margin-bottom: 20rpx;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 20rpx;
  border-top: 1rpx solid #F5F5F4;
}

.status-tag {
  padding: 12rpx 24rpx;
  border-radius: 40rpx;
  font-size: 24rpx;
  font-weight: 600;

  &.available {
    background: linear-gradient(135deg, #D1FAE5 0%, #A7F3D0 100%);
    color: #059669;
  }

  &.pending {
    background: linear-gradient(135deg, #FEF3C7 0%, #FDE68A 100%);
    color: #D97706;
  }

  &.adopted {
    background: linear-gradient(135deg, #DBEAFE 0%, #BFDBFE 100%);
    color: #2563EB;
  }

  &.rescued {
    background: linear-gradient(135deg, #FFE5D9 0%, #FFDCC5 100%);
    color: #FF8C42;
  }
}

.btn-adopt {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx 32rpx;
  background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
  border-radius: 40rpx;
  border: none;
  box-shadow: 0 4rpx 12rpx rgba(255, 140, 66, 0.25);
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.95);
  }
}

.btn-icon {
  font-size: 28rpx;
}

.btn-text {
  font-size: 26rpx;
  font-weight: 600;
  color: #FFFFFF;
}

/* ===================================
   加载状态
   =================================== */

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx 0;
  gap: 20rpx;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid #F5F5F4;
  border-top-color: #FF8C42;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.loading-text {
  font-size: 24rpx;
  color: #A8A29E;
}

.no-more {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40rpx 0;
}

.no-more-text {
  font-size: 24rpx;
  color: #D6D3D1;
}

.list-footer {
  height: 40rpx;
}

/* ===================================
   筛选弹窗
   =================================== */

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

.filter-content {
  width: 100%;
  max-height: 80vh;
  background: #FFFFFF;
  border-radius: 40rpx 40rpx 0 0;
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
  align-items: center;
  justify-content: space-between;
  padding: 32rpx;
  border-bottom: 1rpx solid #F5F5F4;
}

.filter-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #57534E;
}

.filter-close {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #FEF7F0;
  border-radius: 50%;
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.9);
  }
}

.close-icon {
  font-size: 32rpx;
  color: #78716C;
}

.filter-scroll {
  flex: 1;
  padding: 32rpx;
  overflow-y: auto;
}

.filter-section {
  margin-bottom: 40rpx;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.filter-label {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: #57534E;
  margin-bottom: 20rpx;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.filter-option {
  padding: 16rpx 32rpx;
  background: #FEF7F0;
  border-radius: 40rpx;
  border: 2rpx solid transparent;
  font-size: 26rpx;
  color: #78716C;
  transition: all 0.3s ease;

  &.active {
    background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
    color: #FFFFFF;
    border-color: transparent;
    transform: scale(1.05);
    font-weight: 600;
  }
  
  &:active {
    transform: scale(0.95);
  }
}

.filter-actions {
  display: flex;
  gap: 20rpx;
  padding: 32rpx;
  border-top: 1rpx solid #F5F5F4;
}

.btn-reset,
.btn-confirm {
  flex: 1;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 24rpx;
  border: none;
  font-size: 30rpx;
  font-weight: 600;
  transition: all 0.3s ease;
  
  &:active {
    transform: scale(0.98);
  }
}

.btn-reset {
  background: #FEF7F0;
  color: #78716C;
  border: 2rpx solid #F5F5F4;
}

.btn-confirm {
  background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
  color: #FFFFFF;
  box-shadow: 0 4rpx 16rpx rgba(255, 140, 66, 0.25);
}
</style>
