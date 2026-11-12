<template>
  <div>
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">新增用户</el-button>
      <el-button type="danger" @click="handleBatchDelete" :disabled="!selectedRows.length">批量删除</el-button>
      <el-input v-model="query.keyword" placeholder="用户名/手机号" clearable style="width: 200px; margin-left: 10px" />
      <el-button @click="search">搜索</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="真实姓名" width="120" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="orgName" label="所属组织" width="150" />
      <el-table-column prop="role" label="角色" width="100" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 'enabled' ? 'success' : 'danger'">
            {{ row.status === 'enabled' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="500px">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" maxlength="11" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="所属组织" prop="orgId">
          <el-select v-model="form.orgId" placeholder="请选择">
            <el-option v-for="item in orgList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择">
            <el-option label="管理员" value="admin" />
            <el-option label="社区工作人员" value="staff" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserList, addUser, updateUser, deleteUser, batchDeleteUser } from '@/api/system'
import { getOrgList } from '@/api/system'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const query = ref({ keyword: '' })
const selectedRows = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const orgList = ref([])
const formRef = ref()

const form = ref({
  id: null,
  username: '',
  realName: '',
  phone: '',
  password: '',
  orgId: '',
  role: ''
})

const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式错误', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  orgId: [{ required: true, message: '请选择所属组织', trigger: 'change' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      page: page.value,
      pageSize: pageSize.value,
      keyword: query.value.keyword
    })
    list.value = res.list || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

const loadOrgList = async () => {
  const res = await getOrgList()
  orgList.value = res || []
}

const search = () => {
  page.value = 1
  loadData()
}

const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

const handleAdd = () => {
  isEdit.value = false
  form.value = { id: null, username: '', realName: '', phone: '', password: '', orgId: '', role: '' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.value = { ...row, password: '' }
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该用户？', '提示', { type: 'warning' })
      .then(async () => {
        await deleteUser(row.id)
        ElMessage.success('删除成功')
        loadData()
      })
}

const handleBatchDelete = () => {
  const ids = selectedRows.value.map(row => row.id)
  ElMessageBox.confirm(`确认删除选中的 ${ids.length} 个用户？`, '提示', { type: 'warning' })
      .then(async () => {
        await batchDeleteUser(ids)
        ElMessage.success('批量删除成功')
        loadData()
      })
}

const submitForm = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await updateUser(form.value.id, form.value)
  } else {
    await addUser(form.value)
  }
  ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
  dialogVisible.value = false
  loadData()
}

onMounted(() => {
  loadData()
  loadOrgList()
})
</script>

<style scoped>
.toolbar { margin-bottom: 20px; }
</style>
