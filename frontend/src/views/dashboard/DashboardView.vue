<template>
  <div class="p-6 space-y-6 max-w-[1600px] mx-auto">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-foreground">Bonjour, {{ auth.user?.prenom }} 👋</h1>
        <p class="text-sm text-muted-foreground mt-0.5">{{ today }} · {{ auth.roleLabel }}</p>
      </div>
      <div class="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs font-medium">
        <div class="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse" /> Temps réel
      </div>
    </div>

    <!-- Loader global -->
    <div v-if="loading" class="flex justify-center py-10">
      <div class="w-8 h-8 border-4 border-emerald-500 border-t-transparent rounded-full animate-spin" />
    </div>

    <template v-else>
      <!-- KPIs -->
      <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <div class="kpi-card">
          <div class="flex items-center justify-between mb-3">
            <span class="text-xs font-semibold text-muted-foreground uppercase tracking-wide">Entrepôts actifs</span>
            <div class="w-8 h-8 rounded-lg bg-emerald-100 flex items-center justify-center"><Warehouse :size="16" class="text-emerald-600" /></div>
          </div>
          <div class="text-3xl font-bold text-foreground mb-0.5">{{ stats.totalEntrepots }}</div>
          <div class="text-xs text-emerald-600 font-medium">Taux occ. moy. {{ stats.tauxOccupationMoyen }}%</div>
        </div>
        <div class="kpi-card">
          <div class="flex items-center justify-between mb-3">
            <span class="text-xs font-semibold text-muted-foreground uppercase tracking-wide">Produits en stock</span>
            <div class="w-8 h-8 rounded-lg bg-blue-100 flex items-center justify-center"><Package :size="16" class="text-blue-600" /></div>
          </div>
          <div class="text-3xl font-bold text-foreground mb-0.5">{{ stats.totalProduits }}</div>
          <div class="text-xs text-red-500 font-medium">{{ stats.produitsCritiques }} critiques</div>
        </div>
        <div class="kpi-card">
          <div class="flex items-center justify-between mb-3">
            <span class="text-xs font-semibold text-muted-foreground uppercase tracking-wide">Transferts actifs</span>
            <div class="w-8 h-8 rounded-lg bg-purple-100 flex items-center justify-center"><MoveRight :size="16" class="text-purple-600" /></div>
          </div>
          <div class="text-3xl font-bold text-foreground mb-0.5">{{ stats.transfertsEnCours }}</div>
          <RouterLink to="/transfers" class="text-xs text-blue-600 hover:underline">Voir détail →</RouterLink>
        </div>
        <div class="kpi-card">
          <div class="flex items-center justify-between mb-3">
            <span class="text-xs font-semibold text-muted-foreground uppercase tracking-wide">Commandes en cours</span>
            <div class="w-8 h-8 rounded-lg bg-amber-100 flex items-center justify-center"><ShoppingCart :size="16" class="text-amber-600" /></div>
          </div>
          <div class="text-3xl font-bold text-foreground mb-0.5">{{ stats.commandesEnAttente }}</div>
          <RouterLink to="/purchase-orders" class="text-xs text-amber-600 hover:underline">Voir détail →</RouterLink>
        </div>
      </div>

      <!-- Activity row -->
      <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <div class="bg-emerald-50 border border-emerald-100 rounded-2xl p-5">
          <div class="flex items-center gap-2 mb-2"><PackagePlus :size="15" class="text-emerald-600" /><span class="text-xs font-semibold text-emerald-700 uppercase tracking-wide">Entrées du mois</span></div>
          <div class="text-2xl font-bold text-emerald-800">{{ stats.entreesMonth }}</div>
        </div>
        <div class="bg-red-50 border border-red-100 rounded-2xl p-5">
          <div class="flex items-center gap-2 mb-2"><PackageMinus :size="15" class="text-red-500" /><span class="text-xs font-semibold text-red-600 uppercase tracking-wide">Sorties du mois</span></div>
          <div class="text-2xl font-bold text-red-700">{{ stats.sortiesMonth }}</div>
        </div>
        <div class="bg-blue-50 border border-blue-100 rounded-2xl p-5">
          <div class="flex items-center gap-2 mb-2"><PackageSearch :size="15" class="text-blue-600" /><span class="text-xs font-semibold text-blue-700 uppercase tracking-wide">Réceptions ce mois</span></div>
          <div class="text-2xl font-bold text-blue-800">{{ stats.receptionsMonth }}</div>
        </div>
        <div class="bg-amber-50 border border-amber-100 rounded-2xl p-5">
          <div class="flex items-center gap-2 mb-2"><ClipboardList :size="15" class="text-amber-600" /><span class="text-xs font-semibold text-amber-700 uppercase tracking-wide">Inventaires actifs</span></div>
          <div class="text-2xl font-bold text-amber-800">{{ stats.inventairesActifs }}</div>
        </div>
      </div>

      <!-- Charts + Taux occupation -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div class="lg:col-span-2 card-premium rounded-2xl p-6">
          <div class="flex items-center justify-between mb-6">
            <div><h3 class="font-semibold text-foreground">Évolution des mouvements</h3><p class="text-xs text-muted-foreground mt-0.5">30 derniers jours</p></div>
            <div class="flex items-center gap-3 text-xs text-muted-foreground">
              <span class="flex items-center gap-1.5"><span class="w-2.5 h-2.5 rounded-full bg-emerald-500" />Entrées</span>
              <span class="flex items-center gap-1.5"><span class="w-2.5 h-2.5 rounded-full bg-red-400" />Sorties</span>
            </div>
          </div>
          <apexchart v-if="chartSeries[0].data.length" type="area" height="220" :options="chartOptions" :series="chartSeries" />
          <div v-else class="h-[220px] flex items-center justify-center text-muted-foreground text-sm">Aucun mouvement récent</div>
        </div>
        <div class="card-premium rounded-2xl p-6">
          <h3 class="font-semibold text-foreground mb-4">Taux d'occupation</h3>
          <div v-if="activeWarehouses.length" class="space-y-4">
            <div v-for="wh in activeWarehouses" :key="wh.id" class="group">
              <div class="flex items-center justify-between mb-1.5">
                <span class="text-sm font-medium text-foreground">{{ wh.code }}</span>
                <span class="text-sm font-semibold" :class="occ(wh) > 80 ? 'text-red-500' : occ(wh) > 60 ? 'text-amber-500' : 'text-emerald-600'">{{ occ(wh) }}%</span>
              </div>
              <div class="w-full h-2 bg-muted rounded-full overflow-hidden">
                <div class="h-full rounded-full transition-all duration-700" :class="occ(wh) > 80 ? 'bg-red-400' : occ(wh) > 60 ? 'bg-amber-400' : 'bg-emerald-500'" :style="{ width: occ(wh) + '%' }" />
              </div>
              <div class="text-xs text-muted-foreground mt-1">{{ wh.capaciteUtilisee?.toLocaleString('fr-FR') }} / {{ wh.capaciteTotale?.toLocaleString('fr-FR') }} m³</div>
            </div>
          </div>
          <div v-else class="text-center text-sm text-muted-foreground py-8">Aucun entrepôt actif</div>
        </div>
      </div>
    </template>
  </div>
</template>
<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { Warehouse, Package, MoveRight, ShoppingCart, PackagePlus, PackageMinus, PackageSearch, ClipboardList } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import WarehouseService from '@/services/warehouse.service'
import { purchaseOrderService, transferService, receiptService, issueService, inventoryService } from '@/services/operations.service'
import dashboardService from '@/services/dashboard.service'
import api from '@/services/api'

const auth = useAuthStore()
const loading = ref(true)
const today = new Date().toLocaleDateString('fr-FR', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })

const stats = ref({ totalEntrepots: 0, tauxOccupationMoyen: 0, totalProduits: 0, produitsCritiques: 0, transfertsEnCours: 0, commandesEnAttente: 0, entreesMonth: 0, sortiesMonth: 0, receptionsMonth: 0, inventairesActifs: 0 })
const activeWarehouses = ref<any[]>([])
const stockEvolution = ref<{ date: string; entrees: number; sorties: number }[]>([])
const productRotation = ref<{ month: string; productsMoved: number; entries: number; exits: number }[]>([])

function occ(wh: any) {
  if (!wh.capaciteTotale) return 0
  return Math.min(100, Math.round((wh.capaciteUtilisee / wh.capaciteTotale) * 100))
}

const chartOptions = computed(() => ({
  chart: { type: 'area', toolbar: { show: false }, background: 'transparent' },
  colors: ['#10b981', '#f87171'],
  fill: { type: 'gradient', gradient: { opacityFrom: 0.25, opacityTo: 0 } },
  stroke: { curve: 'smooth', width: 2 },
  xaxis: { categories: stockEvolution.value.map(d => d.date), labels: { style: { colors: '#9ca3af', fontSize: '10px' }, rotate: -30 }, axisBorder: { show: false }, axisTicks: { show: false }, tickAmount: 6 },
  yaxis: { labels: { style: { colors: '#9ca3af', fontSize: '10px' } } },
  grid: { borderColor: '#f0f0f0', strokeDashArray: 4 },
  tooltip: { theme: 'light', y: { formatter: (v: number) => `${v} mvts` } },
  dataLabels: { enabled: false },
}))
const chartSeries = computed(() => [
  { name: 'Entrées', data: stockEvolution.value.map(d => d.entrees) },
  { name: 'Sorties', data: stockEvolution.value.map(d => d.sorties) },
])

async function loadDashboard() {
  loading.value = true
  try {
    const [warehouses, orders, transfers, receipts, issues, inventories, movements] = await Promise.allSettled([
      WarehouseService.getAll(),
      purchaseOrderService.findAll(0, 200),
      transferService.findAll(0, 200),
      receiptService.findAll(0, 200),
      issueService.findAll(0, 200),
      inventoryService.findAll(0, 200),
      api.get('/stocks/movements', { params: { page: 0, size: 200 } }),
    ])

    const whs = warehouses.status === 'fulfilled' ? warehouses.value : []
    activeWarehouses.value = whs.filter((w: any) => w.actif)

    const totalOcc = activeWarehouses.value.reduce((s: number, w: any) => s + occ(w), 0)
    stats.value.totalEntrepots = activeWarehouses.value.length
    stats.value.tauxOccupationMoyen = activeWarehouses.value.length ? Math.round(totalOcc / activeWarehouses.value.length) : 0

    if (orders.status === 'fulfilled') {
      const all = orders.value.content
      stats.value.commandesEnAttente = all.filter((o: any) => ['BROUILLON', 'VALIDEE', 'ENVOYEE'].includes(o.statut)).length
    }
    if (transfers.status === 'fulfilled') {
      stats.value.transfertsEnCours = transfers.value.content.filter((t: any) => ['BROUILLON', 'EXPEDIE'].includes(t.statut)).length
    }
    if (receipts.status === 'fulfilled') {
      const now = new Date(); const m = now.getMonth(); const y = now.getFullYear()
      stats.value.receptionsMonth = receipts.value.content.filter((r: any) => {
        const d = new Date(r.dateReception); return d.getMonth() === m && d.getFullYear() === y
      }).length
    }
    if (inventories.status === 'fulfilled') {
      stats.value.inventairesActifs = inventories.value.content.filter((i: any) => ['PLANIFIE', 'EN_COURS'].includes(i.statut)).length
    }

    if (movements.status === 'fulfilled') {
      const mvts = movements.value.data.content || []
      const now = new Date(); const m = now.getMonth(); const y = now.getFullYear()
      stats.value.entreesMonth = mvts.filter((mv: any) => mv.type === 'ENTREE' && new Date(mv.createdAt).getMonth() === m && new Date(mv.createdAt).getFullYear() === y).length
      stats.value.sortiesMonth = mvts.filter((mv: any) => mv.type === 'SORTIE' && new Date(mv.createdAt).getMonth() === m && new Date(mv.createdAt).getFullYear() === y).length

      // Construire évolution 30 jours
      const days: Record<string, { entrees: number; sorties: number }> = {}
      for (let i = 29; i >= 0; i--) {
        const d = new Date(); d.setDate(d.getDate() - i)
        const key = d.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit' })
        days[key] = { entrees: 0, sorties: 0 }
      }
      mvts.forEach((mv: any) => {
        const key = new Date(mv.createdAt).toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit' })
        if (days[key]) {
          if (mv.type === 'ENTREE') days[key].entrees++
          else if (mv.type === 'SORTIE') days[key].sorties++
        }
      })
      stockEvolution.value = Object.entries(days).map(([date, v]) => ({ date, ...v }))

      stats.value.totalProduits = new Set(mvts.map((mv: any) => mv.produitId)).size
      productRotation.value = Array.from(
        new Map(
          mvts
            .filter((mv: any) => mv.type === 'ENTREE' || mv.type === 'SORTIE')
            .reduce((acc: any[], mv: any) => {
              const month = new Date(mv.createdAt).toLocaleDateString('fr-FR', { month: '2-digit', year: 'numeric' })
              acc.push({ month, ...mv })
              return acc
            }, [])
            .reduce((map: Map<string, any>, mv: any) => {
              const key = mv.month
              if (!map.has(key)) {
                map.set(key, { month: key, productsMoved: new Set<number>(), entries: 0, exits: 0 })
              }
              const item = map.get(key)
              item.productsMoved.add(mv.produitId)
              if (mv.type === 'ENTREE') item.entries += 1
              if (mv.type === 'SORTIE') item.exits += 1
              return map
            }, new Map())
            .entries()
        )
      ).map(([month, item]: any) => ({
        month,
        productsMoved: item.productsMoved.size,
        entries: item.entries,
        exits: item.exits,
      })).sort((a: any, b: any) => a.month.localeCompare(b.month))
    }
  } finally {
    loading.value = false
  }
}

onMounted(loadDashboard)
</script>
