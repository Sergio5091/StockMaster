<template>
  <div class="p-6 max-w-[1400px] mx-auto">
    <PageHeader title="Bons de réception" :subtitle="`${total} bon(s)`">
      <template #actions>
        <RouterLink to="/receipts/new" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white">
          <Plus :size="15" /> Nouveau bon
        </RouterLink>
      </template>
    </PageHeader>

    <div class="card-premium rounded-2xl p-4 mb-6 flex flex-wrap gap-3 items-center">
      <div class="relative flex-1 min-w-[200px]">
        <Search :size="15" class="absolute left-3.5 top-1/2 -translate-y-1/2 text-muted-foreground" />
        <input v-model="search" type="search" placeholder="N° bon, fournisseur…" class="input-field pl-10" @input="onSearch" />
      </div>
      <select v-model="filterStatut" class="select-field w-auto min-w-[200px]" @change="load">
        <option value="">Tous les statuts</option>
        <option value="BROUILLON">Brouillon</option>
        <option value="EN_ATTENTE_VALIDATION">En attente validation</option>
        <option value="VALIDE">Validé</option>
        <option value="REJETE">Rejeté</option>
      </select>
    </div>

    <!-- Loader -->
    <div v-if="loading" class="flex justify-center py-20">
      <div class="w-8 h-8 border-4 border-emerald-500 border-t-transparent rounded-full animate-spin" />
    </div>

    <!-- Erreur -->
    <div v-else-if="error" class="card-premium rounded-2xl p-8 text-center text-red-500">
      <p class="font-medium">{{ error }}</p>
      <button @click="load" class="mt-3 text-sm underline">Réessayer</button>
    </div>

    <div v-else class="card-premium rounded-2xl overflow-hidden">
      <table class="w-full text-sm">
        <thead>
          <tr class="border-b border-border bg-muted/30">
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">N° Bon</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Fournisseur</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden md:table-cell">Entrepôt</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden lg:table-cell">Date réception</th>
            <th class="text-center px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden lg:table-cell">Lignes</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Statut</th>
            <th class="px-4 py-3.5"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="r in items" :key="r.id" class="table-row-hover border-b border-border/50 last:border-0">
            <td class="px-4 py-3.5">
              <span class="font-mono font-bold text-sm text-foreground">{{ r.numero }}</span>
              <div v-if="r.commandeFournisseurId" class="text-xs text-blue-600 mt-0.5">Lié à CF#{{ r.commandeFournisseurId }}</div>
            </td>
            <td class="px-4 py-3.5">
              <div class="flex items-center gap-2">
                <div class="w-7 h-7 rounded-lg flex items-center justify-center text-white text-xs font-bold shrink-0" style="background: linear-gradient(135deg, #3b82f6, #06b6d4);">
                  {{ (r.fournisseurNom || '??').slice(0,2).toUpperCase() }}
                </div>
                <span class="font-medium text-foreground text-sm">{{ r.fournisseurNom }}</span>
              </div>
            </td>
            <td class="px-4 py-3.5 hidden md:table-cell text-sm text-muted-foreground">{{ r.entrepotNom }}</td>
            <td class="px-4 py-3.5 hidden lg:table-cell text-sm text-muted-foreground">{{ formatDate(r.dateReception) }}</td>
            <td class="px-4 py-3.5 hidden lg:table-cell text-center">
              <span class="inline-flex items-center justify-center w-6 h-6 rounded-full bg-muted text-xs font-bold text-muted-foreground">{{ r.lignes?.length ?? 0 }}</span>
            </td>
            <td class="px-4 py-3.5"><StatusBadge :status="r.statut" :dot="true" /></td>
            <td class="px-4 py-3.5">
              <RouterLink :to="`/receipts/${r.id}`" class="p-1.5 rounded-lg hover:bg-muted text-muted-foreground hover:text-foreground transition-colors inline-flex">
                <Eye :size="14" />
              </RouterLink>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!items.length && !loading" class="text-center py-16 text-muted-foreground">
        <PackageOpen :size="36" class="mx-auto mb-3 opacity-30" />
        <p class="font-medium">Aucun bon de réception</p>
        <RouterLink to="/receipts/new" class="mt-3 inline-flex items-center gap-1.5 text-sm text-emerald-600 hover:underline">
          <Plus :size="14" /> Créer le premier bon
        </RouterLink>
      </div>
      <!-- Pagination -->
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
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { Plus, Search, Eye, PackageOpen } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { receiptService, type Receipt } from '@/services/operations.service'
import { formatDate } from '@/utils/formatters'

const items = ref<Receipt[]>([])
const loading = ref(false)
const error = ref('')
const search = ref('')
const filterStatut = ref('')
const page = ref(0)
const total = ref(0)
const totalPages = ref(0)
let searchTimer: ReturnType<typeof setTimeout>

async function load() {
  loading.value = true; error.value = ''
  try {
    const data = await receiptService.findAll(page.value, 20)
    let content = data.content
    if (search.value) content = content.filter(r => `${r.numero} ${r.fournisseurNom}`.toLowerCase().includes(search.value.toLowerCase()))
    if (filterStatut.value) content = content.filter(r => r.statut === filterStatut.value)
    items.value = content
    total.value = data.totalElements
    totalPages.value = data.totalPages
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors du chargement des bons de réception'
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
