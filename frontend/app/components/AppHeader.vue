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
        <div class="hidden md:flex flex-1 max-w-md relative" data-search-desktop>
          <Icon name="lucide:search" class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[--color-text-muted] z-10" />
          <input
            v-model="searchQuery"
            type="search"
            :placeholder="$t('nav.search')"
            class="input pl-10 py-2 text-sm w-full"
            autocomplete="off"
            @input="onInput"
            @keydown.enter="goSearch"
            @keydown.escape="closeSuggestions"
            @focus="searchFocused = true"
          />
          <!-- Suggestions dropdown -->
          <Transition name="dropdown">
            <div
              v-if="showSuggestions"
              class="absolute top-full left-0 right-0 mt-1 bg-white border border-[--color-border] rounded-xl shadow-lg overflow-hidden z-50"
            >
              <NuxtLink
                v-for="p in suggestions"
                :key="p.id"
                :to="localePath(`/products/${p.slug}`)"
                class="flex items-center gap-3 px-4 py-2.5 hover:bg-[--color-background-soft] transition-colors"
                @click="closeSuggestions"
              >
                <NuxtImg
                  v-if="p.coverImageUrl"
                  :src="p.coverImageUrl"
                  :alt="p.name"
                  width="36" height="36"
                  class="w-9 h-9 rounded-lg object-cover shrink-0 bg-[--color-background-soft]"
                />
                <div v-else class="w-9 h-9 rounded-lg bg-[--color-background-soft] shrink-0" />
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-medium text-[--color-text] truncate">{{ p.name }}</p>
                  <p v-if="p.minPriceCents" class="text-xs text-[--color-text-muted]">{{ formatPrice(p.minPriceCents) }}</p>
                </div>
              </NuxtLink>
              <button
                class="w-full text-left px-4 py-2.5 text-sm text-[--color-primary] font-medium hover:bg-[--color-background-soft] border-t border-[--color-border] transition-colors"
                @click="goSearch"
              >
                Voir tous les résultats pour "{{ searchQuery }}"
              </button>
            </div>
          </Transition>
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

          <!-- RDV link discret -->
          <NuxtLink
            :to="localePath('/rdv')"
            class="hidden md:inline-flex items-center gap-1.5 text-sm text-[--color-text-muted] hover:text-[--color-text] transition-colors px-2"
          >
            <Icon name="simple-icons:whatsapp" class="w-3.5 h-3.5 text-green-500" />
            {{ $t('nav.rdv') }}
          </NuxtLink>

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
      <div class="relative" data-search-mobile>
        <Icon name="lucide:search" class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[--color-text-muted] z-10" />
        <input
          v-model="searchQuery"
          type="search"
          :placeholder="$t('nav.search')"
          class="input pl-10 py-2 text-sm w-full"
          autocomplete="off"
          @input="onInput"
          @keydown.enter="goSearch"
          @keydown.escape="closeSuggestions"
          @focus="searchFocused = true"
        />
        <!-- Mobile suggestions dropdown -->
        <Transition name="dropdown">
          <div
            v-if="showSuggestions"
            class="absolute top-full left-0 right-0 mt-1 bg-white border border-[--color-border] rounded-xl shadow-lg overflow-hidden z-50"
          >
            <NuxtLink
              v-for="p in suggestions"
              :key="p.id"
              :to="localePath(`/products/${p.slug}`)"
              class="flex items-center gap-3 px-4 py-2.5 hover:bg-[--color-background-soft] transition-colors"
              @click="closeSuggestions"
            >
              <NuxtImg
                v-if="p.coverImageUrl"
                :src="p.coverImageUrl"
                :alt="p.name"
                width="36" height="36"
                class="w-9 h-9 rounded-lg object-cover shrink-0 bg-[--color-background-soft]"
              />
              <div v-else class="w-9 h-9 rounded-lg bg-[--color-background-soft] shrink-0" />
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-[--color-text] truncate">{{ p.name }}</p>
                <p v-if="p.minPriceCents" class="text-xs text-[--color-text-muted]">{{ formatPrice(p.minPriceCents) }}</p>
              </div>
            </NuxtLink>
            <button
              class="w-full text-left px-4 py-2.5 text-sm text-[--color-primary] font-medium hover:bg-[--color-background-soft] border-t border-[--color-border] transition-colors"
              @click="goSearch"
            >
              Voir tous les résultats pour "{{ searchQuery }}"
            </button>
          </div>
        </Transition>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
const { locale, setLocale } = useI18n()
const localePath = useLocalePath()
const router = useRouter()
const cart = useCartStore()
const currencyStore = useCurrencyStore()
const config = useRuntimeConfig()

const searchQuery = ref('')
const currencyOpen = ref(false)
const searchFocused = ref(false)
const suggestions = ref<any[]>([])
let debounceTimer: ReturnType<typeof setTimeout> | null = null

const showSuggestions = computed(
  () => searchFocused.value && searchQuery.value.trim().length >= 2 && suggestions.value.length > 0
)

function onInput() {
  if (debounceTimer) clearTimeout(debounceTimer)
  if (searchQuery.value.trim().length < 2) {
    suggestions.value = []
    return
  }
  debounceTimer = setTimeout(fetchSuggestions, 300)
}

async function fetchSuggestions() {
  try {
    const res = await $fetch<{ content: any[] }>('/api/products', {
      baseURL: '',
      params: { search: searchQuery.value.trim(), size: 5, page: 0 },
    })
    suggestions.value = res.content ?? []
  } catch {
    suggestions.value = []
  }
}

function closeSuggestions() {
  searchFocused.value = false
  suggestions.value = []
}

function goSearch() {
  if (!searchQuery.value.trim()) return
  closeSuggestions()
  router.push(localePath(`/catalogue?search=${encodeURIComponent(searchQuery.value.trim())}`))
}

function formatPrice(cents: number) {
  return Math.round(cents / 100) + ' MAD'
}

function toggleLocale() {
  setLocale(locale.value === 'fr' ? 'en' : 'fr')
}

onMounted(() => {
  document.addEventListener('click', (e) => {
    const t = e.target as HTMLElement
    if (!t.closest('[data-currency-toggle]')) currencyOpen.value = false
    if (!t.closest('[data-search-desktop]') && !t.closest('[data-search-mobile]')) closeSuggestions()
  })
})
</script>

<style scoped>
.dropdown-enter-active, .dropdown-leave-active { transition: opacity 0.15s, transform 0.15s; }
.dropdown-enter-from, .dropdown-leave-to { opacity: 0; transform: translateY(-4px); }
</style>
