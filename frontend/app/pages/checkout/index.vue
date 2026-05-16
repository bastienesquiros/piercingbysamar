<template>
  <div class="min-h-screen bg-[--color-background-soft] py-10">
    <div class="container-site max-w-5xl">

      <!-- Header -->
      <div class="mb-8 text-center">
        <NuxtLink :to="localePath('/')" class="font-serif text-2xl font-semibold text-[--color-text]">
          Piercing <span class="text-primary">by Samar</span>
        </NuxtLink>
        <p class="text-[--color-text-muted] text-sm mt-1">Finaliser votre commande</p>
      </div>

      <!-- Empty cart redirect -->
      <div v-if="cart.isEmpty" class="text-center py-20">
        <Icon name="lucide:shopping-bag" class="w-12 h-12 text-[--color-border] mx-auto mb-4" />
        <p class="text-[--color-text-muted] mb-4">Votre panier est vide.</p>
        <NuxtLink :to="localePath('/catalogue')" class="btn-primary">Voir le catalogue</NuxtLink>
      </div>

      <div v-else class="grid grid-cols-1 lg:grid-cols-5 gap-8">

        <!-- ── Form ───────────────────────────────────────────── -->
        <div class="lg:col-span-3 space-y-5">

          <!-- Order type selector -->
          <div class="bg-white rounded-2xl p-6 border border-[--color-border]">
            <h2 class="font-semibold text-[--color-text] mb-4">Mode de livraison</h2>
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">

              <!-- Shipping -->
              <button
                class="flex items-start gap-3 p-4 rounded-xl border-2 text-left transition-all duration-200"
                :class="form.orderType === 'SHIPPING'
                  ? 'border-[--color-primary] bg-[--color-primary-light]/30'
                  : 'border-[--color-border] hover:border-[--color-primary-light]'"
                @click="form.orderType = 'SHIPPING'"
              >
                <div
                  class="mt-0.5 w-4 h-4 rounded-full border-2 shrink-0 flex items-center justify-center"
                  :class="form.orderType === 'SHIPPING'
                    ? 'border-[--color-primary]'
                    : 'border-[--color-border]'"
                >
                  <div v-if="form.orderType === 'SHIPPING'" class="w-2 h-2 rounded-full bg-[--color-primary]" />
                </div>
                <div>
                  <p class="font-semibold text-sm text-[--color-text]">{{ $t('checkout.shipping') }}</p>
                  <p class="text-xs text-[--color-text-muted] mt-0.5">Livraison mondiale via Stripe</p>
                </div>
              </button>

              <!-- Click & Collect -->
              <button
                class="flex items-start gap-3 p-4 rounded-xl border-2 text-left transition-all duration-200"
                :class="form.orderType === 'CLICK_COLLECT'
                  ? 'border-[--color-primary] bg-[--color-primary-light]/30'
                  : 'border-[--color-border] hover:border-[--color-primary-light]'"
                @click="form.orderType = 'CLICK_COLLECT'"
              >
                <div
                  class="mt-0.5 w-4 h-4 rounded-full border-2 shrink-0 flex items-center justify-center"
                  :class="form.orderType === 'CLICK_COLLECT'
                    ? 'border-[--color-primary]'
                    : 'border-[--color-border]'"
                >
                  <div v-if="form.orderType === 'CLICK_COLLECT'" class="w-2 h-2 rounded-full bg-[--color-primary]" />
                </div>
                <div>
                  <p class="font-semibold text-sm text-[--color-text]">{{ $t('checkout.click_collect') }}</p>
                  <p class="text-xs text-[--color-text-muted] mt-0.5">{{ $t('checkout.click_collect_sub') }}</p>
                  <a
                    href="https://maps.app.goo.gl/b4qDcoBaafQaAqzMA"
                    target="_blank"
                    rel="noopener"
                    class="inline-flex items-center gap-1 text-xs text-[--color-primary-dark] hover:underline mt-1"
                    @click.stop
                  >
                    <Icon name="lucide:map-pin" class="w-3 h-3" />
                    Voir le magasin
                  </a>
                  <p class="text-xs font-medium text-amber-600 mt-1">
                    <Icon name="lucide:banknote" class="w-3 h-3 inline mr-0.5" />
                    {{ $t('checkout.click_collect_mad_only') }}
                  </p>
                </div>
              </button>
            </div>
          </div>

          <!-- Contact info -->
          <div class="bg-white rounded-2xl p-6 border border-[--color-border]">
            <h2 class="font-semibold text-[--color-text] mb-4">{{ $t('checkout.your_info') }}</h2>
            <div class="space-y-4">
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label class="text-xs font-semibold text-[--color-text-muted] uppercase tracking-wide block mb-1.5">
                    Nom complet *
                  </label>
                  <input v-model="form.customerName" type="text" class="input" placeholder="Samar El Idrissi" />
                  <p v-if="errors.customerName" class="text-red-500 text-xs mt-1">{{ errors.customerName }}</p>
                </div>
                <div>
                  <label class="text-xs font-semibold text-[--color-text-muted] uppercase tracking-wide block mb-1.5">
                    Email *
                  </label>
                  <input v-model="form.customerEmail" type="email" class="input" placeholder="samar@exemple.com" />
                  <p v-if="errors.customerEmail" class="text-red-500 text-xs mt-1">{{ errors.customerEmail }}</p>
                </div>
              </div>
              <div>
                <label class="text-xs font-semibold text-[--color-text-muted] uppercase tracking-wide block mb-1.5">
                  Téléphone <span v-if="form.orderType === 'CLICK_COLLECT'" class="text-red-500">*</span><span v-else class="font-normal normal-case">(optionnel)</span>
                </label>
                <input v-model="form.customerPhone" type="tel" class="input" placeholder="+212 7 81 57 00 83" />
                <p v-if="errors.customerPhone" class="text-red-500 text-xs mt-1">{{ errors.customerPhone }}</p>
              </div>
              <div>
                <label class="text-xs font-semibold text-[--color-text-muted] uppercase tracking-wide block mb-1.5">
                  Notes (optionnel)
                </label>
                <textarea v-model="form.notes" class="input resize-none" rows="2" placeholder="Instructions particulières…" />
              </div>
            </div>
          </div>

          <!-- Shipping address (only for SHIPPING) -->
          <Transition name="fade">
            <div v-if="form.orderType === 'SHIPPING'" class="bg-white rounded-2xl p-6 border border-[--color-border]">
              <h2 class="font-semibold text-[--color-text] mb-4">Adresse de livraison</h2>
              <div class="space-y-4">
                <div>
                  <label class="text-xs font-semibold text-[--color-text-muted] uppercase tracking-wide block mb-1.5">
                    Adresse *
                  </label>
                  <input v-model="form.shippingAddress" type="text" class="input" placeholder="12 rue des Oliviers" />
                  <p v-if="errors.shippingAddress" class="text-red-500 text-xs mt-1">{{ errors.shippingAddress }}</p>
                </div>
                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <label class="text-xs font-semibold text-[--color-text-muted] uppercase tracking-wide block mb-1.5">
                      Ville *
                    </label>
                    <input v-model="form.shippingCity" type="text" class="input" placeholder="Paris" />
                    <p v-if="errors.shippingCity" class="text-red-500 text-xs mt-1">{{ errors.shippingCity }}</p>
                  </div>
                  <div>
                    <label class="text-xs font-semibold text-[--color-text-muted] uppercase tracking-wide block mb-1.5">
                      Code postal *
                    </label>
                    <input v-model="form.shippingPostalCode" type="text" class="input" placeholder="75001" />
                    <p v-if="errors.shippingPostalCode" class="text-red-500 text-xs mt-1">{{ errors.shippingPostalCode }}</p>
                  </div>
                </div>
                <div>
                  <label class="text-xs font-semibold text-[--color-text-muted] uppercase tracking-wide block mb-1.5">
                    Pays *
                  </label>
                  <select v-model="form.shippingCountry" class="input">
                    <option value="">Sélectionner un pays</option>
                    <option value="FR">France</option>
                    <option value="BE">Belgique</option>
                    <option value="CH">Suisse</option>
                    <option value="MA">Maroc</option>
                    <option value="DE">Allemagne</option>
                    <option value="ES">Espagne</option>
                    <option value="IT">Italie</option>
                    <option value="GB">Royaume-Uni</option>
                    <option value="US">États-Unis</option>
                    <option value="CA">Canada</option>
                    <option value="OTHER">Autre</option>
                  </select>
                  <p v-if="errors.shippingCountry" class="text-red-500 text-xs mt-1">{{ errors.shippingCountry }}</p>
                </div>
              </div>
            </div>
          </Transition>

          <!-- Error global -->
          <div v-if="globalError" class="bg-red-50 border border-red-200 rounded-xl p-4 text-sm text-red-600">
            <p class="flex items-center gap-2">
              <Icon name="lucide:alert-circle" class="w-4 h-4 shrink-0" />
              {{ globalError }}
            </p>
            <a
              v-if="isStockError"
              :href="whatsappStockErrorUrl"
              target="_blank"
              rel="noopener"
              class="inline-flex items-center gap-2 mt-3 px-4 py-2 rounded-full text-sm font-semibold
                     bg-[#25D366] text-white hover:bg-[#1ebe5a] transition-colors"
            >
              <Icon name="simple-icons:whatsapp" class="w-4 h-4" />
              Nous contacter sur WhatsApp
            </a>
          </div>

          <!-- Submit -->
          <button
            class="btn-primary w-full py-4 text-base"
            :disabled="loading"
            @click="submit"
          >
            <Icon v-if="loading" name="lucide:loader-2" class="w-5 h-5 animate-spin" />
            <Icon v-else-if="form.orderType === 'SHIPPING'" name="lucide:credit-card" class="w-5 h-5" />
            <Icon v-else name="lucide:store" class="w-5 h-5" />
            <span v-if="loading">Traitement…</span>
            <span v-else-if="form.orderType === 'SHIPPING'">{{ $t('checkout.pay') }} — Payer via Stripe</span>
            <span v-else>Confirmer la réservation</span>
          </button>

          <p class="text-center text-xs text-[--color-text-muted]">
            <Icon name="lucide:lock" class="w-3 h-3 inline mr-1" />
            Paiement sécurisé · Données chiffrées SSL
          </p>
        </div>

        <!-- ── Order summary ───────────────────────────────────── -->
        <div class="lg:col-span-2">
          <div class="bg-white rounded-2xl border border-[--color-border] p-6 sticky top-24">
            <h2 class="font-semibold text-[--color-text] mb-4">Récapitulatif</h2>

            <ul class="space-y-4 divide-y divide-[--color-border]">
              <li v-for="item in cart.items" :key="item.variantId" class="flex gap-3 pt-4 first:pt-0">
                <div class="w-14 h-14 rounded-lg overflow-hidden bg-[--color-background-soft] shrink-0">
                  <NuxtImg
                    v-if="item.imageUrl"
                    :src="item.imageUrl"
                    :alt="item.productName"
                    width="56"
                    height="56"
                    class="w-full h-full object-cover"
                  />
                  <div v-else class="w-full h-full flex items-center justify-center">
                    <Icon name="lucide:image" class="w-5 h-5 text-[--color-border]" />
                  </div>
                </div>
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-medium text-[--color-text] line-clamp-2">{{ item.productName }}</p>
                  <p class="text-xs text-[--color-text-muted]">{{ item.variantLabel }}</p>
                  <p class="text-xs text-[--color-text-muted]">× {{ item.quantity }}</p>
                </div>
                <p class="text-sm font-semibold text-[--color-text] shrink-0">
                  {{ format(item.price * item.quantity) }}
                </p>
              </li>
            </ul>

            <div class="mt-5 pt-4 border-t border-[--color-border] space-y-2">
              <div class="flex justify-between text-sm text-[--color-text-muted]">
                <span>Sous-total</span>
                <span>{{ format(cart.total) }}</span>
              </div>
              <div v-if="form.orderType === 'SHIPPING'" class="flex justify-between text-sm text-[--color-text-muted]">
                <span>Livraison</span>
                <span>Calculé par Stripe</span>
              </div>
              <div class="flex justify-between font-semibold text-[--color-text] text-base pt-2 border-t border-[--color-border]">
                <span>Total</span>
                <div class="text-right">
                  <span class="font-serif text-lg">{{ format(cart.total) }}</span>
                  <p
                    v-if="form.orderType === 'CLICK_COLLECT' && currencyStore.currency !== 'MAD'"
                    class="text-xs text-amber-600 font-normal mt-0.5"
                  >
                    ≈ {{ new Intl.NumberFormat('fr-MA', { style: 'currency', currency: 'MAD', minimumFractionDigits: 0, maximumFractionDigits: 0 }).format(cart.total / 100) }} à régler en magasin
                  </p>
                </div>
              </div>
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

const localePath = useLocalePath()
const cart = useCartStore()
const currencyStore = useCurrencyStore()
const { post } = useApi()
const router = useRouter()
const { format } = usePrice()

const loading = ref(false)
const globalError = ref<string | null>(null)

const isStockError = computed(() => !!globalError.value?.toLowerCase().includes('stock'))
const whatsappStockErrorUrl = computed(() => {
  const items = cart.items.map((i) => `${i.productName} (${i.variantLabel})`).join(', ')
  const msg = encodeURIComponent(
    `Bonjour, je tentais de passer commande pour : ${items} mais j'ai un problème de stock. Pouvez-vous m'aider ?`
  )
  return `https://wa.me/212781570083?text=${msg}`
})

const form = reactive({
  orderType: 'CLICK_COLLECT' as 'SHIPPING' | 'CLICK_COLLECT',
  customerName: '',
  customerEmail: '',
  customerPhone: '',
  shippingAddress: '',
  shippingCity: '',
  shippingPostalCode: '',
  shippingCountry: '',
  notes: '',
})

const errors = reactive<Record<string, string>>({})

function validate(): boolean {
  Object.keys(errors).forEach((k) => delete errors[k])
  let valid = true

  if (!form.customerName.trim()) { errors.customerName = 'Nom requis'; valid = false }
  if (!form.customerEmail.trim() || !/^[^@]+@[^@]+\.[^@]+$/.test(form.customerEmail)) {
    errors.customerEmail = 'Email invalide'; valid = false
  }
  if (form.orderType === 'CLICK_COLLECT' && !form.customerPhone.trim()) {
    errors.customerPhone = 'Téléphone requis pour le Click & Collect'; valid = false
  }
  if (form.orderType === 'SHIPPING') {
    if (!form.shippingAddress.trim()) { errors.shippingAddress = 'Adresse requise'; valid = false }
    if (!form.shippingCity.trim()) { errors.shippingCity = 'Ville requise'; valid = false }
    if (!form.shippingPostalCode.trim()) { errors.shippingPostalCode = 'Code postal requis'; valid = false }
    if (!form.shippingCountry) { errors.shippingCountry = 'Pays requis'; valid = false }
  }
  return valid
}

async function submit() {
  if (!validate()) return
  loading.value = true
  globalError.value = null

  try {
    const payload = {
      orderType: form.orderType,
      customerName: form.customerName,
      customerEmail: form.customerEmail,
      customerPhone: form.customerPhone || null,
      shippingAddress: form.orderType === 'SHIPPING' ? form.shippingAddress : null,
      shippingCity: form.orderType === 'SHIPPING' ? form.shippingCity : null,
      shippingPostalCode: form.orderType === 'SHIPPING' ? form.shippingPostalCode : null,
      shippingCountry: form.orderType === 'SHIPPING' ? form.shippingCountry : null,
      notes: form.notes || null,
      currency: 'MAD',
      items: cart.items.map((i) => ({ variantId: i.variantId, quantity: i.quantity })),
    }

    const order = await post<Order>('/api/orders', payload)

    if (form.orderType === 'SHIPPING') {
      // Redirect to Stripe Checkout
      const { checkoutUrl } = await post<{ checkoutUrl: string }>('/api/stripe/checkout', {
        orderReference: order.reference,
        displayCurrency: currencyStore.currency,
      })
      cart.clear()
      window.location.href = checkoutUrl
    } else {
      // Click & Collect — go straight to confirmation
      cart.clear()
      router.push(localePath(`/order/confirmation?reference=${order.reference}`))
    }
  } catch (err: unknown) {
    const e = err as { data?: { message?: string } }
    globalError.value = e?.data?.message ?? 'Une erreur est survenue. Veuillez réessayer.'
  } finally {
    loading.value = false
  }
}

useSeoMeta({ title: 'Paiement — Piercing by Samar', robots: 'noindex' })
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.25s, transform 0.25s; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(-6px); }
</style>
