<script setup>
import { onMounted, reactive, ref } from 'vue'
import api from '../../api'
import { money } from '../../utils/format'

const products = ref([])
const categories = ref([])
const loading = ref(true)
const showModal = ref(false)
const saving = ref(false)
const editingId = ref(null)
const filter = reactive({ keyword: '', page: 0, size: 10, totalPages: 0 })
const form = reactive({
  name: '',
  subtitle: '',
  description: '',
  price: 0,
  originalPrice: null,
  stock: 0,
  cover: '',
  categoryId: '',
  status: true
})

async function load(page = 0) {
  loading.value = true
  try {
    const data = await api.get('/admin/products', {
      params: { keyword: filter.keyword || undefined, page, size: filter.size }
    })
    products.value = data.content
    filter.page = data.page
    filter.totalPages = data.totalPages
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  categories.value = await api.get('/admin/categories')
  await load()
})

function openCreate() {
  editingId.value = null
  Object.assign(form, {
    name: '',
    subtitle: '',
    description: '',
    price: 0,
    originalPrice: null,
    stock: 0,
    cover: '',
    categoryId: categories.value[0]?.id || '',
    status: true
  })
  showModal.value = true
}

function openEdit(product) {
  editingId.value = product.id
  Object.assign(form, {
    name: product.name,
    subtitle: product.subtitle || '',
    description: product.description || '',
    price: product.price,
    originalPrice: product.originalPrice,
    stock: product.stock,
    cover: product.cover,
    categoryId: product.categoryId,
    status: product.status
  })
  showModal.value = true
}

async function save() {
  saving.value = true
  try {
    const url = editingId.value ? `/admin/products/${editingId.value}` : '/admin/products'
    const method = editingId.value ? 'put' : 'post'
    await api[method](url, form)
    showModal.value = false
    await load(filter.page)
  } catch (error) {
    window.alert(error.message)
  } finally {
    saving.value = false
  }
}

async function disable(product) {
  if (!window.confirm(`确定下架「${product.name}」吗？`)) return
  try {
    await api.delete(`/admin/products/${product.id}`)
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
        <span>PRODUCTS</span>
        <h1>商品管理</h1>
      </div>
      <button class="btn btn-primary" @click="openCreate">新增商品</button>
    </div>

    <div class="admin-toolbar">
      <form @submit.prevent="load(0)">
        <input v-model.trim="filter.keyword" placeholder="搜索商品名称" />
        <button class="btn btn-dark" type="submit">搜索</button>
      </form>
    </div>

    <div class="admin-table-wrap">
      <table class="admin-table">
        <thead>
          <tr>
            <th>商品</th>
            <th>分类</th>
            <th>价格</th>
            <th>库存 / 销量</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in products" :key="product.id">
            <td>
              <div class="table-product">
                <img :src="product.cover" :alt="product.name" />
                <div><strong>{{ product.name }}</strong><span>#{{ product.id }}</span></div>
              </div>
            </td>
            <td>{{ product.categoryName }}</td>
            <td>{{ money(product.price) }}</td>
            <td>{{ product.stock }} / {{ product.sales }}</td>
            <td><span :class="['status-tag', product.status ? 'on' : 'off']">{{ product.status ? '销售中' : '已下架' }}</span></td>
            <td>
              <button class="table-action" @click="openEdit(product)">编辑</button>
              <button v-if="product.status" class="table-action danger" @click="disable(product)">下架</button>
            </td>
          </tr>
          <tr v-if="!loading && !products.length"><td colspan="6" class="table-empty">暂无商品数据</td></tr>
        </tbody>
      </table>
    </div>

    <div v-if="filter.totalPages > 1" class="pagination">
      <button :disabled="filter.page === 0" @click="load(filter.page - 1)">上一页</button>
      <span>第 {{ filter.page + 1 }} / {{ filter.totalPages }} 页</span>
      <button :disabled="filter.page + 1 >= filter.totalPages" @click="load(filter.page + 1)">下一页</button>
    </div>

    <div v-if="showModal" class="modal-mask" @click.self="showModal = false">
      <form class="modal-card" @submit.prevent="save">
        <header>
          <h2>{{ editingId ? '编辑商品' : '新增商品' }}</h2>
          <button type="button" @click="showModal = false">×</button>
        </header>
        <div class="modal-body">
          <div class="form-row">
            <label><span>商品名称 *</span><input v-model.trim="form.name" required /></label>
            <label>
              <span>商品分类 *</span>
              <select v-model="form.categoryId" required>
                <option v-for="category in categories" :key="category.id" :value="category.id">{{ category.name }}</option>
              </select>
            </label>
          </div>
          <label><span>商品副标题</span><input v-model.trim="form.subtitle" /></label>
          <div class="form-row three">
            <label><span>售价 *</span><input v-model.number="form.price" required min="0.01" step="0.01" type="number" /></label>
            <label><span>原价</span><input v-model.number="form.originalPrice" min="0" step="0.01" type="number" /></label>
            <label><span>库存 *</span><input v-model.number="form.stock" required min="0" type="number" /></label>
          </div>
          <label><span>商品图片地址 *</span><input v-model.trim="form.cover" required type="url" /></label>
          <label><span>商品介绍</span><textarea v-model.trim="form.description" rows="4"></textarea></label>
          <label class="checkbox-label"><input v-model="form.status" type="checkbox" /> 上架销售</label>
        </div>
        <footer>
          <button class="btn btn-outline" type="button" @click="showModal = false">取消</button>
          <button class="btn btn-primary" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
        </footer>
      </form>
    </div>
  </div>
</template>
