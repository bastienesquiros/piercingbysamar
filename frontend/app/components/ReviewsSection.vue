<template>
  <section class="py-16 bg-[--color-background-soft] overflow-hidden">
    <div class="container-site">

      <!-- Header -->
      <div class="flex flex-col sm:flex-row sm:items-end justify-between gap-4 mb-10">
        <div>
          <div class="flex items-center gap-2 mb-2">
            <span class="text-amber-400 text-xl leading-none">★★★★★</span>
            <span class="font-semibold text-[--color-text]">5.0</span>
            <span class="text-sm text-[--color-text-muted]">sur Google</span>
          </div>
          <h2 class="font-serif text-3xl md:text-4xl font-semibold text-[--color-text]">
            Ce qu'on dit de nous
          </h2>
        </div>
        <a
          href="https://maps.app.goo.gl/b4qDcoBaafQaAqzMA"
          target="_blank"
          rel="noopener noreferrer"
          class="btn-outline text-sm py-2 px-4 shrink-0 inline-flex items-center gap-2"
        >
          <svg class="w-4 h-4" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
          </svg>
          Voir tous les avis
        </a>
      </div>

      <!-- Carousel -->
      <div class="relative">
        <!-- Flèche gauche -->
        <button
          v-if="canScrollLeft"
          class="hidden md:flex absolute left-0 top-1/2 -translate-y-1/2 -translate-x-4 z-10 w-10 h-10 rounded-full bg-white border border-[--color-border] shadow-md items-center justify-center hover:bg-[--color-background-soft] transition-colors"
          @click="scroll(-1)"
        >
          <Icon name="lucide:chevron-left" class="w-5 h-5 text-[--color-text]" />
        </button>

        <div
          ref="track"
          class="flex gap-4 overflow-x-auto scrollbar-hide pb-2 -mx-4 px-4 scroll-smooth"
          style="scroll-snap-type: x mandatory;"
          @scroll="onScroll"
        >
        <div
          v-for="review in REVIEWS"
          :key="review.name"
          class="bg-white rounded-2xl border border-[--color-border] p-6 flex flex-col gap-3 shrink-0 w-72"
          style="scroll-snap-align: start;"
        >
          <!-- Stars -->
          <span class="text-amber-400 text-base leading-none tracking-wide">★★★★★</span>

          <!-- Text -->
          <p class="text-sm text-[--color-text] leading-relaxed flex-1">
            "{{ review.text }}"
          </p>

          <!-- Author -->
          <div class="flex items-center gap-2.5 pt-1 border-t border-[--color-border]">
            <div class="w-8 h-8 rounded-full bg-[--color-primary-light] flex items-center justify-center shrink-0">
              <span class="text-[--color-primary-dark] text-xs font-semibold">{{ review.name[0].toUpperCase() }}</span>
            </div>
            <div>
              <p class="text-xs font-semibold text-[--color-text]">{{ review.name }}</p>
              <p class="text-[10px] text-[--color-text-muted]">{{ review.when }}</p>
            </div>
          </div>
        </div>

        <!-- CTA card -->
        <a
          href="https://maps.app.goo.gl/b4qDcoBaafQaAqzMA"
          target="_blank"
          rel="noopener noreferrer"
          class="bg-[--color-primary-light] rounded-2xl border border-[--color-border] p-6 flex flex-col items-center justify-center gap-4 shrink-0 w-56 group hover:bg-[--color-primary] transition-colors duration-200"
          style="scroll-snap-align: start;"
        >
          <span class="text-amber-400 text-3xl">★★★★★</span>
          <p class="text-sm font-semibold text-[--color-primary-dark] group-hover:text-white text-center transition-colors">
            Lire tous les avis sur Google
          </p>
          <Icon name="lucide:arrow-right" class="w-5 h-5 text-[--color-primary-dark] group-hover:text-white transition-colors" />
        </a>
        </div>

        <!-- Flèche droite -->
        <button
          v-if="canScrollRight"
          class="hidden md:flex absolute right-0 top-1/2 -translate-y-1/2 translate-x-4 z-10 w-10 h-10 rounded-full bg-white border border-[--color-border] shadow-md items-center justify-center hover:bg-[--color-background-soft] transition-colors"
          @click="scroll(1)"
        >
          <Icon name="lucide:chevron-right" class="w-5 h-5 text-[--color-text]" />
        </button>
      </div>

    </div>
  </section>
</template>

<script setup lang="ts">
const track = ref<HTMLElement | null>(null)
const canScrollLeft = ref(false)
const canScrollRight = ref(true)

function onScroll() {
  const el = track.value
  if (!el) return
  canScrollLeft.value = el.scrollLeft > 0
  canScrollRight.value = el.scrollLeft + el.clientWidth < el.scrollWidth - 4
}

function scroll(dir: 1 | -1) {
  const el = track.value
  if (!el) return
  // Scroll par la largeur visible du conteneur pour un déplacement naturel
  el.scrollBy({ left: dir * el.clientWidth * 0.75, behavior: 'smooth' })
}

onMounted(() => {
  onScroll()
})

const REVIEWS = [
  {
    name: 'Saïda M',
    when: 'il y a 5 mois',
    text: "Un accueil chaleureux, des sourires et une vraie gentillesse. L'équipe est très professionnelle, à l'écoute et prend le temps de conseiller. Un large choix de bijoux, tous plus beaux les uns que les autres. Une expérience remarquable, je recommande vivement !",
  },
  {
    name: 'Bella Nova',
    when: 'il y a 1 mois',
    text: "Je suis venue pour un piercing au nombril (mon premier !). Les deux filles sont adorables et très professionnelles. J'avais un peu peur mais tout s'est très bien passé grâce à leur bienveillance. Foncez sans hésiter !",
  },
  {
    name: 'Émilie Giat',
    when: 'il y a 3 mois',
    text: "Ma fille, une amie et moi sommes venues faire plusieurs piercings. Le personnel est d'une gentillesse et d'une patience incroyables ! L'hygiène est irréprochable et le travail est méticuleux !",
  },
  {
    name: 'asma bayalla',
    when: 'il y a 5 mois',
    text: "Amazing piercing shop — the staff was super friendly, professional, and made me feel comfortable the whole time. Everything was clean and well explained. I love my piercing and will definitely be back.",
  },
  {
    name: 'Aicha Fall',
    when: 'il y a 4 mois',
    text: "Très accueillant, une hygiène irréprochable et un personnel attentionné. Les filles m'ont conseillée et guidée tout au long de mon rendez-vous. Difficile de choisir tellement le choix est vaste. Je recommande à 100% !",
  },
  {
    name: 'Jade Casaban',
    when: 'il y a 3 mois',
    text: "Parfait ! Je suis venue avec mon amie faire deux piercings (flat). Les filles sont adorables et demandent la permission avant de filmer ou de poster quoi que ce soit. Les piercings sont magnifiques !! Merci à elles 🥰",
  },
  {
    name: 'Cam Is',
    when: 'il y a 7 mois',
    text: "Dommage que Samar n'ait pas plus de visibilité car son travail est parfait ! Professionnelle, méticuleuse, et un large choix de jolis bijoux. Je recommande vivement !",
  },
  {
    name: 'Kaoutar',
    when: 'il y a 2 mois',
    text: "Je recommande vivement ce studio ! Un très large choix de piercings, donc facile de trouver ce qu'on cherche. Le personnel est d'une gentillesse et d'une bienveillance incroyables. Ils prennent le temps d'expliquer et de conseiller.",
  },
]
</script>
