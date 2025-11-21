import request from '@/utils/request'

/**
 * 用户登录
 */
export function login(data) {
  return request({
    url: '/user/phone-login',
    method: 'post',
    data
  })
}

/**
 * 获取用户信息
 */
export function getUserInfo(userId) {
  return request({
    url: '/user/info',
    method: 'get',
    params: { userId }
  })
}

/**
 * 获取用户列表
 */
export function getUserList(params) {
  return request({
    url: '/user/list',
    method: 'get',
    params
  })
}

/**
 * 更新用户信息
 */
export function updateUser(userId, data) {
  return request({
    url: '/user/info',
    method: 'put',
    params: { userId },
    data
  })
}

/**
 * 获取积分记录
 */
export function getPointsRecords(userId, limit) {
  return request({
    url: '/user/points/records',
    method: 'get',
    params: { userId, limit }
  })
}

/**
 * 增加用户积分
 */
export function addPoints(data) {
  return request({
    url: '/user/points/add',
    method: 'post',
    params: data
  })
}

/**
 * 扣减用户积分
 */
export function deductPoints(data) {
  return request({
    url: '/user/points/deduct',
    method: 'post',
    params: data
  })
}
