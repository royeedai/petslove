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
    <scroll-view scroll-x class="category-scroll">
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
        <image :src="item.photo" mode="aspectFill" class="animal-image"></image>
        
        <!-- 徽章标签 -->
        <view class="badge-wrapper">
          <view v-if="item.isUrgent" class="badge urgent">
            🚨 紧急
          </view>
          <view v-if="item.healthStatus === 'injured'" class="badge health">
            🏥 受伤
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
              <text class="info-value">{{ item.breed }}</text>
            </view>
            <view class="info-item">
              <text class="info-label">年龄</text>
              <text class="info-value">{{ item.age }}</text>
            </view>
          </view>

          <view class="info-row">
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
            <view class="action-btns">
              <button class="btn-action" @click.stop="handleLike(item)">
                <text class="btn-icon">{{ item.isLiked ? '❤️' : '🤍' }}</text>
              </button>
              <button class="btn-action primary" @click.stop="handleAdopt(item)">
                <text class="btn-icon">🏠</text>
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
import { ref, onMounted } from 'vue'

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

onMounted(() => {
  loadData()
})

const loadData = () => {
  // TODO: 加载动物列表数据
  console.log('加载动物列表')
}

const handleSearch = () => {
  console.log('搜索:', keyword.value)
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
    icon: 'success'
  })
}

const handleAdopt = (item) => {
  uni.navigateTo({
    url: `/pages/adoption/apply?animalId=${item.id}`
  })
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
  background: #F8F9FA;
  padding-bottom: 20rpx;
}

/* 搜索栏 */
.search-bar {
  display: flex;
  gap: 16rpx;
  padding: 24rpx;
  background: #fff;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.search-input-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 20rpx 24rpx;
  background: #F8F9FA;
  border-radius: 16rpx;
}

.search-icon {
  font-size: 32rpx;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: #2C3E50;
}

.filter-btn {
  width: 88rpx;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
  border-radius: 16rpx;
  box-shadow: 0 4rpx 16rpx rgba(255, 140, 66, 0.3);
}

.filter-icon {
  font-size: 36rpx;
}

/* 分类滚动 */
.category-scroll {
  white-space: nowrap;
  background: #fff;
  padding: 20rpx 0;
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
  background: #F8F9FA;
  border-radius: 40rpx;
  transition: all 0.3s ease;

  &.active {
    background: linear-gradient(135deg, #FFE5D9 0%, #FFDCC5 100%);
    
    .category-text {
      color: #FF8C42;
      font-weight: 600;
    }
  }
}

.category-emoji {
  font-size: 32rpx;
}

.category-text {
  font-size: 26rpx;
  color: #7F8C8D;
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
  background: #fff;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.animal-image {
  width: 100%;
  height: 400rpx;
  background: linear-gradient(135deg, #F8F9FA 0%, #E8EAED 100%);
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
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  font-size: 22rpx;
  font-weight: 500;
  backdrop-filter: blur(10rpx);

  &.urgent {
    background: rgba(255, 71, 87, 0.9);
    color: #fff;
  }

  &.health {
    background: rgba(52, 152, 219, 0.9);
    color: #fff;
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
  color: #2C3E50;
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

  &.male {
    background: linear-gradient(135deg, #89CFF0 0%, #4A90E2 100%);
    color: #fff;
  }

  &.female {
    background: linear-gradient(135deg, #FFB6D9 0%, #FF85B3 100%);
    color: #fff;
  }
}

.info-row {
  display: flex;
  align-items: center;
  gap: 32rpx;
  margin-bottom: 16rpx;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.info-label {
  font-size: 24rpx;
  color: #95A5A6;
}

.info-value {
  font-size: 26rpx;
  color: #2C3E50;
  font-weight: 500;
}

.location-icon {
  font-size: 28rpx;
}

.location-text {
  font-size: 26rpx;
  color: #7F8C8D;
}

.info-desc {
  margin: 20rpx 0;
  font-size: 26rpx;
  color: #7F8C8D;
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
  border-top: 1rpx solid #E8EAED;
}

.status-tag {
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  font-weight: 500;

  &.available {
    background: linear-gradient(135deg, #D4EDDA 0%, #C3E6CB 100%);
    color: #28A745;
  }

  &.pending {
    background: linear-gradient(135deg, #FFF3CD 0%, #FFE8A1 100%);
    color: #FFC107;
  }

  &.adopted {
    background: linear-gradient(135deg, #D1ECF1 0%, #BEE5EB 100%);
    color: #17A2B8;
  }

  &.rescued {
    background: linear-gradient(135deg, #FFE5D9 0%, #FFDCC5 100%);
    color: #FF8C42;
  }
}

.action-btns {
  display: flex;
  gap: 16rpx;
}

.btn-action {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 24rpx;
  border-radius: 40rpx;
  border: none;
  background: #F8F9FA;
  font-size: 26rpx;
  color: #7F8C8D;
  transition: all 0.3s ease;

  &.primary {
    background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
    color: #fff;
    box-shadow: 0 4rpx 12rpx rgba(255, 140, 66, 0.3);
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
  color: #7F8C8D;
  font-weight: 500;
}

.empty-desc {
  font-size: 24rpx;
  color: #95A5A6;
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
  background: #fff;
  border-radius: 40rpx 40rpx 0 0;
  padding: 40rpx 32rpx;
}

.filter-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 40rpx;
}

.filter-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #2C3E50;
}

.filter-close {
  font-size: 40rpx;
  color: #95A5A6;
  line-height: 1;
}

.filter-section {
  margin-bottom: 40rpx;
}

.filter-label {
  display: block;
  font-size: 28rpx;
  font-weight: 500;
  color: #2C3E50;
  margin-bottom: 20rpx;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.filter-option {
  padding: 16rpx 32rpx;
  background: #F8F9FA;
  border-radius: 40rpx;
  font-size: 26rpx;
  color: #7F8C8D;
  transition: all 0.3s ease;

  &.active {
    background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
    color: #fff;
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
  border-radius: 16rpx;
  font-size: 30rpx;
  border: none;
}

.btn-reset {
  background: #F8F9FA;
  color: #7F8C8D;
}

.btn-confirm {
  background: linear-gradient(135deg, #FF9D5C 0%, #FF7F29 100%);
  color: #fff;
  box-shadow: 0 4rpx 16rpx rgba(255, 140, 66, 0.3);
}
</style>
