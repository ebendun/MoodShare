<template>
  <div class="about-page">
    <!-- Hero Section -->
    <div class="hero-section">
      <div class="container">
        <div class="row align-items-center min-vh-50">
          <div class="col-lg-6">
            <h1 class="display-4 fw-bold text-primary mb-4">
              <i class="bi bi-heart-fill me-3"></i>MoodShare
            </h1>
            <p class="lead text-muted mb-4">
              一个温暖的心情分享社区，让每一份情感都能被理解和陪伴
            </p>
            <div class="d-flex gap-3">
              <router-link v-if="!isLoggedIn" to="/register" class="btn btn-primary btn-lg">
                <i class="bi bi-person-plus me-2"></i>加入我们
              </router-link>
              <router-link v-else to="/" class="btn btn-primary btn-lg">
                <i class="bi bi-house me-2"></i>回到首页
              </router-link>
              <a href="#features" class="btn btn-outline-primary btn-lg">
                <i class="bi bi-info-circle me-2"></i>了解更多
              </a>
            </div>
          </div>
          <div class="col-lg-6 text-center">
            <div class="feature-icon-large">
              <i class="bi bi-chat-heart display-1 text-primary"></i>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Features Section -->
    <div id="features" class="features-section py-5 bg-light">
      <div class="container">
        <div class="text-center mb-5">
          <h2 class="fw-bold mb-3">为什么选择 MoodShare？</h2>
          <p class="text-muted">我们相信每一种情感都值得被分享和理解</p>
        </div>
        
        <div class="row g-4">
          <div class="col-md-4">
            <div class="feature-card h-100">
              <div class="card-body text-center p-4">
                <div class="feature-icon mb-3">
                  <i class="bi bi-shield-check display-4 text-success"></i>
                </div>
                <h5 class="card-title">安全私密</h5>
                <p class="card-text text-muted">
                  您的心情动态只对好友可见，我们保护您的隐私，让您可以安心分享内心世界
                </p>
              </div>
            </div>
          </div>
          
          <div class="col-md-4">
            <div class="feature-card h-100">
              <div class="card-body text-center p-4">
                <div class="feature-icon mb-3">
                  <i class="bi bi-people-fill display-4 text-info"></i>
                </div>
                <h5 class="card-title">温暖社区</h5>
                <p class="card-text text-muted">
                  在这里，您可以找到志同道合的朋友，分享喜怒哀乐，获得理解和支持
                </p>
              </div>
            </div>
          </div>
          
          <div class="col-md-4">
            <div class="feature-card h-100">
              <div class="card-body text-center p-4">
                <div class="feature-icon mb-3">
                  <i class="bi bi-palette display-4 text-warning"></i>
                </div>
                <h5 class="card-title">多样表达</h5>
                <p class="card-text text-muted">
                  支持文字、图片多种形式记录心情，让每一种情感都能找到最合适的表达方式
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Philosophy Section -->
    <div class="philosophy-section py-5">
      <div class="container">
        <div class="row align-items-center">
          <div class="col-lg-6">
            <h2 class="fw-bold mb-4">我们的理念</h2>
            <div class="philosophy-item mb-4">
              <h5 class="text-primary">
                <i class="bi bi-heart me-2"></i>情感共鸣
              </h5>
              <p class="text-muted">
                每个人都有喜怒哀乐，我们希望通过分享让彼此的情感产生共鸣，让孤独的心灵找到温暖。
              </p>
            </div>
            
            <div class="philosophy-item mb-4">
              <h5 class="text-primary">
                <i class="bi bi-lightbulb me-2"></i>成长陪伴
              </h5>
              <p class="text-muted">
                通过记录和分享心情，我们可以更好地了解自己，在朋友的陪伴下成长和进步。
              </p>
            </div>
            
            <div class="philosophy-item mb-4">
              <h5 class="text-primary">
                <i class="bi bi-globe me-2"></i>正能量传递
              </h5>
              <p class="text-muted">
                我们鼓励用户分享正面的情感和经历，让积极向上的能量在社区中传递。
              </p>
            </div>
          </div>
          
          <div class="col-lg-6">
            <div class="quote-card">
              <div class="card border-0 shadow-sm">
                <div class="card-body p-4">
                  <blockquote class="blockquote text-center">
                    <p class="mb-3">
                      "分享让快乐加倍，让痛苦减半。在 MoodShare，每一份情感都值得被珍视。"
                    </p>
                    <footer class="blockquote-footer">
                      <cite title="MoodShare Team">MoodShare 团队</cite>
                    </footer>
                  </blockquote>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- How It Works Section -->
    <div class="how-it-works-section py-5 bg-light">
      <div class="container">
        <div class="text-center mb-5">
          <h2 class="fw-bold mb-3">如何开始使用？</h2>
          <p class="text-muted">简单三步，开启您的心情分享之旅</p>
        </div>
        
        <div class="row g-4">
          <div class="col-md-4">
            <div class="step-card text-center">
              <div class="step-number">1</div>
              <h5 class="mt-3 mb-3">注册账号</h5>
              <p class="text-muted">
                创建您的专属账号，设置个人资料，让朋友更好地认识您
              </p>
            </div>
          </div>
          
          <div class="col-md-4">
            <div class="step-card text-center">
              <div class="step-number">2</div>
              <h5 class="mt-3 mb-3">分享心情</h5>
              <p class="text-muted">
                记录当下的心情，配上文字或图片，让情感有个安放的地方
              </p>
            </div>
          </div>
          
          <div class="col-md-4">
            <div class="step-card text-center">
              <div class="step-number">3</div>
              <h5 class="mt-3 mb-3">交流互动</h5>
              <p class="text-muted">
                与好友互动交流，点赞评论，在彼此的陪伴中感受温暖
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>    <!-- Support Section -->
    <div class="support-section py-5 bg-light">
      <div class="container">
        <div class="text-center mb-5">
          <h2 class="fw-bold mb-3">支持我们</h2>
          <p class="text-muted">如果您喜欢 MoodShare，可以通过以下方式支持我们的项目</p>
        </div>
        
        <div class="row justify-content-center">
          <div class="col-lg-8">
            <div class="support-card">
              <div class="card border-0 shadow-sm">
                <div class="card-body p-5">
                  <div class="row g-4">
                    <div class="col-md-6">
                      <div class="support-method text-center">
                        <div class="support-icon mb-3">
                          <i class="bi bi-cup-hot display-4 text-warning"></i>
                        </div>
                        <h5 class="mb-3">请我们喝杯咖啡</h5>
                        <p class="text-muted mb-3">
                          您的支持是我们持续改进的动力
                        </p>                        <button class="btn btn-outline-warning" @click="showDonationQR">
                          <i class="bi bi-heart me-2"></i>打赏
                        </button>
                      </div>
                    </div>
                    
                    <div class="col-md-6">
                      <div class="support-method text-center">
                        <div class="support-icon mb-3">
                          <i class="bi bi-github display-4 text-dark"></i>
                        </div>
                        <h5 class="mb-3">开源支持</h5>
                        <p class="text-muted mb-3">
                          为项目点个 Star，或参与开发
                        </p>
                        <a href="https://github.com/ebendun/MoodShare" target="_blank" class="btn btn-outline-dark">
                          <i class="bi bi-star me-2"></i>GitHub Star
                        </a>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Call to Action Section -->
    <div class="cta-section py-5">
      <div class="container text-center">
        <h2 class="fw-bold mb-3">准备好分享您的心情了吗？</h2>
        <p class="lead text-muted mb-4">
          加入 MoodShare 社区，让您的每一种情感都能被理解和陪伴
        </p>
        <div class="d-flex justify-content-center gap-3">
          <router-link v-if="!isLoggedIn" to="/register" class="btn btn-primary btn-lg">
            <i class="bi bi-person-plus me-2"></i>立即注册
          </router-link>
          <router-link v-else to="/moods/create" class="btn btn-primary btn-lg">
            <i class="bi bi-plus-circle me-2"></i>分享心情
          </router-link>
        </div>
      </div>
    </div>    <!-- Donation Modal -->
    <div class="modal fade" id="donationModal" tabindex="-1" aria-labelledby="donationModalLabel" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header border-0">
            <h5 class="modal-title" id="donationModalLabel">
              <i class="bi bi-heart-fill text-danger me-2"></i>支持 MoodShare
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body text-center p-4">
            <h6 class="mb-3">感谢您的支持！</h6>
            <p class="text-muted mb-4">扫描下方二维码即可打赏</p>
            
            <!-- 支付方式选择 -->
            <div class="payment-tabs mb-4">
              <ul class="nav nav-pills nav-justified" role="tablist">
                <li class="nav-item" role="presentation">
                  <button class="nav-link active" id="wechat-tab" data-bs-toggle="pill" data-bs-target="#wechat" type="button" role="tab">
                    <i class="bi bi-wechat text-success me-2"></i>微信支付
                  </button>
                </li>
                <li class="nav-item" role="presentation">
                  <button class="nav-link" id="alipay-tab" data-bs-toggle="pill" data-bs-target="#alipay" type="button" role="tab">
                    <i class="bi bi-alipay text-primary me-2"></i>支付宝
                  </button>
                </li>
              </ul>
            </div>
            
            <!-- 二维码内容 -->
            <div class="tab-content">              <div class="tab-pane fade show active" id="wechat" role="tabpanel">
                <div class="qr-code-container mb-3">
                  <img src="/images/wechat-qr.jpg" alt="微信支付二维码" class="qr-image">
                  <p class="mt-2 text-muted">微信支付二维码</p>
                  <small class="text-muted">请使用微信扫一扫</small>
                </div>
              </div>              <div class="tab-pane fade" id="alipay" role="tabpanel">
                <div class="qr-code-container mb-3">
                  <img src="/images/alipay-qr.png" alt="支付宝支付二维码" class="qr-image">
                  <p class="mt-2 text-muted">支付宝支付二维码</p>
                  <small class="text-muted">请使用支付宝扫一扫</small>
                </div>
              </div>
            </div>
              
            <p class="text-muted small">
              <i class="bi bi-shield-check me-1"></i>
              所有交易均通过安全加密处理
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useStore } from 'vuex'
import { Modal } from 'bootstrap'

export default {
  name: 'AboutView',  setup() {
    const store = useStore()
    
    const isLoggedIn = computed(() => store.getters.isLoggedIn)
    
    const showDonationQR = () => {
      const modal = new Modal(document.getElementById('donationModal'))
      modal.show()
    }
    
    return {
      isLoggedIn,
      showDonationQR
    }
  }
}
</script>

<style scoped>
.about-page {
  overflow-x: hidden;
}

.hero-section {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  min-height: 60vh;
  display: flex;
  align-items: center;
}

.min-vh-50 {
  min-height: 50vh;
}

.feature-icon-large {
  background: linear-gradient(135deg, #007bff, #0056b3);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

.features-section {
  position: relative;
}

.feature-card {
  background: white;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border: none;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0,0,0,0.15);
}

.feature-icon {
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  border-radius: 50%;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}

.philosophy-section {
  background: white;
}

.philosophy-item {
  padding: 1rem 0;
  border-left: 3px solid #007bff;
  padding-left: 1rem;
  margin-left: 0.5rem;
}

.quote-card {
  position: relative;
}

.quote-card::before {
  content: '"';
  position: absolute;
  top: -20px;
  left: 20px;
  font-size: 4rem;
  color: #007bff;
  opacity: 0.3;
}

.how-it-works-section {
  position: relative;
}

.step-card {
  position: relative;
  padding: 2rem 1rem;
}

.step-number {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #007bff, #0056b3);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  font-weight: bold;
  margin: 0 auto;
  box-shadow: 0 5px 15px rgba(0,123,255,0.3);
}

.cta-section {
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
  color: white;
}

.cta-section .text-muted {
  color: rgba(255,255,255,0.8) !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .display-4 {
    font-size: 2.5rem;
  }
  
  .hero-section {
    min-height: 50vh;
    padding: 2rem 0;
  }
  
  .feature-card {
    margin-bottom: 1rem;
  }
  
  .step-card {
    padding: 1.5rem 0.5rem;
  }
}

/* 按钮动画 */
.btn {
  transition: all 0.3s ease;
}

.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0,0,0,0.2);
}

/* 打赏模块样式 */
.support-section {
  background: #f8f9fa;
}

.support-card {
  background: white;
  border-radius: 15px;
  overflow: hidden;
}

.support-method {
  padding: 1rem;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.support-method:hover {
  background: #f8f9fa;
  transform: translateY(-2px);
}

.support-icon {
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  border-radius: 50%;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}

.donation-option .card {
  transition: all 0.3s ease;
  cursor: pointer;
}

.donation-option .card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

.donation-option .card.selected {
  background: #007bff;
  color: white;
}

.modal-content {
  border-radius: 15px;
  border: none;
  box-shadow: 0 20px 40px rgba(0,0,0,0.1);
}

.modal-header {
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  border-radius: 15px 15px 0 0;
}

/* 二维码样式 */
.qr-code-container {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 10px;
  border: 2px dashed #dee2e6;
}

.qr-placeholder {
  padding: 40px 20px;
  text-align: center;
  color: #6c757d;
}

.payment-tabs .nav-link {
  border-radius: 50px;
  font-weight: 500;
  padding: 10px 20px;
}

.payment-tabs .nav-link.active {
  background: linear-gradient(135deg, #007bff, #0056b3);
  border: none;
  color: white;
}

/* 滚动动画 */
.fade-in {
  animation: fadeInUp 0.8s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.qr-image {
  width: 200px;
  height: 200px;
  border: 1px solid #ddd;
  border-radius: 8px;
  object-fit: contain;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
</style>
