<template>
  <div class="min-h-screen flex flex-col bg-[--color-background]">
    <AppHeader />
    <main class="flex-1">
      <slot />
    </main>
    <AppFooter />
    <CartDrawer />
  </div>
</template>

<script setup lang="ts">
const currencyStore = useCurrencyStore()
const config = useRuntimeConfig()

onMounted(() => {
  currencyStore.fetchRates(config.public.apiBase as string)
})
</script>
