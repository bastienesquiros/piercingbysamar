import { defineStore } from 'pinia'

interface AuthUser {
  email: string
  role: string
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: null as string | null,
    user: null as AuthUser | null,
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    isAdmin: (state) => state.user?.role === 'ADMIN',
    authHeader: (state) => (state.token ? { Authorization: `Bearer ${state.token}` } : {}),
  },

  actions: {
    setAuth(token: string, email: string, role: string) {
      this.token = token
      this.user = { email, role }
    },
    logout() {
      this.token = null
      this.user = null
    },
  },

  persist: true,
})
