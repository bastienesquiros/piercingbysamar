import { describe, it, expect, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useCurrencyStore } from '../stores/currency'

describe('useCurrencyStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('defaults to MAD', () => {
    const store = useCurrencyStore()
    expect(store.currency).toBe('MAD')
    expect(store.rate).toBe(1)
    expect(store.symbol).toBe('DH')
  })

  it('switches to EUR', () => {
    const store = useCurrencyStore()
    store.set('EUR')
    expect(store.currency).toBe('EUR')
    expect(store.symbol).toBe('€')
    expect(store.rate).toBeCloseTo(0.0926, 3)
  })

  it('switches to USD', () => {
    const store = useCurrencyStore()
    store.set('USD')
    expect(store.currency).toBe('USD')
    expect(store.symbol).toBe('$')
    expect(store.rate).toBeCloseTo(0.099, 3)
  })

  it('updates rates from API response', () => {
    const store = useCurrencyStore()
    // Simulate fetchRates having updated the rates
    store.rates.EUR = 0.092
    store.rates.USD = 0.10
    store.set('EUR')
    expect(store.rate).toBeCloseTo(0.092, 3)
  })
})

describe('checkout currency logic', () => {
  // Reproduit la logique de checkoutCurrency du checkout/index.vue
  function checkoutCurrency(orderType: 'SHIPPING' | 'CLICK_COLLECT', displayCurrency: string): string {
    if (orderType === 'CLICK_COLLECT') return 'MAD'
    return displayCurrency === 'MAD' ? 'EUR' : displayCurrency
  }

  it('C&C is always MAD regardless of display currency', () => {
    expect(checkoutCurrency('CLICK_COLLECT', 'MAD')).toBe('MAD')
    expect(checkoutCurrency('CLICK_COLLECT', 'EUR')).toBe('MAD')
    expect(checkoutCurrency('CLICK_COLLECT', 'USD')).toBe('MAD')
  })

  it('Shipping in MAD falls back to EUR', () => {
    expect(checkoutCurrency('SHIPPING', 'MAD')).toBe('EUR')
  })

  it('Shipping in EUR stays EUR', () => {
    expect(checkoutCurrency('SHIPPING', 'EUR')).toBe('EUR')
  })

  it('Shipping in USD stays USD', () => {
    expect(checkoutCurrency('SHIPPING', 'USD')).toBe('USD')
  })
})
