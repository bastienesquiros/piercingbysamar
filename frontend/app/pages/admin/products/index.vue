<template>
  <div class="space-y-5">

    <!-- Header -->
    <div class="flex items-center justify-between gap-3 flex-wrap">
      <div class="flex items-center gap-2">
        <!-- Import stocks -->
        <button class="btn-ghost py-2 px-3 text-sm flex items-center gap-1.5" @click="downloadTemplate">
          <Icon name="lucide:download" class="w-4 h-4" />
          Modèle stocks (.csv)
        </button>
        <label class="btn-ghost py-2 px-3 text-sm flex items-center gap-1.5 cursor-pointer">
          <Icon v-if="importing" name="lucide:loader-2" class="w-4 h-4 animate-spin" />
          <Icon v-else name="lucide:upload" class="w-4 h-4" />
          Mettre à jour les stocks
          <input type="file" accept=".csv,text/csv" class="hidden" :disabled="importing" @change="importStock" />
        </label>
      </div>
      <button class="btn-primary py-2 px-4 text-sm" @click="openCreate">
        <Icon name="lucide:plus" class="w-4 h-4" />
        Nouveau produit
      </button>
    </div>

    <!-- Résultat import -->
    <div v-if="importResult" class="rounded-xl border p-4 text-sm space-y-2"
      :class="importResult.errors.length ? 'border-orange-200 bg-orange-50' : 'border-green-200 bg-green-50'">
      <div class="flex items-center justify-between">
        <p class="font-semibold" :class="importResult.errors.length ? 'text-orange-700' : 'text-green-700'">
          <Icon :name="importResult.errors.length ? 'lucide:alert-triangle' : 'lucide:check-circle'" class="w-4 h-4 inline mr-1" />
          {{ importResult.updatedCount }} variante(s) mise(s) à jour
          <span v-if="importResult.errors.length" class="ml-2 text-orange-600">· {{ importResult.errors.length }} erreur(s)</span>
        </p>
        <button class="text-[--color-text-muted] hover:text-[--color-text-muted]" @click="importResult = null">
          <Icon name="lucide:x" class="w-4 h-4" />
        </button>
      </div>
      <ul v-if="importResult.errors.length" class="text-orange-600 space-y-0.5 text-xs">
        <li v-for="e in importResult.errors" :key="e">{{ e }}</li>
      </ul>
    </div>

    <!-- Filters -->
    <div class="flex flex-wrap gap-3">
      <div class="relative flex-1 min-w-48">
        <Icon name="lucide:search" class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[--color-text-muted]" />
        <input v-model="filterName" type="text" placeholder="Rechercher par nom…" class="input pl-9 py-2 text-sm w-full" />
      </div>
      <select v-model="filterActive" class="input py-2 text-sm">
        <option value="">Tous les statuts</option>
        <option value="true">Actifs</option>
        <option value="false">Inactifs</option>
      </select>
    </div>

    <!-- Table -->
    <div class="bg-white rounded-xl border border-[--color-border] overflow-hidden">
      <div v-if="pending" class="p-10 text-center text-[--color-text-muted]">
        <Icon name="lucide:loader-2" class="w-6 h-6 animate-spin mx-auto mb-2" />
        Chargement…
      </div>

      <div v-else-if="!products.length" class="p-10 text-center text-[--color-text-muted]">
        Aucun produit. <button class="text-[--color-primary-dark] underline" @click="openCreate">Créer le premier.</button>
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full text-sm">
          <thead class="bg-[--color-background-soft] text-[--color-text-muted] text-xs uppercase tracking-wide border-b border-[--color-border]">
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
            <tr v-for="product in products" :key="product.id" class="hover:bg-[--color-background-soft] transition-colors">
              <!-- Thumbnail -->
              <td class="px-5 py-3">
                <div class="w-10 h-10 rounded-lg overflow-hidden bg-[--color-background-soft]">
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
                <p class="font-medium text-[--color-text]">{{ product.name }}</p>
                <p class="text-[--color-text-muted] text-xs font-mono">{{ product.slug }}</p>
              </td>
              <td class="px-5 py-3 text-[--color-text-muted]">{{ product.categoryName }}</td>
              <td class="px-5 py-3 text-[--color-text-muted] text-xs">{{ materialLabel(product.material) }}</td>
              <td class="px-5 py-3 text-[--color-text] whitespace-nowrap">
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
                  :class="product.active !== false ? 'text-green-600' : 'text-[--color-text-muted]'"
                >
                  {{ product.active !== false ? 'Actif' : 'Inactif' }}
                </span>
              </td>
              <td class="px-5 py-3">
                <div class="flex items-center gap-2 justify-end">
                  <button class="text-xs text-blue-600 hover:underline" @click="openEdit(product.id)">Modifier</button>
                  <button
                    class="text-xs hover:underline"
                    :class="product.active !== false ? 'text-red-500' : 'text-green-600'"
                    :disabled="togglingId === product.id"
                    @click="toggleActive(product)"
                  >
                    <Icon v-if="togglingId === product.id" name="lucide:loader-2" class="w-3 h-3 animate-spin inline" />
                    <span v-else>{{ product.active !== false ? 'Désactiver' : 'Réactiver' }}</span>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="data && data.totalPages > 1" class="flex justify-center gap-2">
      <button class="px-3 py-1.5 rounded-lg border border-[--color-border] text-sm disabled:opacity-40" :disabled="page === 0" @click="page--">← Précédent</button>
      <span class="px-3 py-1.5 text-sm text-[--color-text-muted]">{{ page + 1 }} / {{ data.totalPages }}</span>
      <button class="px-3 py-1.5 rounded-lg border border-[--color-border] text-sm disabled:opacity-40" :disabled="data.last" @click="page++">Suivant →</button>
    </div>

    <!-- ── Product panel (create / edit) ───────────────────────── -->
    <Transition name="slide">
      <div v-if="panelOpen" class="fixed inset-0 z-50 flex justify-end">
        <div class="absolute inset-0 bg-black/30" @click="closePanel" />
        <aside class="relative w-full max-w-2xl bg-white shadow-2xl flex flex-col overflow-y-auto">

          <!-- Panel header -->
          <div class="flex items-center justify-between px-6 py-5 border-b border-[--color-border] sticky top-0 bg-white z-10">
            <h3 class="font-semibold text-[--color-text]">
              {{ editingId ? 'Modifier le produit' : 'Nouveau produit' }}
            </h3>
            <button class="text-[--color-text-muted] hover:text-[--color-text-muted]" @click="closePanel">
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
              <h4 class="text-xs font-semibold text-[--color-text-muted] uppercase tracking-wide">Informations produit</h4>

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
                    <option value="TITANIUM">Titane ASTM F136</option>
                    <option value="STEEL">Acier chirurgical</option>
                    <option value="BIOFLEX">Bioflex</option>
                    <option value="GOLD">Or 14K</option>
                  </select>
                </div>
                <div class="col-span-2">
                  <label class="field-label">Description</label>
                  <textarea v-model="form.description" class="input resize-none" rows="3" placeholder="Description du produit…" />
                </div>
                <div class="col-span-2">
                  <label class="flex items-center gap-2.5 cursor-pointer w-fit">
                    <input v-model="form.nickelFree" type="checkbox" class="w-4 h-4 rounded accent-[--color-primary] cursor-pointer" />
                    <span class="text-sm text-[--color-text]">Nickel Free <span class="text-xs text-[--color-text-muted]">(badge affiché sur la fiche produit)</span></span>
                  </label>
                </div>
              </div>
            </section>

            <!-- ── SEO ── -->
            <section class="space-y-4">
              <h4 class="text-xs font-semibold text-[--color-text-muted] uppercase tracking-wide">SEO (optionnel)</h4>
              <div>
                <label class="field-label">Meta title</label>
                <input v-model="form.metaTitle" type="text" class="input" placeholder="Anneau Titane Hélix — Piercing by Samar" />
              </div>
              <div>
                <label class="field-label">Meta description</label>
                <textarea v-model="form.metaDescription" class="input resize-none" rows="2" />
              </div>
            </section>

            <!-- ── Tags (edit only) ── -->
            <section v-if="editingProduct" class="space-y-3">
              <div class="flex items-center gap-2">
                <h4 class="text-xs font-semibold text-[--color-text-muted] uppercase tracking-wide">Tags</h4>
                <span class="text-[10px] text-[--color-text-muted] bg-[--color-background-soft] rounded px-1.5 py-0.5">auto-sauvegardé</span>
                <Transition name="fade">
                  <span v-if="tagSaved" class="text-xs text-green-600 flex items-center gap-1">
                    <Icon name="lucide:check" class="w-3 h-3" /> sauvegardé
                  </span>
                </Transition>
              </div>
              <div class="flex flex-wrap gap-2">
                <button
                  v-for="tag in allTags"
                  :key="tag.id"
                  type="button"
                  class="px-3 py-1 rounded-full text-xs font-medium border transition-colors"
                  :class="productTagIds.has(tag.id)
                    ? 'bg-[--color-primary] text-white border-[--color-primary]'
                    : 'bg-white text-[--color-text-muted] border-[--color-border] hover:border-[--color-primary]'"
                  @click="toggleTag(tag.id)"
                >
                  {{ tag.name }}
                </button>
              </div>
              <p v-if="allTags.length === 0" class="text-xs text-[--color-text-muted]">
                Aucun tag disponible. <NuxtLink to="/admin/tags" class="underline">Créer des tags</NuxtLink>
              </p>
            </section>

            <!-- ── Variants (edit only) ── -->
            <section v-if="editingProduct" class="space-y-4">
              <div class="flex items-center justify-between">
                <h4 class="text-xs font-semibold text-[--color-text-muted] uppercase tracking-wide">Variantes</h4>
                <button class="text-xs text-[--color-primary-dark] hover:underline flex items-center gap-1" @click="openAddVariant">
                  <Icon name="lucide:plus" class="w-3.5 h-3.5" />
                  Ajouter
                </button>
              </div>

              <!-- Add variant form -->
              <Transition name="fade">
                <div v-if="showAddVariant" class="bg-[--color-background-soft] rounded-xl p-4 space-y-3">
                  <div class="grid grid-cols-2 gap-3">
                    <div class="col-span-2">
                      <label class="field-label">SKU *</label>
                      <div class="flex gap-2">
                        <input v-model="newVariant.sku" type="text" class="input py-2 text-sm flex-1" :class="variantErrors.sku ? 'border-red-400' : ''" placeholder="PBS-LABRET-001" />
                        <button type="button" class="btn-ghost py-2 px-3 text-xs whitespace-nowrap" @click="generateSku">
                          <Icon name="lucide:wand-2" class="w-3.5 h-3.5 mr-1 inline" />Auto
                        </button>
                      </div>
                      <p v-if="variantErrors.sku" class="text-xs text-red-500 mt-1">{{ variantErrors.sku }}</p>
                    </div>
                    <div>
                      <label class="field-label">Prix (MAD) *</label>
                      <input v-model.number="newVariant.priceEuros" type="number" min="0" step="1" class="input py-2 text-sm" :class="variantErrors.price ? 'border-red-400' : ''" placeholder="299" />
                      <p v-if="variantErrors.price" class="text-xs text-red-500 mt-1">{{ variantErrors.price }}</p>
                    </div>
                    <div>
                      <label class="field-label">Stock</label>
                      <input v-model.number="newVariant.stock" type="number" min="0" class="input py-2 text-sm" placeholder="10" />
                    </div>
                    <div>
                      <label class="field-label">Taille *</label>
                      <input v-model="newVariant.size" type="text" class="input py-2 text-sm" :class="variantErrors.size ? 'border-red-400' : ''" placeholder="8mm" />
                      <p v-if="variantErrors.size" class="text-xs text-red-500 mt-1">{{ variantErrors.size }}</p>
                    </div>
                    <div>
                      <label class="field-label">Couleur *</label>
                      <input v-model="newVariant.color" type="text" class="input py-2 text-sm" :class="variantErrors.color ? 'border-red-400' : ''" placeholder="Silver" />
                      <p v-if="variantErrors.color" class="text-xs text-red-500 mt-1">{{ variantErrors.color }}</p>
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
                  class="bg-[--color-background-soft] rounded-xl px-4 py-3 text-sm space-y-2"
                >
                  <!-- Collapsed view -->
                  <div v-if="editingVariantId !== v.id" class="flex items-center justify-between">
                    <div>
                      <p class="font-medium text-[--color-text]">
                        {{ [v.size, v.color].filter(Boolean).join(' · ') || '—' }}
                        <span v-if="!v.active" class="ml-2 text-xs text-orange-500">(inactif)</span>
                      </p>
                      <p class="text-[--color-text-muted] text-xs">
                        {{ v.sku }} · {{ formatPrice(v.priceCents) }} ·
                        dispo : <span :class="v.availableStock === 0 ? 'text-red-400 font-medium' : ''">{{ v.availableStock }}</span>
                        <template v-if="v.reservedStock > 0">
                          · <span class="text-amber-500">{{ v.reservedStock }} réservé{{ v.reservedStock > 1 ? 's' : '' }}</span>
                        </template>
                        · total : {{ v.stock }}
                      </p>
                    </div>
                    <div class="flex items-center gap-2">
                      <button class="text-[--color-text-muted] hover:text-[--color-text]" @click="openEditVariant(v)">
                        <Icon name="lucide:pencil" class="w-4 h-4" />
                      </button>
                      <button class="text-red-400 hover:text-red-600" @click="deleteVariant(v.id)">
                        <Icon name="lucide:trash-2" class="w-4 h-4" />
                      </button>
                    </div>
                  </div>

                  <!-- Inline edit form -->
                  <div v-else class="space-y-3">
                    <div class="grid grid-cols-2 gap-3">
                      <div>
                        <label class="label text-xs">SKU</label>
                        <input v-model="editVariantForm.sku" type="text" class="input py-1.5 text-sm" />
                      </div>
                      <div>
                        <label class="label text-xs">Prix (MAD)</label>
                        <input v-model.number="editVariantForm.priceEuros" type="number" min="0" step="1" class="input py-1.5 text-sm" />
                      </div>
                      <div>
                        <label class="label text-xs">Taille</label>
                        <input v-model="editVariantForm.size" type="text" class="input py-1.5 text-sm" placeholder="8mm" />
                      </div>
                      <div>
                        <label class="label text-xs">Couleur</label>
                        <input v-model="editVariantForm.color" type="text" class="input py-1.5 text-sm" placeholder="Silver" />
                      </div>
                      <div>
                        <label class="label text-xs">Stock</label>
                        <input v-model.number="editVariantForm.stock" type="number" min="0" class="input py-1.5 text-sm" />
                      </div>
                      <div>
                        <label class="label text-xs opacity-0 select-none">.</label>
                        <label class="flex items-center gap-2 text-sm text-[--color-text] cursor-pointer py-1.5">
                          <input v-model="editVariantForm.active" type="checkbox" class="w-4 h-4 accent-[--color-primary]" />
                          Active
                        </label>
                      </div>
                    </div>
                    <div class="flex justify-end gap-2">
                      <button class="btn-ghost py-1 px-3 text-xs" @click="editingVariantId = null">Annuler</button>
                      <button class="btn-primary py-1 px-3 text-xs" :disabled="savingVariantEdit || !isVariantDirty" @click="saveVariant(v.id)">
                        <Icon v-if="savingVariantEdit" name="lucide:loader-2" class="w-3 h-3 animate-spin" />
                        <span v-else>Enregistrer</span>
                      </button>
                    </div>
                  </div>
                </li>
              </ul>
              <p v-else class="text-sm text-[--color-text-muted]">Aucune variante. Ajoutez-en au moins une.</p>
            </section>

            <!-- ── Images (edit only) ── -->
            <section v-if="editingProduct" class="space-y-4">
              <div class="flex items-center gap-2">
                <h4 class="text-xs font-semibold text-[--color-text-muted] uppercase tracking-wide">Images</h4>
                <span class="text-[10px] text-[--color-text-muted] bg-[--color-background-soft] rounded px-1.5 py-0.5">auto-sauvegardé</span>
              </div>

              <!-- Images produit (sans variante) -->
              <div class="space-y-2">
                <p class="text-xs text-[--color-text-muted] font-medium">Photos générales du produit</p>
                <ImageGalleryEditor
                  :images="sortedImages.filter(i => !i.variantId)"
                  :uploading="uploadingImages"
                  @delete="deleteImage"
                  @move="moveImage"
                  @update-alt="updateImageAlt"
                  @upload="(e) => uploadImagesForVariant(e, null)"
                />
              </div>

              <!-- Images par variante -->
              <div v-if="editingProduct.variants.length" class="space-y-3">
                <p class="text-xs text-[--color-text-muted] font-medium">Photos par variante <span class="text-[--color-text-muted] font-normal">(remplacent les photos générales lors de la sélection)</span></p>
                <div v-for="v in editingProduct.variants" :key="v.id" class="border border-[--color-border] rounded-xl p-3 space-y-2">
                  <p class="text-xs font-semibold text-[--color-text-muted]">
                    {{ [v.size, v.color].filter(Boolean).join(' · ') || v.sku }}
                  </p>
                  <ImageGalleryEditor
                    :images="sortedImages.filter(i => i.variantId === v.id)"
                    :uploading="uploadingVariantId === v.id"
                    :compact="true"
                    @delete="deleteImage"
                    @move="moveImage"
                    @update-alt="updateImageAlt"
                    @upload="(e) => uploadImagesForVariant(e, v.id)"
                  />
                </div>
              </div>
            </section>
          </div>

          <!-- Panel footer -->
          <div class="px-6 py-4 border-t border-[--color-border] bg-white sticky bottom-0 flex gap-3 justify-end">
            <button class="btn-ghost" @click="closePanel">Annuler</button>
            <button class="btn-primary" :disabled="savingProduct || (!!editingId && !isDirty)" @click="saveProduct">
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

const MATERIAL_LABELS: Record<string, string> = {
  TITANIUM: 'Titane ASTM F136',
  STEEL: 'Acier chirurgical',
  BIOFLEX: 'Bioflex',
  GOLD: 'Or 14K',
}
function materialLabel(val: string | null | undefined) {
  return val ? (MATERIAL_LABELS[val] ?? val) : '—'
}

const auth = useAuthStore()
const { get, post, put, del } = useApi()
const headers = computed(() => auth.authHeader)
const { success, error: toastError } = useToast()

// ── Products list ──────────────────────────────────────────────
const page = ref(0)
const filterName = ref('')
const filterActive = ref<string>('') // '', 'true', 'false'

const { data, pending, refresh } = await useAsyncData(
  'admin-products',
  () => {
    const query: Record<string, unknown> = { page: page.value, size: 20 }
    if (filterName.value.trim()) query.name = filterName.value.trim()
    if (filterActive.value !== '') query.active = filterActive.value
    return get<PageResponse<ProductSummary>>('/api/admin/products', { headers: headers.value, query })
  },
  { watch: [page, filterName, filterActive] }
)
const products = computed(() => data.value?.content ?? [])

watch([filterName, filterActive], () => { page.value = 0 })

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

// ── Tags ───────────────────────────────────────────────────────
interface Tag { id: number; name: string; slug: string }
const { data: tagsData } = await useAsyncData('admin-tags', () =>
  get<Tag[]>('/api/tags')
)
const allTags = computed(() => tagsData.value ?? [])

const productTagIds = ref<Set<number>>(new Set())
const tagSaved = ref(false)
let tagSavedTimer: ReturnType<typeof setTimeout> | null = null

// ── Panel state ────────────────────────────────────────────────
const panelOpen = ref(false)
const editingId = ref<number | null>(null)
const editingProduct = ref<ProductDetail | null>(null)
const panelError = ref<string | null>(null)
const savingProduct = ref(false)

// Watch editingProduct to sync selected tag IDs (must be after editingProduct declaration)
watch(editingProduct, (p) => {
  if (!p) { productTagIds.value = new Set(); return }
  const tagNames = p.tags ?? []
  productTagIds.value = new Set(
    allTags.value.filter(t => tagNames.includes(t.name)).map(t => t.id)
  )
})

async function toggleTag(tagId: number) {
  if (!editingProduct.value) return
  const productId = editingProduct.value.id
  if (productTagIds.value.has(tagId)) {
    await del(`/api/admin/products/${productId}/tags/${tagId}`, { headers: headers.value })
    productTagIds.value.delete(tagId)
    productTagIds.value = new Set(productTagIds.value)
  } else {
    await post(`/api/admin/products/${productId}/tags/${tagId}`, {}, { headers: headers.value })
    productTagIds.value.add(tagId)
    productTagIds.value = new Set(productTagIds.value)
  }
  tagSaved.value = true
  if (tagSavedTimer) clearTimeout(tagSavedTimer)
  tagSavedTimer = setTimeout(() => { tagSaved.value = false }, 2000)
}

const form = reactive({
  name: '',
  description: '',
  material: '',
  nickelFree: false,
  categoryId: null as number | null,
  metaTitle: '',
  metaDescription: '',
})

const initialForm = reactive({ ...form })

const isDirty = computed(() =>
  form.name !== initialForm.name ||
  form.description !== initialForm.description ||
  form.material !== initialForm.material ||
  form.nickelFree !== initialForm.nickelFree ||
  form.categoryId !== initialForm.categoryId ||
  form.metaTitle !== initialForm.metaTitle ||
  form.metaDescription !== initialForm.metaDescription
)

function snapshotForm() {
  Object.assign(initialForm, { ...form })
}

function resetForm() {
  form.name = ''
  form.description = ''
  form.material = ''
  form.nickelFree = false
  form.categoryId = null
  form.metaTitle = ''
  form.metaDescription = ''
  snapshotForm()
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
  snapshotForm()
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
      snapshotForm()
      success('Produit mis à jour.')
    } else {
      const created = await post<ProductDetail>('/api/admin/products', payload, { headers: headers.value })
      editingId.value = created.id
      editingProduct.value = created
      snapshotForm()
      success('Produit créé.')
    }
    await refresh()
    if (!editingId.value) closePanel()
  } catch (e: unknown) {
    const err = e as { data?: { message?: string } }
    panelError.value = err?.data?.message ?? 'Erreur lors de la sauvegarde.'
    toastError(panelError.value!)
  } finally {
    savingProduct.value = false
  }
}

const togglingId = ref<number | null>(null)

async function toggleActive(product: ProductSummary) {
  togglingId.value = product.id
  const wasActive = product.active !== false
  const newActive = !wasActive
  // Optimistic update
  const idx = data.value?.content.findIndex(p => p.id === product.id) ?? -1
  if (idx !== -1 && data.value) {
    data.value.content[idx] = { ...data.value.content[idx], active: newActive }
  }
  try {
    await put(`/api/admin/products/${product.id}`, { active: newActive }, { headers: headers.value })
    success(wasActive ? 'Produit désactivé.' : 'Produit réactivé.')
    refresh()
  } catch {
    // Revert optimistic update
    if (idx !== -1 && data.value) {
      data.value.content[idx] = { ...data.value.content[idx], active: wasActive }
    }
    toastError('Erreur lors de la modification.')
  } finally {
    togglingId.value = null
  }
}

// ── Variants ───────────────────────────────────────────────────
const showAddVariant = ref(false)
const savingVariant = ref(false)
const variantErrors = reactive({ sku: '', price: '', size: '', color: '' })
const newVariant = reactive({ sku: '', priceEuros: 0, size: '', color: '', stock: 0 })

function openAddVariant() {
  newVariant.sku = ''; newVariant.priceEuros = 0; newVariant.size = ''; newVariant.color = ''; newVariant.stock = 0
  variantErrors.sku = ''; variantErrors.price = ''; variantErrors.size = ''; variantErrors.color = ''
  showAddVariant.value = true
  generateSku()
}

function generateSku() {
  const base = (editingProduct.value?.name ?? 'PBS')
    .toUpperCase().replace(/[^A-Z0-9]/g, '-').replace(/-+/g, '-').slice(0, 8)
  const suffix = [newVariant.size, newVariant.color].filter(Boolean).join('-').toUpperCase().replace(/\s+/g, '').slice(0, 6)
  const rand = Math.random().toString(36).slice(2, 5).toUpperCase()
  newVariant.sku = [base, suffix, rand].filter(Boolean).join('-')
}

function validateVariantForm(): boolean {
  variantErrors.sku = newVariant.sku.trim() ? '' : 'SKU requis'
  variantErrors.price = newVariant.priceEuros > 0 ? '' : 'Prix requis (> 0)'
  variantErrors.size = newVariant.size.trim() ? '' : 'Taille requise'
  variantErrors.color = newVariant.color.trim() ? '' : 'Couleur requise'
  return !variantErrors.sku && !variantErrors.price && !variantErrors.size && !variantErrors.color
}

async function addVariant() {
  if (!editingId.value || !validateVariantForm()) return
  savingVariant.value = true
  try {
    const updated = await post<ProductDetail>(
      `/api/admin/products/${editingId.value}/variants`,
      { sku: newVariant.sku, priceCents: Math.round(newVariant.priceEuros * 100), size: newVariant.size || null, color: newVariant.color || null, stock: newVariant.stock },
      { headers: headers.value }
    )
    editingProduct.value = updated
    newVariant.sku = ''; newVariant.priceEuros = 0; newVariant.size = ''; newVariant.color = ''; newVariant.stock = 0
    variantErrors.sku = ''; variantErrors.price = ''; variantErrors.size = ''; variantErrors.color = ''
    showAddVariant.value = false
    refresh()
    success('Variante ajoutée.')
  } catch { toastError('Erreur lors de l\'ajout de la variante.') }
  finally { savingVariant.value = false }
}

async function deleteVariant(variantId: number) {
  if (!editingId.value) return
  try {
    await del(`/api/admin/products/${editingId.value}/variants/${variantId}`, { headers: headers.value })
    const updated = await get<ProductDetail>(`/api/admin/products/${editingId.value}`, { headers: headers.value }).catch(() => null)
    if (updated) editingProduct.value = updated
    refresh()
    success('Variante supprimée.')
  } catch { toastError('Erreur lors de la suppression.') }
}

// ── Variant inline edit ────────────────────────────────────────
const editingVariantId = ref<number | null>(null)
const savingVariantEdit = ref(false)
const editVariantForm = reactive({ sku: '', priceEuros: 0, size: '', color: '', stock: 0, active: true })
const initialVariantForm = reactive({ ...editVariantForm })

const isVariantDirty = computed(() =>
  editVariantForm.sku !== initialVariantForm.sku ||
  editVariantForm.priceEuros !== initialVariantForm.priceEuros ||
  editVariantForm.size !== initialVariantForm.size ||
  editVariantForm.color !== initialVariantForm.color ||
  editVariantForm.stock !== initialVariantForm.stock ||
  editVariantForm.active !== initialVariantForm.active
)

function openEditVariant(v: { id: number; sku: string; priceCents: number; size: string | null; color: string | null; stock: number; active: boolean }) {
  editingVariantId.value = v.id
  editVariantForm.sku = v.sku
  editVariantForm.priceEuros = v.priceCents / 100
  editVariantForm.size = v.size ?? ''
  editVariantForm.color = v.color ?? ''
  editVariantForm.stock = v.stock
  editVariantForm.active = v.active
  Object.assign(initialVariantForm, { ...editVariantForm })
}

async function saveVariant(variantId: number) {
  if (!editingId.value) return
  savingVariantEdit.value = true
  try {
    const updated = await put<ProductDetail>(
      `/api/admin/products/${editingId.value}/variants/${variantId}`,
      {
        sku: editVariantForm.sku,
        priceCents: Math.round(editVariantForm.priceEuros * 100),
        size: editVariantForm.size || null,
        color: editVariantForm.color || null,
        stock: editVariantForm.stock,
        active: editVariantForm.active,
      },
      { headers: headers.value },
    )
    editingProduct.value = updated
    editingVariantId.value = null
    refresh()
    success('Variante mise à jour.')
  } catch { toastError('Erreur lors de la mise à jour.') }
  finally { savingVariantEdit.value = false }
}

// ── Images ─────────────────────────────────────────────────────
const uploadingImages = ref(false)
const uploadingVariantId = ref<number | null>(null)

async function uploadImagesForVariant(e: Event, variantId: number | null) {
  if (!editingId.value) return
  const files = (e.target as HTMLInputElement).files
  if (!files?.length) return

  if (variantId === null) uploadingImages.value = true
  else uploadingVariantId.value = variantId

  let successCount = 0
  let errorCount = 0
  for (const file of Array.from(files)) {
    const fd = new FormData()
    fd.append('file', file)
    if (variantId !== null) fd.append('variantId', String(variantId))
    try {
      await $fetch(`/api/admin/products/${editingId.value}/images`, {
        method: 'POST',
        baseURL: '',
        headers: auth.authHeader,
        body: fd,
      })
      successCount++
    } catch (err: any) {
      errorCount++
      const msg = err?.data?.message ?? err?.message ?? 'Erreur inconnue'
      toastError(`Échec upload "${file.name}" : ${msg}`)
    }
  }
  const updated = await get<ProductDetail>(`/api/admin/products/${editingId.value}`, { headers: headers.value }).catch(() => null)
  if (updated) editingProduct.value = updated
  uploadingImages.value = false
  uploadingVariantId.value = null
  refresh()
  if (successCount > 0 && errorCount === 0) success(`${successCount} image${successCount > 1 ? 's' : ''} uploadée${successCount > 1 ? 's' : ''}.`)
  else if (successCount > 0) success(`${successCount} uploadée(s), ${errorCount} échouée(s).`)
  ;(e.target as HTMLInputElement).value = ''
}

async function deleteImage(imageId: number) {
  if (!editingId.value) return
  try {
    await del(`/api/admin/products/${editingId.value}/images/${imageId}`, { headers: headers.value })
    const updated = await get<ProductDetail>(`/api/admin/products/${editingId.value}`, { headers: headers.value }).catch(() => null)
    if (updated) editingProduct.value = updated
    refresh()
    success('Image supprimée.')
  } catch { toastError('Erreur lors de la suppression.') }
}

const sortedImages = computed(() =>
  [...(editingProduct.value?.images ?? [])].sort((a, b) => (a.position ?? 0) - (b.position ?? 0))
)

async function updateImageAlt(imageId: number, altText: string) {
  if (!editingId.value) return
  try {
    await $fetch(`/api/admin/products/${editingId.value}/images/${imageId}`, {
      method: 'PATCH',
      baseURL: '',
      headers: auth.authHeader,
      body: { altText },
    })
    // Update locally without full reload
    if (editingProduct.value) {
      const img = editingProduct.value.images.find(i => i.id === imageId)
      if (img) img.altText = altText
    }
    success('Alt text mis à jour.')
  } catch { toastError('Erreur lors de la mise à jour.') }
}

async function moveImage(imageId: number, currentIdx: number, direction: -1 | 1) {
  if (!editingId.value || !editingProduct.value) return

  // Trouver l'image et travailler dans son groupe (même variantId)
  const targetImage = sortedImages.value.find(i => i.id === imageId)
  if (!targetImage) return
  const imgs = sortedImages.value.filter(i => i.variantId === targetImage.variantId)

  const swapIdx = currentIdx + direction
  if (swapIdx < 0 || swapIdx >= imgs.length) return

  const posA = currentIdx
  const posB = swapIdx
  imgs[currentIdx].position = posB
  imgs[swapIdx].position = posA
  editingProduct.value.images = editingProduct.value.images.map(img => {
    const updated = imgs.find(i => i.id === img.id)
    return updated ?? img
  })

  try {
    await Promise.all([
      $fetch(`/api/admin/products/${editingId.value}/images/${imgs[currentIdx].id}`, {
        method: 'PATCH',
        baseURL: '',
        headers: auth.authHeader,
        body: { position: posB },
      }),
      $fetch(`/api/admin/products/${editingId.value}/images/${imgs[swapIdx].id}`, {
        method: 'PATCH',
        baseURL: '',
        headers: auth.authHeader,
        body: { position: posA },
      }),
    ])
  } catch { toastError('Erreur lors du réordonnancement.') }
}

function formatPrice(cents: number) {
  if (!cents && cents !== 0) return '—'
  return Math.round(cents / 100) + ' MAD'
}

// ── Import stocks ──────────────────────────────────────────────
const importing = ref(false)
interface ImportResult { updatedCount: number; errors: string[]; updated: string[] }
const importResult = ref<ImportResult | null>(null)

async function downloadTemplate() {
  const res = await fetch('/api/admin/import/stock-template.csv', { headers: auth.authHeader })
  const blob = await res.blob()
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url; a.download = 'template_stocks.csv'; a.click()
  URL.revokeObjectURL(url)
}

async function importStock(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  importing.value = true
  importResult.value = null
  try {
    const fd = new FormData()
    fd.append('file', file)
    const result = await $fetch<ImportResult>('/api/admin/import/stock', {
      method: 'POST', baseURL: '', headers: auth.authHeader, body: fd,
    })
    importResult.value = result
    if (result.updatedCount > 0) { await refresh(); refreshPanelProduct() }
    if (!result.errors.length) success(`${result.updatedCount} stock(s) mis à jour.`)
  } catch (err: any) {
    toastError(err?.data?.message ?? 'Erreur lors de l\'import')
  } finally {
    importing.value = false
    ;(e.target as HTMLInputElement).value = ''
  }
}

function refreshPanelProduct() {
  if (!editingId.value || !editingProduct.value) return
  get<ProductDetail>(`/api/admin/products/${editingId.value}`, { headers: headers.value })
    .then(p => { if (p) editingProduct.value = p })
    .catch(() => {})
}

useSeoMeta({ title: 'Produits — Admin', robots: 'noindex' })

const route = useRoute()
onMounted(async () => {
  const editId = route.query.edit
  if (editId) {
    await openEdit(Number(editId))
  }
})
</script>

<style scoped>
.field-label { @apply text-xs font-semibold text-[--color-text-muted] uppercase tracking-wide block mb-1.5; }
.slide-enter-active, .slide-leave-active { transition: opacity 0.25s; }
.slide-enter-from, .slide-leave-to { opacity: 0; }
.slide-enter-active aside, .slide-leave-active aside { transition: transform 0.3s cubic-bezier(0.4,0,0.2,1); }
.slide-enter-from aside, .slide-leave-to aside { transform: translateX(100%); }
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s, transform 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(-4px); }
</style>
