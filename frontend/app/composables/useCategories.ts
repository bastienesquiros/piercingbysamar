import type { Category } from '~/types'

export function useCategories() {
  const { get } = useApi()
  const categories = useState<Category[]>('categories', () => [])

  async function fetchCategories() {
    if (categories.value.length) return
    const data = await get<Category[]>('/api/categories')
    categories.value = data ?? []
  }

  const topLevel = computed(() =>
    categories.value.filter((c) => !c.parentId)
  )

  return { categories, topLevel, fetchCategories }
}
