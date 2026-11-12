<template>
  <div class="ranking-page">
    <div class="nav-bar">
      <button class="back-btn" @click="goBack">
        <span class="back-icon">←</span>
      </button>
      <h2 class="nav-title">积分排行榜</h2>
      <div class="nav-placeholder"></div>
    </div>

    <div class="ranking-content">
      <div class="podium" v-if="topThree.length >= 3">
        <div class="podium-item second">
          <div class="medal">🥈</div>
          <div class="avatar">{{ topThree[1].realName.charAt(0) }}</div>
          <div class="name">{{ topThree[1].realName }}</div>
          <div class="score">{{ topThree[1].score }}分</div>
          <div class="rank-label">2</div>
        </div>
        <div class="podium-item first">
          <div class="medal">🥇</div>
          <div class="avatar gold">{{ topThree[0].realName.charAt(0) }}</div>
          <div class="name">{{ topThree[0].realName }}</div>
          <div class="score">{{ topThree[0].score }}分</div>
          <div class="rank-label">1</div>
        </div>
        <div class="podium-item third">
          <div class="medal">🥉</div>
          <div class="avatar">{{ topThree[2].realName.charAt(0) }}</div>
          <div class="name">{{ topThree[2].realName }}</div>
          <div class="score">{{ topThree[2].score }}分</div>
          <div class="rank-label">3</div>
        </div>
      </div>

      <div class="ranking-list">
        <div
          v-for="(item, index) in rankingList"
          :key="index"
          class="ranking-item"
          :class="{ highlight: item.phone === userPhone }"
        >
          <span class="rank-num">{{ item.rank }}</span>
          <span class="user-avatar">{{ item.realName.charAt(0) }}</span>
          <div class="user-info">
            <span class="user-name">{{ item.realName }}</span>
            <span class="user-stats">答题{{ item.totalQuiz }}次，正确{{ item.correctNum }}次</span>
          </div>
          <span class="user-score">{{ item.score }}分</span>
        </div>
      </div>

      <div class="my-score" v-if="myScore">
        <div class="score-header">我的积分</div>
        <div class="score-body">
          <div class="my-rank">第{{ myScore.rank }}名</div>
          <div class="my-points">{{ myScore.score }}分</div>
        </div>
        <div class="my-stats">
          累计答题{{ myScore.totalQuiz }}次，正确{{ myScore.correctNum }}次，正确率{{ correctRate }}%
        </div>
      </div>

      <div class="action-btns">
        <button class="btn btn-primary" @click="goQuiz">去答题</button>
        <button class="btn btn-secondary" @click="goPrizes">兑换奖品</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getRanking, getUserScore } from '@/api/quiz'

const router = useRouter()

const rankingList = ref([])
const myScore = ref(null)
const userPhone = ref('')

const topThree = computed(() => rankingList.value.slice(0, 3))
const correctRate = computed(() => {
  if (!myScore.value || myScore.value.totalQuiz === 0) return 0
  return Math.round((myScore.value.correctNum / myScore.value.totalQuiz) * 100)
})

const goBack = () => {
  router.push('/')
}

const goQuiz = () => {
  router.push('/quiz')
}

const goPrizes = () => {
  router.push('/prizes')
}

onMounted(async () => {
  try {
    rankingList.value = await getRanking(20)

    userPhone.value = localStorage.getItem('quiz_user_phone') || ''
    if (userPhone.value) {
      try {
        myScore.value = await getUserScore(userPhone.value)
      } catch (error) {
        console.log('获取个人积分失败:', error)
      }
    }
  } catch (error) {
    showToast('获取排行榜失败')
  }
})
</script>

<style scoped>
.ranking-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #1890ff 0%, #096dd9 30%, #f5f5f5 30%);
}

.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 88px;
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

.ranking-content {
  padding: 120px 24px 40px;
}

.podium {
  display: flex;
  justify-content: center;
  align-items: flex-end;
  gap: 20px;
  padding: 40px 0;
}

.podium-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}

.medal {
  font-size: 48px;
  margin-bottom: 12px;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: 600;
  color: #1890ff;
  margin-bottom: 8px;
  border: 4px solid #e8e8e8;
}

.avatar.gold {
  background: linear-gradient(135deg, #fff9e6, #fff);
  border-color: #ffd666;
}

.name {
  font-size: 24px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
}

.score {
  font-size: 28px;
  font-weight: 700;
  color: #1890ff;
  margin-bottom: 12px;
}

.rank-label {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #e8e8e8;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 600;
  color: #666;
}

.first {
  order: 2;
}

.first .rank-label {
  background: linear-gradient(135deg, #ffd666, #ffa940);
  color: #fff;
  width: 48px;
  height: 48px;
  font-size: 28px;
}

.second {
  order: 1;
}

.second .rank-label {
  background: linear-gradient(135deg, #bfbfbf, #999);
  color: #fff;
}

.third {
  order: 3;
}

.third .rank-label {
  background: linear-gradient(135deg, #d4a373, #bc6c25);
  color: #fff;
}

.ranking-list {
  background: #fff;
  border-radius: 24px;
  overflow: hidden;
  margin-bottom: 24px;
}

.ranking-item {
  display: flex;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.ranking-item:last-child {
  border-bottom: none;
}

.ranking-item.highlight {
  background: #e6f7ff;
}

.rank-num {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 600;
  color: #666;
  margin-right: 20px;
}

.user-avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: #f0f5ff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 600;
  color: #1890ff;
  margin-right: 20px;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 28px;
  font-weight: 500;
  color: #333;
  display: block;
  margin-bottom: 6px;
}

.user-stats {
  font-size: 22px;
  color: #999;
}

.user-score {
  font-size: 32px;
  font-weight: 700;
  color: #1890ff;
}

.my-score {
  background: linear-gradient(135deg, #1890ff, #096dd9);
  border-radius: 24px;
  padding: 32px;
  color: #fff;
  margin-bottom: 32px;
}

.score-header {
  font-size: 26px;
  opacity: 0.8;
  margin-bottom: 16px;
}

.score-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.my-rank {
  font-size: 28px;
  font-weight: 500;
}

.my-points {
  font-size: 48px;
  font-weight: 700;
}

.my-stats {
  font-size: 24px;
  opacity: 0.8;
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
</style>
