<template>
  <nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container">
      <router-link class="navbar-brand" to="/">
        <span class="fw-bold text-primary">MoodShare</span>
      </router-link>
      
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" 
              data-bs-target="#navbarNav" aria-controls="navbarNav" 
              aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      
      <div class="collapse navbar-collapse" id="navbarNav">        <!-- 未登录显示登录/注册链接 -->
        <ul v-if="!isLoggedIn" class="navbar-nav ms-auto">
          <li class="nav-item">
            <router-link class="nav-link" to="/about">关于我们</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/login">登录</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/register">注册</router-link>
          </li>
        </ul>
        
        <!-- 已登录显示功能菜单 -->
        <ul v-else class="navbar-nav me-auto">
          <li class="nav-item">
            <router-link class="nav-link" to="/">首页</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/moods/create">
              <i class="bi bi-plus-circle"></i> 发布心情
            </router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/friends">好友</router-link>
          </li>
        </ul>          <!-- 用户菜单 -->
        <ul v-if="isLoggedIn" class="navbar-nav ms-auto">
            <!-- 通知下拉菜单 -->
          <li class="nav-item dropdown">            <a class="nav-link dropdown-toggle position-relative" href="#" id="notificationsDropdown" 
               role="button" data-bs-toggle="dropdown" aria-expanded="false"
               @click.prevent="handleNotificationsClick">
              <i class="bi bi-bell"></i>
              <span v-if="unreadNotificationCount > 0" class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                {{ unreadNotificationCount > 9 ? '9+' : unreadNotificationCount }}
                <span class="visually-hidden">未读通知</span>
              </span>
            </a>
            <ul class="dropdown-menu dropdown-menu-end notifications-dropdown" aria-labelledby="notificationsDropdown">
              <li v-if="notifications.length === 0" class="dropdown-item text-center">暂无通知</li>
              <template v-else>
                <li v-for="notification in notifications.slice(0, 5)" :key="notification.id" 
                    :class="{'notification-unread': !notification.read}">
                  <a class="dropdown-item" href="#" @click.prevent="handleNotificationClick(notification)">
                    <div class="d-flex justify-content-between">
                      <span>{{ notification.message || notification.content }}</span>
                      <small class="text-muted">{{ formatDate(notification.createdAt) }}</small>
                    </div>
                  </a>
                </li>
                <li><hr class="dropdown-divider"></li>
                <li>
                  <a class="dropdown-item text-center" href="#" @click.prevent="markAllNotificationsAsRead">
                    标记全部已读
                  </a>
                </li>
              </template>
            </ul>
          </li>
          
          <li class="nav-item dropdown">            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" 
               role="button" data-bs-toggle="dropdown" aria-expanded="false"
               @click.prevent="handleUserMenuClick">
              {{ currentUser ? currentUser.username : '用户' }}
            </a>
            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdown">
              <li>
                <router-link class="dropdown-item" to="/profile">个人资料</router-link>
              </li>
              <li>
                <router-link class="dropdown-item" to="/about">关于我们</router-link>
              </li>
              <li><hr class="dropdown-divider"></li>
              <li>
                <a class="dropdown-item" href="#" @click.prevent="logout">退出登录</a>
              </li>
            </ul>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script>
import { computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

export default {
  name: 'NavbarComponent',
  setup() {
    const store = useStore()
    const router = useRouter()
      const isLoggedIn = computed(() => store.getters.isLoggedIn)
    const currentUser = computed(() => store.getters.currentUser)
    const unreadNotificationCount = computed(() => store.getters.unreadNotificationCount)
    const notifications = computed(() => store.getters.allNotifications)
    
    // 定时获取未读通知数
    let notificationTimer = null
    
    onMounted(async () => {
      console.log('🚀 Navbar - Component mounted, isLoggedIn:', isLoggedIn.value)
      
      if (isLoggedIn.value) {
        // 初始加载通知数量
        store.dispatch('getUnreadNotificationCount')
        
        // 设置定时器每60秒更新一次
        notificationTimer = setInterval(() => {
          if (isLoggedIn.value) {
            store.dispatch('getUnreadNotificationCount')
          }
        }, 60000)      }
      
      // 添加全局点击事件来关闭下拉菜单
      document.addEventListener('click', (event) => {
        const dropdowns = document.querySelectorAll('.dropdown-menu.show')
        dropdowns.forEach(menu => {
          const dropdown = menu.closest('.dropdown')
          if (dropdown && !dropdown.contains(event.target)) {
            menu.classList.remove('show')
          }
        })      })
    })
      // 点击通知图标时加载通知
    const handleNotificationsClick = (event) => {
      console.log('🔔 Navbar - Notifications clicked')
      if (isLoggedIn.value) {
        store.dispatch('fetchNotifications')
      }
      
      // 先关闭所有其他下拉菜单
      document.querySelectorAll('.dropdown-menu.show').forEach(menu => {
        menu.classList.remove('show')
      })
      
      // 然后切换当前下拉菜单
      const dropdown = event.target.closest('.dropdown')
      const menu = dropdown.querySelector('.dropdown-menu')
      if (menu) {
        menu.classList.add('show')
      }
    }    // 点击单个通知
    const handleNotificationClick = (notification) => {
      // 标记为已读
      store.dispatch('markNotificationAsRead', notification.id)
      
      // 关闭下拉菜单
      document.querySelectorAll('.dropdown-menu.show').forEach(menu => {
        menu.classList.remove('show')
      })
      
      // 根据通知类型执行不同操作
      const notificationType = notification.type || notification.notificationType;
      const relatedId = notification.relatedId || notification.targetId;
      
      if (notificationType === 'FRIEND_REQUEST') {
        router.push('/friends')
      } else if (['MOOD_LIKE', 'MOOD_COMMENT'].includes(notificationType)) {
        // 如果有关联的心情ID，跳转到心情详情
        if (relatedId) {
          router.push(`/moods/${relatedId}`)
        }
      }
    }
    
    // 标记所有通知为已读
    const markAllNotificationsAsRead = () => {
      store.dispatch('markAllNotificationsAsRead')
      // 关闭下拉菜单
      document.querySelectorAll('.dropdown-menu.show').forEach(menu => {
        menu.classList.remove('show')
      })
    }// 点击用户菜单
    const handleUserMenuClick = (event) => {
      console.log('👤 Navbar - User menu clicked')
      
      // 先关闭所有其他下拉菜单
      document.querySelectorAll('.dropdown-menu.show').forEach(menu => {
        menu.classList.remove('show')
      })
      
      // 然后切换当前下拉菜单
      const dropdown = event.target.closest('.dropdown')
      const menu = dropdown.querySelector('.dropdown-menu')
      if (menu) {
        menu.classList.add('show')
      }
    }
    
    // 退出登录
    const logout = () => {
      // 清除通知定时器
      if (notificationTimer) {
        clearInterval(notificationTimer)
      }
      
      store.dispatch('logout')
      router.push('/login')
    }
      // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return new Intl.DateTimeFormat('zh-CN', { 
        month: 'short', 
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      }).format(date)
    }
      return {
      isLoggedIn,
      currentUser,
      unreadNotificationCount,
      notifications,
      handleNotificationsClick,
      handleUserMenuClick,
      handleNotificationClick,
      markAllNotificationsAsRead,
      formatDate,
      logout
    }
  }
}
</script>

<style scoped>
.navbar {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.navbar-brand {
  font-size: 1.5rem;
}

.nav-link {
  font-weight: 500;
}

.notifications-dropdown {
  width: 300px;
  max-height: 400px;
  overflow-y: auto;
}

.notification-unread {
  background-color: rgba(13, 110, 253, 0.05);
  font-weight: 500;
}

.notification-unread .dropdown-item:hover {
  background-color: rgba(13, 110, 253, 0.1);
}

/* 确保下拉菜单正确显示 */
.dropdown-menu {
  display: none;
  z-index: 1050;
}

.dropdown-menu.show {
  display: block !important;
}

/* 确保下拉菜单触发器可以正常工作 */
.dropdown-toggle {
  cursor: pointer;
}

.dropdown-toggle::after {
  display: inline-block;
  margin-left: 0.255em;
  vertical-align: 0.255em;
  content: "";
  border-top: 0.3em solid;
  border-right: 0.3em solid transparent;
  border-bottom: 0;
  border-left: 0.3em solid transparent;
}

/* 调试样式 */
.dropdown:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.dropdown-menu {
  z-index: 1050;
}
</style>
