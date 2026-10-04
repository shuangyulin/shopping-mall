<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import api from '../api'
import { datetime, money } from '../utils/format'

const route = useRoute()
const order = ref(null)
const loading = ref(true)
const operating = ref(false)

async function load() {
  loading.value = true
  try {
    order.value = await api.get(`/orders/${route.params.orderNo}`)
  } catch (error) {
    window.alert(error.message)
  } finally {
    loading.value = false
  }
}

async function operate(action) {
  const label = action === 'pay' ? '确认模拟支付该订单吗？' : '确定取消该订单吗？'
  if (!window.confirm(label)) return
  operating.value = true
  try {
    order.value = await api.post(`/orders/${order.value.orderNo}/${action}`)
  } catch (error) {
    window.alert(error.message)
  } finally {
    operating.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="page-wrap">
    <div class="page-hero compact">
      <div class="container">
        <span class="eyebrow">ORDER DETAIL</span>
        <h1>订单详情</h1>
      </div>
    </div>
    <div v-if="loading" class="container loading-state">加载中...</div>
    <div v-else-if="order" class="container detail-order-layout">
      <section class="panel">
        <div class="order-detail-head">
          <div>
            <span>订单编号</span>
            <strong>{{ order.orderNo }}</strong>
            <small>{{ datetime(order.createdAt) }}</small>
          </div>
          <b :class="`status-${order.status.toLowerCase()}`">{{ order.statusText }}</b>
        </div>
        <div class="order-items">
          <article v-for="item in order.items" :key="item.productId">
            <img :src="item.productCover" :alt="item.productName" />
            <div>
              <strong>{{ item.productName }}</strong>
              <span>{{ money(item.price) }} × {{ item.quantity }}</span>
            </div>
            <b>{{ money(item.subtotal) }}</b>
          </article>
        </div>
        <div class="order-total">
          <span>订单金额</span>
          <strong>{{ money(order.totalAmount) }}</strong>
        </div>
      </section>

      <aside class="panel">
        <h3>收货信息</h3>
        <dl class="info-list">
          <dt>收货人</dt><dd>{{ order.receiverName }}</dd>
          <dt>手机号</dt><dd>{{ order.receiverPhone }}</dd>
          <dt>收货地址</dt><dd>{{ order.receiverAddress }}</dd>
          <dt>备注</dt><dd>{{ order.remark || '无' }}</dd>
        </dl>
        <div v-if="order.status === 'PENDING_PAYMENT'" class="order-actions">
          <button class="btn btn-outline" :disabled="operating" @click="operate('cancel')">取消订单</button>
          <button class="btn btn-primary" :disabled="operating" @click="operate('pay')">模拟支付</button>
        </div>
        <p class="form-hint">本项目按毕业设计范围模拟支付，不接入真实支付渠道。</p>
      </aside>
    </div>
  </div>
</template>
