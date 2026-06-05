import type { Category } from '~/types'

export function useCategories() {
  const { get } = useApi()

  const { data, refresh } = useAsyncData(
    'categories',
    () => get<Category[]>('/api/categories'),
    { default: () => [] as Category[], dedupe: 'defer' }
  )

  const categories = computed(() => data.value ?? [])

  const topLevel = computed(() =>
    categories.value.filter((c) => !c.parentId)
  )

  return { categories, topLevel, refresh }
}
