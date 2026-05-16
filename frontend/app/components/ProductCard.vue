<template>
  <NuxtLink
    :to="localePath(`/products/${product.slug}`)"
    class="product-card group flex flex-col"
  >
    <!-- Image -->
    <div class="relative aspect-square bg-[--color-background-soft] overflow-hidden">
      <NuxtImg
        v-if="product.coverImageUrl"
        :src="product.coverImageUrl"
        :alt="product.name"
        class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
        loading="lazy"
        width="400"
        height="400"
      />
      <div v-else class="w-full h-full flex items-center justify-center">
        <Icon name="lucide:image" class="w-12 h-12 text-[--color-border]" />
      </div>

      <!-- Badges -->
      <div class="absolute top-3 left-3 flex flex-col gap-1">
        <span v-if="product.nickelFree" class="badge-primary text-[10px] px-2 py-0.5">
          Nickel Free
        </span>
        <span v-if="!product.inStock" class="badge text-[10px] px-2 py-0.5 bg-white/90">
          {{ $t('product.out_of_stock') }}
        </span>
        <span
          v-for="tag in (product.tags ?? []).filter(t => ['Bestseller','Nouveauté','New'].includes(t))"
          :key="tag"
          class="badge text-[10px] px-2 py-0.5 bg-white/90 font-semibold"
        >
          {{ tag }}
        </span>
      </div>
    </div>

    <!-- Info -->
    <div class="p-4 flex flex-col flex-1">
      <p class="text-xs text-[--color-text-muted] mb-1">{{ product.categoryName }}</p>
      <h3 class="font-medium text-[--color-text] line-clamp-2 text-base leading-snug tracking-wide mb-2 flex-1">
        {{ product.name }}
      </h3>

      <!-- Material badge -->
      <p class="text-xs text-[--color-text-muted] mb-3">{{ product.material }}</p>

      <!-- Price + CTA -->
      <div class="flex items-center justify-between">
        <div class="flex flex-col gap-px">
          <span class="text-[10px] text-[--color-text-muted]" v-if="product.maxPriceCents && product.maxPriceCents !== product.minPriceCents">
            {{ $t('product.from') }}
          </span>
          <span class="font-semibold text-[--color-text]">
            {{ format(product.minPriceCents) }}
          </span>
        </div>
        <div
          class="w-8 h-8 rounded-full bg-[--color-primary-light] flex items-center justify-center
                 text-[--color-primary-dark] transition-all duration-200
                 group-hover:bg-[--color-primary] group-hover:text-white"
        >
          <Icon name="lucide:plus" class="w-4 h-4" />
        </div>
      </div>
    </div>
  </NuxtLink>
</template>

<script setup lang="ts">
import type { ProductSummary } from '~/types'

defineProps<{ product: ProductSummary }>()

const localePath = useLocalePath()
const { format, formatRange } = usePrice()
</script>
