<template>
  <div class="file-upload">
    <el-upload
      :action="uploadUrl"
      :headers="uploadHeaders"
      :accept="accept"
      :multiple="multiple"
      :limit="limit"
      :file-list="fileList"
      :list-type="listType"
      :before-upload="handleBeforeUpload"
      :on-success="handleSuccess"
      :on-error="handleError"
      :on-remove="handleRemove"
      :on-exceed="handleExceed"
    >
      <template v-if="listType === 'picture-card'">
        <el-icon><Plus /></el-icon>
      </template>
      <template v-else>
        <el-button type="primary">
          <el-icon><Upload /></el-icon>
          选择文件
        </el-button>
      </template>
    </el-upload>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Upload } from '@element-plus/icons-vue'
import { getToken } from '@/utils/auth'

const props = defineProps({
  modelValue: {
    type: [String, Array],
    default: ''
  },
  // 上传类型：image-图片，file-文件
  uploadType: {
    type: String,
    default: 'image'
  },
  // 接受的文件类型
  accept: {
    type: String,
    default: ''
  },
  // 是否多选
  multiple: {
    type: Boolean,
    default: false
  },
  // 最大上传数量
  limit: {
    type: Number,
    default: 5
  },
  // 列表类型
  listType: {
    type: String,
    default: 'picture-card'
  },
  // 最大文件大小（MB）
  maxSize: {
    type: Number,
    default: 10
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

// 上传URL
const uploadUrl = import.meta.env.VITE_API_BASE_URL + 
  (props.uploadType === 'image' ? '/system/file/upload/image' : '/system/file/upload')

// 上传请求头
const uploadHeaders = {
  Authorization: getToken()
}

// 文件列表
const fileList = ref([])

// 监听modelValue变化
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    if (Array.isArray(newVal)) {
      fileList.value = newVal.map((url, index) => ({
        uid: Date.now() + index,
        name: url.substring(url.lastIndexOf('/') + 1),
        url: url
      }))
    } else if (newVal) {
      fileList.value = [{
        uid: Date.now(),
        name: newVal.substring(newVal.lastIndexOf('/') + 1),
        url: newVal
      }]
    }
  } else {
    fileList.value = []
  }
}, { immediate: true })

// 上传前检查
const handleBeforeUpload = (file) => {
  // 检查文件类型
  if (props.uploadType === 'image') {
    const isImage = file.type.startsWith('image/')
    if (!isImage) {
      ElMessage.error('只能上传图片文件!')
      return false
    }
  }

  // 检查文件大小
  const isLtMaxSize = file.size / 1024 / 1024 < props.maxSize
  if (!isLtMaxSize) {
    ElMessage.error(`文件大小不能超过 ${props.maxSize}MB!`)
    return false
  }

  return true
}

// 上传成功
const handleSuccess = (response, file) => {
  if (response.code === 200) {
    const url = response.data.url
    
    if (props.multiple) {
      const urls = fileList.value.map(f => f.url)
      emit('update:modelValue', urls)
    } else {
      emit('update:modelValue', url)
    }
    
    emit('success', url)
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 上传失败
const handleError = (error) => {
  console.error('上传失败', error)
  ElMessage.error('上传失败')
}

// 移除文件
const handleRemove = (file) => {
  if (props.multiple) {
    const urls = fileList.value.filter(f => f.uid !== file.uid).map(f => f.url)
    emit('update:modelValue', urls)
  } else {
    emit('update:modelValue', '')
  }
}

// 超出限制
const handleExceed = () => {
  ElMessage.warning(`最多只能上传 ${props.limit} 个文件`)
}
</script>

<style scoped lang="scss">
.file-upload {
  :deep(.el-upload--picture-card) {
    width: 148px;
    height: 148px;
  }

  :deep(.el-upload-list--picture-card .el-upload-list__item) {
    width: 148px;
    height: 148px;
  }
}
</style>
