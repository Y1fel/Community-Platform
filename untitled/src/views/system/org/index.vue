<template>
  <div>
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">新增组织</el-button>
      <el-input v-model="searchKey" placeholder="搜索组织" clearable style="width: 200px; margin-left: 10px" />
      <el-button @click="loadData">搜索</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" row-key="id" border default-expand-all>
      <el-table-column prop="name" label="组织名称" min-width="200" />
      <el-table-column prop="code" label="组织编码" width="150" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑组织' : '新增组织'" width="500px">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="上级组织">
          <el-tree-select
              v-model="form.parentId"
              :data="tableData"
              :props="{ label: 'name', value: 'id' }"
              check-strictly
              clearable
              placeholder="不选则为顶级组织"
          />
        </el-form-item>
        <el-form-item label="组织名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="组织编码" prop="code">
          <el-input v-model="form.code" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
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
import { getOrgList, addOrg, updateOrg, deleteOrg } from '@/api/system'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const searchKey = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const form = ref({
  id: null,
  parentId: null,
  name: '',
  code: '',
  sort: 0
})

const formRules = {
  name: [{ required: true, message: '请输入组织名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入组织编码', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getOrgList()
    tableData.value = res || []
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  form.value = { id: null, parentId: null, name: '', code: '', sort: 0 }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该组织？', '提示', { type: 'warning' })
      .then(async () => {
        await deleteOrg(row.id)
        ElMessage.success('删除成功')
        loadData()
      })
}

const submitForm = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await updateOrg(form.value.id, form.value)
  } else {
    await addOrg(form.value)
  }
  ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
  dialogVisible.value = false
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.toolbar { margin-bottom: 20px; }
</style>
