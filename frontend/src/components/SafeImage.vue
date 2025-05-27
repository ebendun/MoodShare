<template>
  <img 
    :src="currentSrc" 
    :alt="alt" 
    :class="imageClass" 
    :width="width" 
    :height="height"
    @error="handleImageError" 
  />
</template>

<script>
import { ref, watch } from 'vue'
// 使用项目中已有的logo作为默认图片
import defaultAvatar from '../assets/logo.png'

export default {
  name: 'SafeImage',
  props: {
    src: {
      type: String,
      default: ''
    },
    alt: {
      type: String,
      default: 'Image'
    },
    imageClass: {
      type: String,
      default: ''
    },
    width: {
      type: [String, Number],
      default: null
    },
    height: {
      type: [String, Number],
      default: null
    }  },  setup(props) {
    const currentSrc = ref(props.src || defaultAvatar)
      // 修正图片URL格式
    const getCorrectImageUrl = (src) => {
      if (!src) return defaultAvatar
      
      // 如果是完整的URL（包含http或https），直接返回
      if (src.startsWith('http://') || src.startsWith('https://')) {
        return src
      }
      
      // 如果已经包含 /uploads 前缀，直接返回
      if (src.startsWith('/uploads/')) {
        return src
      }
      
      // 如果是文件名（可能缺少 /uploads 前缀），添加前缀
      if (src.startsWith('/') && !src.startsWith('/uploads/')) {
        return '/uploads' + src
      }
      
      // 如果只是文件名，添加完整前缀
      return '/uploads/' + src
    };
    
    // 当源图片URL改变时更新当前显示的图片
    watch(() => props.src, (newSrc) => {
      const correctedUrl = getCorrectImageUrl(newSrc)
      currentSrc.value = correctedUrl
      console.log('SafeImage: 原始URL =', newSrc, '修正后URL =', correctedUrl)
    }, { immediate: true });
    
    const handleImageError = () => {
      console.error('SafeImage: 图片加载失败！')
      console.error('SafeImage: 原始路径:', props.src)
      console.error('SafeImage: 当前尝试的路径:', currentSrc.value)
      console.error('SafeImage: 完整URL:', window.location.origin + currentSrc.value)
      console.error('SafeImage: 使用默认图片')
      // 当图片加载失败时使用默认图片
      currentSrc.value = defaultAvatar
    }

    return {
      currentSrc,
      handleImageError
    }
  }
}
</script>