<template>
  <!-- Overlay -->
  <Transition name="fade">
    <div
      v-if="cart.open"
      class="fixed inset-0 z-50 bg-black/50"
      @click="cart.closeCart()"
    />
  </Transition>

  <!-- Drawer -->
  <Transition name="slide">
    <aside
      v-if="cart.open"
      class="fixed top-0 right-0 z-50 h-full w-full max-w-md
             bg-white shadow-2xl flex flex-col"
      style="will-change: transform;"
    >
      <!-- Header -->
      <div class="flex items-center justify-between px-6 py-5 border-b border-[--color-border]">
        <div class="flex items-center gap-3">
          <Icon name="lucide:shopping-bag" class="w-5 h-5 text-primary" />
          <h2 class="font-serif text-xl font-semibold">{{ $t('cart.title') }}</h2>
          <span v-if="cart.count > 0" class="badge-primary">{{ cart.count }}</span>
        </div>
        <button class="btn-ghost p-2" @click="cart.closeCart()">
          <Icon name="lucide:x" class="w-5 h-5" />
        </button>
      </div>

      <!-- Items -->
      <div class="flex-1 overflow-y-auto px-6 py-4">
        <!-- Empty state -->
        <div v-if="cart.isEmpty" class="flex flex-col items-center justify-center h-full gap-4 text-center">
          <Icon name="lucide:shopping-bag" class="w-12 h-12 text-[--color-border]" />
          <p class="text-[--color-text-muted]">{{ $t('cart.empty') }}</p>
          <button class="btn-outline" @click="cart.closeCart()">
            {{ $t('cart.continue_shopping') }}
          </button>
        </div>

        <!-- Cart items -->
        <ul v-else class="space-y-4">
          <li
            v-for="item in cart.items"
            :key="item.variantId"
            class="flex gap-4 py-4 border-b border-[--color-border] last:border-0 animate-fade-in"
          >
            <!-- Image -->
            <div class="w-20 h-20 rounded-lg overflow-hidden bg-[--color-background-soft] shrink-0">
              <NuxtImg
                v-if="item.imageUrl"
                :src="item.imageUrl"
                :alt="item.productName"
                class="w-full h-full object-cover"
              />
              <div v-else class="w-full h-full flex items-center justify-center">
                <Icon name="lucide:image" class="w-6 h-6 text-[--color-border]" />
              </div>
            </div>

            <!-- Info -->
            <div class="flex-1 min-w-0">
              <p class="font-medium text-sm text-[--color-text] line-clamp-2">
                {{ item.productName }}
              </p>
              <p class="text-xs text-[--color-text-muted] mt-0.5">{{ item.variantLabel }}</p>
              <p class="text-sm font-semibold text-[--color-primary-dark] mt-1">
                {{ format(item.price) }}
              </p>

              <!-- Quantity -->
              <div class="flex items-center gap-2 mt-2">
                <button
                  class="w-7 h-7 rounded-full border border-[--color-border] flex items-center justify-center
                         text-[--color-text-muted] hover:border-[--color-primary]
                         hover:text-[--color-primary] transition-colors text-sm"
                  @click="cart.updateQuantity(item.variantId, item.quantity - 1)"
                >
                  <Icon name="lucide:minus" class="w-3 h-3" />
                </button>
                <span class="w-8 text-center text-sm font-medium">{{ item.quantity }}</span>
                <button
                  class="w-7 h-7 rounded-full border border-[--color-border] flex items-center justify-center
                         text-[--color-text-muted] hover:border-[--color-primary]
                         hover:text-[--color-primary] transition-colors text-sm
                         disabled:opacity-30 disabled:cursor-not-allowed disabled:hover:border-[--color-border] disabled:hover:text-[--color-text-muted]"
                  :disabled="item.quantity >= item.stock"
                  @click="cart.updateQuantity(item.variantId, item.quantity + 1)"
                >
                  <Icon name="lucide:plus" class="w-3 h-3" />
                </button>
              </div>
            </div>

            <!-- Remove -->
            <button
              class="shrink-0 text-[--color-text-muted] hover:text-red-500 transition-colors p-1"
              :aria-label="$t('cart.remove')"
              @click="cart.remove(item.variantId)"
            >
              <Icon name="lucide:trash-2" class="w-4 h-4" />
            </button>
          </li>
        </ul>
      </div>

      <!-- Footer -->
      <div v-if="!cart.isEmpty" class="px-6 py-5 border-t border-[--color-border] bg-[--color-background-soft]">
        <div class="flex items-center justify-between mb-4">
          <span class="text-[--color-text-muted] font-medium">{{ $t('cart.total') }}</span>
          <span class="font-serif text-xl font-semibold text-[--color-text]">
            {{ format(cart.total) }}
          </span>
        </div>
        <NuxtLink
          :to="localePath('/checkout')"
          class="btn-primary w-full text-center"
          @click="cart.closeCart()"
        >
          <Icon name="lucide:credit-card" class="w-4 h-4" />
          {{ $t('cart.checkout') }}
        </NuxtLink>
      </div>
    </aside>
  </Transition>
</template>

<script setup lang="ts">
const cart = useCartStore()
const localePath = useLocalePath()
const { format } = usePrice()
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-enter-active {
  transition: transform 0.2s ease-out;
}
.slide-leave-active {
  transition: transform 0.15s ease-in;
}
.slide-enter-from,
.slide-leave-to {
  transform: translateX(100%);
}
</style>
