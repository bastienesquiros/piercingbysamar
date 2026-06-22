<template>
  <nav class="md:hidden fixed bottom-0 left-0 right-0 z-50 bg-white border-t border-[--color-border] safe-area-bottom">
    <div class="grid grid-cols-4 h-16">

      <!-- Liens classiques -->
      <NuxtLink
        v-for="item in links"
        :key="item.to"
        :to="item.to"
        class="flex flex-col items-center justify-center gap-1 text-[--color-text-muted] transition-colors relative"
        :class="isActive(item.to) ? 'text-[--color-primary-dark]' : 'hover:text-[--color-text]'"
      >
        <Icon :name="item.icon" class="w-5 h-5" />
        <span class="text-[10px] font-medium leading-none">{{ item.label }}</span>
        <span
          v-if="isActive(item.to)"
          class="absolute top-0 left-1/2 -translate-x-1/2 w-6 h-0.5 rounded-full bg-[--color-primary]"
        />
      </NuxtLink>

      <!-- Panier (ouvre le drawer) -->
      <button
        class="flex flex-col items-center justify-center gap-1 text-[--color-text-muted] hover:text-[--color-text] transition-colors relative"
        @click="cart.toggleCart()"
      >
        <div class="relative">
          <Icon name="lucide:shopping-bag" class="w-5 h-5" />
          <span
            v-if="cart.count > 0"
            class="absolute -top-1.5 -right-1.5 min-w-[16px] h-4 px-0.5 rounded-full
                   bg-[--color-primary] text-white text-[10px] font-bold
                   flex items-center justify-center leading-none"
          >
            {{ cart.count }}
          </span>
        </div>
        <span class="text-[10px] font-medium leading-none">{{ $t('nav.cart') }}</span>
      </button>

    </div>
  </nav>
</template>

<script setup lang="ts">
const { t } = useI18n()
const localePath = useLocalePath()
const route = useRoute()
const cart = useCartStore()

const links = computed(() => [
  { to: localePath('/'),          icon: 'lucide:home',     label: t('nav.home') },
  { to: localePath('/catalogue'), icon: 'lucide:grid-2x2', label: t('nav.catalogue') },
  { to: localePath('/rdv'),       icon: 'lucide:calendar', label: t('nav.rdv_short') },
])

function isActive(to: string) {
  if (to === localePath('/')) return route.path === to
  return route.path === to || route.path.startsWith(to + '/')
}
</script>

<style scoped>
.safe-area-bottom {
  padding-bottom: env(safe-area-inset-bottom);
}
</style>
