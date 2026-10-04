import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  { path: '/', name: 'home', component: () => import('../views/HomeView.vue') },
  { path: '/products', name: 'products', component: () => import('../views/ProductsView.vue') },
  { path: '/products/:id', name: 'product-detail', component: () => import('../views/ProductDetailView.vue') },
  { path: '/login', name: 'login', component: () => import('../views/LoginView.vue'), meta: { guestOnly: true } },
  { path: '/register', name: 'register', component: () => import('../views/RegisterView.vue'), meta: { guestOnly: true } },
  { path: '/cart', name: 'cart', component: () => import('../views/CartView.vue'), meta: { auth: true } },
  { path: '/checkout', name: 'checkout', component: () => import('../views/CheckoutView.vue'), meta: { auth: true } },
  { path: '/orders', name: 'orders', component: () => import('../views/OrdersView.vue'), meta: { auth: true } },
  { path: '/orders/:orderNo', name: 'order-detail', component: () => import('../views/OrderDetailView.vue'), meta: { auth: true } },
  {
    path: '/admin',
    component: () => import('../views/admin/AdminLayout.vue'),
    meta: { auth: true, admin: true, hideHeader: true },
    children: [
      { path: '', name: 'admin-dashboard', component: () => import('../views/admin/DashboardView.vue') },
      { path: 'products', name: 'admin-products', component: () => import('../views/admin/ProductsView.vue') },
      { path: 'orders', name: 'admin-orders', component: () => import('../views/admin/OrdersView.vue') }
    ]
  },
  { path: '/:pathMatch(.*)*', name: 'not-found', component: () => import('../views/NotFoundView.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.auth && !auth.isLoggedIn) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.meta.guestOnly && auth.isLoggedIn) {
    return { name: 'home' }
  }
  if (to.meta.admin && !auth.isAdmin) {
    return { name: 'home' }
  }
  return true
})

export default router
