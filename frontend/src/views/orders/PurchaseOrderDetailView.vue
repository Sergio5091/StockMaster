<template>
  <div class="p-6 max-w-[1000px] mx-auto">
    <PageHeader :title="order.numero" :subtitle="`Commande · ${order.fournisseurNom}`" back="Commandes fournisseurs">
      <template #actions>
        <StatusBadge :status="order.statut" :dot="true" />
        <button v-if="can('manage_orders') && order.statut === 'BROUILLON'" @click="validate" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white"><CheckCircle2 :size="15" /> Valider</button>
        <button v-if="can('manage_orders') && order.statut === 'VALIDEE'" @click="send" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white"><Send :size="15" /> Envoyer</button>
      </template>
    </PageHeader>
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div class="card-premium rounded-2xl p-5 space-y-3 text-sm">
        <div class="flex justify-between"><span class="text-muted-foreground">N°</span><span class="font-mono font-bold">{{ order.numero }}</span></div>
        <div class="flex justify-between"><span class="text-muted-foreground">Fournisseur</span><span class="font-medium">{{ order.fournisseurNom }}</span></div>
        <div class="flex justify-between"><span class="text-muted-foreground">Entrepôt</span><span class="font-medium">{{ order.entrepotNom }}</span></div>
        <div class="flex justify-between"><span class="text-muted-foreground">Commande</span><span>{{ formatDate(order.dateCommande) }}</span></div>
        <div v-if="order.dateLivraisonPrevue" class="flex justify-between">
          <span class="text-muted-foreground">Livraison prévue</span>
          <span :class="isLate ? 'text-red-500 font-semibold' : ''">{{ formatDate(order.dateLivraisonPrevue) }}{{ isLate ? ' ⚠' : '' }}</span>
        </div>
        <div class="flex justify-between"><span class="text-muted-foreground">Créé par</span><span>{{ order.creePar }}</span></div>
        <div class="flex justify-between pt-2 border-t border-border"><span class="text-muted-foreground">Montant total</span><span class="font-bold text-foreground">{{ formatCurrency(order.montantTotal) }}</span></div>
        <div v-if="order.note" class="pt-2 border-t border-border"><span class="text-xs text-muted-foreground">Note: </span><span>{{ order.note }}</span></div>
      </div>
      <div class="lg:col-span-2">
        <div class="card-premium rounded-2xl overflow-hidden">
          <div class="px-5 py-4 border-b border-border"><h3 class="font-semibold">Lignes de commande ({{ order.lignes.length }})</h3></div>
          <table class="w-full text-sm">
            <thead><tr class="border-b border-border bg-muted/30">
              <th class="text-left px-4 py-3 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Produit</th>
              <th class="text-right px-4 py-3 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Commandée</th>
              <th class="text-right px-4 py-3 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Reçue</th>
              <th class="text-right px-4 py-3 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Montant</th>
            </tr></thead>
            <tbody>
              <tr v-for="(l, i) in order.lignes" :key="i" class="border-b border-border/50 last:border-0">
                <td class="px-4 py-3 font-medium text-foreground">{{ l.produit }}</td>
                <td class="px-4 py-3 text-right text-muted-foreground">{{ l.qteCommandee }}</td>
                <td class="px-4 py-3 text-right font-bold text-foreground">{{ l.qteRecue }}</td>
                <td class="px-4 py-3 text-right font-semibold text-foreground">{{ formatCurrency(l.montant) }}</td>
              </tr>
            </tbody>
          </table>
          <div v-if="!order.lignes.length" class="text-center py-12 text-sm text-muted-foreground">Aucune ligne de commande</div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { formatDate, formatCurrency } from '@/utils/formatters'
import { purchaseOrderService } from '@/services/operations.service'
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { CheckCircle2, Send } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { usePermissions } from '@/composables/usePermissions'
const { can } = usePermissions()
const route = useRoute()
const order = ref<any>({ numero: '', fournisseurNom: '', entrepotNom: '', statut: '', dateCommande: '', creePar: '', montantTotal: 0, lignes: [] })
const loading = ref(true)
onMounted(async () => {
  try {
    order.value = await purchaseOrderService.findById(Number(route.params.id))
  } finally {
    loading.value = false
  }
})
const isLate = computed(() =>
  order.value.dateLivraisonPrevue && ['BROUILLON','VALIDEE','ENVOYEE'].includes(order.value.statut)
    ? new Date(order.value.dateLivraisonPrevue) < new Date()
    : false
)
async function validate() {
  try { order.value = await purchaseOrderService.validate(Number(route.params.id)) } catch {}
}
async function send() {
  try { order.value = await purchaseOrderService.send(Number(route.params.id)) } catch {}
}
</script>
