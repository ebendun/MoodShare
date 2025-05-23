<template>
  <div class="login-view">
    <div class="row justify-content-center">
      <div class="col-md-6 col-lg-5">
        <div class="card shadow">
          <div class="card-body p-5">
            <h2 class="text-center mb-4">登录</h2>
            
            <!-- 错误提示 -->
            <div v-if="error" class="alert alert-danger mb-3" role="alert">
              {{ error }}
            </div>
            
            <form @submit.prevent="handleLogin">
              <div class="mb-3">
                <label for="username" class="form-label">用户名</label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="username" 
                  v-model="username" 
                  required
                  autocomplete="username"
                >
              </div>
              
              <div class="mb-3">
                <label for="password" class="form-label">密码</label>
                <input 
                  type="password" 
                  class="form-control" 
                  id="password" 
                  v-model="password" 
                  required
                  autocomplete="current-password"
                >
              </div>
              
              <div class="d-grid">
                <button 
                  type="submit" 
                  class="btn btn-primary" 
                  :disabled="isLoading"
                >
                  <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status"></span>
                  登录
                </button>
              </div>
            </form>
            
            <div class="mt-3 text-center">
              <p>还没有账号？ <router-link to="/register">立即注册</router-link></p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'

export default {
  name: 'LoginView',
  setup() {
    const store = useStore()
    const router = useRouter()
    const route = useRoute()
    
    const username = ref('')
    const password = ref('')
    const isLoading = computed(() => store.state.loading)
    const error = computed(() => store.state.error)
    
    const handleLogin = async () => {
      try {
        console.log('Login attempt started for:', username.value);
        const user = await store.dispatch('login', { 
          username: username.value, 
          password: password.value 
        });
        
        if (user) {
          console.log('Login successful, redirecting user');
          // 登录成功后跳转
          const redirectPath = route.query.redirect || '/';
          console.log('Redirecting to:', redirectPath);
          
          // 使用 nextTick 确保在 DOM 更新后再导航
          setTimeout(() => {
            router.push(redirectPath).catch(navError => {
              console.error('Navigation error:', navError);
              // 如果导航失败，强制刷新页面
              window.location.href = redirectPath;
            });
          }, 100);
        } else {
          console.error('Login failed, no user returned');
        }
      } catch (error) {
        console.error('Login error:', error);
        // 错误已经在store中设置，通过error计算属性显示
      }
    }
    
    return {
      username,
      password,
      isLoading,
      error,
      handleLogin
    }
  }
}
</script>

<style scoped>
.login-view {
  padding-top: 3rem;
}
</style>
