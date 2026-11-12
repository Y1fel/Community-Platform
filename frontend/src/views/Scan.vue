<template>
  <div class="home-page">
    <!-- 顶部标题区域 -->
    <div class="header">
      <div class="logo">
        <span class="logo-icon">🏠</span>
      </div>
      <h1 class="title">社区民情收集平台</h1>
      <p class="subtitle">大事小情码上说</p>
    </div>

    <!-- 核心操作区域 -->
    <div class="main-content">
      <!-- 填写上报 -->
      <div class="action-card submit-card" @click="goToSubmit">
        <div class="action-icon-area">
          <span class="action-icon">✏️</span>
        </div>
        <div class="action-info">
          <span class="action-title">填写上报</span>
          <span class="action-desc">提交您的意见和问题</span>
        </div>
        <span class="action-arrow">›</span>
      </div>

      <!-- 知识问答 -->
      <div class="action-card quiz-card" @click="goToQuiz">
        <div class="action-icon-area">
          <span class="action-icon">📝</span>
        </div>
        <div class="action-info">
          <span class="action-title">知识问答</span>
          <span class="action-desc">答题赢积分，兑换奖品</span>
        </div>
        <span class="action-arrow">›</span>
      </div>

      <!-- 社区电话 -->
      <div class="action-card phone-card" @click="callPhone">
        <div class="action-icon-area">
          <span class="action-icon">📞</span>
        </div>
        <div class="action-info">
          <span class="action-title">社区联系电话</span>
          <span class="action-desc">18015288836</span>
        </div>
        <span class="action-arrow">›</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goToSubmit = () => {
  router.push('/submit')
}

const goToQuiz = () => {
  router.push('/quiz')
}

const callPhone = () => {
  window.location.href = 'tel:18015288836'
}
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #1890ff 0%, #096dd9 100%);
}

.header {
  padding: 80px 32px 60px;
  text-align: center;
}

.logo {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
}

.logo-icon {
  font-size: 40px;
}

.title {
  font-size: 36px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 8px;
}

.subtitle {
  font-size: 24px;
  color: rgba(255, 255, 255, 0.8);
}

.main-content {
  background: #f5f7fa;
  border-radius: 32px;
  padding: 32px 40px;
  margin: 0 16px 16px;
}

.action-card {
  display: flex;
  align-items: center;
  gap: 20px;
  background: #fff;
  border-radius: 20px;
  padding: 28px 24px;
  margin-bottom: 20px;
  cursor: pointer;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: all 0.2s ease;
}

.action-card:active {
  transform: scale(0.98);
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.08);
}

.action-icon-area {
  width: 64px;
  height: 64px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.submit-card .action-icon-area {
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
}

.quiz-card .action-icon-area {
  background: linear-gradient(135deg, #fff7e6 0%, #ffe58f 100%);
}

.phone-card .action-icon-area {
  background: linear-gradient(135deg, #f6ffed 0%, #d9f7be 100%);
}

.action-icon {
  font-size: 32px;
}

.action-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.action-title {
  font-size: 28px;
  font-weight: 600;
  color: #333;
}

.action-desc {
  font-size: 22px;
  color: #999;
}

.action-arrow {
  font-size: 36px;
  color: #ccc;
  font-weight: 300;
}
</style>
