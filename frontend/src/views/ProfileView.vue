<template>  <div class="profile-view">
    <h2 class="mb-4">{{ isOwnProfile ? '个人资料' : user.username + ' 的资料' }}</h2>
    
    <div class="row">
      <!-- 左侧资料卡 -->
      <div class="col-md-4">
        <div class="card mb-4">
          <div class="card-body text-center">
            <SafeImage 
              :src="user.profilePicture" 
              alt="Profile Picture" 
              imageClass="rounded-circle img-thumbnail mb-3" 
              width="150" 
              height="150"
            />
            <h3>{{ user.username }}</h3>
            <p v-if="isOwnProfile" class="text-muted">{{ user.email }}</p>
            
            <div v-if="user.bio" class="mb-3">
              <p>{{ user.bio }}</p>
            </div>
            
            <button 
              v-if="isOwnProfile"
              class="btn btn-outline-primary w-100" 
              @click="showEditModal = true"
            >
              编辑资料
            </button>
          </div>
        </div>
        
        <div class="card">
          <div class="card-body">
            <h5 class="card-title">账号信息</h5>
            <ul class="list-group list-group-flush">
              <li class="list-group-item d-flex justify-content-between align-items-center">
                用户名
                <span class="badge bg-primary rounded-pill">{{ user.username }}</span>
              </li>
              <li v-if="isOwnProfile" class="list-group-item d-flex justify-content-between align-items-center">
                电子邮箱
                <span class="text-muted">{{ user.email }}</span>
              </li>
              <li v-if="isOwnProfile" class="list-group-item d-flex justify-content-between align-items-center">
                用户角色
                <span class="badge bg-secondary rounded-pill">{{ user.role || 'USER' }}</span>
              </li>
            </ul>
            <button 
              v-if="isOwnProfile"
              class="btn btn-outline-warning w-100 mt-3" 
              @click="showPasswordModal = true"
            >
              修改密码
            </button>
          </div>
        </div>
      </div>        <!-- 右侧内容 -->
      <div class="col-md-8">
        <div class="card mb-4">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0">{{ isOwnProfile ? '我的心情' : 'TA的心情' }}</h5>
            <router-link 
              v-if="isOwnProfile"
              to="/moods/create" 
              class="btn btn-sm btn-outline-primary"
            >
              发布心情
            </router-link>
          </div>
          <div class="card-body">
            <!-- 加载中 -->
            <div v-if="loadingMoods" class="text-center py-4">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
              </div>
              <p class="mt-2">加载中...</p>
            </div>
            
            <!-- 心情列表 -->
            <div v-else-if="userMoods.length > 0" class="list-group">
              <div v-for="mood in userMoods" :key="mood.id" class="list-group-item">
                <div class="d-flex justify-content-between align-items-center mb-2">
                  <h6 class="mb-0">
                    <span :class="getMoodTypeClass(mood.moodType)">{{ getMoodTypeEmoji(mood.moodType) }}</span>
                    {{ mood.title || '无标题' }}
                  </h6>
                  <small class="text-muted">{{ formatDate(mood.createdAt) }}</small>
                </div>
                <p class="mb-2">{{ mood.content }}</p>
                <div class="d-flex justify-content-end">
                  <router-link :to="`/moods/${mood.id}`" class="btn btn-sm btn-outline-primary">
                    查看详情
                  </router-link>
                </div>
              </div>
            </div>
            
            <!-- 无心情提示 -->
            <div v-else class="text-center py-4">
              <p class="text-muted">{{ isOwnProfile ? '您还没有发布任何心情。' : 'TA还没有发布任何心情。' }}</p>
              <router-link 
                v-if="isOwnProfile"
                to="/moods/create" 
                class="btn btn-primary mt-2"
              >
                立即发布
              </router-link>
            </div>
          </div>
        </div>
        
        <div class="card">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0">{{ isOwnProfile ? '好友' : 'TA的好友' }}</h5>
            <router-link 
              v-if="isOwnProfile"
              to="/friends" 
              class="btn btn-sm btn-outline-primary"
            >
              管理好友
            </router-link>
          </div>
          <div class="card-body">
            <!-- 加载中 -->
            <div v-if="loadingFriends" class="text-center py-4">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
              </div>
              <p class="mt-2">加载中...</p>
            </div>
            
            <!-- 好友列表 -->
            <div v-else-if="friends.length > 0" class="list-group">
              <div v-for="friend in friends.slice(0, 5)" :key="friend.id" 
                   class="list-group-item d-flex align-items-center">
                <SafeImage 
                  :src="friend.profilePicture" 
                  alt="Profile" 
                  imageClass="rounded-circle me-3" 
                  width="40" 
                  height="40"
                />
                <span>{{ friend.username }}</span>
              </div>
              
              <div v-if="friends.length > 5" class="text-center mt-3">
                <router-link 
                  v-if="isOwnProfile"
                  to="/friends" 
                  class="btn btn-sm btn-link"
                >
                  查看全部 {{ friends.length }} 位好友
                </router-link>
                <span v-else class="text-muted">
                  共 {{ friends.length }} 位好友
                </span>
              </div>
            </div>
            
            <!-- 无好友提示 -->
            <div v-else class="text-center py-4">
              <p class="text-muted">{{ isOwnProfile ? '您还没有添加任何好友。' : 'TA还没有添加任何好友。' }}</p>
              <router-link 
                v-if="isOwnProfile"
                to="/friends" 
                class="btn btn-primary mt-2"
              >
                添加好友
              </router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
      <!-- 编辑资料模态框 -->
    <div 
      v-if="isOwnProfile"
      class="modal fade" 
      :class="{ show: showEditModal }" 
      tabindex="-1" 
      :style="{ display: showEditModal ? 'block' : 'none' }"
    >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">编辑个人资料</h5>
            <button type="button" class="btn-close" @click="showEditModal = false"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="updateProfile">
              <div class="mb-3">
                <label for="bio" class="form-label">个人简介</label>
                <textarea 
                  class="form-control" 
                  id="bio" 
                  v-model="profileForm.bio" 
                  rows="3"
                  placeholder="介绍一下自己..."
                ></textarea>
              </div>                <div class="mb-3">
                <label for="profilePicture" class="form-label">头像</label>
                <div class="d-flex mb-2 align-items-center">
                  <SafeImage 
                    :src="profileForm.profilePicture || ''" 
                    alt="Profile Picture Preview" 
                    imageClass="img-thumbnail me-3" 
                    width="100" 
                    height="100"
                  />
                  <div>
                    <input 
                      type="file" 
                      class="form-control mb-2" 
                      id="profilePicture" 
                      @change="handleImageSelected"
                      accept="image/*"
                      :disabled="isUploading"
                    >
                    <div class="text-muted small">支持 JPG, PNG, GIF 格式图片</div>
                    <div v-if="hasUnsavedChanges" class="text-warning small mt-1">
                      <i class="bi bi-exclamation-triangle"></i> 有未保存的更改
                    </div>
                  </div>
                </div>                <div class="d-flex align-items-center">
                  <span class="me-2">或者直接输入图片URL:</span>
                  <input 
                    type="text" 
                    class="form-control" 
                    id="profilePictureUrl" 
                    v-model="profileForm.profilePicture"
                    placeholder="输入图片链接或路径..."
                    autocomplete="url"
                  >
                </div>
              </div>
                <div class="text-center mt-3">
                <button type="submit" class="btn btn-primary" :disabled="isLoading || isUploading">
                  <span v-if="isLoading || isUploading" class="spinner-border spinner-border-sm me-2"></span>
                  {{ isUploading ? '上传中...' : '保存修改' }}
                </button>
                <button type="button" class="btn btn-secondary ms-2" @click="showEditModal = false" :disabled="isUploading">
                  取消
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>    <div 
      v-if="isOwnProfile && showEditModal"
      class="modal-backdrop fade show"
    ></div>
    
    <!-- 修改密码模态框 -->
    <div 
      v-if="isOwnProfile"
      class="modal fade" 
      :class="{ show: showPasswordModal }" 
      tabindex="-1" 
      :style="{ display: showPasswordModal ? 'block' : 'none' }"
    >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">修改密码</h5>
            <button type="button" class="btn-close" @click="showPasswordModal = false"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="changePassword">
              <div class="mb-3">
                <label for="currentPassword" class="form-label">当前密码</label>
                <input 
                  type="password" 
                  class="form-control" 
                  id="currentPassword" 
                  v-model="passwordForm.oldPassword" 
                  required
                >
              </div>
              
              <div class="mb-3">
                <label for="newPassword" class="form-label">新密码</label>
                <input 
                  type="password" 
                  class="form-control" 
                  id="newPassword" 
                  v-model="passwordForm.newPassword" 
                  required
                >
              </div>
              
              <div class="mb-3">
                <label for="confirmPassword" class="form-label">确认新密码</label>
                <input 
                  type="password" 
                  class="form-control" 
                  id="confirmPassword" 
                  v-model="passwordForm.confirmPassword" 
                  required
                >
                <div class="form-text text-danger" v-if="passwordMismatch">
                  两次输入的密码不匹配
                </div>
              </div>
              
              <div class="text-center mt-3">
                <button 
                  type="submit" 
                  class="btn btn-primary" 
                  :disabled="isLoading || passwordMismatch"
                >
                  <span v-if="isLoading" class="spinner-border spinner-border-sm me-2"></span>
                  修改密码
                </button>
                <button type="button" class="btn btn-secondary ms-2" @click="showPasswordModal = false">
                  取消
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>    </div>
    <div 
      v-if="isOwnProfile && showPasswordModal"
      class="modal-backdrop fade show"
    ></div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useStore } from 'vuex'
import SafeImage from '../components/SafeImage.vue'
import { fileApi } from '../api/api'

export default {
  name: 'ProfileView',
  props: ['id'],
  components: {
    SafeImage
  },  setup(props) {
    const store = useStore()
    
    // 判断是否是查看自己的资料
    const isOwnProfile = computed(() => !props.id || props.id === store.state.user?.id?.toString())
    
    // 用户数据 - 如果是查看别人的资料，需要单独加载
    const targetUser = ref({})
    const isLoadingUser = ref(false)
    
    // 使用 store 中的用户数据（仅当查看自己的资料时）
    const currentUser = computed(() => store.state.user || {})
    
    // 显示的用户数据 - 根据是否是自己的资料来决定
    const user = computed(() => isOwnProfile.value ? currentUser.value : targetUser.value)
    
    // 文件上传状态
    const isUploading = ref(false)
    
    // 心情和好友数据
    const userMoods = ref([])
    const friends = ref([])
    const loadingMoods = ref(false)
    const loadingFriends = ref(false)
    
    // 模态框控制
    const showEditModal = ref(false)
    const showPasswordModal = ref(false)
    
    // 表单数据
    const profileForm = reactive({
      bio: '',
      profilePicture: ''
    })
    
    const passwordForm = reactive({
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    })
      // 状态
    const isLoading = computed(() => store.state.loading)
    const passwordMismatch = computed(() => 
      passwordForm.newPassword && 
      passwordForm.confirmPassword && 
      passwordForm.newPassword !== passwordForm.confirmPassword
    )
      // 检查是否有未保存的更改
    const hasUnsavedChanges = computed(() => {
      return profileForm.bio !== (user.value.bio || '') || 
             profileForm.profilePicture !== (user.value.profilePicture || '')
    })
    
    // 加载用户心情数据
    const loadUserMoods = async () => {
      loadingMoods.value = true
      try {
        const response = await store.dispatch('fetchUserMoods')
        userMoods.value = response || []
        console.log('用户心情加载成功:', userMoods.value)
      } catch (error) {
        console.error('加载用户心情失败:', error)
      } finally {
        loadingMoods.value = false
      }
    }
    
    // 加载用户好友数据
    const loadUserFriends = async () => {
      loadingFriends.value = true
      try {
        const response = await store.dispatch('fetchFriends')
        friends.value = response || []
        console.log('用户好友加载成功:', friends.value)
      } catch (error) {
        console.error('加载用户好友失败:', error)
      } finally {
        loadingFriends.value = false
      }
    }
    
    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return new Intl.DateTimeFormat('zh-CN', { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric' 
      }).format(date)
    }
    
    // 获取心情类型对应的表情和样式
    const getMoodTypeEmoji = (moodType) => {
      switch(moodType) {
        case 'HAPPY': return '😄'
        case 'SAD': return '😢'
        case 'ANGRY': return '😠'
        case 'EXCITED': return '🤩'
        case 'CALM': return '😌'
        default: return '😐'
      }
    }
    
    const getMoodTypeClass = (moodType) => {
      switch(moodType) {
        case 'HAPPY': return 'text-success'
        case 'SAD': return 'text-primary'
        case 'ANGRY': return 'text-danger'
        case 'EXCITED': return 'text-warning'
        case 'CALM': return 'text-info'
        default: return 'text-muted'
      }
    }    // 加载用户数据
    const loadUserData = async () => {
      isLoadingUser.value = true
      try {
        if (isOwnProfile.value) {
          // 加载自己的资料
          const userData = await store.dispatch('fetchUserProfile')
          
          // 初始化编辑表单
          profileForm.bio = userData.bio || ''
          profileForm.profilePicture = userData.profilePicture || ''
          
          // 加载心情和好友数据（只有自己的资料才加载）
          loadUserMoods()
          loadUserFriends()
        } else {
          // 加载指定用户的资料
          const userData = await store.dispatch('fetchUserById', props.id)
          targetUser.value = userData
          
          // 暂时不加载其他用户的心情和好友列表
          // 如果需要的话，可以在后端添加相应的API
        }
      } catch (error) {
        console.error('Error loading user data:', error)
      } finally {
        isLoadingUser.value = false
      }
    }
    
    // 监听路由参数变化
    watch(() => props.id, () => {
      loadUserData()
    }, { immediate: false })
    
    onMounted(() => {
      loadUserData()
    })
      // 处理图片选择
    const handleImageSelected = async (event) => {
      const file = event.target.files[0];
      if (!file) return;

      const validTypes = ['image/jpeg', 'image/png', 'image/gif'];
      if (!validTypes.includes(file.type)) {
        alert('请选择有效的图片文件 (JPG, PNG, GIF)');
        event.target.value = ''; // Clear the file input
        return;
      }

      isUploading.value = true;      try {
        const response = await fileApi.uploadAvatar(file);

        if (response.data && response.data.success && response.data.url) {
          profileForm.profilePicture = response.data.url;
          console.log('头像上传成功:', profileForm.profilePicture);
          // 提示用户图片已上传，需要保存
          alert('图片上传成功！请点击"保存修改"按钮来更新您的头像。');
        } else {
          // Handle cases where the server indicates failure or returns unexpected data
          const serverMessage = response.data?.message || '服务器未返回有效的图片URL或指示上传失败。';
          console.error('头像上传失败 (服务器响应问题):', serverMessage, response.data);
          alert(`上传失败：${serverMessage}`);
        }
      } catch (error) {
        console.error('头像上传处理发生错误:', error); // Log the full error object
        let errorMessage = '上传失败：未知错误';
        if (error.response) {
          // The request was made and the server responded with a status code
          // that falls out of the range of 2xx
          errorMessage = `上传失败：${error.response.data?.message || error.response.statusText || `服务器错误 (状态码: ${error.response.status})`}`;
        } else if (error.request) {
          // The request was made but no response was received
          errorMessage = '上传失败：无法连接到服务器，请检查网络连接。';
        } else {
          // Something happened in setting up the request that triggered an Error
          errorMessage = `上传失败：${error.message || '客户端发生错误'}`;
        }
        alert(errorMessage);
      } finally {
        isUploading.value = false;
        // Clear the file input in finally to ensure it's always cleared,
        // allowing re-selection of the same file if needed.
        if (event.target) {
            event.target.value = '';
        }
      }
    }    // 更新个人资料
    const updateProfile = async () => {
      try {
        console.log('开始更新个人资料:', {
          bio: profileForm.bio,
          profilePicture: profileForm.profilePicture
        })
        
        const updatedUser = await store.dispatch('updateProfile', {
          bio: profileForm.bio,
          profilePicture: profileForm.profilePicture
        })
        
        console.log('API返回的更新后用户数据:', updatedUser)
        console.log('Store中的用户数据:', store.state.user)
        
        // 关闭模态框
        showEditModal.value = false
        
        alert('个人资料更新成功！')
      } catch (error) {
        console.error('Error updating profile:', error)
        alert('更新个人资料失败: ' + (error.response?.data?.message || '请稍后重试'))
      }
    }
    
    // 修改密码
    const changePassword = async () => {
      if (passwordMismatch.value) return
      
      try {
        await store.dispatch('changePassword', {
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
          // 重置表单
        passwordForm.oldPassword = ''
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
          // 关闭模态框
        showPasswordModal.value = false
      } catch (error) {
        console.error('Error changing password:', error)
      }
    }
    
    return {
      isOwnProfile,
      user,
      isLoadingUser,
      userMoods,
      friends,
      loadingMoods,
      loadingFriends,
      showEditModal,
      showPasswordModal,
      profileForm,
      passwordForm,
      isLoading,
      isUploading,
      passwordMismatch,
      hasUnsavedChanges,
      formatDate,
      getMoodTypeEmoji,
      getMoodTypeClass,
      handleImageSelected,
      updateProfile,
      changePassword
    }
  }
}
</script>

<style scoped>
.profile-view {
  max-width: 1100px;
  margin: 0 auto;
  padding-top: 20px;
}

.modal.show {
  display: block;
  padding-right: 17px;
}

.modal-backdrop.show {
  opacity: 0.5;
}
</style>
