/**
 * Composable central pour le formatage des prix.
 * La devise de base est le MAD (dirham marocain).
 * Les prix sont stockés en centimes MAD dans la base de données.
 */
const LOCALES: Record<string, string> = {
  MAD: 'fr-MA',
  EUR: 'fr-FR',
  USD: 'en-US',
}

export function usePrice() {
  const currency = useCurrencyStore()

  function format(madCentimes: number): string {
    if (madCentimes == null) return ''
    const amount = (madCentimes / 100) * currency.rate

    return new Intl.NumberFormat(currency.locale, {
      style: 'currency',
      currency: currency.currency,
      minimumFractionDigits: currency.currency === 'MAD' ? 0 : 2,
      maximumFractionDigits: currency.currency === 'MAD' ? 0 : 2,
    }).format(amount)
  }

  /** Formate des centimes MAD dans une devise cible spécifique (indépendant de la devise affichée). */
  function formatAs(madCentimes: number, targetCurrency: string): string {
    if (madCentimes == null) return ''
    const rate = currency.rates[targetCurrency as 'MAD' | 'EUR' | 'USD'] ?? 1
    const locale = LOCALES[targetCurrency] ?? 'fr-FR'
    return new Intl.NumberFormat(locale, {
      style: 'currency',
      currency: targetCurrency,
      minimumFractionDigits: targetCurrency === 'MAD' ? 0 : 2,
      maximumFractionDigits: targetCurrency === 'MAD' ? 0 : 2,
    }).format((madCentimes / 100) * rate)
  }

  function formatRange(minCents: number, maxCents: number): string {
    if (minCents === maxCents) return format(minCents)
    return `${format(minCents)} – ${format(maxCents)}`
  }

  return { format, formatAs, formatRange, currency }
}
