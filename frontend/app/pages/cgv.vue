<template>
  <div class="container-site max-w-2xl py-12">
    <h1 class="section-title mb-10">Conditions de vente</h1>
    <div class="space-y-5 text-sm text-[--color-text-muted]">
      <p>Les produits vendus sur piercingbysamar.com sont des bijoux et piercings fabriqués en titane ASTM F136, acier chirurgical ou bioflex, certifiés hypoallergéniques.</p>
      <p><strong class="text-[--color-text]">Prix :</strong> affichés en dirhams marocains (MAD). Une conversion indicative en euros ou dollars est disponible sur le site. Les prix peuvent être modifiés sans préavis.</p>
      <p v-if="stripeEnabled"><strong class="text-[--color-text]">Commande :</strong> validée à réception du paiement (en ligne via Stripe) ou à la confirmation du Click & Collect. Un email de confirmation est envoyé automatiquement.</p>
      <p v-else><strong class="text-[--color-text]">Commande :</strong> validée à la confirmation du Click & Collect. Un email de confirmation est envoyé automatiquement.</p>
      <p v-if="stripeEnabled"><strong class="text-[--color-text]">Livraison :</strong> expédition mondiale. Les délais dépendent de la destination. Le Click & Collect est disponible à Marrakech.</p>
      <p v-else><strong class="text-[--color-text]">Retrait :</strong> Click & Collect uniquement, disponible à Marrakech. Aucune livraison à domicile proposée.</p>
      <p v-if="stripeEnabled"><strong class="text-[--color-text]">Paiement :</strong> sécurisé via Stripe (en ligne) ou en liquide/carte sur place (Click & Collect). Aucune donnée bancaire n'est stockée sur nos serveurs.</p>
      <p v-else><strong class="text-[--color-text]">Paiement :</strong> en liquide ou carte sur place lors du retrait (Click & Collect). Aucun paiement en ligne disponible.</p>
      <p><strong class="text-[--color-text]">Retours :</strong> les produits non portés peuvent être retournés dans les 7 jours suivant la réception. Les piercings déjà portés ne sont pas repris pour des raisons d'hygiène.</p>
      <p>Pour toute question : <a href="https://wa.me/212781570083" class="text-[--color-primary-dark] underline">+212 7 81 57 00 83</a></p>
    </div>
  </div>
</template>

<script setup lang="ts">
const { public: { stripeEnabled } } = useRuntimeConfig()
useSeoMeta({ title: 'Conditions de vente — Piercing by Samar', robots: 'noindex' })
</script>
