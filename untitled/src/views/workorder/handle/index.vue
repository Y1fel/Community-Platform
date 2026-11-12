<template>
  <div>
    <!-- 工单信息（只读） -->
    <el-card style="margin-bottom: 20px;">
      <template #header>
        <span>工单信息</span>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="工单编号">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="上报人">{{ detail.reporter }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detail.phone }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag type="warning">待处理</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="家庭住址" :span="2">{{ detail.address }}</el-descriptions-item>
        <el-descriptions-item label="问题描述" :span="2">{{ detail.description }}</el-descriptions-item>
        <el-descriptions-item label="上报时间">{{ formatDate(detail.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="图片" :span="2" v-if="detail.images && detail.images.length">
          <el-image
              v-for="(img, index) in detail.images"
              :key="index"
              :src="img"
              style="width: 100px; height: 100px; margin-right: 10px; border-radius: 4px;"
              :preview-src-list="detail.images"
              fit="cover"
          />
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 处理表单 -->
    <el-card>
      <template #header>
        <span>处理工单</span>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="处理方式" prop="handleType">
          <el-radio-group v-model="form.handleType">
            <el-radio label="phone">电话联系</el-radio>
            <el-radio label="visit">实地拜访</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理结果" prop="result">
          <el-input
              v-model="form.result"
              type="textarea"
              :rows="4"
              placeholder="请填写处理结果，包括联系情况、处理措施等..."
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit" :loading="submitting">提交处理结果</el-button>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getWorkorderDetail, handleWorkorder } from '@/api/workorder'
import { formatDate } from '@/utils/format'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const submitting = ref(false)

const detail = ref({})
const form = ref({
  handleType: 'phone',
  result: ''
})

const rules = {
  handleType: [{ required: true, message: '请选择处理方式', trigger: 'change' }],
  result: [{ required: true, message: '请填写处理结果', trigger: 'blur' }]
}

const loadDetail = async () => {
  const id = route.params.id
  try {
    const res = await getWorkorderDetail(id)
    detail.value = res || {}
  } catch (err) {
    console.error('获取工单详情失败:', err)
  }
}

const submit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    await handleWorkorder(route.params.id, {
      handleType: form.value.handleType,
      result: form.value.result
    })
    ElMessage.success('提交成功，进入审核流程')
    router.push('/workorder/todo')
  } catch (err) {
    ElMessage.error('提交失败')
    console.error(err)
  } finally {
    submitting.value = false
  }
}

const goBack = () => {
  router.back()
}

onMounted(loadDetail)
</script>