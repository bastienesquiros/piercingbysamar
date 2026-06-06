<template>
  <div class="space-y-6">
    <!-- Nouveau -->
    <div class="bg-white rounded-2xl border border-[--color-border] p-6 space-y-4">
      <h2 class="font-semibold text-[--color-text]">Nouvelle question</h2>
      <div class="space-y-3">
        <input
          v-model="newQuestion"
          type="text"
          class="input"
          placeholder="Question…"
          @keydown.enter="createItem"
        />
        <textarea
          v-model="newAnswer"
          class="input min-h-[80px] resize-y"
          placeholder="Réponse…"
          rows="3"
        />
        <div class="flex justify-end">
          <button class="btn-primary" :disabled="!newQuestion.trim() || !newAnswer.trim() || creating" @click="createItem">
            <Icon v-if="creating" name="lucide:loader-2" class="w-4 h-4 animate-spin" />
            <Icon v-else name="lucide:plus" class="w-4 h-4" />
            Ajouter
          </button>
        </div>
      </div>
    </div>

    <!-- Liste -->
    <div class="bg-white rounded-2xl border border-[--color-border] p-6">
      <p class="font-semibold text-[--color-text] mb-4">Questions ({{ items.length }})</p>

      <div v-if="!items.length" class="text-center py-8 text-[--color-text-muted]">Aucune FAQ pour l'instant.</div>

      <ul v-else class="space-y-3">
        <li
          v-for="(item, idx) in items"
          :key="item.id"
          class="bg-[--color-background-soft] rounded-xl px-4 py-3 space-y-2"
        >
          <!-- Vue réduite -->
          <div v-if="editingId !== item.id" class="flex items-start justify-between gap-3">
            <div class="flex-1 min-w-0">
              <p class="font-medium text-sm text-[--color-text] flex items-center gap-2">
                <span v-if="!item.active" class="text-xs bg-orange-100 text-orange-600 rounded px-1.5 py-0.5">inactif</span>
                {{ item.question }}
              </p>
              <p class="text-xs text-[--color-text-muted] mt-0.5 line-clamp-2">{{ item.answer }}</p>
            </div>
            <div class="flex items-center gap-1 shrink-0">
              <!-- Réordonner -->
              <div class="flex flex-col">
                <button class="p-1 text-[--color-text-muted] hover:text-[--color-text] disabled:opacity-30" :disabled="idx === 0" @click="move(item, idx, -1)">
                  <Icon name="lucide:chevron-up" class="w-3.5 h-3.5" />
                </button>
                <button class="p-1 text-[--color-text-muted] hover:text-[--color-text] disabled:opacity-30" :disabled="idx === items.length - 1" @click="move(item, idx, 1)">
                  <Icon name="lucide:chevron-down" class="w-3.5 h-3.5" />
                </button>
              </div>
              <!-- Toggle actif -->
              <button
                class="p-1.5 rounded-lg transition-colors"
                :class="item.active ? 'text-green-600 hover:bg-green-50' : 'text-[--color-text-muted] hover:bg-[--color-background-soft]'"
                :title="item.active ? 'Désactiver' : 'Activer'"
                @click="toggleActive(item)"
              >
                <Icon :name="item.active ? 'lucide:eye' : 'lucide:eye-off'" class="w-4 h-4" />
              </button>
              <button class="p-1.5 text-[--color-text-muted] hover:text-[--color-text]" @click="startEdit(item)">
                <Icon name="lucide:pencil" class="w-4 h-4" />
              </button>
              <button class="p-1.5 text-red-400 hover:text-red-600" @click="deleteTarget = item">
                <Icon name="lucide:trash-2" class="w-4 h-4" />
              </button>
            </div>
          </div>

          <!-- Édition inline -->
          <div v-else class="space-y-3">
            <input v-model="editForm.question" type="text" class="input py-1.5 text-sm" placeholder="Question" />
            <textarea v-model="editForm.answer" class="input text-sm resize-y" rows="3" placeholder="Réponse" />
            <div class="flex justify-end gap-2">
              <button class="btn-ghost py-1 px-3 text-sm" @click="editingId = null">Annuler</button>
              <button class="btn-primary py-1 px-3 text-sm" :disabled="saving" @click="saveEdit(item.id)">
                <Icon v-if="saving" name="lucide:loader-2" class="w-3 h-3 animate-spin" />
                <span v-else>Enregistrer</span>
              </button>
            </div>
          </div>
        </li>
      </ul>
    </div>

    <!-- Modale suppression -->
    <Teleport to="body">
      <div v-if="deleteTarget" class="fixed inset-0 bg-black/40 z-50 flex items-center justify-center p-4">
        <div class="bg-white rounded-2xl p-6 max-w-sm w-full shadow-xl">
          <h3 class="font-semibold text-[--color-text] mb-2">Supprimer cette question ?</h3>
          <p class="text-sm text-[--color-text-muted] mb-6 line-clamp-3">« {{ deleteTarget.question }} »</p>
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

const { get, post, put, del } = useApi()
const auth = useAuthStore()
const headers = computed(() => auth.authHeader)

interface FaqItem { id: number; question: string; answer: string; position: number; active: boolean }

const { data, refresh } = await useAsyncData('faq-admin', () => get<FaqItem[]>('/api/admin/faq', { headers: headers.value }))
const items = computed(() => data.value ?? [])

const newQuestion = ref('')
const newAnswer = ref('')
const creating = ref(false)

const editingId = ref<number | null>(null)
const editForm = reactive({ question: '', answer: '' })
const saving = ref(false)

const deleteTarget = ref<FaqItem | null>(null)
const deleting = ref(false)

async function createItem() {
  if (!newQuestion.value.trim() || !newAnswer.value.trim()) return
  creating.value = true
  try {
    await post('/api/admin/faq', { question: newQuestion.value.trim(), answer: newAnswer.value.trim() }, { headers: headers.value })
    newQuestion.value = ''
    newAnswer.value = ''
    await refresh()
  } finally { creating.value = false }
}

function startEdit(item: FaqItem) {
  editingId.value = item.id
  editForm.question = item.question
  editForm.answer = item.answer
}

async function saveEdit(id: number) {
  saving.value = true
  try {
    await put(`/api/admin/faq/${id}`, { question: editForm.question, answer: editForm.answer }, { headers: headers.value })
    await refresh()
    editingId.value = null
  } finally { saving.value = false }
}

async function toggleActive(item: FaqItem) {
  await put(`/api/admin/faq/${item.id}`, { active: !item.active }, { headers: headers.value })
  await refresh()
}

async function move(item: FaqItem, idx: number, dir: -1 | 1) {
  const list = [...items.value]
  const swapIdx = idx + dir
  if (swapIdx < 0 || swapIdx >= list.length) return
  await Promise.all([
    put(`/api/admin/faq/${list[idx].id}`, { position: swapIdx }, { headers: headers.value }),
    put(`/api/admin/faq/${list[swapIdx].id}`, { position: idx }, { headers: headers.value }),
  ])
  await refresh()
}

async function confirmDelete() {
  if (!deleteTarget.value) return
  deleting.value = true
  try {
    await del(`/api/admin/faq/${deleteTarget.value.id}`, { headers: headers.value })
    await refresh()
    deleteTarget.value = null
  } finally { deleting.value = false }
}

useSeoMeta({ title: 'FAQ — Admin Piercing by Samar', robots: 'noindex' })
</script>
