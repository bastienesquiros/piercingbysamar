<template>
  <div class="container-site max-w-2xl py-12">
    <h1 class="section-title mb-2">Questions fréquentes</h1>
    <p class="text-[--color-text-muted] mb-10 text-center">Tout ce qu'il faut savoir avant de passer commande.</p>

    <div v-if="items.length" class="divide-y divide-[--color-border]">
      <div v-for="item in items" :key="item.id" class="py-5">
        <button
          class="flex items-center justify-between w-full text-left gap-4"
          @click="toggle(item.id)"
        >
          <span class="font-semibold text-[--color-text]">{{ item.question }}</span>
          <Icon
            name="lucide:chevron-down"
            class="w-5 h-5 text-[--color-primary] shrink-0 transition-transform duration-200"
            :class="{ 'rotate-180': open.has(item.id) }"
          />
        </button>
        <Transition name="faq">
          <p v-if="open.has(item.id)" class="mt-3 text-sm text-[--color-text-muted] leading-relaxed whitespace-pre-line">
            {{ item.answer }}
          </p>
        </Transition>
      </div>
    </div>

    <div v-else class="text-center py-16 text-[--color-text-muted]">
      Aucune question pour l'instant.
    </div>

    <div class="mt-12 bg-[--color-background-soft] rounded-2xl p-6 text-center space-y-3">
      <p class="font-semibold text-[--color-text]">Vous n'avez pas trouvé votre réponse ?</p>
      <a
        href="https://wa.me/212781570083"
        target="_blank"
        rel="noopener"
        class="inline-flex items-center gap-2 btn-primary"
      >
        <Icon name="simple-icons:whatsapp" class="w-4 h-4" />
        Nous contacter sur WhatsApp
      </a>
    </div>
  </div>
</template>

<script setup lang="ts">
interface FaqItem { id: number; question: string; answer: string }

const { get } = useApi()
const { data } = await useAsyncData('faq-public', () => get<FaqItem[]>('/api/faq'))
const items = computed(() => data.value ?? [])

const open = reactive(new Set<number>())
function toggle(id: number) {
  open.has(id) ? open.delete(id) : open.add(id)
}

useSeoMeta({
  title: 'FAQ — Piercing by Samar',
  description: 'Réponses à vos questions sur nos piercings, matériaux, prise de rendez-vous et click & collect à Marrakech.',
})
</script>

<style scoped>
.faq-enter-active, .faq-leave-active { transition: opacity 0.15s, max-height 0.2s; max-height: 300px; overflow: hidden; }
.faq-enter-from, .faq-leave-to { opacity: 0; max-height: 0; }
</style>
