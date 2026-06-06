<template>
  <div class="space-y-2">
    <!-- Images existantes -->
    <div
      v-for="(img, idx) in images"
      :key="img.id"
      class="flex gap-3 items-center bg-gray-50 rounded-xl p-2"
    >
      <div class="relative shrink-0 rounded-lg overflow-hidden" :class="compact ? 'w-12 h-12' : 'w-16 h-16'">
        <NuxtImg :src="img.r2Url" :alt="img.altText ?? ''" width="64" height="64" class="w-full h-full object-cover" />
      </div>

      <input
        :value="img.altText ?? ''"
        type="text"
        class="input py-1.5 text-sm flex-1"
        placeholder="Description (SEO)"
        @change="$emit('update-alt', img.id, ($event.target as HTMLInputElement).value)"
      />

      <div class="flex flex-col gap-1">
        <button
          class="p-1 text-gray-400 hover:text-gray-700 disabled:opacity-30"
          :disabled="idx === 0"
          @click="$emit('move', img.id, idx, -1)"
        >
          <Icon name="lucide:chevron-up" class="w-3.5 h-3.5" />
        </button>
        <button
          class="p-1 text-gray-400 hover:text-gray-700 disabled:opacity-30"
          :disabled="idx === images.length - 1"
          @click="$emit('move', img.id, idx, 1)"
        >
          <Icon name="lucide:chevron-down" class="w-3.5 h-3.5" />
        </button>
      </div>
      <button class="p-1.5 text-red-400 hover:text-red-600" @click="$emit('delete', img.id)">
        <Icon name="lucide:trash-2" class="w-3.5 h-3.5" />
      </button>
    </div>

    <!-- Zone d'upload -->
    <label
      class="flex items-center gap-2 border border-dashed border-gray-200 rounded-xl cursor-pointer
             hover:border-[--color-primary-light] transition-colors"
      :class="compact ? 'px-3 py-2' : 'flex-col py-6 justify-center'"
    >
      <Icon name="lucide:upload-cloud" :class="compact ? 'w-4 h-4 text-gray-400' : 'w-7 h-7 text-gray-400'" />
      <span class="text-xs text-gray-500">{{ images.length ? 'Ajouter des photos' : 'Uploader des photos' }}</span>
      <span v-if="!compact" class="text-xs text-gray-400">min. 1200×1200 px · max 5 Mo</span>
      <input type="file" accept="image/*" multiple class="hidden" :disabled="uploading" @change="$emit('upload', $event)" />
    </label>

    <div v-if="uploading" class="flex items-center gap-2 text-xs text-gray-400">
      <Icon name="lucide:loader-2" class="w-3 h-3 animate-spin" />
      Upload en cours…
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ProductImage } from '~/types'

defineProps<{
  images: ProductImage[]
  uploading: boolean
  compact?: boolean
}>()

defineEmits<{
  delete: [id: number]
  move: [id: number, idx: number, dir: number]
  'update-alt': [id: number, value: string]
  upload: [event: Event]
}>()
</script>
