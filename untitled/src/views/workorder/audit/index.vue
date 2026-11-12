<template>
  <div>
    <!-- 查询条件 -->
    <el-form :model="query" inline class="search-form">
      <el-form-item label="关键字">
        <el-input v-model="query.keyword" placeholder="问题描述" clearable />
      </el-form-item>
      <el-form-item label="上报人">
        <el-input v-model="query.reporter" clearable />
      </el-form-item>
      <el-form-item label="联系方式">
        <el-input v-model="query.phone" clearable />
      </el-form-item>
      <el-form-item label="上报时间">
        <el-date-picker
            v-model="query.timeRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格 -->
    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="id" label="工单编号" width="120" />
      <el-table-column prop="reporter" label="上报人" width="100" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="address" label="家庭住址" min-width="200" show-overflow-tooltip />
      <el-table-column prop="description" label="问题描述" min-width="250" show-overflow-tooltip />
      <el-table-column prop="createTime" label="上报时间" width="160" :formatter="(row) => formatDate(row.createTime)" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="success" size="small" @click="handlePass(row)">通过</el-button>
          <el-button type="danger" size="small" @click="handleReject(row)">驳回</el-button>
          <el-button size="small" @click="viewDetail(row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
    />

    <!-- 详情弹窗 -->
    <WorkorderDetail ref="detailRef" />

    <!-- 驳回弹窗 -->
    <el-dialog v-model="rejectVisible" title="驳回原因" width="400px">
      <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请输入驳回原因" />
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" @click="submitReject">确认驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAuditList, auditPass, auditReject } from '@/api/workorder'
import { ElMessage, ElMessageBox } from 'element-plus'
import WorkorderDetail from '@/components/WorkorderDetail.vue'
import { formatDate } from '@/utils/format'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const query = ref({
  keyword: '',
  reporter: '',
  phone: '',
  timeRange: []
})
const detailRef = ref()
const rejectVisible = ref(false)
const rejectReason = ref('')
const currentRow = ref(null)

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value,
      keyword: query.value.keyword,
      reporter: query.value.reporter,
      phone: query.value.phone
    }
    if (query.value.timeRange && query.value.timeRange.length === 2) {
      params.startTime = query.value.timeRange[0]
      params.endTime = query.value.timeRange[1]
    }
    const res = await getAuditList(params)
    list.value = res.list || []
    total.value = res.total || 0
  } catch (err) {
    console.error('获取审核列表失败:', err)
  } finally {
    loading.value = false
  }
}

const search = () => {
  page.value = 1
  loadData()
}

const reset = () => {
  query.value = { keyword: '', reporter: '', phone: '', timeRange: [] }
  search()
}

const handlePass = async (row) => {
  await ElMessageBox.confirm('确认通过该工单？', '提示', { type: 'warning' })
  await auditPass(row.id)
  ElMessage.success('审核通过')
  loadData()
}

const handleReject = (row) => {
  currentRow.value = row
  rejectReason.value = ''
  rejectVisible.value = true
}

const submitReject = async () => {
  if (!rejectReason.value.trim()) {
    ElMessage.error('请输入驳回原因')
    return
  }
  await auditReject(currentRow.value.id, { reason: rejectReason.value })
  ElMessage.success('已驳回')
  rejectVisible.value = false
  loadData()
}

const viewDetail = (row) => {
  detailRef.value.open(row)
}

onMounted(loadData)
</script>

<style scoped>
.search-form { margin-bottom: 20px; }
</style>