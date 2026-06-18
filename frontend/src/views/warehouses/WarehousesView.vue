<template>
  <div class="p-6 max-w-[1600px] mx-auto">
    <PageHeader title="Entrepôts" subtitle="Gestion des sites de stockage">
      <template #actions>
        <button v-if="can('manage_warehouses')" @click="showForm = true" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white">
          <Plus :size="15" /> Nouvel entrepôt
        </button>
      </template>
    </PageHeader>

    <!-- Loading State -->
    <div v-if="loading" class="text-center py-20">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto mb-4"></div>
      <p class="text-muted-foreground">Chargement des entrepôts...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="card-premium rounded-2xl p-6 mb-6 border-red-200 bg-red-50">
      <p class="text-red-700 font-medium">Erreur : {{ error }}</p>
      <button @click="warehouseStore.fetchWarehouses()" class="mt-3 btn-outline-red text-sm">
        Réessayer
      </button>
    </div>

    <!-- Content -->
    <template v-else>
      <div class="card-premium rounded-2xl p-4 mb-6 flex flex-wrap gap-3 items-center">
        <div class="relative flex-1 min-w-[200px]">
          <Search :size="15" class="absolute left-3.5 top-1/2 -translate-y-1/2 text-muted-foreground" />
          <input v-model="search" type="search" placeholder="Rechercher…" class="input-field pl-10" />
        </div>
        <select v-model="filterVille" class="select-field w-auto min-w-[140px]">
          <option value="">Toutes les villes</option>
          <option v-for="v in villes" :key="v">{{ v }}</option>
        </select>
        <select v-model="filterStatut" class="select-field w-auto min-w-[130px]">
          <option value="">Tous les statuts</option>
          <option value="actif">Actif</option>
          <option value="inactif">Inactif</option>
        </select>
      </div>
      
      <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-5">
        <div v-for="wh in filtered" :key="wh.id" class="card-premium rounded-2xl overflow-hidden group hover:shadow-lg transition-all duration-200" :class="{ 'opacity-60': !wh.actif }">
          <div class="px-5 py-4 flex items-start justify-between" style="background: linear-gradient(135deg, #f8fffe 0%, #ecfdf5 100%);">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-xl flex items-center justify-center" style="background: linear-gradient(135deg, #059669, #10b981);">
                <Warehouse :size="18" class="text-white" />
              </div>
              <div>
                <div class="font-bold text-foreground">{{ wh.code }}</div>
                <div class="text-xs text-muted-foreground">{{ wh.ville }}, {{ wh.pays }}</div>
              </div>
            </div>
            <StatusBadge :status="wh.actif ? 'actif' : 'inactif'" :dot="true" />
          </div>
          <div class="px-5 pb-5 pt-4 space-y-4">
            <div>
              <div class="font-semibold text-foreground">{{ wh.nom }}</div>
              <div class="text-xs text-muted-foreground mt-0.5">{{ wh.adresse }}</div>
            </div>
            <div>
              <div class="flex items-center justify-between mb-1.5 text-xs">
                <span class="text-muted-foreground font-medium">Taux d'occupation</span>
                <span class="font-semibold" :class="occ(wh) > 80 ? 'text-red-500' : occ(wh) > 60 ? 'text-amber-600' : 'text-emerald-600'">{{ occ(wh) }}%</span>
              </div>
              <div class="w-full h-2.5 bg-muted rounded-full overflow-hidden">
                <div class="h-full rounded-full transition-all duration-700" :class="occ(wh) > 80 ? 'bg-gradient-to-r from-red-400 to-red-500' : occ(wh) > 60 ? 'bg-gradient-to-r from-amber-400 to-amber-500' : 'bg-gradient-to-r from-emerald-400 to-emerald-600'" :style="{ width: occ(wh) + '%' }" />
              </div>
              <div class="flex justify-between mt-1 text-xs text-muted-foreground">
                <span>{{ (wh.capaciteUtilisee || 0).toLocaleString('fr-FR') }} m³ utilisés</span>
                <span>{{ (wh.capaciteTotale || 0).toLocaleString('fr-FR') }} m³ total</span>
              </div>
            </div>
            <div class="grid grid-cols-3 gap-2">
              <div class="text-center p-2 rounded-xl bg-muted/50"><div class="text-base font-bold text-foreground">{{ wh.zones || '—' }}</div><div class="text-[10px] text-muted-foreground uppercase tracking-wide">Zones</div></div>
              <div class="text-center p-2 rounded-xl bg-muted/50"><div class="text-base font-bold text-foreground">—</div><div class="text-[10px] text-muted-foreground uppercase tracking-wide">Produits</div></div>
              <div class="text-center p-2 rounded-xl bg-muted/50"><div class="text-xs font-medium text-foreground truncate">{{ wh.responsable?.nom?.split(' ')[0] || '—' }}</div><div class="text-[10px] text-muted-foreground uppercase tracking-wide">Resp.</div></div>
            </div>
            <div class="flex gap-2 pt-1">
              <RouterLink :to="`/warehouses/${wh.id}`" class="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl bg-emerald-50 text-emerald-700 border border-emerald-200 text-xs font-semibold hover:bg-emerald-100 transition-colors"><Eye :size="13" /> Détail</RouterLink>
              <RouterLink :to="`/warehouses/${wh.id}/zones`" class="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl bg-muted text-muted-foreground border border-border text-xs font-medium hover:bg-muted/80 transition-colors"><Layers :size="13" /> Zones</RouterLink>
              <button v-if="can('manage_warehouses')" @click="editWh(wh)" class="p-2 rounded-xl bg-muted border border-border hover:bg-muted/80 transition-colors"><Edit3 :size="13" class="text-muted-foreground" /></button>
            </div>
          </div>
        </div>
      </div>
      
      <div v-if="!filtered.length && !loading" class="text-center py-20 text-muted-foreground">
        <Warehouse :size="40" class="mx-auto mb-3 opacity-30" />
        <p class="font-medium">Aucun entrepôt trouvé</p>
        <p class="text-sm mt-1">Vérifiez que le backend est démarré et que la base de données est accessible.</p>
      </div>
    </template>

    <WarehouseFormModal v-if="showForm" :warehouse="editingWh" @close="closeForm" @save="saveWh" />
  </div>
</template>
<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { Plus, Search, Warehouse, Layers, Eye, Edit3 } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import WarehouseFormModal from './WarehouseFormModal.vue'
import { useWarehouseStore } from '@/stores/warehouses'
import { usePermissions } from '@/composables/usePermissions'
import type { Warehouse as WarehouseType } from '@/services/warehouse.service'

const { can } = usePermissions()
const warehouseStore = useWarehouseStore()

// State
const search = ref('')
const filterVille = ref('')
const filterStatut = ref('')
const showForm = ref(false)
const editingWh = ref<WarehouseType | null>(null)

// Computed
const warehouses = computed(() => warehouseStore.warehouses)
const loading = computed(() => warehouseStore.loading)
const error = computed(() => warehouseStore.error)

const villes = computed(() => [...new Set(warehouses.value.map(w => w.ville).filter(Boolean))])

const filtered = computed(() => warehouses.value.filter(w => {
  const searchText = `${w.nom || ''} ${w.code || ''} ${w.ville || ''}`.toLowerCase()
  if (search.value && !searchText.includes(search.value.toLowerCase())) return false
  if (filterVille.value && w.ville !== filterVille.value) return false
  if (filterStatut.value === 'actif' && !w.actif) return false
  if (filterStatut.value === 'inactif' && w.actif) return false
  return true
}))

// Methods
function occ(w: WarehouseType) { 
  const utilise = w.capaciteUtilisee || 0
  const total = w.capaciteTotale || 0
  return total > 0 ? Math.round((utilise / total) * 100) : 0
}

function editWh(w: WarehouseType) { 
  editingWh.value = w
  showForm.value = true 
}

function closeForm() { 
  showForm.value = false
  editingWh.value = null 
}

async function saveWh(w: any) {
  try {
    if (editingWh.value) {
      await warehouseStore.updateWarehouse(editingWh.value.id, w)
    } else {
      await warehouseStore.createWarehouse(w)
    }
    closeForm()
  } catch (error) {
    console.error('Erreur lors de la sauvegarde:', error)
  }
}

// Lifecycle
onMounted(() => {
  warehouseStore.fetchWarehouses()
})
</script>
