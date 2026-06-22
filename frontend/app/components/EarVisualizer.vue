<template>
  <section class="py-16 bg-[--color-background-warm]">
    <div class="container-site">
      <h2 class="font-serif text-3xl md:text-4xl font-semibold text-center text-[--color-text] mb-2">
        Où se place mon piercing ?
      </h2>
      <p class="text-center text-[--color-text-muted] mb-12 max-w-sm mx-auto text-sm">
        Sélectionne une zone pour découvrir le nom et les bijoux correspondants.
      </p>

      <div class="flex flex-col md:flex-row md:items-start items-center justify-center gap-10 md:gap-16">

        <!-- Ear SVG -->
        <div class="shrink-0 w-[220px] md:mt-4">
          <svg viewBox="0 0 240 360" xmlns="http://www.w3.org/2000/svg" class="w-full drop-shadow-sm">
            <!-- Outer ear -->
            <path
              d="M 95,28
                 C 120,8 185,20 215,65
                 C 238,100 240,155 228,205
                 C 218,245 198,268 182,282
                 C 170,295 162,308 155,328
                 C 148,346 136,358 120,358
                 C 103,358 90,345 84,328
                 C 78,310 82,292 80,278
                 C 65,265 45,245 40,218
                 C 35,192 44,165 56,152
                 C 46,138 42,118 48,96
                 C 56,68 75,42 95,28 Z"
              fill="#F5DEC8" stroke="#D4A882" stroke-width="1.5"
            />
            <!-- Inner helix ridge -->
            <path
              d="M 102,50 C 130,34 178,50 200,88 C 218,120 218,162 205,198 C 195,228 180,248 165,262"
              stroke="#C49070" stroke-width="5" stroke-linecap="round" fill="none" opacity="0.4"
            />
            <!-- Anti-helix body -->
            <path
              d="M 118,96 C 138,80 162,92 170,118 C 176,140 170,165 162,182 C 155,198 148,212 142,226"
              stroke="#C49070" stroke-width="4.5" stroke-linecap="round" fill="none" opacity="0.35"
            />
            <!-- Anti-helix superior crus -->
            <path
              d="M 162,118 C 175,100 185,84 192,72"
              stroke="#C49070" stroke-width="3.5" stroke-linecap="round" fill="none" opacity="0.3"
            />
            <!-- Concha bowl -->
            <ellipse cx="118" cy="198" rx="26" ry="30" fill="#EBC9A8" stroke="#C49070" stroke-width="1" opacity="0.55" />
            <!-- Ear canal -->
            <ellipse cx="104" cy="198" rx="10" ry="14" fill="#C49862" opacity="0.4" />
            <!-- Tragus -->
            <path
              d="M 58,155 C 46,163 44,178 46,193 C 48,207 57,212 64,208 C 68,200 68,174 63,164 Z"
              fill="#EBC9A8" stroke="#C49070" stroke-width="1"
            />
            <!-- Anti-tragus (subtle) -->
            <path
              d="M 70,260 C 62,268 62,278 70,282 C 76,280 80,272 78,265 Z"
              fill="#EBC9A8" stroke="#C49070" stroke-width="1" opacity="0.6"
            />

            <!-- Piercing dots -->
            <g
              v-for="zone in zones"
              :key="zone.slug"
              class="cursor-pointer"
              @click="toggle(zone.slug)"
            >
              <!-- Halo quand actif -->
              <circle
                v-if="active === zone.slug"
                :cx="zone.cx" :cy="zone.cy" r="21"
                fill="var(--color-primary)" opacity="0.15"
              />
              <circle
                :cx="zone.cx" :cy="zone.cy" r="13"
                :fill="active === zone.slug ? 'var(--color-primary)' : 'white'"
                :stroke="active === zone.slug ? 'var(--color-primary)' : '#D4A882'"
                stroke-width="1.8"
                style="transition: fill 0.2s, stroke 0.2s"
              />
              <text
                :x="zone.cx" :y="zone.cy"
                text-anchor="middle"
                dominant-baseline="central"
                :fill="active === zone.slug ? 'white' : '#9A7055'"
                font-size="6.5"
                font-weight="700"
                font-family="system-ui, sans-serif"
                class="pointer-events-none select-none"
              >{{ zone.initials }}</text>
            </g>
          </svg>
        </div>

        <!-- Info card -->
        <div class="w-full max-w-[280px] relative" style="min-height: 420px;">
          <Transition name="card" mode="out-in">
            <div
              v-if="activeZone"
              :key="activeZone.slug"
              class="absolute inset-x-0 top-0 bg-white rounded-2xl border border-[--color-border] overflow-hidden shadow-sm"
            >
              <div class="aspect-square bg-[--color-background-soft] relative overflow-hidden">
                <img
                  v-if="activeZone.imageUrl"
                  :src="activeZone.imageUrl"
                  :alt="activeZone.label"
                  class="w-full h-full object-cover"
                />
                <div v-else class="w-full h-full flex items-center justify-center">
                  <Icon name="lucide:image" class="w-14 h-14 text-[--color-border]" />
                </div>
              </div>
              <div class="p-5">
                <p class="text-[10px] text-[--color-text-muted] uppercase tracking-widest mb-1">Piercing</p>
                <h3 class="font-serif text-2xl font-semibold text-[--color-text] mb-1">
                  {{ activeZone.label }}
                </h3>
                <p class="text-sm text-[--color-text-muted] mb-5">{{ activeZone.placement }}</p>
                <NuxtLink
                  :to="localePath(`/catalogue?category=${activeZone.slug}`)"
                  class="btn-primary block w-full text-center py-2.5 text-sm"
                >
                  Voir les bijoux →
                </NuxtLink>
              </div>
            </div>

            <div
              v-else
              key="placeholder"
              class="absolute inset-0 flex flex-col items-center justify-center gap-3 text-center"
            >
              <div class="w-14 h-14 rounded-full bg-[--color-background-soft] flex items-center justify-center">
                <Icon name="lucide:mouse-pointer-click" class="w-6 h-6 text-[--color-border]" />
              </div>
              <p class="text-sm text-[--color-text-muted] max-w-[160px]">
                Sélectionne une zone sur l'oreille
              </p>
            </div>
          </Transition>
        </div>

      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
const localePath = useLocalePath()
const { categoryImage } = useCategoryImage()

const ZONES = [
  { slug: 'lobe',   label: 'Lobe',       placement: 'Partie basse souple de l\'oreille',    initials: 'LB', cx: 118, cy: 322 },
  { slug: 'helix',  label: 'Hélix',      placement: 'Rebord cartilagineux extérieur',        initials: 'HX', cx: 220, cy: 88  },
  { slug: 'conch',  label: 'Conch',      placement: 'Coquille centrale de l\'oreille',       initials: 'CO', cx: 134, cy: 212 },
  { slug: 'daith',  label: 'Daith',      placement: 'Pli interne de la conque',              initials: 'DA', cx: 90,  cy: 155 },
  { slug: 'tragus', label: 'Tragus',     placement: 'Petite saillie devant le conduit',      initials: 'TR', cx: 50,  cy: 183 },
  { slug: 'indu',   label: 'Industriel', placement: 'Barre traversant deux points cartilage', initials: 'IN', cx: 172, cy: 46  },
  { slug: 'rock',   label: 'Rock',       placement: 'Crête supérieure du cartilage',         initials: 'RK', cx: 168, cy: 112 },
]

const active = ref<string | null>(null)

const zones = computed(() =>
  ZONES.map(z => ({ ...z, imageUrl: categoryImage(z.slug) }))
)

const activeZone = computed(() =>
  zones.value.find(z => z.slug === active.value) ?? null
)

function toggle(slug: string) {
  active.value = active.value === slug ? null : slug
}
</script>

<style scoped>
.card-enter-active,
.card-leave-active {
  transition: opacity 0.18s ease, transform 0.18s ease;
}
.card-enter-from,
.card-leave-to {
  opacity: 0;
  transform: translateY(8px);
}
</style>
