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
                </div>              </div>
              
              <!-- 验证码 -->
              <div class="mb-3">
                <label for="captcha" class="form-label">验证码</label>
                <div class="input-group">
                  <input 
                    type="text" 
                    class="form-control" 
                    id="captcha" 
                    v-model="captchaCode" 
                    placeholder="请输入验证码"
                    required
                    autocomplete="off"
                  >
                  <div class="input-group-text p-0">
                    <img 
                      v-if="captchaImage" 
                      :src="captchaImage" 
                      @click="refreshCaptcha"
                      alt="验证码"
                      style="height: 38px; cursor: pointer; border-radius: 0 6px 6px 0;"
                      title="点击刷新验证码"
                    >
                    <div v-else class="px-3 py-2 text-muted" @click="refreshCaptcha" style="cursor: pointer;">
                      点击获取
                    </div>
                  </div>
                </div>
                <div class="form-text">点击验证码图片可以刷新</div>
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
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { captchaApi } from '../api/api'

export default {
  name: 'RegisterView',
  setup() {
    const store = useStore()
    const router = useRouter()
    
    const username = ref('')
    const email = ref('')
    const password = ref('')
    const confirmPassword = ref('')
    const captchaCode = ref('')
    const captchaImage = ref('')
    const sessionId = ref('')
    
    const isLoading = computed(() => store.state.loading)
    const passwordMismatch = computed(() => 
      password.value && confirmPassword.value && 
      password.value !== confirmPassword.value
    )
    
    // 获取验证码
    const refreshCaptcha = async () => {
      try {
        const response = await captchaApi.getCaptcha()
        if (response.data.success) {
          captchaImage.value = response.data.data.captchaImage
          sessionId.value = response.data.data.sessionId
          captchaCode.value = '' // 清空已输入的验证码
        }
      } catch (error) {
        console.error('获取验证码失败:', error)
      }
    }
    
    const handleRegister = async () => {
      if (passwordMismatch.value) {
        return
      }
      
      try {        await store.dispatch('register', {
          username: username.value,
          email: email.value,
          password: password.value,
          captchaCode: captchaCode.value,
          sessionId: sessionId.value
        })
        
        // 注册成功，跳转到登录页
        router.push({
          path: '/login',
          query: { registered: 'true' }
        })      } catch (error) {
        console.error('Registration error:', error)
        // 注册失败时刷新验证码
        refreshCaptcha()
      }
    }
    
    // 组件挂载时获取验证码
    onMounted(() => {
      refreshCaptcha()
    })
    
    return {
      username,
      email,
      password,
      confirmPassword,
      captchaCode,
      captchaImage,
      sessionId,
      isLoading,
      passwordMismatch,
      handleRegister,
      refreshCaptcha
    }
  }
}
</script>

<style scoped>
.register-view {
  padding-top: 3rem;
}
</style>
