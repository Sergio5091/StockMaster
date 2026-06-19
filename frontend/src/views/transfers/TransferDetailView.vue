<template>
  <div class="p-6 max-w-[1000px] mx-auto">
    <PageHeader :title="transfer.numero" subtitle="Transfert inter-entrepôts" back="Transferts">
      <template #actions>
        <StatusBadge :status="transfer.statut" :dot="true" />
        <button v-if="can('validate_transfer') && transfer.statut === 'BROUILLON'" @click="transfer.statut = 'EXPEDIE'" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white"><Truck :size="15" /> Expédier</button>
        <button v-if="can('validate_transfer') && transfer.statut === 'EXPEDIE'" @click="transfer.statut = 'RECU'" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white"><CheckCircle2 :size="15" /> Marquer reçu</button>
      </template>
    </PageHeader>
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div class="card-premium rounded-2xl p-5 space-y-3 text-sm">
        <div class="flex justify-between"><span class="text-muted-foreground">N°</span><span class="font-mono font-bold">{{ transfer.numero }}</span></div>
        <div class="flex justify-between"><span class="text-muted-foreground">Source</span><span class="font-medium">{{ transfer.entrepotSourceNom }}</span></div>
        <div class="flex justify-between"><span class="text-muted-foreground">Destination</span><span class="font-medium">{{ transfer.entrepotDestNom }}</span></div>
        <div v-if="transfer.dateExpedition" class="flex justify-between"><span class="text-muted-foreground">Expédition</span><span>{{ formatDate(transfer.dateExpedition) }}</span></div>
        <div v-if="transfer.dateReception" class="flex justify-between"><span class="text-muted-foreground">Réception</span><span>{{ formatDate(transfer.dateReception) }}</span></div>
        <div class="flex justify-between"><span class="text-muted-foreground">Créé par</span><span>{{ transfer.creePar }}</span></div>
        <div v-if="transfer.note" class="pt-2 border-t border-border"><span class="text-xs text-muted-foreground">Note: </span><span>{{ transfer.note }}</span></div>
      </div>
      <div class="lg:col-span-2">
        <div class="card-premium rounded-2xl overflow-hidden">
          <div class="px-5 py-4 border-b border-border"><h3 class="font-semibold">Lignes ({{ transfer.lignes.length }})</h3></div>
          <table class="w-full text-sm">
            <thead><tr class="border-b border-border bg-muted/30">
              <th class="text-left px-4 py-3 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Produit</th>
              <th class="text-right px-4 py-3 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Demandée</th>
              <th class="text-right px-4 py-3 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Reçue</th>
            </tr></thead>
            <tbody>
              <tr v-for="(l, i) in transfer.lignes" :key="i" class="border-b border-border/50 last:border-0">
                <td class="px-4 py-3 font-medium text-foreground">{{ l.produit }}</td>
                <td class="px-4 py-3 text-right text-muted-foreground">{{ l.qteDemandee }}</td>
                <td class="px-4 py-3 text-right font-bold" :class="l.qteRecue !== null ? 'text-emerald-600' : 'text-muted-foreground'">{{ l.qteRecue ?? '—' }}</td>
              </tr>
            </tbody>
          </table>
          <div v-if="!transfer.lignes.length" class="text-center py-12 text-sm text-muted-foreground">Aucune ligne</div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { formatDate } from '@/utils/formatters'
import { transferService, type Transfer } from '@/services/operations.service'
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Truck, CheckCircle2 } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { usePermissions } from '@/composables/usePermissions'
const { can } = usePermissions()
const route = useRoute()
const transfer = ref<Transfer | null>(null)
onMounted(async () => {
  transfer.value = await transferService.findById(Number(route.params.id))
})
</script>
