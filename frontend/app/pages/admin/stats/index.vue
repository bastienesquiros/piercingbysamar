<template>
  <div class="space-y-6">
    <!-- Période -->
    <div class="bg-white rounded-2xl border border-[--color-border] p-4 flex flex-wrap items-center gap-3">
      <div class="flex gap-2 flex-wrap">
        <button
          v-for="p in PERIODS"
          :key="p.key"
          class="px-3 py-1.5 text-sm rounded-lg border transition-colors"
          :class="period === p.key
            ? 'bg-[--color-primary] text-white border-[--color-primary]'
            : 'border-[--color-border] text-[--color-text-muted] hover:border-[--color-primary]'"
          @click="setPeriod(p.key)"
        >
          {{ p.label }}
        </button>
        <span class="text-sm text-[--color-text-muted] self-center">ou</span>
        <input v-model="customFrom" type="date" class="input py-1 text-sm" />
        <input v-model="customTo" type="date" class="input py-1 text-sm" />
        <button class="btn-primary py-1 px-3 text-sm" @click="applyCustom">Appliquer</button>
      </div>
      <div class="ml-auto flex gap-2">
        <button class="btn-ghost py-1.5 px-3 text-sm flex items-center gap-1.5" :disabled="exporting" @click="exportCsv">
          <Icon v-if="exporting" name="lucide:loader-2" class="w-4 h-4 animate-spin" />
          <Icon v-else name="lucide:download" class="w-4 h-4" />
          Exporter CSV
        </button>
      </div>
    </div>

    <!-- KPI cards -->
    <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
      <div class="bg-white rounded-2xl border border-[--color-border] p-5">
        <p class="text-xs text-[--color-text-muted] uppercase tracking-wide mb-1">Chiffre d'affaires</p>
        <p class="text-2xl font-serif font-semibold text-[--color-text]">{{ formatPrice(overview?.revenueCents) }}</p>
        <p class="text-xs text-[--color-text-muted] mt-1">{{ overview?.orderCount ?? 0 }} commandes</p>
      </div>
      <div class="bg-white rounded-2xl border border-[--color-border] p-5">
        <p class="text-xs text-[--color-text-muted] uppercase tracking-wide mb-1">Panier moyen</p>
        <p class="text-2xl font-serif font-semibold text-[--color-text]">{{ formatPrice(overview?.averageOrderCents) }}</p>
        <p class="text-xs text-[--color-text-muted] mt-1">par commande</p>
      </div>
      <div class="bg-white rounded-2xl border border-[--color-border] p-5">
        <p class="text-xs text-[--color-text-muted] uppercase tracking-wide mb-1">CA total (all-time)</p>
        <p class="text-2xl font-serif font-semibold text-[--color-text]">{{ formatPrice(overview?.allTimeRevenueCents) }}</p>
        <p class="text-xs text-[--color-text-muted] mt-1">{{ overview?.allTimeOrderCount ?? 0 }} commandes</p>
      </div>
      <div class="bg-white rounded-2xl border border-[--color-border] p-5">
        <p class="text-xs text-[--color-text-muted] uppercase tracking-wide mb-1">Statuts (période)</p>
        <div class="space-y-0.5 mt-1">
          <p v-for="s in overview?.byStatus ?? []" :key="s.status" class="text-xs flex justify-between">
            <span class="text-[--color-text-muted]">{{ statusLabel(s.status) }}</span>
            <span class="font-medium text-[--color-text]">{{ s.count }}</span>
          </p>
        </div>
      </div>
    </div>

    <!-- Graphique CA par jour -->
    <div class="bg-white rounded-2xl border border-[--color-border] p-6">
      <h3 class="text-sm font-semibold text-[--color-text] mb-6">Chiffre d'affaires par jour</h3>
      <div v-if="revenueData.length" class="overflow-x-auto -mx-2 px-2">
        <div class="min-w-[560px]">
        <!-- Chart SVG -->
        <svg :viewBox="`0 0 ${chartW} ${chartH}`" class="w-full" style="overflow: visible">
          <!-- Grid lines -->
          <line v-for="i in 4" :key="i"
            :x1="paddingL" :y1="paddingT + ((chartH - paddingT - paddingB) / 4) * (i - 1)"
            :x2="chartW - paddingR" :y2="paddingT + ((chartH - paddingT - paddingB) / 4) * (i - 1)"
            stroke="#f0ede8" stroke-width="1" />
          <!-- Y-axis labels -->
          <text v-for="i in 5" :key="'y' + i"
            :x="paddingL - 8"
            :y="paddingT + ((chartH - paddingT - paddingB) / 4) * (i - 1) + 4"
            text-anchor="end" font-size="13" fill="#9ca3af">
            {{ formatPrice(maxRevenue - ((maxRevenue / 4) * (i - 1))) }}
          </text>
          <!-- Bars -->
          <g v-for="(d, i) in revenueData" :key="d.date">
            <rect
              :x="paddingL + i * barStep + barStep * 0.15"
              :y="paddingT + barAreaH - barHeight(d.revenueCents)"
              :width="barWidth"
              :height="barHeight(d.revenueCents)"
              fill="var(--color-primary)"
              rx="3"
              class="transition-all duration-300"
            />
            <!-- Tooltip on hover via title -->
            <title>{{ d.date }} · {{ formatPrice(d.revenueCents) }} · {{ d.orderCount }} commande(s)</title>
            <!-- X-axis label (show every N depending on density) -->
            <text v-if="shouldShowLabel(i)"
              :x="paddingL + i * barStep + barStep / 2"
              :y="chartH - paddingB + 14"
              text-anchor="middle" font-size="11" fill="#9ca3af">
              {{ formatDate(d.date) }}
            </text>
          </g>
        </svg>
        </div>
      </div>
      <div v-else class="flex items-center justify-center h-32 text-[--color-text-muted] text-sm">
        Aucune donnée pour cette période.
      </div>
    </div>

    <!-- Top produits -->
    <div class="bg-white rounded-2xl border border-[--color-border] p-6">
      <h3 class="text-sm font-semibold text-[--color-text] mb-4">Top produits vendus</h3>
      <div v-if="topProducts.length" class="space-y-2">
        <div v-for="(p, i) in topProducts" :key="p.productName" class="flex items-center gap-3">
          <span class="text-xs font-mono text-[--color-text-muted] w-5 text-right">{{ i + 1 }}</span>
          <!-- Barre relative -->
          <NuxtLink
            :to="`/admin/products?edit=${p.productId}`"
            class="flex-1 bg-gray-100 rounded-full h-6 relative overflow-hidden hover:opacity-80 transition-opacity"
          >
            <div
              class="h-full rounded-full bg-[--color-primary-light] transition-all duration-500"
              :style="{ width: `${(p.quantity / topProducts[0].quantity) * 100}%` }"
            />
            <span class="absolute inset-0 flex items-center px-3 text-xs font-medium text-[--color-text] truncate">
              {{ p.productName }}
            </span>
          </NuxtLink>
          <span class="text-xs font-semibold text-[--color-text] w-8 text-right">{{ p.quantity }}</span>
          <span class="text-xs text-[--color-text-muted] w-20 text-right">{{ formatPrice(p.revenueCents) }}</span>
        </div>
      </div>
      <p v-else class="text-sm text-[--color-text-muted]">Aucune vente sur cette période.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ layout: 'admin', middleware: 'admin' })

const { get } = useApi()
const auth = useAuthStore()
const headers = computed(() => auth.authHeader)

// ── Période ─────────────────────────────────────────────────────
const PERIODS = [
  { key: 'today', label: "Aujourd'hui" },
  { key: 'week', label: '7 jours' },
  { key: 'month', label: '30 jours' },
  { key: 'year', label: 'Cette année' },
]

const period = ref('month')
const from = ref('')
const to = ref('')
const customFrom = ref('')
const customTo = ref('')

function setPeriod(key: string) {
  period.value = key
  const now = new Date()
  const todayStr = now.toISOString().split('T')[0]
  to.value = todayStr
  if (key === 'today') from.value = todayStr
  else if (key === 'week') from.value = offsetDate(-6)
  else if (key === 'month') from.value = offsetDate(-29)
  else if (key === 'year') from.value = new Date(now.getFullYear(), 0, 1).toISOString().split('T')[0]
  loadAll()
}

function offsetDate(days: number) {
  const d = new Date()
  d.setDate(d.getDate() + days)
  return d.toISOString().split('T')[0]
}

function applyCustom() {
  if (!customFrom.value || !customTo.value) return
  period.value = ''
  from.value = customFrom.value
  to.value = customTo.value
  loadAll()
}

// ── Data ─────────────────────────────────────────────────────────
interface Overview { revenueCents: number; orderCount: number; averageOrderCents: number; allTimeRevenueCents: number; allTimeOrderCount: number; byStatus: { status: string; count: number }[] }
interface RevenueDay { date: string; revenueCents: number; orderCount: number }
interface TopProduct { productName: string; quantity: number; revenueCents: number; productId: number }

const overview = ref<Overview | null>(null)
const revenueData = ref<RevenueDay[]>([])
const topProducts = ref<TopProduct[]>([])
const loading = ref(false)
const exporting = ref(false)

async function loadAll() {
  if (!from.value || !to.value) return
  loading.value = true
  try {
    const [ov, rv, tp] = await Promise.all([
      get<Overview>(`/api/admin/stats/overview?from=${from.value}&to=${to.value}`, { headers: headers.value }),
      get<RevenueDay[]>(`/api/admin/stats/revenue?from=${from.value}&to=${to.value}`, { headers: headers.value }),
      get<TopProduct[]>(`/api/admin/stats/top-products?from=${from.value}&to=${to.value}&limit=10`, { headers: headers.value }),
    ])
    overview.value = ov
    revenueData.value = rv
    topProducts.value = tp
  } finally { loading.value = false }
}

// ── Chart dimensions ─────────────────────────────────────────────
const chartW = 800
const chartH = 250
const paddingL = 80
const paddingR = 20
const paddingT = 10
const paddingB = 32

const barAreaW = computed(() => chartW - paddingL - paddingR)
const barAreaH = computed(() => chartH - paddingT - paddingB)
const barStep = computed(() => revenueData.value.length ? barAreaW.value / revenueData.value.length : 0)
const barWidth = computed(() => barStep.value * 0.7)
const maxRevenue = computed(() => Math.max(...revenueData.value.map(d => d.revenueCents), 1))

function barHeight(cents: number) {
  return (cents / maxRevenue.value) * barAreaH.value
}

function shouldShowLabel(i: number) {
  const n = revenueData.value.length
  if (n <= 15) return true
  if (n <= 31) return i % 3 === 0
  return i % 7 === 0
}

function formatDate(dateStr: string) {
  const d = new Date(dateStr)
  return `${d.getDate()}/${d.getMonth() + 1}`
}

// ── Utils ─────────────────────────────────────────────────────────
function formatPrice(cents?: number | null) {
  if (cents == null) return '—'
  return Math.round(cents / 100) + ' MAD'
}

const STATUS_LABELS: Record<string, string> = {
  PENDING: 'En attente',
  PAID: 'Payées',
  SHIPPED: 'Expédiées',
  DELIVERED: 'Livrées',
  CANCELLED: 'Annulées',
  CLICK_COLLECT_PENDING: 'C&C en attente',
  READY: 'Prêtes',
  COLLECTED: 'Retirées',
}
function statusLabel(s: string) { return STATUS_LABELS[s] ?? s }

async function exportCsv() {
  if (!from.value || !to.value) return
  exporting.value = true
  try {
    const res = await fetch(`/api/admin/export/orders.csv?from=${from.value}&to=${to.value}`, {
      headers: { ...auth.authHeader }
    })
    const blob = await res.blob()
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `commandes_${from.value}_${to.value}.csv`
    a.click()
    URL.revokeObjectURL(url)
  } finally { exporting.value = false }
}

// Init
onMounted(() => setPeriod('month'))

useSeoMeta({ title: 'Stats — Admin Piercing by Samar', robots: 'noindex' })
</script>
