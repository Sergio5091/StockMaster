<template>
  <div class="p-6 max-w-[1200px] mx-auto">
    <PageHeader :title="`Zones — ${warehouseName}`" subtitle="Zones de l'entrepôt" back="Entrepôts">
      <template #actions>
        <button v-if="can('manage_zones')" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white"><Plus :size="15" /> Nouvelle zone</button>
      </template>
    </PageHeader>
    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <div v-for="z in zones" :key="z.id" class="card-premium rounded-2xl p-5 hover:shadow-md transition-all">
        <div class="flex items-center justify-between mb-4">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-xl flex items-center justify-center" :class="iconBg(z.type)"><Layers :size="18" :class="iconColor(z.type)" /></div>
            <div><div class="font-bold text-foreground">{{ z.code }}</div><div class="text-xs text-muted-foreground">{{ z.nom }}</div></div>
          </div>
          <StatusBadge :status="z.type" />
        </div>
        <div class="mb-3">
          <div class="flex justify-between text-xs mb-1.5">
            <span class="text-muted-foreground">Occupation</span>
            <span class="font-semibold" :class="occ(z) > 85 ? 'text-red-500' : occ(z) > 65 ? 'text-amber-500' : 'text-emerald-600'">{{ occ(z) }}%</span>
          </div>
          <div class="w-full h-2.5 bg-muted rounded-full overflow-hidden">
            <div class="h-full rounded-full transition-all" :class="occ(z) > 85 ? 'bg-red-400' : occ(z) > 65 ? 'bg-amber-400' : 'bg-emerald-500'" :style="{ width: occ(z) + '%' }" />
          </div>
          <div class="flex justify-between text-xs text-muted-foreground mt-1">
            <span>{{ z.occupationM3 }} m³ utilisés</span><span>{{ z.capaciteM3 }} m³ total</span>
          </div>
        </div>
        <div class="flex justify-between text-sm border-t border-border pt-3">
          <span class="text-muted-foreground">Emplacements libres</span>
          <span class="font-semibold text-emerald-600">{{ z.libres }} / {{ z.emplacements }}</span>
        </div>
        <RouterLink :to="`/zones/${z.id}/locations`" class="mt-3 w-full flex items-center justify-center gap-1.5 py-2 rounded-xl bg-muted border border-border text-xs font-medium hover:bg-muted/80 transition-colors text-muted-foreground">
          <MapPin :size="12" /> Voir les emplacements
        </RouterLink>
      </div>
    </div>
    <div v-if="!zones.length" class="text-center py-20 text-muted-foreground"><Layers :size="40" class="mx-auto mb-3 opacity-30" /><p>Aucune zone pour cet entrepôt</p></div>
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { Plus, Layers, MapPin } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { ZONES, WAREHOUSES } from '@/services/mockData'
import { usePermissions } from '@/composables/usePermissions'
const { can } = usePermissions()
const route = useRoute()
const warehouseId = Number(route.params.warehouseId)
const zones = computed(() => ZONES.filter(z => z.entrepotId === warehouseId))
const warehouseName = computed(() => WAREHOUSES.find(w => w.id === warehouseId)?.code || 'Entrepôt')
function occ(z: typeof ZONES[0]) { return Math.round((z.occupationM3 / z.capaciteM3) * 100) }
function iconBg(type: string) { return { RECEPTION: 'bg-blue-100', STOCKAGE: 'bg-emerald-100', EXPEDITION: 'bg-amber-100', QUARANTAINE: 'bg-red-100' }[type] || 'bg-muted' }
function iconColor(type: string) { return { RECEPTION: 'text-blue-600', STOCKAGE: 'text-emerald-600', EXPEDITION: 'text-amber-600', QUARANTAINE: 'text-red-500' }[type] || 'text-muted-foreground' }
</script>
