<template>
  <div class="min-h-screen bg-[--color-background-soft] flex flex-col items-center justify-center py-16 px-4">
    <div class="w-full max-w-lg">

      <!-- Loading -->
      <div v-if="pending" class="text-center py-20">
        <Icon name="lucide:loader-2" class="w-10 h-10 text-[--color-primary] animate-spin mx-auto mb-4" />
        <p class="text-[--color-text-muted]">{{ $t('order.loading') }}</p>
      </div>

      <!-- Error -->
      <div v-else-if="error || !order" class="text-center py-20">
        <Icon name="lucide:alert-circle" class="w-10 h-10 text-red-400 mx-auto mb-4" />
        <p class="text-[--color-text] font-semibold mb-2">{{ $t('order.not_found') }}</p>
        <NuxtLink :to="localePath('/')" class="btn-outline mt-4">{{ $t('order.back_home') }}</NuxtLink>
      </div>

      <!-- Success -->
      <div v-else class="text-center animate-fade-in">
        <!-- Checkmark -->
        <div class="w-20 h-20 rounded-full bg-[--color-primary-light] flex items-center justify-center mx-auto mb-6">
          <Icon name="lucide:check" class="w-10 h-10 text-[--color-primary-dark]" />
        </div>

        <h1 class="font-serif text-3xl font-semibold text-[--color-text] mb-2">
          {{ $t('order.confirmed') }} 🎉
        </h1>
        <p class="text-[--color-text-muted] mb-1">
          {{ $t('order.confirmation_email') }}
          <strong class="text-[--color-text]">{{ order.customerEmail }}</strong>
        </p>
        <p class="text-sm text-[--color-text-muted] mb-8">
          {{ $t('order.reference') }} :
          <span class="font-mono font-semibold text-[--color-text] tracking-wider">{{ order.reference }}</span>
        </p>

        <!-- Order summary card -->
        <div class="bg-white rounded-2xl border border-[--color-border] p-6 text-left mb-6">
          <!-- Type badge -->
          <div class="flex items-center justify-between mb-4">
            <span class="badge-primary text-xs">
              <Icon
                :name="order.orderType === 'SHIPPING' ? 'lucide:truck' : 'lucide:store'"
                class="w-3 h-3 mr-1"
              />
              {{ order.orderType === 'SHIPPING' ? $t('order.type_shipping') : $t('order.type_click_collect') }}
            </span>
            <span class="badge text-xs">{{ statusLabel }}</span>
          </div>

          <!-- Items -->
          <ul class="space-y-3 mb-4">
            <li v-for="item in order.items" :key="item.id" class="flex justify-between text-sm">
              <span class="text-[--color-text]">
                {{ item.snapshotProductName }}
                <span v-if="item.snapshotVariantLabel" class="text-[--color-text-muted]"> · {{ item.snapshotVariantLabel }}</span>
                <span class="text-[--color-text-muted]"> × {{ item.quantity }}</span>
              </span>
              <span class="font-medium text-[--color-text]">{{ formatOrder(item.totalCents) }}</span>
            </li>
          </ul>

          <!-- Total -->
          <div class="flex justify-between font-semibold text-[--color-text] pt-3 border-t border-[--color-border]">
            <span>{{ order.orderType === 'CLICK_COLLECT' ? $t('order.to_pay_in_store') : $t('order.total_paid') }}</span>
            <span class="font-serif text-lg">{{ formatOrder(order.totalCents) }}</span>
          </div>

          <!-- Shipping address -->
          <div v-if="order.orderType === 'SHIPPING' && order.shippingAddress" class="mt-4 pt-4 border-t border-[--color-border] text-sm text-[--color-text-muted]">
            <p class="font-semibold text-[--color-text] mb-1 flex items-center gap-1.5">
              <Icon name="lucide:map-pin" class="w-3.5 h-3.5" />
              {{ $t('order.shipping_address') }}
            </p>
            <p>{{ order.shippingAddress }}</p>
            <p>{{ order.shippingPostalCode }} {{ order.shippingCity }}</p>
            <p>{{ order.shippingCountry }}</p>
          </div>

          <!-- Click & Collect info -->
          <div v-if="order.orderType === 'CLICK_COLLECT'" class="mt-4 pt-4 border-t border-[--color-border] text-sm text-[--color-text-muted]">
            <p class="font-semibold text-[--color-text] mb-1 flex items-center gap-1.5">
              <Icon name="lucide:store" class="w-3.5 h-3.5" />
              {{ $t('order.collect_title') }}
            </p>
            <p>{{ $t('order.collect_info') }}</p>
            <p class="mt-1">{{ $t('order.collect_payment') }}</p>
          </div>
        </div>

        <!-- CTA -->
        <div class="flex flex-col sm:flex-row gap-3 justify-center mt-2 mb-4">
          <NuxtLink :to="localePath('/catalogue')" class="btn-outline">
            <Icon name="lucide:shopping-bag" class="w-4 h-4" />
            {{ $t('order.continue_shopping') }}
          </NuxtLink>
          <NuxtLink :to="localePath('/')" class="btn-outline">
            <Icon name="lucide:home" class="w-4 h-4" />
            {{ $t('order.home') }}
          </NuxtLink>
        </div>

        <!-- WhatsApp aide -->
        <div class="border-t border-[--color-border] pt-6 mb-6">
          <a :href="whatsappUrl" target="_blank" rel="noopener" class="inline-flex items-center gap-2 text-sm text-[--color-text-muted] hover:text-[--color-text] transition-colors">
            <Icon name="simple-icons:whatsapp" class="w-4 h-4 text-green-500" />
            {{ $t('order.whatsapp_question') }}
          </a>
        </div>

        <!-- Flow 2 : RDV post-achat -->
        <div class="bg-[--color-primary-light] rounded-2xl border border-[--color-border] p-6 text-left">
          <div class="flex items-start gap-4">
            <div class="w-10 h-10 rounded-full bg-[--color-primary]/20 flex items-center justify-center shrink-0 mt-0.5">
              <Icon name="lucide:sparkles" class="w-5 h-5 text-[--color-primary-dark]" />
            </div>
            <div class="flex-1">
              <p class="font-semibold text-[--color-text] mb-1">{{ $t('order.rdv_upsell_title') }}</p>
              <p class="text-sm text-[--color-text-muted] mb-4">{{ $t('order.rdv_upsell_subtitle') }}</p>
              <NuxtLink
                :to="localePath('/rdv')"
                class="btn-primary py-2.5 px-5 text-sm inline-flex items-center gap-2"
              >
                <Icon name="simple-icons:whatsapp" class="w-4 h-4" />
                {{ $t('nav.rdv') }}
              </NuxtLink>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Order } from '~/types'

definePageMeta({ layout: false })

const route = useRoute()
const localePath = useLocalePath()
const { get } = useApi()
const { formatAs } = usePrice()

// Formate dans la devise réelle de la commande (stockée en BDD)
function formatOrder(cents: number): string {
  if (!order.value) return ''
  return formatAs(cents, order.value.currency)
}

const reference = route.query.reference as string

const { data: order, pending, error } = await useAsyncData(
  `order-${reference}`,
  () => reference ? get<Order>(`/api/orders/${reference}`) : Promise.reject(new Error('No reference')),
)

const { t } = useI18n()

const statusLabel = computed(() => {
  const labels: Record<string, string> = {
    PENDING: t('order.status_pending'),
    PAID: t('order.status_paid'),
    SHIPPED: t('order.status_shipped'),
    DELIVERED: t('order.status_delivered'),
    CANCELLED: t('order.status_cancelled'),
    CLICK_COLLECT_PENDING: t('order.status_pending'),
    READY: t('order.status_ready'),
    COLLECTED: t('order.status_collected'),
  }
  return labels[order.value?.status ?? ''] ?? order.value?.status ?? ''
})

const whatsappUrl = computed(() => {
  const msg = encodeURIComponent(
    `Bonjour, j'ai une question sur ma commande ${reference}.`
  )
  return `https://wa.me/212781570083?text=${msg}`
})

useSeoMeta({ title: $t('order.confirmed') + ' — Piercing by Samar', robots: 'noindex' })
</script>
