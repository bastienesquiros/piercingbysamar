<template>
  <div class="min-h-screen flex flex-col bg-[--color-background]">
    <AppHeader />
    <main class="flex-1 pb-16 md:pb-0">
      <slot />
    </main>
    <AppFooter />
    <BottomNav />
    <CartDrawer />
    <ToastContainer />
  </div>
</template>

<script setup lang="ts">
const currencyStore = useCurrencyStore()
const { error: toastError } = useToast()

onMounted(() => {
  currencyStore.fetchRates()
})

onErrorCaptured((err) => {
  console.error('[app error]', err)
  toastError('Une erreur inattendue est survenue.')
  return false
})
</script>
