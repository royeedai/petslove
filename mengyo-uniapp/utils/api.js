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
