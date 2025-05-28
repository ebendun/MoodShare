<template>
  <div class="mood-detail-view">
    <!-- 加载中 -->
    <div v-if="isLoading" class="text-center my-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-2">加载中...</p>
    </div>
    
    <!-- 心情不存在 -->
    <div v-else-if="!mood" class="text-center my-5">
      <div class="alert alert-warning">
        <i class="bi bi-exclamation-triangle me-2"></i>
        此心情不存在或已被删除
      </div>
      <router-link to="/" class="btn btn-primary mt-3">返回首页</router-link>
    </div>
    
    <!-- 心情详情 -->
    <div v-else class="row">
      <div class="col-md-8">
        <!-- 心情卡片 -->
        <div class="card mb-4">
          <div class="card-body">
            <div class="d-flex justify-content-between mb-3">
              <div class="d-flex align-items-center">
                <SafeImage 
                  :src="mood.user.profilePicture || ''" 
                  alt="Profile" 
                  imageClass="rounded-circle me-3" 
                  width="50" 
                  height="50" 
                />
                <div>
                  <h5 class="mb-0">{{ mood.user.username }}</h5>
                  <small class="text-muted">{{ formatDate(mood.createdAt) }}</small>
                </div>
              </div>
              
              <div class="d-flex align-items-center">
                <span class="badge bg-info me-2">{{ getMoodTypeName(mood.moodType) }}</span>
                <div class="dropdown" v-if="isOwnMood">
                  <button class="btn btn-sm btn-light" type="button" 
                          data-bs-toggle="dropdown" aria-expanded="false">
                    <i class="bi bi-three-dots-vertical"></i>
                  </button>
                  <ul class="dropdown-menu dropdown-menu-end">
                    <li>
                      <router-link :to="`/moods/${mood.id}/edit`" class="dropdown-item">
                        编辑
                      </router-link>
                    </li>
                    <li>
                      <a class="dropdown-item" href="#" @click.prevent="deleteMood">
                        删除
                      </a>
                    </li>
                    <li><hr class="dropdown-divider"></li>
                    <li>
                      <a class="dropdown-item" href="#" 
                         @click.prevent="updatePrivacy('PUBLIC')" 
                         :class="{ active: mood.privacyLevel === 'PUBLIC' }">
                        公开
                      </a>
                    </li>
                    <li>
                      <a class="dropdown-item" href="#" 
                         @click.prevent="updatePrivacy('FRIENDS')" 
                         :class="{ active: mood.privacyLevel === 'FRIENDS' }">
                        好友可见
                      </a>
                    </li>
                    <li>
                      <a class="dropdown-item" href="#" 
                         @click.prevent="updatePrivacy('PRIVATE')" 
                         :class="{ active: mood.privacyLevel === 'PRIVATE' }">
                        仅自己可见
                      </a>
                    </li>
                  </ul>
                </div>
              </div>
            </div>
              <div class="mood-content mb-3">
              <p class="fs-5">
                <span class="mood-emoji">{{ mood.emoji || '😊' }}</span>
                {{ mood.content }}
              </p>
              
              <!-- 心情图片 -->
              <div v-if="mood.imageUrl" class="mt-3">
                <SafeImage 
                  :src="mood.imageUrl" 
                  alt="心情图片" 
                  imageClass="img-fluid rounded mood-image" 
                  style="max-width: 100%; max-height: 400px; cursor: pointer;"
                  @click="showImageModal(mood.imageUrl)"
                />
              </div>
            </div>
            
            <div v-if="mood.tags && mood.tags.length > 0" class="mb-3">
              <span v-for="tag in mood.tags" :key="tag" class="mood-tag">
                #{{ tag }}
              </span>
            </div>
            
            <div class="mb-3">
              <div v-if="mood.location" class="text-muted small mb-1">
                <i class="bi bi-geo-alt"></i> {{ mood.location }}
              </div>
              <div v-if="mood.weather" class="text-muted small">
                <i class="bi bi-cloud"></i> {{ mood.weather }}
              </div>
            </div>
            
            <div class="d-flex justify-content-between mt-4">
              <button class="btn" 
                      :class="mood.liked ? 'btn-primary' : 'btn-outline-primary'"
                      @click="toggleLike">
                <i class="bi" :class="mood.liked ? 'bi-heart-fill' : 'bi-heart'"></i>
                {{ mood.likeCount || 0 }} 喜欢
              </button>
              
              <div>
                <span class="me-2">
                  <i class="bi bi-eye"></i> 
                  {{ getPrivacyName(mood.privacyLevel) }}
                </span>
                
                <span>
                  <i class="bi bi-chat"></i>
                  {{ mood.comments?.length || 0 }} 评论
                </span>
              </div>
            </div>
          </div>
        </div>
          <!-- 评论区 -->
        <div class="card">
          <div class="card-header bg-white">
            <h5 class="mb-0">评论 ({{ mood.comments?.length || 0 }})</h5>
            <!-- 调试信息 -->
            <div class="small text-muted mt-1">
              <div>调试: mood存在: {{ !!mood }}</div>
              <div>调试: comments类型: {{ typeof mood?.comments }} ({{ Array.isArray(mood?.comments) ? 'Array' : 'Not Array' }})</div>
              <div>调试: comments长度: {{ mood?.comments?.length }}</div>
              <div>调试: comments内容: {{ JSON.stringify(mood?.comments) }}</div>
            </div>
          </div>
          
          <div class="card-body">            <!-- 添加评论 -->
            <div class="mb-4">
              <form @submit.prevent="addComment">
                <div class="mb-3">
                  <label for="commentInput" class="visually-hidden">添加评论</label>
                  <textarea 
                    id="commentInput"
                    name="commentInput"
                    class="form-control" 
                    v-model="commentText" 
                    placeholder="添加评论..." 
                    rows="2"
                    required
                  ></textarea>
                </div>
                
                <!-- 图片上传区域 -->
                <div class="mb-3">
                  <div class="d-flex align-items-center gap-3">
                    <label for="commentImage" class="btn btn-outline-secondary btn-sm">
                      <i class="bi bi-image"></i> 选择图片
                    </label>
                    <input 
                      type="file" 
                      id="commentImage" 
                      accept="image/*" 
                      @change="handleImageSelect" 
                      class="visually-hidden"
                    >
                    <span v-if="selectedImage" class="text-muted">{{ selectedImage.name }}</span>
                    <button 
                      v-if="selectedImage" 
                      type="button" 
                      @click="removeSelectedImage" 
                      class="btn btn-sm btn-outline-danger"
                    >
                      移除图片
                    </button>
                  </div>
                  
                  <!-- 图片预览 -->
                  <div v-if="imagePreview" class="mt-2">
                    <img :src="imagePreview" alt="预览" class="img-thumbnail" style="max-width: 200px; max-height: 150px;">
                  </div>
                </div>
                
                <div class="d-flex justify-content-end">
                  <button 
                    type="submit" 
                    class="btn btn-primary" 
                    :disabled="!commentText.trim() || isUploading"
                  >
                    <span v-if="isUploading" class="spinner-border spinner-border-sm me-2"></span>
                    {{ isUploading ? '发送中...' : '发送' }}
                  </button>
                </div>
              </form>
            </div>
            
            <!-- 评论列表 -->
            <div v-if="mood.comments && mood.comments.length > 0">              <div v-for="comment in mood.comments" :key="comment.id" class="d-flex mb-3">
                <SafeImage 
                  :src="(comment.user && comment.user.profilePicture) || ''" 
                  alt="Comment user" 
                  imageClass="rounded-circle me-3" 
                  width="40" 
                  height="40" 
                />                <div class="p-3 bg-light rounded flex-grow-1">
                  <div class="d-flex justify-content-between mb-2">
                    <strong>{{ comment.user ? comment.user.username : 'Unknown User' }}</strong>
                    <small class="text-muted">{{ formatDate(comment.createdAt) }}</small>
                  </div>
                  <p class="mb-2">{{ comment.content }}</p>
                  
                  <!-- 评论图片 -->
                  <div v-if="comment.imageUrl" class="mt-2">
                    <SafeImage 
                      :src="comment.imageUrl" 
                      alt="评论图片" 
                      imageClass="img-thumbnail comment-image" 
                      style="max-width: 300px; max-height: 200px; cursor: pointer;"
                      @click="showImageModal(comment.imageUrl)"
                    />
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 无评论提示 -->
            <div v-else class="text-center py-4">
              <p class="text-muted">暂无评论，来发表第一条评论吧！</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧信息栏 -->
      <div class="col-md-4">
        <div class="card mb-4">
          <div class="card-body">
            <h5 class="card-title">用户信息</h5>
            <div class="text-center mb-3">
              <SafeImage 
                :src="mood.user.profilePicture || ''" 
                alt="Profile" 
                imageClass="rounded-circle" 
                width="100" 
                height="100" 
              />
              <h5 class="mt-2">{{ mood.user.username }}</h5>
            </div>
            <hr>
            <router-link :to="`/user/${mood.user.id}`" class="btn btn-outline-primary w-100">
              查看用户资料
            </router-link>
          </div>
        </div>
        
        <div class="card">
          <div class="card-body">
            <h5 class="card-title">相关心情</h5>
            
            <!-- TODO: 后续可以添加相关心情的功能 -->
            <p class="text-muted">暂无相关心情</p>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 图片预览模态框 -->
    <div 
      v-if="showImagePreview" 
      class="modal fade show" 
      style="display: block;"
      @click="hideImageModal"
    >
      <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content" @click.stop>
          <div class="modal-header">
            <h5 class="modal-title">图片预览</h5>
            <button type="button" class="btn-close" @click="hideImageModal"></button>
          </div>
          <div class="modal-body text-center">
            <img :src="previewImageUrl" alt="预览" class="img-fluid">
          </div>
        </div>
      </div>
    </div>
    <div v-if="showImagePreview" class="modal-backdrop fade show"></div>
  </div>
</template>

<script>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import SafeImage from '../components/SafeImage.vue'
import { fileApi } from '../api/api.js'

export default {
  name: 'MoodDetailView',
  components: {
    SafeImage
  },
  props: {
    id: {
      type: String,
      required: true
    }
  },
  setup(props) {
    const store = useStore()
    const router = useRouter()
      const commentText = ref('')
    const selectedImage = ref(null)
    const imagePreview = ref('')
    const isUploading = ref(false)
    const showImagePreview = ref(false)
    const previewImageUrl = ref('')
      // 获取状态
    const mood = computed(() => {
      console.log('🔍 MoodDetailView - Current mood from store:', store.state.mood)
      if (store.state.mood) {
        console.log('🔍 MoodDetailView - Mood comments:', store.state.mood.comments)
        console.log('🔍 MoodDetailView - Comments length:', store.state.mood.comments?.length)
      }
      return store.state.mood
    })
    const currentUser = computed(() => store.state.user)
    const isLoading = computed(() => store.state.loading)
      // 是否是自己的心情
    const isOwnMood = computed(() => {
      return mood.value && 
             currentUser.value && 
             mood.value.user.username === currentUser.value.username
    })
    
    // 初始化 Bootstrap 下拉菜单
    const initializeDropdowns = () => {
      if (window.bootstrap) {
        const dropdownElements = document.querySelectorAll('[data-bs-toggle="dropdown"]')
        dropdownElements.forEach(element => {
          if (!element._dropdown) {
            try {
              element._dropdown = new window.bootstrap.Dropdown(element)
            } catch (error) {
              console.warn('Failed to initialize dropdown:', error)
            }
          }
        })
      }
    }
      // 加载心情详情
    onMounted(async () => {
      console.log('[MoodDetailView] onMounted - Fetching mood with ID:', props.id)
      try {
        const result = await store.dispatch('fetchMoodById', props.id)
        console.log('[MoodDetailView] Mood fetched successfully:', result)
        console.log('[MoodDetailView] Current mood state:', mood.value)
        console.log('[MoodDetailView] Comments in mood:', mood.value?.comments)
        
        // 心情加载完成后初始化下拉菜单
        await nextTick()
        setTimeout(() => {
          initializeDropdowns()
        }, 100)
      } catch (error) {
        console.error('[MoodDetailView] Error fetching mood:', error)
      }
    })
    
    // 点赞
    const toggleLike = async () => {
      try {
        await store.dispatch('toggleLike', mood.value.id)
      } catch (error) {
        console.error('Error toggling like:', error)
      }
    }
      // 添加评论
    const addComment = async () => {
      if (!commentText.value.trim()) return
      
      try {
        isUploading.value = true
        let imageUrl = null
        
        // 如果有选择图片，先上传图片
        if (selectedImage.value) {
          const uploadResponse = await fileApi.uploadCommentImage(selectedImage.value)
          if (uploadResponse.data && uploadResponse.data.success) {
            imageUrl = uploadResponse.data.url
          }
        }
        
        // 添加评论
        await store.dispatch('addComment', {
          moodId: mood.value.id,
          content: commentText.value.trim(),
          imageUrl: imageUrl
        })
        
        // 重置表单
        commentText.value = ''
        selectedImage.value = null
        imagePreview.value = ''
      } catch (error) {
        console.error('Error adding comment:', error)
        alert('评论发送失败：' + (error.response?.data?.message || error.message))
      } finally {
        isUploading.value = false
      }
    }
    
    // 处理图片选择
    const handleImageSelect = (event) => {
      const file = event.target.files[0]
      if (!file) return
      
      // 验证文件类型
      const validTypes = ['image/jpeg', 'image/png', 'image/gif']
      if (!validTypes.includes(file.type)) {
        alert('请选择有效的图片文件 (JPG, PNG, GIF)')
        event.target.value = ''
        return
      }
      
      // 验证文件大小 (5MB)
      if (file.size > 5 * 1024 * 1024) {
        alert('图片文件大小不能超过5MB')
        event.target.value = ''
        return
      }
      
      selectedImage.value = file
      
      // 生成预览
      const reader = new FileReader()
      reader.onload = (e) => {
        imagePreview.value = e.target.result
      }
      reader.readAsDataURL(file)
    }
    
    // 移除选择的图片
    const removeSelectedImage = () => {
      selectedImage.value = null
      imagePreview.value = ''
      // 清空file input
      const fileInput = document.getElementById('commentImage')
      if (fileInput) {
        fileInput.value = ''
      }
    }
    
    // 显示图片预览模态框
    const showImageModal = (imageUrl) => {
      previewImageUrl.value = imageUrl
      showImagePreview.value = true
    }
    
    // 隐藏图片预览模态框
    const hideImageModal = () => {
      showImagePreview.value = false
      previewImageUrl.value = ''
    }
    
    // 删除心情
    const deleteMood = async () => {
      if (confirm('确定要删除这条心情吗？')) {
        try {
          await store.dispatch('deleteMood', mood.value.id)
          router.push('/')
        } catch (error) {
          console.error('Error deleting mood:', error)
        }
      }
    }
    
    // 更新隐私设置
    const updatePrivacy = async (privacyLevel) => {
      try {
        await store.dispatch('updatePrivacy', {
          moodId: mood.value.id,
          privacyLevel
        })
      } catch (error) {
        console.error('Error updating privacy:', error)
      }
    }
    
    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return new Intl.DateTimeFormat('zh-CN', { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      }).format(date)
    }
    
    // 获取心情类型名称
    const getMoodTypeName = (moodType) => {
      const types = {
        'HAPPY': '开心',
        'SAD': '悲伤',
        'ANGRY': '生气',
        'EXCITED': '兴奋',
        'WORRIED': '担忧',
        'NEUTRAL': '平静'
      }
      return types[moodType] || '未知'
    }
    
    // 获取隐私级别名称
    const getPrivacyName = (privacy) => {
      const types = {
        'PUBLIC': '公开',
        'FRIENDS': '好友可见',
        'PRIVATE': '仅自己可见'
      }
      return types[privacy] || '未知'
    }
  
      return {
      mood,
      isLoading,
      isOwnMood,
      commentText,
      selectedImage,
      imagePreview,
      isUploading,
      showImagePreview,
      previewImageUrl,
      formatDate,
      getMoodTypeName,
      getPrivacyName,
      toggleLike,
      addComment,
      deleteMood,
      updatePrivacy,
      handleImageSelect,
      removeSelectedImage,
      showImageModal,
      hideImageModal
    }
  }
}
</script>

<style scoped>
.mood-detail-view {
  max-width: 1100px;
  margin: 0 auto;
  padding-top: 20px;
}

.mood-content {
  font-size: 1.1rem;
  line-height: 1.5;
}

.mood-emoji {
  font-size: 1.8em;
  margin-right: 8px;
  vertical-align: middle;
}

.comment-image {
  border-radius: 8px;
  transition: transform 0.2s ease;
}

.comment-image:hover {
  transform: scale(1.02);
}

.modal-backdrop {
  z-index: 1040;
}
</style>
