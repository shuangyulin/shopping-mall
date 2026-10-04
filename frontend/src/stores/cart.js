import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import api from '../api'
import { useAuthStore } from './auth'

export const useCartStore = defineStore('cart', () => {
  const cart = ref({ items: [], totalQuantity: 0, totalAmount: 0 })
  const loading = ref(false)
  const totalQuantity = computed(() => cart.value.totalQuantity || 0)

  async function fetchCart() {
    const auth = useAuthStore()
    if (!auth.isLoggedIn) {
      cart.value = { items: [], totalQuantity: 0, totalAmount: 0 }
      return
    }
    loading.value = true
    try {
      cart.value = await api.get('/cart')
    } finally {
      loading.value = false
    }
  }

  async function add(productId, quantity = 1) {
    cart.value = await api.post('/cart', { productId, quantity })
  }

  async function update(itemId, quantity) {
    cart.value = await api.put(`/cart/${itemId}`, { productId: 0, quantity })
  }

  async function remove(itemId) {
    cart.value = await api.delete(`/cart/${itemId}`)
  }

  async function clear() {
    await api.delete('/cart')
    cart.value = { items: [], totalQuantity: 0, totalAmount: 0 }
  }

  function reset() {
    cart.value = { items: [], totalQuantity: 0, totalAmount: 0 }
  }

  return { cart, loading, totalQuantity, fetchCart, add, update, remove, clear, reset }
})
