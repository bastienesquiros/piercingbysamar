<template>
  <div class="space-y-5">

    <!-- Filter -->
    <div class="flex gap-3 flex-wrap">
      <select v-model="filterStatus" class="input py-2 text-sm">
        <option value="">Tous les statuts</option>
        <option value="PENDING">En attente</option>
        <option value="PAID">Payée</option>
        <option value="READY">Prête (C&amp;C)</option>
        <option value="SHIPPED">Expédiée</option>
        <option value="DELIVERED">Livrée</option>
        <option value="CANCELLED">Annulée</option>
      </select>
      <p class="text-sm text-[--color-text-muted] self-center">{{ data?.totalElements ?? 0 }} commande(s)</p>
    </div>

    <!-- Table -->
    <div class="bg-white rounded-xl border border-[--color-border] overflow-hidden">

      <!-- Loading -->
      <div v-if="pending" class="p-10 text-center text-[--color-text-muted]">
        <Icon name="lucide:loader-2" class="w-6 h-6 animate-spin mx-auto mb-2" />
        Chargement…
      </div>

      <div v-else-if="!orders.length" class="p-10 text-center text-[--color-text-muted]">
        Aucune commande.
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full text-sm">
          <thead class="bg-[--color-background-soft] text-[--color-text-muted] text-xs uppercase tracking-wide border-b border-[--color-border]">
            <tr>
              <th class="text-left px-5 py-3">Référence</th>
              <th class="text-left px-5 py-3">Client</th>
              <th class="text-left px-5 py-3">Email</th>
              <th class="text-left px-5 py-3">Type</th>
              <th class="text-left px-5 py-3">Total</th>
              <th class="text-left px-5 py-3">Statut</th>
              <th class="text-left px-5 py-3">Date</th>
              <th class="px-5 py-3" />
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="order in orders" :key="order.reference" class="hover:bg-[--color-background-soft] transition-colors">
              <td class="px-5 py-3 font-mono text-xs text-[--color-text-muted] whitespace-nowrap">{{ order.reference }}</td>
              <td class="px-5 py-3 text-[--color-text] font-medium whitespace-nowrap">{{ order.customerName }}</td>
              <td class="px-5 py-3 text-[--color-text-muted] text-xs">{{ order.customerEmail }}</td>
              <td class="px-5 py-3">
                <span class="inline-flex items-center gap-1 text-xs text-[--color-text-muted]">
                  <Icon :name="order.orderType === 'SHIPPING' ? 'lucide:truck' : 'lucide:store'" class="w-3.5 h-3.5" />
                  {{ order.orderType === 'SHIPPING' ? 'Livraison' : 'Click & Collect' }}
                </span>
              </td>
              <td class="px-5 py-3 font-semibold text-[--color-text] whitespace-nowrap">{{ formatMad(order.totalCents) }}</td>
              <td class="px-5 py-3">
                <StatusBadge :status="order.status" />
              </td>
              <td class="px-5 py-3 text-[--color-text-muted] text-xs whitespace-nowrap">{{ formatDate(order.createdAt) }}</td>
              <td class="px-5 py-3">
                <button
                  class="text-xs text-[--color-primary-dark] hover:underline"
                  @click="openOrder(order)"
                >
                  Détails
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="data && data.totalPages > 1" class="flex justify-center gap-2">
      <button
        class="px-3 py-1.5 rounded-lg border border-[--color-border] text-sm disabled:opacity-40 hover:bg-[--color-background-soft]"
        :disabled="page === 0"
        @click="page--"
      >
        ← Précédent
      </button>
      <span class="px-3 py-1.5 text-sm text-[--color-text-muted]">{{ page + 1 }} / {{ data.totalPages }}</span>
      <button
        class="px-3 py-1.5 rounded-lg border border-[--color-border] text-sm disabled:opacity-40 hover:bg-[--color-background-soft]"
        :disabled="data.last"
        @click="page++"
      >
        Suivant →
      </button>
    </div>

    <!-- Order detail panel -->
    <Transition name="slide">
      <div v-if="selectedOrder" class="fixed inset-0 z-50 flex justify-end">
        <div class="absolute inset-0 bg-black/30" @click="selectedOrder = null" />
        <aside class="relative w-full max-w-xl bg-white shadow-2xl flex flex-col overflow-y-auto">
          <!-- Header -->
          <div class="flex items-center justify-between px-6 py-5 border-b border-[--color-border] sticky top-0 bg-white z-10">
            <div>
              <p class="font-mono text-sm text-[--color-text-muted]">{{ selectedOrder.reference }}</p>
              <h3 class="font-semibold text-[--color-text]">{{ selectedOrder.customerName }}</h3>
            </div>
            <button class="text-[--color-text-muted] hover:text-[--color-text-muted]" @click="selectedOrder = null">
              <Icon name="lucide:x" class="w-5 h-5" />
            </button>
          </div>

          <!-- Body -->
          <div class="px-6 py-5 space-y-6 flex-1">

            <!-- Status update -->
            <div :class="updatingStatus ? 'opacity-60 pointer-events-none' : ''">
              <p class="text-xs font-semibold text-[--color-text-muted] uppercase tracking-wide mb-2 flex items-center gap-2">
                Changer le statut
                <Icon v-if="updatingStatus" name="lucide:loader-2" class="w-3 h-3 animate-spin text-[--color-text-muted]" />
              </p>
              <div class="flex flex-wrap gap-2">
                <button
                  v-for="s in availableStatuses(selectedOrder)"
                  :key="s.value"
                  class="px-3 py-1.5 rounded-lg border text-xs font-medium transition-colors"
                  :class="selectedOrder.status === s.value
                    ? 'border-[--color-primary] bg-[--color-primary-light] text-[--color-primary-dark]'
                    : 'border-[--color-border] text-[--color-text-muted] hover:border-[--color-primary-light]'"
                  :disabled="updatingStatus"
                  @click="updateStatus(selectedOrder, s.value)"
                >
                  <Icon v-if="updatingStatus && statusTarget === s.value" name="lucide:loader-2" class="w-3 h-3 animate-spin inline mr-1" />
                  {{ s.label }}
                </button>
              </div>
            </div>

            <!-- Customer info -->
            <div>
              <p class="text-xs font-semibold text-[--color-text-muted] uppercase tracking-wide mb-2">Client</p>
              <div class="bg-[--color-background-soft] rounded-xl p-4 text-sm space-y-1">
                <p class="text-[--color-text] font-medium">{{ selectedOrder.customerName }}</p>
                <p class="text-[--color-text-muted]">{{ selectedOrder.customerEmail }}</p>
                <p v-if="selectedOrder.customerPhone" class="text-[--color-text-muted]">{{ selectedOrder.customerPhone }}</p>
              </div>
            </div>

            <!-- Shipping -->
            <div v-if="selectedOrder.orderType === 'SHIPPING' && selectedOrder.shippingAddress">
              <p class="text-xs font-semibold text-[--color-text-muted] uppercase tracking-wide mb-2">Adresse livraison</p>
              <div class="bg-[--color-background-soft] rounded-xl p-4 text-sm text-[--color-text-muted] space-y-0.5">
                <p>{{ selectedOrder.shippingAddress }}</p>
                <p>{{ selectedOrder.shippingPostalCode }} {{ selectedOrder.shippingCity }}</p>
                <p>{{ selectedOrder.shippingCountry }}</p>
              </div>
            </div>

            <!-- Items -->
            <div>
              <p class="text-xs font-semibold text-[--color-text-muted] uppercase tracking-wide mb-2">Articles</p>
              <ul class="space-y-2">
                <li
                  v-for="item in selectedOrder.items"
                  :key="item.id"
                  class="flex justify-between items-center text-sm bg-[--color-background-soft] rounded-xl px-4 py-3"
                >
                  <div>
                    <p class="text-[--color-text] font-medium">{{ item.snapshotProductName }}</p>
                    <p class="text-[--color-text-muted] text-xs">{{ item.snapshotVariantLabel || '—' }} · × {{ item.quantity }}</p>
                  </div>
                  <p class="font-semibold text-[--color-text]">{{ formatMad(item.totalCents) }}</p>
                </li>
              </ul>
            </div>

            <!-- Total -->
            <div class="bg-[--color-background-soft] rounded-xl p-4">
              <div class="flex justify-between text-sm text-[--color-text-muted] mb-1">
                <span>Sous-total</span><span>{{ formatMad(selectedOrder.subtotalCents) }}</span>
              </div>
              <div class="flex justify-between text-sm text-[--color-text-muted] mb-2">
                <span>Livraison</span><span>{{ formatMad(selectedOrder.shippingCostCents) }}</span>
              </div>
              <div class="flex justify-between font-semibold text-[--color-text] border-t border-[--color-border] pt-2">
                <span>Total</span><span>{{ formatMad(selectedOrder.totalCents) }}</span>
              </div>
            </div>

            <!-- Notes -->
            <div v-if="selectedOrder.notes">
              <p class="text-xs font-semibold text-[--color-text-muted] uppercase tracking-wide mb-2">Notes</p>
              <p class="text-sm text-[--color-text-muted] bg-[--color-background-soft] rounded-xl px-4 py-3">{{ selectedOrder.notes }}</p>
            </div>
          </div>
        </aside>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import type { Order, PageResponse } from '~/types'

definePageMeta({ layout: 'admin', middleware: 'admin' })

const auth = useAuthStore()
const { get, post, patch } = useApi()
const headers = computed(() => auth.authHeader)
const { success, error: toastError } = useToast()

const page = ref(0)
const filterStatus = ref('')

const { data, pending, refresh } = await useAsyncData(
  'admin-orders',
  () => {
    const query: Record<string, unknown> = { page: page.value, size: 20 }
    if (filterStatus.value) query.status = filterStatus.value
    return get<PageResponse<Order>>('/api/admin/orders', { headers: headers.value, query })
  },
  { watch: [page, filterStatus] }
)

const orders = computed(() => data.value?.content ?? [])

watch(filterStatus, () => { page.value = 0 })

// ── Detail panel ───────────────────────────────────────────────
const selectedOrder = ref<Order | null>(null)
const updatingStatus = ref(false)
const statusTarget = ref<string | null>(null)

function openOrder(order: Order) { selectedOrder.value = { ...order } }

const SHIPPING_STATUSES = [
  { value: 'PENDING', label: 'En attente' },
  { value: 'PAID', label: 'Payée' },
  { value: 'SHIPPED', label: 'Expédiée' },
  { value: 'DELIVERED', label: 'Livrée' },
  { value: 'CANCELLED', label: 'Annulée' },
]
const CC_STATUSES = [
  { value: 'CLICK_COLLECT_PENDING', label: 'En attente' },
  { value: 'READY', label: 'Prête' },
  { value: 'COLLECTED', label: 'Retirée' },
  { value: 'CANCELLED', label: 'Annulée' },
]
function availableStatuses(order: Order) {
  return order.orderType === 'SHIPPING' ? SHIPPING_STATUSES : CC_STATUSES
}

async function updateStatus(order: Order, status: string) {
  if (order.status === status) return
  updatingStatus.value = true
  statusTarget.value = status
  try {
    const updated = await patch<Order>(`/api/admin/orders/${order.id}/status`,
      { status },
      { headers: headers.value }
    )
    selectedOrder.value = updated
    refresh()
    success('Statut mis à jour.')
  } catch { toastError('Erreur lors de la mise à jour du statut.') }
  finally {
    updatingStatus.value = false
    statusTarget.value = null
  }
}

function formatMad(cents: number) {
  if (!cents && cents !== 0) return '—'
  return Math.round(cents / 100) + ' MAD'
}

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

useSeoMeta({ title: 'Commandes — Admin', robots: 'noindex' })
</script>

<style scoped>
.slide-enter-active, .slide-leave-active { transition: opacity 0.25s; }
.slide-enter-from, .slide-leave-to { opacity: 0; }
.slide-enter-active aside, .slide-leave-active aside { transition: transform 0.3s cubic-bezier(0.4,0,0.2,1); }
.slide-enter-from aside, .slide-leave-to aside { transform: translateX(100%); }
</style>
