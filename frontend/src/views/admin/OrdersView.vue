<script setup>
import { onMounted, reactive, ref } from 'vue'
import api from '../../api'
import { datetime, money } from '../../utils/format'

const orders = ref([])
const loading = ref(true)
const filter = reactive({ keyword: '', status: '', page: 0, size: 10, totalPages: 0 })

onMounted(() => load())

async function load(page = 0) {
  loading.value = true
  try {
    const data = await api.get('/admin/orders', {
      params: {
        keyword: filter.keyword || undefined,
        status: filter.status || undefined,
        page,
        size: filter.size
      }
    })
    orders.value = data.content
    filter.page = data.page
    filter.totalPages = data.totalPages
  } finally {
    loading.value = false
  }
}

async function updateStatus(order, status) {
  try {
    await api.patch(`/admin/orders/${order.orderNo}/status`, { status })
    await load(filter.page)
  } catch (error) {
    window.alert(error.message)
  }
}
</script>

<template>
  <div>
    <div class="admin-page-head">
      <div>
        <span>ORDERS</span>
        <h1>订单管理</h1>
      </div>
      <p>查询订单并更新发货、完成等状态。</p>
    </div>

    <div class="admin-toolbar order-filter" @submit.prevent="load(0)">
      <form @submit.prevent="load(0)">
        <input v-model.trim="filter.keyword" placeholder="按订单号搜索" />
        <select v-model="filter.status">
          <option value="">全部状态</option>
          <option value="PENDING_PAYMENT">待付款</option>
          <option value="PAID">待发货</option>
          <option value="SHIPPED">已发货</option>
          <option value="COMPLETED">已完成</option>
          <option value="CANCELLED">已取消</option>
        </select>
        <button class="btn btn-dark" type="submit">查询</button>
      </form>
    </div>

    <div class="admin-table-wrap">
      <table class="admin-table">
        <thead>
          <tr>
            <th>订单信息</th>
            <th>收货人</th>
            <th>商品</th>
            <th>金额</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.orderNo">
            <td><strong>{{ order.orderNo }}</strong><br /><small>{{ datetime(order.createdAt) }}</small></td>
            <td>{{ order.receiverName }}<br /><small>{{ order.receiverPhone }}</small></td>
            <td>{{ order.items.length }} 种 / {{ order.items.reduce((sum, item) => sum + item.quantity, 0) }} 件</td>
            <td>{{ money(order.totalAmount) }}</td>
            <td><span :class="`status-${order.status.toLowerCase()}`">{{ order.statusText }}</span></td>
            <td>
              <select
                v-if="!['COMPLETED', 'CANCELLED'].includes(order.status)"
                :value="order.status"
                @change="updateStatus(order, $event.target.value)"
              >
                <option v-if="order.status === 'PENDING_PAYMENT'" value="PENDING_PAYMENT">待付款</option>
                <option value="PAID">待发货</option>
                <option value="SHIPPED">已发货</option>
                <option value="COMPLETED">已完成</option>
                <option value="CANCELLED">已取消</option>
              </select>
              <span v-else>-</span>
            </td>
          </tr>
          <tr v-if="!loading && !orders.length"><td colspan="6" class="table-empty">暂无订单数据</td></tr>
        </tbody>
      </table>
    </div>

    <div v-if="filter.totalPages > 1" class="pagination">
      <button :disabled="filter.page === 0" @click="load(filter.page - 1)">上一页</button>
      <span>第 {{ filter.page + 1 }} / {{ filter.totalPages }} 页</span>
      <button :disabled="filter.page + 1 >= filter.totalPages" @click="load(filter.page + 1)">下一页</button>
    </div>
  </div>
</template>
