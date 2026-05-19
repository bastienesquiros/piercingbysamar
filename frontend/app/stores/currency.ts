import { defineStore } from 'pinia'

type CurrencyCode = 'MAD' | 'EUR' | 'USD'

// Taux de secours (mis à jour depuis /api/exchange-rates au démarrage)
const DEFAULT_RATES: Record<CurrencyCode, number> = {
  MAD: 1,
  EUR: 0.0926,
  USD: 0.099,
}

const SYMBOLS: Record<CurrencyCode, string> = {
  MAD: 'DH',
  EUR: '€',
  USD: '$',
}

const LOCALES: Record<CurrencyCode, string> = {
  MAD: 'fr-MA',
  EUR: 'fr-FR',
  USD: 'en-US',
}

export const useCurrencyStore = defineStore('currency', {
  state: () => ({
    currency: 'MAD' as CurrencyCode,
    rates: { ...DEFAULT_RATES } as Record<CurrencyCode, number>,
  }),

  getters: {
    symbol: (state) => SYMBOLS[state.currency],
    rate: (state) => state.rates[state.currency],
    locale: (state) => LOCALES[state.currency],
    currencies: () => Object.keys(DEFAULT_RATES) as CurrencyCode[],
  },

  actions: {
    set(code: CurrencyCode) { this.currency = code },

    async fetchRates() {
      try {
        const data = await $fetch<{ rates: Record<string, number> }>(
          `/api/exchange-rates`
        )
        if (data?.rates?.EUR) this.rates.EUR = data.rates.EUR
        if (data?.rates?.USD) this.rates.USD = data.rates.USD
      } catch {
        // Silencieux : on garde les taux par défaut
      }
    },
  },

  persist: true,
})
