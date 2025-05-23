<template>
  <div class="profile-view">
    <h2 class="mb-4">个人资料</h2>
    
    <div class="row">
      <!-- 左侧资料卡 -->
      <div class="col-md-4">
        <div class="card mb-4">
          <div class="card-body text-center">
            <img 
              :src="user.profilePicture || 'https://via.placeholder.com/150'" 
              alt="Profile Picture" 
              class="rounded-circle img-thumbnail mb-3" 
              width="150" 
              height="150"
            >
            <h3>{{ user.username }}</h3>
            <p class="text-muted">{{ user.email }}</p>
            
            <div v-if="user.bio" class="mb-3">
              <p>{{ user.bio }}</p>
            </div>
            
            <button 
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
              <li class="list-group-item d-flex justify-content-between align-items-center">
                电子邮箱
                <span class="text-muted">{{ user.email }}</span>
              </li>
              <li class="list-group-item d-flex justify-content-between align-items-center">
                用户角色
                <span class="badge bg-secondary rounded-pill">{{ user.role || 'USER' }}</span>
              </li>
            </ul>
            <button class="btn btn-outline-warning w-100 mt-3" @click="showPasswordModal = true">
              修改密码
            </button>
          </div>
        </div>
      </div>
      
      <!-- 右侧内容 -->
      <div class="col-md-8">
        <div class="card mb-4">
          <div class="card-header bg-white">
            <h5 class="mb-0">我的心情</h5>
          </div>
          <div class="card-body">
            <!-- TODO: 显示用户发布的心情 -->
            <p class="text-center text-muted py-4">
              这里将显示您发布的心情
            </p>
          </div>
        </div>
        
        <div class="card">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0">好友</h5>
            <router-link to="/friends" class="btn btn-sm btn-outline-primary">
              管理好友
            </router-link>
          </div>
          <div class="card-body">
            <!-- TODO: 显示部分好友 -->
            <p class="text-center text-muted py-4">
              这里将显示您的好友
            </p>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 编辑资料模态框 -->
    <div class="modal fade" :class="{ show: showEditModal }" tabindex="-1" 
         :style="{ display: showEditModal ? 'block' : 'none' }">
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
              </div>
              
              <div class="mb-3">
                <label for="profilePicture" class="form-label">头像 URL</label>
                <input 
                  type="url" 
                  class="form-control" 
                  id="profilePicture" 
                  v-model="profileForm.profilePicture"
                  placeholder="输入图片链接..."
                >
                <div class="form-text">输入一个有效的图片URL地址来更新头像。</div>
              </div>
              
              <div class="text-center mt-3">
                <button type="submit" class="btn btn-primary" :disabled="isLoading">
                  <span v-if="isLoading" class="spinner-border spinner-border-sm me-2"></span>
                  保存修改
                </button>
                <button type="button" class="btn btn-secondary ms-2" @click="showEditModal = false">
                  取消
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
    <div class="modal-backdrop fade" :class="{ show: showEditModal }" 
         v-if="showEditModal"></div>
    
    <!-- 修改密码模态框 -->
    <div class="modal fade" :class="{ show: showPasswordModal }" tabindex="-1" 
         :style="{ display: showPasswordModal ? 'block' : 'none' }">
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
      </div>
    </div>
    <div class="modal-backdrop fade" :class="{ show: showPasswordModal }" 
         v-if="showPasswordModal"></div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useStore } from 'vuex'

export default {
  name: 'ProfileView',
  setup() {
    const store = useStore()
    
    // 用户信息
    const user = reactive({
      username: '',
      email: '',
      bio: '',
      profilePicture: '',
      role: ''
    })
    
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
    
    // 加载用户数据
    onMounted(async () => {
      try {
        const userData = await store.dispatch('fetchUserProfile')
        
        // 更新用户信息
        Object.assign(user, userData)
        
        // 初始化编辑表单
        profileForm.bio = user.bio || ''
        profileForm.profilePicture = user.profilePicture || ''
      } catch (error) {
        console.error('Error loading profile:', error)
      }
    })
    
    // 更新个人资料
    const updateProfile = async () => {
      try {
        const updatedUser = await store.dispatch('updateProfile', {
          bio: profileForm.bio,
          profilePicture: profileForm.profilePicture
        })
        
        // 更新本地数据
        Object.assign(user, updatedUser)
        
        // 关闭模态框
        showEditModal.value = false
      } catch (error) {
        console.error('Error updating profile:', error)
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
      user,
      showEditModal,
      showPasswordModal,
      profileForm,
      passwordForm,
      isLoading,
      passwordMismatch,
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
