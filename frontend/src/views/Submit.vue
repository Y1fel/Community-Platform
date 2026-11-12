<template>
  <div class="submit-page">
    <div class="nav-bar">
      <button class="back-btn" @click="goBack">
        <span class="back-icon">←</span>
      </button>
      <h2 class="nav-title">问题上报</h2>
      <div class="nav-placeholder"></div>
    </div>

    <div class="form-content">
      <div class="form-card">
        <div class="form-item">
          <label class="form-label">
            <span class="required">*</span>上报人
          </label>
          <input 
            v-model="form.reporter" 
            type="text" 
            class="form-input" 
            placeholder="请输入您的姓名"
          />
        </div>

        <div class="form-item">
          <label class="form-label">
            <span class="required">*</span>手机号
          </label>
          <input 
            v-model="form.phone" 
            type="tel" 
            class="form-input" 
            placeholder="请输入您的手机号码"
            maxlength="11"
          />
        </div>

        <div class="form-item">
          <label class="form-label">
            <span class="required">*</span>家庭住址
          </label>
          <input 
            v-model="form.address" 
            type="text" 
            class="form-input" 
            placeholder="请输入您的详细住址"
          />
        </div>

        <div class="form-item">
          <label class="form-label">
            <span class="required">*</span>问题描述
          </label>
          <textarea 
            v-model="form.description" 
            class="form-textarea" 
            placeholder="请详细描述您遇到的问题或建议"
            rows="4"
          ></textarea>
        </div>

        <div class="form-item">
          <label class="form-label">图片（选填）</label>
          <div class="image-upload">
            <div class="upload-grid">
              <div 
                v-for="(img, index) in form.images" 
                :key="index" 
                class="upload-item has-image"
              >
                <img :src="img" alt="上传图片" />
                <button class="remove-btn" @click="removeImage(index)">
                  <span>×</span>
                </button>
              </div>
              <div 
                v-if="form.images.length < 4" 
                class="upload-item upload-btn"
                @click="chooseImage"
              >
                <span class="upload-icon">+</span>
                <span class="upload-text">添加图片</span>
              </div>
            </div>
            <p class="upload-hint">最多可上传4张图片</p>
          </div>
        </div>
      </div>

      <div class="submit-btn-wrap">
        <button 
          class="submit-btn" 
          :class="{ disabled: !isValid || isSubmitting }"
          :disabled="!isValid || isSubmitting"
          @click="handleSubmit"
        >
          提交上报
        </button>
      </div>

      <div class="footer-info">
        <p>提交后，社区工作人员将尽快处理并与您联系</p>
      </div>
    </div>

    <input 
      ref="fileInput" 
      type="file" 
      accept="image/*" 
      multiple 
      class="hidden-input"
      @change="handleFileChange"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { submitReport } from '@/api/report'

const router = useRouter()
const fileInput = ref(null)

const form = ref({
  reporter: '',
  phone: '',
  address: '',
  description: '',
  images: []
})

const isValid = computed(() => {
  return (
    form.value.reporter.trim() &&
    form.value.phone.trim() &&
    /^1[3-9]\d{9}$/.test(form.value.phone) &&
    form.value.address.trim() &&
    form.value.description.trim()
  )
})

const goBack = () => {
  router.back()
}

const chooseImage = () => {
  fileInput.value.click()
}

const compressImage = (file) => new Promise((resolve, reject) => {
  const reader = new FileReader()
  reader.onload = (event) => {
    const image = new Image()
    image.onload = () => {
      const maxSide = 1280
      const scale = Math.min(1, maxSide / Math.max(image.width, image.height))
      const canvas = document.createElement('canvas')
      canvas.width = Math.round(image.width * scale)
      canvas.height = Math.round(image.height * scale)
      const context = canvas.getContext('2d')
      context.drawImage(image, 0, 0, canvas.width, canvas.height)
      resolve(canvas.toDataURL('image/jpeg', 0.75))
    }
    image.onerror = reject
    image.src = event.target.result
  }
  reader.onerror = reject
  reader.readAsDataURL(file)
})

const handleFileChange = async (e) => {
  const files = e.target.files
  if (!files || files.length === 0) return

  const remaining = 4 - form.value.images.length
  const count = Math.min(files.length, remaining)

  try {
    const images = await Promise.all(
      Array.from(files).slice(0, count).map(compressImage)
    )
    form.value.images.push(...images)
  } catch (error) {
    showToast('图片处理失败，请重新选择')
  }

  e.target.value = ''
}

const removeImage = (index) => {
  form.value.images.splice(index, 1)
}

const isSubmitting = ref(false)

const handleSubmit = async () => {
  if (!isValid.value) {
    showToast('请填写完整信息')
    return
  }

  isSubmitting.value = true

  try {
    const res = await submitReport(form.value)
    showToast({
      message: '提交成功',
      duration: 1500
    })
    setTimeout(() => {
      router.push({
        path: '/success',
        query: {
          id: res.id,
          createTime: res.createTime
        }
      })
    }, 1500)
  } catch (error) {
    showToast('提交失败，请稍后重试')
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.submit-page {
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

.form-content {
  padding: 120px 24px 40px;
}

.form-card {
  background: #fff;
  border-radius: 24px;
  padding: 32px;
  margin-bottom: 32px;
}

.form-item {
  margin-bottom: 32px;
}

.form-item:last-child {
  margin-bottom: 0;
}

.form-label {
  font-size: 26px;
  font-weight: 500;
  color: #333;
  margin-bottom: 12px;
  display: block;
}

.required {
  color: #ff4d4f;
  margin-right: 4px;
}

.form-input {
  width: 100%;
  height: 88px;
  padding: 0 24px;
  background: #f8f9fa;
  border: 2px solid #e8e8e8;
  border-radius: 16px;
  font-size: 28px;
  color: #333;
}

.form-input:focus {
  border-color: #1890ff;
  outline: none;
}

.form-input::placeholder {
  color: #999;
}

.form-textarea {
  width: 100%;
  padding: 24px;
  background: #f8f9fa;
  border: 2px solid #e8e8e8;
  border-radius: 16px;
  font-size: 28px;
  color: #333;
  resize: none;
}

.form-textarea:focus {
  border-color: #1890ff;
  outline: none;
}

.form-textarea::placeholder {
  color: #999;
}

.image-upload {
  margin-top: 12px;
}

.upload-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.upload-item {
  width: calc((100% - 48px) / 4);
  aspect-ratio: 1;
  border-radius: 12px;
  position: relative;
  overflow: hidden;
}

.upload-item.has-image {
  background: #f8f9fa;
}

.upload-item.has-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-btn {
  position: absolute;
  top: -4px;
  right: -4px;
  width: 40px;
  height: 40px;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.remove-btn span {
  font-size: 28px;
  color: #fff;
}

.upload-btn {
  background: #f8f9fa;
  border: 2px dashed #d9d9d9;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.upload-icon {
  font-size: 40px;
  color: #999;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 22px;
  color: #999;
}

.upload-hint {
  font-size: 22px;
  color: #999;
  margin-top: 12px;
}

.submit-btn-wrap {
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

.footer-info {
  text-align: center;
  padding: 32px 0;
}

.footer-info p {
  font-size: 24px;
  color: #999;
}

.hidden-input {
  display: none;
}
</style>
