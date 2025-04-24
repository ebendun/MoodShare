<template>
  <div class="register container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="card">
          <div class="card-header">用户注册</div>
          <div class="card-body">
            <form @submit.prevent="onSubmit">
              <div class="mb-3">
                <label class="form-label">用户名</label>
                <input type="text" v-model="username" class="form-control" required>
              </div>
              <div class="mb-3">
                <label class="form-label">邮箱</label>
                <input type="email" v-model="email" class="form-control" required>
              </div>
              <div class="mb-3">
                <label class="form-label">密码</label>
                <input type="password" v-model="password" class="form-control" required>
              </div>
              <div class="mb-3">
                <label class="form-label">确认密码</label>
                <input type="password" v-model="confirmPassword" class="form-control" required>
                <div v-if="passwordMismatch" class="text-danger mt-1">两次输入的密码不一致</div>
              </div>
              <button type="submit" class="btn btn-primary" :disabled="!isFormValid">注册</button>
              <router-link to="/login" class="ms-3">已有账号？去登录</router-link>
              <div v-if="error" class="alert alert-danger mt-3">{{ error }}</div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'RegisterView',
  data() {
    return {
      username: '',
      email: '',
      password: '',
      confirmPassword: '',
      error: null
    }
  },
  computed: {
    passwordMismatch() {
      return this.password && this.confirmPassword && this.password !== this.confirmPassword
    },
    isFormValid() {
      return this.username &&
          this.email &&
          this.password &&
          this.confirmPassword &&
          this.password === this.confirmPassword
    }
  },
  methods: {
    async onSubmit() {
      if (!this.isFormValid) return

      try {
        this.error = null
        await this.$store.dispatch('register', {
          username: this.username,
          email: this.email,
          password: this.password
        })
        this.$router.push('/')
      } catch (err) {
        if (err.response && err.response.data && err.response.data.message) {
          this.error = err.response.data.message
        } else {
          this.error = '注册失败，请稍后再试'
        }
        console.error('注册错误:', err)
      }
    }
  }
}
</script>