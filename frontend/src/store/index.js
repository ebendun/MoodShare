import { createStore } from 'vuex'
import { userApi, moodApi, friendApi, notificationApi } from '../api/api'
import { jwtDecode } from 'jwt-decode'

export default createStore({
  state: {
    token: localStorage.getItem('token') || '',
    user: null,
    moods: [],
    mood: null,
    friends: [],
    friendRequests: [],
    notifications: [],
    unreadNotificationCount: 0,
    loading: false,
    error: null
  },

  getters: {
    isLoggedIn: state => !!state.token,
    currentUser: state => state.user,
    allMoods: state => state.moods,
    currentMood: state => state.mood,
    userFriends: state => state.friends,
    friendRequests: state => state.friendRequests,
    allNotifications: state => state.notifications,
    unreadNotificationCount: state => state.unreadNotificationCount,
    isLoading: state => state.loading,
    error: state => state.error
  },

  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
      localStorage.setItem('token', token)
    },
    SET_USER(state, user) {
      state.user = user
    },
    CLEAR_AUTH(state) {
      state.token = ''
      state.user = null
      localStorage.removeItem('token')
    },
    SET_MOODS(state, moods) {
      state.moods = moods
    },
    SET_MOOD(state, mood) {
      state.mood = mood
    },
    ADD_MOOD(state, mood) {
      state.moods.unshift(mood)
    },
    UPDATE_MOOD(state, updatedMood) {
      const index = state.moods.findIndex(m => m.id === updatedMood.id)
      if (index !== -1) {
        state.moods.splice(index, 1, updatedMood)
      }
      if (state.mood && state.mood.id === updatedMood.id) {
        state.mood = updatedMood
      }
    },
    REMOVE_MOOD(state, moodId) {
      state.moods = state.moods.filter(m => m.id !== moodId)
      if (state.mood && state.mood.id === moodId) {
        state.mood = null
      }
    },
    SET_FRIENDS(state, friends) {
      state.friends = friends
    },
    SET_FRIEND_REQUESTS(state, requests) {
      state.friendRequests = requests
    },    ADD_FRIEND(state, friend) {
      console.log('添加好友到列表:', friend);
      // 检查是否已经存在，避免重复添加
      const exists = state.friends.some(f => f.id === friend.id);
      if (!exists) {
        state.friends.push(friend);
        console.log('好友已添加到列表，当前列表数量:', state.friends.length);
      } else {
        console.log('好友已经存在于列表中，不重复添加');
      }
    },
    REMOVE_FRIEND(state, friendId) {
      state.friends = state.friends.filter(f => f.id !== friendId)
    },
    REMOVE_FRIEND_REQUEST(state, requestId) {
      state.friendRequests = state.friendRequests.filter(r => r.id !== requestId)
    },
    SET_NOTIFICATIONS(state, notifications) {
      state.notifications = notifications
    },
    SET_UNREAD_NOTIFICATION_COUNT(state, count) {
      state.unreadNotificationCount = count
    },
    MARK_NOTIFICATION_READ(state, notificationId) {
      const index = state.notifications.findIndex(n => n.id === notificationId)
      if (index !== -1) {
        state.notifications[index].read = true
        state.unreadNotificationCount = Math.max(0, state.unreadNotificationCount - 1)
      }
    },
    MARK_ALL_NOTIFICATIONS_READ(state) {
      state.notifications.forEach(notification => {
        notification.read = true
      })
      state.unreadNotificationCount = 0
    },
    REMOVE_NOTIFICATION(state, notificationId) {
      const notification = state.notifications.find(n => n.id === notificationId)
      state.notifications = state.notifications.filter(n => n.id !== notificationId)
      if (notification && !notification.read) {
        state.unreadNotificationCount = Math.max(0, state.unreadNotificationCount - 1)
      }
    },
    SET_LOADING(state, loading) {
      state.loading = loading
    },
    SET_ERROR(state, error) {
      state.error = error
    },
    CLEAR_ERROR(state) {
      state.error = null
    },
    ADD_COMMENT_TO_MOOD(state, { moodId, comment }) {
      // 添加评论到指定心情
      if (state.mood && state.mood.id === moodId) {
        if (!state.mood.comments) state.mood.comments = []
        state.mood.comments.push(comment)
      }
      
      // 更新心情列表中的心情
      const moodIndex = state.moods.findIndex(m => m.id === moodId)
      if (moodIndex !== -1) {
        if (!state.moods[moodIndex].comments) state.moods[moodIndex].comments = []
        state.moods[moodIndex].comments.push(comment)
      }
    },
    UPDATE_MOOD_LIKE(state, { moodId, liked }) {
      // 更新单个心情的点赞状态
      if (state.mood && state.mood.id === moodId) {
        state.mood.liked = liked
        state.mood.likeCount = liked 
          ? state.mood.likeCount + 1 
          : Math.max(0, state.mood.likeCount - 1)
      }
      
      // 更新心情列表中的点赞状态
      const moodIndex = state.moods.findIndex(m => m.id === moodId)
      if (moodIndex !== -1) {
        state.moods[moodIndex].liked = liked
        state.moods[moodIndex].likeCount = liked 
          ? state.moods[moodIndex].likeCount + 1 
          : Math.max(0, state.moods[moodIndex].likeCount - 1)
      }
    }  },

  actions: {    // 认证相关
    async login({ commit, dispatch }, { username, password }) {
      commit('SET_LOADING', true)
      commit('CLEAR_ERROR')
      try {
        console.log('Attempting login for user:', username);
        const response = await userApi.login(username, password)
        console.log('Login response:', response);
        
        if (!response.data || !response.data.token) {
          console.error('Login succeeded but no token returned');
          commit('SET_ERROR', '登录成功但未返回有效令牌');
          return null;
        }
        
        const token = response.data.token
        
        // 先解析token确保它有效
        try {
          const decoded = jwtDecode(token)
          console.log('Decoded token:', decoded);
          
          // 只有解析成功才设置token和用户信息
          commit('SET_TOKEN', token)
          
          const user = {
            id: decoded.id,
            username: decoded.sub,
            role: decoded.role
          }
          commit('SET_USER', user)
          
          // 登录成功后获取用户信息
          try {
            await dispatch('fetchUserProfile');
          } catch (profileError) {
            console.warn('Could not fetch user profile after login:', profileError);
            // Continue anyway since login worked
          }
          
          console.log('Login successful for user:', user.username);
          return user
        } catch (tokenError) {
          console.error('Invalid token received:', tokenError);
          commit('SET_ERROR', '收到的令牌无效');
          return null;
        }
      } catch (error) {
        console.error('Login error:', error);
        const errorMessage = error.response?.data?.message || '登录失败，请检查用户名和密码';
        commit('SET_ERROR', errorMessage)
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async register({ commit }, { username, email, password }) {
      commit('SET_LOADING', true)
      commit('CLEAR_ERROR')
      
      try {
        const response = await userApi.register(username, email, password)
        return response.data
      } catch (error) {
        commit('SET_ERROR', error.response?.data?.message || '注册失败，请检查输入信息')
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
      async logout({ commit }) {
      commit('CLEAR_AUTH')
    },
      // 获取用户个人资料
    async fetchUserProfile({ commit }) {
      commit('SET_LOADING', true)
      try {
        const response = await userApi.getProfile()
        const userProfile = response.data
        // 合并基本用户信息和详细资料
        commit('SET_USER', {
          ...userProfile
        })
        return userProfile
      } catch (error) {
        console.error('Error fetching user profile:', error)
        // Don't set error state as this is often called in the background
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    // 获取指定用户信息
    async fetchUserById({ commit }, userId) {
      commit('SET_LOADING', true)
      try {
        const response = await userApi.getUserById(userId)
        return response.data
      } catch (error) {
        console.error('Error fetching user by id:', error)
        commit('SET_ERROR', '获取用户信息失败')
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    // 心情相关
    async fetchMoods({ commit }, { privacyLevel, moodType, location } = {}) {
      commit('SET_LOADING', true)
      commit('CLEAR_ERROR')
      
      try {
        const response = await moodApi.getAllMoods(privacyLevel, moodType, location)
        commit('SET_MOODS', response.data)
        return response.data
      } catch (error) {
        commit('SET_ERROR', error.response?.data?.message || '获取心情列表失败')
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async fetchFeed({ commit }, { page, size } = {}) {
      commit('SET_LOADING', true)
      commit('CLEAR_ERROR')
      
      try {
        const response = await moodApi.getFeed(page, size)
        commit('SET_MOODS', response.data)
        return response.data
      } catch (error) {
        commit('SET_ERROR', error.response?.data?.message || '获取心情动态失败')
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async fetchFriendMoods({ commit }) {
      commit('SET_LOADING', true)
      commit('CLEAR_ERROR')
      
      try {
        const response = await moodApi.getFriendsMoods()
        commit('SET_MOODS', response.data)
        return response.data
      } catch (error) {
        commit('SET_ERROR', error.response?.data?.message || '获取好友心情失败')
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
      async fetchMoodById({ commit }, moodId) {
      commit('SET_LOADING', true)
      commit('CLEAR_ERROR')
      
      try {
        const moodResponse = await moodApi.getMoodById(moodId)
        const mood = moodResponse.data
        
        // 获取评论列表
        try {
          const commentsResponse = await moodApi.getComments(moodId)
          mood.comments = commentsResponse.data
        } catch (commentError) {
          console.error('Error fetching comments:', commentError)
          mood.comments = []
        }
        
        commit('SET_MOOD', mood)
        return mood
      } catch (error) {
        commit('SET_ERROR', error.response?.data?.message || '获取心情详情失败')
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async createMood({ commit }, moodData) {
      commit('SET_LOADING', true)
      commit('CLEAR_ERROR')
      
      try {
        const response = await moodApi.createMood(moodData)
        commit('ADD_MOOD', response.data)
        return response.data
      } catch (error) {
        commit('SET_ERROR', error.response?.data?.message || '创建心情失败')
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async updateMood({ commit }, { moodId, moodData }) {
      commit('SET_LOADING', true)
      commit('CLEAR_ERROR')
      
      try {
        const response = await moodApi.updateMood(moodId, moodData)
        commit('UPDATE_MOOD', response.data)
        return response.data
      } catch (error) {
        commit('SET_ERROR', error.response?.data?.message || '更新心情失败')
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
      async deleteMood({ commit }, moodId) {
      commit('SET_LOADING', true)
      commit('CLEAR_ERROR')
      
      try {
        await moodApi.deleteMood(moodId)
        commit('REMOVE_MOOD', moodId)
        return true
      } catch (error) {
        commit('SET_ERROR', error.response?.data?.message || '删除心情失败')
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async fetchUserMoods({ commit }) {
      commit('SET_LOADING', true)
      commit('CLEAR_ERROR')
      
      try {
        console.log('开始获取用户心情...');
        const response = await moodApi.getUserMoods();
        console.log('用户心情获取成功:', response);
        
        // 正确解析响应数据
        let moodsData = [];
        if (response.data) {
          if (Array.isArray(response.data)) {
            moodsData = response.data;
          } else if (response.data.data && Array.isArray(response.data.data)) {
            moodsData = response.data.data;
          }
        }
        
        console.log('解析后的用户心情:', moodsData, '数量:', moodsData.length);
        return moodsData;
      } catch (error) {
        console.error('获取用户心情失败:', error);
        commit('SET_ERROR', error.response?.data?.message || '获取用户心情失败');
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async toggleLike({ commit }, moodId) {
      commit('CLEAR_ERROR')
      
      try {
        const response = await moodApi.toggleLike(moodId)
        const liked = response.data.liked
        commit('UPDATE_MOOD_LIKE', { moodId, liked })
        return liked
      } catch (error) {
        commit('SET_ERROR', error.response?.data?.message || '点赞操作失败')
        throw error
      }
    },
      async addComment({ commit }, { moodId, content }) {
      commit('CLEAR_ERROR')
      
      try {
        const response = await moodApi.addComment(moodId, content)
        const comment = response.data
        commit('ADD_COMMENT_TO_MOOD', { moodId, comment })
        return comment
      } catch (error) {
        commit('SET_ERROR', error.response?.data?.message || '添加评论失败')
        throw error
      }
    },
    
    async getComments({ commit }, moodId) {
      commit('CLEAR_ERROR')
      
      try {
        const response = await moodApi.getComments(moodId)
        return response.data
      } catch (error) {
        commit('SET_ERROR', error.response?.data?.message || '获取评论失败')
        throw error
      }
    },
    
    async deleteComment({ commit }, { moodId, commentId }) {
      commit('CLEAR_ERROR')
      
      try {
        await moodApi.deleteComment(moodId, commentId)
        // 这里可以添加删除评论的mutation
        return true
      } catch (error) {
        commit('SET_ERROR', error.response?.data?.message || '删除评论失败')
        throw error
      }
    },
    
    async updatePrivacy({ commit }, { moodId, privacyLevel }) {
      commit('SET_LOADING', true)
      commit('CLEAR_ERROR')
      
      try {
        const response = await moodApi.updatePrivacy(moodId, privacyLevel)
        commit('UPDATE_MOOD', response.data)
        return response.data
      } catch (error) {
        commit('SET_ERROR', error.response?.data?.message || '更新隐私设置失败')
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    // 好友相关
    async fetchFriends({ commit }) {
      commit('SET_LOADING', true)
      commit('CLEAR_ERROR')
      
      try {
        console.log('开始获取好友列表...');
        const response = await friendApi.getFriends();
        console.log('好友列表获取成功:', response);
        
        // 正确解析响应数据
        let friendsData = [];
        if (response.data) {
          if (Array.isArray(response.data)) {
            friendsData = response.data;
          } else if (response.data.data && Array.isArray(response.data.data)) {
            friendsData = response.data.data;
          }
        }
        
        console.log('解析后的好友列表:', friendsData, '数量:', friendsData.length);
        commit('SET_FRIENDS', friendsData);
        return friendsData;
      } catch (error) {
        console.error('获取好友列表失败:', error);
        commit('SET_ERROR', error.response?.data?.message || '获取好友列表失败');
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
      async fetchFriendRequests({ commit }) {
      commit('SET_LOADING', true)
      commit('CLEAR_ERROR')
      
      try {
        console.log('开始获取好友请求列表...');
        const response = await friendApi.getFriendRequests();
        console.log('好友请求列表获取成功:', response.data);
        
        let requestsData = [];
        if (response.data) {
          if (Array.isArray(response.data)) {
            requestsData = response.data;
          } else if (response.data.data && Array.isArray(response.data.data)) {
            requestsData = response.data.data;
          }
        }
        
        commit('SET_FRIEND_REQUESTS', requestsData);
        return requestsData;
      } catch (error) {
        console.error('获取好友请求列表失败:', error);
        commit('SET_ERROR', error.response?.data?.message || '获取好友请求失败');
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },async searchUserByUsername({ commit }, username) {
      commit('SET_LOADING', true)
      commit('CLEAR_ERROR')
      
      try {
        console.log('搜索用户名:', username);
        const response = await userApi.searchByUsername(username)
        // The response will be a list of users matching the username
        const users = response.data
        console.log('搜索结果:', users);
        
        if (!users || users.length === 0) {
          console.error('找不到用户:', username);
          throw new Error(`找不到用户 "${username}"`);
        }
        
        // Find exact match first
        const exactMatch = users.find(u => u.username.toLowerCase() === username.toLowerCase());
        if (exactMatch) {
          console.log('找到精确匹配:', exactMatch.username);
          return exactMatch;
        }
        
        // If no exact match, use the first one
        console.log('未找到精确匹配，使用第一个结果:', users[0].username);
        return users[0];
      } catch (error) {
        console.error('搜索用户错误:', error);
        commit('SET_ERROR', error.response?.data?.message || '查找用户失败');
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },      async sendFriendRequest({ commit, dispatch }, username) {
      commit('SET_LOADING', true)
      commit('CLEAR_ERROR')
      
      try {
        console.log('准备查找用户:', username);
        
        // 不管输入的是什么，始终视为用户名进行搜索
        // 很多用户可能会用数字用户名，如 "123456"
        let userId;
        try {
          const user = await dispatch('searchUserByUsername', username);
          console.log('搜索结果:', user);
          if (user && user.id) {
            userId = user.id;
            console.log('找到用户ID:', userId);
          } else {
            throw new Error('找不到该用户');
          }
        } catch (searchError) {
          console.error('Error searching for user:', searchError);
          throw new Error(`无法找到用户 "${username}"，请确认用户名正确`);        }
        
        // 确保userId是数字
        if (typeof userId !== 'number' || isNaN(userId) || userId <= 0) {
          console.error('无效的用户ID:', userId);
          throw new Error('获取到的用户ID无效');
        }
        
        console.log('准备发送好友请求到用户ID:', userId);
        const response = await friendApi.sendFriendRequest(userId);
        console.log('好友请求响应:', response.data);
        return response.data;
      } catch (error) {
        console.error('好友请求错误:', error);
        commit('SET_ERROR', error.response?.data?.message || '发送好友请求失败');
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },    async acceptFriendRequest({ commit, dispatch }, requestId) {
      commit('SET_LOADING', true)
      commit('CLEAR_ERROR')
      
      try {
        console.log('开始接受好友请求, 用户ID:', requestId);
        const response = await friendApi.acceptFriendRequest(requestId);
        console.log('接受好友请求响应:', response);
        
        // 从好友请求列表中移除
        commit('REMOVE_FRIEND_REQUEST', requestId);
        
        // 强制延迟后刷新好友列表，确保后端有时间处理
        setTimeout(async () => {
          console.log('延迟刷新好友列表...');
          await dispatch('fetchFriends');
          console.log('好友列表刷新完成');
        }, 500);
        
        // 如果后端返回了新好友信息，直接添加到好友列表
        if (response.data && response.data.data) {
          console.log('后端返回的新好友信息:', response.data.data);
          commit('ADD_FRIEND', response.data.data);
        }
        
        console.log('好友请求已接受, 好友关系已建立');
        return response.data;
      } catch (error) {
        console.error('接受好友请求失败:', error);
        commit('SET_ERROR', error.response?.data?.message || '接受好友请求失败');
        throw error;
      } finally {
        commit('SET_LOADING', false);
      }
    },
    
    async rejectFriendRequest({ commit }, requestId) {
      commit('SET_LOADING', true)
      commit('CLEAR_ERROR')
      
      try {
        await friendApi.rejectFriendRequest(requestId)
        commit('REMOVE_FRIEND_REQUEST', requestId)
        return true
      } catch (error) {
        commit('SET_ERROR', error.response?.data?.message || '拒绝好友请求失败')
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
    
    async removeFriend({ commit }, friendId) {
      commit('SET_LOADING', true)
      commit('CLEAR_ERROR')
      
      try {
        await friendApi.removeFriend(friendId)
        commit('REMOVE_FRIEND', friendId)
        return true
      } catch (error) {
        commit('SET_ERROR', error.response?.data?.message || '删除好友失败')
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
      // 通知相关
    async fetchNotifications({ commit }) {
      commit('SET_LOADING', true)
      commit('CLEAR_ERROR')
      
      try {
        const response = await notificationApi.getAllNotifications()
        // 确保即使数据格式不符也能处理
        let notificationsData = [];
        if (response.data) {
          if (Array.isArray(response.data)) {
            notificationsData = response.data;
          } else if (response.data.data && Array.isArray(response.data.data)) {
            notificationsData = response.data.data;
          } else if (typeof response.data === 'object') {
            notificationsData = [response.data];
          }
        }
        commit('SET_NOTIFICATIONS', notificationsData)
        return notificationsData
      } catch (error) {
        commit('SET_ERROR', error.response?.data?.message || '获取通知失败')
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
      async getUnreadNotificationCount({ commit }) {
      try {
        const response = await notificationApi.getUnreadCount()
        
        // 处理不同格式的响应
        let count = 0;
        if (response.data) {
          if (typeof response.data === 'number') {
            count = response.data;
          } else if (response.data.count !== undefined) {
            count = response.data.count;
          } else if (response.data.data !== undefined && typeof response.data.data === 'number') {
            count = response.data.data;
          }
        }
        
        commit('SET_UNREAD_NOTIFICATION_COUNT', count)
        return count
      } catch (error) {
        console.error('获取未读通知数量失败', error)
        // Don't set error state as this is often called in the background
        return 0
      }
    },
    
    async markNotificationAsRead({ commit }, notificationId) {
      try {
        await notificationApi.markAsRead(notificationId)
        commit('MARK_NOTIFICATION_READ', notificationId)
      } catch (error) {
        console.error('标记通知已读失败', error)
      }
    },
    
    async markAllNotificationsAsRead({ commit }) {
      try {
        await notificationApi.markAllAsRead()
        commit('MARK_ALL_NOTIFICATIONS_READ')
      } catch (error) {
        console.error('标记所有通知已读失败', error)
      }
    },
    
    async deleteNotification({ commit }, notificationId) {
      try {
        await notificationApi.deleteNotification(notificationId)
        commit('REMOVE_NOTIFICATION', notificationId)
      } catch (error) {
        console.error('删除通知失败', error)
      }
    },
    
    // 验证当前存储的令牌是否有效
    async checkAuth({ commit, dispatch }) {
      const token = localStorage.getItem('token')
      if (!token) {
        commit('CLEAR_AUTH')
        return false
      }
      
      try {
        // 验证token是否有效
        const response = await userApi.checkStatus()
        
        if (response.data) {
          // Token有效，设置用户信息
          const decoded = jwtDecode(token)
          const user = {
            id: decoded.id,
            username: decoded.sub,
            role: decoded.role
          }
          commit('SET_USER', user)
          
          // 尝试获取完整的用户资料
          try {
            await dispatch('fetchUserProfile')
          } catch (profileError) {
            console.warn('Could not fetch user profile during auth check:', profileError)
          }
          
          return true
        } else {
          // Token无效
          commit('CLEAR_AUTH')
          return false
        }
      } catch (error) {
        console.error('Auth check failed:', error)
        commit('CLEAR_AUTH')
        return false
      }
    },
    
    // 更新用户个人资料
    async updateProfile({ commit, state }, { bio, profilePicture }) {
      commit('SET_LOADING', true)
      commit('CLEAR_ERROR')
      
      try {
        // 调用API更新个人资料
        await userApi.updateProfile(bio, profilePicture)
        
        // 更新本地用户数据
        const updatedUser = {
          ...state.user,
          bio,
          profilePicture
        }
        commit('SET_USER', updatedUser)
        
        console.log('个人资料更新成功', updatedUser)
        return updatedUser
      } catch (error) {
        console.error('更新个人资料失败:', error)
        commit('SET_ERROR', error.response?.data?.message || '更新个人资料失败')
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },
  },
  modules: {
  }
})
