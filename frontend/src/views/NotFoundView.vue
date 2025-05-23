<template>
  <div class="not-found-view">
    <div class="text-center">
      <h1 class="display-1">404</h1>
      <h2>页面未找到</h2>
      <p class="lead">很抱歉，您请求的页面不存在。</p>
      <router-link to="/" class="btn btn-primary">返回首页</router-link>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NotFoundView'
}
</script>

<style scoped>
.not-found-view {
  padding: 5rem 0;
}
</style>
