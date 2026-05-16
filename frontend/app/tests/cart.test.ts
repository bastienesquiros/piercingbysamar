import { describe, it, expect, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useCartStore } from '../stores/cart'

describe('useCartStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  const item = () => ({
    variantId: 1,
    productId: 10,
    productName: 'Anneau Titanium',
    variantLabel: '8mm',
    sku: 'TI-8MM',
    price: 2200, // 22 MAD
    quantity: 1,
    stock: 5,
    imageUrl: null,
  })

  it('adds an item to the cart', () => {
    const cart = useCartStore()
    cart.add(item())
    expect(cart.items).toHaveLength(1)
    expect(cart.count).toBe(1)
  })

  it('increments quantity when same variant added twice', () => {
    const cart = useCartStore()
    cart.add(item())
    cart.add(item())
    expect(cart.items).toHaveLength(1)
    expect(cart.items[0].quantity).toBe(2)
  })

  it('removes an item', () => {
    const cart = useCartStore()
    cart.add(item())
    cart.remove(1)
    expect(cart.isEmpty).toBe(true)
  })

  it('calculates total correctly', () => {
    const cart = useCartStore()
    cart.add(item())
    cart.add({ ...item(), variantId: 2, price: 3000, quantity: 2 })
    // 2200*1 + 3000*2 = 8200
    expect(cart.total).toBe(8200)
  })

  it('caps quantity at stock limit', () => {
    const cart = useCartStore()
    cart.add(item()) // stock = 5
    cart.updateQuantity(1, 10) // try to set 10
    expect(cart.items[0].quantity).toBe(5)
  })

  it('removes item when quantity set to 0', () => {
    const cart = useCartStore()
    cart.add(item())
    cart.updateQuantity(1, 0)
    expect(cart.isEmpty).toBe(true)
  })

  it('clears the cart', () => {
    const cart = useCartStore()
    cart.add(item())
    cart.add({ ...item(), variantId: 2 })
    cart.clear()
    expect(cart.isEmpty).toBe(true)
    expect(cart.count).toBe(0)
  })

  it('opens and closes the cart drawer', () => {
    const cart = useCartStore()
    expect(cart.open).toBe(false)
    cart.add(item()) // add() opens cart
    expect(cart.open).toBe(true)
    cart.closeCart()
    expect(cart.open).toBe(false)
    cart.toggleCart()
    expect(cart.open).toBe(true)
  })
})
