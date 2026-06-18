<template>
  <div class="p-6 max-w-[1200px] mx-auto">
    <PageHeader title="Emplacements" :subtitle="`Zone ${zoneId}`" back="Zones">
      <template #actions>
        <button v-if="can('manage_locations')" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white"><Plus :size="15" /> Nouvel emplacement</button>
      </template>
    </PageHeader>
    <div class="card-premium rounded-2xl overflow-hidden">
      <table class="w-full text-sm">
        <thead><tr class="border-b border-border bg-muted/30">
          <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Code</th>
          <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden md:table-cell">Rayon / Étagère</th>
          <th class="text-right px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden md:table-cell">Poids</th>
          <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Statut</th>
          <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden lg:table-cell">Contenu</th>
          <th class="px-4 py-3.5"></th>
        </tr></thead>
        <tbody>
          <tr v-for="loc in locations" :key="loc.id" class="table-row-hover border-b border-border/50 last:border-0">
            <td class="px-4 py-3.5 font-mono text-xs font-bold text-foreground">{{ loc.code }}</td>
            <td class="px-4 py-3.5 hidden md:table-cell text-sm text-muted-foreground">{{ loc.rayon }} / {{ loc.etagere }} / {{ loc.position }}</td>
            <td class="px-4 py-3.5 text-right hidden md:table-cell text-sm text-muted-foreground">{{ loc.poidsActuel }} / {{ loc.capaciteKg }} kg</td>
            <td class="px-4 py-3.5"><StatusBadge :status="loc.statut" :dot="true" /></td>
            <td class="px-4 py-3.5 hidden lg:table-cell text-sm text-muted-foreground">{{ loc.contenu?.produit || '—' }}</td>
            <td class="px-4 py-3.5"><RouterLink :to="`/locations/${loc.id}`" class="p-1.5 rounded-lg hover:bg-muted text-muted-foreground hover:text-foreground transition-colors inline-flex"><Eye :size="14" /></RouterLink></td>
          </tr>
        </tbody>
      </table>
      <div v-if="!locations.length" class="text-center py-16 text-muted-foreground"><MapPin :size="36" class="mx-auto mb-3 opacity-30" /><p>Aucun emplacement</p></div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { Plus, Eye, MapPin } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { LOCATIONS } from '@/services/mockData'
import { usePermissions } from '@/composables/usePermissions'
const { can } = usePermissions()
const route = useRoute()
const zoneId = Number(route.params.id)
const locations = computed(() => LOCATIONS.filter(l => l.zoneId === zoneId))
</script>
