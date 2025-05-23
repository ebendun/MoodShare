import axios from 'axios';

// 创建axios实例
const api = axios.create({
  baseURL: '',  // Don't use /api since we're already proxying directly
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
  login: (username, password) => api.post('/auth/login', { username, password }),
  register: (username, email, password) => api.post('/auth/register', { username, email, password }),
  getProfile: () => api.get('/auth/me'),
  updateProfile: (bio, profilePicture) => api.put('/auth/profile', { bio, profilePicture }),
  checkStatus: () => api.get('/auth/status'),
  searchByUsername: (username) => api.get(`/users/search?username=${encodeURIComponent(username)}`)
};

// 心情相关API
export const moodApi = {
  getAllMoods: (privacyLevel, moodType, location) => {
    const params = {};
    if (privacyLevel) params.privacyLevel = privacyLevel;
    if (moodType) params.moodType = moodType;
    if (location) params.location = location;
    return api.get('/moods', { params });
  },
  getMoodById: (id) => api.get(`/moods/${id}`),
  createMood: (moodData) => api.post('/moods', moodData),
  updateMood: (id, moodData) => api.put(`/moods/${id}`, moodData),
  deleteMood: (id) => api.delete(`/moods/${id}`),
  toggleLike: (id) => api.post(`/moods/${id}/like`),
  getFeed: (page = 0, size = 20) => api.get(`/moods/feed?page=${page}&size=${size}`),
  getFriendsMoods: () => api.get('/moods/friends'),
  getNearbyMoods: (latitude, longitude, radiusKm = 10) => 
    api.get(`/moods/nearby?latitude=${latitude}&longitude=${longitude}&radiusKm=${radiusKm}`),
  addComment: (moodId, content) => api.post(`/moods/${moodId}/comments`, { content }),
  getComments: (moodId) => api.get(`/moods/${moodId}/comments`),
  deleteComment: (moodId, commentId) => api.delete(`/moods/${moodId}/comments/${commentId}`),
  updatePrivacy: (moodId, privacyLevel) => api.put(`/moods/${moodId}/privacy?privacyLevel=${privacyLevel}`)
};

// 好友相关API
export const friendApi = {
  getFriends: () => api.get('/friends'),
  getFriendRequests: () => api.get('/friends/requests'),
  sendFriendRequest: (userId) => api.post(`/friends/request/${userId}`),
  acceptFriendRequest: (userId) => api.post(`/friends/accept/${userId}`),
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
