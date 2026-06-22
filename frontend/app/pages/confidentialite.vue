<template>
  <div class="container-site max-w-2xl py-12">
    <h1 class="section-title mb-10">Confidentialité</h1>
    <div class="space-y-5 text-sm text-[--color-text-muted]">
      <p v-if="stripeEnabled">Piercing by Samar collecte uniquement les informations nécessaires au traitement de vos commandes : nom, email, téléphone et adresse de livraison.</p>
      <p v-else>Piercing by Samar collecte uniquement les informations nécessaires au traitement de vos commandes : nom, email et téléphone.</p>
      <p v-if="stripeEnabled">Ces données ne sont jamais revendues ni partagées avec des tiers, à l'exception de Stripe pour le traitement des paiements.</p>
      <p v-else>Ces données ne sont jamais revendues ni partagées avec des tiers.</p>
      <p v-if="stripeEnabled">Les paiements sont sécurisés via <a href="https://stripe.com/fr/privacy" target="_blank" rel="noopener" class="text-[--color-primary-dark] underline">Stripe</a>. Nous ne stockons aucune donnée bancaire.</p>
      <p>Ce site utilise uniquement des cookies techniques (panier, langue, devise). Aucun cookie publicitaire.</p>
      <p>Pour toute demande concernant vos données : <a href="mailto:contact@piercingbysamar.com" class="text-[--color-primary-dark] underline">contact@piercingbysamar.com</a></p>
    </div>
  </div>
</template>

<script setup lang="ts">
const { public: { stripeEnabled } } = useRuntimeConfig()
useSeoMeta({ title: 'Confidentialité — Piercing by Samar', robots: 'noindex' })
</script>
