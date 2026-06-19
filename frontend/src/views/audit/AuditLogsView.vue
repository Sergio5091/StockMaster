<template>
  <div class="p-6 max-w-[1400px] mx-auto">
    <PageHeader title="Journal d'audit" subtitle="Traçabilité complète des mouvements de stock" />
    <div class="card-premium rounded-2xl p-4 mb-6 flex flex-wrap gap-3 items-center">
      <div class="relative flex-1 min-w-[200px]">
        <Search :size="15" class="absolute left-3.5 top-1/2 -translate-y-1/2 text-muted-foreground" />
        <input v-model="search" type="search" placeholder="Produit, référence…" class="input-field pl-10" @input="onSearch" />
      </div>
      <select v-model="filterType" class="select-field w-auto min-w-[180px]" @change="load">
        <option value="">Tous les types</option>
        <option value="ENTREE">Entrée</option>
        <option value="SORTIE">Sortie</option>
        <option value="TRANSFERT_SORTANT">Transfert sortant</option>
        <option value="TRANSFERT_ENTRANT">Transfert entrant</option>
        <option value="AJUSTEMENT_INVENTAIRE">Ajustement</option>
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
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Date</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Type</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden md:table-cell">Produit</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden md:table-cell">Entrepôt</th>
            <th class="text-right px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Qté</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden lg:table-cell">Référence doc.</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="mv in filtered" :key="mv.id" class="table-row-hover border-b border-border/50 last:border-0">
            <td class="px-4 py-3.5 text-xs text-muted-foreground whitespace-nowrap">{{ formatDatetime(mv.createdAt) }}</td>
            <td class="px-4 py-3.5">
              <span :class="['inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border', typeClass(mv.type)]">{{ typeLabel(mv.type) }}</span>
            </td>
            <td class="px-4 py-3.5 hidden md:table-cell">
              <div class="text-sm font-medium text-foreground">{{ mv.produitNom }}</div>
              <div class="text-xs text-muted-foreground font-mono">{{ mv.produitRef }}</div>
            </td>
            <td class="px-4 py-3.5 hidden md:table-cell text-sm text-muted-foreground">
              {{ mv.entrepotSourceNom || mv.entrepotDestinationNom || '—' }}
            </td>
            <td class="px-4 py-3.5 text-right font-semibold" :class="['ENTREE','TRANSFERT_ENTRANT','AJUSTEMENT_INVENTAIRE'].includes(mv.type) ? 'text-emerald-600' : 'text-red-500'">
              {{ ['ENTREE','TRANSFERT_ENTRANT'].includes(mv.type) ? '+' : '-' }}{{ mv.quantite }}
            </td>
            <td class="px-4 py-3.5 hidden lg:table-cell text-xs text-muted-foreground font-mono">{{ mv.referenceDocument || '—' }}</td>
          </tr>
        </tbody>
      </table>
      <div v-if="!filtered.length" class="text-center py-16 text-muted-foreground">
        <History :size="36" class="mx-auto mb-3 opacity-30" />
        <p>Aucun mouvement trouvé</p>
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
import { ref, computed, onMounted } from 'vue'
import { Search, History } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import api from '@/services/api'
import { formatDatetime } from '@/utils/formatters'

interface Movement { id: number; type: string; produitId: number; produitNom: string; produitRef: string; entrepotSourceNom?: string; entrepotDestinationNom?: string; quantite: number; referenceDocument?: string; createdAt: string }

const items = ref<Movement[]>([])
const loading = ref(false)
const error = ref('')
const search = ref('')
const filterType = ref('')
const page = ref(0)
const totalPages = ref(0)
let searchTimer: ReturnType<typeof setTimeout>

const filtered = computed(() => items.value.filter(mv => {
  if (search.value && !`${mv.produitNom} ${mv.produitRef} ${mv.referenceDocument || ''}`.toLowerCase().includes(search.value.toLowerCase())) return false
  if (filterType.value && mv.type !== filterType.value) return false
  return true
}))

async function load() {
  loading.value = true; error.value = ''
  try {
    const { data } = await api.get('/stocks/movements', { params: { page: page.value, size: 50 } })
    items.value = data.content || []
    totalPages.value = data.totalPages || 0
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors du chargement du journal'
  } finally {
    loading.value = false
  }
}

function onSearch() {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => { page.value = 0; load() }, 300)
}

function typeLabel(t: string) {
  return ({ ENTREE: 'Entrée', SORTIE: 'Sortie', TRANSFERT_SORTANT: 'Transf. sortant', TRANSFERT_ENTRANT: 'Transf. entrant', AJUSTEMENT_INVENTAIRE: 'Ajustement' } as any)[t] || t
}
function typeClass(t: string) {
  return ({ ENTREE: 'bg-emerald-50 text-emerald-700 border-emerald-200', SORTIE: 'bg-red-50 text-red-600 border-red-200', TRANSFERT_SORTANT: 'bg-blue-50 text-blue-700 border-blue-200', TRANSFERT_ENTRANT: 'bg-indigo-50 text-indigo-700 border-indigo-200', AJUSTEMENT_INVENTAIRE: 'bg-amber-50 text-amber-700 border-amber-200' } as any)[t] || 'badge-neutral'
}

onMounted(load)
</script>
