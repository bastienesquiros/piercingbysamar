<template>
  <div>
    <!-- Hero -->
    <section class="relative overflow-hidden bg-[--color-background-soft] py-20 md:py-32">
      <div class="container-site text-center">
        <p class="text-xs uppercase tracking-[0.3em] text-[--color-text-muted] mb-4">Marrakech · Maroc</p>
        <h1 class="font-serif text-5xl md:text-7xl font-semibold text-[--color-text] mb-4">
          {{ $t('home.hero_title') }}
        </h1>
        <p class="text-[--color-text-muted] text-base md:text-lg mb-8 max-w-md mx-auto">
          {{ $t('home.hero_subtitle') }}
        </p>
        <NuxtLink :to="localePath('/catalogue')" class="btn-primary text-base px-8 py-3">
          {{ $t('home.view_all') }}
        </NuxtLink>
      </div>
    </section>

    <!-- Trust bar: Click & Collect + Shipping + Payment -->
    <div class="bg-white border-b border-[--color-border]">
      <div class="container-site">
        <div class="grid grid-cols-1 sm:grid-cols-3 divide-y sm:divide-y-0 sm:divide-x divide-[--color-border] py-3">
          <div class="flex items-center justify-center gap-2.5 py-2 sm:py-0 text-sm text-[--color-text-muted]">
            <Icon name="lucide:store" class="w-4 h-4 text-[--color-primary] shrink-0" />
            <span>{{ $t('product.click_collect_badge') }}</span>
          </div>
          <div class="flex items-center justify-center gap-2.5 py-2 sm:py-0 text-sm text-[--color-text-muted]">
            <Icon name="lucide:truck" class="w-4 h-4 text-[--color-primary] shrink-0" />
            <span>{{ $t('home.trust_shipping') }}</span>
          </div>
          <div class="flex items-center justify-center gap-2.5 py-2 sm:py-0 text-sm text-[--color-text-muted]">
            <Icon name="lucide:lock" class="w-4 h-4 text-[--color-primary] shrink-0" />
            <span>{{ stripeEnabled ? $t('home.trust_payment') : $t('home.trust_payment_no_stripe') }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Shop by body area -->
    <section class="py-16">
      <div class="container-site">
        <h2 class="section-title text-center mb-10">{{ $t('home.shop_by') }}</h2>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-4 md:gap-6">
          <NuxtLink
            v-for="cat in topLevel"
            :key="cat.slug"
            :to="localePath(`/catalogue?category=${cat.slug}`)"
            class="group relative overflow-hidden rounded-2xl aspect-square flex items-end p-4 cursor-pointer"
          >
            <!-- Image si dispo, gradient sinon -->
            <NuxtImg
              v-if="categoryImage(cat.slug)"
              :src="categoryImage(cat.slug)!"
              :alt="cat.name"
              class="absolute inset-0 w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
              width="400" height="400"
            />
            <div
              v-else
              class="absolute inset-0 transition-transform duration-500 group-hover:scale-105"
              :style="categoryGradient(cat.slug)"
            />
            <!-- Overlay -->
            <div class="absolute inset-0 bg-gradient-to-t from-black/60 via-black/10 to-transparent" />
            <!-- Label -->
            <div class="relative z-10">
              <p class="font-serif text-2xl font-semibold text-white leading-tight">
                {{ $t('categories.' + cat.slug, cat.name) }}
              </p>
              <p class="text-white/70 text-xs mt-0.5">
                {{ cat.children?.length
                  ? $t('footer.placements', { n: cat.children.length })
                  : $t('footer.discover') }}
              </p>
            </div>
            <!-- Arrow -->
            <div class="absolute top-4 right-4 z-10 opacity-0 group-hover:opacity-100 transition-opacity">
              <Icon name="lucide:arrow-right" class="w-5 h-5 text-white" />
            </div>
          </NuxtLink>
        </div>
      </div>
    </section>

    <!-- Featured products -->
    <section class="py-16 bg-[--color-background-soft]">
      <div class="container-site">
        <div class="flex items-baseline justify-between mb-8">
          <h2 class="section-title">{{ $t('home.new_arrivals') }}</h2>
        </div>

        <!-- Loading skeleton -->
        <div v-if="pending" class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div v-for="n in 8" :key="n" class="aspect-square rounded-xl bg-[--color-background-warm] animate-pulse" />
        </div>

        <!-- Product grid -->
        <div v-else-if="products?.content?.length" class="grid grid-cols-2 md:grid-cols-4 gap-4">
          <ProductCard
            v-for="product in products.content"
            :key="product.id"
            :product="product"
          />
        </div>

        <!-- CTA -->
        <div class="mt-10 text-center">
          <NuxtLink
            :to="localePath('/catalogue')"
            class="btn-outline text-base px-10 py-3 inline-flex items-center gap-2"
          >
            {{ $t('home.view_catalogue') }}
            <Icon name="lucide:arrow-right" class="w-4 h-4" />
          </NuxtLink>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import type { PageResponse, ProductSummary } from '~/types'

const localePath = useLocalePath()
const { get } = useApi()
const { topLevel, fetchCategories } = useCategories()
const { categoryImage } = useCategoryImage()
const { public: { stripeEnabled } } = useRuntimeConfig()

onMounted(() => fetchCategories())

const { data: products, pending } = await useAsyncData(
  'home-products',
  () => get<PageResponse<ProductSummary>>('/api/products', { query: { page: 0, size: 8 } }),
  { lazy: true }
)

const GRADIENTS: Record<string, string> = {
  nez:     'background: linear-gradient(135deg, #b8a99a 0%, #8c7b6e 100%)',
  labret:  'background: linear-gradient(135deg, #c4a882 0%, #9c7a4e 100%)',
  nombril: 'background: linear-gradient(135deg, #a8b8a0 0%, #6e8c6a 100%)',
}

function categoryGradient(slug: string) {
  return GRADIENTS[slug] ?? 'background: linear-gradient(135deg, #c0b0a0 0%, #8c7a6a 100%)'
}

useSeoMeta({
  title: 'Piercing by Samar — Bijoux & Piercings Titane',
  description: 'Bijoux et piercings en titane ASTM F136 et acier chirurgical. Nickel Free, qualité premium. Click & Collect Marrakech ou livraison mondiale.',
  ogTitle: 'Piercing by Samar',
  ogDescription: 'Bijoux & piercings premium. Titane ASTM F136 · Nickel Free · Marrakech',
})
</script>
