<template>
  <section class="py-16 bg-[--color-primary-light] border-y border-[--color-border]">
    <div class="container-site">
      <div class="flex flex-col md:flex-row items-center justify-between gap-8">

        <!-- Texte -->
        <div class="text-center md:text-left max-w-lg">
          <p class="text-xs uppercase tracking-[0.25em] text-[--color-primary-dark] font-semibold mb-3">
            {{ $t('rdv_banner.eyebrow') }}
          </p>
          <h2 class="font-serif text-3xl md:text-4xl font-semibold text-[--color-text] mb-3">
            {{ $t('rdv_banner.title') }}
          </h2>
          <p class="text-[--color-text-muted] text-base leading-relaxed">
            {{ $t('rdv_banner.subtitle') }}
          </p>
        </div>

        <!-- CTA -->
        <div class="flex flex-col items-center gap-3 shrink-0">
          <NuxtLink
            :to="localePath('/rdv')"
            class="btn-primary text-base px-10 py-4 flex items-center gap-2.5"
          >
            <Icon name="simple-icons:whatsapp" class="w-5 h-5" />
            {{ $t('rdv_banner.cta') }}
          </NuxtLink>
          <p class="text-xs text-[--color-text-muted]">{{ $t('rdv_banner.free') }}</p>
        </div>

      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
const localePath = useLocalePath()
</script>
