<template>
  <div class="quiz-page">
    <div class="nav-bar">
      <button class="back-btn" @click="goBack">
        <span class="back-icon">←</span>
      </button>
      <h2 class="nav-title">知识问答</h2>
      <div class="nav-placeholder"></div>
    </div>

    <div class="quiz-content" v-if="!showResult">
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
      </div>
      <div class="progress-text">{{ currentIndex + 1 }}/{{ questions.length }}</div>

      <div class="question-card" v-if="currentQuestion">
        <div class="question-header">
          <span class="difficulty-tag" :class="'level-' + currentQuestion.difficulty">
            {{ currentQuestion.difficulty === 1 ? '简单' : '中等' }}
          </span>
          <span class="score-tag">{{ currentQuestion.score }}分</span>
        </div>
        <h3 class="question-content">{{ currentQuestion.content }}</h3>
        <div class="options-list">
          <div
            v-for="(option, idx) in currentQuestion.options"
            :key="idx"
            class="option-item"
            :class="{ selected: selectedAnswer === getOptionLabel(idx) }"
            @click="selectOption(getOptionLabel(idx))"
          >
            <span class="option-label">{{ getOptionLabel(idx) }}</span>
            <span class="option-text">{{ option }}</span>
            <span class="option-check" v-if="selectedAnswer === getOptionLabel(idx)">✓</span>
          </div>
        </div>
      </div>

      <div class="action-btns">
        <button
          class="submit-btn"
          :class="{ disabled: !selectedAnswer }"
          :disabled="!selectedAnswer"
          @click="handleSubmit"
        >
          {{ isLastQuestion ? '提交答案' : '下一题' }}
        </button>
      </div>

      <div class="user-form" v-if="!hasUserInfo">
        <div class="form-title">请填写您的信息</div>
        <input
          v-model="userInfo.name"
          type="text"
          class="form-input"
          placeholder="您的姓名"
        />
        <input
          v-model="userInfo.phone"
          type="tel"
          class="form-input"
          placeholder="手机号码"
          maxlength="11"
        />
      </div>
    </div>

    <div class="result-content" v-else>
      <div class="result-icon" :class="isCorrect ? 'correct' : 'wrong'">
        <span>{{ isCorrect ? '✓' : '✗' }}</span>
      </div>
      <h2 class="result-title">{{ isCorrect ? '回答正确' : '回答错误' }}</h2>
      <p class="result-desc">
        正确答案是：<span class="correct-answer">{{ resultData.correctAnswer }}</span>
      </p>
      <div class="result-score">
        <div class="score-item">
          <span class="score-label">本次得分</span>
          <span class="score-value" :class="isCorrect ? 'positive' : 'zero'">+{{ resultData.score }}</span>
        </div>
        <div class="score-item">
          <span class="score-label">累计积分</span>
          <span class="score-value highlight">{{ resultData.totalScore }}</span>
        </div>
      </div>
      <div class="action-btns">
        <button class="btn btn-secondary" @click="goBack">返回首页</button>
        <button class="btn btn-primary" @click="continueQuiz" v-if="!isLastQuestion">继续答题</button>
        <button class="btn btn-primary" @click="viewRanking" v-else>查看排行榜</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getQuestions, submitAnswer } from '@/api/quiz'

const router = useRouter()

const questions = ref([])
const currentIndex = ref(0)
const selectedAnswer = ref('')
const showResult = ref(false)
const resultData = ref({})
const isCorrect = ref(false)
const hasUserInfo = ref(false)
const userInfo = ref({ name: '', phone: '' })

const currentQuestion = computed(() => questions.value[currentIndex.value])
const progressPercent = computed(() => ((currentIndex.value + 1) / questions.value.length) * 100)
const isLastQuestion = computed(() => currentIndex.value === questions.value.length - 1)

const getOptionLabel = (idx) => String.fromCharCode(65 + idx)

const selectOption = (label) => {
  selectedAnswer.value = label
}

const handleSubmit = async () => {
  if (!selectedAnswer.value) {
    showToast('请选择答案')
    return
  }

  if (!hasUserInfo.value && (!userInfo.value.name || !userInfo.value.phone)) {
    showToast('请填写姓名和手机号')
    return
  }

  try {
    const res = await submitAnswer({
      questionId: currentQuestion.value.id,
      answer: selectedAnswer.value,
      phone: userInfo.value.phone,
      realName: userInfo.value.name
    })

    resultData.value = res
    isCorrect.value = res.isCorrect
    showResult.value = true

    if (!hasUserInfo.value) {
      hasUserInfo.value = true
    }

    localStorage.setItem('quiz_user_phone', userInfo.value.phone)
    localStorage.setItem('quiz_user_name', userInfo.value.name)
  } catch (error) {
    showToast('提交失败，请稍后重试')
  }
}

const continueQuiz = () => {
  showResult.value = false
  selectedAnswer.value = ''
  currentIndex.value++
}

const viewRanking = () => {
  router.push('/ranking')
}

const goBack = () => {
  router.push('/')
}

onMounted(async () => {
  try {
    questions.value = await getQuestions(5)
  } catch (error) {
    showToast('获取题目失败，请稍后重试')
  }
})
</script>

<style scoped>
.quiz-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 88px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  z-index: 100;
}

.back-btn {
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #f5f5f5;
}

.back-icon {
  font-size: 32px;
  color: #333;
}

.nav-title {
  font-size: 32px;
  font-weight: 600;
  color: #333;
}

.nav-placeholder {
  width: 64px;
}

.quiz-content {
  padding: 120px 24px 40px;
}

.progress-bar {
  height: 8px;
  background: #e8e8e8;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 16px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #1890ff, #096dd9);
  border-radius: 4px;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 24px;
  color: #999;
  text-align: right;
  margin-bottom: 24px;
}

.question-card {
  background: #fff;
  border-radius: 24px;
  padding: 32px;
  margin-bottom: 32px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}

.question-header {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.difficulty-tag {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 22px;
}

.level-1 {
  background: #f6ffed;
  color: #52c41a;
}

.level-2 {
  background: #fff7e6;
  color: #fa8c16;
}

.score-tag {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 22px;
  background: #e6f7ff;
  color: #1890ff;
}

.question-content {
  font-size: 32px;
  font-weight: 600;
  color: #333;
  line-height: 1.6;
  margin-bottom: 32px;
}

.options-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.option-item {
  display: flex;
  align-items: center;
  padding: 28px 24px;
  background: #f8f9fa;
  border-radius: 16px;
  border: 2px solid transparent;
  transition: all 0.2s ease;
}

.option-item.selected {
  background: #e6f7ff;
  border-color: #1890ff;
}

.option-label {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  border-radius: 50%;
  font-size: 28px;
  font-weight: 600;
  color: #666;
  margin-right: 20px;
  flex-shrink: 0;
}

.option-item.selected .option-label {
  background: #1890ff;
  color: #fff;
}

.option-text {
  flex: 1;
  font-size: 28px;
  color: #333;
}

.option-check {
  font-size: 32px;
  color: #1890ff;
  font-weight: bold;
}

.action-btns {
  padding: 0 16px;
}

.submit-btn {
  width: 100%;
  height: 96px;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  border-radius: 48px;
  font-size: 32px;
  font-weight: 600;
  color: #fff;
  box-shadow: 0 8px 24px rgba(24, 144, 255, 0.3);
}

.submit-btn.disabled {
  background: #ccc;
  box-shadow: none;
}

.user-form {
  margin-top: 32px;
  background: #fff;
  border-radius: 24px;
  padding: 24px 32px;
}

.form-title {
  font-size: 26px;
  font-weight: 500;
  color: #666;
  margin-bottom: 20px;
}

.form-input {
  width: 100%;
  height: 80px;
  padding: 0 24px;
  background: #f8f9fa;
  border: 2px solid #e8e8e8;
  border-radius: 12px;
  font-size: 28px;
  margin-bottom: 16px;
}

.form-input:last-child {
  margin-bottom: 0;
}

.result-content {
  padding: 120px 24px 40px;
  text-align: center;
}

.result-icon {
  width: 160px;
  height: 160px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 32px;
  font-size: 80px;
  color: #fff;
}

.result-icon.correct {
  background: linear-gradient(135deg, #52c41a, #389e0d);
}

.result-icon.wrong {
  background: linear-gradient(135deg, #ff4d4f, #cf1322);
}

.result-title {
  font-size: 40px;
  font-weight: 700;
  color: #333;
  margin-bottom: 16px;
}

.result-desc {
  font-size: 28px;
  color: #666;
  margin-bottom: 40px;
}

.correct-answer {
  color: #1890ff;
  font-weight: 600;
}

.result-score {
  background: rgba(24, 144, 255, 0.1);
  border-radius: 20px;
  padding: 32px;
  margin-bottom: 40px;
}

.score-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
}

.score-item:first-child {
  border-bottom: 1px solid rgba(24, 144, 255, 0.2);
}

.score-label {
  font-size: 28px;
  color: #666;
}

.score-value {
  font-size: 36px;
  font-weight: 700;
  color: #333;
}

.score-value.positive {
  color: #52c41a;
}

.score-value.zero {
  color: #999;
}

.score-value.highlight {
  color: #1890ff;
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

.btn-secondary {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.btn-primary {
  background: #fff;
  color: #1890ff;
}
</style>
