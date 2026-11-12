<template>
  <div class="success-page">
    <div class="success-content">
      <div class="success-icon">
        <svg viewBox="0 0 100 100" class="check-svg">
          <circle cx="50" cy="50" r="45" fill="#1890ff" />
          <path 
            d="M35 50 L45 60 L70 35" 
            stroke="#fff" 
            stroke-width="6" 
            stroke-linecap="round" 
            stroke-linejoin="round" 
            fill="none"
          />
        </svg>
      </div>

      <h2 class="success-title">提交成功</h2>
      <p class="success-desc">您的问题已成功提交，社区工作人员将尽快处理</p>

      <div class="info-card">
        <div class="info-item">
          <span class="info-label">工单编号</span>
          <span class="info-value">{{ orderNo }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">提交时间</span>
          <span class="info-value">{{ submitTime }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">当前状态</span>
          <span class="info-value status-pending">{{ statusText }}</span>
        </div>
      </div>

      <div class="action-btns">
        <button class="btn btn-secondary" @click="goBack">
          返回首页
        </button>
        <button class="btn btn-primary" @click="goToSubmit">
          继续上报
        </button>
      </div>

      <div class="contact-card">
        <div class="contact-header">
          <span class="contact-icon">📞</span>
          <span class="contact-title">如有疑问，请联系社区</span>
        </div>
        <p class="contact-phone">社区联系电话：18015288836</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const orderNo = route.query.id || ('MD' + Date.now().toString().slice(-8))
const submitTime = route.query.createTime || (() => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const hour = String(now.getHours()).padStart(2, '0')
  const minute = String(now.getMinutes()).padStart(2, '0')
  const second = String(now.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}:${second}`
})()
const statusText = '待处理'

const goBack = () => {
  router.push('/')
}

const goToSubmit = () => {
  router.push('/submit')
}
</script>

<style scoped>
.success-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #1890ff 0%, #096dd9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 24px;
}

.success-content {
  width: 100%;
  text-align: center;
}

.success-icon {
  width: 160px;
  height: 160px;
  margin: 0 auto 40px;
}

.check-svg {
  width: 100%;
  height: 100%;
  animation: scaleIn 0.5s ease-out;
}

@keyframes scaleIn {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  50% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.success-title {
  font-size: 40px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 16px;
}

.success-desc {
  font-size: 28px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 40px;
}

.info-card {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 20px;
  padding: 32px;
  margin-bottom: 40px;
  backdrop-filter: blur(10px);
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
}

.info-item:first-child {
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.info-label {
  font-size: 26px;
  color: rgba(255, 255, 255, 0.8);
}

.info-value {
  font-size: 26px;
  font-weight: 600;
  color: #fff;
}

.status-pending {
  color: #ffd666;
}

.action-btns {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 40px;
}

.btn {
  height: 88px;
  border-radius: 44px;
  font-size: 30px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.btn-primary {
  background: #fff;
  color: #1890ff;
}

.contact-card {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 24px;
  text-align: left;
}

.contact-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.contact-icon {
  font-size: 28px;
}

.contact-title {
  font-size: 26px;
  color: rgba(255, 255, 255, 0.9);
}

.contact-phone {
  font-size: 28px;
  color: #fff;
  font-weight: 500;
}
</style>
