<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container">
      <router-link class="navbar-brand" to="/">心情分享</router-link>

      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav me-auto">
          <li class="nav-item">
            <router-link class="nav-link" to="/">首页</router-link>
          </li>
        </ul>

        <div class="navbar-nav" v-if="isLoggedIn">
          <span class="nav-item nav-link">{{ currentUser.username }}</span>
          <a href="#" class="nav-link" @click.prevent="logout">退出</a>
        </div>
        <div class="navbar-nav" v-else>
          <router-link class="nav-link" to="/login">登录</router-link>
          <router-link class="nav-link" to="/register">注册</router-link>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
export default {
  computed: {
    isLoggedIn() {
      return !!this.$store.state.token
    },
    currentUser() {
      return this.$store.state.user || {}
    }
  },
  methods: {
    async logout() {
      await this.$store.dispatch('logout')
      this.$router.push('/login')
    }
  }
}
</script>