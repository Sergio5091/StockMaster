<template>
  <div class="p-6 max-w-[1400px] mx-auto">
    <PageHeader title="Historique des mouvements" subtitle="Tous les mouvements de stock">
      <template #actions>
        <RouterLink to="/stocks"
          class="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-muted border border-border text-sm font-medium hover:bg-muted/80 transition-colors">
          <BarChart3 :size="15" /> Stocks
        </RouterLink>
        <button @click="exportCsv" :disabled="exporting"
          class="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-muted border border-border text-sm font-medium hover:bg-muted/80 transition-colors disabled:opacity-50">
          <Download :size="15" /> {{ exporting ? '…' : 'Exporter CSV' }}
        </button>
      </template>
    </PageHeader>

    <!-- Filtres -->
    <div class="card-premium rounded-2xl p-4 mb-6 flex flex-wrap gap-3 items-center">
      <div class="relative flex-1 min-w-[200px]">
        <Search :size="15" class="absolute left-3.5 top-1/2 -translate-y-1/2 text-muted-foreground" />
        <input v-model="search" type="search" placeholder="Produit, référence doc…" class="input-field pl-10" @input="onSearch" />
      </div>
      <select v-model="filterType" class="select-field w-auto min-w-[180px]" @change="onFilterChange">
        <option value="">Tous les types</option>
        <option value="ENTREE">Entrées</option>
        <option value="SORTIE">Sorties</option>
        <option value="TRANSFERT_SORTANT">Transferts sortants</option>
        <option value="TRANSFERT_ENTRANT">Transferts entrants</option>
        <option value="AJUSTEMENT_INVENTAIRE">Ajustements</option>
      </select>
      <div class="flex items-center gap-2">
        <input v-model="dateDebut" type="date" class="input-field w-auto" placeholder="Du" @change="onFilterChange" />
        <span class="text-xs text-muted-foreground">→</span>
        <input v-model="dateFin" type="date" class="input-field w-auto" placeholder="Au" @change="onFilterChange" />
      </div>
    </div>

    <!-- Loader -->
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
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Produit</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden md:table-cell">Entrepôt</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden lg:table-cell">Document / Note</th>
            <th class="text-right px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Qté</th>
            <th class="text-right px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden md:table-cell">Avant → Après</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="m in displayed" :key="m.id" class="table-row-hover border-b border-border/50 last:border-0">
            <td class="px-4 py-3.5 text-xs text-muted-foreground whitespace-nowrap">{{ formatDatetime(m.createdAt) }}</td>
            <td class="px-4 py-3.5"><StatusBadge :status="m.type" /></td>
            <td class="px-4 py-3.5">
              <div class="font-medium text-foreground text-sm">{{ m.produitNom }}</div>
              <div class="text-xs text-muted-foreground font-mono">{{ m.produitRef }}</div>
            </td>
            <td class="px-4 py-3.5 hidden md:table-cell text-sm text-muted-foreground">
              {{ m.entrepotSourceNom || m.entrepotDestinationNom || '—' }}
            </td>
            <td class="px-4 py-3.5 hidden lg:table-cell">
              <div class="text-xs font-mono font-medium text-foreground">{{ m.referenceDocument || '—' }}</div>
              <div v-if="m.note" class="text-xs text-muted-foreground truncate max-w-[200px]">{{ m.note }}</div>
            </td>
            <td class="px-4 py-3.5 text-right">
              <span class="font-bold text-base" :class="movementColor(m.type)">
                {{ isEntree(m.type) ? '+' : '-' }}{{ m.quantite }}
              </span>
            </td>
            <td class="px-4 py-3.5 text-right hidden md:table-cell text-xs text-muted-foreground">
              {{ m.quantiteAvant }} → <span class="font-semibold text-foreground">{{ m.quantiteApres }}</span>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="!displayed.length" class="text-center py-16 text-muted-foreground">
        <ArrowLeftRight :size="36" class="mx-auto mb-3 opacity-30" />
        <p>Aucun mouvement trouvé</p>
      </div>

      <div v-if="totalPages > 1" class="flex items-center justify-between px-4 py-3 border-t border-border bg-muted/20">
        <span class="text-xs text-muted-foreground">{{ totalElements }} mouvement(s) · Page {{ page + 1 }} / {{ totalPages }}</span>
        <div class="flex items-center gap-1">
          <button @click="page--; load()" :disabled="page === 0"
            class="p-1.5 rounded-lg hover:bg-muted disabled:opacity-40 transition-colors">
            <ChevronLeft :size="16" />
          </button>
          <button @click="page++; load()" :disabled="page >= totalPages - 1"
            class="p-1.5 rounded-lg hover:bg-muted disabled:opacity-40 transition-colors">
            <ChevronRight :size="16" />
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { Search, Download, ChevronLeft, ChevronRight, ArrowLeftRight, BarChart3 } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { stockMovementService, type StockMovement, type MovementType } from '@/services/stock.service'

const movements = ref<StockMovement[]>([])
const loading = ref(false)
const error = ref('')
const exporting = ref(false)
const search = ref('')
const filterType = ref<MovementType | ''>('')
const dateDebut = ref('')
const dateFin = ref('')
const page = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
let searchTimer: ReturnType<typeof setTimeout>

// Filtre local par texte (produit / doc)
const displayed = computed(() => {
  if (!search.value) return movements.value
  const q = search.value.toLowerCase()
  return movements.value.filter(m =>
    `${m.produitNom} ${m.produitRef} ${m.referenceDocument || ''}`.toLowerCase().includes(q)
  )
})

function isEntree(type: string) {
  return ['ENTREE', 'TRANSFERT_ENTRANT'].includes(type)
}

function movementColor(type: string) {
  if (isEntree(type)) return 'text-emerald-600'
  if (['SORTIE', 'TRANSFERT_SORTANT'].includes(type)) return 'text-red-500'
  return 'text-blue-500'
}

function formatDatetime(d: string) {
  if (!d) return '—'
  return new Date(d).toLocaleString('fr-FR', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  })
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const params: Record<string, unknown> = { page: page.value, size: 50 }
    if (filterType.value) params.type = filterType.value
    if (dateDebut.value && dateFin.value) {
      params.dateDebut = dateDebut.value
      params.dateFin = dateFin.value
    }
    const data = await stockMovementService.findAll(params)
    movements.value = data.content
    totalPages.value = data.totalPages
    totalElements.value = data.totalElements
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors du chargement des mouvements'
  } finally {
    loading.value = false
  }
}

function onSearch() {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => { page.value = 0 }, 300)
}

function onFilterChange() {
  page.value = 0
  load()
}

async function exportCsv() {
  exporting.value = true
  try {
    const data = await stockMovementService.findAll({ page: 0, size: 1000 })
    const rows = data.content
    const headers = 'Date;Type;Produit;Ref;EntrepôtSource;EntrepôtDest;Quantité;Avant;Après;Document;Note'
    const lines = rows.map(m => [
      formatDatetime(m.createdAt), m.type, m.produitNom, m.produitRef,
      m.entrepotSourceNom || '', m.entrepotDestinationNom || '',
      m.quantite, m.quantiteAvant, m.quantiteApres,
      m.referenceDocument || '', m.note || '',
    ].map(v => `"${v}"`).join(';'))
    const csv = [headers, ...lines].join('\n')
    const blob = new Blob([csv], { type: 'text/csv' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `mouvements-${new Date().toISOString().slice(0, 10)}.csv`
    a.click()
    URL.revokeObjectURL(url)
  } finally {
    exporting.value = false
  }
}

onMounted(load)
</script>
