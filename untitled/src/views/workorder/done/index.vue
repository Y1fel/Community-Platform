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
      <el-form-item label="完成时间">
        <el-date-picker
            v-model="query.completeTimeRange"
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
      <el-table-column prop="completeTime" label="完成时间" width="160" :formatter="(row) => formatDate(row.completeTime)" />
      <el-table-column prop="handleResult" label="处理结果" min-width="200" show-overflow-tooltip />
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
import { getDoneList } from '@/api/workorder'
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
  timeRange: [],
  completeTimeRange: []
})
const detailRef = ref()

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
    if (query.value.completeTimeRange && query.value.completeTimeRange.length === 2) {
      params.completeStartTime = query.value.completeTimeRange[0]
      params.completeEndTime = query.value.completeTimeRange[1]
    }
    const res = await getDoneList(params)
    list.value = res.list || []
    total.value = res.total || 0
  } catch (err) {
    console.error('获取已办列表失败:', err)
  } finally {
    loading.value = false
  }
}

const search = () => {
  page.value = 1
  loadData()
}

const reset = () => {
  query.value = { keyword: '', reporter: '', phone: '', timeRange: [], completeTimeRange: [] }
  search()
}

const viewDetail = (row) => {
  detailRef.value.open(row)
}

onMounted(loadData)
</script>

<style scoped>
.search-form { margin-bottom: 20px; }
</style>