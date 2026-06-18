<template>
  <div class="p-6 max-w-[1400px] mx-auto">
    <PageHeader title="Bons de réception" :subtitle="`${filtered.length} bon(s)`">
      <template #actions>
        <RouterLink to="/receipts/new" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white">
          <Plus :size="15" /> Nouveau bon
        </RouterLink>
      </template>
    </PageHeader>

    <!-- Filters -->
    <div class="card-premium rounded-2xl p-4 mb-6 flex flex-wrap gap-3 items-center">
      <div class="relative flex-1 min-w-[200px]">
        <Search :size="15" class="absolute left-3.5 top-1/2 -translate-y-1/2 text-muted-foreground" />
        <input v-model="search" type="search" placeholder="N° bon, fournisseur…" class="input-field pl-10" />
      </div>
      <select v-model="filterEntrepot" class="select-field w-auto min-w-[170px]">
        <option value="">Tous les entrepôts</option>
        <option v-for="e in entrepots" :key="e">{{ e }}</option>
      </select>
      <select v-model="filterStatut" class="select-field w-auto min-w-[200px]">
        <option value="">Tous les statuts</option>
        <option value="BROUILLON">Brouillon</option>
        <option value="EN_ATTENTE_VALIDATION">En attente validation</option>
        <option value="VALIDE">Validé</option>
        <option value="REJETE">Rejeté</option>
      </select>
    </div>

    <!-- Table -->
    <div class="card-premium rounded-2xl overflow-hidden">
      <table class="w-full text-sm">
        <thead>
          <tr class="border-b border-border bg-muted/30">
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">N° Bon</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Fournisseur</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden md:table-cell">Entrepôt</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden lg:table-cell">Date réception</th>
            <th class="text-center px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden lg:table-cell">Lignes</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Statut</th>
            <th class="px-4 py-3.5"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="r in filtered" :key="r.id" class="table-row-hover border-b border-border/50 last:border-0">
            <td class="px-4 py-3.5">
              <span class="font-mono font-bold text-sm text-foreground">{{ r.numero }}</span>
              <div v-if="r.commandeId" class="text-xs text-blue-600 mt-0.5">Lié à CF-2024-00{{ r.commandeId }}</div>
            </td>
            <td class="px-4 py-3.5">
              <div class="flex items-center gap-2">
                <div class="w-7 h-7 rounded-lg flex items-center justify-center text-white text-xs font-bold shrink-0" style="background: linear-gradient(135deg, #3b82f6, #06b6d4);">
                  {{ r.fournisseurNom.slice(0,2).toUpperCase() }}
                </div>
                <span class="font-medium text-foreground text-sm">{{ r.fournisseurNom }}</span>
              </div>
            </td>
            <td class="px-4 py-3.5 hidden md:table-cell text-sm text-muted-foreground">{{ r.entrepotNom }}</td>
            <td class="px-4 py-3.5 hidden lg:table-cell text-sm text-muted-foreground">{{ formatDate(r.dateReception) }}</td>
            <td class="px-4 py-3.5 hidden lg:table-cell text-center">
              <span class="inline-flex items-center justify-center w-6 h-6 rounded-full bg-muted text-xs font-bold text-muted-foreground">{{ r.lignes.length }}</span>
            </td>
            <td class="px-4 py-3.5"><StatusBadge :status="r.statut" :dot="true" /></td>
            <td class="px-4 py-3.5">
              <RouterLink :to="`/receipts/${r.id}`" class="p-1.5 rounded-lg hover:bg-muted text-muted-foreground hover:text-foreground transition-colors inline-flex">
                <Eye :size="14" />
              </RouterLink>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!filtered.length" class="text-center py-16 text-muted-foreground">
        <PackageOpen :size="36" class="mx-auto mb-3 opacity-30" />
        <p class="font-medium">Aucun bon de réception</p>
        <RouterLink to="/receipts/new" class="mt-3 inline-flex items-center gap-1.5 text-sm text-emerald-600 hover:underline">
          <Plus :size="14" /> Créer le premier bon
        </RouterLink>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, computed } from 'vue'
import { RouterLink } from 'vue-router'
import { Plus, Search, Eye, PackageOpen } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { RECEIPTS, formatDate } from '@/services/mockData'
import { usePermissions } from '@/composables/usePermissions'

const { can } = usePermissions()
const search = ref('')
const filterEntrepot = ref('')
const filterStatut = ref('')

const entrepots = computed(() => [...new Set(RECEIPTS.map(r => r.entrepotNom))])
const filtered = computed(() => RECEIPTS.filter(r => {
  if (search.value && !`${r.numero} ${r.fournisseurNom}`.toLowerCase().includes(search.value.toLowerCase())) return false
  if (filterEntrepot.value && r.entrepotNom !== filterEntrepot.value) return false
  if (filterStatut.value && r.statut !== filterStatut.value) return false
  return true
}))
</script>
