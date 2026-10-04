<script setup>
import { onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import AppHeader from './components/AppHeader.vue'
import AppFooter from './components/AppFooter.vue'
import { useAuthStore } from './stores/auth'
import { useCartStore } from './stores/cart'

const route = useRoute()
const auth = useAuthStore()
const cart = useCartStore()

onMounted(() => {
  auth.fetchMe().catch(() => auth.logout())
  cart.fetchCart()
})

watch(() => auth.isLoggedIn, () => cart.fetchCart())
</script>

<template>
  <div class="app-shell">
    <AppHeader v-if="!route.meta.hideHeader" />
    <main :class="{ 'main-with-header': !route.meta.hideHeader }">
      <RouterView />
    </main>
    <AppFooter v-if="!route.meta.hideHeader" />
  </div>
</template>
