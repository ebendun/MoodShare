// src/api.js
import axios from 'axios';

const api = axios.create({
    baseURL: process.env.NODE_ENV === 'production'
        ? '/api'
        : '/api', // 通过代理访问后端
    timeout: 10000,
    withCredentials: true // 允许携带跨域 cookie
});

// 请求拦截器 - 添加 token
api.interceptors.request.use(config => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
}, error => {
    return Promise.reject(error);
});
export default {
    // 用户认证
    register(userData) {
        return api.post('/auth/register', userData)
    },
    login(credentials) {
        return api.post('/auth/login', credentials)
    },
    checkStatus() {
        return api.get('/auth/status')
    },

    // 心情相关接口
    getAllMoods() {
        return api.get('/moods')
    },
    getMoodById(id) {
        return api.get(`/moods/${id}`)
    },
    createMood(moodData) {
        return api.post('/moods', moodData)
    },
    toggleLike(id) {
        return api.post(`/moods/${id}/like`)
    },
    deleteMood(id) {
        return api.delete(`/moods/${id}`)
    },

    // 评论相关接口
    getComments(moodId) {
        return api.get(`/moods/${moodId}/comments`)
    },
    addComment(moodId, commentData) {
        return api.post(`/moods/${moodId}/comments`, commentData)
    },
    deleteComment(moodId, commentId) {
        return api.delete(`/moods/${moodId}/comments/${commentId}`)
    }
}