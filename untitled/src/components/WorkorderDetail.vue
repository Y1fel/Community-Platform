<template>
  <el-dialog v-model="visible" title="工单详情" width="650px">
    <el-descriptions :column="2" border>
      <el-descriptions-item label="工单编号">{{ data.id }}</el-descriptions-item>
      <el-descriptions-item label="上报人">{{ data.reporter }}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{ data.phone }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="statusType">{{ statusText }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="家庭住址" :span="2">{{ data.address }}</el-descriptions-item>
      <el-descriptions-item label="问题描述" :span="2">{{ data.description }}</el-descriptions-item>
      <el-descriptions-item label="上报时间">{{ formatDate(data.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="完成时间" v-if="data.completeTime">{{ formatDate(data.completeTime) }}</el-descriptions-item>
      <el-descriptions-item label="图片" :span="2" v-if="data.images && data.images.length">
        <el-image
            v-for="(img, index) in data.images"
            :key="index"
            :src="img"
            style="width: 100px; height: 100px; margin-right: 10px; border-radius: 4px;"
            :preview-src-list="data.images"
            fit="cover"
        />
      </el-descriptions-item>
      <el-descriptions-item label="处理方式" v-if="data.handleType">
        {{ data.handleType === 'phone' ? '电话联系' : '实地拜访' }}
      </el-descriptions-item>
      <el-descriptions-item label="处理人" v-if="data.handlerName">{{ data.handlerName }}</el-descriptions-item>
      <el-descriptions-item label="处理结果" :span="2" v-if="data.handleResult">{{ data.handleResult }}</el-descriptions-item>
      <el-descriptions-item label="审核人" v-if="data.auditorName">{{ data.auditorName }}</el-descriptions-item>
      <el-descriptions-item label="驳回原因" :span="2" v-if="data.rejectReason">
        <span style="color: #F56C6C;">{{ data.rejectReason }}</span>
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { formatDate } from '@/utils/format'
import { getWorkorderDetail } from '@/api/workorder'

const visible = ref(false)
const data = ref({})

const statusMap = {
  pending: { text: '待处理', type: 'warning' },
  auditing: { text: '审核中', type: 'primary' },
  done: { text: '已处理', type: 'success' }
}

const statusText = computed(() => statusMap[data.value.status]?.text || data.value.status)
const statusType = computed(() => statusMap[data.value.status]?.type || 'info')

const open = async (row) => {
  visible.value = true
  data.value = row
  try {
    data.value = await getWorkorderDetail(row.id)
  } catch (error) {
    ElMessage.error('工单详情加载失败')
  }
}

defineExpose({ open })
</script>
