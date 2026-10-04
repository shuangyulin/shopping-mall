<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useCartStore } from '../stores/cart'

const router = useRouter()
const auth = useAuthStore()
const cart = useCartStore()
const menuOpen = ref(false)
const keyword = ref('')

function search() {
  router.push({ name: 'products', query: keyword.value ? { keyword: keyword.value } : {} })
  menuOpen.value = false
}

function logout() {
  auth.logout()
  cart.reset()
  router.push('/')
  menuOpen.value = false
}
</script>

<template>
  <header class="site-header">
    <div class="container header-inner">
      <RouterLink class="brand" to="/" @click="menuOpen = false">
        <span class="brand-mark">拾</span>
        <span>
          <strong>拾光商城</strong>
          <small>SHIGUANG MALL</small>
        </span>
      </RouterLink>

      <nav :class="['main-nav', { open: menuOpen }]">
        <RouterLink to="/" @click="menuOpen = false">首页</RouterLink>
        <RouterLink to="/products" @click="menuOpen = false">全部商品</RouterLink>
        <RouterLink v-if="auth.isLoggedIn" to="/orders" @click="menuOpen = false">我的订单</RouterLink>
        <RouterLink v-if="auth.isAdmin" to="/admin" @click="menuOpen = false">后台管理</RouterLink>
      </nav>

      <form class="header-search" @submit.prevent="search">
        <input v-model.trim="keyword" placeholder="搜索商品" aria-label="搜索商品" />
        <button type="submit" aria-label="搜索">⌕</button>
      </form>

      <div class="header-actions">
        <RouterLink v-if="auth.isLoggedIn" class="user-link" to="/orders">
          {{ auth.user?.nickname || auth.user?.username }}
        </RouterLink>
        <RouterLink v-else class="user-link" to="/login">登录 / 注册</RouterLink>
        <RouterLink class="cart-link" to="/cart">
          <span>购物车</span>
          <b v-if="cart.totalQuantity">{{ cart.totalQuantity }}</b>
        </RouterLink>
        <button v-if="auth.isLoggedIn" class="text-button" @click="logout">退出</button>
        <button class="menu-button" @click="menuOpen = !menuOpen">菜单</button>
      </div>
    </div>
  </header>
</template>
