<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <div class="stat-item">
            <div class="stat-icon" style="background: #409EFF;">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-label">待办工单</p>
              <p class="stat-number">{{ stats.todo }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <div class="stat-item">
            <div class="stat-icon" style="background: #67C23A;">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-label">今日上报</p>
              <p class="stat-number">{{ stats.today }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <div class="stat-item">
            <div class="stat-icon" style="background: #E6A23C;">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-label">已处理</p>
              <p class="stat-number">{{ stats.done }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近工单 -->
    <el-row style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <el-tabs v-model="activeTab" @tab-change="handleTabChange">
            <el-tab-pane label="最近待办" name="todo">
              <el-table :data="recentTodo" size="small" :header-cell-style="headerStyle" v-loading="loading">
                <el-table-column type="index" label="序号" width="60" />
                <el-table-column prop="id" label="编号" width="100" />
                <el-table-column prop="reporter" label="上报人" width="100" />
                <el-table-column prop="description" label="问题描述" min-width="200" show-overflow-tooltip />
                <el-table-column prop="phone" label="联系方式" width="130" />
                <el-table-column prop="address" label="地址" min-width="150" show-overflow-tooltip />
                <el-table-column prop="createTime" label="上报时间" width="160" :formatter="(row) => formatDate(row.createTime)" />
              </el-table>
            </el-tab-pane>

            <el-tab-pane label="最近审核" name="audit">
              <el-table :data="recentAudit" size="small" :header-cell-style="headerStyle" v-loading="loading">
                <el-table-column type="index" label="序号" width="60" />
                <el-table-column prop="id" label="编号" width="100" />
                <el-table-column prop="reporter" label="上报人" width="100" />
                <el-table-column prop="description" label="问题描述" min-width="200" show-overflow-tooltip />
                <el-table-column prop="phone" label="联系方式" width="130" />
                <el-table-column prop="address" label="地址" min-width="150" show-overflow-tooltip />
                <el-table-column prop="createTime" label="上报时间" width="160" :formatter="(row) => formatDate(row.createTime)" />
              </el-table>
            </el-tab-pane>

            <el-tab-pane label="最近已办" name="done">
              <el-table :data="recentDone" size="small" :header-cell-style="headerStyle" v-loading="loading">
                <el-table-column type="index" label="序号" width="60" />
                <el-table-column prop="id" label="编号" width="100" />
                <el-table-column prop="reporter" label="上报人" width="100" />
                <el-table-column prop="description" label="问题描述" min-width="200" show-overflow-tooltip />
                <el-table-column prop="phone" label="联系方式" width="130" />
                <el-table-column prop="address" label="地址" min-width="150" show-overflow-tooltip />
                <el-table-column prop="completeTime" label="完成时间" width="160" :formatter="(row) => formatDate(row.completeTime)" />
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Document, TrendCharts, CircleCheck } from '@element-plus/icons-vue'
import { getDashboardSummary, getTodoList, getAuditList, getDoneList } from '@/api/workorder'
import { formatDate } from '@/utils/format'

const activeTab = ref('todo')
const loading = ref(false)
const stats = ref({ todo: 0, today: 0, done: 0 })
const recentTodo = ref([])
const recentAudit = ref([])
const recentDone = ref([])

const headerStyle = {
  backgroundColor: '#f5f7fa',
  color: '#606266',
  fontWeight: 'bold'
}

const loadStats = async () => {
  try {
    const data = await getDashboardSummary()
    stats.value = data?.stats || { todo: 0, today: 0, done: 0 }
  } catch (err) {
    console.error('获取统计失败:', err)
  }
}

const loadList = async (tabName) => {
  loading.value = true
  try {
    const params = { pageSize: 5, page: 1 }
    let res

    switch (tabName) {
      case 'todo':
        res = await getTodoList(params)
        recentTodo.value = res.list || []
        break
      case 'audit':
        res = await getAuditList(params)
        recentAudit.value = res.list || []
        break
      case 'done':
        res = await getDoneList(params)
        recentDone.value = res.list || []
        break
    }
  } catch (err) {
    console.error(`获取${tabName}列表失败:`, err)
  } finally {
    loading.value = false
  }
}

const handleTabChange = (tabName) => {
  loadList(tabName)
}

onMounted(() => {
  loadStats()
  loadList('todo')
})
</script>

<style scoped>
.stat-item { display: flex; align-items: center; }
.stat-icon {
  width: 60px; height: 60px; border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
  color: #fff; font-size: 28px;
}
.stat-info { margin-left: 15px; }
.stat-label { color: #909399; font-size: 14px; margin: 0; }
.stat-number { color: #303133; font-size: 28px; font-weight: bold; margin: 5px 0 0; }
</style>