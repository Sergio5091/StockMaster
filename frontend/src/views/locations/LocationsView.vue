<template>
  <div class="p-6 max-w-[1200px] mx-auto">
    <PageHeader title="Emplacements" :subtitle="zoneName ? `Zone ${zoneName}` : `Zone ${zoneId}`">
      <template #actions>
        <button v-if="can('manage_locations')" @click="showForm = true"
          class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white">
          <Plus :size="15" /> Nouvel emplacement
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

    <div v-else class="card-premium rounded-2xl overflow-hidden">
      <table class="w-full text-sm">
        <thead>
          <tr class="border-b border-border bg-muted/30">
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Code</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden md:table-cell">Rayon / Étagère / Position</th>
            <th class="text-right px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden md:table-cell">Poids</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Statut</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden lg:table-cell">Produit</th>
            <th class="px-4 py-3.5"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="loc in locations" :key="loc.id" class="table-row-hover border-b border-border/50 last:border-0">
            <td class="px-4 py-3.5 font-mono text-xs font-bold text-foreground">{{ loc.code }}</td>
            <td class="px-4 py-3.5 hidden md:table-cell text-sm text-muted-foreground">
              {{ [loc.rayon, loc.etagere, loc.position].filter(Boolean).join(' / ') || '—' }}
            </td>
            <td class="px-4 py-3.5 text-right hidden md:table-cell text-sm text-muted-foreground">
              {{ loc.poidsActuel ?? 0 }} / {{ loc.capaciteKg ?? 0 }} kg
            </td>
            <td class="px-4 py-3.5"><StatusBadge :status="loc.statut" :dot="true" /></td>
            <td class="px-4 py-3.5 hidden lg:table-cell text-sm text-muted-foreground">{{ loc.produitNom || '—' }}</td>
            <td class="px-4 py-3.5">
              <RouterLink :to="`/locations/${loc.id}`"
                class="p-1.5 rounded-lg hover:bg-muted text-muted-foreground hover:text-foreground transition-colors inline-flex">
                <Eye :size="14" />
              </RouterLink>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!locations.length" class="text-center py-16 text-muted-foreground">
        <MapPin :size="36" class="mx-auto mb-3 opacity-30" />
        <p>Aucun emplacement dans cette zone</p>
      </div>
    </div>

    <!-- Modal création -->
    <Teleport to="body">
      <div v-if="showForm" class="fixed inset-0 z-50 flex items-center justify-center p-4 modal-overlay" @click.self="showForm = false">
        <div class="bg-card rounded-2xl shadow-2xl w-full max-w-md border border-border">
          <div class="flex items-center justify-between px-6 py-4 border-b border-border">
            <h2 class="font-semibold">Nouvel emplacement</h2>
            <button @click="showForm = false"><X :size="16" class="text-muted-foreground" /></button>
          </div>
          <form @submit.prevent="createLocation" class="p-6 space-y-4">
            <div class="grid grid-cols-3 gap-3">
              <div><label class="label-field">Rayon</label><input v-model="locForm.rayon" class="input-field" placeholder="A" /></div>
              <div><label class="label-field">Étagère</label><input v-model="locForm.etagere" class="input-field" placeholder="1" /></div>
              <div><label class="label-field">Position</label><input v-model="locForm.position" class="input-field" placeholder="01" /></div>
            </div>
            <div><label class="label-field">Capacité (kg)</label><input v-model.number="locForm.capaciteKg" type="number" min="0" class="input-field" /></div>
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
import { Plus, Eye, MapPin, X } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import WarehouseService, { type Location } from '@/services/warehouse.service'
import { usePermissions } from '@/composables/usePermissions'

const { can } = usePermissions()
const route = useRoute()
const zoneId = Number(route.params.id)

const locations = ref<Location[]>([])
const zoneName = ref('')
const loading = ref(false)
const error = ref('')
const showForm = ref(false)
const saving = ref(false)
const formError = ref('')
const locForm = reactive({ rayon: '', etagere: '', position: '', capaciteKg: 0 })

async function load() {
  loading.value = true; error.value = ''
  try {
    locations.value = await WarehouseService.getLocations(zoneId)
    if (locations.value[0]?.zoneNom) zoneName.value = locations.value[0].zoneNom
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors du chargement des emplacements'
  } finally {
    loading.value = false
  }
}

async function createLocation() {
  saving.value = true; formError.value = ''
  try {
    await WarehouseService.createLocation(zoneId, locForm)
    await load()
    showForm.value = false
    Object.assign(locForm, { rayon: '', etagere: '', position: '', capaciteKg: 0 })
  } catch (e: any) {
    formError.value = e?.response?.data?.message || 'Erreur lors de la création'
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>
