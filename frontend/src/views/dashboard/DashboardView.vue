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
          <span class="text-xs font-semibold text-muted-foreground uppercase tracking-wide">Produits actifs</span>
          <div class="w-8 h-8 rounded-lg bg-blue-100 flex items-center justify-center"><Package :size="16" class="text-blue-600" /></div>
        </div>
        <div class="text-3xl font-bold text-foreground mb-0.5">{{ stats.totalProduits }}</div>
        <div class="text-xs text-red-500 font-medium">{{ stats.produitsCritiques }} critiques</div>
      </div>
      <div class="kpi-card">
        <div class="flex items-center justify-between mb-3">
          <span class="text-xs font-semibold text-muted-foreground uppercase tracking-wide">Valeur du stock</span>
          <div class="w-8 h-8 rounded-lg bg-purple-100 flex items-center justify-center"><TrendingUp :size="16" class="text-purple-600" /></div>
        </div>
        <div class="text-2xl font-bold text-foreground mb-0.5">{{ formatK(stats.valeurTotaleStock) }}</div>
        <div class="text-xs text-muted-foreground">Valorisation globale</div>
      </div>
      <div class="kpi-card">
        <div class="flex items-center justify-between mb-3">
          <span class="text-xs font-semibold text-muted-foreground uppercase tracking-wide">Alertes actives</span>
          <div class="w-8 h-8 rounded-lg bg-red-100 flex items-center justify-center"><Bell :size="16" class="text-red-500" /></div>
        </div>
        <div class="text-3xl font-bold text-foreground mb-0.5">{{ stats.alertesActives }}</div>
        <RouterLink to="/alerts" class="text-xs text-red-500 font-medium hover:underline">Voir les alertes →</RouterLink>
      </div>
    </div>

    <!-- Activity row -->
    <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
      <div class="bg-emerald-50 border border-emerald-100 rounded-2xl p-5">
        <div class="flex items-center gap-2 mb-2"><PackagePlus :size="15" class="text-emerald-600" /><span class="text-xs font-semibold text-emerald-700 uppercase tracking-wide">Entrées du mois</span></div>
        <div class="text-2xl font-bold text-emerald-800">{{ stats.entreesMonth.count }}</div>
        <div class="text-sm text-emerald-600 font-medium mt-0.5">{{ formatCurrency(stats.entreesMonth.valeur) }}</div>
      </div>
      <div class="bg-red-50 border border-red-100 rounded-2xl p-5">
        <div class="flex items-center gap-2 mb-2"><PackageMinus :size="15" class="text-red-500" /><span class="text-xs font-semibold text-red-600 uppercase tracking-wide">Sorties du mois</span></div>
        <div class="text-2xl font-bold text-red-700">{{ stats.sortiesMonth.count }}</div>
        <div class="text-sm text-red-500 font-medium mt-0.5">{{ formatCurrency(stats.sortiesMonth.valeur) }}</div>
      </div>
      <div class="bg-blue-50 border border-blue-100 rounded-2xl p-5">
        <div class="flex items-center gap-2 mb-2"><MoveRight :size="15" class="text-blue-600" /><span class="text-xs font-semibold text-blue-700 uppercase tracking-wide">Transferts actifs</span></div>
        <div class="text-2xl font-bold text-blue-800">{{ stats.transfertsEnCours }}</div>
        <RouterLink to="/transfers" class="text-xs text-blue-600 hover:underline">Voir détail →</RouterLink>
      </div>
      <div class="bg-amber-50 border border-amber-100 rounded-2xl p-5">
        <div class="flex items-center gap-2 mb-2"><ShoppingCart :size="15" class="text-amber-600" /><span class="text-xs font-semibold text-amber-700 uppercase tracking-wide">Commandes en cours</span></div>
        <div class="text-2xl font-bold text-amber-800">{{ stats.commandesEnAttente }}</div>
        <RouterLink to="/purchase-orders" class="text-xs text-amber-600 hover:underline">Voir détail →</RouterLink>
      </div>
    </div>

    <!-- Charts -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div class="lg:col-span-2 card-premium rounded-2xl p-6">
        <div class="flex items-center justify-between mb-6">
          <div><h3 class="font-semibold text-foreground">Évolution des mouvements</h3><p class="text-xs text-muted-foreground mt-0.5">30 derniers jours</p></div>
          <div class="flex items-center gap-3 text-xs text-muted-foreground">
            <span class="flex items-center gap-1.5"><span class="w-2.5 h-2.5 rounded-full bg-emerald-500" />Entrées</span>
            <span class="flex items-center gap-1.5"><span class="w-2.5 h-2.5 rounded-full bg-red-400" />Sorties</span>
          </div>
        </div>
        <apexchart type="area" height="220" :options="chartOptions" :series="chartSeries" />
      </div>
      <div class="card-premium rounded-2xl p-6">
        <h3 class="font-semibold text-foreground mb-4">Taux d'occupation</h3>
        <div class="space-y-4">
          <div v-for="wh in activeWarehouses" :key="wh.id" class="group">
            <div class="flex items-center justify-between mb-1.5">
              <span class="text-sm font-medium text-foreground">{{ wh.code }}</span>
              <span class="text-sm font-semibold" :class="occ(wh) > 80 ? 'text-red-500' : occ(wh) > 60 ? 'text-amber-500' : 'text-emerald-600'">{{ occ(wh) }}%</span>
            </div>
            <div class="w-full h-2 bg-muted rounded-full overflow-hidden">
              <div class="h-full rounded-full transition-all duration-700" :class="occ(wh) > 80 ? 'bg-red-400' : occ(wh) > 60 ? 'bg-amber-400' : 'bg-emerald-500'" :style="{ width: occ(wh) + '%' }" />
            </div>
            <div class="text-xs text-muted-foreground mt-1">{{ wh.capaciteUtilisee.toLocaleString('fr-FR') }} / {{ wh.capaciteTotale.toLocaleString('fr-FR') }} m³</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Bottom row -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <div class="card-premium rounded-2xl p-6">
        <div class="flex items-center justify-between mb-4">
          <h3 class="font-semibold text-foreground">Top 5 produits mouvementés</h3>
          <RouterLink to="/stocks/movements" class="text-xs text-emerald-600 hover:underline font-medium">Tous les mouvements →</RouterLink>
        </div>
        <div class="space-y-3">
          <div v-for="(p, i) in topProducts" :key="p.nom" class="flex items-center gap-3">
            <span class="w-6 h-6 rounded-lg text-xs font-bold flex items-center justify-center shrink-0" :class="i < 3 ? 'bg-emerald-100 text-emerald-700' : 'bg-muted text-muted-foreground'">{{ i + 1 }}</span>
            <div class="flex-1 min-w-0">
              <div class="text-sm font-medium text-foreground truncate">{{ p.nom }}</div>
              <div class="w-full h-1 bg-muted rounded-full mt-1 overflow-hidden">
                <div class="h-full rounded-full bg-emerald-500" :style="{ width: (p.mouvements / topProducts[0].mouvements * 100) + '%' }" />
              </div>
            </div>
            <div class="text-right shrink-0">
              <div class="text-sm font-semibold text-foreground">{{ p.mouvements }}</div>
              <div class="text-xs" :class="p.variation.startsWith('+') ? 'text-emerald-600' : 'text-red-500'">{{ p.variation }}</div>
            </div>
          </div>
        </div>
      </div>
      <div class="card-premium rounded-2xl p-6">
        <div class="flex items-center justify-between mb-4">
          <h3 class="font-semibold text-foreground">Alertes récentes</h3>
          <RouterLink to="/alerts" class="text-xs text-emerald-600 hover:underline font-medium">Toutes les alertes →</RouterLink>
        </div>
        <div class="space-y-2.5">
          <div v-for="a in recentAlerts" :key="a.id" class="flex items-start gap-3 p-3 rounded-xl hover:bg-muted/50 transition-colors">
            <div :class="['w-8 h-8 rounded-lg flex items-center justify-center shrink-0', alertIconBg(a.type)]">
              <component :is="alertIcon(a.type)" :size="14" :class="alertIconColor(a.type)" />
            </div>
            <div class="flex-1 min-w-0">
              <div class="text-sm font-medium text-foreground truncate">{{ a.titre }}</div>
              <div class="text-xs text-muted-foreground mt-0.5 line-clamp-1">{{ a.message }}</div>
            </div>
            <div class="text-xs text-muted-foreground shrink-0">{{ relTime(a.createdAt) }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import { Warehouse, Package, TrendingUp, Bell, PackagePlus, PackageMinus, MoveRight, ShoppingCart, AlertTriangle, Layers, Clock } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import { formatCurrency } from '@/utils/formatters'

const auth = useAuthStore()

// Données par défaut en cas d'erreur API
const stats = {
  totalEntrepots: 0,
  totalProduits: 0,
  produitsCritiques: 0,
  valeurTotaleStock: 0,
  entreesMonth: { count: 0, valeur: 0 },
  sortiesMonth: { count: 0, valeur: 0 },
  transfertsEnCours: 0,
  commandesEnAttente: 0,
  alertesActives: 0,
  tauxOccupationMoyen: 0
}

const topProducts: Array<{ nom: string; mouvements: number; variation: string }> = []
const activeWarehouses: Array<{ id: number; code: string; capaciteUtilisee: number; capaciteTotale: number }> = []
const recentAlerts: Array<{ id: number; type: string; titre: string; message: string; createdAt: string }> = []
const stockEvolution: Array<{ date: string; entrees: number; sorties: number }> = []
const today = new Date().toLocaleDateString('fr-FR', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })
function occ(wh: typeof WAREHOUSES[0]) { return Math.round((wh.capaciteUtilisee / wh.capaciteTotale) * 100) }
function formatK(n: number) { return n >= 1000 ? (n / 1000).toFixed(0) + ' k€' : n + ' €' }
function relTime(dateStr: string) { const diff = Date.now() - new Date(dateStr).getTime(); const h = Math.floor(diff / 3600000); if (h < 1) return "À l'instant"; if (h < 24) return `il y a ${h}h`; return `il y a ${Math.floor(h / 24)}j` }
function alertIcon(type: string) { return { stock_critical: AlertTriangle, stock_excess: TrendingUp, zone_full: Layers, order_late: Clock }[type] || Bell }
function alertIconBg(type: string) { return { stock_critical: 'bg-red-100', stock_excess: 'bg-blue-100', zone_full: 'bg-amber-100', order_late: 'bg-orange-100' }[type] || 'bg-muted' }
function alertIconColor(type: string) { return { stock_critical: 'text-red-500', stock_excess: 'text-blue-500', zone_full: 'text-amber-500', order_late: 'text-orange-500' }[type] || 'text-muted-foreground' }

const chartOptions = computed(() => ({
  chart: { type: 'area', toolbar: { show: false }, background: 'transparent' },
  colors: ['#10b981', '#f87171'],
  fill: { type: 'gradient', gradient: { shadeIntensity: 1, opacityFrom: 0.25, opacityTo: 0, stops: [0, 100] } },
  stroke: { curve: 'smooth', width: 2 },
  xaxis: { categories: stockEvolution.map(d => d.date), labels: { style: { colors: '#9ca3af', fontSize: '10px' }, rotate: -30 }, axisBorder: { show: false }, axisTicks: { show: false }, tickAmount: 6 },
  yaxis: { labels: { style: { colors: '#9ca3af', fontSize: '10px' } } },
  grid: { borderColor: '#f0f0f0', strokeDashArray: 4 },
  tooltip: { theme: 'light', y: { formatter: (v: number) => `${v} unités` } },
  dataLabels: { enabled: false },
}))
const chartSeries = computed(() => [
  { name: 'Entrées', data: stockEvolution.map(d => d.entrees) },
  { name: 'Sorties', data: stockEvolution.map(d => d.sorties) },
])
</script>
