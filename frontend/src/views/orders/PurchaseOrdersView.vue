<template>
  <div class="p-6 max-w-[1400px] mx-auto">
    <PageHeader title="Commandes fournisseurs" :subtitle="`${total} commande(s)`">
      <template #actions>
        <RouterLink v-if="can('manage_orders')" to="/purchase-orders/new" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white">
          <Plus :size="15" /> Nouvelle commande
        </RouterLink>
      </template>
    </PageHeader>

    <!-- KPIs -->
    <div class="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
      <div class="kpi-card" v-for="kpi in kpis" :key="kpi.label">
        <div :class="['text-xs font-semibold uppercase tracking-wide mb-1', kpi.color]">{{ kpi.label }}</div>
        <div :class="['text-3xl font-bold', kpi.color]">{{ kpi.count }}</div>
      </div>
    </div>

    <div class="card-premium rounded-2xl p-4 mb-6 flex flex-wrap gap-3 items-center">
      <div class="relative flex-1 min-w-[200px]">
        <Search :size="15" class="absolute left-3.5 top-1/2 -translate-y-1/2 text-muted-foreground" />
        <input v-model="search" type="search" placeholder="N° commande, fournisseur…" class="input-field pl-10" @input="onSearch" />
      </div>
      <select v-model="filterStatut" class="select-field w-auto min-w-[200px]" @change="load">
        <option value="">Tous les statuts</option>
        <option value="BROUILLON">Brouillon</option>
        <option value="VALIDEE">Validée</option>
        <option value="ENVOYEE">Envoyée</option>
        <option value="PARTIELLEMENT_LIVREE">Partiellement livrée</option>
        <option value="LIVREE">Livrée</option>
        <option value="ANNULEE">Annulée</option>
      </select>
    </div>

    <div v-if="loading" class="flex justify-center py-20">
      <div class="w-8 h-8 border-4 border-emerald-500 border-t-transparent rounded-full animate-spin" />
    </div>
    <div v-else-if="error" class="card-premium rounded-2xl p-8 text-center text-red-500">
      <p class="font-medium">{{ error }}</p>
      <button @click="load" class="mt-3 text-sm underline">Réessayer</button>
    </div>

    <div v-else class="card-premium rounded-2xl overflow-hidden">
      <table class="w-full text-sm">
        <thead>
          <tr class="border-b border-border bg-muted/30">
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">N° Commande</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Fournisseur</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden md:table-cell">Entrepôt</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden lg:table-cell">Livraison prévue</th>
            <th class="text-right px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden lg:table-cell">Montant</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Statut</th>
            <th class="px-4 py-3.5"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="o in filtered" :key="o.id" class="table-row-hover border-b border-border/50 last:border-0">
            <td class="px-4 py-3.5">
              <span class="font-mono font-bold text-sm text-foreground">{{ o.numero }}</span>
              <div class="text-xs text-muted-foreground mt-0.5">{{ formatDate(o.dateCommande) }}</div>
            </td>
            <td class="px-4 py-3.5">
              <div class="flex items-center gap-2">
                <div class="w-7 h-7 rounded-lg flex items-center justify-center text-white text-xs font-bold shrink-0" style="background: linear-gradient(135deg, #3b82f6, #06b6d4);">
                  {{ (o.fournisseurNom || '??').slice(0,2).toUpperCase() }}
                </div>
                <span class="font-medium text-foreground text-sm">{{ o.fournisseurNom }}</span>
              </div>
            </td>
            <td class="px-4 py-3.5 hidden md:table-cell text-sm text-muted-foreground">{{ o.entrepotNom }}</td>
            <td class="px-4 py-3.5 hidden lg:table-cell">
              <span v-if="o.dateLivraisonPrevue" :class="['text-sm', isLate(o) ? 'text-red-500 font-semibold' : 'text-muted-foreground']">
                {{ formatDate(o.dateLivraisonPrevue) }}{{ isLate(o) ? ' ⚠' : '' }}
              </span>
              <span v-else class="text-muted-foreground">—</span>
            </td>
            <td class="px-4 py-3.5 text-right hidden lg:table-cell font-semibold text-foreground">{{ formatCurrency(o.montantTotal) }}</td>
            <td class="px-4 py-3.5"><StatusBadge :status="o.statut" :dot="true" /></td>
            <td class="px-4 py-3.5">
              <RouterLink :to="`/purchase-orders/${o.id}`" class="p-1.5 rounded-lg hover:bg-muted text-muted-foreground hover:text-foreground transition-colors inline-flex">
                <Eye :size="14" />
              </RouterLink>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!filtered.length" class="text-center py-16 text-muted-foreground">
        <ShoppingCart :size="36" class="mx-auto mb-3 opacity-30" />
        <p class="font-medium">Aucune commande fournisseur</p>
      </div>
      <div v-if="totalPages > 1" class="flex items-center justify-between px-4 py-3 border-t border-border text-sm text-muted-foreground">
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
import { Plus, Search, Eye, ShoppingCart } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { purchaseOrderService, type PurchaseOrder } from '@/services/operations.service'
import { formatDate, formatCurrency } from '@/utils/formatters'
import { usePermissions } from '@/composables/usePermissions'

const { can } = usePermissions()
const items = ref<PurchaseOrder[]>([])
const loading = ref(false)
const error = ref('')
const search = ref('')
const filterStatut = ref('')
const page = ref(0)
const total = ref(0)
const totalPages = ref(0)
let searchTimer: ReturnType<typeof setTimeout>

const filtered = computed(() => items.value.filter(o => {
  if (search.value && !`${o.numero} ${o.fournisseurNom}`.toLowerCase().includes(search.value.toLowerCase())) return false
  if (filterStatut.value && o.statut !== filterStatut.value) return false
  return true
}))

const kpis = computed(() => [
  { label: 'Brouillon', count: items.value.filter(o => o.statut === 'BROUILLON').length, color: 'text-muted-foreground' },
  { label: 'Envoyées', count: items.value.filter(o => o.statut === 'ENVOYEE').length, color: 'text-blue-600' },
  { label: 'En retard', count: items.value.filter(o => isLate(o)).length, color: 'text-red-500' },
  { label: 'Livrées', count: items.value.filter(o => o.statut === 'LIVREE').length, color: 'text-emerald-600' },
])

function isLate(o: PurchaseOrder) {
  return o.dateLivraisonPrevue && !o.dateLivraisonReelle &&
    ['ENVOYEE', 'PARTIELLEMENT_LIVREE'].includes(o.statut) &&
    new Date(o.dateLivraisonPrevue) < new Date()
}

async function load() {
  loading.value = true; error.value = ''
  try {
    const data = await purchaseOrderService.findAll(page.value, 50)
    items.value = data.content
    total.value = data.totalElements
    totalPages.value = data.totalPages
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors du chargement des commandes'
  } finally {
    loading.value = false
  }
}

function onSearch() {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => { page.value = 0; load() }, 300)
}

onMounted(load)
</script>
