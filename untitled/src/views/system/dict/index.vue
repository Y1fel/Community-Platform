<template>
  <div>
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">新增字典</el-button>
      <el-input v-model="query.keyword" placeholder="字典类型/名称" clearable style="width: 200px; margin-left: 10px" />
      <el-button @click="search">搜索</el-button>
    </div>

    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="dictType" label="字典类型" width="150" />
      <el-table-column prop="dictName" label="字典名称" width="150" />
      <el-table-column prop="dictCode" label="字典编码" width="150" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑字典' : '新增字典'" width="500px">
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="form.dictType" placeholder="如：task_type" />
        </el-form-item>
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="form.dictName" placeholder="如：任务类型" />
        </el-form-item>
        <el-form-item label="字典编码" prop="dictCode">
          <el-input v-model="form.dictCode" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
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
import { getDictList, addDict, updateDict, deleteDict } from '@/api/system'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const query = ref({ keyword: '' })
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const form = ref({
  id: null,
  dictType: '',
  dictName: '',
  dictCode: '',
  sort: 0,
  remark: ''
})

const formRules = {
  dictType: [{ required: true, message: '请输入字典类型', trigger: 'blur' }],
  dictName: [{ required: true, message: '请输入字典名称', trigger: 'blur' }],
  dictCode: [{ required: true, message: '请输入字典编码', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDictList({
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

const search = () => {
  page.value = 1
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  form.value = { id: null, dictType: '', dictName: '', dictCode: '', sort: 0, remark: '' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该字典？', '提示', { type: 'warning' })
      .then(async () => {
        await deleteDict(row.id)
        ElMessage.success('删除成功')
        loadData()
      })
}

const submitForm = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await updateDict(form.value.id, form.value)
  } else {
    await addDict(form.value)
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
