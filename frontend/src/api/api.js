import axios from 'axios';

// 创建axios实例
const api = axios.create({
  baseURL: '',
  timeout: 10000
});

// 请求拦截器，添加JWT到请求头
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => Promise.reject(error)
);

// 响应拦截器处理常见错误
api.interceptors.response.use(
  response => response,
  error => {
    console.error('API error:', error.response?.status, error.response?.data);
    
    if (error.response && error.response.status === 401) {
      // 未授权，清除token并重定向到登录页
      console.log('Unauthorized access detected, clearing token and redirecting to login');
      localStorage.removeItem('token');
      
      // 只有在当前不在登录页的情况下才重定向
      if (!window.location.pathname.includes('/login')) {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

// 用户相关API
export const userApi = {
  login: (username, password, captchaCode, sessionId) => api.post('/auth/login', { username, password, captchaCode, sessionId }),//登录
  register: (username, email, password, captchaCode, sessionId) => api.post('/auth/register', { username, email, password, captchaCode, sessionId }),//注册
  getProfile: () => api.get('/auth/me'),//获取用户信息
  getUserById: (userId) => api.get(`/auth/users/${userId}`),//获取指定用户信息
  updateProfile: (bio, profilePicture) => api.put('/auth/profile', { bio, profilePicture }),//更新用户信息
  changePassword: (oldPassword, newPassword) => api.put('/auth/change-password', { oldPassword, newPassword }),//修改密码
  checkStatus: () => api.get('/auth/status'),//检查用户状态
  searchByUsername: (username) => api.get(`/auth/users/search?username=${encodeURIComponent(username)}`)
};

// 验证码相关API
export const captchaApi = {
  getCaptcha: () => api.get('/auth/captcha'),//获取验证码
  verifyCaptcha: (sessionId, captchaCode) => api.post('/auth/captcha/verify', { sessionId, captchaCode })//验证验证码
};

// 文件上传API
export const fileApi = {
  uploadAvatar: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/files/upload/avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  },  uploadMoodImage: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/files/upload/mood', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  },
  uploadCommentImage: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/files/upload/comment', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  }
};

// 心情相关API
export const moodApi = {
  getAllMoods: (privacyLevel, moodType, location) => {
    const params = {};
    if (privacyLevel) params.privacyLevel = privacyLevel;
    if (moodType) params.moodType = moodType;
    if (location) params.location = location;
    return api.get('/moods', { params });
  },//获取心情列表
  getMoodById: (id) => api.get(`/moods/${id}`),  getUserMoods: () => {
    console.log('API 调用: getUserMoods');
    return api.get('/moods/user')
      .then(response => {
        console.log('获取用户心情成功:', response);
        return response;
      })
      .catch(error => {
        console.error('获取用户心情API错误:', error.response?.data);
        throw error;
      });
  },
  getUserMoodsByUserId: (userId) => {
    console.log('API 调用: getUserMoodsByUserId, userId:', userId);
    return api.get(`/moods/user/${userId}`)
      .then(response => {
        console.log('获取指定用户心情成功:', response);
        return response;
      })
      .catch(error => {
        console.error('获取指定用户心情API错误:', error.response?.data);
        throw error;
      });
  },
  createMood: (moodData) => api.post('/moods', moodData),//创建心情
  updateMood: (id, moodData) => api.put(`/moods/${id}`, moodData),
  deleteMood: (id) => api.delete(`/moods/${id}`),
  toggleLike: (id) => api.post(`/moods/${id}/like`),
  getFeed: (page = 0, size = 20) => api.get(`/moods/feed?page=${page}&size=${size}`),//获取心情动态
  getFriendsMoods: () => api.get('/moods/friends'),
  getNearbyMoods: (latitude, longitude, radiusKm = 10) => 
    api.get(`/moods/nearby?latitude=${latitude}&longitude=${longitude}&radiusKm=${radiusKm}`),
  addComment: (moodId, content, imageUrl = null) => {
    const commentData = { content };
    if (imageUrl) {
      commentData.imageUrl = imageUrl;
    }
    return api.post(`/moods/${moodId}/comments`, commentData);
  },
  getComments: (moodId) => api.get(`/moods/${moodId}/comments`),
  deleteComment: (moodId, commentId) => api.delete(`/moods/${moodId}/comments/${commentId}`),
  updatePrivacy: (moodId, privacyLevel) => api.put(`/moods/${moodId}/privacy?privacyLevel=${privacyLevel}`)
};

// 好友相关API
export const friendApi = {
  getFriends: () => {
    console.log('API 调用: getFriends');
    return api.get('/friends')
      .then(response => {
        console.log('获取好友列表成功:', response);
        return response;
      })
      .catch(error => {
        console.error('获取好友列表API错误:', error.response?.data);
        throw error;
      });
  },
  getFriendRequests: () => api.get('/friends/requests'),
  sendFriendRequest: (userId) => {
    console.log('API 调用: sendFriendRequest, userId:', userId, 'type:', typeof userId);
    // 确保传入的是有效的用户ID（数字类型）
    if (typeof userId !== 'number' || isNaN(userId) || userId <= 0) {
      console.error('无效的用户ID:', userId);
      return Promise.reject(new Error('无效的用户ID'));
    }
    return api.post(`/friends/request/${userId}`);
  },  acceptFriendRequest: (userId) => {
    console.log('API 调用: acceptFriendRequest, userId:', userId);
    return api.post(`/friends/accept/${userId}`)
      .then(response => {
        console.log('接受好友请求成功:', response);
        
        // 解析返回的数据，确保处理不同的返回格式
        if (response.data && response.data.data) {
          console.log('好友信息已返回:', response.data.data);
        } else if (response.data && response.data.success) {
          console.log('请求成功，但无详细好友信息');
        }
        
        return response;
      })
      .catch(error => {
        console.error('接受好友请求API错误:', error.response?.data);
        throw error;
      });
  },
  rejectFriendRequest: (userId) => api.post(`/friends/reject/${userId}`),
  removeFriend: (friendId) => api.delete(`/friends/${friendId}`)
};

// 通知相关API
export const notificationApi = {
  getAllNotifications: () => api.get('/notifications'),
  getUnreadNotifications: () => api.get('/notifications/unread'),
  getUnreadCount: () => api.get('/notifications/unread/count'),
  markAsRead: (notificationId) => api.put(`/notifications/${notificationId}/read`),
  markAllAsRead: () => api.put('/notifications/read-all'),
  deleteNotification: (notificationId) => api.delete(`/notifications/${notificationId}`)
};

export default api;
