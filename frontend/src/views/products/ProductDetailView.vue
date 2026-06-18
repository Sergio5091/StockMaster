<template>
  <div class="p-6 max-w-[1200px] mx-auto">
    <PageHeader :title="product.nom" :subtitle="product.reference" back="Produits">
      <template #actions>
        <StatusBadge :status="product.actif ? 'actif' : 'inactif'" :dot="true" />
        <button v-if="can('manage_products')" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white"><Edit3 :size="15" /> Modifier</button>
      </template>
    </PageHeader>
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Product info -->
      <div class="space-y-5">
        <div class="card-premium rounded-2xl p-5">
          <div class="w-full h-40 rounded-xl bg-gradient-to-br from-emerald-50 to-emerald-100 flex items-center justify-center mb-4">
            <Package :size="48" class="text-emerald-300" />
          </div>
          <div class="space-y-3 text-sm">
            <div class="flex justify-between"><span class="text-muted-foreground">Référence</span><span class="font-mono font-bold text-sm">{{ product.reference }}</span></div>
            <div class="flex justify-between"><span class="text-muted-foreground">Code-barres</span><span class="font-mono text-xs">{{ product.codeBarres || '—' }}</span></div>
            <div class="flex justify-between"><span class="text-muted-foreground">Catégorie</span><span class="font-medium">{{ product.categorieNom }}</span></div>
            <div class="flex justify-between"><span class="text-muted-foreground">Fournisseur</span><span class="font-medium">{{ product.fournisseurNom }}</span></div>
            <div class="flex justify-between"><span class="text-muted-foreground">Unité</span><span class="font-medium">{{ product.uniteMesure }}</span></div>
            <div class="flex justify-between"><span class="text-muted-foreground">Poids</span><span class="font-medium">{{ product.poidsKg }} kg</span></div>
          </div>
        </div>
        <div class="card-premium rounded-2xl p-5">
          <h3 class="font-semibold mb-3 text-sm text-muted-foreground uppercase tracking-wide">Prix</h3>
          <div class="grid grid-cols-2 gap-3">
            <div class="p-3 rounded-xl bg-muted/50 text-center">
              <div class="text-xs text-muted-foreground mb-1">Achat</div>
              <div class="font-bold text-foreground">{{ formatCurrency(product.prixAchat) }}</div>
            </div>
            <div class="p-3 rounded-xl bg-emerald-50 border border-emerald-100 text-center">
              <div class="text-xs text-emerald-600 mb-1">Vente</div>
              <div class="font-bold text-emerald-700">{{ formatCurrency(product.prixVente) }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Stock by warehouse -->
      <div class="lg:col-span-2 space-y-5">
        <div class="card-premium rounded-2xl p-5">
          <h3 class="font-semibold mb-4 flex items-center gap-2"><BarChart3 :size="16" class="text-emerald-600" /> Stock par entrepôt</h3>
          <div class="space-y-3">
            <div v-for="s in productStocks" :key="s.id" class="flex items-center gap-4 p-3.5 rounded-xl border border-border/50 hover:bg-muted/30 transition-colors">
              <div class="w-9 h-9 rounded-xl bg-emerald-100 flex items-center justify-center shrink-0">
                <Warehouse :size="16" class="text-emerald-600" />
              </div>
              <div class="flex-1 min-w-0">
                <div class="font-medium text-sm text-foreground">{{ s.entrepotNom }}</div>
                <div class="text-xs text-muted-foreground">{{ s.entrepotCode }}</div>
                <StockIndicator :qty="s.quantiteDisponible" :min="product.stockMinimum" :max="product.stockMaximum" class="mt-1.5" />
              </div>
              <div class="text-right shrink-0">
                <div class="font-bold text-lg text-foreground">{{ s.quantiteDisponible }}</div>
                <div class="text-xs text-muted-foreground">+ {{ s.quantiteEnTransit }} transit</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Thresholds -->
        <div class="card-premium rounded-2xl p-5">
          <h3 class="font-semibold mb-3 flex items-center gap-2 text-sm"><AlertTriangle :size="15" class="text-amber-500" /> Seuils de stock</h3>
          <div class="grid grid-cols-2 gap-4">
            <div class="p-4 rounded-xl bg-red-50 border border-red-100">
              <div class="text-xs text-red-600 font-semibold uppercase mb-1">Stock minimum</div>
              <div class="text-2xl font-bold text-red-700">{{ product.stockMinimum }}</div>
              <div class="text-xs text-red-400 mt-0.5">unités</div>
            </div>
            <div class="p-4 rounded-xl bg-blue-50 border border-blue-100">
              <div class="text-xs text-blue-600 font-semibold uppercase mb-1">Stock maximum</div>
              <div class="text-2xl font-bold text-blue-700">{{ product.stockMaximum }}</div>
              <div class="text-xs text-blue-400 mt-0.5">unités</div>
            </div>
          </div>
        </div>

        <!-- Recent movements -->
        <div class="card-premium rounded-2xl overflow-hidden">
          <div class="px-5 py-4 border-b border-border flex items-center justify-between">
            <h3 class="font-semibold flex items-center gap-2"><ArrowLeftRight :size="15" class="text-emerald-600" /> Derniers mouvements</h3>
            <RouterLink to="/stocks/movements" class="text-xs text-emerald-600 hover:underline">Tout voir →</RouterLink>
          </div>
          <div class="divide-y divide-border/50">
            <div v-for="m in recentMvts" :key="m.id" class="flex items-center gap-3 px-5 py-3 hover:bg-muted/30 transition-colors">
              <StatusBadge :status="m.type" />
              <div class="flex-1 min-w-0 text-sm">
                <span class="text-muted-foreground">{{ m.refDocument }}</span>
                <span class="text-xs text-muted-foreground ml-2">{{ m.utilisateur }}</span>
              </div>
              <div class="text-right shrink-0">
                <div class="font-semibold" :class="m.type === 'ENTREE' ? 'text-emerald-600' : m.type === 'SORTIE' ? 'text-red-500' : 'text-blue-500'">
                  {{ m.type === 'SORTIE' ? '-' : '+' }}{{ Math.abs(m.quantite) }}
                </div>
                <div class="text-xs text-muted-foreground">{{ formatDate(m.createdAt) }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { Package, Edit3, BarChart3, Warehouse, AlertTriangle, ArrowLeftRight } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import StockIndicator from '@/components/common/StockIndicator.vue'
import { PRODUCTS, STOCKS, STOCK_MOVEMENTS, formatCurrency, formatDate } from '@/services/mockData'
import { usePermissions } from '@/composables/usePermissions'
const { can } = usePermissions()
const route = useRoute()
const id = Number(route.params.id)
const product = PRODUCTS.find(p => p.id === id) || PRODUCTS[0]
const productStocks = STOCKS.filter(s => s.produitId === product.id)
const recentMvts = STOCK_MOVEMENTS.filter(m => m.produitRef === product.reference).slice(0, 5)
</script>
