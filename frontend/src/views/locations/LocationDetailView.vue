<template>
  <div class="p-6 max-w-[800px] mx-auto">
    <PageHeader :title="loc.code" subtitle="Détail de l'emplacement" back="Emplacements" />
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <div class="card-premium rounded-2xl p-5 space-y-4">
        <h3 class="font-semibold text-foreground">Informations</h3>
        <div class="space-y-2.5 text-sm">
          <div class="flex justify-between"><span class="text-muted-foreground">Rayon</span><span class="font-medium">{{ loc.rayon }}</span></div>
          <div class="flex justify-between"><span class="text-muted-foreground">Étagère</span><span class="font-medium">{{ loc.etagere }}</span></div>
          <div class="flex justify-between"><span class="text-muted-foreground">Position</span><span class="font-medium">{{ loc.position }}</span></div>
          <div class="flex justify-between"><span class="text-muted-foreground">Capacité</span><span class="font-medium">{{ loc.capaciteKg }} kg</span></div>
          <div class="flex justify-between"><span class="text-muted-foreground">Poids actuel</span><span class="font-medium">{{ loc.poidsActuel }} kg</span></div>
          <div class="flex justify-between"><span class="text-muted-foreground">Statut</span><StatusBadge :status="loc.statut" :dot="true" /></div>
        </div>
      </div>
      <div class="card-premium rounded-2xl p-5">
        <h3 class="font-semibold text-foreground mb-4">Contenu</h3>
        <div v-if="loc.contenu" class="p-4 rounded-xl bg-muted/50 border border-border">
          <div class="font-semibold text-foreground">{{ loc.contenu.produit }}</div>
          <div class="text-xs text-muted-foreground font-mono mt-1">{{ loc.contenu.reference }}</div>
          <div class="text-2xl font-bold text-emerald-600 mt-2">{{ loc.contenu.quantite }} unités</div>
        </div>
        <div v-else class="text-center py-8 text-sm text-muted-foreground">Emplacement vide</div>
        <div class="mt-4 p-3 rounded-xl bg-muted/30 border border-border">
          <div class="text-xs text-muted-foreground mb-1 font-medium">QR Code</div>
          <div class="font-mono text-sm font-bold text-foreground">{{ loc.qrCode }}</div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { useRoute } from 'vue-router'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { LOCATIONS } from '@/services/mockData'
const route = useRoute()
const loc = LOCATIONS.find(l => l.id === Number(route.params.id)) || LOCATIONS[0]
</script>
