<template>
  <div class="min-h-screen bg-[--color-background-soft] flex flex-col items-center justify-center px-4 text-center">
    <div class="mb-6">
      <p class="font-serif text-8xl font-semibold text-[--color-primary-light]">{{ error.statusCode }}</p>
    </div>
    <h1 class="font-serif text-3xl font-semibold text-[--color-text] mb-3">
      {{ title }}
    </h1>
    <p class="text-[--color-text-muted] max-w-sm mb-8">{{ message }}</p>
    <div class="flex items-center gap-3 mb-10">
      <button class="btn-outline" @click="handleError">
        <Icon name="lucide:arrow-left" class="w-4 h-4" />
        Retour
      </button>
      <NuxtLink to="/" class="btn-primary">
        <Icon name="lucide:home" class="w-4 h-4" />
        Accueil
      </NuxtLink>
    </div>

    <!-- Aide WhatsApp -->
    <a :href="whatsappUrl" target="_blank" rel="noopener" class="inline-flex items-center gap-2 text-sm text-[--color-text-muted] hover:text-[--color-text] transition-colors">
      <Icon name="simple-icons:whatsapp" class="w-4 h-4 text-green-500" />
      Besoin d'aide ? Contactez-nous sur WhatsApp
    </a>
  </div>
</template>

<script setup lang="ts">
import type { NuxtError } from '#app'

const props = defineProps<{ error: NuxtError }>()

const title = computed(() => {
  if (props.error.statusCode === 404) return 'Page introuvable'
  if (props.error.statusCode === 403) return 'Accès refusé'
  return 'Une erreur est survenue'
})

const message = computed(() => {
  if (props.error.statusCode === 404) return 'La page que vous cherchez n\'existe pas ou a été déplacée.'
  if (props.error.statusCode === 403) return 'Vous n\'avez pas accès à cette page.'
  return 'Quelque chose s\'est mal passé. Veuillez réessayer.'
})

const whatsappUrl = computed(() => {
  const text = props.error.statusCode === 404
    ? `Bonjour, je cherchais une page sur votre site mais elle est introuvable. Pouvez-vous m'aider ?`
    : `Bonjour, j'ai rencontré une erreur sur votre site (code ${props.error.statusCode}). Pouvez-vous m'aider ?`
  return `https://wa.me/212781570083?text=${encodeURIComponent(text)}`
})

function handleError() {
  clearError({ redirect: '/' })
}
</script>
