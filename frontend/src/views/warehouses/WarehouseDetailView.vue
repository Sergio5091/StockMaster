<template>
  <div class="p-6 max-w-[1400px] mx-auto">
    <div v-if="loading" class="flex justify-center py-20">
      <div class="w-8 h-8 border-4 border-emerald-500 border-t-transparent rounded-full animate-spin" />
    </div>

    <div v-else-if="error" class="card-premium rounded-2xl p-8 text-center text-red-500">
      <p class="font-medium">{{ error }}</p>
      <button @click="load" class="mt-3 text-sm underline">Réessayer</button>
    </div>

    <template v-else-if="warehouse">
    <PageHeader :title="warehouse.nom" :subtitle="`${warehouse.code} · ${warehouse.ville}`" back="Entrepôts">
      <template #actions>
        <RouterLink :to="`/warehouses/${warehouse.id}/zones`" class="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-muted border border-border text-sm font-medium text-foreground hover:bg-muted/80 transition-colors"><Layers :size="15" /> Gérer les zones</RouterLink>
        <button v-if="can('manage_warehouses')" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white"><Edit3 :size="15" /> Modifier</button>
      </template>
    </PageHeader>
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div class="lg:col-span-1 space-y-5">
        <div class="card-premium rounded-2xl p-5">
          <div class="flex items-center gap-3 mb-4">
            <div class="w-12 h-12 rounded-xl flex items-center justify-center" style="background: linear-gradient(135deg, #059669, #10b981);"><Warehouse :size="22" class="text-white" /></div>
            <div><div class="font-bold text-foreground">{{ warehouse.code }}</div><StatusBadge :status="warehouse.actif ? 'actif' : 'inactif'" :dot="true" /></div>
          </div>
          <div class="space-y-2.5 text-sm">
            <div class="flex justify-between"><span class="text-muted-foreground">Adresse</span><span class="font-medium text-right">{{ warehouse.adresse }}, {{ warehouse.ville }}</span></div>
            <div class="flex justify-between"><span class="text-muted-foreground">Téléphone</span><span class="font-medium">{{ warehouse.telephone }}</span></div>
            <div class="flex justify-between"><span class="text-muted-foreground">Email</span><span class="font-medium text-xs">{{ warehouse.email }}</span></div>
            <div class="flex justify-between"><span class="text-muted-foreground">Responsable</span><span class="font-medium">{{ warehouse.responsableUsername || '—' }}</span></div>
          </div>
        </div>
        <div class="card-premium rounded-2xl p-5">
          <h3 class="font-semibold text-foreground mb-4 flex items-center gap-2"><BarChart3 :size="16" class="text-emerald-600" /> Occupation</h3>
          <div class="text-center mb-4">
            <div class="text-4xl font-bold mb-1" :class="occ > 80 ? 'text-red-500' : occ > 60 ? 'text-amber-500' : 'text-emerald-600'">{{ occ }}%</div>
            <div class="text-sm text-muted-foreground">{{ warehouse.capaciteUtilisee.toLocaleString('fr-FR') }} / {{ warehouse.capaciteTotale.toLocaleString('fr-FR') }} m³</div>
          </div>
          <div class="w-full h-4 bg-muted rounded-full overflow-hidden">
            <div class="h-full rounded-full transition-all duration-700" :class="occ > 80 ? 'bg-red-400' : occ > 60 ? 'bg-amber-400' : 'bg-emerald-500'" :style="{ width: occ + '%' }" />
          </div>
        </div>
      </div>
      <div class="lg:col-span-2">
        <div class="card-premium rounded-2xl overflow-hidden">
          <div class="px-5 py-4 border-b border-border flex items-center justify-between">
            <h3 class="font-semibold text-foreground flex items-center gap-2"><Layers :size="16" class="text-emerald-600" /> Zones ({{ warehouseZones.length }})</h3>
            <RouterLink :to="`/warehouses/${warehouse.id}/zones`" class="text-xs text-emerald-600 hover:underline">Tout voir →</RouterLink>
          </div>
          <div class="p-4 space-y-3">
            <div v-for="z in warehouseZones" :key="z.id" class="flex items-center gap-4 p-3.5 rounded-xl hover:bg-muted/50 transition-colors border border-border/50">
              <div class="w-9 h-9 rounded-xl flex items-center justify-center shrink-0" :class="zoneIconBg(z.type)"><Layers :size="16" :class="zoneIconColor(z.type)" /></div>
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-2 mb-0.5">
                  <span class="font-semibold text-foreground text-sm">{{ z.code }}</span>
                  <span class="text-xs text-muted-foreground">— {{ z.nom }}</span>
                  <StatusBadge :status="z.type" class="ml-1" />
                </div>
                <div class="w-full h-1.5 bg-muted rounded-full overflow-hidden mt-1.5">
                  <div class="h-full rounded-full" :class="zoneOcc(z) > 85 ? 'bg-red-400' : zoneOcc(z) > 65 ? 'bg-amber-400' : 'bg-emerald-500'" :style="{ width: zoneOcc(z) + '%' }" />
                </div>
              </div>
              <div class="text-right shrink-0">
                <div class="font-semibold text-sm" :class="zoneOcc(z) > 85 ? 'text-red-500' : zoneOcc(z) > 65 ? 'text-amber-600' : 'text-emerald-600'">{{ zoneOcc(z) }}%</div>
                <div class="text-xs text-muted-foreground">{{ (z.occupationM3 || 0).toLocaleString('fr-FR') }} / {{ (z.capaciteM3 || 0).toLocaleString('fr-FR') }} m³</div>
              </div>
            </div>
            <div v-if="!warehouseZones.length" class="text-center py-8 text-sm text-muted-foreground">Aucune zone</div>
          </div>
        </div>
      </div>
    </div>
    </template>
  </div>
</template>
<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { Warehouse, Layers, Edit3, BarChart3 } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import WarehouseService, { type Warehouse as WarehouseType, type Zone } from '@/services/warehouse.service'
import { usePermissions } from '@/composables/usePermissions'
const { can } = usePermissions()
const route = useRoute()
const id = Number(route.params.id)
const warehouse = ref<WarehouseType | null>(null)
const warehouseZones = ref<Zone[]>([])
const loading = ref(true)
const error = ref('')
const occ = computed(() => {
  if (!warehouse.value?.capaciteTotale) return 0
  return Math.round(((warehouse.value.capaciteUtilisee || 0) / warehouse.value.capaciteTotale) * 100)
})
function zoneOcc(z: Zone) {
  if (!z.capaciteM3) return 0
  return Math.round(((z.occupationM3 || 0) / z.capaciteM3) * 100)
}
function zoneIconBg(type: string) { return { RECEPTION: 'bg-blue-100', STOCKAGE: 'bg-emerald-100', EXPEDITION: 'bg-amber-100', QUARANTAINE: 'bg-red-100' }[type] || 'bg-muted' }
function zoneIconColor(type: string) { return { RECEPTION: 'text-blue-600', STOCKAGE: 'text-emerald-600', EXPEDITION: 'text-amber-600', QUARANTAINE: 'text-red-500' }[type] || 'text-muted-foreground' }

async function load() {
  loading.value = true
  error.value = ''
  try {
    const [wh, zones] = await Promise.all([
      WarehouseService.getById(id),
      WarehouseService.getZones(id),
    ])
    warehouse.value = wh
    warehouseZones.value = zones
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors du chargement de l’entrepôt'
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>
