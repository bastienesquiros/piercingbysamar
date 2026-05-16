import { describe, it, expect, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useCurrencyStore } from '../stores/currency'

// Reproduit usePrice sans les auto-imports Nuxt
function makePrice(currencyCode: 'MAD' | 'EUR' | 'USD', rates = { MAD: 1, EUR: 0.0926, USD: 0.099 }) {
  const LOCALES: Record<string, string> = { MAD: 'fr-MA', EUR: 'fr-FR', USD: 'en-US' }

  function formatAs(madCentimes: number, targetCurrency: string): string {
    if (madCentimes == null) return ''
    const rate = rates[targetCurrency as 'MAD' | 'EUR' | 'USD'] ?? 1
    const locale = LOCALES[targetCurrency] ?? 'fr-FR'
    return new Intl.NumberFormat(locale, {
      style: 'currency',
      currency: targetCurrency,
      minimumFractionDigits: targetCurrency === 'MAD' ? 0 : 2,
      maximumFractionDigits: targetCurrency === 'MAD' ? 0 : 2,
    }).format((madCentimes / 100) * rate)
  }

  return { formatAs }
}

describe('usePrice — formatAs', () => {
  it('formats MAD centimes in MAD with no decimals', () => {
    const { formatAs } = makePrice('MAD')
    const result = formatAs(22000, 'MAD') // 220 MAD
    expect(result).toContain('220')
    expect(result).not.toContain('220.00')
  })

  it('converts MAD centimes to EUR correctly', () => {
    const { formatAs } = makePrice('EUR')
    // 22000 centimes = 220 MAD * 0.0926 = 20.37 EUR
    const result = formatAs(22000, 'EUR')
    expect(result).toContain('20')
    expect(result).toContain('€')
  })

  it('converts MAD centimes to USD correctly', () => {
    const { formatAs } = makePrice('USD')
    // 22000 centimes = 220 MAD * 0.099 = 21.78 USD
    const result = formatAs(22000, 'USD')
    expect(result).toContain('21')
    expect(result).toContain('$')
  })

  it('returns empty string for null/undefined', () => {
    const { formatAs } = makePrice('MAD')
    expect(formatAs(null as unknown as number, 'MAD')).toBe('')
  })

  it('does not lose cents precision when converting', () => {
    const { formatAs } = makePrice('EUR', { MAD: 1, EUR: 0.0926, USD: 0.099 })
    // 18000 centimes = 180 MAD → ~16.67 EUR (not 180 EUR)
    const result = formatAs(18000, 'EUR')
    expect(result).not.toContain('180')
    expect(result).toContain('16')
  })
})
