<template>
  <div id="app">
    <Navbar />
    <div class="container mt-4">
      <div v-if="error" class="alert alert-danger alert-dismissible fade show" role="alert">
        {{ error }}
        <button type="button" class="btn-close" @click="clearError" aria-label="Close"></button>
      </div>
      <router-view />
    </div>
    <footer class="footer mt-5 py-3 bg-light">
      <div class="container text-center">
        <span class="text-muted">© 2025 MoodShare - 分享您的心情</span>
      </div>
    </footer>
  </div>
</template>

<script>
import { computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import Navbar from '@/components/Navbar.vue'
import 'bootstrap/dist/css/bootstrap.min.css'

export default {
  name: 'App',
  components: {
    Navbar
  },
  setup() {
    const store = useStore()
    const router = useRouter()
    const error = computed(() => store.state.error)
    
    // 应用启动时检查登录状态
    onMounted(() => {
      const token = localStorage.getItem('token')
      
      // 如果有token但state中没有用户信息，尝试重新获取用户信息
      if (token && !store.state.user) {
        console.log('Token exists but no user in state, checking token validity')
        store.dispatch('checkAuth').catch(err => {
          console.error('Auth check failed, redirecting to login:', err)
          router.push('/login')
        })
      } else if (!token && window.location.pathname !== '/login' && window.location.pathname !== '/register') {
        // 没有token且不在登录/注册页面，重定向到登录页
        console.log('No token and not on auth page, redirecting to login')
        router.push('/login')
      }
    })
    
    const clearError = () => {
      store.commit('CLEAR_ERROR')
    }
    
    return {
      error,
      clearError
    }
  }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.footer {
  margin-top: auto;
}

/* 自定义心情卡片样式 */
.mood-card {
  transition: all 0.2s ease;
  border-radius: 8px;
}

.mood-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 表情图标样式 */
.mood-emoji {
  font-size: 1.5em;
  margin-right: 8px;
}

/* 标签样式 */
.mood-tag {
  background-color: #f0f7ff;
  color: #0366d6;
  font-size: 0.8em;
  padding: 3px 8px;
  border-radius: 12px;
  margin-right: 5px;
  cursor: pointer;
}

/* 心情类型徽章样式 */
.mood-type-badge {
  font-size: 0.75em;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* 动画效果 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
