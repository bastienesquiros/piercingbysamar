<template>
  <div class="space-y-6">
    <h2 class="text-xl font-semibold text-gray-800">Bonjour 👋</h2>

    <!-- Stats cards -->
    <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
      <div v-for="stat in stats" :key="stat.label" class="bg-white rounded-xl border border-gray-200 p-5">
        <div class="flex items-center justify-between mb-3">
          <p class="text-sm text-gray-500">{{ stat.label }}</p>
          <div class="w-9 h-9 rounded-lg flex items-center justify-center" :class="stat.iconBg">
            <Icon :name="stat.icon" class="w-4 h-4" :class="stat.iconColor" />
          </div>
        </div>
        <p class="text-2xl font-semibold text-gray-800">{{ stat.value }}</p>
      </div>
    </div>

    <!-- Recent orders -->
    <div class="bg-white rounded-xl border border-gray-200">
      <div class="flex items-center justify-between px-5 py-4 border-b border-gray-100">
        <h3 class="font-semibold text-gray-700">Dernières commandes</h3>
        <NuxtLink to="/admin/orders" class="text-xs text-[--color-primary-dark] hover:underline">
          Voir tout
        </NuxtLink>
      </div>

      <div v-if="pendingOrders" class="p-8 text-center text-gray-400 text-sm">
        <Icon name="lucide:loader-2" class="w-5 h-5 animate-spin mx-auto mb-2" />
        Chargement…
      </div>

      <div v-else-if="!recentOrders.length" class="p-8 text-center text-gray-400 text-sm">
        Aucune commande pour le moment.
      </div>

      <!-- Mobile: cards + Desktop: table (v-else block) -->
      <template v-else>
        <ul class="divide-y divide-gray-100 sm:hidden">
          <li v-for="order in recentOrders" :key="order.reference" class="px-5 py-3 space-y-1">
            <div class="flex items-center justify-between">
              <span class="font-mono text-xs text-gray-500">{{ order.reference }}</span>
              <StatusBadge :status="order.status" />
            </div>
            <p class="text-sm font-medium text-gray-800">{{ order.customerName }}</p>
            <div class="flex items-center justify-between text-xs text-gray-500">
              <span class="flex items-center gap-1">
                <Icon :name="order.orderType === 'SHIPPING' ? 'lucide:truck' : 'lucide:store'" class="w-3.5 h-3.5" />
                {{ order.orderType === 'SHIPPING' ? 'Livraison' : 'C&C' }}
              </span>
              <span class="font-medium text-gray-700">{{ formatPrice(order.totalCents) }}</span>
            </div>
          </li>
        </ul>

        <table class="hidden sm:table w-full text-sm">
          <thead class="bg-gray-50 text-gray-500 text-xs uppercase tracking-wide">
            <tr>
              <th class="text-left px-5 py-3">Référence</th>
              <th class="text-left px-5 py-3">Client</th>
              <th class="text-left px-5 py-3">Type</th>
              <th class="text-left px-5 py-3">Total</th>
              <th class="text-left px-5 py-3">Statut</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="order in recentOrders" :key="order.reference" class="hover:bg-gray-50 transition-colors">
              <td class="px-5 py-3 font-mono text-xs text-gray-600">{{ order.reference }}</td>
              <td class="px-5 py-3 text-gray-800">{{ order.customerName }}</td>
              <td class="px-5 py-3">
                <span class="inline-flex items-center gap-1 text-xs text-gray-500">
                  <Icon :name="order.orderType === 'SHIPPING' ? 'lucide:truck' : 'lucide:store'" class="w-3.5 h-3.5" />
                  {{ order.orderType === 'SHIPPING' ? 'Livraison' : 'C&C' }}
                </span>
              </td>
              <td class="px-5 py-3 font-medium text-gray-800">{{ formatPrice(order.totalCents) }}</td>
              <td class="px-5 py-3">
                <StatusBadge :status="order.status" />
              </td>
            </tr>
          </tbody>
        </table>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Order, PageResponse } from '~/types'

definePageMeta({ layout: 'admin', middleware: 'admin' })

const auth = useAuthStore()
const { get } = useApi()

const headers = computed(() => auth.authHeader)

const { data: ordersData, pending: pendingOrders } = await useAsyncData('admin-orders-dash', () =>
  get<PageResponse<Order>>('/api/admin/orders', { headers: headers.value, query: { page: 0, size: 5 } })
)

const recentOrders = computed(() => ordersData.value?.content ?? [])

const stats = computed(() => [
  {
    label: 'Commandes totales',
    value: ordersData.value?.totalElements ?? '—',
    icon: 'lucide:shopping-cart',
    iconBg: 'bg-blue-50',
    iconColor: 'text-blue-500',
  },
  {
    label: 'En attente',
    value: recentOrders.value.filter((o) => ['PENDING', 'CLICK_COLLECT_PENDING'].includes(o.status)).length,
    icon: 'lucide:clock',
    iconBg: 'bg-amber-50',
    iconColor: 'text-amber-500',
  },
  {
    label: 'Payées',
    value: recentOrders.value.filter((o) => o.status === 'PAID').length,
    icon: 'lucide:check-circle',
    iconBg: 'bg-green-50',
    iconColor: 'text-green-500',
  },
  {
    label: 'Click & Collect',
    value: recentOrders.value.filter((o) => o.orderType === 'CLICK_COLLECT').length,
    icon: 'lucide:store',
    iconBg: 'bg-purple-50',
    iconColor: 'text-purple-500',
  },
])

function formatPrice(cents: number) {
  if (!cents && cents !== 0) return '—'
  return Math.round(cents / 100) + ' MAD'
}

useSeoMeta({ title: 'Dashboard — Admin', robots: 'noindex' })
</script>
