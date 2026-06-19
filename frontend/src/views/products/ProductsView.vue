<template>
  <div class="p-6 max-w-[1600px] mx-auto">
    <PageHeader title="Produits" :subtitle="`${totalElements} produit(s)`">
      <template #actions>
        <button v-if="can('manage_products')" @click="openCreate" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white">
          <Plus :size="15" /> Nouveau produit
        </button>
      </template>
    </PageHeader>

    <!-- Filters -->
    <div class="card-premium rounded-2xl p-4 mb-6 flex flex-wrap gap-3 items-center">
      <div class="relative flex-1 min-w-[200px]">
        <Search :size="15" class="absolute left-3.5 top-1/2 -translate-y-1/2 text-muted-foreground" />
        <input v-model="search" type="search" placeholder="Nom, référence, code-barres…" class="input-field pl-10" @input="onSearch" />
      </div>
      <select v-model="filterCatId" class="select-field w-auto min-w-[160px]" @change="onFilterChange">
        <option value="">Toutes catégories</option>
        <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.nom }}</option>
      </select>
      <select v-model="filterStock" class="select-field w-auto min-w-[150px]" @change="applyLocalFilter">
        <option value="">Tous les stocks</option>
        <option value="CRITIQUE">🔴 Critique</option>
        <option value="FAIBLE">🟠 Faible</option>
        <option value="NORMAL">🟢 Normal</option>
        <option value="EXCESSIF">🔵 Excédentaire</option>
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

    <!-- Table -->
    <div v-else class="card-premium rounded-2xl overflow-hidden">
      <table class="w-full text-sm">
        <thead>
          <tr class="border-b border-border bg-muted/30">
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Produit</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden md:table-cell">Catégorie</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden lg:table-cell">Fournisseur</th>
            <th class="text-right px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Prix achat</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Niveau stock</th>
            <th class="px-4 py-3.5"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in displayedProducts" :key="p.id" class="table-row-hover border-b border-border/50 last:border-0">
            <td class="px-4 py-3.5">
              <div class="flex items-center gap-3">
                <div class="w-9 h-9 rounded-xl bg-muted flex items-center justify-center shrink-0 border border-border overflow-hidden">
                  <img v-if="p.imageUrl" :src="p.imageUrl" :alt="p.nom" class="w-full h-full object-cover" />
                  <Package v-else :size="16" class="text-muted-foreground" />
                </div>
                <div>
                  <div class="font-semibold text-foreground">{{ p.nom }}</div>
                  <div class="text-xs text-muted-foreground font-mono">{{ p.reference }}</div>
                </div>
              </div>
            </td>
            <td class="px-4 py-3.5 hidden md:table-cell text-sm text-muted-foreground">{{ p.categorieNom ?? '—' }}</td>
            <td class="px-4 py-3.5 hidden lg:table-cell text-sm text-muted-foreground">{{ p.fournisseurNom ?? '—' }}</td>
            <td class="px-4 py-3.5 text-right font-medium text-foreground">
              {{ p.prixAchat ? formatCurrency(p.prixAchat) : '—' }}
            </td>
            <td class="px-4 py-3.5">
              <StockIndicator :qty="0" :min="p.stockMinimum" :max="p.stockMaximum" :show-status="true" />
            </td>
            <td class="px-4 py-3.5">
              <div class="flex items-center gap-1 justify-end">
                <RouterLink :to="`/products/${p.id}`" class="p-1.5 rounded-lg hover:bg-muted text-muted-foreground hover:text-foreground transition-colors">
                  <Eye :size="14" />
                </RouterLink>
                <button v-if="can('manage_products')" @click="openEdit(p)" class="p-1.5 rounded-lg hover:bg-muted text-muted-foreground hover:text-foreground transition-colors">
                  <Edit3 :size="14" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="!displayedProducts.length && !loading" class="text-center py-16 text-muted-foreground">
        <Package :size="36" class="mx-auto mb-3 opacity-30" />
        <p class="font-medium">Aucun produit trouvé</p>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="flex items-center justify-between px-4 py-3 border-t border-border bg-muted/20">
        <span class="text-xs text-muted-foreground">{{ totalElements }} produit(s) · Page {{ page + 1 }} / {{ totalPages }}</span>
        <div class="flex items-center gap-1">
          <button @click="page--; load()" :disabled="page === 0" class="p-1.5 rounded-lg hover:bg-muted disabled:opacity-40 transition-colors">
            <ChevronLeft :size="16" />
          </button>
          <button @click="page++; load()" :disabled="page >= totalPages - 1" class="p-1.5 rounded-lg hover:bg-muted disabled:opacity-40 transition-colors">
            <ChevronRight :size="16" />
          </button>
        </div>
      </div>
    </div>

    <ProductFormModal
      v-if="showForm"
      :product="editingProduct"
      @close="closeForm"
      @saved="onSaved"
    />
  </div>
</template>
<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { Plus, Search, Package, Eye, Edit3, ChevronLeft, ChevronRight } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StockIndicator from '@/components/common/StockIndicator.vue'
import ProductFormModal from './ProductFormModal.vue'
import ProductService, { type Product } from '@/services/product.service'
import { categoryService, type Category } from '@/services/category.service'
import { usePermissions } from '@/composables/usePermissions'

const { can } = usePermissions()

const products = ref<Product[]>([])
const categories = ref<Category[]>([])
const loading = ref(false)
const error = ref('')
const search = ref('')
const filterCatId = ref<number | ''>('')
const filterStock = ref('')
const page = ref(0)
const totalElements = ref(0)
const totalPages = ref(0)
const showForm = ref(false)
const editingProduct = ref<Product | null>(null)
let searchTimer: ReturnType<typeof setTimeout>

const displayedProducts = computed(() =>
  filterStock.value
    ? products.value.filter(p => p.niveauStock === filterStock.value)
    : products.value
)

function formatCurrency(n: number) {
  return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(n)
}

async function load() {
  loading.value = true; error.value = ''
  try {
    const catId = filterCatId.value ? Number(filterCatId.value) : undefined
    let data
    if (search.value.trim()) {
      data = await ProductService.search(search.value.trim(), page.value, 20)
    } else {
      data = await ProductService.getAll(page.value, 20, catId)
    }
    products.value = data.content
    totalElements.value = data.totalElements
    totalPages.value = data.totalPages
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors du chargement des produits'
  } finally {
    loading.value = false
  }
}

function onSearch() {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => { page.value = 0; load() }, 300)
}

function onFilterChange() {
  page.value = 0; load()
}

function applyLocalFilter() {
  // filtre local seulement — pas de rechargement API
}

function openCreate() { editingProduct.value = null; showForm.value = true }
function openEdit(p: Product) { editingProduct.value = p; showForm.value = true }
function closeForm() { showForm.value = false; editingProduct.value = null }
function onSaved() { closeForm(); load() }

onMounted(async () => {
  await Promise.all([load(), categoryService.findActiveCategories().then(c => categories.value = c)])
})
</script>
