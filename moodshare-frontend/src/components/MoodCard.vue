<template>
  <div class="card mb-4">
    <div class="card-body">
      <div class="d-flex justify-content-between">
        <h5 class="card-title">
          <span class="emoji me-2">{{ mood.emoji }}</span>
          <router-link :to="`/mood/${mood.id}`" class="text-decoration-none">
            {{ truncate(mood.content) }}
          </router-link>
        </h5>
        <div v-if="canDelete" class="actions">
          <button @click.stop="$emit('delete', mood.id)" class="btn btn-sm btn-outline-danger">
            <i class="bi bi-trash"></i>
          </button>
        </div>
      </div>

      <div class="tags mb-2">
        <span v-for="tag in mood.tags" :key="tag" class="badge bg-info me-1">{{ tag }}</span>
      </div>

      <div class="mood-meta text-muted">
        <small>由 {{ mood.username }} 发布于 {{ formatDate(mood.createdAt) }}</small>
      </div>

      <div class="mood-actions mt-2">
        <button @click.stop="$emit('like', mood.id)" class="btn"
                :class="mood.likedByCurrentUser ? 'btn-danger' : 'btn-outline-danger'">
          <i class="bi bi-heart-fill"></i> {{ mood.likeCount }}
        </button>

        <router-link :to="`/mood/${mood.id}`" class="btn btn-outline-primary ms-2">
          <i class="bi bi-chat"></i> {{ mood.comments.length }} 评论
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    mood: {
      type: Object,
      required: true,
      // 添加默认值，确保即使部分属性缺失也不会报错
      default: () => ({
        id: '',
        emoji: '😊',
        content: '',
        username: '未知用户',
        createdAt: new Date().toISOString(),
        likeCount: 0,
        likedByCurrentUser: false,
        tags: [],
        comments: []
      })
    }
  },
  computed: {
    canDelete() {
      const currentUser = this.$store.state.user
      return currentUser && (this.mood.username === currentUser.username || currentUser.isAdmin)
    }
  },
  methods: {
    truncate(text, length = 100) {
      if (!text) return '';
      return text.length > length ? text.substring(0, length) + '...' : text
    },
    formatDate(dateStr) {
      if (!dateStr) return '未知日期';
      return new Date(dateStr).toLocaleString('zh-CN')
    }
  }
}
</script>