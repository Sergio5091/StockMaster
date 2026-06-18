<template>
  <div class="p-6 max-w-[1200px] mx-auto">
    <PageHeader :title="supplier.nom" :subtitle="supplier.code" back="Fournisseurs">
      <template #actions>
        <StatusBadge :status="supplier.actif ? 'actif' : 'inactif'" :dot="true" />
        <button v-if="can('manage_suppliers')" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white"><Edit3 :size="15" /> Modifier</button>
      </template>
    </PageHeader>
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div class="space-y-5">
        <div class="card-premium rounded-2xl p-5 space-y-4">
          <div class="flex items-center gap-3">
            <div class="w-14 h-14 rounded-2xl flex items-center justify-center text-white font-bold text-xl" style="background: linear-gradient(135deg, #3b82f6, #06b6d4);">{{ supplier.nom.slice(0,2).toUpperCase() }}</div>
            <div><div class="font-bold text-foreground text-lg">{{ supplier.nom }}</div><div class="text-xs text-muted-foreground">{{ supplier.code }}</div></div>
          </div>
          <div class="space-y-2.5 text-sm">
            <div class="flex items-center gap-2 text-muted-foreground"><MapPin :size="13" /><span>{{ supplier.adresse }}, {{ supplier.ville }}</span></div>
            <div class="flex items-center gap-2 text-muted-foreground"><Phone :size="13" /><span>{{ supplier.telephone }}</span></div>
            <div class="flex items-center gap-2 text-muted-foreground"><Mail :size="13" /><span>{{ supplier.email }}</span></div>
          </div>
        </div>
        <div class="card-premium rounded-2xl p-5">
          <h3 class="font-semibold mb-3 text-sm uppercase tracking-wide text-muted-foreground">Contact commercial</h3>
          <div class="flex items-center gap-3 mb-3">
            <div class="w-10 h-10 rounded-xl bg-blue-100 flex items-center justify-center text-sm font-bold text-blue-600">{{ supplier.contactPrenom[0] }}{{ supplier.contactNom[0] }}</div>
            <div><div class="font-semibold">{{ supplier.contactPrenom }} {{ supplier.contactNom }}</div><div class="text-xs text-muted-foreground">{{ supplier.contactEmail }}</div></div>
          </div>
          <div class="text-sm text-muted-foreground">{{ supplier.contactTelephone }}</div>
        </div>
      </div>

      <div class="lg:col-span-2 space-y-5">
        <!-- KPIs -->
        <div class="grid grid-cols-3 gap-4">
          <div class="kpi-card">
            <div class="text-xs text-muted-foreground mb-1 uppercase tracking-wide">Commandes</div>
            <div class="text-3xl font-bold text-foreground">{{ supplier.commandesTotal }}</div>
          </div>
          <div class="kpi-card">
            <div class="text-xs text-muted-foreground mb-1 uppercase tracking-wide">Ponctualité</div>
            <div class="text-3xl font-bold" :class="supplier.tauxRespectDelai >= 90 ? 'text-emerald-600' : supplier.tauxRespectDelai >= 75 ? 'text-amber-500' : 'text-red-500'">{{ supplier.tauxRespectDelai }}%</div>
          </div>
          <div class="kpi-card">
            <div class="text-xs text-muted-foreground mb-1 uppercase tracking-wide">Montant total</div>
            <div class="text-xl font-bold text-foreground">{{ formatCurrency(supplier.montantTotal) }}</div>
          </div>
        </div>
        <!-- Orders -->
        <div class="card-premium rounded-2xl overflow-hidden">
          <div class="px-5 py-4 border-b border-border">
            <h3 class="font-semibold flex items-center gap-2"><ShoppingCart :size="15" class="text-emerald-600" /> Dernières commandes</h3>
          </div>
          <table class="w-full text-sm">
            <thead><tr class="border-b border-border bg-muted/30">
              <th class="text-left px-4 py-3 text-xs font-semibold text-muted-foreground uppercase tracking-wide">N°</th>
              <th class="text-left px-4 py-3 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Date</th>
              <th class="text-left px-4 py-3 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Montant</th>
              <th class="text-left px-4 py-3 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Statut</th>
            </tr></thead>
            <tbody>
              <tr v-for="o in supplierOrders" :key="o.id" class="table-row-hover border-b border-border/50 last:border-0">
                <td class="px-4 py-3 font-mono text-xs font-medium">{{ o.numero }}</td>
                <td class="px-4 py-3 text-muted-foreground text-xs">{{ formatDate(o.dateCommande) }}</td>
                <td class="px-4 py-3 font-semibold">{{ formatCurrency(o.montantTotal) }}</td>
                <td class="px-4 py-3"><StatusBadge :status="o.statut" /></td>
              </tr>
            </tbody>
          </table>
          <div v-if="!supplierOrders.length" class="text-center py-10 text-sm text-muted-foreground">Aucune commande</div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { Edit3, MapPin, Phone, Mail, ShoppingCart } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { SUPPLIERS, PURCHASE_ORDERS, formatCurrency, formatDate } from '@/services/mockData'
import { usePermissions } from '@/composables/usePermissions'
const { can } = usePermissions()
const route = useRoute()
const id = Number(route.params.id)
const supplier = SUPPLIERS.find(s => s.id === id) || SUPPLIERS[0]
const supplierOrders = computed(() => PURCHASE_ORDERS.filter(o => o.fournisseurId === supplier.id))
</script>
