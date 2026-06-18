<template>
  <div class="p-6 max-w-[1000px] mx-auto">
    <PageHeader :title="issue.numero" :subtitle="`Bon de sortie · ${issue.motif.replace('_',' ')}`" back="Bons de sortie">
      <template #actions>
        <StatusBadge :status="issue.statut" :dot="true" />
        <button v-if="can('validate_issue') && issue.statut === 'EN_ATTENTE_VALIDATION'" @click="validate" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white"><CheckCircle2 :size="15" /> Valider</button>
        <button v-if="can('validate_issue') && issue.statut === 'EN_ATTENTE_VALIDATION'" @click="cancel" class="flex items-center gap-2 px-4 py-2.5 rounded-xl border border-red-300 text-red-600 bg-red-50 text-sm font-semibold hover:bg-red-100 transition-colors"><XCircle :size="15" /> Annuler</button>
      </template>
    </PageHeader>
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div class="card-premium rounded-2xl p-5 space-y-3 text-sm">
        <div class="flex justify-between"><span class="text-muted-foreground">N° bon</span><span class="font-mono font-bold">{{ issue.numero }}</span></div>
        <div class="flex justify-between"><span class="text-muted-foreground">Entrepôt</span><span class="font-medium">{{ issue.entrepotNom }}</span></div>
        <div class="flex justify-between"><span class="text-muted-foreground">Motif</span><span class="font-medium">{{ issue.motif.replace('_',' ') }}</span></div>
        <div v-if="issue.clientNom" class="flex justify-between"><span class="text-muted-foreground">Client</span><span class="font-medium">{{ issue.clientNom }}</span></div>
        <div class="flex justify-between"><span class="text-muted-foreground">Date</span><span>{{ formatDate(issue.dateSortie) }}</span></div>
        <div class="flex justify-between"><span class="text-muted-foreground">Créé par</span><span>{{ issue.creePar }}</span></div>
        <div v-if="issue.note" class="pt-2 border-t border-border"><span class="text-xs text-muted-foreground">Note: </span><span>{{ issue.note }}</span></div>
      </div>
      <div class="lg:col-span-2">
        <div class="card-premium rounded-2xl overflow-hidden">
          <div class="px-5 py-4 border-b border-border flex items-center justify-between">
            <h3 class="font-semibold">Lignes ({{ issue.lignes.length }})</h3>
            <span class="text-sm font-bold text-foreground">{{ formatCurrency(issue.montant) }}</span>
          </div>
          <table class="w-full text-sm">
            <thead><tr class="border-b border-border bg-muted/30">
              <th class="text-left px-4 py-3 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Produit</th>
              <th class="text-right px-4 py-3 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Demandée</th>
              <th class="text-right px-4 py-3 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Sortie</th>
            </tr></thead>
            <tbody>
              <tr v-for="(l, i) in issue.lignes" :key="i" class="border-b border-border/50 last:border-0">
                <td class="px-4 py-3 font-medium text-foreground">{{ l.produit }}</td>
                <td class="px-4 py-3 text-right text-muted-foreground">{{ l.qteDemandee }}</td>
                <td class="px-4 py-3 text-right font-bold text-foreground">{{ l.qteSortie }}</td>
              </tr>
            </tbody>
          </table>
          <div v-if="!issue.lignes.length" class="text-center py-12 text-sm text-muted-foreground">Aucune ligne</div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { CheckCircle2, XCircle } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { ISSUES, formatDate, formatCurrency } from '@/services/mockData'
import { usePermissions } from '@/composables/usePermissions'
const { can } = usePermissions()
const route = useRoute()
const issue = ref({ ...(ISSUES.find(i => i.id === Number(route.params.id)) || ISSUES[0]) })
function validate() { issue.value.statut = 'VALIDE' }
function cancel() { issue.value.statut = 'ANNULE' }
</script>
