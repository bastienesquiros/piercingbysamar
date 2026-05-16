<template>
  <div class="min-h-screen bg-[--color-background-soft] flex items-center justify-center px-4">
    <div class="w-full max-w-sm">
      <!-- Logo -->
      <div class="text-center mb-8">
        <p class="font-serif text-2xl font-semibold text-[--color-text]">
          Piercing <span class="text-primary">by Samar</span>
        </p>
        <p class="text-[--color-text-muted] text-sm mt-1">Accès administration</p>
      </div>

      <div class="bg-white rounded-2xl border border-[--color-border] p-8 shadow-sm">
        <div class="space-y-4">
          <div>
            <label class="text-xs font-semibold text-[--color-text-muted] uppercase tracking-wide block mb-1.5">
              Email
            </label>
            <input
              v-model="email"
              type="email"
              class="input"
              placeholder="admin@admin.com"
              @keydown.enter="login"
            />
          </div>
          <div>
            <label class="text-xs font-semibold text-[--color-text-muted] uppercase tracking-wide block mb-1.5">
              Mot de passe
            </label>
            <input
              v-model="password"
              type="password"
              class="input"
              placeholder="••••••••"
              @keydown.enter="login"
            />
          </div>

          <div v-if="error" class="bg-red-50 border border-red-200 rounded-xl px-4 py-3 text-sm text-red-600">
            {{ error }}
          </div>

          <button
            class="btn-primary w-full py-3"
            :disabled="loading"
            @click="login"
          >
            <Icon v-if="loading" name="lucide:loader-2" class="w-4 h-4 animate-spin" />
            <Icon v-else name="lucide:lock" class="w-4 h-4" />
            {{ loading ? 'Connexion…' : 'Se connecter' }}
          </button>
        </div>
      </div>

      <p class="text-center text-xs text-[--color-text-muted] mt-6">
        <NuxtLink to="/" class="hover:text-[--color-text] transition-colors">← Retour au site</NuxtLink>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ layout: false })

const auth = useAuthStore()
const { post } = useApi()
const router = useRouter()
const localePath = useLocalePath()

if (auth.isAuthenticated && auth.isAdmin) {
  await navigateTo(localePath('/admin'))
}

const email = ref('')
const password = ref('')
const loading = ref(false)
const error = ref<string | null>(null)

async function login() {
  if (!email.value || !password.value) return
  loading.value = true
  error.value = null
  try {
    const res = await post<{ token: string; email: string; role: string }>('/api/auth/login', {
      email: email.value,
      password: password.value,
    })
    auth.setAuth(res.token, res.email, res.role)
    await router.push(localePath('/admin'))
  } catch {
    error.value = 'Email ou mot de passe incorrect.'
  } finally {
    loading.value = false
  }
}

useSeoMeta({ title: 'Admin — Connexion', robots: 'noindex' })
</script>
