<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useCartStore } from '../stores/cart'
import { money } from '../utils/format'

const props = defineProps({
  product: { type: Object, required: true }
})

const router = useRouter()
const auth = useAuthStore()
const cart = useCartStore()
const adding = ref(false)

async function addToCart() {
  if (!auth.isLoggedIn) {
    router.push({ name: 'login', query: { redirect: router.currentRoute.value.fullPath } })
    return
  }
  adding.value = true
  try {
    await cart.add(props.product.id, 1)
  } catch (error) {
    window.alert(error.message)
  } finally {
    adding.value = false
  }
}

function imageFallback(event) {
  event.target.src = 'https://picsum.photos/seed/mall-product/800/800'
}
</script>

<template>
  <article class="product-card">
    <RouterLink class="product-image" :to="`/products/${product.id}`">
      <img :src="product.cover" :alt="product.name" loading="lazy" @error="imageFallback" />
      <span v-if="product.sales > 100" class="sales-badge">热销</span>
    </RouterLink>
    <div class="product-card-body">
      <span class="category-label">{{ product.categoryName }}</span>
      <RouterLink :to="`/products/${product.id}`">
        <h3>{{ product.name }}</h3>
      </RouterLink>
      <p>{{ product.subtitle }}</p>
      <div class="product-card-bottom">
        <div class="price-group">
          <strong>{{ money(product.price) }}</strong>
          <del v-if="product.originalPrice">{{ money(product.originalPrice) }}</del>
        </div>
        <button class="round-add" :disabled="adding || product.stock < 1" @click="addToCart">
          {{ product.stock < 1 ? '无货' : adding ? '...' : '+' }}
        </button>
      </div>
    </div>
  </article>
</template>
