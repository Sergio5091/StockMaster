<template>
  <div class="p-6 max-w-[1600px] mx-auto">
    <PageHeader title="Stocks" subtitle="Vue globale des niveaux de stock">
      <template #actions>
        <RouterLink to="/stocks/movements"
          class="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-muted border border-border text-sm font-medium hover:bg-muted/80 transition-colors">
          <ArrowLeftRight :size="15" /> Mouvements
        </RouterLink>
      </template>
    </PageHeader>

    <!-- KPIs -->
    <div class="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
      <div class="kpi-card">
        <div class="text-xs text-red-500 uppercase tracking-wide font-semibold mb-1">🔴 Critique</div>
        <div class="text-3xl font-bold text-red-600">{{ kpis.critical }}</div>
        <div class="text-xs text-muted-foreground mt-1">produits en rupture</div>
      </div>
      <div class="kpi-card">
        <div class="text-xs text-amber-500 uppercase tracking-wide font-semibold mb-1">🟠 Faible</div>
        <div class="text-3xl font-bold text-amber-600">{{ kpis.low }}</div>
        <div class="text-xs text-muted-foreground mt-1">stocks insuffisants</div>
      </div>
      <div class="kpi-card">
        <div class="text-xs text-emerald-600 uppercase tracking-wide font-semibold mb-1">🟢 Normal</div>
        <div class="text-3xl font-bold text-emerald-600">{{ kpis.normal }}</div>
        <div class="text-xs text-muted-foreground mt-1">stocks OK</div>
      </div>
      <div class="kpi-card">
        <div class="text-xs text-blue-500 uppercase tracking-wide font-semibold mb-1">🔵 Excédentaire</div>
        <div class="text-3xl font-bold text-blue-600">{{ kpis.excess }}</div>
        <div class="text-xs text-muted-foreground mt-1">stocks en surplus</div>
      </div>
    </div>

    <!-- Filtres -->
    <div class="card-premium rounded-2xl p-4 mb-6 flex flex-wrap gap-3 items-center">
      <div class="relative flex-1 min-w-[200px]">
        <Search :size="15" class="absolute left-3.5 top-1/2 -translate-y-1/2 text-muted-foreground" />
        <input v-model="search" type="search" placeholder="Produit, référence…" class="input-field pl-10" @input="onSearch" />
      </div>
      <select v-model="filterEntrepotNom" class="select-field w-auto min-w-[170px]">
        <option value="">Tous les entrepôts</option>
        <option v-for="e in entrepots" :key="e">{{ e }}</option>
      </select>
      <select v-model="filterStatut" class="select-field w-auto min-w-[160px]">
        <option value="">Tous les niveaux</option>
        <option value="critical">🔴 Critique</option>
        <option value="low">🟠 Faible</option>
        <option value="normal">🟢 Normal</option>
        <option value="excess">🔵 Excédentaire</option>
      </select>
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
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Produit</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden md:table-cell">Entrepôt</th>
            <th class="text-right px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Disponible</th>
            <th class="text-right px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden lg:table-cell">Réservé</th>
            <th class="text-right px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden lg:table-cell">En transit</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Niveau</th>
            <th class="px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide text-center">Action</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="s in paginated" :key="s.id" class="table-row-hover border-b border-border/50 last:border-0">
            <td class="px-4 py-3.5">
              <div class="flex items-center gap-3">
                <div class="w-8 h-8 rounded-lg bg-muted flex items-center justify-center border border-border shrink-0">
                  <Package :size="14" class="text-muted-foreground" />
                </div>
                <div>
                  <div class="font-medium text-foreground">{{ s.produitNom }}</div>
                  <div class="text-xs text-muted-foreground font-mono">{{ s.produitRef }}</div>
                </div>
              </div>
            </td>
            <td class="px-4 py-3.5 hidden md:table-cell">
              <span class="text-xs font-mono font-semibold text-muted-foreground bg-muted px-2 py-0.5 rounded">{{ s.entrepotCode }}</span>
              <span class="ml-1.5 text-xs text-muted-foreground">{{ s.entrepotNom }}</span>
            </td>
            <td class="px-4 py-3.5 text-right">
              <span class="text-lg font-bold" :class="stockColor(s.statut)">{{ s.quantiteDisponible }}</span>
            </td>
            <td class="px-4 py-3.5 text-right hidden lg:table-cell text-sm text-amber-600 font-medium">{{ s.quantiteReservee }}</td>
            <td class="px-4 py-3.5 text-right hidden lg:table-cell text-sm text-blue-500 font-medium">{{ s.quantiteEnTransit }}</td>
            <td class="px-4 py-3.5">
              <StockIndicator :qty="s.quantiteDisponible" :min="s.stockMinimum ?? 0" :max="s.stockMaximum ?? 9999" :show-status="true" />
            </td>
            <td class="px-4 py-3.5 text-center">
              <button @click="openAdjust(s)"
                class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg border border-border text-xs font-medium hover:bg-muted transition-colors text-muted-foreground hover:text-foreground"
                title="Ajustement manuel">
                <SlidersHorizontal :size="13" />
                Ajuster
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="!filtered.length" class="text-center py-16 text-muted-foreground">
        <BarChart3 :size="36" class="mx-auto mb-3 opacity-30" /><p>Aucun stock trouvé</p>
      </div>

      <div v-if="totalPagesLocal > 1" class="flex items-center justify-between px-4 py-3 border-t border-border bg-muted/20">
        <span class="text-xs text-muted-foreground">{{ filtered.length }} ligne(s) · Page {{ page + 1 }} / {{ totalPagesLocal }}</span>
        <div class="flex items-center gap-1">
          <button @click="page--" :disabled="page === 0" class="p-1.5 rounded-lg hover:bg-muted disabled:opacity-40 transition-colors"><ChevronLeft :size="16" /></button>
          <button @click="page++" :disabled="page >= totalPagesLocal - 1" class="p-1.5 rounded-lg hover:bg-muted disabled:opacity-40 transition-colors"><ChevronRight :size="16" /></button>
        </div>
      </div>
    </div>

    <!-- Modal ajustement -->
    <StockAdjustModal v-if="adjustStock" :stock="adjustStock" @close="adjustStock = null" @adjusted="onAdjusted" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { Search, ArrowLeftRight, Package, ChevronLeft, ChevronRight, BarChart3, SlidersHorizontal } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StockIndicator from '@/components/common/StockIndicator.vue'
import StockAdjustModal from '@/components/stocks/StockAdjustModal.vue'
import { stockService, type StockItem } from '@/services/stock.service'

const stocks = ref<StockItem[]>([])
const loading = ref(false)
const error = ref('')
const search = ref('')
const filterEntrepotNom = ref('')
const filterStatut = ref('')
const page = ref(0)
const pageSize = 12
const adjustStock = ref<StockItem | null>(null)
let searchTimer: ReturnType<typeof setTimeout>

const entrepots = computed(() => [...new Set(stocks.value.map(s => s.entrepotNom).filter(Boolean))])

const filtered = computed(() => stocks.value.filter(s => {
  if (search.value && !`${s.produitNom} ${s.produitRef}`.toLowerCase().includes(search.value.toLowerCase())) return false
  if (filterEntrepotNom.value && s.entrepotNom !== filterEntrepotNom.value) return false
  if (filterStatut.value && s.statut !== filterStatut.value) return false
  return true
}))

const paginated = computed(() => filtered.value.slice(page.value * pageSize, (page.value + 1) * pageSize))
const totalPagesLocal = computed(() => Math.max(1, Math.ceil(filtered.value.length / pageSize)))

const kpis = computed(() => ({
  critical: stocks.value.filter(s => s.statut === 'critical').length,
  low: stocks.value.filter(s => s.statut === 'low').length,
  normal: stocks.value.filter(s => s.statut === 'normal').length,
  excess: stocks.value.filter(s => s.statut === 'excess').length,
}))

async function load() {
  loading.value = true
  error.value = ''
  try {
    const data = await stockService.findAll(0, 500)
    stocks.value = data.content
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors du chargement des stocks'
  } finally {
    loading.value = false
  }
}

function onSearch() {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => { page.value = 0 }, 300)
}

function stockColor(statut: string) {
  return ({ critical: 'text-red-600', low: 'text-amber-600', normal: 'text-emerald-600', excess: 'text-blue-600' } as Record<string, string>)[statut] || 'text-foreground'
}

function openAdjust(stock: StockItem) {
  adjustStock.value = stock
}

function onAdjusted() {
  load() // Recharger les stocks après ajustement
}

onMounted(load)
</script>
