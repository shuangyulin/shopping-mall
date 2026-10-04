<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const auth = useAuthStore()

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <div class="admin-shell">
    <aside class="admin-sidebar">
      <RouterLink class="admin-brand" to="/admin">
        <span class="brand-mark">拾</span>
        <div>
          <strong>拾光商城</strong>
          <small>管理后台</small>
        </div>
      </RouterLink>
      <nav>
        <RouterLink to="/admin" exact-active-class="active">数据概览</RouterLink>
        <RouterLink to="/admin/products" active-class="active">商品管理</RouterLink>
        <RouterLink to="/admin/orders" active-class="active">订单管理</RouterLink>
      </nav>
      <div class="admin-sidebar-bottom">
        <RouterLink to="/">返回商城</RouterLink>
        <button @click="logout">退出登录</button>
      </div>
    </aside>
    <section class="admin-main">
      <header class="admin-topbar">
        <div>
          <span>当前管理员</span>
          <strong>{{ auth.user?.nickname || auth.user?.username }}</strong>
        </div>
      </header>
      <main class="admin-content">
        <RouterView />
      </main>
    </section>
  </div>
</template>
