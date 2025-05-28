<template>
  <div class="mood-edit-view">
    <h2 class="mb-4">编辑心情</h2>
    
    <!-- 加载中 -->
    <div v-if="isLoading && !mood.content" class="text-center my-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-2">加载中...</p>
    </div>
    
    <!-- 编辑表单 -->
    <div v-else class="card">
      <div class="card-body">
        <form @submit.prevent="handleSubmit">
          <!-- 心情内容 -->
          <div class="mb-3">
            <label for="content" class="form-label">内容</label>
            <textarea 
              class="form-control" 
              id="content" 
              v-model="mood.content" 
              rows="4" 
              required
              placeholder="分享你的心情..."
            ></textarea>
          </div>
          
          <!-- 心情类型 -->
          <div class="mb-3">
            <label class="form-label">心情类型</label>
            <div class="d-flex flex-wrap gap-2">
              <button 
                v-for="type in moodTypes" 
                :key="type.value" 
                type="button" 
                class="btn" 
                :class="mood.moodType === type.value ? 'btn-primary' : 'btn-outline-primary'"
                @click="mood.moodType = type.value"
              >
                {{ type.emoji }} {{ type.label }}
              </button>
            </div>
          </div>
          
          <!-- 表情选择 -->
          <div class="mb-3">
            <label for="emoji" class="form-label">选择表情</label>
            <div class="input-group">
              <span class="input-group-text">{{ mood.emoji || '选择表情' }}</span>
              <select class="form-select" id="emoji" v-model="mood.emoji">
                <option value="">无表情</option>
                <option value="😊">😊 微笑</option>
                <option value="😂">😂 笑哭</option>
                <option value="😍">😍 爱心眼</option>
                <option value="🤔">🤔 思考</option>
                <option value="😢">😢 难过</option>
                <option value="😡">😡 生气</option>
                <option value="🥳">🥳 庆祝</option>
                <option value="😴">😴 困倦</option>
                <option value="😎">😎 酷</option>
                <option value="🤗">🤗 拥抱</option>
              </select>
            </div>          </div>
          
          <!-- 图片上传区域 -->
          <div class="mb-3">
            <label class="form-label">心情图片</label>
            
            <!-- 当前图片显示 -->
            <div v-if="mood.imageUrl" class="mb-2">
              <p class="text-muted small">当前图片：</p>
              <SafeImage 
                :src="mood.imageUrl" 
                alt="当前心情图片" 
                imageClass="img-thumbnail" 
                style="max-width: 200px; max-height: 150px;"
              />
              <button 
                type="button" 
                @click="removeCurrentImage" 
                class="btn btn-sm btn-outline-danger ms-2"
              >
                移除当前图片
              </button>
            </div>
            
            <div class="d-flex align-items-center gap-3 mb-2">
              <label for="moodEditImage" class="btn btn-outline-secondary btn-sm">
                <i class="bi bi-image"></i> {{ mood.imageUrl ? '更换图片' : '添加图片' }}
              </label>
              <input 
                type="file" 
                id="moodEditImage" 
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
                取消选择
              </button>
            </div>
            
            <!-- 新图片预览 -->
            <div v-if="imagePreview" class="mt-2">
              <p class="text-muted small">新图片预览：</p>
              <img :src="imagePreview" alt="预览" class="img-thumbnail" style="max-width: 200px; max-height: 150px;">
            </div>
          </div>
          
          <!-- 标签 -->
          <div class="mb-3">
            <label for="tag" class="form-label">标签</label>
            <div class="input-group">
              <input 
                type="text" 
                class="form-control" 
                id="tag" 
                v-model="tagInput" 
                placeholder="输入标签后按回车添加"
                @keydown.enter.prevent="addTag"
              >
              <button 
                type="button" 
                class="btn btn-outline-secondary" 
                @click="addTag"
              >
                添加
              </button>
            </div>
            
            <div class="mt-2">
              <span 
                v-for="tag in mood.tags" 
                :key="tag" 
                class="badge bg-primary me-2 mb-2 p-2"
              >
                #{{ tag }}
                <button 
                  type="button" 
                  class="btn-close btn-close-white ms-2" 
                  style="font-size: 0.6rem;" 
                  @click="removeTag(tag)"
                ></button>
              </span>
            </div>
          </div>
          
          <!-- 位置信息 -->
          <div class="mb-3">
            <label for="location" class="form-label">位置</label>
            <div class="input-group">
              <span class="input-group-text">
                <i class="bi bi-geo-alt"></i>
              </span>              <input 
                type="text" 
                class="form-control" 
                id="location" 
                v-model="mood.location" 
                placeholder="添加位置信息"
                autocomplete="address-level2"
              >
              <button 
                type="button" 
                class="btn btn-outline-secondary" 
                @click="getCurrentLocation"
              >
                <i class="bi bi-cursor"></i> 更新位置
              </button>
            </div>
          </div>
          
          <!-- 天气信息 -->
          <div class="mb-3">
            <label for="weather" class="form-label">天气</label>
            <select class="form-select" id="weather" v-model="mood.weather">
              <option value="">未知</option>
              <option value="晴朗">☀️ 晴朗</option>
              <option value="多云">⛅ 多云</option>
              <option value="阴天">☁️ 阴天</option>
              <option value="小雨">🌦️ 小雨</option>
              <option value="大雨">🌧️ 大雨</option>
              <option value="雷雨">⛈️ 雷雨</option>
              <option value="雪">❄️ 雪</option>
              <option value="雾">🌫️ 雾</option>
            </select>
          </div>
          
          <!-- 隐私设置 -->
          <div class="mb-3">
            <label class="form-label">隐私设置</label>
            <div class="form-check">
              <input 
                class="form-check-input" 
                type="radio" 
                name="privacyLevel" 
                id="privacyPublic" 
                value="PUBLIC" 
                v-model="mood.privacyLevel"
              >
              <label class="form-check-label" for="privacyPublic">
                <i class="bi bi-globe"></i> 公开 - 所有人可见
              </label>
            </div>
            <div class="form-check">
              <input 
                class="form-check-input" 
                type="radio" 
                name="privacyLevel" 
                id="privacyFriends" 
                value="FRIENDS" 
                v-model="mood.privacyLevel"
              >
              <label class="form-check-label" for="privacyFriends">
                <i class="bi bi-people"></i> 好友可见 - 仅好友可见
              </label>
            </div>
            <div class="form-check">
              <input 
                class="form-check-input" 
                type="radio" 
                name="privacyLevel" 
                id="privacyPrivate" 
                value="PRIVATE" 
                v-model="mood.privacyLevel"
              >
              <label class="form-check-label" for="privacyPrivate">
                <i class="bi bi-lock"></i> 仅自己可见
              </label>
            </div>
          </div>
          
          <!-- 操作按钮 -->
          <div class="d-flex justify-content-between">
            <router-link :to="`/moods/${id}`" class="btn btn-outline-secondary">
              取消
            </router-link>
            
            <button 
              type="submit" 
              class="btn btn-primary px-4" 
              :disabled="isSubmitting || !mood.content"
            >
              <span v-if="isSubmitting" class="spinner-border spinner-border-sm me-1"></span>
              保存修改
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { reactive, ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { fileApi } from '@/api/api'
import SafeImage from '@/components/SafeImage.vue'

export default {
  name: 'MoodEditView',
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
    
    const isLoading = computed(() => store.state.loading)
    const isSubmitting = ref(false)
    
    // 心情数据
    const mood = reactive({
      content: '',
      emoji: '',
      tags: [],
      location: '',
      latitude: null,
      longitude: null,
      moodType: 'NEUTRAL',
      weather: '',
      privacyLevel: 'PUBLIC'
    })
      // 标签输入
    const tagInput = ref('')
    
    // 图片上传相关状态
    const selectedImage = ref(null)
    const imagePreview = ref('')
    const isUploading = ref(false)
    
    // 心情类型选项
    const moodTypes = [
      { value: 'HAPPY', label: '开心', emoji: '😊' },
      { value: 'SAD', label: '悲伤', emoji: '😢' },
      { value: 'ANGRY', label: '生气', emoji: '😡' },
      { value: 'EXCITED', label: '兴奋', emoji: '🤩' },
      { value: 'WORRIED', label: '担忧', emoji: '😰' },
      { value: 'NEUTRAL', label: '平静', emoji: '😐' }
    ]
    
    // 加载心情数据
    onMounted(async () => {
      try {
        const response = await store.dispatch('fetchMoodById', props.id)
          // 将加载的数据填充到表单中
        if (response) {
          Object.assign(mood, {
            content: response.content || '',
            emoji: response.emoji || '',
            imageUrl: response.imageUrl || '', // 加载心情图片
            tags: response.tags || [],
            location: response.location || '',
            latitude: response.latitude || null,
            longitude: response.longitude || null,
            moodType: response.moodType || 'NEUTRAL',
            weather: response.weather || '',
            privacyLevel: response.privacyLevel || 'PUBLIC'
          })
        }
      } catch (error) {
        console.error('Error loading mood:', error)
        router.push('/')
      }
    })
    
    // 添加标签
    const addTag = () => {
      const tag = tagInput.value.trim()
      if (tag && !mood.tags.includes(tag)) {
        mood.tags.push(tag)
      }
      tagInput.value = ''
    }
    
    // 移除标签
    const removeTag = (tag) => {
      const index = mood.tags.indexOf(tag)
      if (index > -1) {
        mood.tags.splice(index, 1)
      }
    }
    
    // 获取当前位置
    const getCurrentLocation = () => {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          (position) => {
            mood.latitude = position.coords.latitude
            mood.longitude = position.coords.longitude
            
            // 尝试获取位置名称
            fetch(`https://nominatim.openstreetmap.org/reverse?lat=${mood.latitude}&lon=${mood.longitude}&format=json`)
              .then(response => response.json())
              .then(data => {
                const address = data.address
                if (address) {
                  const location = []
                  if (address.city) location.push(address.city)
                  else if (address.town) location.push(address.town)
                  
                  if (address.state) location.push(address.state)
                  if (address.country) location.push(address.country)
                  
                  mood.location = location.join(', ')
                }
              })
              .catch(error => {
                console.error('Error getting location name:', error)
                mood.location = `位置 (${mood.latitude.toFixed(4)}, ${mood.longitude.toFixed(4)})`
              })
          },
          (error) => {
            console.error('Error getting location:', error)
            alert('无法获取当前位置。请确保已授予位置权限。')
          }
        )
      } else {
        alert('您的浏览器不支持地理定位功能。')
      }
    }
      // 提交表单
    const handleSubmit = async () => {
      isSubmitting.value = true
      
      try {
        let imageUrl = mood.imageUrl // 保持原有图片
        
        // 如果有选择新图片，先上传图片
        if (selectedImage.value) {
          isUploading.value = true
          const uploadResponse = await fileApi.uploadMoodImage(selectedImage.value)
          if (uploadResponse.data && uploadResponse.data.success) {
            imageUrl = uploadResponse.data.url
          }
        }
        
        // 准备更新数据
        const moodData = { ...mood }
        moodData.imageUrl = imageUrl
        
        await store.dispatch('updateMood', {
          moodId: props.id,
          moodData: moodData
        })
        router.push(`/moods/${props.id}`)
      } catch (error) {
        console.error('Error updating mood:', error)
        alert('更新失败：' + (error.response?.data?.message || error.message))
      } finally {
        isSubmitting.value = false
        isUploading.value = false
      }
    }
    
    // 处理图片选择
    const handleImageSelect = (event) => {
      const file = event.target.files[0]
      if (file) {
        // 验证文件类型
        if (!file.type.startsWith('image/')) {
          alert('请选择图片文件')
          return
        }
        
        // 验证文件大小 (5MB)
        if (file.size > 5 * 1024 * 1024) {
          alert('图片大小不能超过5MB')
          return
        }
        
        selectedImage.value = file
        
        // 创建预览
        const reader = new FileReader()
        reader.onload = (e) => {
          imagePreview.value = e.target.result
        }
        reader.readAsDataURL(file)
      }
    }
    
    // 移除选择的新图片
    const removeSelectedImage = () => {
      selectedImage.value = null
      imagePreview.value = ''
      // 清空file input
      const fileInput = document.getElementById('moodEditImage')
      if (fileInput) {
        fileInput.value = ''
      }
    }
      // 移除当前图片
    const removeCurrentImage = () => {
      mood.imageUrl = ''
    }
    
    return {
      mood,
      tagInput,
      moodTypes,
      isLoading,
      isSubmitting,
      selectedImage,
      imagePreview,
      isUploading,
      addTag,
      removeTag,
      getCurrentLocation,
      handleSubmit,
      handleImageSelect,
      removeSelectedImage,
      removeCurrentImage
    }
  }
}
</script>

<style scoped>
.mood-edit-view {
  max-width: 800px;
  margin: 0 auto;
}
</style>
