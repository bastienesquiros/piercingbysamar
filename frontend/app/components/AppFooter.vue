<template>
  <footer class="bg-[--color-background-soft] border-t border-[--color-border] mt-16">
    <div class="container-site py-12">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">

        <!-- Brand -->
        <div>
          <p class="font-serif text-2xl font-semibold text-[--color-text] mb-3">
            Piercing <span class="text-primary">by Samar</span>
          </p>
          <p class="text-sm text-[--color-text-muted] mb-4 max-w-xs">
            {{ $t('home.hero_subtitle') }}
          </p>
          <!-- Social links -->
          <div class="flex items-center gap-3 flex-wrap">
            <a
              v-for="s in socials"
              :key="s.label"
              :href="s.href"
              target="_blank"
              rel="noopener"
              :aria-label="s.label"
              class="w-9 h-9 rounded-full border border-[--color-border] flex items-center justify-center
                     text-[--color-text-muted] hover:text-[--color-primary]
                     hover:border-[--color-primary] transition-colors"
            >
              <Icon :name="s.icon" class="w-4 h-4" />
            </a>
          </div>
        </div>

        <!-- Contact -->
        <div>
          <p class="text-xs uppercase tracking-widest font-semibold text-[--color-text-muted] mb-4">
            {{ $t('footer.contact') }}
          </p>
          <ul class="space-y-2">
            <li>
              <a
                href="https://wa.me/212781570083"
                target="_blank"
                rel="noopener"
                class="text-sm text-[--color-text-muted] hover:text-[--color-text] transition-colors flex items-center gap-2"
              >
                <Icon name="simple-icons:whatsapp" class="w-3.5 h-3.5 text-green-500" />
                +212 7 81 57 00 83
              </a>
            </li>
            <li>
              <a
                href="tel:+212781570083"
                class="text-sm text-[--color-text-muted] hover:text-[--color-text] transition-colors flex items-center gap-2"
              >
                <Icon name="lucide:phone" class="w-3.5 h-3.5" />
                +212 7 81 57 00 83
              </a>
            </li>
            <li>
              <a
                href="https://maps.app.goo.gl/b4qDcoBaafQaAqzMA"
                target="_blank"
                rel="noopener"
                class="text-sm text-[--color-text-muted] hover:text-[--color-text] transition-colors flex items-center gap-2"
              >
                <Icon name="lucide:map-pin" class="w-3.5 h-3.5" />
                {{ $t('footer.see_address') }} →
              </a>
            </li>
          </ul>
        </div>

        <!-- Legal -->
        <div>
          <p class="text-xs uppercase tracking-widest font-semibold text-[--color-text-muted] mb-4">
            {{ $t('footer.legal_section') }}
          </p>
          <ul class="space-y-2">
            <li>
              <NuxtLink :to="localePath('/faq')" class="text-sm text-[--color-text-muted] hover:text-[--color-text] transition-colors">
                FAQ
              </NuxtLink>
            </li>
            <li>
              <NuxtLink :to="localePath('/legal')" class="text-sm text-[--color-text-muted] hover:text-[--color-text] transition-colors">
                {{ $t('footer.legal') }}
              </NuxtLink>
            </li>
            <li>
              <NuxtLink :to="localePath('/cgv')" class="text-sm text-[--color-text-muted] hover:text-[--color-text] transition-colors">
                {{ $t('footer.cgv') }}
              </NuxtLink>
            </li>
            <li>
              <NuxtLink :to="localePath('/confidentialite')" class="text-sm text-[--color-text-muted] hover:text-[--color-text] transition-colors">
                {{ $t('footer.privacy') }}
              </NuxtLink>
            </li>
          </ul>
        </div>
      </div>

      <!-- Bottom bar -->
      <div class="mt-10 pt-6 border-t border-[--color-border] flex flex-col md:flex-row items-center justify-between gap-3">
        <p class="text-xs text-[--color-text-muted]">
          © {{ new Date().getFullYear() }} Piercing by Samar. {{ $t('footer.rights') }}
        </p>
        <div v-if="stripeEnabled" class="flex items-center gap-2">
          <Icon name="simple-icons:stripe" class="w-8 h-5 text-[--color-text-muted]" />
          <span class="text-xs text-[--color-text-muted]">{{ $t('footer.secure_payment') }}</span>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
const { t } = useI18n()
const localePath = useLocalePath()
const { public: { stripeEnabled } } = useRuntimeConfig()

const socials = [
  { label: 'Instagram',  href: 'https://www.instagram.com/piercing_bysamar',      icon: 'lucide:instagram' },
  { label: 'TikTok',     href: 'https://www.tiktok.com/@piercingbysmar',           icon: 'simple-icons:tiktok' },
  { label: 'Snapchat',   href: 'https://www.snapchat.com/add/piercingbysamar',     icon: 'simple-icons:snapchat' },
  { label: 'WhatsApp',   href: 'https://wa.me/212781570083',                        icon: 'simple-icons:whatsapp' },
]
</script>

