// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },

  modules: [
    '@nuxt/eslint',
    '@nuxt/icon',
    '@nuxt/fonts',
    '@nuxt/image',
    '@pinia/nuxt',
    '@nuxtjs/tailwindcss',
    '@nuxtjs/i18n',
    '@nuxtjs/seo',
    '@vueuse/nuxt',
  ],

  css: ['~/assets/css/main.css'],

  fonts: {
    families: [
      { name: 'Cormorant Garamond', weights: [400, 500, 600, 700], styles: ['normal', 'italic'] },
      { name: 'Inter', weights: [300, 400, 500, 600, 700] },
    ]
  },

  image: {
    quality: 85,
    formats: ['avif', 'webp'],
  },

  i18n: {
    locales: [
      { code: 'fr', name: 'Français', file: 'fr.json' },
      { code: 'en', name: 'English', file: 'en.json' },
    ],
    defaultLocale: 'fr',
    langDir: 'locales/',
    strategy: 'prefix_except_default',
  },

  runtimeConfig: {
    public: {
      apiBase: process.env.NUXT_PUBLIC_API_BASE ?? 'http://localhost:8080',
      stripePublicKey: process.env.NUXT_PUBLIC_STRIPE_KEY ?? '',
    }
  },
})
