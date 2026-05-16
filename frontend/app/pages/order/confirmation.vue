<template>
  <div class="min-h-screen bg-[--color-background-soft] flex flex-col items-center justify-center py-16 px-4">
    <div class="w-full max-w-lg">

      <!-- Loading -->
      <div v-if="pending" class="text-center py-20">
        <Icon name="lucide:loader-2" class="w-10 h-10 text-[--color-primary] animate-spin mx-auto mb-4" />
        <p class="text-[--color-text-muted]">Vérification de votre commande…</p>
      </div>

      <!-- Error -->
      <div v-else-if="error || !order" class="text-center py-20">
        <Icon name="lucide:alert-circle" class="w-10 h-10 text-red-400 mx-auto mb-4" />
        <p class="text-[--color-text] font-semibold mb-2">Commande introuvable</p>
        <NuxtLink :to="localePath('/')" class="btn-outline mt-4">Retour à l'accueil</NuxtLink>
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
          Un email de confirmation a été envoyé à
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
              {{ order.orderType === 'SHIPPING' ? 'Livraison à domicile' : 'Click & Collect' }}
            </span>
            <span class="badge text-xs">{{ statusLabel }}</span>
          </div>

          <!-- Items -->
          <ul class="space-y-3 mb-4">
            <li v-for="item in order.items" :key="item.id" class="flex justify-between text-sm">
              <span class="text-[--color-text]">
                {{ item.snapshotName }}
                <span class="text-[--color-text-muted]">× {{ item.quantity }}</span>
              </span>
              <span class="font-medium text-[--color-text]">{{ format(item.lineTotalCents) }}</span>
            </li>
          </ul>

          <!-- Total -->
          <div class="flex justify-between font-semibold text-[--color-text] pt-3 border-t border-[--color-border]">
            <span>Total payé</span>
            <span class="font-serif text-lg">{{ format(order.totalCents) }}</span>
          </div>

          <!-- Shipping address -->
          <div v-if="order.orderType === 'SHIPPING' && order.shippingAddress" class="mt-4 pt-4 border-t border-[--color-border] text-sm text-[--color-text-muted]">
            <p class="font-semibold text-[--color-text] mb-1 flex items-center gap-1.5">
              <Icon name="lucide:map-pin" class="w-3.5 h-3.5" />
              Adresse de livraison
            </p>
            <p>{{ order.shippingAddress }}</p>
            <p>{{ order.shippingPostalCode }} {{ order.shippingCity }}</p>
            <p>{{ order.shippingCountry }}</p>
          </div>

          <!-- Click & Collect info -->
          <div v-if="order.orderType === 'CLICK_COLLECT'" class="mt-4 pt-4 border-t border-[--color-border] text-sm text-[--color-text-muted]">
            <p class="font-semibold text-[--color-text] mb-1 flex items-center gap-1.5">
              <Icon name="lucide:store" class="w-3.5 h-3.5" />
              Retrait en boutique
            </p>
            <p>Marrakech · Vous serez contacté·e dès que votre commande sera prête.</p>
            <p class="mt-1">Paiement sur place : liquide ou carte.</p>
          </div>
        </div>

        <!-- CTA -->
        <div class="flex flex-col sm:flex-row gap-3 justify-center">
          <NuxtLink :to="localePath('/catalogue')" class="btn-outline">
            <Icon name="lucide:shopping-bag" class="w-4 h-4" />
            Continuer mes achats
          </NuxtLink>
          <NuxtLink :to="localePath('/')" class="btn-ghost">
            <Icon name="lucide:home" class="w-4 h-4" />
            Accueil
          </NuxtLink>
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
const { format } = usePrice()

const reference = route.query.reference as string

const { data: order, pending, error } = await useAsyncData(
  `order-${reference}`,
  () => reference ? get<Order>(`/api/orders/${reference}`) : Promise.reject(new Error('No reference')),
)

const statusLabel = computed(() => {
  const labels: Record<string, string> = {
    PENDING: 'En attente',
    PAID: 'Payée',
    SHIPPED: 'Expédiée',
    DELIVERED: 'Livrée',
    CANCELLED: 'Annulée',
    CLICK_COLLECT_PENDING: 'En attente',
    READY: 'Prête à retirer',
    COLLECTED: 'Retirée',
  }
  return labels[order.value?.status ?? ''] ?? order.value?.status ?? ''
})

useSeoMeta({ title: 'Commande confirmée — Piercing by Samar', robots: 'noindex' })
</script>
