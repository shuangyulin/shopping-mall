<script setup>
import { onMounted, reactive, ref } from 'vue'
import api from '../api'
import { datetime, money } from '../utils/format'

const orders = ref([])
const loading = ref(true)
const pagination = reactive({ page: 0, size: 10, totalPages: 0 })

async function load(page = 0) {
  loading.value = true
  try {
    const data = await api.get('/orders', { params: { page, size: pagination.size } })
    orders.value = data.content
    pagination.page = data.page
    pagination.totalPages = data.totalPages
  } finally {
    loading.value = false
  }
}

onMounted(() => load())
</script>

<template>
  <div class="page-wrap">
    <div class="page-hero compact">
      <div class="container">
        <span class="eyebrow">MY ORDERS</span>
        <h1>我的订单</h1>
      </div>
    </div>
    <div class="container">
      <div v-if="loading" class="loading-state">订单加载中...</div>
      <div v-else-if="!orders.length" class="empty-state">
        <b>还没有订单</b>
        <p>选购商品并提交订单后，会在这里显示。</p>
      </div>
      <div v-else class="order-list">
        <article v-for="order in orders" :key="order.orderNo" class="order-card">
          <header>
            <div>
              <strong>订单号 {{ order.orderNo }}</strong>
              <span>{{ datetime(order.createdAt) }}</span>
            </div>
            <b :class="`status-${order.status.toLowerCase()}`">{{ order.statusText }}</b>
          </header>
          <div class="order-products">
            <img v-for="item in order.items.slice(0, 4)" :key="item.productId" :src="item.productCover" :alt="item.productName" />
            <span v-if="order.items.length > 4">+{{ order.items.length - 4 }}</span>
          </div>
          <footer>
            <span>共 {{ order.items.reduce((sum, item) => sum + item.quantity, 0) }} 件</span>
            <span>实付 <strong>{{ money(order.totalAmount) }}</strong></span>
            <RouterLink class="btn btn-outline btn-small" :to="`/orders/${order.orderNo}`">查看详情</RouterLink>
          </footer>
        </article>
      </div>
      <div v-if="pagination.totalPages > 1" class="pagination">
        <button :disabled="pagination.page === 0" @click="load(pagination.page - 1)">上一页</button>
        <span>第 {{ pagination.page + 1 }} / {{ pagination.totalPages }} 页</span>
        <button :disabled="pagination.page + 1 >= pagination.totalPages" @click="load(pagination.page + 1)">下一页</button>
      </div>
    </div>
  </div>
</template>
