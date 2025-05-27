<template>
  <div class="friends-view">
    <h2 class="mb-4">好友管理</h2>
    
    <div class="row">
      <!-- 左侧好友列表 -->
      <div class="col-md-8">
        <div class="card mb-4">          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <div class="d-flex align-items-center">
              <h5 class="mb-0 me-2">我的好友</h5>
              <button class="btn btn-sm btn-outline-secondary" @click="refreshFriendsList" :disabled="isLoading">
                <i class="bi bi-arrow-clockwise"></i>
              </button>
            </div>
            <button class="btn btn-sm btn-primary" @click="showAddFriendModal = true">
              <i class="bi bi-person-plus"></i> 添加好友
            </button>
          </div>
          <div class="card-body">
            <!-- 加载中 -->
            <div v-if="isLoading" class="text-center py-4">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
              </div>
              <p class="mt-2">加载中...</p>
            </div>
              <!-- 好友列表 -->
            <div v-else-if="friends.length > 0" class="list-group">
              <div 
                v-for="friend in friends" 
                :key="friend.id" 
                class="list-group-item d-flex justify-content-between align-items-center"
              >                <div class="d-flex align-items-center">
                  <SafeImage 
                    :src="friend.profilePicture" 
                    alt="Profile" 
                    imageClass="rounded-circle me-3" 
                    width="40" 
                    height="40"
                  />
                  <div>
                    <h6 class="mb-0">{{ friend.username }}</h6>
                    <small v-if="friend.bio" class="text-muted">{{ friend.bio }}</small>
                    <div><span class="badge bg-success">好友</span></div>
                  </div>
                </div>
                <div>
                  <router-link :to="`/user/${friend.id}`" class="btn btn-sm btn-outline-primary me-2">
                    查看资料
                  </router-link>
                  <button 
                    class="btn btn-sm btn-outline-danger" 
                    @click="confirmRemoveFriend(friend)"
                  >
                    删除好友
                  </button>
                </div>
              </div>
            </div>
            
            <!-- 无好友提示 -->
            <div v-else class="text-center py-4">
              <p class="text-muted">您还没有好友。</p>
              <button class="btn btn-primary mt-2" @click="showAddFriendModal = true">
                添加好友
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧好友请求 -->
      <div class="col-md-4">
        <div class="card">
          <div class="card-header bg-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0">好友请求</h5>
            <button class="btn btn-sm btn-outline-secondary" @click="refreshFriendRequests" :disabled="isLoadingRequests">
              <i class="bi bi-arrow-clockwise"></i>
            </button>
          </div>
          <div class="card-body">
            <!-- 加载中 -->
            <div v-if="isLoadingRequests" class="text-center py-4">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
              </div>
              <p class="mt-2">加载中...</p>
            </div>
              <!-- 好友请求列表 -->
            <div v-else-if="friendRequests.length > 0" class="list-group">
              <div 
                v-for="request in friendRequests" 
                :key="request.id" 
                class="list-group-item"
              >                <div class="d-flex align-items-center mb-2">
                  <SafeImage 
                    :src="request.profilePicture" 
                    alt="Profile" 
                    imageClass="rounded-circle me-2" 
                    width="40" 
                    height="40"
                  />
                  <div>
                    <h6 class="mb-0">{{ request.username }}</h6>
                    <small v-if="request.bio" class="text-muted">{{ request.bio }}</small>
                  </div>
                </div>
                <div class="d-flex">
                  <button 
                    class="btn btn-sm btn-success flex-grow-1 me-1" 
                    @click="acceptRequest(request.id)"
                    :disabled="isProcessing"
                  >
                    接受
                  </button>
                  <button 
                    class="btn btn-sm btn-outline-danger flex-grow-1" 
                    @click="rejectRequest(request.id)"
                    :disabled="isProcessing"
                  >
                    拒绝
                  </button>
                </div>
              </div>
            </div>
            
            <!-- 无请求提示 -->
            <div v-else class="text-center py-4">
              <p class="text-muted">没有待处理的好友请求。</p>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 添加好友模态框 -->
    <div class="modal fade" :class="{ show: showAddFriendModal }" tabindex="-1" 
         :style="{ display: showAddFriendModal ? 'block' : 'none' }">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">添加好友</h5>
            <button type="button" class="btn-close" @click="showAddFriendModal = false"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="sendFriendRequest">              <div class="mb-3">
                <label for="username" class="form-label">用户名</label>
                <input 
                  type="text" 
                  class="form-control" 
                  id="username" 
                  v-model="friendUsername" 
                  required
                  placeholder="输入对方的用户名（不是用户ID）"
                  autocomplete="username"
                >
                <div class="form-text">输入对方的完整用户名，如 "xiaoming" 或 "123456"</div>
              </div>
              
              <div v-if="addFriendError" class="alert alert-danger mb-3">
                {{ addFriendError }}
              </div>
              
              <div class="d-grid gap-2">
                <button 
                  type="submit" 
                  class="btn btn-primary" 
                  :disabled="isProcessing"
                >
                  <span v-if="isProcessing" class="spinner-border spinner-border-sm me-2"></span>
                  发送请求
                </button>
                <button type="button" class="btn btn-secondary" @click="showAddFriendModal = false">
                  取消
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
    <div class="modal-backdrop fade" :class="{ show: showAddFriendModal }" 
         v-if="showAddFriendModal"></div>
    
    <!-- 确认删除好友模态框 -->
    <div class="modal fade" :class="{ show: showRemoveModal }" tabindex="-1" 
         :style="{ display: showRemoveModal ? 'block' : 'none' }">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">删除好友</h5>
            <button type="button" class="btn-close" @click="showRemoveModal = false"></button>
          </div>
          <div class="modal-body">
            <p>确定要删除好友 <strong>{{ selectedFriend?.username }}</strong> 吗？</p>
            <p class="text-muted">删除后，您将不再是好友关系。</p>
          </div>
          <div class="modal-footer">
            <button 
              type="button" 
              class="btn btn-danger" 
              @click="removeFriend"
              :disabled="isProcessing"
            >
              <span v-if="isProcessing" class="spinner-border spinner-border-sm me-2"></span>
              确定删除
            </button>
            <button type="button" class="btn btn-secondary" @click="showRemoveModal = false">
              取消
            </button>
          </div>
        </div>
      </div>
    </div>
    <div class="modal-backdrop fade" :class="{ show: showRemoveModal }" 
         v-if="showRemoveModal"></div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'

export default {
  name: 'FriendsView',
  setup() {
    const store = useStore()
      // 状态
    const isLoading = computed(() => store.state.loading)
    const isLoadingRequests = ref(false)
    const isProcessing = ref(false)
    const addFriendError = ref('')
    
    // 数据
    const friends = computed(() => store.getters.userFriends)
    const friendRequests = computed(() => store.getters.friendRequests)
    
    // 模态框控制
    const showAddFriendModal = ref(false)
    const showRemoveModal = ref(false)
    
    // 表单数据
    const friendUsername = ref('')
    const selectedFriend = ref(null)
    
    // 加载数据
    const loadData = async () => {
      try {
        await store.dispatch('fetchFriends')
        
        isLoadingRequests.value = true
        await store.dispatch('fetchFriendRequests')
      } catch (error) {
        console.error('Error loading friends data:', error)
      } finally {
        isLoadingRequests.value = false
      }
    }    // 发送好友请求
    const sendFriendRequest = async () => {
      if (!friendUsername.value.trim()) return
      
      isProcessing.value = true
      addFriendError.value = ''
      
      try {
        console.log('尝试添加好友:', friendUsername.value.trim());
        // 传入用户名，不要尝试解析为用户ID
        const response = await store.dispatch('sendFriendRequest', friendUsername.value.trim());
        console.log('添加好友响应:', response);
        
        showAddFriendModal.value = false
        friendUsername.value = ''
        alert('好友请求已发送，对方接受后你们将成为好友')
      } catch (error) {
        console.error('Error sending friend request:', error);
        
        // 显示更具体的错误信息
        if (error.response && error.response.data && error.response.data.message) {
          addFriendError.value = error.response.data.message;
        } else if (error.message) {
          addFriendError.value = error.message;
        } else {
          addFriendError.value = '发送好友请求失败，请稍后再试';
        }
      } finally {
        isProcessing.value = false
      }
    }    // 接受好友请求
    const acceptRequest = async (requestId) => {
      isProcessing.value = true
      
      try {
        console.log('接受好友请求, ID:', requestId);
        const response = await store.dispatch('acceptFriendRequest', requestId);
        console.log('好友请求接受响应:', response);
        
        // 显示操作成功消息
        alert('已成功接受好友请求');
        
        // 强制刷新数据
        await loadData();
      } catch (error) {
        console.error('接受好友请求失败:', error);
        
        // 显示具体错误信息
        let errorMessage = '接受好友请求失败';
        if (error.response?.data?.message) {
          errorMessage = error.response.data.message;
        } else if (error.message) {
          errorMessage = error.message;
        }
        
        alert(errorMessage);
        
        // 刷新好友请求列表，确保UI显示正确
        refreshFriendRequests();
      } finally {
        isProcessing.value = false
      }
    }
    
    // 拒绝好友请求
    const rejectRequest = async (requestId) => {
      isProcessing.value = true
      
      try {
        await store.dispatch('rejectFriendRequest', requestId)
      } catch (error) {
        console.error('Error rejecting friend request:', error)
      } finally {
        isProcessing.value = false
      }
    }
    
    // 确认删除好友
    const confirmRemoveFriend = (friend) => {
      selectedFriend.value = friend
      showRemoveModal.value = true
    }
    
    // 删除好友
    const removeFriend = async () => {
      if (!selectedFriend.value) return
      
      isProcessing.value = true
      
      try {
        await store.dispatch('removeFriend', selectedFriend.value.id)
        showRemoveModal.value = false
        selectedFriend.value = null
      } catch (error) {
        console.error('Error removing friend:', error)
      } finally {
        isProcessing.value = false
      }
    }
      // 手动刷新好友请求
    const refreshFriendRequests = async () => {
      if (isLoadingRequests.value) return;
      
      isLoadingRequests.value = true;
      try {
        console.log('手动刷新好友请求列表...');
        await store.dispatch('fetchFriendRequests');
        console.log('好友请求数量:', friendRequests.value.length);
      } catch (error) {
        console.error('刷新好友请求失败:', error);
      } finally {
        isLoadingRequests.value = false;
      }
    };
    
    // 手动刷新好友列表
    const refreshFriendsList = async () => {
      if (isLoading.value) return;
      
      try {
        console.log('手动刷新好友列表...');
        await store.dispatch('fetchFriends');
        console.log('好友数量:', friends.value.length);
        alert('好友列表已更新');
      } catch (error) {
        console.error('刷新好友列表失败:', error);
        alert('刷新好友列表失败');
      }
    };
    
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
    
    // 加载初始数据
    onMounted(() => {
      loadData()
      
      // 设置定时器，每30秒刷新一次好友请求列表
      const requestTimer = setInterval(() => {
        console.log('自动刷新好友请求列表...');
        store.dispatch('fetchFriendRequests').catch(error => {
          console.error('自动刷新好友请求失败:', error);
        });
      }, 30000);
      
      // 组件卸载时清除定时器
      return () => {
        clearInterval(requestTimer);
      };    });
    
    return {
      isLoading,
      isLoadingRequests,
      isProcessing,
      addFriendError,
      friends,
      friendRequests,
      showAddFriendModal,
      showRemoveModal,
      friendUsername,
      selectedFriend,
      sendFriendRequest,
      acceptRequest,
      rejectRequest,
      confirmRemoveFriend,
      removeFriend,
      refreshFriendRequests,
      refreshFriendsList,
      formatDate
    }
  }
}
</script>

<style scoped>
.friends-view {
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
