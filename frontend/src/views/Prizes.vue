<template>
  <div class="prizes-page">
    <div class="nav-bar">
      <button class="back-btn" @click="goBack">
        <span class="back-icon">←</span>
      </button>
      <h2 class="nav-title">奖品兑换</h2>
      <div class="nav-placeholder"></div>
    </div>

    <div class="prizes-content">
      <div class="my-score-card">
        <div class="score-info">
          <span class="score-label">我的积分</span>
          <span class="score-value">{{ myScore || 0 }}</span>
        </div>
      </div>

      <div class="prizes-grid">
        <div
          v-for="prize in prizes"
          :key="prize.id"
          class="prize-card"
          :class="{ disabled: prize.stock <= 0 || (myScore || 0) < prize.cost }"
        >
          <div class="prize-image">
            <span class="prize-icon">🎁</span>
          </div>
          <div class="prize-info">
            <h3 class="prize-name">{{ prize.name }}</h3>
            <p class="prize-desc">{{ prize.description }}</p>
            <div class="prize-footer">
              <span class="prize-cost">{{ prize.cost }}积分</span>
              <span class="prize-stock">{{ prize.stock }}份</span>
            </div>
          </div>
          <button
            class="exchange-btn"
            :class="{ disabled: prize.stock <= 0 || (myScore || 0) < prize.cost }"
            @click="handleExchange(prize)"
          >
            {{ prize.stock <= 0 ? '已售罄' : (myScore || 0) < prize.cost ? '积分不足' : '立即兑换' }}
          </button>
        </div>
      </div>

      <div class="exchange-history">
        <h3 class="history-title">兑换记录</h3>
        <div class="history-list">
          <div
            v-for="record in exchangeRecords"
            :key="record.id"
            class="history-item"
          >
            <div class="history-info">
              <span class="history-prize">{{ record.prizeName }}</span>
              <span class="history-time">{{ formatTime(record.createTime) }}</span>
            </div>
            <span class="history-status" :class="record.status">
              {{ record.status === 'pending' ? '待领取' : record.status === 'completed' ? '已领取' : '已取消' }}
            </span>
          </div>
          <div class="empty-state" v-if="exchangeRecords.length === 0">
            暂无兑换记录
          </div>
        </div>
      </div>

      <div class="action-btns">
        <button class="btn btn-primary" @click="goQuiz">去答题赚积分</button>
        <button class="btn btn-secondary" @click="goRanking">查看排行榜</button>
      </div>
    </div>

    <div class="exchange-modal" v-if="showExchangeModal" @click.self="showExchangeModal = false">
      <div class="modal-content">
        <div class="modal-header">确认兑换</div>
        <div class="modal-body">
          <div class="modal-prize">
            <span class="modal-icon">🎁</span>
            <div class="modal-prize-info">
              <span class="modal-prize-name">{{ selectedPrize?.name }}</span>
              <span class="modal-prize-cost">消耗 {{ selectedPrize?.cost }} 积分</span>
            </div>
          </div>
          <div class="modal-form">
            <input
              v-model="exchangeForm.name"
              type="text"
              class="form-input"
              placeholder="您的姓名"
            />
            <input
              v-model="exchangeForm.phone"
              type="tel"
              class="form-input"
              placeholder="手机号码"
              maxlength="11"
            />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="showExchangeModal = false">取消</button>
          <button class="btn btn-primary" @click="confirmExchange">确认兑换</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getPrizes, exchangePrize, getExchangeRecords, getUserScore } from '@/api/quiz'

const router = useRouter()

const prizes = ref([])
const myScore = ref(0)
const exchangeRecords = ref([])
const showExchangeModal = ref(false)
const selectedPrize = ref(null)
const exchangeForm = ref({ name: '', phone: '' })
const userPhone = ref('')
const userName = ref('')

const goBack = () => {
  router.push('/')
}

const goQuiz = () => {
  router.push('/quiz')
}

const goRanking = () => {
  router.push('/ranking')
}

const handleExchange = (prize) => {
  if (prize.stock <= 0) {
    showToast('奖品已售罄')
    return
  }
  if ((myScore.value || 0) < prize.cost) {
    showToast('积分不足')
    return
  }
  selectedPrize.value = prize
  exchangeForm.value = {
    name: userName.value || '',
    phone: userPhone.value || ''
  }
  showExchangeModal.value = true
}

const confirmExchange = async () => {
  if (!exchangeForm.value.name || !exchangeForm.value.phone) {
    showToast('请填写完整信息')
    return
  }

  try {
    await exchangePrize({
      prizeId: selectedPrize.value.id,
      phone: exchangeForm.value.phone,
      realName: exchangeForm.value.name
    })

    showToast('兑换成功')
    showExchangeModal.value = false
    exchangeForm.value = { name: '', phone: '' }
    await loadData()
  } catch (error) {
    showToast('兑换失败，请稍后重试')
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

const loadData = async () => {
  try {
    prizes.value = await getPrizes()

    userPhone.value = localStorage.getItem('quiz_user_phone') || ''
    userName.value = localStorage.getItem('quiz_user_name') || ''

    if (userPhone.value) {
      try {
        const scoreInfo = await getUserScore(userPhone.value)
        if (scoreInfo) {
          myScore.value = scoreInfo.score || 0
        }
      } catch (error) {
        console.log('获取积分失败:', error)
      }
    }
  } catch (error) {
    showToast('获取奖品失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.prizes-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 88px;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  z-index: 100;
}

.back-btn {
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
}

.back-icon {
  font-size: 32px;
  color: #fff;
}

.nav-title {
  font-size: 32px;
  font-weight: 600;
  color: #fff;
}

.nav-placeholder {
  width: 64px;
}

.prizes-content {
  padding: 120px 24px 40px;
}

.my-score-card {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  border-radius: 24px;
  padding: 32px;
  margin-bottom: 32px;
}

.score-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.score-label {
  font-size: 28px;
  color: rgba(255, 255, 255, 0.8);
}

.score-value {
  font-size: 56px;
  font-weight: 700;
  color: #fff;
}

.prizes-grid {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-bottom: 32px;
}

.prize-card {
  background: #fff;
  border-radius: 24px;
  padding: 24px;
  position: relative;
}

.prize-card.disabled {
  opacity: 0.6;
}

.prize-image {
  width: 120px;
  height: 120px;
  background: #f0f5ff;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  float: left;
  margin-right: 20px;
}

.prize-icon {
  font-size: 64px;
}

.prize-info {
  overflow: hidden;
}

.prize-name {
  font-size: 32px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.prize-desc {
  font-size: 26px;
  color: #666;
  margin-bottom: 12px;
}

.prize-footer {
  display: flex;
  gap: 20px;
}

.prize-cost {
  font-size: 28px;
  font-weight: 600;
  color: #1890ff;
}

.prize-stock {
  font-size: 24px;
  color: #999;
}

.exchange-btn {
  position: absolute;
  right: 24px;
  bottom: 24px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  color: #fff;
  border-radius: 24px;
  font-size: 24px;
  font-weight: 500;
}

.exchange-btn.disabled {
  background: #ccc;
}

.exchange-history {
  background: #fff;
  border-radius: 24px;
  padding: 24px;
  margin-bottom: 32px;
}

.history-title {
  font-size: 30px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.history-item:last-child {
  border-bottom: none;
}

.history-info {
  flex: 1;
}

.history-prize {
  font-size: 28px;
  font-weight: 500;
  color: #333;
  display: block;
  margin-bottom: 4px;
}

.history-time {
  font-size: 22px;
  color: #999;
}

.history-status {
  font-size: 24px;
  font-weight: 500;
  padding: 6px 16px;
  border-radius: 12px;
}

.history-status.pending {
  background: #fff7e6;
  color: #fa8c16;
}

.history-status.completed {
  background: #f6ffed;
  color: #52c41a;
}

.history-status.cancelled {
  background: #fff1f0;
  color: #ff4d4f;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #999;
  font-size: 26px;
}

.action-btns {
  padding: 0 16px;
}

.btn {
  width: 100%;
  height: 88px;
  border-radius: 44px;
  font-size: 30px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.btn-primary {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  color: #fff;
  box-shadow: 0 8px 24px rgba(24, 144, 255, 0.3);
}

.btn-secondary {
  background: #fff;
  color: #1890ff;
}

.exchange-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  width: 85%;
  background: #fff;
  border-radius: 24px;
  overflow: hidden;
}

.modal-header {
  font-size: 32px;
  font-weight: 600;
  color: #333;
  text-align: center;
  padding: 32px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-body {
  padding: 32px;
}

.modal-prize {
  display: flex;
  align-items: center;
  margin-bottom: 32px;
}

.modal-icon {
  font-size: 64px;
  margin-right: 20px;
}

.modal-prize-info {
  flex: 1;
}

.modal-prize-name {
  font-size: 30px;
  font-weight: 600;
  color: #333;
  display: block;
  margin-bottom: 8px;
}

.modal-prize-cost {
  font-size: 26px;
  color: #1890ff;
}

.modal-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-input {
  width: 100%;
  height: 80px;
  padding: 0 24px;
  background: #f8f9fa;
  border: 2px solid #e8e8e8;
  border-radius: 12px;
  font-size: 28px;
}

.modal-footer {
  padding: 24px 32px;
  border-top: 1px solid #f0f0f0;
}
</style>
