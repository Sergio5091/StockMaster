<template>
  <div class="p-6 max-w-[1000px] mx-auto">
    <PageHeader :title="receipt.numero" :subtitle="`Bon de réception · ${receipt.fournisseurNom}`" back="Bons de réception">
      <template #actions>
        <StatusBadge :status="receipt.statut" :dot="true" />
        <button v-if="can('validate_receipt') && receipt.statut === 'EN_ATTENTE_VALIDATION'" @click="validate" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white"><CheckCircle2 :size="15" /> Valider</button>
        <button v-if="can('validate_receipt') && receipt.statut === 'EN_ATTENTE_VALIDATION'" @click="reject" class="flex items-center gap-2 px-4 py-2.5 rounded-xl border border-red-300 text-red-600 bg-red-50 text-sm font-semibold hover:bg-red-100 transition-colors"><XCircle :size="15" /> Rejeter</button>
      </template>
    </PageHeader>
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div class="space-y-4">
        <div class="card-premium rounded-2xl p-5 space-y-3 text-sm">
          <div class="flex justify-between"><span class="text-muted-foreground">N° bon</span><span class="font-mono font-bold">{{ receipt.numero }}</span></div>
          <div class="flex justify-between"><span class="text-muted-foreground">Fournisseur</span><span class="font-medium">{{ receipt.fournisseurNom }}</span></div>
          <div class="flex justify-between"><span class="text-muted-foreground">Entrepôt</span><span class="font-medium">{{ receipt.entrepotNom }}</span></div>
          <div class="flex justify-between"><span class="text-muted-foreground">Date réception</span><span>{{ formatDate(receipt.dateReception) }}</span></div>
          <div class="flex justify-between"><span class="text-muted-foreground">Créé par</span><span>{{ receipt.creePar }}</span></div>
          <div v-if="receipt.valideePar" class="flex justify-between"><span class="text-muted-foreground">Validé par</span><span class="text-emerald-600 font-medium">{{ receipt.valideePar }}</span></div>
          <div v-if="receipt.note" class="pt-2 border-t border-border"><span class="text-muted-foreground text-xs">Note: </span><span>{{ receipt.note }}</span></div>
        </div>
        <div class="card-premium rounded-2xl p-5">
          <h3 class="font-semibold mb-4 text-xs uppercase tracking-wide text-muted-foreground">Progression</h3>
          <div class="space-y-3">
            <div v-for="step in timeline" :key="step.label" class="flex items-center gap-3">
              <div :class="['w-6 h-6 rounded-full flex items-center justify-center shrink-0 text-xs', step.done ? 'bg-emerald-500 text-white' : 'bg-muted border-2 border-border']">
                <Check v-if="step.done" :size="12" /><span v-else class="text-muted-foreground/40">·</span>
              </div>
              <span :class="['text-sm', step.done ? 'font-medium text-foreground' : 'text-muted-foreground']">{{ step.label }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="lg:col-span-2">
        <div class="card-premium rounded-2xl overflow-hidden">
          <div class="px-5 py-4 border-b border-border"><h3 class="font-semibold">Lignes ({{ receipt.lignes.length }})</h3></div>
          <table class="w-full text-sm">
            <thead><tr class="border-b border-border bg-muted/30">
              <th class="text-left px-4 py-3 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Produit</th>
              <th class="text-right px-4 py-3 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Attendue</th>
              <th class="text-right px-4 py-3 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Reçue</th>
              <th class="text-center px-4 py-3 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Qualité</th>
            </tr></thead>
            <tbody>
              <tr v-for="(l, i) in receipt.lignes" :key="i" class="border-b border-border/50 last:border-0">
                <td class="px-4 py-3 font-medium text-foreground">{{ l.produit }}</td>
                <td class="px-4 py-3 text-right text-muted-foreground">{{ l.qteAttendue }}</td>
                <td class="px-4 py-3 text-right font-bold" :class="l.qteRecue < l.qteAttendue ? 'text-amber-500' : 'text-foreground'">{{ l.qteRecue }}</td>
                <td class="px-4 py-3 text-center">
                  <span :class="['inline-flex items-center gap-1 text-xs font-medium px-2 py-0.5 rounded-full', l.qualiteOk ? 'bg-emerald-100 text-emerald-700' : 'bg-red-100 text-red-600']">
                    <Check v-if="l.qualiteOk" :size="10" /><X v-else :size="10" /> {{ l.qualiteOk ? 'OK' : 'NOK' }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-if="!receipt.lignes.length" class="text-center py-12 text-sm text-muted-foreground">Aucune ligne</div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { formatDate } from '@/utils/formatters'
import { receiptService, type Receipt } from '@/services/operations.service'
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { CheckCircle2, XCircle, Check, X } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { usePermissions } from '@/composables/usePermissions'
const { can } = usePermissions()
const route = useRoute()
const receipt = ref<Receipt | null>(null)
onMounted(async () => {
  receipt.value = await receiptService.findById(Number(route.params.id))
})
const timeline = computed(() => receipt.value ? [{ label: 'Brouillon créé', done: true }, { label: 'Soumis pour validation', done: receipt.value.statut !== 'BROUILLON' }, { label: 'Validé', done: receipt.value.statut === 'VALIDE' }] : [])
function validate() { receipt.value.statut = 'VALIDE' }
function reject() { receipt.value.statut = 'REJETE' }
</script>
