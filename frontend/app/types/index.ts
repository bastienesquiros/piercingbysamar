// ── API types matching Spring Boot DTOs ─────────────────────────

export interface Category {
  id: number
  parentId: number | null
  name: string
  slug: string
  description: string | null
  imageUrl: string | null
  children: Category[]
}

export interface ProductSummary {
  id: number
  name: string
  slug: string
  material: string
  nickelFree: boolean
  categoryId: number
  categoryName: string
  minPriceCents: number
  maxPriceCents: number
  inStock: boolean
  coverImageUrl: string | null
  tags: string[]
}

export interface ProductVariant {
  id: number
  sku: string
  size: string | null
  color: string | null
  priceCents: number
  stock: number
  reservedStock: number
  availableStock: number
  inStock: boolean
  active: boolean
}

export interface ProductImage {
  id: number
  r2Url: string
  position: number
  altText: string | null
  variantId: number | null
}

export interface ProductDetail {
  id: number
  name: string
  slug: string
  description: string | null
  material: string
  nickelFree: boolean
  categoryId: number
  categoryName: string
  metaTitle: string | null
  metaDescription: string | null
  tags: string[]
  active: boolean
  variants: ProductVariant[]
  images: ProductImage[]
  createdAt: string
  updatedAt: string
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  last: boolean
}

export interface OrderItem {
  id: number
  productVariantId: number
  snapshotProductName: string
  snapshotVariantLabel: string
  unitPriceCents: number
  quantity: number
  totalCents: number
}

export interface Order {
  id: number
  reference: string
  orderType: 'SHIPPING' | 'CLICK_COLLECT'
  status: string
  customerEmail: string
  customerName: string
  customerPhone: string | null
  shippingAddress: string | null
  shippingCity: string | null
  shippingPostalCode: string | null
  shippingCountry: string | null
  subtotalCents: number
  shippingCostCents: number
  totalCents: number
  currency: string
  notes: string | null
  items: OrderItem[]
  createdAt: string
  updatedAt: string
}
