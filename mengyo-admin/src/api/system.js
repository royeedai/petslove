import request from '@/utils/request'

/**
 * 获取轮播图列表
 */
export function getBannerList(params) {
  return request({
    url: '/system/banners',
    method: 'get',
    params
  })
}

/**
 * 保存轮播图
 */
export function saveBanner(data) {
  return request({
    url: '/system/banner',
    method: 'post',
    data
  })
}

/**
 * 删除轮播图
 */
export function deleteBanner(id) {
  return request({
    url: `/system/banner/${id}`,
    method: 'delete'
  })
}

/**
 * 获取所有配置
 */
export function getAllConfigs() {
  return request({
    url: '/system/configs',
    method: 'get'
  })
}

/**
 * 获取配置值
 */
export function getConfigValue(key) {
  return request({
    url: `/system/config/${key}`,
    method: 'get'
  })
}

/**
 * 保存配置
 */
export function saveConfig(data) {
  return request({
    url: '/system/config',
    method: 'post',
    params: data
  })
}

/**
 * 上传文件
 */
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/system/file/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 上传图片
 */
export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/system/file/upload/image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 删除文件
 */
export function deleteFile(url) {
  return request({
    url: '/system/file/delete',
    method: 'delete',
    params: { url }
  })
}

/**
 * 获取存储类型
 */
export function getStorageType() {
  return request({
    url: '/system/file/storage-type',
    method: 'get'
  })
}
