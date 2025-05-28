<template>
  <div class="home-view">
    <div class="row">
      <!-- 左侧边栏 -->
      <div class="col-md-3">
        <div class="card mb-4">
          <div class="card-body">
            <h5 class="card-title">心情筛选</h5>
            
            <div class="mb-3">
              <label class="form-label">心情类型</label>
              <select class="form-select" v-model="filters.moodType">
                <option value="">全部</option>
                <option value="HAPPY">开心</option>
                <option value="SAD">悲伤</option>
                <option value="ANGRY">生气</option>
                <option value="EXCITED">兴奋</option>
                <option value="WORRIED">担忧</option>
                <option value="NEUTRAL">平静</option>
              </select>
            </div>
            
            <div class="mb-3">
              <label class="form-label">隐私设置</label>
              <select class="form-select" v-model="filters.privacyLevel">
                <option value="">全部</option>
                <option value="PUBLIC">公开</option>
                <option value="FRIENDS">好友可见</option>
              </select>
            </div>
            
            <div class="mb-3">
              <label class="form-label">位置</label>
              <input 
                type="text" 
                class="form-control" 
                v-model="filters.location" 
                placeholder="输入位置关键词"
                autocomplete="address-level2"
              >
            </div>
            
            <button class="btn btn-primary w-100" @click="applyFilters">
              应用筛选
            </button>
          </div>
        </div>
        
        <div class="card">
          <div class="card-body">
            <h5 class="card-title">查看类型</h5>
            <div class="list-group list-group-flush">
              <button 
                class="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
                :class="{ active: activeView === 'all' }"
                @click="switchView('all')"
              >
                全部心情
              </button>
              <button 
                class="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
                :class="{ active: activeView === 'feed' }"
                @click="switchView('feed')"
              >
                我的动态
              </button>
              <button 
                class="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
                :class="{ active: activeView === 'friends' }"
                @click="switchView('friends')"
              >
                好友动态
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 主内容区 -->
      <div class="col-md-9">
        <div class="d-flex justify-content-between align-items-center mb-4">
          <h2 class="m-0">{{ viewTitle }}</h2>
          <router-link to="/moods/create" class="btn btn-success">
            <i class="bi bi-plus-circle"></i> 发布心情
          </router-link>
        </div>
        
        <!-- 加载指示器 -->
        <div v-if="isLoading" class="text-center my-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading...</span>
          </div>
          <p class="mt-2">加载中...</p>
        </div>
        
        <!-- 心情列表 -->
        <div v-else-if="moods.length > 0">
          <div class="card mb-4 mood-card" v-for="mood in moods" :key="mood.id">
            <div class="card-body">
              <div class="d-flex justify-content-between mb-2">
                <div class="d-flex align-items-center">
                  <SafeImage 
                    :src="(mood.user && mood.user.profilePicture) || ''" 
                    alt="Profile" 
                    imageClass="rounded-circle me-2" 
                    width="40" 
                    height="40" 
                  />
                  <div>
                    <h5 class="mb-0">{{ mood.user ? mood.user.username : 'Unknown User' }}</h5>
                    <small class="text-muted">
                      {{ formatDate(mood.createdAt) }}
                    </small>
                  </div>
                </div>
                
                <div class="d-flex">
                  <span class="badge bg-info mood-type-badge">
                    {{ getMoodTypeName(mood.moodType) }}
                  </span>
                  <div class="dropdown ms-2" v-if="mood.user && currentUser && mood.user.username === currentUser.username">
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
                        <a class="dropdown-item" href="#" @click.prevent="deleteMood(mood.id)">
                          删除
                        </a>
                      </li>
                      <li><hr class="dropdown-divider"></li>
                      <li>
                        <a class="dropdown-item" href="#" 
                           @click.prevent="updatePrivacy(mood.id, 'PUBLIC')" 
                           :class="{ active: mood.privacyLevel === 'PUBLIC' }">
                          公开
                        </a>
                      </li>
                      <li>
                        <a class="dropdown-item" href="#" 
                           @click.prevent="updatePrivacy(mood.id, 'FRIENDS')" 
                           :class="{ active: mood.privacyLevel === 'FRIENDS' }">
                          好友可见
                        </a>
                      </li>
                      <li>
                        <a class="dropdown-item" href="#" 
                           @click.prevent="updatePrivacy(mood.id, 'PRIVATE')" 
                           :class="{ active: mood.privacyLevel === 'PRIVATE' }">
                          仅自己可见
                        </a>
                      </li>
                    </ul>
                  </div>
                </div>
              </div>
                <p class="card-text">
                <span class="mood-emoji">{{ mood.emoji || '😊' }}</span>
                {{ mood.content }}
              </p>
              
              <!-- 心情图片 -->
              <div v-if="mood.imageUrl" class="mb-3">
                <SafeImage 
                  :src="mood.imageUrl" 
                  alt="心情图片" 
                  imageClass="img-fluid rounded mood-image" 
                  style="max-width: 100%; max-height: 400px; cursor: pointer;"
                  @click="openImageModal(mood.imageUrl)"
                />
              </div>
              
              <div v-if="mood.tags && mood.tags.length > 0" class="mb-3">
                <span v-for="tag in mood.tags" :key="tag" class="mood-tag">
                  #{{ tag }}
                </span>
              </div>
              
              <div v-if="mood.location" class="text-muted small mb-3">
                <i class="bi bi-geo-alt"></i> {{ mood.location }}
              </div>
              
              <div class="d-flex justify-content-between align-items-center mt-3">
                <div>
                  <button class="btn btn-sm" 
                          :class="mood.liked ? 'btn-primary' : 'btn-outline-primary'"
                          @click="toggleLike(mood.id)">
                    <i class="bi" :class="mood.liked ? 'bi-heart-fill' : 'bi-heart'"></i>
                    {{ mood.likeCount || 0 }}
                  </button>
                  
                  <router-link :to="`/moods/${mood.id}`" class="btn btn-sm btn-outline-secondary ms-2">
                    <i class="bi bi-chat"></i>
                    {{ mood.comments?.length || 0 }}
                  </router-link>
                </div>
              </div>
                <!-- 评论预览 (最多显示2条) -->              <div v-if="mood.comments && mood.comments.length > 0" class="mt-3">
                <hr>
                <div v-for="comment in mood.comments.slice(0, 2)" :key="comment.id" class="d-flex mb-2">
                  <SafeImage 
                    :src="(comment.user && comment.user.profilePicture) || ''" 
                    alt="Comment user" 
                    imageClass="rounded-circle me-2" 
                    width="30" 
                    height="30" 
                  />                  <div class="p-2 bg-light rounded flex-grow-1">
                    <div class="d-flex justify-content-between">
                      <strong>{{ comment.user ? comment.user.username : 'Unknown User' }}</strong>
                      <small class="text-muted">{{ formatDate(comment.createdAt) }}</small>
                    </div>
                    <p class="mb-1">{{ comment.content }}</p>
                    
                    <!-- 评论图片预览 -->
                    <div v-if="comment.imageUrl" class="mt-1">
                      <SafeImage 
                        :src="comment.imageUrl" 
                        alt="评论图片" 
                        imageClass="img-thumbnail comment-preview" 
                        style="max-width: 150px; max-height: 100px; cursor: pointer;"
                      />
                    </div>
                  </div>
                </div>
                
                <div v-if="mood.comments.length > 2" class="text-center mt-2">
                  <router-link :to="`/moods/${mood.id}`" class="btn btn-sm btn-link">
                    查看全部 {{ mood.comments.length }} 条评论
                  </router-link>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 无数据提示 -->
        <div v-else class="text-center my-5 p-5 border rounded bg-light">
          <i class="bi bi-emoji-frown display-1 text-muted"></i>
          <h4 class="mt-3">没有找到心情</h4>
          <p class="text-muted">尝试调整筛选条件或发布新的心情。</p>
          <router-link to="/moods/create" class="btn btn-primary mt-2">
            发布心情
          </router-link>        </div>
      </div>
    </div>
    
    <!-- 图片放大模态框 -->
    <div class="modal fade" id="imageModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">图片预览</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body text-center">
            <img :src="modalImageUrl" class="img-fluid" alt="图片预览" v-if="modalImageUrl">
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, reactive, nextTick } from 'vue'
import { useStore } from 'vuex'
// Removed unused import: useRouter
import SafeImage from '../components/SafeImage.vue'

export default {
  name: 'HomeView',
  components: {
    SafeImage
  },
  setup() {
    const store = useStore()
    // Router functionality is not currently used in this component
    // 状态
    const moods = computed(() => store.getters.allMoods)
    const isLoading = computed(() => store.state.loading)
    const currentUser = computed(() => store.getters.currentUser)
      // 视图控制
    const activeView = ref('all')
    const modalImageUrl = ref('')
    const viewTitle = computed(() => {
      switch (activeView.value) {
        case 'all': return '全部心情'
        case 'feed': return '我的动态'
        case 'friends': return '好友动态'
        default: return '心情'
      }
    })
    
    // 筛选器
    const filters = reactive({
      moodType: '',
      privacyLevel: '',
      location: ''
    })
      // 加载数据
    const loadMoods = async () => {
      switch (activeView.value) {
        case 'all':
          await store.dispatch('fetchMoods', filters)
          break
        case 'feed':
          await store.dispatch('fetchFeed')
          break
        case 'friends':
          await store.dispatch('fetchFriendMoods')
          break
      }
      
      // 加载完数据后初始化 Bootstrap 下拉菜单
      await nextTick()
      initializeDropdowns()
    }
      // 初始化 Bootstrap 下拉菜单
    const initializeDropdowns = () => {
      // 检查 Bootstrap 是否已加载
      if (typeof window.bootstrap === 'undefined') {
        console.warn('Bootstrap is not loaded yet, skipping dropdown initialization')
        return
      }
      
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
    
    // 切换视图
    const switchView = (view) => {
      activeView.value = view
      loadMoods()
    }
    
    // 应用筛选
    const applyFilters = () => {
      if (activeView.value !== 'all') {
        activeView.value = 'all'
      }
      loadMoods()
    }
    
    // 点赞功能
    const toggleLike = async (moodId) => {
      try {
        await store.dispatch('toggleLike', moodId)
      } catch (error) {
        console.error('Error toggling like:', error)
      }
    }
    
    // 删除心情
    const deleteMood = async (moodId) => {
      if (confirm('确定要删除这条心情吗？')) {
        try {
          await store.dispatch('deleteMood', moodId)
        } catch (error) {
          console.error('Error deleting mood:', error)
        }
      }
    }
    
    // 更新隐私设置
    const updatePrivacy = async (moodId, privacyLevel) => {
      try {
        await store.dispatch('updatePrivacy', { moodId, privacyLevel })
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
    
    // 打开图片模态框
    const openImageModal = (imageUrl) => {
      modalImageUrl.value = imageUrl
      const modal = new window.bootstrap.Modal(document.getElementById('imageModal'))
      modal.show()
    }
      // 加载初始数据
    onMounted(async () => {
      await loadMoods()
      // 确保下拉菜单初始化
      await nextTick()
      initializeDropdowns()
    })
      return {
      moods,
      isLoading,
      currentUser,
      activeView,
      viewTitle,
      modalImageUrl,
      filters,
      switchView,
      applyFilters,
      toggleLike,
      deleteMood,
      updatePrivacy,
      formatDate,
      getMoodTypeName,
      openImageModal
    }
  }
}
</script>

<style scoped>
.mood-card {
  transition: all 0.2s ease;
}

.mood-card:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.mood-image {
  border: 1px solid #e0e0e0;
  transition: transform 0.2s ease;
}

.mood-image:hover {
  transform: scale(1.02);
}

.mood-emoji {
  font-size: 1.2em;
  margin-right: 0.5em;
}

.mood-tag {
  display: inline-block;
  background: #e3f2fd;
  color: #1976d2;
  padding: 0.2em 0.5em;
  border-radius: 12px;
  font-size: 0.85em;
  margin-right: 0.5em;
  margin-bottom: 0.25em;
}

.mood-type-badge {
  font-size: 0.8em;
}

.comment-preview {
  border: 1px solid #dee2e6;
}
</style>
