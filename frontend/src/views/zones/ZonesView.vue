<template>
  <div class="p-6 max-w-[1200px] mx-auto">
    <PageHeader :title="`Zones — ${warehouseName}`" subtitle="Zones de l'entrepôt">
      <template #actions>
        <button v-if="can('manage_zones')" @click="showForm = true"
          class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white">
          <Plus :size="15" /> Nouvelle zone
        </button>
      </template>
    </PageHeader>

    <div v-if="loading" class="flex justify-center py-20">
      <div class="w-8 h-8 border-4 border-emerald-500 border-t-transparent rounded-full animate-spin" />
    </div>

    <div v-else-if="error" class="card-premium rounded-2xl p-8 text-center text-red-500">
      <p class="font-medium">{{ error }}</p>
      <button @click="load" class="mt-3 text-sm underline">Réessayer</button>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <div v-for="z in zones" :key="z.id" class="card-premium rounded-2xl p-5 hover:shadow-md transition-all">
        <div class="flex items-center justify-between mb-4">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-xl flex items-center justify-center" :class="iconBg(z.type)">
              <Layers :size="18" :class="iconColor(z.type)" />
            </div>
            <div>
              <div class="font-bold text-foreground">{{ z.code }}</div>
              <div class="text-xs text-muted-foreground">{{ z.nom }}</div>
            </div>
          </div>
          <StatusBadge :status="z.type" />
        </div>
        <div class="mb-3">
          <div class="flex justify-between text-xs mb-1.5">
            <span class="text-muted-foreground">Occupation</span>
            <span class="font-semibold" :class="z.tauxOccupation > 85 ? 'text-red-500' : z.tauxOccupation > 65 ? 'text-amber-500' : 'text-emerald-600'">
              {{ z.tauxOccupation }}%
            </span>
          </div>
          <div class="w-full h-2.5 bg-muted rounded-full overflow-hidden">
            <div class="h-full rounded-full transition-all"
              :class="z.tauxOccupation > 85 ? 'bg-red-400' : z.tauxOccupation > 65 ? 'bg-amber-400' : 'bg-emerald-500'"
              :style="{ width: z.tauxOccupation + '%' }" />
          </div>
          <div class="flex justify-between text-xs text-muted-foreground mt-1">
            <span>{{ z.occupationM3 }} m³ utilisés</span>
            <span>{{ z.capaciteM3 }} m³ total</span>
          </div>
        </div>
        <RouterLink :to="`/zones/${z.id}/locations`"
          class="mt-3 w-full flex items-center justify-center gap-1.5 py-2 rounded-xl bg-muted border border-border text-xs font-medium hover:bg-muted/80 transition-colors text-muted-foreground">
          <MapPin :size="12" /> Voir les emplacements
        </RouterLink>
      </div>
    </div>

    <div v-if="!zones.length && !loading && !error" class="text-center py-20 text-muted-foreground">
      <Layers :size="40" class="mx-auto mb-3 opacity-30" />
      <p>Aucune zone pour cet entrepôt</p>
    </div>

    <!-- Modal création zone -->
    <Teleport to="body">
      <div v-if="showForm" class="fixed inset-0 z-50 flex items-center justify-center p-4 modal-overlay" @click.self="showForm = false">
        <div class="bg-card rounded-2xl shadow-2xl w-full max-w-md border border-border">
          <div class="flex items-center justify-between px-6 py-4 border-b border-border">
            <h2 class="font-semibold">Nouvelle zone</h2>
            <button @click="showForm = false"><X :size="16" class="text-muted-foreground" /></button>
          </div>
          <form @submit.prevent="createZone" class="p-6 space-y-4">
            <div><label class="label-field">Nom *</label><input v-model="zoneForm.nom" class="input-field" required /></div>
            <div>
              <label class="label-field">Type *</label>
              <select v-model="zoneForm.type" class="select-field" required>
                <option value="RECEPTION">Réception</option>
                <option value="STOCKAGE">Stockage</option>
                <option value="EXPEDITION">Expédition</option>
                <option value="QUARANTAINE">Quarantaine</option>
              </select>
            </div>
            <div><label class="label-field">Capacité (m³)</label><input v-model.number="zoneForm.capaciteM3" type="number" min="0" step="0.1" class="input-field" /></div>
            <div v-if="formError" class="text-red-500 text-sm">{{ formError }}</div>
            <div class="flex gap-3 pt-2 border-t border-border">
              <button type="button" @click="showForm = false" class="flex-1 py-2.5 rounded-xl border border-border text-sm hover:bg-muted transition-colors">Annuler</button>
              <button type="submit" :disabled="saving" class="flex-1 btn-primary py-2.5 rounded-xl text-sm font-semibold text-white disabled:opacity-60">
                {{ saving ? '…' : 'Créer' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>
  </div>
</template>
<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { Plus, Layers, MapPin, X } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import WarehouseService, { type Zone } from '@/services/warehouse.service'
import { usePermissions } from '@/composables/usePermissions'

const { can } = usePermissions()
const route = useRoute()
const warehouseId = Number(route.params.warehouseId ?? route.params.id)

const zones = ref<Zone[]>([])
const warehouseName = ref('Entrepôt')
const loading = ref(false)
const error = ref('')
const showForm = ref(false)
const saving = ref(false)
const formError = ref('')
const zoneForm = reactive({ nom: '', type: 'STOCKAGE', capaciteM3: 0 })

async function load() {
  loading.value = true; error.value = ''
  try {
    const [wh, zs] = await Promise.all([
      WarehouseService.getById(warehouseId),
      WarehouseService.getZones(warehouseId),
    ])
    warehouseName.value = wh.code
    zones.value = zs
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors du chargement des zones'
  } finally {
    loading.value = false
  }
}

async function createZone() {
  saving.value = true; formError.value = ''
  try {
    await WarehouseService.createZone(warehouseId, zoneForm)
    await load()
    showForm.value = false
    Object.assign(zoneForm, { nom: '', type: 'STOCKAGE', capaciteM3: 0 })
  } catch (e: any) {
    formError.value = e?.response?.data?.message || 'Erreur lors de la création'
  } finally {
    saving.value = false
  }
}

function iconBg(t: string) {
  return ({ RECEPTION: 'bg-blue-100', STOCKAGE: 'bg-emerald-100', EXPEDITION: 'bg-amber-100', QUARANTAINE: 'bg-red-100' } as any)[t] || 'bg-muted'
}
function iconColor(t: string) {
  return ({ RECEPTION: 'text-blue-600', STOCKAGE: 'text-emerald-600', EXPEDITION: 'text-amber-600', QUARANTAINE: 'text-red-500' } as any)[t] || 'text-muted-foreground'
}

onMounted(load)
</script>
