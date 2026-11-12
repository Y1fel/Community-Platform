<template>
  <div>
    <!-- 搜索表单 -->
    <AppSearchForm :model="searchForm" @search="loadData" @reset="resetSearch">
      <el-form-item label="关键字">
        <el-input v-model="searchForm.keyword" placeholder="问题描述" clearable />
      </el-form-item>
      <el-form-item label="上报人">
        <el-input v-model="searchForm.reporter" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="联系方式">
        <el-input v-model="searchForm.phone" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px;">
          <el-option label="待处理" value="pending" />
          <el-option label="审核中" value="auditing" />
          <el-option label="已处理" value="done" />
        </el-select>
      </el-form-item>
      <el-form-item label="上报时间">
        <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item label="完成时间">
        <el-date-picker
            v-model="completeDateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
        />
      </el-form-item>
    </AppSearchForm>

    <!-- 表格 -->
    <el-table :data="list" v-loading="loading" border>
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="id" label="工单编号" width="120" />
      <el-table-column prop="reporter" label="上报人" width="100" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="address" label="家庭住址" min-width="200" show-overflow-tooltip />
      <el-table-column prop="description" label="问题描述" min-width="250" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="上报时间" width="160" :formatter="(row) => formatDate(row.createTime)" />
      <el-table-column prop="completeTime" label="完成时间" width="160" :formatter="(row) => formatDate(row.completeTime)" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import AppSearchForm from '@/components/AppSearchForm.vue'
import WorkorderDetail from '@/components/WorkorderDetail.vue'
import { queryWorkorderList } from '@/api/workorder'
import { formatDate } from '@/utils/format'

const loading = ref(false)
const list = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dateRange = ref([])
const completeDateRange = ref([])

const searchForm = ref({
  keyword: '',
  reporter: '',
  phone: '',
  status: ''
})

const detailRef = ref()

const statusMap = {
  pending: { text: '待处理', type: 'warning' },
  auditing: { text: '审核中', type: 'primary' },
  done: { text: '已处理', type: 'success' }
}

const statusText = (status) => statusMap[status]?.text || status
const statusType = (status) => statusMap[status]?.type || 'info'

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value,
      ...searchForm.value,
      startTime: dateRange.value?.[0],
      endTime: dateRange.value?.[1],
      completeStartTime: completeDateRange.value?.[0],
      completeEndTime: completeDateRange.value?.[1]
    }
    const res = await queryWorkorderList(params)
    list.value = res.list || []
    total.value = res.total || 0
  } catch (err) {
    console.error('获取工单列表失败:', err)
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.value = { keyword: '', reporter: '', phone: '', status: '' }
  dateRange.value = []
  completeDateRange.value = []
  page.value = 1
  loadData()
}

const viewDetail = (row) => {
  detailRef.value.open(row)
}

onMounted(loadData)
</script>