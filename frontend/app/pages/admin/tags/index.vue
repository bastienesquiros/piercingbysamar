<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-serif font-semibold text-[--color-text]">Gestion des tags</h1>
    </div>

    <!-- Add tag -->
    <div class="bg-white rounded-2xl border border-[--color-border] p-6">
      <h2 class="font-semibold text-[--color-text] mb-4">Nouveau tag</h2>
      <div class="flex gap-3">
        <input
          v-model="newName"
          type="text"
          class="input flex-1"
          placeholder="Ex: Bestseller, Nouveauté, Titane…"
          @keydown.enter="create"
        />
        <button class="btn-primary" :disabled="!newName.trim() || creating" @click="create">
          <Icon v-if="creating" name="lucide:loader-2" class="w-4 h-4 animate-spin" />
          <Icon v-else name="lucide:plus" class="w-4 h-4" />
          Ajouter
        </button>
      </div>
      <p v-if="createError" class="text-red-500 text-sm mt-2">{{ createError }}</p>
    </div>

    <!-- Tags list -->
    <div class="bg-white rounded-2xl border border-[--color-border] p-6">
      <h2 class="font-semibold text-[--color-text] mb-4">Tags existants ({{ tags.length }})</h2>

      <div v-if="tags.length === 0" class="text-center py-8 text-[--color-text-muted]">
        Aucun tag pour l'instant.
      </div>

      <ul v-else class="divide-y divide-[--color-border]">
        <li v-for="tag in tags" :key="tag.id" class="py-3 flex items-center gap-3">

          <!-- Edit mode -->
          <template v-if="editingId === tag.id">
            <input
              v-model="editName"
              type="text"
              class="input flex-1 py-1.5 text-sm"
              @keydown.enter="saveEdit(tag.id)"
              @keydown.escape="cancelEdit"
            />
            <button class="btn-primary px-3 py-1.5 text-sm" :disabled="!editName.trim()" @click="saveEdit(tag.id)">
              <Icon name="lucide:check" class="w-4 h-4" />
            </button>
            <button class="btn-ghost px-3 py-1.5 text-sm" @click="cancelEdit">
              <Icon name="lucide:x" class="w-4 h-4" />
            </button>
          </template>

          <!-- View mode -->
          <template v-else>
            <span class="flex-1 text-sm font-medium text-[--color-text]">{{ tag.name }}</span>
            <span class="text-xs text-[--color-text-muted] font-mono">{{ tag.slug }}</span>
            <button class="btn-ghost px-2 py-1" @click="startEdit(tag)">
              <Icon name="lucide:pencil" class="w-4 h-4" />
            </button>
            <button
              class="btn-ghost px-2 py-1 text-red-500 hover:text-red-700 hover:bg-red-50"
              @click="remove(tag)"
            >
              <Icon name="lucide:trash-2" class="w-4 h-4" />
            </button>
          </template>
        </li>
      </ul>
    </div>

    <!-- Confirm delete modal -->
    <Teleport to="body">
      <div v-if="deleteTarget" class="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4">
        <div class="bg-white rounded-2xl p-6 max-w-sm w-full shadow-xl">
          <h3 class="font-semibold text-[--color-text] mb-2">Supprimer le tag ?</h3>
          <p class="text-sm text-[--color-text-muted] mb-6">
            Le tag <strong>"{{ deleteTarget.name }}"</strong> sera retiré de tous les produits.
          </p>
          <div class="flex gap-3 justify-end">
            <button class="btn-ghost" @click="deleteTarget = null">Annuler</button>
            <button class="btn-danger" :disabled="deleting" @click="confirmDelete">
              <Icon v-if="deleting" name="lucide:loader-2" class="w-4 h-4 animate-spin" />
              Supprimer
            </button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
definePageMeta({ layout: 'admin', middleware: 'admin' })

const { get, post, patch, del } = useApi()
const auth = useAuthStore()
const headers = computed(() => auth.authHeader)

interface Tag { id: number; name: string; slug: string }

const { data: tagsData, refresh: refreshAllTags } = await useAsyncData('tags-admin', () =>
  get<Tag[]>('/api/tags')
)
const tags = computed(() => tagsData.value ?? [])
const newName = ref('')
const creating = ref(false)
const createError = ref('')

const editingId = ref<number | null>(null)
const editName = ref('')

const deleteTarget = ref<Tag | null>(null)
const deleting = ref(false)

async function create() {
  if (!newName.value.trim()) return
  creating.value = true
  createError.value = ''
  try {
    await post<Tag>('/api/admin/tags', { name: newName.value.trim() }, { headers: headers.value })
    newName.value = ''
    await refreshAllTags()
  } catch (e: any) {
    createError.value = e?.data?.message ?? 'Erreur lors de la création'
  } finally {
    creating.value = false
  }
}

function startEdit(tag: Tag) {
  editingId.value = tag.id
  editName.value = tag.name
}

function cancelEdit() {
  editingId.value = null
  editName.value = ''
}

async function saveEdit(id: number) {
  if (!editName.value.trim()) return
  try {
    await patch<Tag>(`/api/admin/tags/${id}`, { name: editName.value.trim() }, { headers: headers.value })
    await refreshAllTags()
    cancelEdit()
  } catch (e: any) {
    alert(e?.data?.message ?? 'Erreur lors de la modification')
  }
}

function remove(tag: Tag) {
  deleteTarget.value = tag
}

async function confirmDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await del(`/api/admin/tags/${deleteTarget.value.id}`, { headers: headers.value })
    await refreshAllTags()
    deleteTarget.value = null
  } catch (e: any) {
    alert(e?.data?.message ?? 'Erreur lors de la suppression')
  } finally {
    deleting.value = false
  }
}

useSeoMeta({ title: 'Tags — Admin Piercing by Samar', robots: 'noindex' })
</script>
