<template>
  <div class="flex gap-6 md:gap-8 overflow-x-auto scrollbar-hide px-2 py-4 justify-start md:justify-center">
    <NuxtLink
      v-for="cat in categories"
      :key="cat.slug"
      :to="localePath(`/catalogue?category=${cat.slug}`)"
      class="category-circle group"
      :class="{ active: route.query.category === cat.slug }"
    >
      <div class="category-circle-img relative">
        <NuxtImg
          :src="`/images/categories/${cat.slug}.avif`"
          :alt="cat.label"
          width="96"
          height="96"
          class="w-full h-full object-cover"
          loading="lazy"
        />
        <!-- Tooltip -->
        <div class="
          pointer-events-none absolute -top-10 left-1/2 -translate-x-1/2
          whitespace-nowrap bg-[--color-text] text-white text-[10px] px-2 py-1 rounded-md
          opacity-0 group-hover:opacity-100 transition-opacity duration-200 z-10
          before:content-[''] before:absolute before:top-full before:left-1/2 before:-translate-x-1/2
          before:border-4 before:border-transparent before:border-t-[--color-text]
        ">
          {{ cat.placement }}
        </div>
      </div>
      <span class="category-circle-label">{{ cat.label }}</span>
    </NuxtLink>
  </div>
</template>

<script setup lang="ts">
const localePath = useLocalePath()
const route = useRoute()

const categories = [
  { slug: 'lobe',   label: 'Lobe',       placement: 'Lobe de l\'oreille' },
  { slug: 'helix',  label: 'Hélix',      placement: 'Cartilage supérieur' },
  { slug: 'conch',  label: 'Conch',      placement: 'Conque de l\'oreille' },
  { slug: 'daith',  label: 'Daith',      placement: 'Pli de l\'oreille' },
  { slug: 'tragus', label: 'Tragus',     placement: 'Petite saillie avant' },
  { slug: 'indu',   label: 'Industriel', placement: 'Barre cartilage' },
  { slug: 'rock',   label: 'Rock',       placement: 'Conque externe' },
]
</script>
