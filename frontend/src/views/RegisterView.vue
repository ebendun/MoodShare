<template>
  <div class="register-view">
    <div class="row justify-content-center">
      <div class="col-md-6 col-lg-5">
        <div class="card shadow">
          <div class="card-body p-5">
            <h2 class="text-center mb-4">注册账号</h2>
            
            <form @submit.prevent="handleRegister">
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
                <label for="email" class="form-label">邮箱</label>
                <input 
                  type="email" 
                  class="form-control" 
                  id="email" 
                  v-model="email" 
                  required
                  autocomplete="email"
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
                  autocomplete="new-password"
                >
              </div>
              
              <div class="mb-3">
                <label for="confirmPassword" class="form-label">确认密码</label>
                <input 
                  type="password" 
                  class="form-control" 
                  id="confirmPassword" 
                  v-model="confirmPassword" 
                  required
                  autocomplete="new-password"
                >
                <div class="form-text text-danger" v-if="passwordMismatch">
                  两次输入的密码不匹配
                </div>
              </div>
              
              <div class="d-grid">
                <button 
                  type="submit" 
                  class="btn btn-primary" 
                  :disabled="isLoading || passwordMismatch"
                >
                  <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status"></span>
                  注册
                </button>
              </div>
            </form>
            
            <div class="mt-3 text-center">
              <p>已有账号？ <router-link to="/login">返回登录</router-link></p>
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
import { useRouter } from 'vue-router'

export default {
  name: 'RegisterView',
  setup() {
    const store = useStore()
    const router = useRouter()
    
    const username = ref('')
    const email = ref('')
    const password = ref('')
    const confirmPassword = ref('')
    
    const isLoading = computed(() => store.state.loading)
    const passwordMismatch = computed(() => 
      password.value && confirmPassword.value && 
      password.value !== confirmPassword.value
    )
    
    const handleRegister = async () => {
      if (passwordMismatch.value) {
        return
      }
      
      try {
        await store.dispatch('register', {
          username: username.value,
          email: email.value,
          password: password.value
        })
        
        // 注册成功，跳转到登录页
        router.push({
          path: '/login',
          query: { registered: 'true' }
        })
      } catch (error) {
        console.error('Registration error:', error)
      }
    }
    
    return {
      username,
      email,
      password,
      confirmPassword,
      isLoading,
      passwordMismatch,
      handleRegister
    }
  }
}
</script>

<style scoped>
.register-view {
  padding-top: 3rem;
}
</style>
