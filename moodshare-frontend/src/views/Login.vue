<template>
  <div class="login container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="card">
          <div class="card-header">用户登录</div>
          <div class="card-body">
            <form @submit.prevent="onSubmit">
              <div class="mb-3">
                <label class="form-label">用户名</label>
                <input type="text" v-model="username" class="form-control" required>
              </div>
              <div class="mb-3">
                <label class="form-label">密码</label>
                <input type="password" v-model="password" class="form-control" required>
              </div>
              <button type="submit" class="btn btn-primary">登录</button>
              <router-link to="/register" class="ms-3">注册新用户</router-link>
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
  name: 'LoginView',
  data() {
    return {
      username: '',
      password: '',
      error: null
    }
  },
  methods: {
    async onSubmit() {
      try {
        this.error = null
        await this.$store.dispatch('login', {
          username: this.username,
          password: this.password
        })
        this.$router.push('/')
      } catch (err) {
        this.error = '登录失败，请检查用户名和密码'
      }
    }
  }
}
</script>