import type { FetchOptions } from 'ofetch'

export function useApi() {
  const config = useRuntimeConfig()
  // Server-side: use internal Docker URL if set, fallback to public URL
  const baseURL = (import.meta.server && config.apiBase ? config.apiBase : config.public.apiBase) as string

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
