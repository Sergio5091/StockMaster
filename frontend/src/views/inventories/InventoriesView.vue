<template>
  <div class="p-6 max-w-[1200px] mx-auto">
    <PageHeader title="Inventaires" :subtitle="`${INVENTORIES.length} inventaire(s)`">
      <template #actions>
        <RouterLink v-if="can('launch_inventory')" to="/inventories/new" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white">
          <Plus :size="15" /> Planifier un inventaire
        </RouterLink>
      </template>
    </PageHeader>

    <!-- Filters -->
    <div class="card-premium rounded-2xl p-4 mb-6 flex flex-wrap gap-3 items-center">
      <select v-model="filterStatut" class="select-field w-auto min-w-[160px]">
        <option value="">Tous les statuts</option>
        <option value="PLANIFIE">Planifié</option>
        <option value="EN_COURS">En cours</option>
        <option value="TERMINE">Terminé</option>
        <option value="ANNULE">Annulé</option>
      </select>
      <select v-model="filterType" class="select-field w-auto min-w-[140px]">
        <option value="">Tous les types</option>
        <option value="COMPLET">Complet</option>
        <option value="PARTIEL">Partiel</option>
      </select>
    </div>

    <!-- List -->
    <div class="space-y-4">
      <div v-for="inv in filtered" :key="inv.id" class="card-premium rounded-2xl overflow-hidden hover:shadow-md transition-all">
        <div class="flex items-center gap-5 px-5 py-4">
          <!-- Icon -->
          <div :class="['w-12 h-12 rounded-xl flex items-center justify-center shrink-0', statutBg(inv.statut)]">
            <ClipboardList :size="20" :class="statutColor(inv.statut)" />
          </div>

          <!-- Main info -->
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2 mb-1 flex-wrap">
              <span class="font-bold text-foreground font-mono">{{ inv.numero }}</span>
              <StatusBadge :status="inv.statut" :dot="true" />
              <span class="text-xs px-2 py-0.5 rounded-full bg-muted border border-border text-muted-foreground font-medium">{{ inv.type }}</span>
            </div>
            <div class="flex items-center gap-4 text-sm text-muted-foreground flex-wrap">
              <span class="flex items-center gap-1"><Warehouse :size="12" /> {{ inv.entrepotNom }}</span>
              <span v-if="inv.zoneNom" class="flex items-center gap-1"><Layers :size="12" /> {{ inv.zoneNom }}</span>
              <span v-if="(inv as any).categorieNom" class="flex items-center gap-1"><Tag :size="12" /> {{ (inv as any).categorieNom }}</span>
              <span class="flex items-center gap-1"><Calendar :size="12" /> {{ formatDate(inv.datePlanifiee) }}</span>
            </div>
          </div>

          <!-- Stats -->
          <div class="flex items-center gap-6 shrink-0">
            <div class="text-center hidden md:block">
              <div class="text-xl font-bold text-foreground">{{ inv.lignes }}</div>
              <div class="text-xs text-muted-foreground">Lignes</div>
            </div>
            <div v-if="inv.statut === 'TERMINE'" class="text-center hidden md:block">
              <div class="text-xl font-bold" :class="inv.ecarts > 0 ? 'text-amber-500' : 'text-emerald-600'">{{ inv.ecarts }}</div>
              <div class="text-xs text-muted-foreground">Écarts</div>
            </div>
            <div class="text-center hidden lg:block">
              <div class="text-sm font-medium text-foreground">{{ inv.creePar.split(' ')[0] }}</div>
              <div class="text-xs text-muted-foreground">Créé par</div>
            </div>
          </div>

          <!-- Actions -->
          <div class="flex items-center gap-2 shrink-0">
            <RouterLink
              v-if="inv.statut === 'PLANIFIE' || inv.statut === 'EN_COURS'"
              :to="`/inventories/${inv.id}/session`"
              class="flex items-center gap-1.5 px-3.5 py-2 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs font-semibold hover:bg-emerald-100 transition-colors">
              <Play :size="12" /> {{ inv.statut === 'PLANIFIE' ? 'Démarrer' : 'Continuer' }}
            </RouterLink>
            <RouterLink
              v-else
              :to="`/inventories/${inv.id}/session`"
              class="flex items-center gap-1.5 px-3.5 py-2 rounded-xl bg-muted border border-border text-muted-foreground text-xs font-medium hover:bg-muted/80 transition-colors">
              <Eye :size="12" /> Consulter
            </RouterLink>
          </div>
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
  </div>
</template>
<script setup lang="ts">
import { ref, computed } from 'vue'
import { RouterLink } from 'vue-router'
import { Plus, ClipboardList, Warehouse, Layers, Tag, Calendar, Eye, Play } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { INVENTORIES, formatDate } from '@/services/mockData'
import { usePermissions } from '@/composables/usePermissions'

const { can } = usePermissions()
const filterStatut = ref('')
const filterType = ref('')

const filtered = computed(() => INVENTORIES.filter(i => {
  if (filterStatut.value && i.statut !== filterStatut.value) return false
  if (filterType.value && i.type !== filterType.value) return false
  return true
}))

function statutBg(s: string) {
  return { PLANIFIE: 'bg-blue-100', EN_COURS: 'bg-amber-100', TERMINE: 'bg-emerald-100', ANNULE: 'bg-gray-100' }[s] || 'bg-muted'
}
function statutColor(s: string) {
  return { PLANIFIE: 'text-blue-600', EN_COURS: 'text-amber-600', TERMINE: 'text-emerald-600', ANNULE: 'text-gray-500' }[s] || 'text-muted-foreground'
}
</script>
