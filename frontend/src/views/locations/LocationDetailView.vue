<template>
  <div class="p-6 max-w-[800px] mx-auto">
    <div v-if="loading" class="flex justify-center py-20">
      <div class="w-8 h-8 border-4 border-emerald-500 border-t-transparent rounded-full animate-spin" />
    </div>

    <div v-else-if="error" class="card-premium rounded-2xl p-8 text-center text-red-500">
      <p class="font-medium">{{ error }}</p>
      <button @click="load" class="mt-3 text-sm underline">Réessayer</button>
    </div>

    <template v-else-if="loc">
      <PageHeader :title="loc.code" subtitle="Détail de l'emplacement" back="Emplacements" />
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="card-premium rounded-2xl p-5 space-y-4">
          <h3 class="font-semibold text-foreground">Informations</h3>
          <div class="space-y-2.5 text-sm">
            <div class="flex justify-between"><span class="text-muted-foreground">Zone</span><span class="font-medium">{{ loc.zoneNom || '—' }}</span></div>
            <div class="flex justify-between"><span class="text-muted-foreground">Rayon</span><span class="font-medium">{{ loc.rayon || '—' }}</span></div>
            <div class="flex justify-between"><span class="text-muted-foreground">Étagère</span><span class="font-medium">{{ loc.etagere || '—' }}</span></div>
            <div class="flex justify-between"><span class="text-muted-foreground">Position</span><span class="font-medium">{{ loc.position || '—' }}</span></div>
            <div class="flex justify-between"><span class="text-muted-foreground">Capacité</span><span class="font-medium">{{ loc.capaciteKg }} kg</span></div>
            <div class="flex justify-between"><span class="text-muted-foreground">Poids actuel</span><span class="font-medium">{{ loc.poidsActuel }} kg</span></div>
            <div class="flex justify-between"><span class="text-muted-foreground">Statut</span><StatusBadge :status="String(loc.statut).toLowerCase()" :dot="true" /></div>
          </div>
        </div>
        <div class="card-premium rounded-2xl p-5">
          <h3 class="font-semibold text-foreground mb-4">Contenu</h3>
          <div v-if="loc.produitNom" class="p-4 rounded-xl bg-muted/50 border border-border">
            <div class="font-semibold text-foreground">{{ loc.produitNom }}</div>
            <div class="text-xs text-muted-foreground font-mono mt-1">Produit #{{ loc.produitId }}</div>
          </div>
          <div v-else class="text-center py-8 text-sm text-muted-foreground">Emplacement vide</div>
          <div class="mt-4 p-3 rounded-xl bg-muted/30 border border-border">
            <div class="text-xs text-muted-foreground mb-1 font-medium">Code emplacement</div>
            <div class="font-mono text-sm font-bold text-foreground">{{ loc.code }}</div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import WarehouseService, { type Location } from '@/services/warehouse.service'
const route = useRoute()
const loc = ref<Location | null>(null)
const loading = ref(true)
const error = ref('')

async function load() {
  loading.value = true
  error.value = ''
  try {
    loc.value = await WarehouseService.getLocationById(Number(route.params.id))
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors du chargement de l’emplacement'
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>
