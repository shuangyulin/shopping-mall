<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const loading = ref(false)
const error = ref('')
const form = reactive({ username: 'user', password: 'user123' })

async function submit() {
  loading.value = true
  error.value = ''
  try {
    await auth.login(form)
    router.replace(route.query.redirect || '/')
  } catch (exception) {
    error.value = exception.message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-visual">
      <div>
        <span class="eyebrow">WELCOME BACK</span>
        <h1>欢迎回来，<br />继续发现好物。</h1>
        <p>登录后可以管理购物车、提交订单和查看购买记录。</p>
      </div>
    </div>
    <div class="auth-form-wrap">
      <form class="auth-card" @submit.prevent="submit">
        <span class="eyebrow">ACCOUNT LOGIN</span>
        <h2>用户登录</h2>
        <p class="form-hint">测试账号：user / user123</p>
        <label>
          <span>用户名</span>
          <input v-model.trim="form.username" required autocomplete="username" placeholder="请输入用户名" />
        </label>
        <label>
          <span>密码</span>
          <input v-model="form.password" required type="password" autocomplete="current-password" placeholder="请输入密码" />
        </label>
        <p v-if="error" class="form-error">{{ error }}</p>
        <button class="btn btn-primary btn-block" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
        <p class="auth-switch">还没有账号？<RouterLink to="/register">立即注册</RouterLink></p>
      </form>
    </div>
  </div>
</template>
