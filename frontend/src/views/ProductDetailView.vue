<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../api'
import { money } from '../utils/format'
import { useAuthStore } from '../stores/auth'
import { useCartStore } from '../stores/cart'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const cart = useCartStore()
const product = ref(null)
const quantity = ref(1)
const loading = ref(true)
const adding = ref(false)

onMounted(async () => {
  try {
    product.value = await api.get(`/catalog/products/${route.params.id}`)
  } catch (error) {
    window.alert(error.message)
    router.replace('/products')
  } finally {
    loading.value = false
  }
})

async function addToCart(goToCart = false) {
  if (!auth.isLoggedIn) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  adding.value = true
  try {
    await cart.add(product.value.id, quantity.value)
    if (goToCart) {
      router.push('/cart')
    } else {
      window.alert('已加入购物车')
    }
  } catch (error) {
    window.alert(error.message)
  } finally {
    adding.value = false
  }
}
</script>

<template>
  <div class="container detail-wrap">
    <div v-if="loading" class="loading-state">商品加载中...</div>
    <div v-else-if="product" class="product-detail">
      <div class="detail-image">
        <img :src="product.cover" :alt="product.name" />
      </div>
      <div class="detail-info">
        <span class="category-label">{{ product.categoryName }}</span>
        <h1>{{ product.name }}</h1>
        <p class="detail-subtitle">{{ product.subtitle }}</p>
        <div class="detail-price">
          <strong>{{ money(product.price) }}</strong>
          <del v-if="product.originalPrice">{{ money(product.originalPrice) }}</del>
        </div>
        <div class="detail-meta">
          <span>库存 {{ product.stock }} 件</span>
          <span>已售 {{ product.sales }} 件</span>
          <span>正品保障</span>
        </div>
        <div class="detail-description">
          <h3>商品介绍</h3>
          <p>{{ product.description }}</p>
        </div>
        <div class="purchase-row">
          <div class="quantity-control">
            <button :disabled="quantity <= 1" @click="quantity--">−</button>
            <span>{{ quantity }}</span>
            <button :disabled="quantity >= product.stock" @click="quantity++">+</button>
          </div>
          <button class="btn btn-outline" :disabled="adding || product.stock < 1" @click="addToCart(false)">加入购物车</button>
          <button class="btn btn-primary" :disabled="adding || product.stock < 1" @click="addToCart(true)">立即购买</button>
        </div>
      </div>
    </div>
  </div>
</template>
