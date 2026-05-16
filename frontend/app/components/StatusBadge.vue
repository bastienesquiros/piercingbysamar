<template>
  <span
    class="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-medium"
    :class="config.class"
  >
    <span class="w-1.5 h-1.5 rounded-full" :class="config.dot" />
    {{ config.label }}
  </span>
</template>

<script setup lang="ts">
const props = defineProps<{ status: string }>()

const STATUS_MAP: Record<string, { label: string; class: string; dot: string }> = {
  PENDING:               { label: 'En attente',    class: 'bg-amber-50 text-amber-700',  dot: 'bg-amber-500' },
  PAID:                  { label: 'Payée',          class: 'bg-green-50 text-green-700',  dot: 'bg-green-500' },
  SHIPPED:               { label: 'Expédiée',       class: 'bg-blue-50 text-blue-700',    dot: 'bg-blue-500' },
  DELIVERED:             { label: 'Livrée',          class: 'bg-gray-100 text-gray-600',   dot: 'bg-gray-400' },
  CANCELLED:             { label: 'Annulée',         class: 'bg-red-50 text-red-700',      dot: 'bg-red-500' },
  CLICK_COLLECT_PENDING: { label: 'C&C en attente', class: 'bg-purple-50 text-purple-700',dot: 'bg-purple-500' },
  READY:                 { label: 'Prête',           class: 'bg-teal-50 text-teal-700',    dot: 'bg-teal-500' },
  COLLECTED:             { label: 'Retirée',         class: 'bg-gray-100 text-gray-600',   dot: 'bg-gray-400' },
}

const config = computed(
  () => STATUS_MAP[props.status] ?? { label: props.status, class: 'bg-gray-100 text-gray-600', dot: 'bg-gray-400' }
)
</script>
