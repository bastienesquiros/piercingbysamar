<template>
  <div class="space-y-5">

    <!-- Header -->
    <div class="flex items-center justify-between">
      <h2 class="text-xl font-semibold text-gray-800">Catégories</h2>
      <button class="btn-primary py-2 px-4 text-sm" @click="openCreate">
        <Icon name="lucide:plus" class="w-4 h-4" />
        Nouvelle catégorie
      </button>
    </div>

    <!-- Table -->
    <div class="bg-white rounded-xl border border-gray-200 overflow-hidden">
      <div v-if="pending" class="p-10 text-center text-gray-400">
        <Icon name="lucide:loader-2" class="w-6 h-6 animate-spin mx-auto mb-2" />
        Chargement…
      </div>
      <div v-else-if="!categories.length" class="p-10 text-center text-gray-400">
        Aucune catégorie.
      </div>
      <div v-else class="overflow-x-auto">
        <table class="w-full text-sm">
          <thead class="bg-gray-50 text-gray-500 text-xs uppercase tracking-wide border-b border-gray-100">
            <tr>
              <th class="text-left px-5 py-3">Nom</th>
              <th class="text-left px-5 py-3">Slug</th>
              <th class="text-left px-5 py-3">Parent</th>
              <th class="text-left px-5 py-3">Description</th>
              <th class="px-5 py-3 w-24" />
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-50">
            <tr v-for="cat in categories" :key="cat.id" class="hover:bg-gray-50/50 transition-colors">
              <td class="px-5 py-3 font-medium text-gray-800">{{ cat.name }}</td>
              <td class="px-5 py-3 text-gray-500 font-mono text-xs">{{ cat.slug }}</td>
              <td class="px-5 py-3 text-gray-500 text-xs">{{ parentName(cat.parentId) }}</td>
              <td class="px-5 py-3 text-gray-400 text-xs max-w-xs truncate">{{ cat.description || '—' }}</td>
              <td class="px-5 py-3">
                <div class="flex items-center gap-2 justify-end">
                  <button class="text-gray-400 hover:text-gray-700" @click="openEdit(cat)">
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
          <div class="flex items-center justify-between px-6 py-4 border-b border-gray-200">
            <h3 class="font-semibold text-gray-800">
              {{ editingId ? 'Modifier la catégorie' : 'Nouvelle catégorie' }}
            </h3>
            <button class="text-gray-400 hover:text-gray-700" @click="closePanel">
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
              <p class="text-xs text-gray-400 mt-1">URL : /catalogue?category={{ form.slug || 'slug' }}</p>
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
          </div>

          <!-- Panel footer -->
          <div class="px-6 py-4 border-t border-gray-200 flex justify-end gap-3">
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
