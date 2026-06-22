<template>
  <div class="min-h-screen bg-gray-50 flex">

    <!-- Sidebar — desktop only -->
    <aside class="hidden lg:flex w-60 shrink-0 bg-[#1C1410] text-white flex-col min-h-screen">
      <!-- Logo -->
      <div class="px-6 py-5 border-b border-white/10">
        <p class="font-serif text-lg font-semibold">
          Piercing <span class="text-[--color-primary]">by Samar</span>
        </p>
        <p class="text-white/40 text-xs mt-0.5">Admin</p>
      </div>

      <!-- Nav -->
      <nav class="flex-1 px-3 py-4 space-y-1">
        <NuxtLink
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors"
          :class="isActive(item.to)
            ? 'bg-[--color-primary] text-[#1C1410]'
            : 'text-white/70 hover:text-white hover:bg-white/10'"
        >
          <Icon :name="item.icon" class="w-4 h-4 shrink-0" />
          {{ item.label }}
        </NuxtLink>
      </nav>

      <!-- User / logout -->
      <div class="px-4 py-4 border-t border-white/10">
        <p class="text-white/40 text-xs mb-2 truncate">{{ auth.user?.email }}</p>
        <button
          class="flex items-center gap-2 text-sm text-white/60 hover:text-white transition-colors"
          @click="logout"
        >
          <Icon name="lucide:log-out" class="w-4 h-4" />
          Déconnexion
        </button>
      </div>
    </aside>

    <!-- Main -->
    <div class="flex-1 flex flex-col overflow-hidden min-w-0">
      <!-- Top bar -->
      <header class="h-14 bg-white border-b border-gray-200 flex items-center px-4 lg:px-6 shrink-0">
        <h1 class="text-sm font-semibold text-gray-700 truncate">{{ currentPageTitle }}</h1>
        <div class="ml-auto flex items-center gap-3">
          <NuxtLink to="/" target="_blank" class="text-xs text-gray-400 hover:text-gray-600 flex items-center gap-1">
            <Icon name="lucide:external-link" class="w-3.5 h-3.5" />
            <span class="hidden sm:inline">Voir le site</span>
          </NuxtLink>
          <!-- Mobile logout -->
          <button class="lg:hidden text-gray-400 hover:text-gray-700" @click="logout">
            <Icon name="lucide:log-out" class="w-4 h-4" />
          </button>
        </div>
      </header>

      <!-- Page -->
      <main class="flex-1 overflow-auto p-4 lg:p-6 pb-24 lg:pb-6">
        <slot />
      </main>
    </div>
  </div>

  <!-- Mobile bottom nav -->
  <nav class="lg:hidden fixed bottom-0 inset-x-0 bg-[#1C1410] border-t border-white/10 z-40 flex safe-area-bottom">
    <NuxtLink
      v-for="item in navItems"
      :key="item.to"
      :to="item.to"
      class="flex-1 flex flex-col items-center gap-0.5 py-2 px-1 text-[9px] font-medium transition-colors leading-none"
      :class="isActive(item.to)
        ? 'text-[--color-primary]'
        : 'text-white/50 hover:text-white'"
    >
      <Icon :name="item.icon" class="w-[18px] h-[18px] mb-0.5" />
      <span class="truncate w-full text-center">{{ item.label }}</span>
    </NuxtLink>
  </nav>

  <ToastContainer />
</template>

<script setup lang="ts">
const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

const navItems = [
  { to: '/admin', label: 'Dashboard', icon: 'lucide:layout-dashboard' },
  { to: '/admin/products', label: 'Produits', icon: 'lucide:package' },
  { to: '/admin/categories', label: 'Catégories', icon: 'lucide:tag' },
  { to: '/admin/tags', label: 'Tags', icon: 'lucide:hash' },
  { to: '/admin/orders', label: 'Commandes', icon: 'lucide:shopping-cart' },
  { to: '/admin/faq', label: 'FAQ', icon: 'lucide:help-circle' },
  { to: '/admin/stats', label: 'Stats', icon: 'lucide:bar-chart-2' },
]

const isActive = (to: string) =>
  to === '/admin' ? route.path === '/admin' : route.path.startsWith(to)

const currentPageTitle = computed(() => {
  if (route.path === '/admin') return 'Dashboard'
  if (route.path.startsWith('/admin/products')) return 'Produits'
  if (route.path.startsWith('/admin/categories')) return 'Catégories'
  if (route.path.startsWith('/admin/tags')) return 'Tags'
  if (route.path.startsWith('/admin/orders')) return 'Commandes'
  if (route.path.startsWith('/admin/faq')) return 'FAQ'
  if (route.path.startsWith('/admin/stats')) return 'Stats & Export'
  return 'Admin'
})

function logout() {
  auth.logout()
  router.push('/admin/login')
}
</script>

<style scoped>
.safe-area-bottom {
  padding-bottom: env(safe-area-inset-bottom);
}
</style>
