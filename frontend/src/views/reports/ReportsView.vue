<template>
  <div class="p-6 max-w-[1000px] mx-auto">
    <PageHeader title="Rapports & Exports" subtitle="Générez et téléchargez vos rapports" />

    <div class="grid grid-cols-1 md:grid-cols-2 gap-5">

      <!-- Mouvements de stock -->
      <div class="card-premium rounded-2xl p-5">
        <div class="flex items-center gap-3 mb-4">
          <div class="w-10 h-10 rounded-xl bg-emerald-100 flex items-center justify-center"><ArrowLeftRight :size="18" class="text-emerald-600" /></div>
          <div>
            <div class="font-semibold text-foreground">Mouvements de stock</div>
            <div class="text-xs text-muted-foreground">Entrées, sorties, transferts, ajustements</div>
          </div>
        </div>
        <div class="space-y-3 mb-4">
          <div class="grid grid-cols-2 gap-3">
            <div><label class="label-field">Du</label><input v-model="reports.movements.dateFrom" type="date" class="input-field" /></div>
            <div><label class="label-field">Au</label><input v-model="reports.movements.dateTo" type="date" class="input-field" /></div>
          </div>
          <div><label class="label-field">Entrepôt</label>
            <select v-model="reports.movements.entrepotId" class="select-field">
              <option value="">Tous</option>
              <option v-for="w in warehouses" :key="w.id" :value="w.id">{{ w.nom }}</option>
            </select>
          </div>
          <div><label class="label-field">Type</label>
            <select v-model="reports.movements.type" class="select-field">
              <option value="">Tous</option>
              <option value="ENTREE">Entrées</option>
              <option value="SORTIE">Sorties</option>
              <option value="TRANSFERT_SORTANT">Transferts</option>
              <option value="AJUSTEMENT_INVENTAIRE">Ajustements</option>
            </select>
          </div>
        </div>
        <div class="flex gap-2">
          <button @click="download('movements','csv')" :disabled="downloading" class="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl bg-muted border border-border text-muted-foreground text-xs font-medium hover:bg-muted/80 transition-colors disabled:opacity-50"><FileText :size="13" /> CSV</button>
          <button @click="download('movements','json')" :disabled="downloading" class="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs font-semibold hover:bg-emerald-100 transition-colors disabled:opacity-50"><FileDown :size="13" /> JSON</button>
          <button @click="downloadPdf('movements')" :disabled="downloading" class="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl bg-red-50 border border-red-200 text-red-700 text-xs font-semibold hover:bg-red-100 transition-colors disabled:opacity-50"><FileText :size="13" /> PDF</button>
        </div>
      </div>

      <!-- État des stocks -->
      <div class="card-premium rounded-2xl p-5">
        <div class="flex items-center gap-3 mb-4">
          <div class="w-10 h-10 rounded-xl bg-blue-100 flex items-center justify-center"><BarChart3 :size="18" class="text-blue-600" /></div>
          <div>
            <div class="font-semibold text-foreground">État des stocks</div>
            <div class="text-xs text-muted-foreground">Niveaux de stock par entrepôt</div>
          </div>
        </div>
        <div class="space-y-3 mb-4">
          <div><label class="label-field">Entrepôt</label>
            <select v-model="reports.stock.entrepotId" class="select-field">
              <option value="">Tous</option>
              <option v-for="w in warehouses" :key="w.id" :value="w.id">{{ w.nom }}</option>
            </select>
          </div>
        </div>
        <div class="flex gap-2">
          <button @click="download('stock','csv')" :disabled="downloading" class="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl bg-muted border border-border text-muted-foreground text-xs font-medium hover:bg-muted/80 transition-colors disabled:opacity-50"><FileText :size="13" /> CSV</button>
          <button @click="download('stock','json')" :disabled="downloading" class="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs font-semibold hover:bg-emerald-100 transition-colors disabled:opacity-50"><FileDown :size="13" /> JSON</button>
          <button @click="downloadPdf('stock')" :disabled="downloading" class="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl bg-red-50 border border-red-200 text-red-700 text-xs font-semibold hover:bg-red-100 transition-colors disabled:opacity-50"><FileText :size="13" /> PDF</button>
        </div>
      </div>

      <!-- Inventaires -->
      <div class="card-premium rounded-2xl p-5">
        <div class="flex items-center gap-3 mb-4">
          <div class="w-10 h-10 rounded-xl bg-amber-100 flex items-center justify-center"><ClipboardList :size="18" class="text-amber-600" /></div>
          <div>
            <div class="font-semibold text-foreground">Rapport d'inventaire</div>
            <div class="text-xs text-muted-foreground">Résultats de comptage et écarts</div>
          </div>
        </div>
        <div class="space-y-3 mb-4">
          <div><label class="label-field">Inventaire</label>
            <select v-model="reports.inventory.id" class="select-field">
              <option v-for="inv in inventories" :key="inv.id" :value="inv.id">{{ inv.numero }} — {{ inv.entrepotNom }}</option>
            </select>
          </div>
        </div>
        <div class="flex gap-2">
          <button @click="download('inventory','json')" :disabled="downloading || !reports.inventory.id" class="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs font-semibold hover:bg-emerald-100 transition-colors disabled:opacity-50"><FileDown :size="13" /> JSON</button>
          <button @click="downloadPdf('inventory')" :disabled="downloading || !reports.inventory.id" class="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl bg-red-50 border border-red-200 text-red-700 text-xs font-semibold hover:bg-red-100 transition-colors disabled:opacity-50"><FileText :size="13" /> PDF</button>
        </div>
      </div>

      <!-- Commandes -->
      <div class="card-premium rounded-2xl p-5">
        <div class="flex items-center gap-3 mb-4">
          <div class="w-10 h-10 rounded-xl bg-purple-100 flex items-center justify-center"><ShoppingCart :size="18" class="text-purple-600" /></div>
          <div>
            <div class="font-semibold text-foreground">Commandes fournisseurs</div>
            <div class="text-xs text-muted-foreground">Historique et statuts</div>
          </div>
        </div>
        <div class="space-y-3 mb-4">
          <div><label class="label-field">Statut</label>
            <select v-model="reports.orders.statut" class="select-field">
              <option value="">Tous</option>
              <option value="ENVOYEE">Envoyées</option>
              <option value="LIVREE">Livrées</option>
              <option value="ANNULEE">Annulées</option>
            </select>
          </div>
        </div>
        <div class="flex gap-2">
          <button @click="download('orders','json')" :disabled="downloading" class="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs font-semibold hover:bg-emerald-100 transition-colors disabled:opacity-50"><FileDown :size="13" /> JSON</button>
        </div>
      </div>
    </div>

    <Teleport to="body">
      <div v-if="toast" class="fixed bottom-5 right-5 z-50 flex items-center gap-3 px-5 py-3 rounded-2xl bg-foreground text-background shadow-xl text-sm font-medium">
        <Download :size="16" /> {{ toast }}
      </div>
    </Teleport>
  </div>
</template>
<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { ArrowLeftRight, BarChart3, ClipboardList, ShoppingCart, FileText, FileDown, Download } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import WarehouseService from '@/services/warehouse.service'
import { inventoryService, purchaseOrderService } from '@/services/operations.service'
import api from '@/services/api'

const warehouses = ref<any[]>([])
const inventories = ref<any[]>([])
const downloading = ref(false)
const toast = ref('')

const today = new Date().toISOString().slice(0, 10)
const firstDay = new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().slice(0, 10)

const reports = reactive({
  movements: { dateFrom: firstDay, dateTo: today, entrepotId: '', type: '' },
  stock: { entrepotId: '' },
  inventory: { id: 0 },
  orders: { statut: '' },
})

async function download(type: string, format: string) {
  downloading.value = true
  toast.value = `Génération du rapport "${type}"…`
  try {
    let data: any[] = []
    if (type === 'movements') {
      const params: Record<string, any> = { page: 0, size: 1000 }
      if (reports.movements.type) params.type = reports.movements.type
      if (reports.movements.dateFrom && reports.movements.dateTo) {
        params.dateDebut = reports.movements.dateFrom
        params.dateFin = reports.movements.dateTo
      }
      if (reports.movements.entrepotId) params.entrepotId = reports.movements.entrepotId
      const res = await api.get('/stock-movements', { params })
      data = res.data.content || []
    } else if (type === 'stock') {
      if (reports.stock.entrepotId) {
        const res = await api.get(`/stocks/warehouse/${reports.stock.entrepotId}`)
        data = res.data || []
      } else {
        const res = await api.get('/stocks', { params: { page: 0, size: 1000 } })
        data = res.data.content || []
      }
    } else if (type === 'inventory' && reports.inventory.id) {
      const inv = await inventoryService.findById(reports.inventory.id)
      data = inv.lignes || []
    } else if (type === 'orders') {
      const res = await purchaseOrderService.findAll(0, 1000)
      data = res.content
      if (reports.orders.statut) data = data.filter((o: any) => o.statut === reports.orders.statut)
    }

    const content = format === 'csv' ? toCSV(data) : JSON.stringify(data, null, 2)
    const mime = format === 'csv' ? 'text/csv' : 'application/json'
    const blob = new Blob([content], { type: mime })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a'); a.href = url; a.download = `${type}-${today}.${format}`; a.click()
    URL.revokeObjectURL(url)
    toast.value = `Rapport "${type}" téléchargé ✓`
  } catch {
    toast.value = 'Erreur lors de la génération du rapport'
  } finally {
    downloading.value = false
    setTimeout(() => { toast.value = '' }, 3000)
  }
}

async function downloadPdf(type: string) {
  downloading.value = true
  toast.value = `Génération du PDF "${type}"…`
  try {
    let url = ''
    const params: Record<string, any> = {}
    if (type === 'movements') {
      url = '/v1/reports/movements/pdf'
      if (reports.movements.dateFrom) params.dateDebut = reports.movements.dateFrom
      if (reports.movements.dateTo) params.dateFin = reports.movements.dateTo
      if (reports.movements.entrepotId) params.entrepotId = reports.movements.entrepotId
    } else if (type === 'stock') {
      url = '/v1/reports/stock/pdf'
      if (reports.stock.entrepotId) params.entrepotId = reports.stock.entrepotId
    } else if (type === 'inventory' && reports.inventory.id) {
      url = `/v1/reports/inventory/pdf`
    }
    if (!url) return
    const res = await api.get(url, { params, responseType: 'blob' })
    const blob = new Blob([res.data], { type: 'application/pdf' })
    const a = document.createElement('a'); a.href = URL.createObjectURL(blob)
    a.download = `${type}-${today}.pdf`; a.click()
    URL.revokeObjectURL(a.href)
    toast.value = `PDF "${type}" téléchargé ✓`
  } catch {
    toast.value = 'Erreur lors de la génération du PDF'
  } finally {
    downloading.value = false
    setTimeout(() => { toast.value = '' }, 3000)
  }
}

function toCSV(data: any[]) {
  if (!data.length) return ''
  const headers = Object.keys(data[0]).join(';')
  const rows = data.map(row => Object.values(row).map(v => `"${v ?? ''}"`).join(';'))
  return [headers, ...rows].join('\n')
}

onMounted(async () => {
  const [whs, invs] = await Promise.allSettled([WarehouseService.getAll(), inventoryService.findAll(0, 100)])
  if (whs.status === 'fulfilled') warehouses.value = whs.value.filter((w: any) => w.actif)
  if (invs.status === 'fulfilled') {
    inventories.value = invs.value.content
    if (inventories.value.length) reports.inventory.id = inventories.value[0].id
  }
})
</script>
