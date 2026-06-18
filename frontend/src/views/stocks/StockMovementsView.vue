<template>
  <div class="p-6 max-w-[1400px] mx-auto">
    <PageHeader title="Historique des mouvements" subtitle="Tous les mouvements de stock">
      <template #actions>
        <button class="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-muted border border-border text-sm font-medium hover:bg-muted/80 transition-colors"><Download :size="15" /> Exporter CSV</button>
      </template>
    </PageHeader>
    <div class="card-premium rounded-2xl p-4 mb-6 flex flex-wrap gap-3 items-center">
      <div class="relative flex-1 min-w-[200px]">
        <Search :size="15" class="absolute left-3.5 top-1/2 -translate-y-1/2 text-muted-foreground" />
        <input v-model="search" type="search" placeholder="Produit, référence doc…" class="input-field pl-10" />
      </div>
      <select v-model="filterType" class="select-field w-auto min-w-[160px]">
        <option value="">Tous les types</option>
        <option value="ENTREE">Entrées</option>
        <option value="SORTIE">Sorties</option>
        <option value="TRANSFERT_SORTANT">Transferts</option>
        <option value="AJUSTEMENT_INVENTAIRE">Ajustements</option>
      </select>
    </div>
    <div class="card-premium rounded-2xl overflow-hidden">
      <table class="w-full text-sm">
        <thead><tr class="border-b border-border bg-muted/30">
          <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Date</th>
          <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Type</th>
          <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Produit</th>
          <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden md:table-cell">Entrepôt</th>
          <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden lg:table-cell">Document</th>
          <th class="text-right px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Qté</th>
          <th class="text-right px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden md:table-cell">Avant → Après</th>
        </tr></thead>
        <tbody>
          <tr v-for="m in paginated" :key="m.id" class="table-row-hover border-b border-border/50 last:border-0">
            <td class="px-4 py-3.5 text-xs text-muted-foreground whitespace-nowrap">{{ formatDatetime(m.createdAt) }}</td>
            <td class="px-4 py-3.5"><StatusBadge :status="m.type" /></td>
            <td class="px-4 py-3.5"><div class="font-medium text-foreground text-sm">{{ m.produitNom }}</div><div class="text-xs text-muted-foreground font-mono">{{ m.produitRef }}</div></td>
            <td class="px-4 py-3.5 hidden md:table-cell text-sm text-muted-foreground">{{ m.entrepotNom }}</td>
            <td class="px-4 py-3.5 hidden lg:table-cell"><div class="text-xs font-mono font-medium text-foreground">{{ m.refDocument }}</div><div class="text-xs text-muted-foreground">{{ m.utilisateur }}</div></td>
            <td class="px-4 py-3.5 text-right"><span class="font-bold text-base" :class="m.type === 'ENTREE' ? 'text-emerald-600' : m.type === 'SORTIE' ? 'text-red-500' : 'text-blue-500'">{{ m.type === 'ENTREE' ? '+' : '' }}{{ m.quantite }}</span></td>
            <td class="px-4 py-3.5 text-right hidden md:table-cell text-xs text-muted-foreground">{{ m.quantiteAvant }} → <span class="font-semibold text-foreground">{{ m.quantiteApres }}</span></td>
          </tr>
        </tbody>
      </table>
      <div v-if="totalPages > 1" class="flex items-center justify-between px-4 py-3 border-t border-border bg-muted/20">
        <span class="text-xs text-muted-foreground">{{ filtered.length }} mouvement(s) · Page {{ page }} / {{ totalPages }}</span>
        <div class="flex items-center gap-1">
          <button @click="page--" :disabled="page === 1" class="p-1.5 rounded-lg hover:bg-muted disabled:opacity-40 transition-colors"><ChevronLeft :size="16" /></button>
          <button @click="page++" :disabled="page === totalPages" class="p-1.5 rounded-lg hover:bg-muted disabled:opacity-40 transition-colors"><ChevronRight :size="16" /></button>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, computed } from 'vue'
import { Search, Download, ChevronLeft, ChevronRight } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { STOCK_MOVEMENTS, formatDatetime } from '@/services/mockData'
const movements = STOCK_MOVEMENTS; const search = ref(''); const filterType = ref(''); const page = ref(1); const pageSize = 10
const filtered = computed(() => movements.filter(m => {
  if (search.value && !`${m.produitNom} ${m.produitRef} ${m.refDocument}`.toLowerCase().includes(search.value.toLowerCase())) return false
  if (filterType.value && m.type !== filterType.value) return false
  return true
}))
const totalPages = computed(() => Math.max(1, Math.ceil(filtered.value.length / pageSize)))
const paginated = computed(() => filtered.value.slice((page.value - 1) * pageSize, page.value * pageSize))
</script>
