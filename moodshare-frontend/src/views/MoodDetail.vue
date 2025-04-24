<template>
  <div class="mood-detail container mt-4">
    <div v-if="mood" class="card mb-4">
      <div class="card-body">
        <h3>{{ mood.emoji }} {{ mood.content }}</h3>
        <div class="tags mb-2">
          <span v-for="tag in mood.tags" :key="tag" class="badge bg-info me-1">{{ tag }}</span>
        </div>
        <div class="mood-meta text-muted">
          <small>由 {{ mood.username }} 发布于 {{ formatDate(mood.createdAt) }}</small>
        </div>

        <div class="actions mt-3">
          <button @click="handleLike" class="btn" :class="mood.likedByCurrentUser ? 'btn-danger' : 'btn-outline-danger'">
            <i class="bi bi-heart-fill"></i> {{ mood.likeCount }}
          </button>
          <button v-if="canDelete" @click="handleDelete" class="btn btn-sm btn-outline-danger ms-2">
            <i class="bi bi-trash"></i> 删除
          </button>
        </div>
      </div>
    </div>

    <div class="comments-section">
      <h4>评论 ({{ comments.length }})</h4>

      <form @submit.prevent="addComment" class="mb-4">
        <div class="mb-3">
          <textarea v-model="commentContent" class="form-control" placeholder="写下你的评论..." rows="2"></textarea>
        </div>
        <button type="submit" class="btn btn-primary" :disabled="!commentContent">发表评论</button>
      </form>

      <div class="comment-list">
        <div v-for="comment in comments" :key="comment.id" class="card mb-2">
          <div class="card-body py-2">
            <div class="d-flex justify-content-between">
              <div>
                <strong>{{ comment.username }}</strong>
                <p class="mb-0">{{ comment.content }}</p>
                <small class="text-muted">{{ formatDate(comment.createdAt) }}</small>
              </div>
              <button v-if="canDeleteComment(comment)" @click="deleteComment(comment.id)" class="btn btn-sm text-danger">
                <i class="bi bi-x-lg"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../services/api'

export default {
  props: ['id'],
  data() {
    return {
      mood: null,
      comments: [],
      commentContent: '',
      currentUsername: ''
    }
  },
  computed: {
    canDelete() {
      return this.mood && (this.mood.username === this.currentUsername || this.$store.state.user?.isAdmin)
    }
  },
  async created() {
    await this.loadMoodAndComments()
    this.currentUsername = this.$store.state.user?.username
  },
  methods: {
    async loadMoodAndComments() {
      try {
        const moodResponse = await api.getMoodById(this.id)
        this.mood = moodResponse.data

        const commentsResponse = await api.getComments(this.id)
        this.comments = commentsResponse.data
      } catch (err) {
        console.error('获取心情详情失败', err)
      }
    },
    async handleLike() {
      try {
        await api.toggleLike(this.id)
        await this.loadMoodAndComments()
      } catch (err) {
        console.error('点赞操作失败', err)
      }
    },
    async handleDelete() {
      if (confirm('确定要删除这条心情吗？')) {
        try {
          await api.deleteMood(this.id)
          this.$router.push('/')
        } catch (err) {
          console.error('删除心情失败', err)
        }
      }
    },
    async addComment() {
      if (!this.commentContent.trim()) return

      try {
        await api.addComment(this.id, { content: this.commentContent })
        this.commentContent = ''
        await this.loadMoodAndComments()
      } catch (err) {
        console.error('添加评论失败', err)
      }
    },
    canDeleteComment(comment) {
      return this.currentUsername === comment.username ||
          this.mood.username === this.currentUsername ||
          this.$store.state.user?.isAdmin
    },
    async deleteComment(commentId) {
      if (confirm('确定要删除这条评论吗？')) {
        try {
          await api.deleteComment(this.id, commentId)
          await this.loadMoodAndComments()
        } catch (err) {
          console.error('删除评论失败', err)
        }
      }
    },
    formatDate(dateStr) {
      return new Date(dateStr).toLocaleString('zh-CN')
    }
  }
}
</script>