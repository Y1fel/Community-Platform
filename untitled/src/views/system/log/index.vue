<template>
  <div>
    <el-form :model="query" inline class="search-form">
      <el-form-item label="操作人">
        <el-input v-model="query.operator" clearable />
      </el-form-item>
      <el-form-item label="操作类型">
        <el-select v-model="query.type" placeholder="全部" clearable>
          <el-option label="登录" value="login" />
          <el-option label="操作" value="operation" />
        </el-select>
      </el-form-item>
      <el-form-item label="时间范围">
        <el-date-picker
            v-model="query.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="operator" label="操作人" width="120" />
      <el-table-column prop="type" label="操作类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.type === 'login' ? 'info' : 'success'">
            {{ row.type === 'login' ? '登录' : '操作' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="module" label="操作模块" width="150" />
      <el-table-column prop="content" label="操作内容" min-width="300" show-overflow-tooltip />
      <el-table-column prop="ip" label="IP地址" width="130" />
      <el-table-column prop="createTime" label="操作时间" width="160" />
    </el-table>

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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getLogList } from '@/api/system'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const query = ref({
  operator: '',
  type: '',
  timeRange: []
})

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value,
      operator: query.value.operator,
      type: query.value.type
    }
    if (query.value.timeRange && query.value.timeRange.length === 2) {
      params.startTime = query.value.timeRange[0]
      params.endTime = query.value.timeRange[1]
    }
    const res = await getLogList(params)
    list.value = res.list || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

const search = () => {
  page.value = 1
  loadData()
}

const reset = () => {
  query.value = { operator: '', type: '', timeRange: [] }
  search()
}

onMounted(loadData)
</script>

<style scoped>
.search-form { margin-bottom: 20px; }
</style>
