export type ToastType = 'success' | 'error' | 'info'

interface Toast {
  id: number
  message: string
  type: ToastType
}

const toasts = ref<Toast[]>([])
let counter = 0

export function useToast() {
  function add(message: string, type: ToastType = 'info', duration = 3500) {
    const id = ++counter
    toasts.value.push({ id, message, type })
    setTimeout(() => remove(id), duration)
  }

  function remove(id: number) {
    toasts.value = toasts.value.filter(t => t.id !== id)
  }

  return {
    toasts: readonly(toasts),
    success: (msg: string) => add(msg, 'success'),
    error: (msg: string) => add(msg, 'error', 5000),
    info: (msg: string) => add(msg, 'info'),
    remove,
  }
}
