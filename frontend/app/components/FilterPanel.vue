<template>
  <div class="space-y-6">

    <!-- Catégorie -->
    <div>
      <p class="text-xs font-semibold uppercase tracking-widest text-[--color-text-muted] mb-2">
        {{ $t('catalogue.category') }}
      </p>

      <ul class="space-y-0.5">
        <!-- Tous -->
        <li>
          <button
            class="w-full text-left text-sm px-3 py-1.5 rounded-lg transition-colors"
            :class="!filters.categorySlug
              ? 'bg-[--color-primary-light] text-[--color-primary-dark] font-semibold'
              : 'text-[--color-text-muted] hover:text-[--color-text] hover:bg-[--color-background-warm]'"
            @click="$emit('setCategory', null)"
          >
            {{ $t('catalogue.all') }}
          </button>
        </li>

        <!-- Parent + sous-catégories indentées -->
        <li v-for="parent in topLevelCategories" :key="parent.id">
          <button
            class="w-full text-left text-sm px-3 py-1.5 rounded-lg transition-colors font-medium flex items-center gap-1.5"
            :class="filters.categorySlug === parent.slug
              ? 'bg-[--color-primary-light] text-[--color-primary-dark] font-semibold'
              : 'text-[--color-text] hover:bg-[--color-background-warm]'"
            @click="$emit('setCategory', parent.slug)"
          >
            <span class="flex-1 text-left">{{ $t('categories.' + parent.slug, parent.name) }}</span>
            <CategoryTooltip v-if="categoryImage(parent.slug)" :slug="parent.slug" :src="categoryImage(parent.slug)!" />
          </button>

          <ul v-if="parent.children?.length" class="mt-0.5 space-y-0.5 border-l-2 border-[--color-border] ml-4 pl-2">
            <li v-for="child in parent.children" :key="child.id">
              <button
                class="w-full text-left text-sm px-2 py-1 rounded-lg transition-colors flex items-center gap-1.5"
                :class="filters.categorySlug === child.slug
                  ? 'bg-[--color-primary-light] text-[--color-primary-dark] font-semibold'
                  : 'text-[--color-text-muted] hover:text-[--color-text] hover:bg-[--color-background-warm]'"
                @click="$emit('setCategory', child.slug)"
              >
                <span class="flex-1 text-left">{{ $t('categories.' + child.slug, child.name) }}</span>
                <CategoryTooltip v-if="categoryImage(child.slug)" :slug="child.slug" :src="categoryImage(child.slug)!" />
              </button>
            </li>
          </ul>
        </li>
      </ul>
    </div>

    <!-- Matériau -->
    <div>
      <p class="text-xs font-semibold uppercase tracking-widest text-[--color-text-muted] mb-2">
        {{ $t('catalogue.material') }}
      </p>
      <ul class="space-y-0.5">
        <li v-for="mat in materials" :key="mat.value">
          <button
            class="w-full text-left text-sm px-3 py-1.5 rounded-lg transition-colors"
            :class="filters.material === mat.value
              ? 'bg-[--color-primary-light] text-[--color-primary-dark] font-semibold'
              : 'text-[--color-text-muted] hover:text-[--color-text] hover:bg-[--color-background-warm]'"
            @click="$emit('setMaterial', mat.value)"
          >
            {{ mat.label }}
          </button>
        </li>
      </ul>
    </div>

    <!-- Nickel Free — tout le label est cliquable -->
    <label class="flex items-center gap-3 cursor-pointer group" @click="$emit('toggleNickelFree')">
      <div
        class="w-5 h-5 rounded border-2 flex items-center justify-center transition-colors shrink-0"
        :class="filters.nickelFree
          ? 'bg-[--color-primary] border-[--color-primary]'
          : 'border-[--color-border] group-hover:border-[--color-primary]'"
      >
        <Icon v-if="filters.nickelFree" name="lucide:check" class="w-3 h-3 text-white" />
      </div>
      <span class="text-sm text-[--color-text] select-none">{{ $t('catalogue.nickel_free_only') }}</span>
    </label>

    <!-- Reset — toujours présent pour éviter le jump de layout -->
    <button
      class="btn-ghost w-full text-sm text-red-400 hover:text-red-600 hover:bg-red-50 transition-opacity duration-150"
      :class="hasFilters ? 'opacity-100' : 'opacity-0 pointer-events-none'"
      @click="$emit('reset')"
    >
      <Icon name="lucide:x" class="w-4 h-4" />
      {{ $t('catalogue.reset') }}
    </button>
  </div>
</template>

<script setup lang="ts">
import type { Category } from '~/types'

const props = defineProps<{
  topLevelCategories: Category[]
  materials: { label: string; value: string }[]
  filters: {
    categorySlug: string | null
    material: string | null
    nickelFree: boolean
  }
}>()

defineEmits<{
  setCategory: [slug: string | null]
  setMaterial: [mat: string]
  toggleNickelFree: []
  reset: []
}>()

const { categoryImage } = useCategoryImage()

const hasFilters = computed(
  () => !!props.filters.categorySlug || !!props.filters.material || props.filters.nickelFree
)
</script>
