<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'
import ProductCard from '../components/ProductCard.vue'

const router = useRouter()
const categories = ref([])
const products = ref([])
const loading = ref(true)
const keyword = ref('')

onMounted(async () => {
  try {
    const [categoryData, productData] = await Promise.all([
      api.get('/catalog/categories'),
      api.get('/catalog/products', { params: { page: 0, size: 8 } })
    ])
    categories.value = categoryData
    products.value = productData.content
  } catch (error) {
    window.alert(error.message)
  } finally {
    loading.value = false
  }
})

function goCategory(id) {
  router.push({ name: 'products', query: { categoryId: id } })
}

function search() {
  router.push({ name: 'products', query: keyword.value ? { keyword: keyword.value } : {} })
}
</script>

<template>
  <div>
    <section class="hero">
      <div class="container hero-grid">
        <div class="hero-copy">
          <span class="eyebrow">2026 品质生活节</span>
          <h1>把喜欢的日常<br /><em>装进购物车</em></h1>
          <p>精选数码、家居、运动与食品，简单直接地买到真正需要的好东西。</p>
          <form class="hero-search" @submit.prevent="search">
            <input v-model.trim="keyword" placeholder="今天想找点什么？" />
            <button class="btn btn-primary" type="submit">立即搜索</button>
          </form>
          <div class="hero-points">
            <span>✓ 正品保障</span>
            <span>✓ 七天无理由</span>
            <span>✓ 极速发货</span>
          </div>
        </div>
        <div class="hero-art">
          <div class="hero-shape shape-one"></div>
          <div class="hero-shape shape-two"></div>
          <img src="https://images.unsplash.com/photo-1607082349566-187342175e2f?auto=format&fit=crop&w=1100&q=85" alt="精选商品" />
          <div class="floating-card">
            <small>本周人气</small>
            <strong>无线降噪耳机</strong>
            <span>已售 268 件</span>
          </div>
        </div>
      </div>
    </section>

    <section class="section container">
      <div class="section-heading">
        <div>
          <span class="eyebrow">CATEGORIES</span>
          <h2>快速找到想要的</h2>
        </div>
        <RouterLink to="/products">查看全部 →</RouterLink>
      </div>
      <div class="category-grid">
        <button v-for="category in categories" :key="category.id" class="category-card" @click="goCategory(category.id)">
          <span>{{ category.icon || '◇' }}</span>
          <strong>{{ category.name }}</strong>
          <small>进入分类</small>
        </button>
      </div>
    </section>

    <section class="section section-muted">
      <div class="container">
        <div class="section-heading">
          <div>
            <span class="eyebrow">POPULAR PICKS</span>
            <h2>大家正在买</h2>
          </div>
          <RouterLink to="/products">更多好物 →</RouterLink>
        </div>
        <div v-if="loading" class="loading-state">商品加载中...</div>
        <div v-else class="product-grid">
          <ProductCard v-for="product in products" :key="product.id" :product="product" />
        </div>
      </div>
    </section>

    <section class="section container">
      <div class="feature-banner">
        <div>
          <span class="eyebrow">SIMPLE SHOPPING</span>
          <h2>挑选、下单、收货，三步完成</h2>
          <p>清晰的商品信息和订单状态，让线上购买保持简单。</p>
        </div>
        <div class="steps">
          <div><b>01</b><span>浏览商品</span></div>
          <div><b>02</b><span>提交订单</span></div>
          <div><b>03</b><span>查看进度</span></div>
        </div>
      </div>
    </section>
  </div>
</template>
