<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'
import { useCartStore } from '../stores/cart'
import { money } from '../utils/format'

const router = useRouter()
const cart = useCartStore()
const submitting = ref(false)
const error = ref('')
const form = reactive({
  receiverName: '',
  receiverPhone: '',
  receiverAddress: '',
  remark: ''
})

onMounted(() => cart.fetchCart())

async function submitOrder() {
  if (!cart.cart.items.length) {
    error.value = '购物车为空'
    return
  }
  submitting.value = true
  error.value = ''
  try {
    const order = await api.post('/orders', form)
    await cart.fetchCart()
    router.replace({ name: 'order-detail', params: { orderNo: order.orderNo } })
  } catch (exception) {
    error.value = exception.message
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="page-wrap">
    <div class="page-hero compact">
      <div class="container">
        <span class="eyebrow">CHECKOUT</span>
        <h1>确认订单</h1>
      </div>
    </div>
    <div class="container checkout-layout">
      <form class="checkout-form panel" @submit.prevent="submitOrder">
        <h2>收货信息</h2>
        <div class="form-row">
          <label>
            <span>收货人 *</span>
            <input v-model.trim="form.receiverName" required maxlength="50" placeholder="请输入姓名" />
          </label>
          <label>
            <span>手机号码 *</span>
            <input v-model.trim="form.receiverPhone" required pattern="1[0-9]{10}" placeholder="11位手机号" />
          </label>
        </div>
        <label>
          <span>详细地址 *</span>
          <input v-model.trim="form.receiverAddress" required maxlength="300" placeholder="省 / 市 / 区 / 详细地址" />
        </label>
        <label>
          <span>订单备注</span>
          <textarea v-model.trim="form.remark" maxlength="300" rows="4" placeholder="选填，如配送时间要求"></textarea>
        </label>
        <p v-if="error" class="form-error">{{ error }}</p>
        <button class="btn btn-primary btn-block" :disabled="submitting">
          {{ submitting ? '正在提交...' : '提交订单' }}
        </button>
        <p class="form-hint center">提交后进入待付款状态，可在订单详情中模拟支付。</p>
      </form>

      <aside class="checkout-summary panel">
        <h2>商品清单</h2>
        <div v-for="line in cart.cart.items" :key="line.id" class="checkout-line">
          <img :src="line.cover" :alt="line.productName" />
          <div>
            <strong>{{ line.productName }}</strong>
            <span>{{ money(line.price) }} × {{ line.quantity }}</span>
          </div>
          <b>{{ money(line.subtotal) }}</b>
        </div>
        <div class="summary-total">
          <span>合计</span>
          <strong>{{ money(cart.cart.totalAmount) }}</strong>
        </div>
      </aside>
    </div>
  </div>
</template>
