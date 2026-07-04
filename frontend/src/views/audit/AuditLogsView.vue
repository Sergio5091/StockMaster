<template>
  <div class="p-6 max-w-[1400px] mx-auto">
    <PageHeader title="Journal d'audit" subtitle="Traçabilité complète des actions utilisateurs" />
    <div class="card-premium rounded-2xl p-4 mb-6 flex flex-wrap gap-3 items-center">
      <div class="relative flex-1 min-w-[200px]">
        <Search :size="15" class="absolute left-3.5 top-1/2 -translate-y-1/2 text-muted-foreground" />
        <input v-model="search" type="search" placeholder="Utilisateur, entité, opération…" class="input-field pl-10" @input="onSearch" />
      </div>
      <select v-model="filterEntity" class="select-field w-auto min-w-[180px]" @change="load">
        <option value="">Toutes les entités</option>
        <option value="GoodsReceiptService">Réceptions</option>
        <option value="GoodsIssueService">Sorties</option>
        <option value="TransferService">Transferts</option>
        <option value="InventoryService">Inventaires</option>
        <option value="ProductService">Produits</option>
        <option value="UserApplicationService">Utilisateurs</option>
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
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Date</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Utilisateur</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden md:table-cell">Entité</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden md:table-cell">Opération</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden lg:table-cell">Détails</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="log in filtered" :key="log.id" class="table-row-hover border-b border-border/50 last:border-0">
            <td class="px-4 py-3.5 text-xs text-muted-foreground whitespace-nowrap">{{ formatDatetime(log.performedAt) }}</td>
            <td class="px-4 py-3.5 text-sm font-medium text-foreground">{{ log.performedBy }}</td>
            <td class="px-4 py-3.5 hidden md:table-cell">
              <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border bg-blue-50 text-blue-700 border-blue-200">{{ log.entityName }}</span>
            </td>
            <td class="px-4 py-3.5 hidden md:table-cell text-sm text-muted-foreground font-mono">{{ log.operationDescription }}</td>
            <td class="px-4 py-3.5 hidden lg:table-cell text-xs text-muted-foreground truncate max-w-xs">{{ log.details || '—' }}</td>
          </tr>
        </tbody>
      </table>
      <div v-if="!filtered.length" class="text-center py-16 text-muted-foreground">
        <History :size="36" class="mx-auto mb-3 opacity-30" />
        <p>Aucun log d'audit trouvé</p>
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
import { Search, History } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import api from '@/services/api'
import { formatDatetime } from '@/utils/formatters'

interface AuditLog {
  id: number
  entityName: string
  operationType: string
  operationDescription: string
  performedBy: string
  performedAt: string
  details?: string
}

const items = ref<AuditLog[]>([])
const loading = ref(false)
const error = ref('')
const search = ref('')
const filterEntity = ref('')
const page = ref(0)
const totalPages = ref(0)
let searchTimer: ReturnType<typeof setTimeout>

const filtered = computed(() => items.value.filter(log => {
  if (search.value) {
    const q = search.value.toLowerCase()
    if (!`${log.performedBy} ${log.entityName} ${log.operationDescription} ${log.details || ''}`.toLowerCase().includes(q)) return false
  }
  if (filterEntity.value && log.entityName !== filterEntity.value) return false
  return true
}))

async function load() {
  loading.value = true; error.value = ''
  try {
    const params: Record<string, any> = { page: page.value, size: 50 }
    if (filterEntity.value) params.entityName = filterEntity.value
    const { data } = await api.get('/v1/audit/logs', { params })
    items.value = data.content || []
    totalPages.value = data.totalPages || 0
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors du chargement du journal d\'audit'
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
