<template>
  <div class="dashboard-container">
    <div class="page-header">
      <h2 class="page-title">数据概览</h2>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon user">
              <el-icon><User /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.userCount }}</div>
              <div class="stats-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon animal">
              <el-icon><ShoppingCart /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.animalCount }}</div>
              <div class="stats-label">动物数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon rescue">
              <el-icon><FirstAidKit /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.rescueCount }}</div>
              <div class="stats-label">救助任务</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon donation">
              <el-icon><Present /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">¥{{ stats.donationAmount }}</div>
              <div class="stats-label">捐赠总额</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最近救助任务</span>
          </template>
          <el-empty description="暂无数据" v-if="recentRescues.length === 0" />
          <el-table :data="recentRescues" v-else>
            <el-table-column prop="title" label="任务名称" />
            <el-table-column prop="status" label="状态" />
            <el-table-column prop="createTime" label="创建时间" />
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最新用户</span>
          </template>
          <el-empty description="暂无数据" v-if="recentUsers.length === 0" />
          <el-table :data="recentUsers" v-else>
            <el-table-column prop="nickname" label="昵称" />
            <el-table-column prop="phone" label="手机号" />
            <el-table-column prop="createTime" label="注册时间" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const stats = ref({
  userCount: 0,
  animalCount: 0,
  rescueCount: 0,
  donationAmount: 0
})

const recentRescues = ref([])
const recentUsers = ref([])

onMounted(() => {
  loadData()
})

const loadData = async () => {
  // TODO: 加载统计数据
  stats.value = {
    userCount: 1234,
    animalCount: 567,
    rescueCount: 89,
    donationAmount: 12345.67
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  .page-header {
    margin-bottom: 20px;
    
    .page-title {
      font-size: 20px;
      font-weight: bold;
      color: #333;
    }
  }
  
  .stats-row {
    margin-bottom: 20px;
  }
  
  .stats-card {
    .stats-content {
      display: flex;
      align-items: center;
      
      .stats-icon {
        width: 60px;
        height: 60px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 8px;
        font-size: 28px;
        color: #fff;
        margin-right: 20px;
        
        &.user {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        
        &.animal {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }
        
        &.rescue {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }
        
        &.donation {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }
      }
      
      .stats-info {
        flex: 1;
        
        .stats-value {
          font-size: 24px;
          font-weight: bold;
          color: #333;
          margin-bottom: 5px;
        }
        
        .stats-label {
          font-size: 14px;
          color: #999;
        }
      }
    }
  }
}
</style>
