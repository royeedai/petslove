/**
 * 文件上传工具类
 */
import { fileApi } from './api'

/**
 * 选择并上传图片
 * @param {Object} options 配置选项
 * @param {Number} options.count 最多可选择的图片数量，默认9
 * @param {Array} options.sizeType 所选的图片的尺寸，默认['original', 'compressed']
 * @param {Array} options.sourceType 选择图片的来源，默认['album', 'camera']
 * @returns {Promise<Array>} 上传成功后的图片URL数组
 */
export function chooseAndUploadImage(options = {}) {
  const {
    count = 9,
    sizeType = ['original', 'compressed'],
    sourceType = ['album', 'camera']
  } = options

  return new Promise((resolve, reject) => {
    uni.chooseImage({
      count,
      sizeType,
      sourceType,
      success: async (res) => {
        const tempFilePaths = res.tempFilePaths
        
        // 显示加载提示
        uni.showLoading({
          title: '上传中...',
          mask: true
        })

        try {
          // 并发上传所有图片
          const uploadPromises = tempFilePaths.map(filePath => fileApi.uploadImage(filePath))
          const results = await Promise.all(uploadPromises)
          
          // 提取所有图片URL
          const urls = results.map(result => result.data.url)
          
          uni.hideLoading()
          uni.showToast({
            title: '上传成功',
            icon: 'success'
          })
          
          resolve(urls)
        } catch (error) {
          uni.hideLoading()
          console.error('上传失败', error)
          reject(error)
        }
      },
      fail: (err) => {
        console.error('选择图片失败', err)
        reject(err)
      }
    })
  })
}

/**
 * 选择并上传单张图片
 * @param {Object} options 配置选项
 * @returns {Promise<String>} 上传成功后的图片URL
 */
export function chooseAndUploadSingleImage(options = {}) {
  return chooseAndUploadImage({ ...options, count: 1 }).then(urls => urls[0])
}

/**
 * 预览图片
 * @param {String} current 当前显示图片的链接
 * @param {Array} urls 需要预览的图片链接列表
 */
export function previewImage(current, urls) {
  uni.previewImage({
    current,
    urls
  })
}

/**
 * 压缩图片
 * @param {String} src 图片路径
 * @param {Number} quality 压缩质量，范围0-100，默认80
 * @returns {Promise<String>} 压缩后的临时文件路径
 */
export function compressImage(src, quality = 80) {
  return new Promise((resolve, reject) => {
    uni.compressImage({
      src,
      quality,
      success: (res) => {
        resolve(res.tempFilePath)
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}

/**
 * 获取图片信息
 * @param {String} src 图片路径
 * @returns {Promise<Object>} 图片信息
 */
export function getImageInfo(src) {
  return new Promise((resolve, reject) => {
    uni.getImageInfo({
      src,
      success: (res) => {
        resolve(res)
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}
