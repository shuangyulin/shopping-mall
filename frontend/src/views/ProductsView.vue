<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../api'
import ProductCard from '../components/ProductCard.vue'

const route = useRoute()
const router = useRouter()
const categories = ref([])
const products = ref([])
const loading = ref(false)
const total = ref(0)
const totalPages = ref(0)
const filters = reactive({
  keyword: '',
  categoryId: '',
  page: 0,
  size: 12
})

async function load() {
  loading.value = true
  try {
    const data = await api.get('/catalog/products', {
      params: {
        keyword: filters.keyword || undefined,
        categoryId: filters.categoryId || undefined,
        page: filters.page,
        size: filters.size
      }
    })
    products.value = data.content
    total.value = data.totalElements
    totalPages.value = data.totalPages
  } catch (error) {
    window.alert(error.message)
  } finally {
    loading.value = false
  }
}

function applyFilters() {
  filters.page = 0
  router.replace({
    query: {
      ...(filters.keyword ? { keyword: filters.keyword } : {}),
      ...(filters.categoryId ? { categoryId: filters.categoryId } : {})
    }
  })
  load()
}

function selectCategory(id = '') {
  filters.categoryId = id
  applyFilters()
}

function changePage(page) {
  if (page < 0 || page >= totalPages.value) return
  filters.page = page
  load()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(async () => {
  categories.value = await api.get('/catalog/categories')
  filters.keyword = route.query.keyword || ''
  filters.categoryId = route.query.categoryId || ''
  load()
})

watch(() => route.query, (query) => {
  filters.keyword = query.keyword || ''
  filters.categoryId = query.categoryId || ''
  filters.page = 0
  load()
})
</script>

<template>
  <div class="page-wrap">
    <div class="page-hero compact">
      <div class="container">
        <span class="eyebrow">ALL PRODUCTS</span>
        <h1>全部商品</h1>
        <p>按照分类筛选，找到适合你的好物。</p>
      </div>
    </div>

    <div class="container catalog-layout">
      <aside class="filter-panel">
        <h3>商品分类</h3>
        <button :class="{ active: !filters.categoryId }" @click="selectCategory()">全部分类</button>
        <button
          v-for="category in categories"
          :key="category.id"
          :class="{ active: String(filters.categoryId) === String(category.id) }"
          @click="selectCategory(category.id)"
        >
          {{ category.name }}
        </button>
      </aside>

      <section class="catalog-content">
        <div class="catalog-toolbar">
          <form class="search-box" @submit.prevent="applyFilters">
            <input v-model.trim="filters.keyword" placeholder="搜索商品名称" />
            <button class="btn btn-dark" type="submit">搜索</button>
          </form>
          <span>共 {{ total }} 件商品</span>
        </div>

        <div v-if="loading" class="loading-state">正在加载商品...</div>
        <div v-else-if="products.length" class="product-grid catalog-grid">
          <ProductCard v-for="product in products" :key="product.id" :product="product" />
        </div>
        <div v-else class="empty-state">
          <b>没有找到商品</b>
          <p>换个关键词或分类试试。</p>
        </div>

        <div v-if="totalPages > 1" class="pagination">
          <button :disabled="filters.page === 0" @click="changePage(filters.page - 1)">上一页</button>
          <span>第 {{ filters.page + 1 }} / {{ totalPages }} 页</span>
          <button :disabled="filters.page + 1 >= totalPages" @click="changePage(filters.page + 1)">下一页</button>
        </div>
      </section>
    </div>
  </div>
</template>
