<template>
  <div class="space-y-5">

    <!-- Header -->
    <div class="flex items-center justify-end">
      <button class="btn-primary py-2 px-4 text-sm" @click="openCreate">
        <Icon name="lucide:plus" class="w-4 h-4" />
        Nouvelle catégorie
      </button>
    </div>

    <!-- Table -->
    <div class="bg-white rounded-xl border border-[--color-border] overflow-hidden">
      <div v-if="pending" class="p-10 text-center text-[--color-text-muted]">
        <Icon name="lucide:loader-2" class="w-6 h-6 animate-spin mx-auto mb-2" />
        Chargement…
      </div>
      <div v-else-if="!categories.length" class="p-10 text-center text-[--color-text-muted]">
        Aucune catégorie.
      </div>
      <div v-else class="overflow-x-auto">
        <table class="w-full text-sm">
          <thead class="bg-[--color-background-soft] text-[--color-text-muted] text-xs uppercase tracking-wide border-b border-[--color-border]">
            <tr>
              <th class="text-left px-5 py-3">Image</th>
              <th class="text-left px-5 py-3">Nom</th>
              <th class="text-left px-5 py-3">Slug</th>
              <th class="text-left px-5 py-3">Parent</th>
              <th class="text-left px-5 py-3">Description</th>
              <th class="px-5 py-3 w-24" />
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-50">
            <tr v-for="cat in categories" :key="cat.id" class="hover:bg-[--color-background-soft]/50 transition-colors">
              <td class="px-5 py-3">
                <div v-if="cat.imageUrl" class="w-10 h-10 rounded-lg overflow-hidden border border-[--color-border]">
                  <img :src="cat.imageUrl" :alt="cat.name" class="w-full h-full object-cover" />
                </div>
                <div v-else class="w-10 h-10 rounded-lg bg-[--color-background-soft] flex items-center justify-center">
                  <Icon name="lucide:image" class="w-4 h-4 text-gray-300" />
                </div>
              </td>
              <td class="px-5 py-3 font-medium text-[--color-text]">{{ cat.name }}</td>
              <td class="px-5 py-3 text-[--color-text-muted] font-mono text-xs">{{ cat.slug }}</td>
              <td class="px-5 py-3 text-[--color-text-muted] text-xs">{{ parentName(cat.parentId) }}</td>
              <td class="px-5 py-3 text-[--color-text-muted] text-xs max-w-xs truncate">{{ cat.description || '—' }}</td>
              <td class="px-5 py-3">
                <div class="flex items-center gap-2 justify-end">
                  <button class="text-[--color-text-muted] hover:text-[--color-text]" @click="openEdit(cat)">
                    <Icon name="lucide:pencil" class="w-4 h-4" />
                  </button>
                  <button class="text-red-400 hover:text-red-600" @click="deleteCategory(cat)">
                    <Icon name="lucide:trash-2" class="w-4 h-4" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Slide-over panel -->
    <Transition name="slide">
      <div v-if="panelOpen" class="fixed inset-0 z-50 flex">
        <div class="flex-1 bg-black/30" @click="closePanel" />
        <div class="w-full max-w-md bg-white shadow-xl flex flex-col">
          <!-- Panel header -->
          <div class="flex items-center justify-between px-6 py-4 border-b border-[--color-border]">
            <h3 class="font-semibold text-[--color-text]">
              {{ editingId ? 'Modifier la catégorie' : 'Nouvelle catégorie' }}
            </h3>
            <button class="text-[--color-text-muted] hover:text-[--color-text]" @click="closePanel">
              <Icon name="lucide:x" class="w-5 h-5" />
            </button>
          </div>

          <!-- Panel body -->
          <div class="flex-1 overflow-y-auto p-6 space-y-4">
            <div v-if="panelError" class="bg-red-50 text-red-700 text-sm px-4 py-3 rounded-lg">
              {{ panelError }}
            </div>

            <div>
              <label class="label">Nom <span class="text-red-500">*</span></label>
              <input v-model="form.name" type="text" class="input" placeholder="Septum" @input="autoslug" />
            </div>

            <div>
              <label class="label">Slug <span class="text-red-500">*</span></label>
              <input v-model="form.slug" type="text" class="input font-mono" placeholder="septum" />
              <p class="text-xs text-[--color-text-muted] mt-1">URL : /catalogue?category={{ form.slug || 'slug' }}</p>
            </div>

            <div>
              <label class="label">Catégorie parente</label>
              <select v-model="form.parentId" class="input">
                <option :value="null">— Aucune (catégorie racine) —</option>
                <option v-for="cat in parentOptions" :key="cat.id" :value="cat.id">
                  {{ cat.name }}
                </option>
              </select>
            </div>

            <div>
              <label class="label">Description</label>
              <textarea v-model="form.description" class="input" rows="3" placeholder="Description optionnelle…" />
            </div>

            <!-- Image (uniquement en édition) -->
            <div v-if="editingId">
              <div class="flex items-center gap-2 mb-3">
                <label class="label mb-0">Image</label>
                <span class="text-[10px] text-[--color-text-muted] bg-[--color-background-soft] rounded px-1.5 py-0.5">auto-sauvegardé</span>
              </div>

              <!-- Zone image : preview ou placeholder -->
              <div class="flex items-start gap-4">
                <div class="relative w-24 h-24 rounded-xl overflow-hidden border-2 shrink-0"
                  :class="currentImageUrl ? 'border-[--color-border] shadow-sm' : 'border-dashed border-gray-300 bg-[--color-background-soft]'"
                >
                  <img v-if="currentImageUrl" :src="currentImageUrl" alt="Image catégorie" class="w-full h-full object-cover" />
                  <div v-else class="w-full h-full flex items-center justify-center">
                    <Icon name="lucide:image" class="w-8 h-8 text-gray-300" />
                  </div>
                  <!-- Supprimer -->
                  <button
                    v-if="currentImageUrl"
                    class="absolute inset-0 flex items-center justify-center bg-black/0 hover:bg-black/40 transition-colors group"
                    :disabled="imageUploading"
                    title="Supprimer l'image"
                    @click="removeImage"
                  >
                    <Icon name="lucide:trash-2" class="w-5 h-5 text-white opacity-0 group-hover:opacity-100 transition-opacity drop-shadow" />
                  </button>
                </div>

                <div class="space-y-2 pt-1">
                  <label class="btn-outline py-2 px-3 text-sm cursor-pointer inline-flex items-center gap-2">
                    <Icon name="lucide:upload" class="w-4 h-4" />
                    <span>{{ currentImageUrl ? 'Changer' : 'Ajouter une image' }}</span>
                    <input type="file" accept="image/*" class="hidden" :disabled="imageUploading" @change="onImageSelected" />
                  </label>
                  <p class="text-xs text-[--color-text-muted]">Format carré · min. 400×400 px · max 5 Mo</p>
                  <p v-if="imageUploading" class="text-xs text-[--color-text-muted] flex items-center gap-1">
                    <Icon name="lucide:loader-2" class="w-3 h-3 animate-spin" />
                    Upload en cours…
                  </p>
                </div>
              </div>
            </div>
          </div>

          <!-- Panel footer -->
          <div class="px-6 py-4 border-t border-[--color-border] flex justify-end gap-3">
            <button class="btn-ghost py-2 px-4 text-sm" @click="closePanel">Annuler</button>
            <button class="btn-primary py-2 px-4 text-sm" :disabled="saving || (!!editingId && !isDirty)" @click="save">
              <Icon v-if="saving" name="lucide:loader-2" class="w-4 h-4 animate-spin" />
              <span v-else>{{ editingId ? 'Enregistrer' : 'Créer' }}</span>
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import type { Category } from '~/types'

definePageMeta({ layout: 'admin', middleware: 'admin' })

const auth = useAuthStore()
const { get, post, put, del } = useApi()
const headers = computed(() => auth.authHeader)
const { success, error: toastError } = useToast()

const { data, pending, refresh } = await useAsyncData(
  'admin-categories',
  () => get<Category[]>('/api/admin/categories', { headers: headers.value }),
)
const categories = computed(() => data.value ?? [])

const parentOptions = computed(() =>
  categories.value.filter(c => editingId.value ? c.id !== editingId.value : true),
)

function parentName(parentId: number | null) {
  if (!parentId) return '—'
  return categories.value.find(c => c.id === parentId)?.name ?? `#${parentId}`
}

// ── Panel ──────────────────────────────────────────────────────
const panelOpen = ref(false)
const editingId = ref<number | null>(null)
const panelError = ref<string | null>(null)
const saving = ref(false)

const form = reactive({
  name: '',
  slug: '',
  description: '',
  parentId: null as number | null,
})

const currentImageUrl = ref<string | null>(null)
const imageUploading = ref(false)

const initialForm = reactive({ ...form })

const isDirty = computed(() =>
  form.name !== initialForm.name ||
  form.slug !== initialForm.slug ||
  form.description !== initialForm.description ||
  form.parentId !== initialForm.parentId
)

function snapshotForm() {
  Object.assign(initialForm, { ...form })
}

function resetForm() {
  form.name = ''
  form.slug = ''
  form.description = ''
  form.parentId = null
  currentImageUrl.value = null
  snapshotForm()
}

function openCreate() {
  editingId.value = null
  resetForm()
  panelError.value = null
  panelOpen.value = true
}

function openEdit(cat: Category) {
  editingId.value = cat.id
  form.name = cat.name
  form.slug = cat.slug
  form.description = cat.description ?? ''
  form.parentId = cat.parentId
  currentImageUrl.value = cat.imageUrl
  snapshotForm()
  panelError.value = null
  panelOpen.value = true
}

function closePanel() {
  panelOpen.value = false
  editingId.value = null
  resetForm()
}

function autoslug() {
  if (editingId.value) return
  form.slug = form.name
    .toLowerCase()
    .normalize('NFD').replace(/\p{Diacritic}/gu, '')
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-|-$/g, '')
}

async function save() {
  if (!form.name.trim() || !form.slug.trim()) {
    panelError.value = 'Le nom et le slug sont obligatoires.'
    return
  }
  saving.value = true
  panelError.value = null
  try {
    const body = {
      name: form.name,
      slug: form.slug,
      description: form.description || null,
      parentId: form.parentId,
    }
    if (editingId.value) {
      await put(`/api/admin/categories/${editingId.value}`, body, { headers: headers.value })
    } else {
      await post('/api/admin/categories', body, { headers: headers.value })
    }
    await refresh()
    closePanel()
    success(editingId.value ? 'Catégorie mise à jour.' : 'Catégorie créée.')
  } catch (e: any) {
    panelError.value = e?.data?.message ?? 'Une erreur est survenue.'
    toastError(panelError.value!)
  } finally {
    saving.value = false
  }
}

async function onImageSelected(event: Event) {
  const file = (event.target as HTMLInputElement).files?.[0]
  if (!file || !editingId.value) return
  imageUploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    const updated = await post<Category>(
      `/api/admin/categories/${editingId.value}/image`,
      formData,
      { headers: headers.value },
    )
    currentImageUrl.value = updated.imageUrl
    await refresh()
    success('Image mise à jour.')
  } catch (e: any) {
    toastError(e?.data?.message ?? 'Erreur lors de l\'upload.')
  } finally {
    imageUploading.value = false
  }
}

async function removeImage() {
  if (!editingId.value) return
  imageUploading.value = true
  try {
    await del(`/api/admin/categories/${editingId.value}/image`, { headers: headers.value })
    currentImageUrl.value = null
    await refresh()
    success('Image supprimée.')
  } catch (e: any) {
    toastError(e?.data?.message ?? 'Erreur lors de la suppression.')
  } finally {
    imageUploading.value = false
  }
}

async function deleteCategory(cat: Category) {
  try {
    await del(`/api/admin/categories/${cat.id}`, { headers: headers.value })
    await refresh()
    success(`Catégorie "${cat.name}" supprimée.`)
  } catch (e: any) {
    toastError(e?.data?.message ?? 'Erreur lors de la suppression.')
  }
}
</script>
