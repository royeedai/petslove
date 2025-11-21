<template>
  <view class="container">
    <!-- 轮播图 -->
    <view class="banner">
      <swiper :indicator-dots="true" :autoplay="true" :interval="3000" :duration="500">
        <swiper-item v-for="(item, index) in banners" :key="index">
          <image :src="item.imageUrl" mode="aspectFill" class="banner-image"></image>
        </swiper-item>
      </swiper>
    </view>

    <!-- 功能入口 -->
    <view class="features">
      <view class="feature-item" @click="navigateTo('/pages/rescue/list')">
        <image src="/static/icons/rescue.png" class="feature-icon"></image>
        <text class="feature-text">救助任务</text>
      </view>
      <view class="feature-item" @click="navigateTo('/pages/animal/list')">
        <image src="/static/icons/animal.png" class="feature-icon"></image>
        <text class="feature-text">流浪动物</text>
      </view>
      <view class="feature-item" @click="navigateTo('/pages/donation/index')">
        <image src="/static/icons/donation.png" class="feature-icon"></image>
        <text class="feature-text">爱心捐赠</text>
      </view>
      <view class="feature-item" @click="navigateTo('/pages/adoption/index')">
        <image src="/static/icons/adoption.png" class="feature-icon"></image>
        <text class="feature-text">领养申请</text>
      </view>
    </view>

    <!-- 最新救助任务 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">最新救助</text>
        <text class="section-more" @click="navigateTo('/pages/rescue/list')">更多 ></text>
      </view>
      <view class="rescue-list">
        <view class="rescue-item" v-for="item in rescueList" :key="item.id">
          <image :src="item.cover" mode="aspectFill" class="rescue-image"></image>
          <view class="rescue-info">
            <text class="rescue-title">{{ item.title }}</text>
            <text class="rescue-address">{{ item.address }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 社区动态 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">社区动态</text>
        <text class="section-more" @click="navigateTo('/pages/community/list')">更多 ></text>
      </view>
      <view class="post-list">
        <view class="post-item" v-for="item in postList" :key="item.id">
          <view class="post-user">
            <image :src="item.userAvatar" mode="aspectFill" class="user-avatar"></image>
            <text class="user-nickname">{{ item.userNickname }}</text>
          </view>
          <text class="post-content">{{ item.content }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const banners = ref([])
const rescueList = ref([])
const postList = ref([])

onMounted(() => {
  loadData()
})

const loadData = () => {
  // TODO: 加载首页数据
  console.log('加载首页数据')
}

const navigateTo = (url) => {
  uni.navigateTo({ url })
}
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.banner {
  width: 100%;
  height: 400rpx;
  
  swiper {
    height: 100%;
  }
  
  .banner-image {
    width: 100%;
    height: 100%;
  }
}

.features {
  display: flex;
  flex-wrap: wrap;
  padding: 40rpx;
  background-color: #fff;
  
  .feature-item {
    width: 25%;
    text-align: center;
    margin-bottom: 20rpx;
    
    .feature-icon {
      width: 80rpx;
      height: 80rpx;
    }
    
    .feature-text {
      display: block;
      margin-top: 10rpx;
      font-size: 24rpx;
      color: #666;
    }
  }
}

.section {
  margin-top: 20rpx;
  padding: 30rpx;
  background-color: #fff;
  
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;
    
    .section-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }
    
    .section-more {
      font-size: 28rpx;
      color: #999;
    }
  }
}

.rescue-list {
  .rescue-item {
    display: flex;
    margin-bottom: 20rpx;
    
    .rescue-image {
      width: 160rpx;
      height: 160rpx;
      border-radius: 8rpx;
    }
    
    .rescue-info {
      flex: 1;
      margin-left: 20rpx;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      
      .rescue-title {
        font-size: 28rpx;
        color: #333;
      }
      
      .rescue-address {
        font-size: 24rpx;
        color: #999;
      }
    }
  }
}

.post-list {
  .post-item {
    margin-bottom: 30rpx;
    
    .post-user {
      display: flex;
      align-items: center;
      margin-bottom: 15rpx;
      
      .user-avatar {
        width: 60rpx;
        height: 60rpx;
        border-radius: 50%;
      }
      
      .user-nickname {
        margin-left: 15rpx;
        font-size: 26rpx;
        color: #333;
      }
    }
    
    .post-content {
      font-size: 28rpx;
      color: #666;
      line-height: 1.6;
    }
  }
}
</style>
