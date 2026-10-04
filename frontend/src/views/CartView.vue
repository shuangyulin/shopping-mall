<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'
import { money } from '../utils/format'

const router = useRouter()
const cart = useCartStore()

onMounted(() => cart.fetchCart())

async function changeQuantity(line, delta) {
  const quantity = line.quantity + delta
  if (quantity < 1 || quantity > line.stock) return
  try {
    await cart.update(line.id, quantity)
  } catch (error) {
    window.alert(error.message)
  }
}

async function remove(itemId) {
  if (!window.confirm('确定从购物车移除该商品吗？')) return
  await cart.remove(itemId)
}
</script>

<template>
  <div class="page-wrap">
    <div class="page-hero compact">
      <div class="container">
        <span class="eyebrow">SHOPPING CART</span>
        <h1>我的购物车</h1>
      </div>
    </div>
    <div class="container cart-layout">
      <section class="cart-list">
        <div v-if="cart.loading" class="loading-state">购物车加载中...</div>
        <div v-else-if="!cart.cart.items.length" class="empty-state">
          <b>购物车还是空的</b>
          <p>去挑选几件喜欢的商品吧。</p>
          <RouterLink class="btn btn-primary" to="/products">去购物</RouterLink>
        </div>
        <article v-for="line in cart.cart.items" v-else :key="line.id" class="cart-line">
          <RouterLink :to="`/products/${line.productId}`">
            <img :src="line.cover" :alt="line.productName" />
          </RouterLink>
          <div class="cart-line-info">
            <RouterLink :to="`/products/${line.productId}`"><h3>{{ line.productName }}</h3></RouterLink>
            <span>库存 {{ line.stock }} 件</span>
            <button class="link-danger" @click="remove(line.id)">移除</button>
          </div>
          <div class="quantity-control">
            <button :disabled="line.quantity <= 1" @click="changeQuantity(line, -1)">−</button>
            <span>{{ line.quantity }}</span>
            <button :disabled="line.quantity >= line.stock" @click="changeQuantity(line, 1)">+</button>
          </div>
          <strong class="line-subtotal">{{ money(line.subtotal) }}</strong>
        </article>
      </section>

      <aside v-if="cart.cart.items.length" class="cart-summary">
        <h3>订单摘要</h3>
        <div><span>商品件数</span><b>{{ cart.cart.totalQuantity }} 件</b></div>
        <div><span>商品合计</span><b>{{ money(cart.cart.totalAmount) }}</b></div>
        <div><span>运费</span><b>免运费</b></div>
        <div class="summary-total"><span>应付总额</span><strong>{{ money(cart.cart.totalAmount) }}</strong></div>
        <button class="btn btn-primary btn-block" @click="router.push('/checkout')">去结算</button>
        <RouterLink class="continue-link" to="/products">继续购物</RouterLink>
      </aside>
    </div>
  </div>
</template>
