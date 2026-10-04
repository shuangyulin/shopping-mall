import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import api from '../api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('mall_token') || '')
  const user = ref(JSON.parse(localStorage.getItem('mall_user') || 'null'))

  const isLoggedIn = computed(() => Boolean(token.value))
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  function persist(auth) {
    token.value = auth.token
    user.value = auth.user
    localStorage.setItem('mall_token', auth.token)
    localStorage.setItem('mall_user', JSON.stringify(auth.user))
  }

  async function login(form) {
    persist(await api.post('/auth/login', form))
  }

  async function register(form) {
    persist(await api.post('/auth/register', form))
  }

  async function fetchMe() {
    if (!token.value) return
    user.value = await api.get('/auth/me')
    localStorage.setItem('mall_user', JSON.stringify(user.value))
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('mall_token')
    localStorage.removeItem('mall_user')
  }

  return { token, user, isLoggedIn, isAdmin, login, register, fetchMe, logout }
})
