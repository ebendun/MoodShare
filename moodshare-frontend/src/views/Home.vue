<template>
  <div class="home container mt-4">
    <div class="mb-4">
      <h2>心情分享</h2>
      <mood-form @mood-created="loadMoods" />
    </div>

    <div class="moods-list">
      <mood-card
          v-for="mood in moods"
          :key="mood.id"
          :mood="mood"
          @like="handleLike"
          @delete="handleDelete"
      />

      <div v-if="moods.length === 0" class="text-center my-5">
        <p>还没有心情分享，快来发表第一条吧！</p>
      </div>
    </div>
  </div>
</template>

<script>
import MoodCard from '../components/MoodCard.vue'
import MoodForm from '../components/MoodForm.vue'
import api from '../services/api'

export default {
  name: 'HomeView',
  components: {
    MoodCard,
    MoodForm
  },
  data() {
    return {
      moods: []
    }
  },
  async created() {
    await this.loadMoods()
  },
  methods: {
    async loadMoods() {
      try {
        const response = await api.getAllMoods()
        this.moods = response.data
      } catch (err) {
        console.error('获取心情列表失败', err)
      }
    },
    async handleLike(moodId) {
      try {
        await api.toggleLike(moodId)
        await this.loadMoods()
      } catch (err) {
        console.error('点赞操作失败', err)
      }
    },
    async handleDelete(moodId) {
      if (confirm('确定要删除这条心情吗？')) {
        try {
          await api.deleteMood(moodId)
          await this.loadMoods()
        } catch (err) {
          console.error('删除心情失败', err)
        }
      }
    }
  }
}
</script>