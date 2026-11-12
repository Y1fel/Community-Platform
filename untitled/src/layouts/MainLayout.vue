<template>
  <el-container class="layout">
    <el-aside width="200px" class="sidebar">
      <div class="logo">社区管理平台</div>
      <el-menu
          router
          :default-active="$route.path"
          background-color="#304156"
          text-color="#fff"
          active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-sub-menu index="/workorder">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>事件中心</span>
          </template>
          <el-menu-item index="/workorder/todo">待办工单</el-menu-item>
          <el-menu-item index="/workorder/audit">审核工单</el-menu-item>
          <el-menu-item index="/workorder/done">已办工单</el-menu-item>
          <el-menu-item index="/workorder/query">工单查询</el-menu-item>
        </el-sub-menu>
        <!-- 系统管理后端未实现，暂时隐藏 -->
        <!--
        <el-sub-menu index="/system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/org">组织管理</el-menu-item>
          <el-menu-item index="/system/user">用户管理</el-menu-item>
          <el-menu-item index="/system/dict">数据字典</el-menu-item>
          <el-menu-item index="/system/log">日志管理</el-menu-item>
        </el-sub-menu>
        -->
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span>{{ $route.meta.title }}</span>
        <div class="user-info">
          <span>{{ userName }}</span>
          <el-button link @click="logout">退出</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { HomeFilled, Document } from '@element-plus/icons-vue'  // 去掉 Setting

const route = useRoute()
const router = useRouter()
const userName = ref(localStorage.getItem('userName') || '管理员')

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userName')
  router.push('/login')
}
</script>

<style scoped>
.layout { height: 100vh; }
.sidebar { background: #304156; }
.logo {
  color: #fff;
  text-align: center;
  padding: 20px;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #1f2d3d;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
  background: #fff;
}
.user-info { display: flex; align-items: center; gap: 15px; }
</style>