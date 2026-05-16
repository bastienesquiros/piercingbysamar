<template>
  <span class="relative inline-flex" @mouseenter="show = true" @mouseleave="show = false">
    <span class="w-4 h-4 rounded-full bg-[--color-border] text-[--color-text-muted] text-[10px] font-bold flex items-center justify-center cursor-help select-none hover:bg-[--color-primary-light] hover:text-[--color-primary-dark] transition-colors">
      ?
    </span>
    <Transition name="tooltip">
      <div
        v-if="show"
        class="absolute bottom-full left-1/2 -translate-x-1/2 mb-2 z-50 pointer-events-none"
      >
        <div class="rounded-xl overflow-hidden shadow-xl border border-[--color-border] bg-white w-36">
          <NuxtImg
            :src="src"
            :alt="slug"
            width="144"
            height="144"
            class="w-36 h-36 object-cover"
          />
          <p class="text-[10px] text-center text-[--color-text-muted] py-1 px-2 font-medium capitalize">{{ slug }}</p>
        </div>
        <!-- Arrow -->
        <div class="absolute left-1/2 -translate-x-1/2 top-full w-0 h-0 border-l-4 border-r-4 border-t-4 border-l-transparent border-r-transparent border-t-[--color-border]" />
      </div>
    </Transition>
  </span>
</template>

<script setup lang="ts">
defineProps<{ slug: string; src: string }>()
const show = ref(false)
</script>

<style scoped>
.tooltip-enter-active, .tooltip-leave-active { transition: opacity 0.15s, transform 0.15s; }
.tooltip-enter-from, .tooltip-leave-to { opacity: 0; transform: translateX(-50%) translateY(4px); }
.tooltip-enter-to, .tooltip-leave-from { opacity: 1; transform: translateX(-50%) translateY(0); }
</style>
