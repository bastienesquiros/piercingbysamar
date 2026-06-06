<template>
  <div class="min-h-screen bg-[--color-background]">
    <!-- Breadcrumb -->
    <div class="container-site pt-6 pb-4">
      <nav class="flex items-center gap-2 text-sm text-[--color-text-muted]">
        <NuxtLink :to="localePath('/')" class="hover:text-[--color-text] transition-colors">{{ $t('breadcrumb.home') }}</NuxtLink>
        <Icon name="lucide:chevron-right" class="w-3.5 h-3.5" />
        <NuxtLink :to="localePath('/catalogue')" class="hover:text-[--color-text] transition-colors">
          {{ $t('nav.catalogue') }}
        </NuxtLink>
        <Icon name="lucide:chevron-right" class="w-3.5 h-3.5" />
        <NuxtLink
          :to="localePath(`/catalogue?category=${product.categoryName.toLowerCase()}`)"
          class="hover:text-[--color-text] transition-colors"
        >
          {{ product.categoryName }}
        </NuxtLink>
        <Icon name="lucide:chevron-right" class="w-3.5 h-3.5" />
        <span class="text-[--color-text] font-medium truncate max-w-[180px]">{{ product.name }}</span>
      </nav>
    </div>

    <!-- Product content -->
    <div class="container-site pb-16">
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-10 xl:gap-16">

        <!-- ── Gallery ──────────────────────────────────────────── -->
        <div class="space-y-3">
          <!-- Main image -->
          <div
            ref="galleryEl"
            class="aspect-square rounded-2xl overflow-hidden bg-[--color-background-soft] relative select-none"
          >
            <NuxtImg
              v-if="currentImage"
              :src="currentImage.r2Url"
              :alt="currentImage.altText ?? product.name"
              class="w-full h-full object-cover transition-opacity duration-300"
              width="800"
              height="800"
              loading="eager"
            />
            <div v-else class="w-full h-full flex items-center justify-center">
              <Icon name="lucide:image" class="w-20 h-20 text-[--color-border]" />
            </div>

            <!-- Dot indicators (mobile) -->
            <div
              v-if="sortedImages.length > 1"
              class="absolute bottom-3 left-0 right-0 flex justify-center gap-1.5 lg:hidden"
            >
              <button
                v-for="img in sortedImages"
                :key="img.id"
                class="w-2 h-2 rounded-full transition-all duration-200"
                :class="activeImageId === img.id ? 'bg-white w-4' : 'bg-white/50'"
                @click="activeImageId = img.id"
              />
            </div>
          </div>

          <!-- Thumbnails -->
          <div v-if="product.images.length > 1" class="flex gap-2 overflow-x-auto scrollbar-hide pb-1">
            <button
              v-for="img in sortedImages"
              :key="img.id"
              class="shrink-0 w-20 h-20 rounded-xl overflow-hidden border-2 transition-all duration-200"
              :class="activeImageId === img.id
                ? 'border-[--color-primary]'
                : 'border-[--color-border] hover:border-[--color-primary-light]'"
              @click="activeImageId = img.id"
            >
              <NuxtImg
                :src="img.r2Url"
                :alt="img.altText ?? product.name"
                class="w-full h-full object-cover"
                width="80"
                height="80"
                loading="lazy"
              />
            </button>
          </div>
        </div>

        <!-- ── Info ────────────────────────────────────────────── -->
        <div class="space-y-6">

          <!-- Category + name -->
          <div>
            <p class="text-xs uppercase tracking-widest text-[--color-text-muted] mb-2">
              {{ product.categoryName }}
            </p>
            <h1 class="font-serif text-3xl md:text-4xl font-semibold text-[--color-text] leading-tight mb-3">
              {{ product.name }}
            </h1>

            <!-- Tags (on exclut "Nickel Free" car affiché séparément avec icône) -->
            <div v-if="product.tags.length" class="flex flex-wrap gap-2">
              <span
                v-for="tag in product.tags.filter(t => t.toLowerCase() !== 'nickel free')"
                :key="tag"
                class="badge text-xs"
              >{{ tag }}</span>
            </div>
          </div>

          <!-- Price -->
          <div class="flex items-baseline gap-3">
            <span class="font-serif text-3xl font-semibold text-[--color-text]">
              {{ selectedVariant ? format(selectedVariant.priceCents) : priceRange }}
            </span>
            <span v-if="selectedVariant && quantity > 1" class="text-sm text-[--color-text-muted]">
              × {{ quantity }} = <span class="font-semibold text-[--color-text]">{{ format(selectedVariant.priceCents * quantity) }}</span>
            </span>
          </div>

          <!-- Material info -->
          <div class="flex flex-wrap gap-2">
            <span class="badge">
              <Icon name="lucide:gem" class="w-3 h-3 mr-1" />
              {{ product.material }}
            </span>
            <span v-if="product.nickelFree" class="badge-primary">
              <Icon name="lucide:shield-check" class="w-3 h-3 mr-1" />
              Nickel Free
            </span>
          </div>

          <!-- Variant selectors -->
          <div v-if="hasVariants" class="space-y-4">
            <!-- Size -->
            <div v-if="availableSizes.length">
              <p class="text-sm font-semibold text-[--color-text] mb-2">
                {{ $t('product.size') }}
                <span v-if="selectedSize" class="font-normal text-[--color-text-muted] ml-1">
                  — {{ selectedSize }}
                </span>
              </p>
              <div class="flex flex-wrap gap-2">
                <button
                  v-for="size in availableSizes"
                  :key="size"
                  class="px-4 py-2 rounded-full border text-sm font-medium transition-all duration-200"
                  :class="selectedSize === size
                    ? 'border-[--color-primary] bg-[--color-primary-light] text-[--color-primary-dark]'
                    : 'border-[--color-border] text-[--color-text-muted] hover:border-[--color-primary-light]'"
                  @click="selectedSize = size; selectedColor = null"
                >
                  {{ size }}
                </button>
              </div>
            </div>

            <!-- Color -->
            <div v-if="availableColors.length">
              <p class="text-sm font-semibold text-[--color-text] mb-2">
                {{ $t('product.color') }}
                <span v-if="selectedColor" class="font-normal text-[--color-text-muted] ml-1">
                  — {{ selectedColor }}
                </span>
              </p>
              <div class="flex flex-wrap gap-2">
                <button
                  v-for="color in availableColors"
                  :key="color"
                  class="px-4 py-2 rounded-full border text-sm font-medium transition-all duration-200"
                  :class="selectedColor === color
                    ? 'border-[--color-primary] bg-[--color-primary-light] text-[--color-primary-dark]'
                    : 'border-[--color-border] text-[--color-text-muted] hover:border-[--color-primary-light]'"
                  @click="selectedColor = color"
                >
                  {{ color }}
                </button>
              </div>
            </div>
          </div>

          <!-- Stock & SKU -->
          <div v-if="selectedVariant" class="flex items-center gap-4 text-sm">
            <div class="flex items-center gap-1.5">
              <span
                class="w-2 h-2 rounded-full"
                :class="selectedVariant.inStock ? 'bg-green-500' : 'bg-red-400'"
              />
              <span :class="selectedVariant.inStock ? 'text-green-700' : 'text-red-500'">
                {{ selectedVariant.inStock ? $t('product.in_stock') : $t('product.out_of_stock') }}
              </span>
              <span v-if="selectedVariant.inStock && selectedVariant.availableStock <= 5" class="text-amber-600 text-xs font-medium">
                · {{ $t('product.low_stock', { n: selectedVariant.availableStock }) }}
              </span>
            </div>
            <span class="text-xs text-[--color-text-muted] opacity-60">
              réf. {{ selectedVariant.sku }}
            </span>
          </div>

          <!-- Quantity + Add to cart -->
          <div class="space-y-3">
            <!-- Quantity selector -->
            <div class="flex items-center gap-3">
              <p class="text-sm font-semibold text-[--color-text] w-20">{{ $t('product.quantity') }}</p>
              <div class="flex items-center border border-[--color-border] rounded-full overflow-hidden">
                <button
                  class="w-10 h-10 flex items-center justify-center text-[--color-text-muted]
                         hover:bg-[--color-background-soft] transition-colors disabled:opacity-40"
                  :disabled="quantity <= 1"
                  @click="quantity = Math.max(1, quantity - 1)"
                >
                  <Icon name="lucide:minus" class="w-4 h-4" />
                </button>
                <span class="w-10 text-center font-medium text-sm">{{ quantity }}</span>
                <button
                  class="w-10 h-10 flex items-center justify-center text-[--color-text-muted]
                         hover:bg-[--color-background-soft] transition-colors disabled:opacity-40"
                  :disabled="maxQty !== null && quantity >= maxQty"
                  @click="quantity++"
                >
                  <Icon name="lucide:plus" class="w-4 h-4" />
                </button>
              </div>
            </div>

            <!-- CTA -->
            <button
              v-if="!selectedVariant || selectedVariant.inStock"
              class="btn-primary w-full py-4 text-base"
              :disabled="!canAddToCart"
              @click="addToCart"
            >
              <Icon name="lucide:shopping-bag" class="w-5 h-5" />
              <span v-if="!hasVariants || selectedVariant">{{ $t('product.add_to_cart') }}</span>
              <span v-else>{{ $t('product.choose_variant') }}</span>
            </button>

            <!-- WhatsApp CTA — out of stock -->
            <a
              v-else
              :href="whatsappOutOfStockUrl"
              target="_blank"
              rel="noopener"
              class="flex items-center justify-center gap-2 w-full py-4 rounded-full text-base font-semibold
                     bg-[#25D366] text-white hover:bg-[#1ebe5a] transition-colors"
            >
              <Icon name="simple-icons:whatsapp" class="w-5 h-5" />
              {{ $t('product.whatsapp_contact') }}
            </a>

            <Transition name="fade">
              <p v-if="addedFeedback" class="text-center text-sm text-green-600 font-medium animate-fade-in">
                {{ $t('product.added_to_cart') }}
              </p>
            </Transition>
          </div>

          <!-- Click & Collect banner -->
          <div class="flex items-center gap-3 bg-amber-50 border border-amber-200 rounded-xl px-4 py-3">
            <Icon name="lucide:store" class="w-5 h-5 text-amber-600 shrink-0" />
            <div class="text-sm">
              <p class="font-semibold text-amber-800">{{ $t('product.click_collect_badge') }}</p>
              <p class="text-amber-700">{{ $t('product.click_collect_detail') }}</p>
            </div>
          </div>

          <!-- Description -->
          <div v-if="product.description" class="pt-4 border-t border-[--color-border]">
            <p class="text-sm font-semibold text-[--color-text] mb-2">{{ $t('product.description') }}</p>
            <p class="text-sm text-[--color-text-muted] leading-relaxed whitespace-pre-line">
              {{ product.description }}
            </p>
          </div>

          <!-- Safety info -->
          <div class="bg-[--color-background-soft] rounded-xl p-4 text-sm text-[--color-text-muted] space-y-1">
            <p class="flex items-center gap-2">
              <Icon name="lucide:shield-check" class="w-4 h-4 text-[--color-primary]" />
              {{ $t('product.trust_materials') }}
            </p>
            <p v-if="stripeEnabled" class="flex items-center gap-2">
              <Icon name="lucide:truck" class="w-4 h-4 text-[--color-primary]" />
              {{ $t('product.trust_shipping') }}
            </p>
            <p class="flex items-center gap-2">
              <Icon name="lucide:lock" class="w-4 h-4 text-[--color-primary]" />
              {{ stripeEnabled ? $t('product.trust_payment') : $t('product.trust_payment_no_stripe') }}
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- ── Cross-sell ─────────────────────────────────────────── -->
    <div v-if="relatedProducts && relatedProducts.length" class="container-site pb-16">
      <h2 class="text-lg font-semibold text-[--color-text] mb-6">Vous aimerez aussi</h2>
      <div class="grid grid-cols-2 sm:grid-cols-4 gap-4">
        <ProductCard v-for="p in relatedProducts" :key="p.id" :product="p" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ProductDetail, ProductSummary, ProductVariant } from '~/types'

const route = useRoute()
const localePath = useLocalePath()
const cart = useCartStore()
const { get } = useApi()
const { format } = usePrice()
const { public: { stripeEnabled } } = useRuntimeConfig()

// ── Fetch product + related en parallèle ──────────────────────
const [{ data: product }, { data: relatedProducts }] = await Promise.all([
  useAsyncData(
    `product-${route.params.slug}`,
    () => get<ProductDetail>(`/api/products/${route.params.slug}`),
    { default: () => null as unknown as ProductDetail }
  ),
  useAsyncData(
    `related-${route.params.slug}`,
    () => get<ProductSummary[]>(`/api/products/${route.params.slug}/related?limit=4`),
    { default: () => [] as ProductSummary[] }
  ),
])

if (!product.value) {
  await showError({ statusCode: 404, statusMessage: 'Produit introuvable' })
}

// ── Gallery ────────────────────────────────────────────────────
const allImages = computed(() =>
  [...(product.value?.images ?? [])].sort((a, b) => (a.position ?? 0) - (b.position ?? 0))
)

// Images à afficher : celles de la variante sélectionnée, sinon celles sans variante, sinon toutes
const sortedImages = computed(() => {
  const all = allImages.value
  if (!selectedVariant.value) {
    const noVariant = all.filter((i) => !i.variantId)
    return noVariant.length ? noVariant : all
  }
  const variantImgs = all.filter((i) => i.variantId === selectedVariant.value!.id)
  if (variantImgs.length) return variantImgs
  const noVariant = all.filter((i) => !i.variantId)
  return noVariant.length ? noVariant : all
})
const activeImageId = ref<number | null>(null)
const currentImage = computed(() =>
  sortedImages.value.find((i) => i.id === activeImageId.value) ?? sortedImages.value[0] ?? null
)

const galleryEl = ref<HTMLElement | null>(null)

// Swipe tactile sur mobile
const { direction } = useSwipe(galleryEl, {
  onSwipeEnd() {
    const images = sortedImages.value
    if (images.length <= 1) return
    const idx = images.findIndex((i) => i.id === activeImageId.value)
    if (direction.value === 'left' && idx < images.length - 1) {
      activeImageId.value = images[idx + 1].id
    } else if (direction.value === 'right' && idx > 0) {
      activeImageId.value = images[idx - 1].id
    }
  },
})

// ── Variants ───────────────────────────────────────────────────
const activeVariants = computed(() =>
  (product.value?.variants ?? []).filter((v) => v.active)
)
const hasVariants = computed(() => activeVariants.value.length > 0)

const availableSizes = computed(() =>
  [...new Set(activeVariants.value.map((v) => v.size).filter(Boolean) as string[])]
)
const availableColors = computed(() => {
  const base = selectedSize.value
    ? activeVariants.value.filter((v) => v.size === selectedSize.value)
    : activeVariants.value
  return [...new Set(base.map((v) => v.color).filter(Boolean) as string[])]
})

const selectedSize = ref<string | null>(availableSizes.value[0] ?? null)
const selectedColor = ref<string | null>(null)

const selectedVariant = computed<ProductVariant | null>(() => {
  if (!hasVariants.value) return null
  return activeVariants.value.find((v) => {
    const sizeOk = !selectedSize.value || v.size === selectedSize.value
    const colorOk = !selectedColor.value || v.color === selectedColor.value
    return sizeOk && colorOk
  }) ?? null
})

// Reset galerie à chaque changement de variante (doit être après selectedVariant)
watch(sortedImages, (imgs) => {
  activeImageId.value = imgs[0]?.id ?? null
}, { immediate: true })

// ── Price display ──────────────────────────────────────────────
const priceRange = computed(() => {
  const prices = activeVariants.value.map((v) => v.priceCents)
  if (!prices.length) return ''
  const min = Math.min(...prices)
  const max = Math.max(...prices)
  return min === max ? format(min) : `${format(min)} – ${format(max)}`
})

// ── Cart ───────────────────────────────────────────────────────
const quantity = ref(1)
const maxQty = computed(() => selectedVariant.value?.availableStock ?? null)
const canAddToCart = computed(() =>
  !hasVariants.value
    ? false
    : !!selectedVariant.value && selectedVariant.value.inStock
)

const whatsappOutOfStockUrl = computed(() => {
  const v = selectedVariant.value
  const p = product.value
  if (!v || !p) return 'https://wa.me/212781570083'
  const variantLabel = [v.size, v.color].filter(Boolean).join(' · ') || v.sku
  const msg = encodeURIComponent(
    `Bonjour, je suis intéressé(e) par "${p.name}" (${variantLabel} — réf. ${v.sku}) mais il est indiqué hors stock. Est-il disponible prochainement ?`
  )
  return `https://wa.me/212781570083?text=${msg}`
})

const addedFeedback = ref(false)

function addToCart() {
  if (!selectedVariant.value || !product.value) return
  const v = selectedVariant.value
  const p = product.value

  const label = [v.size, v.color].filter(Boolean).join(' · ') || v.sku

  cart.add({
    variantId: v.id,
    productId: p.id,
    productName: p.name,
    variantLabel: label,
    sku: v.sku,
    price: v.priceCents,
    quantity: quantity.value,
    stock: v.stock,
    imageUrl: sortedImages.value[0]?.r2Url ?? null,
  })

  addedFeedback.value = true
  setTimeout(() => { addedFeedback.value = false }, 2000)
}

// ── SEO ───────────────────────────────────────────────────────
useSeoMeta({
  title: product.value?.metaTitle ?? `${product.value?.name} — Piercing by Samar`,
  description: product.value?.metaDescription ?? product.value?.description ?? undefined,
  ogTitle: product.value?.name,
  ogImage: product.value?.images?.[0]?.r2Url,
})
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active { transition: opacity 0.3s; }
.fade-enter-from,
.fade-leave-to    { opacity: 0; }
</style>
