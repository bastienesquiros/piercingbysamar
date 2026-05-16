<template>
  <div class="min-h-screen flex items-center justify-center bg-[--color-background-soft]">
    <div class="text-center">
      <Icon name="lucide:loader-2" class="w-10 h-10 text-[--color-primary] animate-spin mx-auto mb-4" />
      <p class="text-[--color-text-muted]">{{ $t('misc.loading_payment') }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ layout: false })

const route = useRoute()
const localePath = useLocalePath()

// Stripe redirects to /order/success?reference=XXX (set in application.properties)
const reference = route.query.reference as string

onMounted(() => {
  navigateTo(localePath(`/order/confirmation?reference=${reference}`), { replace: true })
})
</script>
