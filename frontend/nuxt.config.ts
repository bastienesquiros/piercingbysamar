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
    'pinia-plugin-persistedstate/nuxt',
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
    domains: [
      'localhost',
      // Ajouter en prod : 'eu2.contabostorage.com', ton-compte.r2.cloudflarestorage.com, etc.
    ],
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
    apiBase: process.env.NUXT_API_BASE ?? '',  // server-side only (internal Docker URL)
    public: {
      apiBase: process.env.NUXT_PUBLIC_API_BASE ?? 'http://localhost:8080',
      stripePublicKey: process.env.NUXT_PUBLIC_STRIPE_KEY ?? '',
    }
  },

  site: {
    name: 'Piercing by Samar',
    url: process.env.NUXT_PUBLIC_SITE_URL ?? 'https://piercingbysamar.com',
    description: 'Bijoux de piercing en titane ASTM F136 à Marrakech — vente en ligne et Click & Collect.',
    defaultLocale: 'fr',
  },
})
