import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    redirect: '/user/list',
    meta: { title: '用户管理', icon: 'User' },
    children: [
      {
        path: 'list',
        name: 'UserList',
        component: () => import('@/views/user/list.vue'),
        meta: { title: '用户列表', icon: 'UserFilled' }
      },
      {
        path: 'points',
        name: 'UserPoints',
        component: () => import('@/views/user/points.vue'),
        meta: { title: '积分管理', icon: 'Star' }
      }
    ]
  },
  {
    path: '/animal',
    component: Layout,
    redirect: '/animal/list',
    meta: { title: '动物管理', icon: 'ShoppingCart' },
    children: [
      {
        path: 'list',
        name: 'AnimalList',
        component: () => import('@/views/animal/list.vue'),
        meta: { title: '动物列表', icon: 'List' }
      }
    ]
  },
  {
    path: '/rescue',
    component: Layout,
    redirect: '/rescue/list',
    meta: { title: '救助管理', icon: 'FirstAidKit' },
    children: [
      {
        path: 'list',
        name: 'RescueList',
        component: () => import('@/views/rescue/list.vue'),
        meta: { title: '救助任务', icon: 'List' }
      }
    ]
  },
  {
    path: '/adoption',
    component: Layout,
    redirect: '/adoption/list',
    meta: { title: '领养管理', icon: 'House' },
    children: [
      {
        path: 'list',
        name: 'AdoptionList',
        component: () => import('@/views/adoption/list.vue'),
        meta: { title: '领养申请', icon: 'List' }
      }
    ]
  },
  {
    path: '/donation',
    component: Layout,
    redirect: '/donation/list',
    meta: { title: '捐赠管理', icon: 'Present' },
    children: [
      {
        path: 'list',
        name: 'DonationList',
        component: () => import('@/views/donation/list.vue'),
        meta: { title: '捐赠记录', icon: 'List' }
      }
    ]
  },
  {
    path: '/community',
    component: Layout,
    redirect: '/community/post',
    meta: { title: '社区管理', icon: 'ChatDotRound' },
    children: [
      {
        path: 'post',
        name: 'PostList',
        component: () => import('@/views/community/post.vue'),
        meta: { title: '帖子管理', icon: 'Document' }
      },
      {
        path: 'comment',
        name: 'CommentList',
        component: () => import('@/views/community/comment.vue'),
        meta: { title: '评论管理', icon: 'ChatLineRound' }
      }
    ]
  },
  {
    path: '/system',
    component: Layout,
    redirect: '/system/config',
    meta: { title: '系统管理', icon: 'Setting' },
    children: [
      {
        path: 'config',
        name: 'SystemConfig',
        component: () => import('@/views/system/config.vue'),
        meta: { title: '系统配置', icon: 'Tools' }
      },
      {
        path: 'banner',
        name: 'BannerManage',
        component: () => import('@/views/system/banner.vue'),
        meta: { title: '轮播图管理', icon: 'Picture' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.path === '/login') {
    next()
  } else {
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
