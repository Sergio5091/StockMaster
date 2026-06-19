<template>
  <div class="p-6 max-w-[1200px] mx-auto">
    <PageHeader title="Inventaires" :subtitle="`${total} inventaire(s)`">
      <template #actions>
        <RouterLink v-if="can('launch_inventory')" to="/inventories/new" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white">
          <Plus :size="15" /> Planifier un inventaire
        </RouterLink>
      </template>
    </PageHeader>

    <div class="card-premium rounded-2xl p-4 mb-6 flex flex-wrap gap-3 items-center">
      <select v-model="filterStatut" class="select-field w-auto min-w-[160px]" @change="load">
        <option value="">Tous les statuts</option>
        <option value="PLANIFIE">Planifié</option>
        <option value="EN_COURS">En cours</option>
        <option value="TERMINE">Terminé</option>
        <option value="ANNULE">Annulé</option>
      </select>
      <select v-model="filterType" class="select-field w-auto min-w-[140px]" @change="load">
        <option value="">Tous les types</option>
        <option value="COMPLET">Complet</option>
        <option value="PARTIEL">Partiel</option>
      </select>
    </div>

    <div v-if="loading" class="flex justify-center py-20">
      <div class="w-8 h-8 border-4 border-emerald-500 border-t-transparent rounded-full animate-spin" />
    </div>
    <div v-else-if="error" class="card-premium rounded-2xl p-8 text-center text-red-500">
      <p class="font-medium">{{ error }}</p>
      <button @click="load" class="mt-3 text-sm underline">Réessayer</button>
    </div>

    <div v-else class="space-y-4">
      <div v-for="inv in filtered" :key="inv.id" class="card-premium rounded-2xl overflow-hidden hover:shadow-md transition-all">
        <div class="flex items-center gap-5 px-5 py-4">
          <div :class="['w-12 h-12 rounded-xl flex items-center justify-center shrink-0', statutBg(inv.statut)]">
            <ClipboardList :size="20" :class="statutColor(inv.statut)" />
          </div>
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2 mb-1 flex-wrap">
              <span class="font-bold text-foreground font-mono">{{ inv.numero }}</span>
              <StatusBadge :status="inv.statut" :dot="true" />
              <span class="text-xs px-2 py-0.5 rounded-full bg-muted border border-border text-muted-foreground font-medium">{{ inv.type }}</span>
            </div>
            <div class="flex items-center gap-4 text-sm text-muted-foreground flex-wrap">
              <span class="flex items-center gap-1"><Warehouse :size="12" /> {{ inv.entrepotNom }}</span>
              <span v-if="inv.zoneNom" class="flex items-center gap-1"><Layers :size="12" /> {{ inv.zoneNom }}</span>
              <span v-if="inv.categorieNom" class="flex items-center gap-1"><Tag :size="12" /> {{ inv.categorieNom }}</span>
              <span class="flex items-center gap-1"><Calendar :size="12" /> {{ formatDate(inv.datePlanifiee) }}</span>
            </div>
          </div>
          <div class="flex items-center gap-6 shrink-0">
            <div class="text-center hidden md:block">
              <div class="text-xl font-bold text-foreground">{{ inv.nbLignes }}</div>
              <div class="text-xs text-muted-foreground">Lignes</div>
            </div>
            <div v-if="inv.statut === 'TERMINE'" class="text-center hidden md:block">
              <div class="text-xl font-bold" :class="inv.nbEcarts > 0 ? 'text-amber-500' : 'text-emerald-600'">{{ inv.nbEcarts }}</div>
              <div class="text-xs text-muted-foreground">Écarts</div>
            </div>
            <div class="text-center hidden lg:block">
              <div class="text-sm font-medium text-foreground">{{ inv.creePar }}</div>
              <div class="text-xs text-muted-foreground">Créé par</div>
            </div>
          </div>
          <div class="flex items-center gap-2 shrink-0">
            <RouterLink
              v-if="inv.statut === 'PLANIFIE' || inv.statut === 'EN_COURS'"
              :to="`/inventories/${inv.id}/session`"
              class="flex items-center gap-1.5 px-3.5 py-2 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs font-semibold hover:bg-emerald-100 transition-colors">
              <Play :size="12" /> {{ inv.statut === 'PLANIFIE' ? 'Démarrer' : 'Continuer' }}
            </RouterLink>
            <RouterLink v-else :to="`/inventories/${inv.id}/session`"
              class="flex items-center gap-1.5 px-3.5 py-2 rounded-xl bg-muted border border-border text-muted-foreground text-xs font-medium hover:bg-muted/80 transition-colors">
              <Eye :size="12" /> Consulter
            </RouterLink>
          </div>
        </div>
      </div>

      <div v-if="!filtered.length" class="text-center py-20 text-muted-foreground">
        <ClipboardList :size="40" class="mx-auto mb-3 opacity-30" />
        <p class="font-medium">Aucun inventaire trouvé</p>
        <RouterLink to="/inventories/new" class="mt-3 inline-flex items-center gap-1.5 text-sm text-emerald-600 hover:underline">
          <Plus :size="14" /> Planifier le premier inventaire
        </RouterLink>
      </div>

      <div v-if="totalPages > 1" class="flex items-center justify-between px-4 py-3 text-sm text-muted-foreground">
        <span>Page {{ page + 1 }} / {{ totalPages }}</span>
        <div class="flex gap-2">
          <button :disabled="page === 0" @click="page--; load()" class="px-3 py-1.5 rounded-lg border border-border disabled:opacity-40 hover:bg-muted transition-colors">Préc.</button>
          <button :disabled="page >= totalPages - 1" @click="page++; load()" class="px-3 py-1.5 rounded-lg border border-border disabled:opacity-40 hover:bg-muted transition-colors">Suiv.</button>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { Plus, ClipboardList, Warehouse, Layers, Tag, Calendar, Eye, Play } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { inventoryService, type Inventory } from '@/services/operations.service'
import { formatDate } from '@/utils/formatters'
import { usePermissions } from '@/composables/usePermissions'

const { can } = usePermissions()
const items = ref<Inventory[]>([])
const loading = ref(false)
const error = ref('')
const filterStatut = ref('')
const filterType = ref('')
const page = ref(0)
const total = ref(0)
const totalPages = ref(0)

const filtered = computed(() => items.value.filter(i => {
  if (filterStatut.value && i.statut !== filterStatut.value) return false
  if (filterType.value && i.type !== filterType.value) return false
  return true
}))

async function load() {
  loading.value = true; error.value = ''
  try {
    const data = await inventoryService.findAll(page.value, 20)
    items.value = data.content
    total.value = data.totalElements
    totalPages.value = data.totalPages
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors du chargement des inventaires'
  } finally {
    loading.value = false
  }
}

function statutBg(s: string) {
  return ({ PLANIFIE: 'bg-blue-100', EN_COURS: 'bg-amber-100', TERMINE: 'bg-emerald-100', ANNULE: 'bg-gray-100' } as any)[s] || 'bg-muted'
}
function statutColor(s: string) {
  return ({ PLANIFIE: 'text-blue-600', EN_COURS: 'text-amber-600', TERMINE: 'text-emerald-600', ANNULE: 'text-gray-500' } as any)[s] || 'text-muted-foreground'
}

onMounted(load)
</script>
