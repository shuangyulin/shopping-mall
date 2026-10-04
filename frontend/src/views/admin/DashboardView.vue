<script setup>
import { onMounted, ref } from 'vue'
import api from '../../api'
import { money } from '../../utils/format'

const stats = ref({ userCount: 0, productCount: 0, orderCount: 0, pendingOrderCount: 0, salesAmount: 0 })
const loading = ref(true)

onMounted(async () => {
  try {
    stats.value = await api.get('/admin/dashboard')
  } catch (error) {
    window.alert(error.message)
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div>
    <div class="admin-page-head">
      <div>
        <span>OVERVIEW</span>
        <h1>数据概览</h1>
      </div>
      <p>查看商城当前的基础运营数据。</p>
    </div>
    <div v-if="loading" class="loading-state">数据加载中...</div>
    <div v-else class="stats-grid">
      <article>
        <span>注册用户</span>
        <strong>{{ stats.userCount }}</strong>
        <small>累计用户数</small>
      </article>
      <article>
        <span>商品总数</span>
        <strong>{{ stats.productCount }}</strong>
        <small>含下架商品</small>
      </article>
      <article>
        <span>订单总数</span>
        <strong>{{ stats.orderCount }}</strong>
        <small>{{ stats.pendingOrderCount }} 个待付款</small>
      </article>
      <article class="accent">
        <span>有效订单金额</span>
        <strong>{{ money(stats.salesAmount) }}</strong>
        <small>不包含已取消订单</small>
      </article>
    </div>
    <section class="admin-tip">
      <div>
        <h2>管理提示</h2>
        <p>商品下架后不会在商城前台显示，但历史订单记录不会被删除。</p>
      </div>
      <div class="quick-links">
        <RouterLink to="/admin/products">管理商品 →</RouterLink>
        <RouterLink to="/admin/orders">处理订单 →</RouterLink>
      </div>
    </section>
  </div>
</template>
