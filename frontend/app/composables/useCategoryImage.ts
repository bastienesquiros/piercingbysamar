const CATEGORY_IMAGES: Record<string, string> = {
  lobe:   '/images/categories/lobe.avif',
  helix:  '/images/categories/helix.avif',
  conch:  '/images/categories/conch.avif',
  daith:  '/images/categories/daith.avif',
  tragus: '/images/categories/tragus.avif',
  indu:   '/images/categories/indu.avif',
  rock:   '/images/categories/rock.avif',
}

export function useCategoryImage() {
  function categoryImage(slug: string): string | null {
    return CATEGORY_IMAGES[slug] ?? null
  }
  return { categoryImage }
}
