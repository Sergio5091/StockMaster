<template>
  <div class="p-6 max-w-[1400px] mx-auto">
    <PageHeader title="Journal d'audit" subtitle="Traçabilité complète de toutes les actions" />
    <div class="card-premium rounded-2xl p-4 mb-6 flex flex-wrap gap-3 items-center">
      <div class="relative flex-1 min-w-[200px]">
        <Search :size="15" class="absolute left-3.5 top-1/2 -translate-y-1/2 text-muted-foreground" />
        <input v-model="search" type="search" placeholder="Entité, utilisateur…" class="input-field pl-10" />
      </div>
      <select v-model="filterAction" class="select-field w-auto min-w-[150px]">
        <option value="">Toutes les actions</option>
        <option value="CREATE">Création</option>
        <option value="UPDATE">Modification</option>
        <option value="DELETE">Suppression</option>
        <option value="STATUS_CHANGE">Changement statut</option>
      </select>
    </div>
    <div class="card-premium rounded-2xl overflow-hidden">
      <table class="w-full text-sm">
        <thead><tr class="border-b border-border bg-muted/30">
          <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Date</th>
          <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Utilisateur</th>
          <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Action</th>
          <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden md:table-cell">Entité</th>
          <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden lg:table-cell">Changements</th>
        </tr></thead>
        <tbody>
          <tr v-for="log in filtered" :key="log.id" class="table-row-hover border-b border-border/50 last:border-0">
            <td class="px-4 py-3.5 text-xs text-muted-foreground whitespace-nowrap">{{ formatDatetime(log.createdAt) }}</td>
            <td class="px-4 py-3.5">
              <div class="font-medium text-foreground">{{ log.utilisateurNom }}</div>
              <div class="text-xs text-muted-foreground">{{ log.ipAddress }}</div>
            </td>
            <td class="px-4 py-3.5">
              <span :class="['inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border', actionClass(log.action)]">{{ log.action }}</span>
            </td>
            <td class="px-4 py-3.5 hidden md:table-cell">
              <div class="text-sm font-medium text-foreground">{{ log.entite }}</div>
              <div class="text-xs text-muted-foreground font-mono">#{{ log.entiteId }}</div>
            </td>
            <td class="px-4 py-3.5 hidden lg:table-cell">
              <div v-if="log.ancienneValeur && log.nouvelleValeur" class="flex items-center gap-2 text-xs">
                <span class="text-red-500 line-through font-mono">{{ JSON.stringify(log.ancienneValeur) }}</span>
                <span class="text-muted-foreground">→</span>
                <span class="text-emerald-600 font-mono">{{ JSON.stringify(log.nouvelleValeur) }}</span>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!filtered.length" class="text-center py-16 text-muted-foreground"><History :size="36" class="mx-auto mb-3 opacity-30" /><p>Aucune entrée d'audit</p></div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, computed } from 'vue'
import { Search, History } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import { AUDIT_LOGS, formatDatetime } from '@/services/mockData'
const search = ref(''); const filterAction = ref('')
const filtered = computed(() => AUDIT_LOGS.filter(l => {
  if (search.value && !`${l.entite} ${l.utilisateurNom}`.toLowerCase().includes(search.value.toLowerCase())) return false
  if (filterAction.value && l.action !== filterAction.value) return false
  return true
}))
function actionClass(action: string) {
  return { CREATE: 'bg-emerald-50 text-emerald-700 border-emerald-200', UPDATE: 'bg-blue-50 text-blue-700 border-blue-200', DELETE: 'bg-red-50 text-red-600 border-red-200', STATUS_CHANGE: 'bg-amber-50 text-amber-700 border-amber-200' }[action] || 'badge-neutral'
}
</script>
