import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-icons/font/bootstrap-icons.css'

// Import Bootstrap JavaScript and attach to window
import * as bootstrap from 'bootstrap/dist/js/bootstrap.bundle.min.js'
window.bootstrap = bootstrap

console.log('🚀 Main.js - Bootstrap loaded:', !!window.bootstrap)
console.log('🚀 Main.js - Bootstrap Dropdown available:', !!window.bootstrap.Dropdown)
console.log('🚀 Main.js - Bootstrap version:', window.bootstrap?.Tooltip?.VERSION)

// Create Vue app
const app = createApp(App)
  .use(store)
  .use(router)

// Mount the app after a short delay to ensure Bootstrap is fully ready
setTimeout(() => {
  app.mount('#app')
  console.log('🚀 Main.js - Vue app mounted')
}, 100)
