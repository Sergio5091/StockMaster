<template>
  <div class="p-6 max-w-[1000px] mx-auto">
    <PageHeader title="Rapports & Exports" subtitle="Générez et téléchargez vos rapports" />

    <div class="grid grid-cols-1 md:grid-cols-2 gap-5">

      <!-- Report: Mouvements de stock -->
      <div class="card-premium rounded-2xl p-5">
        <div class="flex items-center gap-3 mb-4">
          <div class="w-10 h-10 rounded-xl bg-emerald-100 flex items-center justify-center">
            <ArrowLeftRight :size="18" class="text-emerald-600" />
          </div>
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
            <select v-model="reports.movements.entrepot" class="select-field">
              <option value="">Tous</option>
              <option v-for="w in warehouses" :key="w.id">{{ w.nom }}</option>
            </select>
          </div>
          <div><label class="label-field">Type de mouvement</label>
            <select v-model="reports.movements.type" class="select-field">
              <option value="">Tous</option>
              <option value="ENTREE">Entrées</option>
              <option value="SORTIE">Sorties</option>
              <option value="TRANSFERT">Transferts</option>
              <option value="AJUSTEMENT">Ajustements</option>
            </select>
          </div>
        </div>
        <div class="flex gap-2">
          <button @click="download('movements','excel')" class="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs font-semibold hover:bg-emerald-100 transition-colors"><FileSpreadsheet :size="13" /> Excel</button>
          <button @click="download('movements','csv')" class="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl bg-muted border border-border text-muted-foreground text-xs font-medium hover:bg-muted/80 transition-colors"><FileText :size="13" /> CSV</button>
          <button @click="download('movements','pdf')" class="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl bg-red-50 border border-red-200 text-red-600 text-xs font-medium hover:bg-red-100 transition-colors"><FileDown :size="13" /> PDF</button>
        </div>
      </div>

      <!-- Report: État des stocks -->
      <div class="card-premium rounded-2xl p-5">
        <div class="flex items-center gap-3 mb-4">
          <div class="w-10 h-10 rounded-xl bg-blue-100 flex items-center justify-center">
            <BarChart3 :size="18" class="text-blue-600" />
          </div>
          <div>
            <div class="font-semibold text-foreground">État des stocks</div>
            <div class="text-xs text-muted-foreground">Niveaux de stock à une date donnée</div>
          </div>
        </div>
        <div class="space-y-3 mb-4">
          <div><label class="label-field">Date de référence</label><input v-model="reports.stock.date" type="date" class="input-field" /></div>
          <div><label class="label-field">Entrepôt</label>
            <select v-model="reports.stock.entrepot" class="select-field">
              <option value="">Tous</option>
              <option v-for="w in warehouses" :key="w.id">{{ w.nom }}</option>
            </select>
          </div>
          <div><label class="label-field">Catégorie</label>
            <select v-model="reports.stock.categorie" class="select-field">
              <option value="">Toutes</option>
              <option value="Informatique">Informatique</option>
              <option value="Électronique">Électronique</option>
              <option value="Mobilier Bureau">Mobilier Bureau</option>
            </select>
          </div>
        </div>
        <div class="flex gap-2">
          <button @click="download('stock','excel')" class="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs font-semibold hover:bg-emerald-100 transition-colors"><FileSpreadsheet :size="13" /> Excel</button>
          <button @click="download('stock','csv')" class="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl bg-muted border border-border text-muted-foreground text-xs font-medium hover:bg-muted/80 transition-colors"><FileText :size="13" /> CSV</button>
          <button @click="download('stock','pdf')" class="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl bg-red-50 border border-red-200 text-red-600 text-xs font-medium hover:bg-red-100 transition-colors"><FileDown :size="13" /> PDF</button>
        </div>
      </div>

      <!-- Report: Fournisseurs -->
      <div class="card-premium rounded-2xl p-5">
        <div class="flex items-center gap-3 mb-4">
          <div class="w-10 h-10 rounded-xl bg-purple-100 flex items-center justify-center">
            <Truck :size="18" class="text-purple-600" />
          </div>
          <div>
            <div class="font-semibold text-foreground">Rapport fournisseurs</div>
            <div class="text-xs text-muted-foreground">Performance, délais, commandes</div>
          </div>
        </div>
        <div class="space-y-3 mb-4">
          <div class="grid grid-cols-2 gap-3">
            <div><label class="label-field">Du</label><input v-model="reports.suppliers.dateFrom" type="date" class="input-field" /></div>
            <div><label class="label-field">Au</label><input v-model="reports.suppliers.dateTo" type="date" class="input-field" /></div>
          </div>
          <div><label class="label-field">Fournisseur</label>
            <select v-model="reports.suppliers.fournisseur" class="select-field">
              <option value="">Tous</option>
              <option v-for="s in suppliers" :key="s.id">{{ s.nom }}</option>
            </select>
          </div>
        </div>
        <div class="flex gap-2">
          <button @click="download('suppliers','excel')" class="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs font-semibold hover:bg-emerald-100 transition-colors"><FileSpreadsheet :size="13" /> Excel</button>
          <button @click="download('suppliers','pdf')" class="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl bg-red-50 border border-red-200 text-red-600 text-xs font-medium hover:bg-red-100 transition-colors"><FileDown :size="13" /> PDF</button>
        </div>
      </div>

      <!-- Report: Inventaire -->
      <div class="card-premium rounded-2xl p-5">
        <div class="flex items-center gap-3 mb-4">
          <div class="w-10 h-10 rounded-xl bg-amber-100 flex items-center justify-center">
            <ClipboardList :size="18" class="text-amber-600" />
          </div>
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
          <button @click="download('inventory','excel')" class="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs font-semibold hover:bg-emerald-100 transition-colors"><FileSpreadsheet :size="13" /> Excel</button>
          <button @click="download('inventory','pdf')" class="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl bg-red-50 border border-red-200 text-red-600 text-xs font-medium hover:bg-red-100 transition-colors"><FileDown :size="13" /> PDF</button>
        </div>
      </div>
    </div>

    <!-- Toast -->
    <Teleport to="body">
      <div v-if="toast" class="fixed bottom-5 right-5 z-50 flex items-center gap-3 px-5 py-3 rounded-2xl bg-foreground text-background shadow-xl text-sm font-medium">
        <Download :size="16" /> {{ toast }}
      </div>
    </Teleport>
  </div>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ArrowLeftRight, BarChart3, Truck, ClipboardList, FileSpreadsheet, FileText, FileDown, Download } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import { WAREHOUSES, SUPPLIERS, INVENTORIES } from '@/services/mockData'

const warehouses = WAREHOUSES.filter(w => w.actif)
const suppliers = SUPPLIERS.filter(s => s.actif)
const inventories = INVENTORIES

const today = new Date().toISOString().slice(0, 10)
const firstDay = new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().slice(0, 10)

const reports = reactive({
  movements: { dateFrom: firstDay, dateTo: today, entrepot: '', type: '' },
  stock: { date: today, entrepot: '', categorie: '' },
  suppliers: { dateFrom: firstDay, dateTo: today, fournisseur: '' },
  inventory: { id: inventories[0]?.id ?? 1 },
})

const toast = ref('')
function download(type: string, format: string) {
  toast.value = `Génération du rapport "${type}" en ${format.toUpperCase()}…`
  setTimeout(() => { toast.value = '' }, 3000)
}
</script>
