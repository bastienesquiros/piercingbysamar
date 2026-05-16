<template>
  <div class="space-y-5">

    <!-- Header -->
    <div class="flex items-center justify-between">
      <h2 class="text-xl font-semibold text-gray-800">Produits</h2>
      <button class="btn-primary py-2 px-4 text-sm" @click="openCreate">
        <Icon name="lucide:plus" class="w-4 h-4" />
        Nouveau produit
      </button>
    </div>

    <!-- Table -->
    <div class="bg-white rounded-xl border border-gray-200 overflow-hidden">
      <div v-if="pending" class="p-10 text-center text-gray-400">
        <Icon name="lucide:loader-2" class="w-6 h-6 animate-spin mx-auto mb-2" />
        Chargement…
      </div>

      <div v-else-if="!products.length" class="p-10 text-center text-gray-400">
        Aucun produit. <button class="text-[--color-primary-dark] underline" @click="openCreate">Créer le premier.</button>
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full text-sm">
          <thead class="bg-gray-50 text-gray-500 text-xs uppercase tracking-wide border-b border-gray-100">
            <tr>
              <th class="text-left px-5 py-3 w-12" />
              <th class="text-left px-5 py-3">Nom</th>
              <th class="text-left px-5 py-3">Catégorie</th>
              <th class="text-left px-5 py-3">Matériau</th>
              <th class="text-left px-5 py-3">Prix</th>
              <th class="text-left px-5 py-3">Stock</th>
              <th class="text-left px-5 py-3">Statut</th>
              <th class="px-5 py-3" />
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr v-for="product in products" :key="product.id" class="hover:bg-gray-50 transition-colors">
              <!-- Thumbnail -->
              <td class="px-5 py-3">
                <div class="w-10 h-10 rounded-lg overflow-hidden bg-gray-100">
                  <NuxtImg
                    v-if="product.coverImageUrl"
                    :src="product.coverImageUrl"
                    :alt="product.name"
                    width="40"
                    height="40"
                    class="w-full h-full object-cover"
                  />
                  <div v-else class="w-full h-full flex items-center justify-center">
                    <Icon name="lucide:image" class="w-4 h-4 text-gray-300" />
                  </div>
                </div>
              </td>
              <td class="px-5 py-3">
                <p class="font-medium text-gray-800">{{ product.name }}</p>
                <p class="text-gray-400 text-xs font-mono">{{ product.slug }}</p>
              </td>
              <td class="px-5 py-3 text-gray-500">{{ product.categoryName }}</td>
              <td class="px-5 py-3 text-gray-500 text-xs">{{ product.material }}</td>
              <td class="px-5 py-3 text-gray-700 whitespace-nowrap">
                <template v-if="product.minPriceCents === product.maxPriceCents">
                  {{ formatPrice(product.minPriceCents) }}
                </template>
                <template v-else>
                  {{ formatPrice(product.minPriceCents) }} – {{ formatPrice(product.maxPriceCents) }}
                </template>
              </td>
              <td class="px-5 py-3">
                <span
                  class="inline-flex items-center gap-1 text-xs font-medium"
                  :class="product.inStock ? 'text-green-600' : 'text-red-500'"
                >
                  <span class="w-1.5 h-1.5 rounded-full" :class="product.inStock ? 'bg-green-500' : 'bg-red-400'" />
                  {{ product.inStock ? 'En stock' : 'Rupture' }}
                </span>
              </td>
              <td class="px-5 py-3">
                <span
                  class="text-xs font-medium"
                  :class="product.active !== false ? 'text-green-600' : 'text-gray-400'"
                >
                  {{ product.active !== false ? 'Actif' : 'Inactif' }}
                </span>
              </td>
              <td class="px-5 py-3">
                <div class="flex items-center gap-2">
                  <button class="text-xs text-blue-600 hover:underline" @click="openEdit(product.id)">Modifier</button>
                  <button class="text-xs text-red-500 hover:underline" @click="deactivate(product.id)">Désactiver</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="data && data.totalPages > 1" class="flex justify-center gap-2">
      <button class="px-3 py-1.5 rounded-lg border border-gray-200 text-sm disabled:opacity-40" :disabled="page === 0" @click="page--">← Précédent</button>
      <span class="px-3 py-1.5 text-sm text-gray-600">{{ page + 1 }} / {{ data.totalPages }}</span>
      <button class="px-3 py-1.5 rounded-lg border border-gray-200 text-sm disabled:opacity-40" :disabled="data.last" @click="page++">Suivant →</button>
    </div>

    <!-- ── Product panel (create / edit) ───────────────────────── -->
    <Transition name="slide">
      <div v-if="panelOpen" class="fixed inset-0 z-50 flex justify-end">
        <div class="absolute inset-0 bg-black/30" @click="closePanel" />
        <aside class="relative w-full max-w-2xl bg-white shadow-2xl flex flex-col overflow-y-auto">

          <!-- Panel header -->
          <div class="flex items-center justify-between px-6 py-5 border-b border-gray-100 sticky top-0 bg-white z-10">
            <h3 class="font-semibold text-gray-800">
              {{ editingId ? 'Modifier le produit' : 'Nouveau produit' }}
            </h3>
            <button class="text-gray-400 hover:text-gray-600" @click="closePanel">
              <Icon name="lucide:x" class="w-5 h-5" />
            </button>
          </div>

          <!-- Panel body -->
          <div class="px-6 py-6 space-y-8 flex-1">

            <!-- Error -->
            <div v-if="panelError" class="bg-red-50 border border-red-200 rounded-xl px-4 py-3 text-sm text-red-600">
              {{ panelError }}
            </div>

            <!-- ── Product fields ── -->
            <section class="space-y-4">
              <h4 class="text-xs font-semibold text-gray-500 uppercase tracking-wide">Informations produit</h4>

              <div class="grid grid-cols-2 gap-4">
                <div class="col-span-2">
                  <label class="field-label">Nom *</label>
                  <input v-model="form.name" type="text" class="input" placeholder="Anneau Titane Hélix" />
                </div>
                <div>
                  <label class="field-label">Catégorie</label>
                  <select v-model="form.categoryId" class="input">
                    <option :value="null">— Aucune —</option>
                    <option v-for="cat in flatCategories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
                  </select>
                </div>
                <div>
                  <label class="field-label">Matériau</label>
                  <select v-model="form.material" class="input">
                    <option value="">— Aucun —</option>
                    <option value="Titane ASTM F136">Titane ASTM F136</option>
                    <option value="Acier chirurgical">Acier chirurgical</option>
                    <option value="Bioflex">Bioflex</option>
                    <option value="Or 14K">Or 14K</option>
                  </select>
                </div>
                <div class="col-span-2">
                  <label class="field-label">Description</label>
                  <textarea v-model="form.description" class="input resize-none" rows="3" placeholder="Description du produit…" />
                </div>
                <div class="col-span-2">
                  <label class="flex items-center gap-2 cursor-pointer">
                    <input v-model="form.nickelFree" type="checkbox" class="rounded" />
                    <span class="text-sm text-gray-700">Nickel Free</span>
                  </label>
                </div>
              </div>
            </section>

            <!-- ── SEO ── -->
            <section class="space-y-4">
              <h4 class="text-xs font-semibold text-gray-500 uppercase tracking-wide">SEO (optionnel)</h4>
              <div>
                <label class="field-label">Meta title</label>
                <input v-model="form.metaTitle" type="text" class="input" placeholder="Anneau Titane Hélix — Piercing by Samar" />
              </div>
              <div>
                <label class="field-label">Meta description</label>
                <textarea v-model="form.metaDescription" class="input resize-none" rows="2" />
              </div>
            </section>

            <!-- ── Variants (edit only) ── -->
            <section v-if="editingProduct" class="space-y-4">
              <div class="flex items-center justify-between">
                <h4 class="text-xs font-semibold text-gray-500 uppercase tracking-wide">Variantes</h4>
                <button class="text-xs text-[--color-primary-dark] hover:underline flex items-center gap-1" @click="showAddVariant = !showAddVariant">
                  <Icon name="lucide:plus" class="w-3.5 h-3.5" />
                  Ajouter
                </button>
              </div>

              <!-- Add variant form -->
              <Transition name="fade">
                <div v-if="showAddVariant" class="bg-gray-50 rounded-xl p-4 space-y-3">
                  <div class="grid grid-cols-2 gap-3">
                    <div>
                      <label class="field-label">SKU *</label>
                      <input v-model="newVariant.sku" type="text" class="input py-2 text-sm" placeholder="SKU-001" />
                    </div>
                    <div>
                      <label class="field-label">Prix (€) *</label>
                      <input v-model.number="newVariant.priceEuros" type="number" min="0" step="0.01" class="input py-2 text-sm" placeholder="29.90" />
                    </div>
                    <div>
                      <label class="field-label">Taille</label>
                      <input v-model="newVariant.size" type="text" class="input py-2 text-sm" placeholder="8mm" />
                    </div>
                    <div>
                      <label class="field-label">Couleur</label>
                      <input v-model="newVariant.color" type="text" class="input py-2 text-sm" placeholder="Silver" />
                    </div>
                    <div>
                      <label class="field-label">Stock</label>
                      <input v-model.number="newVariant.stock" type="number" min="0" class="input py-2 text-sm" placeholder="10" />
                    </div>
                  </div>
                  <div class="flex gap-2 justify-end">
                    <button class="btn-ghost py-1.5 px-3 text-sm" @click="showAddVariant = false">Annuler</button>
                    <button class="btn-primary py-1.5 px-4 text-sm" :disabled="savingVariant" @click="addVariant">
                      <Icon v-if="savingVariant" name="lucide:loader-2" class="w-3.5 h-3.5 animate-spin" />
                      Ajouter
                    </button>
                  </div>
                </div>
              </Transition>

              <!-- Variant list -->
              <ul v-if="editingProduct.variants.length" class="space-y-2">
                <li
                  v-for="v in editingProduct.variants"
                  :key="v.id"
                  class="flex items-center justify-between bg-gray-50 rounded-xl px-4 py-3 text-sm"
                >
                  <div>
                    <p class="font-medium text-gray-800">
                      {{ [v.size, v.color].filter(Boolean).join(' · ') || '—' }}
                    </p>
                    <p class="text-gray-400 text-xs">{{ v.sku }} · {{ formatPrice(v.priceCents) }} · stock : {{ v.stock }}</p>
                  </div>
                  <button class="text-red-400 hover:text-red-600" @click="deleteVariant(v.id)">
                    <Icon name="lucide:trash-2" class="w-4 h-4" />
                  </button>
                </li>
              </ul>
              <p v-else class="text-sm text-gray-400">Aucune variante. Ajoutez-en au moins une.</p>
            </section>

            <!-- ── Images (edit only) ── -->
            <section v-if="editingProduct" class="space-y-4">
              <h4 class="text-xs font-semibold text-gray-500 uppercase tracking-wide">Images</h4>

              <!-- Existing images -->
              <div v-if="editingProduct.images.length" class="flex flex-wrap gap-3">
                <div
                  v-for="img in editingProduct.images"
                  :key="img.id"
                  class="relative w-20 h-20 rounded-xl overflow-hidden group"
                >
                  <NuxtImg :src="img.r2Url" :alt="img.altText ?? ''" width="80" height="80" class="w-full h-full object-cover" />
                  <button
                    class="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 transition-opacity
                           flex items-center justify-center text-white"
                    @click="deleteImage(img.id)"
                  >
                    <Icon name="lucide:trash-2" class="w-4 h-4" />
                  </button>
                </div>
              </div>

              <!-- Upload -->
              <div>
                <label
                  class="flex flex-col items-center gap-2 border-2 border-dashed border-gray-200
                         rounded-xl py-6 cursor-pointer hover:border-[--color-primary-light] transition-colors"
                >
                  <Icon name="lucide:upload-cloud" class="w-7 h-7 text-gray-400" />
                  <span class="text-sm text-gray-500">Cliquez pour uploader (max 5 Mo)</span>
                  <input type="file" accept="image/*" multiple class="hidden" @change="uploadImages" />
                </label>
                <div v-if="uploadingImages" class="flex items-center gap-2 mt-2 text-sm text-gray-500">
                  <Icon name="lucide:loader-2" class="w-4 h-4 animate-spin" />
                  Upload en cours…
                </div>
              </div>
            </section>
          </div>

          <!-- Panel footer -->
          <div class="px-6 py-4 border-t border-gray-100 bg-white sticky bottom-0 flex gap-3 justify-end">
            <button class="btn-ghost" @click="closePanel">Annuler</button>
            <button class="btn-primary" :disabled="savingProduct" @click="saveProduct">
              <Icon v-if="savingProduct" name="lucide:loader-2" class="w-4 h-4 animate-spin" />
              {{ editingId ? 'Enregistrer' : 'Créer le produit' }}
            </button>
          </div>
        </aside>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import type { Category, PageResponse, ProductDetail, ProductSummary } from '~/types'

definePageMeta({ layout: 'admin', middleware: 'admin' })

const auth = useAuthStore()
const { get, post, put, del } = useApi()
const headers = computed(() => auth.authHeader)

// ── Products list ──────────────────────────────────────────────
const page = ref(0)
const { data, pending, refresh } = await useAsyncData(
  'admin-products',
  () => get<PageResponse<ProductSummary>>('/api/admin/products', {
    headers: headers.value, query: { page: page.value, size: 20 },
  }),
  { watch: [page] }
)
const products = computed(() => data.value?.content ?? [])

// ── Categories ─────────────────────────────────────────────────
const { data: categoriesData } = await useAsyncData('admin-cats', () =>
  get<Category[]>('/api/categories')
)
const flatCategories = computed(() => {
  const flat: Category[] = []
  const traverse = (cats: Category[]) => {
    for (const c of cats) { flat.push(c); if (c.children?.length) traverse(c.children) }
  }
  traverse(categoriesData.value ?? [])
  return flat
})

// ── Panel state ────────────────────────────────────────────────
const panelOpen = ref(false)
const editingId = ref<number | null>(null)
const editingProduct = ref<ProductDetail | null>(null)
const panelError = ref<string | null>(null)
const savingProduct = ref(false)

const form = reactive({
  name: '',
  description: '',
  material: '',
  nickelFree: false,
  categoryId: null as number | null,
  metaTitle: '',
  metaDescription: '',
})

function resetForm() {
  form.name = ''
  form.description = ''
  form.material = ''
  form.nickelFree = false
  form.categoryId = null
  form.metaTitle = ''
  form.metaDescription = ''
}

function openCreate() {
  editingId.value = null
  editingProduct.value = null
  resetForm()
  panelError.value = null
  panelOpen.value = true
}

async function openEdit(id: number) {
  editingId.value = id
  panelError.value = null
  panelOpen.value = true
  const product = await get<ProductDetail>(`/api/admin/products/${id}`, { headers: headers.value })
    .catch(() => null)
  if (!product) {
    // fallback: find in list
    const p = products.value.find((x) => x.id === id)
    if (p) {
      form.name = p.name
      form.material = p.material
      form.nickelFree = p.nickelFree
      form.categoryId = p.categoryId
    }
    return
  }
  editingProduct.value = product
  form.name = product.name
  form.description = product.description ?? ''
  form.material = product.material
  form.nickelFree = product.nickelFree
  form.categoryId = product.categoryId
  form.metaTitle = product.metaTitle ?? ''
  form.metaDescription = product.metaDescription ?? ''
}

function closePanel() {
  panelOpen.value = false
  editingId.value = null
  editingProduct.value = null
  showAddVariant.value = false
}

async function saveProduct() {
  if (!form.name.trim()) { panelError.value = 'Le nom est requis.'; return }
  savingProduct.value = true
  panelError.value = null
  try {
    const payload = {
      name: form.name,
      description: form.description || null,
      material: form.material || null,
      nickelFree: form.nickelFree,
      categoryId: form.categoryId,
      metaTitle: form.metaTitle || null,
      metaDescription: form.metaDescription || null,
    }
    if (editingId.value) {
      await put(`/api/admin/products/${editingId.value}`, payload, { headers: headers.value })
    } else {
      const created = await post<ProductDetail>('/api/admin/products', payload, { headers: headers.value })
      editingId.value = created.id
      editingProduct.value = created
    }
    await refresh()
    if (!editingId.value) closePanel()
  } catch (e: unknown) {
    const err = e as { data?: { message?: string } }
    panelError.value = err?.data?.message ?? 'Erreur lors de la sauvegarde.'
  } finally {
    savingProduct.value = false
  }
}

async function deactivate(id: number) {
  if (!confirm('Désactiver ce produit ?')) return
  await del(`/api/admin/products/${id}`, { headers: headers.value }).catch(() => {})
  refresh()
}

// ── Variants ───────────────────────────────────────────────────
const showAddVariant = ref(false)
const savingVariant = ref(false)
const newVariant = reactive({ sku: '', priceEuros: 0, size: '', color: '', stock: 0 })

async function addVariant() {
  if (!editingId.value || !newVariant.sku || !newVariant.priceEuros) return
  savingVariant.value = true
  try {
    const updated = await post<ProductDetail>(
      `/api/admin/products/${editingId.value}/variants`,
      { sku: newVariant.sku, priceCents: Math.round(newVariant.priceEuros * 100), size: newVariant.size || null, color: newVariant.color || null, stock: newVariant.stock },
      { headers: headers.value }
    )
    editingProduct.value = updated
    newVariant.sku = ''; newVariant.priceEuros = 0; newVariant.size = ''; newVariant.color = ''; newVariant.stock = 0
    showAddVariant.value = false
    refresh()
  } catch { /* ignore */ }
  finally { savingVariant.value = false }
}

async function deleteVariant(variantId: number) {
  if (!editingId.value || !confirm('Supprimer cette variante ?')) return
  await del(`/api/admin/products/${editingId.value}/variants/${variantId}`, { headers: headers.value }).catch(() => {})
  const updated = await get<ProductDetail>(`/api/admin/products/${editingId.value}`, { headers: headers.value }).catch(() => null)
  if (updated) editingProduct.value = updated
  refresh()
}

// ── Images ─────────────────────────────────────────────────────
const uploadingImages = ref(false)

async function uploadImages(e: Event) {
  if (!editingId.value) return
  const files = (e.target as HTMLInputElement).files
  if (!files?.length) return
  uploadingImages.value = true
  for (const file of Array.from(files)) {
    const fd = new FormData()
    fd.append('file', file)
    await $fetch(`/api/admin/products/${editingId.value}/images`, {
      method: 'POST',
      baseURL: useRuntimeConfig().public.apiBase as string,
      headers: auth.authHeader,
      body: fd,
    }).catch(() => {})
  }
  const updated = await get<ProductDetail>(`/api/admin/products/${editingId.value}`, { headers: headers.value }).catch(() => null)
  if (updated) editingProduct.value = updated
  uploadingImages.value = false
  refresh()
}

async function deleteImage(imageId: number) {
  if (!editingId.value || !confirm('Supprimer cette image ?')) return
  await del(`/api/admin/products/${editingId.value}/images/${imageId}`, { headers: headers.value }).catch(() => {})
  const updated = await get<ProductDetail>(`/api/admin/products/${editingId.value}`, { headers: headers.value }).catch(() => null)
  if (updated) editingProduct.value = updated
  refresh()
}

function formatPrice(cents: number) {
  return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(cents / 100)
}

useSeoMeta({ title: 'Produits — Admin', robots: 'noindex' })
</script>

<style scoped>
.field-label { @apply text-xs font-semibold text-gray-500 uppercase tracking-wide block mb-1.5; }
.slide-enter-active, .slide-leave-active { transition: opacity 0.25s; }
.slide-enter-from, .slide-leave-to { opacity: 0; }
.slide-enter-active aside, .slide-leave-active aside { transition: transform 0.3s cubic-bezier(0.4,0,0.2,1); }
.slide-enter-from aside, .slide-leave-to aside { transform: translateX(100%); }
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s, transform 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(-4px); }
</style>
