export default defineNuxtRouteMiddleware(() => {
  const auth = useAuthStore()
  const localePath = useLocalePath()
  if (!auth.isAuthenticated || !auth.isAdmin) {
    return navigateTo(localePath('/admin/login'))
  }
})
