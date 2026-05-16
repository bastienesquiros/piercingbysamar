/**
 * Composable central pour le formatage des prix.
 * La devise de base est le MAD (dirham marocain).
 * Les prix sont stockés en centimes MAD dans la base de données.
 */
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

  function formatRange(minCents: number, maxCents: number): string {
    if (minCents === maxCents) return format(minCents)
    return `${format(minCents)} – ${format(maxCents)}`
  }

  return { format, formatRange, currency }
}
