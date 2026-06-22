import { useCategories } from './useCategories'

export function useCategoryImage() {
  const { categories } = useCategories()

  function categoryImage(slug: string): string | null {
    const flat: import('~/types').Category[] = []
    const traverse = (cats: import('~/types').Category[]) => {
      for (const c of cats) {
        flat.push(c)
        if (c.children?.length) traverse(c.children)
      }
    }
    traverse(categories.value)
    return flat.find(c => c.slug === slug)?.imageUrl ?? null
  }

  return { categoryImage }
}
