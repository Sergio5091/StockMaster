<template>
  <span :class="['inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-medium border', badgeClass]">
    <span v-if="dot" :class="['w-1.5 h-1.5 rounded-full', dotClass]" />
    {{ label }}
  </span>
</template>
<script setup lang="ts">
import { computed } from 'vue'
const props = defineProps<{ status: string; dot?: boolean }>()
const STATUSES: Record<string, { label: string; cls: string; dot: string }> = {
  BROUILLON: { label: 'Brouillon', cls: 'badge-neutral', dot: 'bg-gray-400' },
  EN_ATTENTE_VALIDATION: { label: 'En attente', cls: 'badge-warning', dot: 'bg-amber-500' },
  VALIDE: { label: 'Validé', cls: 'badge-success', dot: 'bg-emerald-500' },
  REJETE: { label: 'Rejeté', cls: 'badge-danger', dot: 'bg-red-500' },
  ANNULE: { label: 'Annulé', cls: 'badge-danger', dot: 'bg-red-400' },
  EXPEDIE: { label: 'Expédié', cls: 'badge-info', dot: 'bg-blue-500' },
  RECU: { label: 'Reçu', cls: 'badge-success', dot: 'bg-emerald-500' },
  VALIDEE: { label: 'Validée', cls: 'badge-success', dot: 'bg-emerald-500' },
  ENVOYEE: { label: 'Envoyée', cls: 'badge-info', dot: 'bg-blue-500' },
  PARTIELLEMENT_LIVREE: { label: 'Partielle', cls: 'badge-warning', dot: 'bg-amber-500' },
  LIVREE: { label: 'Livrée', cls: 'badge-success', dot: 'bg-emerald-600' },
  PLANIFIE: { label: 'Planifié', cls: 'badge-info', dot: 'bg-blue-400' },
  EN_COURS: { label: 'En cours', cls: 'badge-warning', dot: 'bg-amber-500' },
  TERMINE: { label: 'Terminé', cls: 'badge-success', dot: 'bg-emerald-500' },
  actif: { label: 'Actif', cls: 'badge-success', dot: 'bg-emerald-500' },
  inactif: { label: 'Inactif', cls: 'badge-neutral', dot: 'bg-gray-400' },
  ROLE_ADMIN: { label: 'Admin', cls: 'badge-danger', dot: 'bg-red-500' },
  ROLE_MANAGER: { label: 'Manager', cls: 'badge-info', dot: 'bg-blue-500' },
  ROLE_OPERATOR: { label: 'Magasinier', cls: 'badge-neutral', dot: 'bg-gray-500' },
  ROLE_AUDITOR: { label: 'Auditeur', cls: 'badge-warning', dot: 'bg-amber-500' },
  RECEPTION: { label: 'Réception', cls: 'badge-info', dot: 'bg-blue-500' },
  STOCKAGE: { label: 'Stockage', cls: 'badge-success', dot: 'bg-emerald-500' },
  EXPEDITION: { label: 'Expédition', cls: 'badge-warning', dot: 'bg-amber-500' },
  QUARANTAINE: { label: 'Quarantaine', cls: 'badge-danger', dot: 'bg-red-500' },
  LIBRE: { label: 'Libre', cls: 'badge-success', dot: 'bg-emerald-500' },
  OCCUPE: { label: 'Occupé', cls: 'badge-neutral', dot: 'bg-gray-500' },
  RESERVE: { label: 'Réservé', cls: 'badge-warning', dot: 'bg-amber-500' },
  HORS_SERVICE: { label: 'Hors service', cls: 'badge-danger', dot: 'bg-red-500' },
  critical: { label: 'Critique', cls: 'stock-critical badge-danger', dot: 'bg-red-500' },
  low: { label: 'Faible', cls: 'badge-warning', dot: 'bg-amber-500' },
  normal: { label: 'Normal', cls: 'badge-success', dot: 'bg-emerald-500' },
  excess: { label: 'Excédentaire', cls: 'badge-info', dot: 'bg-blue-500' },
  ENTREE: { label: 'Entrée', cls: 'badge-success', dot: 'bg-emerald-500' },
  SORTIE: { label: 'Sortie', cls: 'badge-danger', dot: 'bg-red-400' },
  TRANSFERT_SORTANT: { label: 'Transfert ↗', cls: 'badge-info', dot: 'bg-blue-400' },
  TRANSFERT_ENTRANT: { label: 'Transfert ↙', cls: 'badge-info', dot: 'bg-blue-500' },
  AJUSTEMENT_INVENTAIRE: { label: 'Ajustement', cls: 'badge-warning', dot: 'bg-amber-500' },
  stock_critical: { label: 'Stock critique', cls: 'badge-danger', dot: 'bg-red-500' },
  stock_excess: { label: 'Stock excédentaire', cls: 'badge-info', dot: 'bg-blue-500' },
  zone_full: { label: 'Zone saturée', cls: 'badge-warning', dot: 'bg-amber-500' },
  order_late: { label: 'Commande retard', cls: 'badge-danger', dot: 'bg-red-400' },
}
const info = computed(() => STATUSES[props.status] || { label: props.status, cls: 'badge-neutral', dot: 'bg-gray-400' })
const badgeClass = computed(() => info.value.cls)
const dotClass = computed(() => info.value.dot)
const label = computed(() => info.value.label)
</script>
