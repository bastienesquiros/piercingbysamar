import { defineStore } from 'pinia'

export interface CartVariant {
  id: number
  name: string
  sku: string
  price: number // cents
}

export interface CartItem {
  variantId: number
  productId: number
  productName: string
  variantLabel: string
  sku: string
  price: number // cents
  quantity: number
  stock: number
  imageUrl: string | null
}

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: [] as CartItem[],
    open: false,
  }),

  getters: {
    count: (state) => state.items.reduce((sum, i) => sum + i.quantity, 0),

    total: (state) => state.items.reduce((sum, i) => sum + i.price * i.quantity, 0),

    isEmpty: (state) => state.items.length === 0,
  },

  actions: {
    add(item: CartItem) {
      const existing = this.items.find((i) => i.variantId === item.variantId)
      if (existing) {
        existing.quantity += item.quantity
      } else {
        this.items.push({ ...item })
      }
      this.open = true
    },

    remove(variantId: number) {
      this.items = this.items.filter((i) => i.variantId !== variantId)
    },

    updateQuantity(variantId: number, quantity: number) {
      const item = this.items.find((i) => i.variantId === variantId)
      if (item) {
        if (quantity <= 0) this.remove(variantId)
        else item.quantity = Math.min(quantity, item.stock)
      }
    },

    clear() {
      this.items = []
    },

    openCart() { this.open = true },
    closeCart() { this.open = false },
    toggleCart() { this.open = !this.open },
  },

  persist: true,
})
