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

// 救助相关接口
export const rescueApi = {
  // 获取救助任务列表
  getTaskList(params) {
    return request({
      url: '/rescue/tasks',
      method: 'GET',
      data: params
    })
  },
  
  // 获取救助任务详情
  getTaskDetail(id) {
    return request({
      url: `/rescue/task/${id}`,
      method: 'GET'
    })
  },
  
  // 发布救助任务
  publishTask(data) {
    return request({
      url: '/rescue/task/publish',
      method: 'POST',
      data
    })
  },
  
  // 接受救助任务
  acceptTask(id) {
    return request({
      url: `/rescue/task/${id}/accept`,
      method: 'POST'
    })
  },
  
  // 完成救助任务
  completeTask(id, data) {
    return request({
      url: `/rescue/task/${id}/complete`,
      method: 'POST',
      data
    })
  },
  
  // 获取流浪动物列表
  getAnimalList(params) {
    return request({
      url: '/rescue/animals',
      method: 'GET',
      data: params
    })
  },
  
  // 获取动物详情
  getAnimalDetail(id) {
    return request({
      url: `/rescue/animal/${id}`,
      method: 'GET'
    })
  },
  
  // 报备流浪动物
  reportAnimal(data) {
    return request({
      url: '/rescue/animal/report',
      method: 'POST',
      data
    })
  }
}

// 社区相关接口
export const communityApi = {
  // 获取帖子列表
  getPostList(params) {
    return request({
      url: '/community/posts',
      method: 'GET',
      data: params
    })
  },
  
  // 获取帖子详情
  getPostDetail(id) {
    return request({
      url: `/community/post/${id}`,
      method: 'GET'
    })
  },
  
  // 发布帖子
  publishPost(data) {
    return request({
      url: '/community/post',
      method: 'POST',
      data
    })
  },
  
  // 删除帖子
  deletePost(id) {
    return request({
      url: `/community/post/${id}`,
      method: 'DELETE'
    })
  },
  
  // 获取评论列表
  getCommentList(postId) {
    return request({
      url: `/community/post/${postId}/comments`,
      method: 'GET'
    })
  },
  
  // 发表评论
  publishComment(data) {
    return request({
      url: '/community/comment',
      method: 'POST',
      data
    })
  },
  
  // 点赞/取消点赞
  toggleLike(targetType, targetId) {
    return request({
      url: '/community/like',
      method: 'POST',
      data: { targetType, targetId }
    })
  },
  
  // 检查是否已点赞
  isLiked(targetType, targetId) {
    return request({
      url: '/community/is-liked',
      method: 'GET',
      data: { targetType, targetId }
    })
  }
}

// 领养相关接口
export const adoptionApi = {
  // 提交领养申请
  submitApplication(data) {
    return request({
      url: '/adoption/apply',
      method: 'POST',
      data
    })
  },
  
  // 获取领养申请列表
  getApplicationList(params) {
    return request({
      url: '/adoption/applications',
      method: 'GET',
      data: params
    })
  },
  
  // 获取我的领养申请
  getMyApplications() {
    return request({
      url: '/adoption/my-applications',
      method: 'GET'
    })
  },
  
  // 获取申请详情
  getApplicationDetail(id) {
    return request({
      url: `/adoption/application/${id}`,
      method: 'GET'
    })
  }
}

// 捐赠相关接口
export const donationApi = {
  // 获取捐赠项目列表
  getProjectList(params) {
    return request({
      url: '/donation/projects',
      method: 'GET',
      data: params
    })
  },
  
  // 获取项目详情
  getProjectDetail(id) {
    return request({
      url: `/donation/project/${id}`,
      method: 'GET'
    })
  },
  
  // 发起捐赠
  makeDonation(data) {
    return request({
      url: '/donation/donate',
      method: 'POST',
      data
    })
  }
}

// 文件上传相关接口
const BASE_URL = '/api'

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
