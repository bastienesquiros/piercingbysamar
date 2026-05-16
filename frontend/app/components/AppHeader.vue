<template>
  <header class="sticky top-0 z-40 bg-white/95 backdrop-blur-sm border-b border-[--color-border]">
    <!-- Top bar -->
    <div class="container-site">
      <div class="flex items-center justify-between h-16 gap-4">

        <!-- Logo -->
        <NuxtLink :to="localePath('/')" class="shrink-0">
          <span class="font-serif text-xl font-semibold tracking-wide text-[--color-text]">
            Piercing <span class="text-primary">by Samar</span>
          </span>
        </NuxtLink>

        <!-- Search bar (desktop) -->
        <div class="hidden md:flex flex-1 max-w-md relative">
          <Icon name="lucide:search" class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[--color-text-muted]" />
          <input
            v-model="searchQuery"
            type="search"
            :placeholder="$t('nav.search')"
            class="input pl-10 py-2 text-sm"
            @keydown.enter="goSearch"
          />
        </div>

        <!-- Right actions -->
        <div class="flex items-center gap-1">

          <!-- Currency toggle -->
          <div class="relative" data-currency-toggle>
            <button
              class="btn-ghost px-3 py-2 text-xs font-semibold tracking-widest uppercase flex items-center gap-1"
              @click="currencyOpen = !currencyOpen"
            >
              {{ currencyStore.currency }}
              <Icon name="lucide:chevron-down" class="w-3 h-3 transition-transform" :class="{ 'rotate-180': currencyOpen }" />
            </button>
            <Transition name="dropdown">
              <div
                v-if="currencyOpen"
                class="absolute right-0 top-full mt-1 bg-white border border-[--color-border] rounded-xl shadow-lg overflow-hidden z-50 min-w-[100px]"
              >
                <button
                  v-for="code in currencyStore.currencies"
                  :key="code"
                  class="w-full text-left px-4 py-2 text-sm hover:bg-[--color-background-soft] transition-colors flex items-center justify-between gap-4"
                  :class="currencyStore.currency === code ? 'text-[--color-primary-dark] font-semibold' : 'text-[--color-text-muted]'"
                  @click="currencyStore.set(code); currencyOpen = false"
                >
                  {{ code }}
                  <span v-if="currencyStore.currency === code">
                    <Icon name="lucide:check" class="w-3.5 h-3.5" />
                  </span>
                </button>
              </div>
            </Transition>
          </div>

          <!-- Language toggle -->
          <button
            class="btn-ghost px-3 py-2 text-xs font-semibold tracking-widest uppercase flex items-center gap-1.5"
            @click="toggleLocale"
          >
            <Icon name="lucide:globe" class="w-4 h-4" />
            <span>{{ locale === 'fr' ? 'FR' : 'EN' }}</span>
          </button>

          <!-- Cart -->
          <button
            class="btn-ghost relative px-3 py-2"
            :aria-label="$t('nav.cart')"
            @click="cart.toggleCart()"
          >
            <Icon name="lucide:shopping-bag" class="w-5 h-5" />
            <span
              v-if="cart.count > 0"
              class="absolute -top-0.5 -right-0.5 min-w-[18px] h-[18px] px-1 rounded-full
                     bg-[--color-primary] text-white text-[10px] font-bold
                     flex items-center justify-center leading-none"
            >
              {{ cart.count }}
            </span>
          </button>
        </div>
      </div>
    </div>

    <!-- Mobile search (always visible below header on mobile) -->
    <div class="md:hidden border-t border-[--color-border] bg-white px-4 py-2">
      <div class="relative">
        <Icon name="lucide:search" class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[--color-text-muted]" />
        <input
          v-model="searchQuery"
          type="search"
          :placeholder="$t('nav.search')"
          class="input pl-10 py-2 text-sm"
          @keydown.enter="goSearch"
        />
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
const { locale, setLocale } = useI18n()
const localePath = useLocalePath()
const route = useRoute()
const router = useRouter()
const cart = useCartStore()
const currencyStore = useCurrencyStore()
const { fetchCategories } = useCategories()

const searchQuery = ref('')
const currencyOpen = ref(false)

onMounted(() => {
  fetchCategories()
  document.addEventListener('click', (e) => {
    if (!(e.target as HTMLElement).closest('[data-currency-toggle]')) {
      currencyOpen.value = false
    }
  })
})

function goSearch() {
  if (!searchQuery.value.trim()) return
  router.push(localePath(`/catalogue?search=${encodeURIComponent(searchQuery.value.trim())}`))
}

function toggleLocale() {
  setLocale(locale.value === 'fr' ? 'en' : 'fr')
}
</script>

<style scoped>
.dropdown-enter-active, .dropdown-leave-active { transition: opacity 0.15s, transform 0.15s; }
.dropdown-enter-from, .dropdown-leave-to { opacity: 0; transform: translateY(-4px); }
</style>
