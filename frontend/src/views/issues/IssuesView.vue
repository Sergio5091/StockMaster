<template>
  <div class="p-6 max-w-[1400px] mx-auto">
    <PageHeader title="Bons de sortie" :subtitle="`${total} bon(s)`">
      <template #actions>
        <RouterLink to="/issues/new" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white">
          <Plus :size="15" /> Nouveau bon
        </RouterLink>
      </template>
    </PageHeader>

    <div class="card-premium rounded-2xl p-4 mb-6 flex flex-wrap gap-3 items-center">
      <div class="relative flex-1 min-w-[200px]">
        <Search :size="15" class="absolute left-3.5 top-1/2 -translate-y-1/2 text-muted-foreground" />
        <input v-model="search" type="search" placeholder="N° bon, client…" class="input-field pl-10" @input="onSearch" />
      </div>
      <select v-model="filterMotif" class="select-field w-auto min-w-[170px]" @change="load">
        <option value="">Tous les motifs</option>
        <option value="LIVRAISON_CLIENT">Livraison client</option>
        <option value="USAGE_INTERNE">Usage interne</option>
        <option value="DESTRUCTION">Destruction</option>
        <option value="RETOUR">Retour</option>
      </select>
      <select v-model="filterStatut" class="select-field w-auto min-w-[200px]" @change="load">
        <option value="">Tous les statuts</option>
        <option value="BROUILLON">Brouillon</option>
        <option value="EN_ATTENTE_VALIDATION">En attente validation</option>
        <option value="VALIDE">Validé</option>
        <option value="ANNULE">Annulé</option>
      </select>
    </div>

    <div v-if="loading" class="flex justify-center py-20">
      <div class="w-8 h-8 border-4 border-emerald-500 border-t-transparent rounded-full animate-spin" />
    </div>
    <div v-else-if="error" class="card-premium rounded-2xl p-8 text-center text-red-500">
      <p class="font-medium">{{ error }}</p>
      <button @click="load" class="mt-3 text-sm underline">Réessayer</button>
    </div>

    <div v-else class="card-premium rounded-2xl overflow-hidden">
      <table class="w-full text-sm">
        <thead>
          <tr class="border-b border-border bg-muted/30">
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">N° Bon</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden md:table-cell">Entrepôt</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Motif / Client</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden lg:table-cell">Date</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Statut</th>
            <th class="px-4 py-3.5"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="iss in items" :key="iss.id" class="table-row-hover border-b border-border/50 last:border-0">
            <td class="px-4 py-3.5">
              <span class="font-mono font-bold text-sm text-foreground">{{ iss.numero }}</span>
            </td>
            <td class="px-4 py-3.5 hidden md:table-cell text-sm text-muted-foreground">{{ iss.entrepotNom }}</td>
            <td class="px-4 py-3.5">
              <span :class="['inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium border', motifBadge(iss.motif)]">{{ motifLabel(iss.motif) }}</span>
              <div v-if="iss.clientNom" class="text-xs text-muted-foreground mt-1">{{ iss.clientNom }}</div>
            </td>
            <td class="px-4 py-3.5 hidden lg:table-cell text-sm text-muted-foreground">{{ formatDate(iss.dateSortie) }}</td>
            <td class="px-4 py-3.5"><StatusBadge :status="iss.statut" :dot="true" /></td>
            <td class="px-4 py-3.5">
              <RouterLink :to="`/issues/${iss.id}`" class="p-1.5 rounded-lg hover:bg-muted text-muted-foreground hover:text-foreground transition-colors inline-flex">
                <Eye :size="14" />
              </RouterLink>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!items.length" class="text-center py-16 text-muted-foreground">
        <PackageMinus :size="36" class="mx-auto mb-3 opacity-30" />
        <p class="font-medium">Aucun bon de sortie</p>
      </div>
      <div v-if="totalPages > 1" class="flex items-center justify-between px-4 py-3 border-t border-border text-sm text-muted-foreground">
        <span>Page {{ page + 1 }} / {{ totalPages }}</span>
        <div class="flex gap-2">
          <button :disabled="page === 0" @click="page--; load()" class="px-3 py-1.5 rounded-lg border border-border disabled:opacity-40 hover:bg-muted transition-colors">Préc.</button>
          <button :disabled="page >= totalPages - 1" @click="page++; load()" class="px-3 py-1.5 rounded-lg border border-border disabled:opacity-40 hover:bg-muted transition-colors">Suiv.</button>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { Plus, Search, Eye, PackageMinus } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { issueService, type Issue } from '@/services/operations.service'
import { formatDate } from '@/utils/formatters'

const items = ref<Issue[]>([])
const loading = ref(false)
const error = ref('')
const search = ref('')
const filterMotif = ref('')
const filterStatut = ref('')
const page = ref(0)
const total = ref(0)
const totalPages = ref(0)
let searchTimer: ReturnType<typeof setTimeout>

async function load() {
  loading.value = true; error.value = ''
  try {
    const data = await issueService.findAll(page.value, 20)
    let content = data.content
    if (search.value) content = content.filter(i => `${i.numero} ${i.clientNom || ''}`.toLowerCase().includes(search.value.toLowerCase()))
    if (filterMotif.value) content = content.filter(i => i.motif === filterMotif.value)
    if (filterStatut.value) content = content.filter(i => i.statut === filterStatut.value)
    items.value = content
    total.value = data.totalElements
    totalPages.value = data.totalPages
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors du chargement des bons de sortie'
  } finally {
    loading.value = false
  }
}

function onSearch() {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => { page.value = 0; load() }, 300)
}

function motifLabel(m: string) {
  return ({ LIVRAISON_CLIENT: 'Livraison client', USAGE_INTERNE: 'Usage interne', DESTRUCTION: 'Destruction', RETOUR: 'Retour' } as any)[m] || m
}
function motifBadge(m: string) {
  return ({ LIVRAISON_CLIENT: 'bg-blue-50 text-blue-700 border-blue-200', USAGE_INTERNE: 'bg-purple-50 text-purple-700 border-purple-200', DESTRUCTION: 'bg-red-50 text-red-600 border-red-200', RETOUR: 'bg-amber-50 text-amber-700 border-amber-200' } as any)[m] || 'badge-neutral'
}

onMounted(load)
</script>
