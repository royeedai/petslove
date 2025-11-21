<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">系统配置</h2>
    </div>

    <el-card class="config-card">
      <template #header>
        <div class="card-header">
          <span>基础配置</span>
          <el-button type="primary" @click="saveBasicConfig">保存基础配置</el-button>
        </div>
      </template>
      
      <el-form :model="basicConfig" label-width="120px">
        <el-form-item label="网站名称">
          <el-input v-model="basicConfig.site_name" placeholder="请输入网站名称" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="basicConfig.contact_phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="联系邮箱">
          <el-input v-model="basicConfig.contact_email" placeholder="请输入联系邮箱" />
        </el-form-item>
        <el-form-item label="网站Logo">
          <el-upload
            class="logo-uploader"
            :show-file-list="false"
            :http-request="handleLogoUpload"
            :before-upload="beforeImageUpload"
          >
            <img v-if="basicConfig.site_logo" :src="basicConfig.site_logo" class="logo" />
            <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="config-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>积分配置</span>
          <el-button type="primary" @click="savePointsConfig">保存积分配置</el-button>
        </div>
      </template>
      
      <el-form :model="pointsConfig" label-width="120px">
        <el-form-item label="签到积分">
          <el-input-number v-model="pointsConfig.points_sign" :min="0" />
        </el-form-item>
        <el-form-item label="发帖积分">
          <el-input-number v-model="pointsConfig.points_post" :min="0" />
        </el-form-item>
        <el-form-item label="救助积分">
          <el-input-number v-model="pointsConfig.points_rescue" :min="0" />
        </el-form-item>
        <el-form-item label="领养积分">
          <el-input-number v-model="pointsConfig.points_adoption" :min="0" />
        </el-form-item>
        <el-form-item label="捐赠积分">
          <el-input-number v-model="pointsConfig.points_donation" :min="0" />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="config-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>存储配置</span>
          <el-button type="primary" @click="saveStorageConfig">保存存储配置</el-button>
        </div>
      </template>
      
      <el-form :model="storageConfig" label-width="140px">
        <el-form-item label="存储方式">
          <el-radio-group v-model="storageConfig.storage_type">
            <el-radio value="local">本地存储</el-radio>
            <el-radio value="oss">阿里云OSS</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <template v-if="storageConfig.storage_type === 'oss'">
          <el-divider content-position="left">阿里云OSS配置</el-divider>
          <el-form-item label="AccessKeyId">
            <el-input 
              v-model="storageConfig.oss_access_key_id" 
              placeholder="请输入阿里云OSS AccessKeyId"
              show-password
            />
          </el-form-item>
          <el-form-item label="AccessKeySecret">
            <el-input 
              v-model="storageConfig.oss_access_key_secret" 
              placeholder="请输入阿里云OSS AccessKeySecret"
              show-password
            />
          </el-form-item>
          <el-form-item label="Endpoint">
            <el-input 
              v-model="storageConfig.oss_endpoint" 
              placeholder="例如：oss-cn-shenzhen.aliyuncs.com"
            />
          </el-form-item>
          <el-form-item label="Bucket名称">
            <el-input 
              v-model="storageConfig.oss_bucket_name" 
              placeholder="请输入Bucket名称"
            />
          </el-form-item>
          <el-form-item label="访问域名">
            <el-input 
              v-model="storageConfig.oss_domain" 
              placeholder="选填，如配置了自定义域名请填写"
            />
          </el-form-item>
        </template>
      </el-form>

      <el-alert 
        v-if="storageConfig.storage_type === 'oss'" 
        type="info" 
        :closable="false"
        style="margin-top: 20px;"
      >
        <template #title>
          <div>
            <p>阿里云OSS配置说明：</p>
            <ul style="margin: 10px 0; padding-left: 20px;">
              <li>AccessKeyId和AccessKeySecret可在阿里云控制台获取</li>
              <li>Endpoint格式：oss-[区域].aliyuncs.com</li>
              <li>访问域名为选填项，如未配置自定义域名可不填</li>
              <li>请确保Bucket已设置公共读权限或配置了CDN加速域名</li>
            </ul>
          </div>
        </template>
      </el-alert>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getAllConfigs, saveConfig, uploadImage } from '@/api/system'

// 基础配置
const basicConfig = ref({
  site_name: '',
  site_logo: '',
  contact_phone: '',
  contact_email: ''
})

// 积分配置
const pointsConfig = ref({
  points_sign: 5,
  points_post: 10,
  points_rescue: 50,
  points_adoption: 100,
  points_donation: 20
})

// 存储配置
const storageConfig = ref({
  storage_type: 'local',
  oss_access_key_id: '',
  oss_access_key_secret: '',
  oss_endpoint: '',
  oss_bucket_name: '',
  oss_domain: ''
})

// 获取所有配置
const loadConfigs = async () => {
  try {
    const res = await getAllConfigs()
    if (res.code === 200) {
      const configs = res.data
      configs.forEach(config => {
        // 基础配置
        if (config.configKey in basicConfig.value) {
          basicConfig.value[config.configKey] = config.configValue
        }
        // 积分配置
        if (config.configKey in pointsConfig.value) {
          pointsConfig.value[config.configKey] = parseInt(config.configValue)
        }
        // 存储配置
        if (config.configKey in storageConfig.value) {
          storageConfig.value[config.configKey] = config.configValue
        }
      })
    }
  } catch (error) {
    console.error('加载配置失败', error)
    ElMessage.error('加载配置失败')
  }
}

// 保存基础配置
const saveBasicConfig = async () => {
  try {
    for (const key in basicConfig.value) {
      await saveConfig({
        configKey: key,
        configValue: basicConfig.value[key],
        description: ''
      })
    }
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败', error)
    ElMessage.error('保存失败')
  }
}

// 保存积分配置
const savePointsConfig = async () => {
  try {
    for (const key in pointsConfig.value) {
      await saveConfig({
        configKey: key,
        configValue: pointsConfig.value[key].toString(),
        description: ''
      })
    }
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败', error)
    ElMessage.error('保存失败')
  }
}

// 保存存储配置
const saveStorageConfig = async () => {
  try {
    for (const key in storageConfig.value) {
      await saveConfig({
        configKey: key,
        configValue: storageConfig.value[key],
        description: ''
      })
    }
    ElMessage.success('保存成功，配置将在下次启动时生效')
  } catch (error) {
    console.error('保存失败', error)
    ElMessage.error('保存失败')
  }
}

// Logo上传前检查
const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 处理Logo上传
const handleLogoUpload = async ({ file }) => {
  try {
    const res = await uploadImage(file)
    if (res.code === 200) {
      basicConfig.value.site_logo = res.data.url
      ElMessage.success('上传成功')
    }
  } catch (error) {
    console.error('上传失败', error)
    ElMessage.error('上传失败')
  }
}

onMounted(() => {
  loadConfigs()
})
</script>

<style scoped lang="scss">
.config-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}

.logo-uploader {
  :deep(.el-upload) {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
    
    &:hover {
      border-color: var(--el-color-primary);
    }
  }
}

.logo-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}

.logo {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: contain;
}
</style>
