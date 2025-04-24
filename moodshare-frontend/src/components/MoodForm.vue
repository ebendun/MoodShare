<template>
  <div class="mood-form">
    <div class="card">
      <div class="card-body">
        <h5 class="card-title">分享你的心情</h5>

        <form @submit.prevent="submitMood">
          <div class="mb-3">
            <textarea v-model="content" class="form-control" rows="3" placeholder="今天感觉如何？" required></textarea>
          </div>

          <div class="mb-3">
            <label class="form-label">选择表情</label>
            <div class="emoji-selector">
              <button
                  v-for="emoji in emojis"
                  :key="emoji"
                  type="button"
                  class="btn emoji-btn"
                  :class="{'btn-primary': selectedEmoji === emoji, 'btn-light': selectedEmoji !== emoji}"
                  @click="selectedEmoji = emoji"
              >
                {{ emoji }}
              </button>
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">标签</label>
            <input
                v-model="tagInput"
                class="form-control"
                placeholder="输入标签，按Enter添加"
                @keydown.enter.prevent="addTag"
            >
            <div class="tags-list mt-2">
              <span v-for="tag in tags" :key="tag" class="badge bg-info me-1 mb-1">
                {{ tag }}
                <button type="button" class="btn-close btn-close-white ms-1" @click="removeTag(tag)"></button>
              </span>
            </div>
          </div>

          <button type="submit" class="btn btn-success" :disabled="!isFormValid">发布</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../services/api'

export default {
  data() {
    return {
      content: '',
      selectedEmoji: '😊',
      tagInput: '',
      tags: [],
      emojis: ['😊', '😢', '😡', '🥰', '😎', '🤔', '😴', '🥳', '😷', '🤯']
    }
  },
  computed: {
    isFormValid() {
      return this.content.trim() !== '' && this.selectedEmoji !== ''
    }
  },
  methods: {
    addTag() {
      const tag = this.tagInput.trim()
      if (tag && !this.tags.includes(tag)) {
        this.tags.push(tag)
      }
      this.tagInput = ''
    },
    removeTag(tag) {
      this.tags = this.tags.filter(t => t !== tag)
    },
    async submitMood() {
      if (!this.isFormValid) return

      try {
        await api.createMood({
          content: this.content,
          emoji: this.selectedEmoji,
          tags: this.tags
        })

        // 重置表单
        this.content = ''
        this.selectedEmoji = '😊'
        this.tags = []

        // 通知父组件
        this.$emit('mood-created')
      } catch (err) {
        console.error('发布心情失败', err)
        alert('发布失败，请稍后重试')
      }
    }
  }
}
</script>

<style scoped>
.emoji-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.emoji-btn {
  font-size: 1.2rem;
  width: 48px;
  height: 48px;
}
</style>