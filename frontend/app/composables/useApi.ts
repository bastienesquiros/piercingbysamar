import type { FetchOptions } from 'ofetch'

export function useApi() {
  const config = useRuntimeConfig()
  // Server-side: call backend directly via internal Docker URL
  // Client-side: use relative URL → proxied through Nuxt server (server/routes/api/[...].ts)
  const baseURL = (import.meta.server && config.apiBase ? config.apiBase : '') as string

  function get<T>(path: string, opts?: FetchOptions) {
    return $fetch<T>(path, { baseURL, method: 'GET', ...opts })
  }

  function post<T>(path: string, body?: unknown, opts?: FetchOptions) {
    return $fetch<T>(path, { baseURL, method: 'POST', body, ...opts })
  }

  function put<T>(path: string, body?: unknown, opts?: FetchOptions) {
    return $fetch<T>(path, { baseURL, method: 'PUT', body, ...opts })
  }

  function del<T>(path: string, opts?: FetchOptions) {
    return $fetch<T>(path, { baseURL, method: 'DELETE', ...opts })
  }

  function patch<T>(path: string, body?: unknown, opts?: FetchOptions) {
    return $fetch<T>(path, { baseURL, method: 'PATCH', body, ...opts })
  }

  return { get, post, put, patch, del }
}
