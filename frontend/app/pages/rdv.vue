<template>
  <div class="container-site py-16 max-w-xl">
    <div class="mb-10">
      <p class="text-xs uppercase tracking-[0.3em] text-[--color-text-muted] mb-2">Marrakech · Maroc</p>
      <h1 class="font-serif text-4xl font-semibold text-[--color-text] mb-3">{{ $t('rdv.title') }}</h1>
      <p class="text-[--color-text-muted]">{{ $t('rdv.subtitle') }}</p>
    </div>

    <form class="space-y-6" @submit.prevent="submit">

      <!-- Prénom -->
      <div>
        <label class="block text-sm font-medium text-[--color-text] mb-1.5">
          {{ $t('rdv.field_name') }} <span class="text-red-500">*</span>
        </label>
        <input
          v-model="form.name"
          type="text"
          class="input w-full"
          :placeholder="$t('rdv.placeholder_name')"
          required
        />
      </div>

      <!-- Type de piercing -->
      <div>
        <label class="block text-sm font-medium text-[--color-text] mb-1.5">
          {{ $t('rdv.field_type') }} <span class="text-red-500">*</span>
        </label>
        <select v-model="form.type" class="input w-full" required>
          <option value="" disabled>{{ $t('rdv.placeholder_type') }}</option>
          <option v-for="cat in topLevel" :key="cat.slug" :value="cat.name">
            {{ $t('categories.' + cat.slug, cat.name) }}
          </option>
          <option value="Autre">{{ $t('rdv.other') }}</option>
        </select>
      </div>

      <!-- Date souhaitée -->
      <div>
        <label class="block text-sm font-medium text-[--color-text] mb-1.5">
          {{ $t('rdv.field_date') }} <span class="text-red-500">*</span>
        </label>
        <input
          v-model="form.date"
          type="date"
          class="input w-full"
          :min="minDate"
          required
        />
      </div>

      <!-- Créneau -->
      <div>
        <label class="block text-sm font-medium text-[--color-text] mb-2">
          {{ $t('rdv.field_slot') }} <span class="text-red-500">*</span>
        </label>
        <div class="grid grid-cols-3 gap-3">
          <button
            v-for="slot in slots"
            :key="slot.value"
            type="button"
            class="py-3 px-4 rounded-xl border text-sm font-medium transition-colors"
            :class="form.slot === slot.value
              ? 'border-[--color-primary] bg-[--color-primary-light] text-[--color-primary-dark]'
              : 'border-[--color-border] text-[--color-text-muted] hover:border-[--color-primary]/50'"
            @click="form.slot = slot.value"
          >
            <Icon :name="slot.icon" class="w-4 h-4 mb-1 mx-auto block" />
            {{ slot.label }}
          </button>
        </div>
      </div>

      <!-- Notes -->
      <div>
        <label class="block text-sm font-medium text-[--color-text] mb-1.5">
          {{ $t('rdv.field_notes') }}
          <span class="text-[--color-text-muted] font-normal">({{ $t('rdv.optional') }})</span>
        </label>
        <textarea
          v-model="form.notes"
          rows="3"
          class="input w-full resize-none"
          :placeholder="$t('rdv.placeholder_notes')"
        />
      </div>

      <!-- Submit -->
      <button
        type="submit"
        :disabled="!isValid"
        class="btn-primary w-full py-4 text-base flex items-center justify-center gap-2.5 disabled:opacity-40 disabled:cursor-not-allowed"
      >
        <Icon name="simple-icons:whatsapp" class="w-5 h-5" />
        {{ $t('rdv.submit') }}
      </button>

      <p class="text-center text-xs text-[--color-text-muted]">
        {{ $t('rdv.disclaimer') }}
      </p>
    </form>
  </div>
</template>

<script setup lang="ts">
const { t } = useI18n()
const { topLevel } = useCategories()

useHead({
  title: computed(() => `${t('rdv.title')} · Piercing by Samar`),
  meta: [{ name: 'description', content: computed(() => t('rdv.subtitle')) as unknown as string }],
})

const minDate = new Date(Date.now() + 86400000).toISOString().split('T')[0]

const form = reactive({
  name: '',
  type: '',
  date: '',
  slot: '',
  notes: '',
})

const slots = computed(() => [
  { value: 'Matin',        label: t('rdv.slot_morning'),   icon: 'lucide:sunrise' },
  { value: 'Après-midi',   label: t('rdv.slot_afternoon'), icon: 'lucide:sun' },
  { value: 'Indifférent',  label: t('rdv.slot_any'),       icon: 'lucide:clock' },
])

const isValid = computed(() =>
  form.name.trim() && form.type && form.date && form.slot
)

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('fr-FR', { weekday: 'long', day: 'numeric', month: 'long' })
}

function submit() {
  if (!isValid.value) return

  const lines = [
    `Bonjour ! Je souhaite prendre rendez-vous pour un piercing.`,
    ``,
    `• Prenom : ${form.name}`,
    `• Piercing : ${form.type}`,
    `• Date : ${formatDate(form.date)}`,
    `• Creneau : ${form.slot}`,
  ]
  if (form.notes.trim()) lines.push(`• Notes : ${form.notes.trim()}`)

  const msg = encodeURIComponent(lines.join('\n'))
  window.open(`https://wa.me/212781570083?text=${msg}`, '_blank', 'noopener')
}
</script>
