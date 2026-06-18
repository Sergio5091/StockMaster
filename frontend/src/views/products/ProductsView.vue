<template>
  <div class="p-6 max-w-[1600px] mx-auto">
    <PageHeader title="Produits" :subtitle="`${filtered.length} produit(s)`">
      <template #actions>
        <button v-if="can('manage_products')" @click="showForm = true" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white">
          <Plus :size="15" /> Nouveau produit
        </button>
      </template>
    </PageHeader>

    <!-- Filters -->
    <div class="card-premium rounded-2xl p-4 mb-6 flex flex-wrap gap-3 items-center">
      <div class="relative flex-1 min-w-[200px]">
        <Search :size="15" class="absolute left-3.5 top-1/2 -translate-y-1/2 text-muted-foreground" />
        <input v-model="search" type="search" placeholder="Nom, référence, code-barres…" class="input-field pl-10" />
      </div>
      <select v-model="filterCat" class="select-field w-auto min-w-[160px]">
        <option value="">Toutes catégories</option>
        <option v-for="c in categories" :key="c">{{ c }}</option>
      </select>
      <select v-model="filterStock" class="select-field w-auto min-w-[150px]">
        <option value="">Tous les stocks</option>
        <option value="critical">🔴 Critique</option>
        <option value="low">🟠 Faible</option>
        <option value="normal">🟢 Normal</option>
        <option value="excess">🔵 Excédentaire</option>
      </select>
    </div>

    <!-- Table -->
    <div class="card-premium rounded-2xl overflow-hidden">
      <table class="w-full text-sm">
        <thead>
          <tr class="border-b border-border bg-muted/30">
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Produit</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden md:table-cell">Catégorie</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden lg:table-cell">Fournisseur</th>
            <th class="text-right px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Prix achat</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Stock</th>
            <th class="px-4 py-3.5"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in paginated" :key="p.id" class="table-row-hover border-b border-border/50 last:border-0">
            <td class="px-4 py-3.5">
              <div class="flex items-center gap-3">
                <div class="w-9 h-9 rounded-xl bg-muted flex items-center justify-center shrink-0 border border-border">
                  <Package :size="16" class="text-muted-foreground" />
                </div>
                <div>
                  <div class="font-semibold text-foreground">{{ p.nom }}</div>
                  <div class="text-xs text-muted-foreground font-mono">{{ p.reference }}</div>
                </div>
              </div>
            </td>
            <td class="px-4 py-3.5 hidden md:table-cell text-sm text-muted-foreground">{{ p.categorieNom }}</td>
            <td class="px-4 py-3.5 hidden lg:table-cell text-sm text-muted-foreground">{{ p.fournisseurNom }}</td>
            <td class="px-4 py-3.5 text-right font-medium text-foreground">{{ formatCurrency(p.prixAchat) }}</td>
            <td class="px-4 py-3.5 min-w-[160px]">
              <StockIndicator :qty="p.stockTotal" :min="p.stockMinimum" :max="p.stockMaximum" :show-status="true" />
            </td>
            <td class="px-4 py-3.5">
              <div class="flex items-center gap-1 justify-end">
                <RouterLink :to="`/products/${p.id}`" class="p-1.5 rounded-lg hover:bg-muted text-muted-foreground hover:text-foreground transition-colors" title="Voir">
                  <Eye :size="14" />
                </RouterLink>
                <button v-if="can('manage_products')" @click="editProduct(p)" class="p-1.5 rounded-lg hover:bg-muted text-muted-foreground hover:text-foreground transition-colors" title="Modifier">
                  <Edit3 :size="14" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <!-- Pagination -->
      <div v-if="totalPages > 1" class="flex items-center justify-between px-4 py-3 border-t border-border bg-muted/20">
        <span class="text-xs text-muted-foreground">{{ filtered.length }} produit(s) · Page {{ page }} / {{ totalPages }}</span>
        <div class="flex items-center gap-1">
          <button @click="page--" :disabled="page === 1" class="p-1.5 rounded-lg hover:bg-muted disabled:opacity-40 transition-colors"><ChevronLeft :size="16" /></button>
          <button v-for="p in totalPages" :key="p" @click="page = p" :class="['px-2.5 py-1 rounded-lg text-xs font-medium transition-colors', page === p ? 'bg-emerald-600 text-white' : 'hover:bg-muted text-muted-foreground']">{{ p }}</button>
          <button @click="page++" :disabled="page === totalPages" class="p-1.5 rounded-lg hover:bg-muted disabled:opacity-40 transition-colors"><ChevronRight :size="16" /></button>
        </div>
      </div>
    </div>

    <ProductFormModal v-if="showForm" :product="editingProduct" @close="closeForm" @save="saveProduct" />
  </div>
</template>
<script setup lang="ts">
import { ref, computed } from 'vue'
import { RouterLink } from 'vue-router'
import { Plus, Search, Package, Eye, Edit3, ChevronLeft, ChevronRight } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StockIndicator from '@/components/common/StockIndicator.vue'
import ProductFormModal from './ProductFormModal.vue'
import { PRODUCTS, formatCurrency } from '@/services/mockData'
import { usePermissions } from '@/composables/usePermissions'

const { can } = usePermissions()
const products = ref([...PRODUCTS])
const search = ref(''); const filterCat = ref(''); const filterStock = ref(''); const page = ref(1); const pageSize = 10
const showForm = ref(false); const editingProduct = ref<typeof PRODUCTS[0] | null>(null)
const categories = computed(() => [...new Set(products.value.map(p => p.categorieNom))])
const filtered = computed(() => products.value.filter(p => {
  if (search.value && !`${p.nom} ${p.reference} ${p.codeBarres || ''}`.toLowerCase().includes(search.value.toLowerCase())) return false
  if (filterCat.value && p.categorieNom !== filterCat.value) return false
  if (filterStock.value && p.stockStatut !== filterStock.value) return false
  return true
}))
const totalPages = computed(() => Math.max(1, Math.ceil(filtered.value.length / pageSize)))
const paginated = computed(() => filtered.value.slice((page.value - 1) * pageSize, page.value * pageSize))
function editProduct(p: typeof PRODUCTS[0]) { editingProduct.value = p; showForm.value = true }
function closeForm() { showForm.value = false; editingProduct.value = null }
function saveProduct(p: any) { const idx = products.value.findIndex(x => x.id === p.id); if (idx >= 0) products.value[idx] = p; else products.value.unshift(p); closeForm() }
</script>
