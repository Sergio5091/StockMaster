<template>
  <div class="p-6 max-w-[1000px] mx-auto">
    <div v-if="loading" class="flex justify-center py-20">
      <div class="w-8 h-8 border-4 border-emerald-500 border-t-transparent rounded-full animate-spin" />
    </div>

    <div v-else-if="error" class="card-premium rounded-2xl p-8 text-center text-red-500">
      <p class="font-medium">{{ error }}</p>
      <button @click="load" class="mt-3 text-sm underline">Réessayer</button>
    </div>

    <template v-else-if="transfer">
      <PageHeader :title="transfer.numero" subtitle="Transfert inter-entrepôts" back="Transferts">
        <template #actions>
          <StatusBadge :status="transfer.statut" :dot="true" />

          <button
            v-if="transfer.statut === 'BROUILLON'"
            @click="ship"
            :disabled="actionLoading"
            class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white disabled:opacity-60">
            <Truck :size="15" /> {{ actionLoading ? '…' : 'Expédier' }}
          </button>

          <button
            v-if="transfer.statut === 'EXPEDIE'"
            @click="receive"
            :disabled="actionLoading"
            class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white disabled:opacity-60">
            <CheckCircle2 :size="15" /> {{ actionLoading ? '…' : 'Marquer reçu' }}
          </button>

          <button
            v-if="['BROUILLON', 'EXPEDIE'].includes(transfer.statut)"
            @click="cancel"
            :disabled="actionLoading"
            class="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-red-50 border border-red-200 text-red-600 text-sm font-medium hover:bg-red-100 transition-colors disabled:opacity-60">
            <XCircle :size="15" /> Annuler
          </button>
        </template>
      </PageHeader>

      <!-- Erreur action -->
      <div v-if="actionError" class="mb-4 p-3 rounded-xl bg-red-50 border border-red-200 text-red-600 text-sm">
        {{ actionError }}
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Infos générales -->
        <div class="card-premium rounded-2xl p-5 space-y-3 text-sm">
          <div class="flex justify-between">
            <span class="text-muted-foreground">N°</span>
            <span class="font-mono font-bold">{{ transfer.numero }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-muted-foreground">Source</span>
            <span class="font-medium">{{ transfer.entrepotSourceNom }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-muted-foreground">Destination</span>
            <span class="font-medium">{{ transfer.entrepotDestinationNom }}</span>
          </div>
          <div v-if="transfer.dateExpedition" class="flex justify-between">
            <span class="text-muted-foreground">Expédition</span>
            <span>{{ formatDate(transfer.dateExpedition) }}</span>
          </div>
          <div v-if="transfer.dateReception" class="flex justify-between">
            <span class="text-muted-foreground">Réception</span>
            <span>{{ formatDate(transfer.dateReception) }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-muted-foreground">Créé par</span>
            <span>{{ transfer.creePar }}</span>
          </div>
          <div v-if="transfer.expedieePar" class="flex justify-between">
            <span class="text-muted-foreground">Expédié par</span>
            <span>{{ transfer.expedieePar }}</span>
          </div>
          <div v-if="transfer.recuePar" class="flex justify-between">
            <span class="text-muted-foreground">Reçu par</span>
            <span>{{ transfer.recuePar }}</span>
          </div>
          <div v-if="transfer.note" class="pt-2 border-t border-border">
            <span class="text-xs text-muted-foreground">Note : </span>
            <span>{{ transfer.note }}</span>
          </div>
        </div>

        <!-- Lignes -->
        <div class="lg:col-span-2">
          <div class="card-premium rounded-2xl overflow-hidden">
            <div class="px-5 py-4 border-b border-border">
              <h3 class="font-semibold">Lignes ({{ transfer.lignes?.length ?? 0 }})</h3>
            </div>
            <table class="w-full text-sm">
              <thead>
                <tr class="border-b border-border bg-muted/30">
                  <th class="text-left px-4 py-3 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Produit</th>
                  <th class="text-right px-4 py-3 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Demandée</th>
                  <th class="text-right px-4 py-3 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Reçue</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="l in transfer.lignes" :key="l.id" class="border-b border-border/50 last:border-0">
                  <td class="px-4 py-3 font-medium text-foreground">{{ l.produitNom }}</td>
                  <td class="px-4 py-3 text-right text-muted-foreground">{{ l.quantiteDemandee }}</td>
                  <td class="px-4 py-3 text-right font-bold"
                    :class="l.quantiteRecue != null ? 'text-emerald-600' : 'text-muted-foreground'">
                    {{ l.quantiteRecue ?? '—' }}
                  </td>
                </tr>
              </tbody>
            </table>
            <div v-if="!transfer.lignes?.length" class="text-center py-12 text-sm text-muted-foreground">
              Aucune ligne
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Truck, CheckCircle2, XCircle } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { transferService, type Transfer } from '@/services/operations.service'
import { formatDate } from '@/utils/formatters'

const route = useRoute()
const router = useRouter()

const transfer = ref<Transfer | null>(null)
const loading = ref(true)
const actionLoading = ref(false)
const error = ref('')
const actionError = ref('')

async function load() {
  loading.value = true
  error.value = ''
  try {
    transfer.value = await transferService.findById(Number(route.params.id))
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors du chargement du transfert'
  } finally {
    loading.value = false
  }
}

async function ship() {
  actionLoading.value = true
  actionError.value = ''
  try {
    transfer.value = await transferService.ship(Number(route.params.id))
  } catch (e: any) {
    actionError.value = e?.response?.data?.message || 'Erreur lors de l\'expédition'
  } finally {
    actionLoading.value = false
  }
}

async function receive() {
  actionLoading.value = true
  actionError.value = ''
  try {
    transfer.value = await transferService.receive(Number(route.params.id))
  } catch (e: any) {
    actionError.value = e?.response?.data?.message || 'Erreur lors de la réception'
  } finally {
    actionLoading.value = false
  }
}

async function cancel() {
  if (!confirm('Confirmer l\'annulation de ce transfert ?')) return
  actionLoading.value = true
  actionError.value = ''
  try {
    transfer.value = await transferService.cancel(Number(route.params.id))
  } catch (e: any) {
    actionError.value = e?.response?.data?.message || 'Erreur lors de l\'annulation'
  } finally {
    actionLoading.value = false
  }
}

onMounted(load)
</script>
