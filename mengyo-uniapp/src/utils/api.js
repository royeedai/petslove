/**
 * API接口定义
 */
import request from './request'

// 用户相关接口
export const userApi = {
  // 微信登录
  wxLogin(data) {
    return request({
      url: '/user/wx-login',
      method: 'POST',
      data
    })
  },
  
  // 手机号登录
  phoneLogin(data) {
    return request({
      url: '/user/phone-login',
      method: 'POST',
      data
    })
  },
  
  // 发送验证码
  sendCode(phone) {
    return request({
      url: '/user/send-code',
      method: 'POST',
      data: { phone }
    })
  },
  
  // 获取用户信息
  getUserInfo(userId) {
    return request({
      url: '/user/info',
      method: 'GET',
      data: { userId }
    })
  },
  
  // 更新用户信息
  updateUserInfo(userId, data) {
    return request({
      url: '/user/info',
      method: 'PUT',
      data: { userId, ...data }
    })
  },
  
  // 实名认证
  verify(userId, data) {
    return request({
      url: '/user/verify',
      method: 'POST',
      data: { userId, ...data }
    })
  },
  
  // 获取积分记录
  getPointsRecords(userId, limit = 20) {
    return request({
      url: '/user/points/records',
      method: 'GET',
      data: { userId, limit }
    })
  }
}

// 文件上传相关接口
const BASE_URL = 'http://localhost:8080/api'

export const fileApi = {
  // 上传图片
  uploadImage(filePath) {
    return new Promise((resolve, reject) => {
      const token = uni.getStorageSync('token')
      
      uni.uploadFile({
        url: BASE_URL + '/system/file/upload/image',
        filePath: filePath,
        name: 'file',
        header: {
          'Authorization': token ? `Bearer ${token}` : ''
        },
        success: (res) => {
          try {
            const data = JSON.parse(res.data)
            if (data.code === 200) {
              resolve(data)
            } else {
              uni.showToast({
                title: data.message || '上传失败',
                icon: 'none'
              })
              reject(data)
            }
          } catch (error) {
            uni.showToast({
              title: '上传失败',
              icon: 'none'
            })
            reject(error)
          }
        },
        fail: (err) => {
          uni.showToast({
            title: '上传失败',
            icon: 'none'
          })
          reject(err)
        }
      })
    })
  },

  // 上传文件
  uploadFile(filePath) {
    return new Promise((resolve, reject) => {
      const token = uni.getStorageSync('token')
      
      uni.uploadFile({
        url: BASE_URL + '/system/file/upload',
        filePath: filePath,
        name: 'file',
        header: {
          'Authorization': token ? `Bearer ${token}` : ''
        },
        success: (res) => {
          try {
            const data = JSON.parse(res.data)
            if (data.code === 200) {
              resolve(data)
            } else {
              uni.showToast({
                title: data.message || '上传失败',
                icon: 'none'
              })
              reject(data)
            }
          } catch (error) {
            uni.showToast({
              title: '上传失败',
              icon: 'none'
            })
            reject(error)
          }
        },
        fail: (err) => {
          uni.showToast({
            title: '上传失败',
            icon: 'none'
          })
          reject(err)
        }
      })
    })
  }
}
