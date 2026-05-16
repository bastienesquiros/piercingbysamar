<template>
  <div class="min-h-screen bg-[--color-background-soft] flex flex-col items-center justify-center px-4 text-center">
    <div class="mb-6">
      <p class="font-serif text-8xl font-semibold text-[--color-primary-light]">{{ error.statusCode }}</p>
    </div>
    <h1 class="font-serif text-3xl font-semibold text-[--color-text] mb-3">
      {{ title }}
    </h1>
    <p class="text-[--color-text-muted] max-w-sm mb-8">{{ message }}</p>
    <div class="flex items-center gap-3">
      <button class="btn-outline" @click="handleError">
        <Icon name="lucide:arrow-left" class="w-4 h-4" />
        Retour
      </button>
      <NuxtLink to="/" class="btn-primary">
        <Icon name="lucide:home" class="w-4 h-4" />
        Accueil
      </NuxtLink>
    </div>
    <p class="mt-10 text-xs text-[--color-border]">Piercing by Samar</p>
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

function handleError() {
  clearError({ redirect: '/' })
}
</script>
