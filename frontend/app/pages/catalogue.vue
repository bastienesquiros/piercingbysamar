<template>
  <div class="min-h-screen bg-[--color-background]">
    <div class="container-site py-8">

      <!-- Header -->
      <div class="mb-6">
        <h1 class="font-serif text-4xl font-semibold text-[--color-text] mb-1">
          {{ activeCategory ? $t('categories.' + activeCategory.slug, activeCategory.name) : $t('nav.catalogue') }}
        </h1>
        <p v-if="activeCategory?.description" class="text-[--color-text-muted]">
          {{ activeCategory.description }}
        </p>
        <p class="text-sm text-[--color-text-muted] mt-1">
          {{ $t('catalogue.products_count', data?.totalElements ?? 0, { n: data?.totalElements ?? 0 }) }}
        </p>
      </div>

      <!-- Mobile filter toggle -->
      <div class="lg:hidden mb-4">
        <button
          class="btn-outline w-full flex items-center justify-between px-4 py-3"
          @click="filtersOpen = !filtersOpen"
        >
          <span class="flex items-center gap-2">
            <Icon name="lucide:sliders-horizontal" class="w-4 h-4" />
            {{ filtersOpen ? $t('catalogue.hide_filters') : $t('catalogue.show_filters') }}
            <span v-if="hasFilters" class="w-2 h-2 rounded-full bg-[--color-primary] inline-block" />
          </span>
          <Icon
            name="lucide:chevron-down"
            class="w-4 h-4 transition-transform duration-200"
            :class="{ 'rotate-180': filtersOpen }"
          />
        </button>

        <!-- Mobile filter panel -->
        <Transition name="collapse">
          <div v-if="filtersOpen" class="mt-2 bg-[--color-background-soft] rounded-2xl p-4 space-y-5">
            <FilterPanel
              :top-level-categories="topLevelCategories"
              :materials="MATERIALS"
              :filters="filters"
              @set-category="setCategory"
              @set-material="setMaterial"
              @toggle-nickel-free="toggleNickelFree"
              @reset="resetFilters"
            />
          </div>
        </Transition>
      </div>

      <div class="flex flex-col lg:flex-row gap-8">

        <!-- Desktop sidebar -->
        <aside class="hidden lg:block lg:w-60 shrink-0">
          <div class="bg-[--color-background-soft] rounded-2xl p-5 sticky top-24 space-y-6">
            <h2 class="font-semibold text-xs uppercase tracking-widest text-[--color-text-muted]">
              {{ $t('catalogue.filters') }}
            </h2>
            <FilterPanel
              :top-level-categories="topLevelCategories"
              :materials="MATERIALS"
              :filters="filters"
              @set-category="setCategory"
              @set-material="setMaterial"
              @toggle-nickel-free="toggleNickelFree"
              @reset="resetFilters"
            />
          </div>
        </aside>

        <!-- Main content -->
        <div class="flex-1 min-w-0">

          <!-- Search result banner -->
          <div v-if="filters.search" class="mb-4 flex items-center gap-2 flex-wrap">
            <span class="text-sm text-[--color-text-muted]">{{ $t('catalogue.results_for') }} :</span>
            <span class="badge-primary">« {{ filters.search }} »</span>
            <button class="btn-ghost p-1" @click="clearSearch">
              <Icon name="lucide:x" class="w-4 h-4" />
            </button>
          </div>

          <!-- Sort bar -->
          <div class="flex items-center justify-end mb-4">
            <select
              v-model="filters.sort"
              class="text-sm border border-[--color-border] rounded-lg px-3 py-2 bg-white text-[--color-text]
                     outline-none focus:border-[--color-primary] cursor-pointer"
            >
              <option value="newest">{{ $t('catalogue.sort_newest') }}</option>
              <option value="price_asc">{{ $t('catalogue.sort_price_asc') }}</option>
              <option value="price_desc">{{ $t('catalogue.sort_price_desc') }}</option>
            </select>
          </div>

          <!-- Loading -->
          <div v-if="pending" class="grid grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-4">
            <div
              v-for="n in 12" :key="n"
              class="aspect-square rounded-xl bg-[--color-background-soft] animate-pulse"
            />
          </div>

          <!-- Empty -->
          <div v-else-if="!data?.content.length" class="flex flex-col items-center justify-center py-20 text-center gap-4">
            <Icon name="lucide:search-x" class="w-12 h-12 text-[--color-border]" />
            <p class="text-[--color-text-muted]">{{ $t('catalogue.no_results') }}</p>
            <button class="btn-outline" @click="resetFilters">{{ $t('catalogue.view_all_products') }}</button>
          </div>

          <!-- Grid -->
          <div v-else class="grid grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-4">
            <ProductCard
              v-for="product in data.content"
              :key="product.id"
              :product="product"
              class="animate-fade-in"
            />
          </div>

          <!-- Pagination -->
          <div v-if="data && data.totalPages > 1" class="flex items-center justify-center gap-2 mt-10">
            <button
              class="btn-ghost px-3 py-2"
              :disabled="filters.page === 0"
              @click="goToPage(filters.page - 1)"
            >
              <Icon name="lucide:chevron-left" class="w-4 h-4" />
            </button>

            <template v-for="p in paginationRange" :key="p">
              <span v-if="p === '...'" class="px-2 text-[--color-text-muted]">…</span>
              <button
                v-else
                class="w-9 h-9 rounded-full text-sm font-medium transition-colors"
                :class="p === filters.page
                  ? 'bg-[--color-primary] text-white'
                  : 'text-[--color-text-muted] hover:bg-[--color-background-soft]'"
                @click="goToPage(p as number)"
              >
                {{ (p as number) + 1 }}
              </button>
            </template>

            <button
              class="btn-ghost px-3 py-2"
              :disabled="data.last"
              @click="goToPage(filters.page + 1)"
            >
              <Icon name="lucide:chevron-right" class="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { PageResponse, ProductSummary } from '~/types'

// Labels affichés → valeurs enum backend
const MATERIALS: { label: string; value: string }[] = [
  { label: 'Titane ASTM F136', value: 'TITANIUM' },
  { label: 'Acier chirurgical', value: 'STEEL' },
  { label: 'Bioflex', value: 'BIOFLEX' },
  { label: 'Or 14K', value: 'GOLD' },
]

const route = useRoute()
const router = useRouter()
const { get } = useApi()
const { categories: allCategories, fetchCategories } = useCategories()

onMounted(() => fetchCategories())

const filtersOpen = ref(false)

const topLevelCategories = computed(() =>
  allCategories.value.filter((c) => !c.parentId)
)

// ── Filters (driven by URL query) ──────────────────────────────
const filters = reactive({
  categorySlug: (route.query.category as string) || null,
  material: (route.query.material as string) || null,
  nickelFree: route.query.nickelFree === 'true',
  search: (route.query.search as string) || null,
  sort: (route.query.sort as string) || 'newest',
  page: Number(route.query.page ?? 0),
})

const hasFilters = computed(
  () => !!filters.categorySlug || !!filters.material || filters.nickelFree || !!filters.search
)

const allFlat = computed(() => {
  const flat: import('~/types').Category[] = []
  const traverse = (cats: import('~/types').Category[]) => {
    for (const c of cats) {
      flat.push(c)
      if (c.children?.length) traverse(c.children)
    }
  }
  traverse(allCategories.value)
  return flat
})

const activeCategory = computed(() =>
  allFlat.value.find((c) => c.slug === filters.categorySlug) ?? null
)

const categoryId = computed(() => activeCategory.value?.id ?? null)

// ── Products fetch ─────────────────────────────────────────────
const { data, pending, refresh } = await useAsyncData(
  'products',
  () => {
    if (filters.search) {
      return get<PageResponse<ProductSummary>>('/api/products/search', {
        query: { q: filters.search, page: filters.page, size: 12 },
      })
    }
    const query: Record<string, unknown> = { page: filters.page, size: 12, sort: filters.sort }
    if (categoryId.value) query.categoryId = categoryId.value
    if (filters.material) query.material = filters.material
    if (filters.nickelFree) query.nickelFree = true
    return get<PageResponse<ProductSummary>>('/api/products', { query })
  },
  { watch: [filters] }
)

// ── URL sync (filters → URL) ───────────────────────────────────
watch(filters, () => {
  const query: Record<string, string> = {}
  if (filters.categorySlug) query.category = filters.categorySlug
  if (filters.material) query.material = filters.material
  if (filters.nickelFree) query.nickelFree = 'true'
  if (filters.search) query.search = filters.search
  if (filters.sort !== 'newest') query.sort = filters.sort
  if (filters.page > 0) query.page = String(filters.page)
  router.replace({ query })
})

// ── URL sync (URL → filters) ───────────────────────────────────
watch(
  () => route.query,
  (q) => {
    const newCat = (q.category as string) || null
    const newMat = (q.material as string) || null
    const newNickel = q.nickelFree === 'true'
    const newSearch = (q.search as string) || null
    const newSort = (q.sort as string) || 'newest'
    const newPage = Number(q.page ?? 0)
    if (newCat !== filters.categorySlug) filters.categorySlug = newCat
    if (newMat !== filters.material) filters.material = newMat
    if (newNickel !== filters.nickelFree) filters.nickelFree = newNickel
    if (newSearch !== filters.search) filters.search = newSearch
    if (newSort !== filters.sort) filters.sort = newSort
    if (newPage !== filters.page) filters.page = newPage
  },
  { deep: true }
)

// ── Filter actions ─────────────────────────────────────────────
function setCategory(slug: string | null) {
  filters.categorySlug = slug
  filters.page = 0
  filtersOpen.value = false
}

function setMaterial(mat: string) {
  filters.material = filters.material === mat ? null : mat
  filters.page = 0
}

function toggleNickelFree() {
  filters.nickelFree = !filters.nickelFree
  filters.page = 0
}

function clearSearch() {
  filters.search = null
  filters.page = 0
}

function resetFilters() {
  filters.categorySlug = null
  filters.material = null
  filters.nickelFree = false
  filters.search = null
  filters.page = 0
}

function goToPage(p: number) {
  filters.page = p
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// ── Pagination range ──────────────────────────────────────────
const paginationRange = computed<(number | '...')[]>(() => {
  const total = data.value?.totalPages ?? 0
  const current = filters.page
  if (total <= 7) return Array.from({ length: total }, (_, i) => i)

  const range: (number | '...')[] = [0]
  if (current > 2) range.push('...')
  for (let i = Math.max(1, current - 1); i <= Math.min(total - 2, current + 1); i++) range.push(i)
  if (current < total - 3) range.push('...')
  range.push(total - 1)
  return range
})

useSeoMeta({
  title: computed(() =>
    activeCategory.value
      ? `${activeCategory.value.name} — Piercing by Samar`
      : 'Catalogue — Piercing by Samar'
  ),
})
</script>

<style scoped>
.collapse-enter-active,
.collapse-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.collapse-enter-from,
.collapse-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}
</style>
