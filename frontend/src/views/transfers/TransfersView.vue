<template>
  <div class="p-6 max-w-[1400px] mx-auto">
    <PageHeader title="Transferts" :subtitle="`${filtered.length} transfert(s)`">
      <template #actions>
        <RouterLink to="/transfers/new" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white">
          <Plus :size="15" /> Nouveau transfert
        </RouterLink>
      </template>
    </PageHeader>

    <!-- Filters -->
    <div class="card-premium rounded-2xl p-4 mb-6 flex flex-wrap gap-3 items-center">
      <div class="relative flex-1 min-w-[200px]">
        <Search :size="15" class="absolute left-3.5 top-1/2 -translate-y-1/2 text-muted-foreground" />
        <input v-model="search" type="search" placeholder="N° transfert, entrepôt…" class="input-field pl-10" />
      </div>
      <select v-model="filterStatut" class="select-field w-auto min-w-[160px]">
        <option value="">Tous les statuts</option>
        <option value="BROUILLON">Brouillon</option>
        <option value="EXPEDIE">Expédié</option>
        <option value="RECU">Reçu</option>
        <option value="ANNULE">Annulé</option>
      </select>
    </div>

    <!-- Cards grid for active transfers -->
    <div v-if="enCours.length" class="mb-6">
      <h3 class="text-sm font-semibold text-muted-foreground uppercase tracking-wide mb-3">En cours</h3>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div v-for="t in enCours" :key="t.id" class="card-premium rounded-2xl p-5 border-l-4"
          :class="t.statut === 'EXPEDIE' ? 'border-l-blue-400' : 'border-l-amber-400'">
          <div class="flex items-start justify-between mb-3">
            <span class="font-mono font-bold text-sm text-foreground">{{ t.numero }}</span>
            <StatusBadge :status="t.statut" :dot="true" />
          </div>
          <div class="flex items-center gap-2 mb-3">
            <div class="flex-1 p-2 rounded-lg bg-muted/50 text-center">
              <div class="text-xs text-muted-foreground">Source</div>
              <div class="text-xs font-bold text-foreground truncate">{{ t.entrepotSourceNom.split(' ')[0] }}</div>
            </div>
            <ArrowRight :size="16" class="text-muted-foreground shrink-0" />
            <div class="flex-1 p-2 rounded-lg bg-muted/50 text-center">
              <div class="text-xs text-muted-foreground">Dest.</div>
              <div class="text-xs font-bold text-foreground truncate">{{ t.entrepotDestNom.split(' ')[0] }}</div>
            </div>
          </div>
          <div class="text-xs text-muted-foreground mb-3">
            {{ t.lignes.length }} produit(s) · Créé par {{ t.creePar.split(' ')[0] }}
          </div>
          <RouterLink :to="`/transfers/${t.id}`" class="w-full flex items-center justify-center gap-1.5 py-2 rounded-xl bg-muted border border-border text-xs font-medium hover:bg-muted/80 transition-colors text-muted-foreground">
            <Eye :size="12" /> Voir le détail
          </RouterLink>
        </div>
      </div>
    </div>

    <!-- Full table -->
    <div class="card-premium rounded-2xl overflow-hidden">
      <div class="px-5 py-4 border-b border-border">
        <h3 class="font-semibold text-foreground text-sm">Historique complet</h3>
      </div>
      <table class="w-full text-sm">
        <thead>
          <tr class="border-b border-border bg-muted/30">
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">N°</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Source → Destination</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden lg:table-cell">Expédition</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden lg:table-cell">Réception</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Statut</th>
            <th class="px-4 py-3.5"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="t in filtered" :key="t.id" class="table-row-hover border-b border-border/50 last:border-0">
            <td class="px-4 py-3.5 font-mono font-bold text-sm text-foreground">{{ t.numero }}</td>
            <td class="px-4 py-3.5">
              <div class="flex items-center gap-2 text-sm">
                <span class="text-muted-foreground">{{ t.entrepotSourceNom }}</span>
                <ArrowRight :size="12" class="text-muted-foreground shrink-0" />
                <span class="font-medium text-foreground">{{ t.entrepotDestNom }}</span>
              </div>
              <div class="text-xs text-muted-foreground mt-0.5">{{ t.lignes.length }} produit(s) · {{ t.creePar }}</div>
            </td>
            <td class="px-4 py-3.5 hidden lg:table-cell text-sm text-muted-foreground">{{ t.dateExpedition ? formatDate(t.dateExpedition) : '—' }}</td>
            <td class="px-4 py-3.5 hidden lg:table-cell text-sm text-muted-foreground">{{ t.dateReception ? formatDate(t.dateReception) : '—' }}</td>
            <td class="px-4 py-3.5"><StatusBadge :status="t.statut" :dot="true" /></td>
            <td class="px-4 py-3.5">
              <RouterLink :to="`/transfers/${t.id}`" class="p-1.5 rounded-lg hover:bg-muted text-muted-foreground hover:text-foreground transition-colors inline-flex">
                <Eye :size="14" />
              </RouterLink>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!filtered.length" class="text-center py-16 text-muted-foreground">
        <ArrowLeftRight :size="36" class="mx-auto mb-3 opacity-30" />
        <p class="font-medium">Aucun transfert trouvé</p>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, computed } from 'vue'
import { RouterLink } from 'vue-router'
import { Plus, Search, Eye, ArrowRight, ArrowLeftRight } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { TRANSFERS, formatDate } from '@/services/mockData'

const search = ref('')
const filterStatut = ref('')

const filtered = computed(() => TRANSFERS.filter(t => {
  if (search.value && !`${t.numero} ${t.entrepotSourceNom} ${t.entrepotDestNom}`.toLowerCase().includes(search.value.toLowerCase())) return false
  if (filterStatut.value && t.statut !== filterStatut.value) return false
  return true
}))
const enCours = computed(() => TRANSFERS.filter(t => ['BROUILLON', 'EXPEDIE'].includes(t.statut)))
</script>
