import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import ProfileView from '../views/ProfileView.vue'
import MoodCreateView from '../views/MoodCreateView.vue'
import MoodEditView from '../views/MoodEditView.vue'
import MoodDetailView from '../views/MoodDetailView.vue'
import FriendsView from '../views/FriendsView.vue'
import NotFoundView from '../views/NotFoundView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView
  },
  {
    path: '/profile',
    name: 'profile',
    component: ProfileView,
    meta: { requiresAuth: true }
  },
  {
    path: '/moods/create',
    name: 'mood-create',
    component: MoodCreateView,
    meta: { requiresAuth: true }
  },
  {
    path: '/moods/:id',
    name: 'mood-detail',
    component: MoodDetailView,
    meta: { requiresAuth: true },
    props: true
  },
  {
    path: '/moods/:id/edit',
    name: 'mood-edit',
    component: MoodEditView,
    meta: { requiresAuth: true },
    props: true
  },
  {
    path: '/friends',
    name: 'friends',
    component: FriendsView,
    meta: { requiresAuth: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: NotFoundView
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 导航守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  console.log(`Navigating to ${to.path}, auth required: ${to.meta.requiresAuth}, token exists: ${!!token}`)
  
  // 如果路由是根路径"/"且没有令牌，直接重定向到登录页
  if (to.path === '/' && !token) {
    console.log('Root path accessed without token, redirecting to login')
    next({ name: 'login' })
    return
  }
  
  // 如果需要登录但没有token，则重定向到登录页
  if (to.meta.requiresAuth && !token) {
    console.log(`Redirecting to login, intended destination: ${to.fullPath}`)
    next({ 
      name: 'login', 
      query: { redirect: to.fullPath } 
    })
  } 
  // 如果去登录页但已经有token，则重定向到主页
  else if ((to.name === 'login' || to.name === 'register') && token) {
    console.log('Already logged in, redirecting to home')
    next({ name: 'home' })
  }
  // 否则继续正常导航
  else {
    console.log(`Proceeding to ${to.path}`)
    next()
  }
})

export default router
