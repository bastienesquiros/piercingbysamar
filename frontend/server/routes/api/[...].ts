import { proxyRequest } from 'h3'

// Proxy all /api/** requests to the backend (reads NUXT_API_BASE at runtime)
export default defineEventHandler(async (event) => {
  const config = useRuntimeConfig()
  const target = `${config.apiBase}${event.path}`
  return proxyRequest(event, target)
})
