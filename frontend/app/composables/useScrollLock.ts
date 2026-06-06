export function useScrollLock(source: MaybeRefOrGetter<boolean>) {
  let scrollY = 0

  watchEffect(() => {
    if (!import.meta.client) return
    const locked = toValue(source)
    if (locked) {
      scrollY = window.scrollY
      document.body.style.position = 'fixed'
      document.body.style.top = `-${scrollY}px`
      document.body.style.width = '100%'
      document.body.style.overflowY = 'scroll'
    } else {
      document.body.style.position = ''
      document.body.style.top = ''
      document.body.style.width = ''
      document.body.style.overflowY = ''
      window.scrollTo(0, scrollY)
    }
  })

  onUnmounted(() => {
    if (!import.meta.client) return
    document.body.style.position = ''
    document.body.style.top = ''
    document.body.style.width = ''
    document.body.style.overflowY = ''
    window.scrollTo(0, scrollY)
  })
}
