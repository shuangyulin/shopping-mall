<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const loading = ref(false)
const error = ref('')
const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  email: ''
})

async function submit() {
  error.value = ''
  if (form.password !== form.confirmPassword) {
    error.value = '两次输入的密码不一致'
    return
  }
  loading.value = true
  try {
    await auth.register({
      username: form.username,
      password: form.password,
      nickname: form.nickname,
      email: form.email
    })
    router.replace('/')
  } catch (exception) {
    error.value = exception.message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page reverse">
    <div class="auth-form-wrap">
      <form class="auth-card" @submit.prevent="submit">
        <span class="eyebrow">CREATE ACCOUNT</span>
        <h2>注册新用户</h2>
        <p class="form-hint">填写基本信息，即可开始购物。</p>
        <label>
          <span>用户名</span>
          <input v-model.trim="form.username" required minlength="3" maxlength="20" placeholder="3-20位用户名" />
        </label>
        <div class="form-row">
          <label>
            <span>密码</span>
            <input v-model="form.password" required minlength="6" type="password" placeholder="至少6位" />
          </label>
          <label>
            <span>确认密码</span>
            <input v-model="form.confirmPassword" required minlength="6" type="password" placeholder="再次输入" />
          </label>
        </div>
        <div class="form-row">
          <label>
            <span>昵称</span>
            <input v-model.trim="form.nickname" maxlength="50" placeholder="选填" />
          </label>
          <label>
            <span>邮箱</span>
            <input v-model.trim="form.email" type="email" placeholder="选填" />
          </label>
        </div>
        <p v-if="error" class="form-error">{{ error }}</p>
        <button class="btn btn-primary btn-block" :disabled="loading">
          {{ loading ? '注册中...' : '创建账号' }}
        </button>
        <p class="auth-switch">已有账号？<RouterLink to="/login">返回登录</RouterLink></p>
      </form>
    </div>
    <div class="auth-visual register-visual">
      <div>
        <span class="eyebrow">JOIN US</span>
        <h1>注册会员，<br />开启轻松购物。</h1>
        <p>一个账号即可完成选购、下单和订单查询。</p>
      </div>
    </div>
  </div>
</template>
