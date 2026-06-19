<template>
  <div class="p-6 max-w-[1200px] mx-auto">
    <PageHeader :title="`Session · ${inv.numero}`" :subtitle="`${inv.entrepotNom} · ${inv.type}`" back="Inventaires">
      <template #actions>
        <StatusBadge :status="inv.statut" :dot="true" />
        <button v-if="can('launch_inventory') && inv.statut !== 'TERMINE'" @click="cloturer" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white"><CheckCircle2 :size="15" /> Clôturer l'inventaire</button>
      </template>
    </PageHeader>

    <!-- Summary -->
    <div class="grid grid-cols-3 gap-4 mb-6">
      <div class="kpi-card"><div class="text-xs text-muted-foreground uppercase tracking-wide mb-1">Lignes total</div><div class="text-3xl font-bold text-foreground">{{ lines.length }}</div></div>
      <div class="kpi-card"><div class="text-xs text-amber-500 uppercase tracking-wide mb-1">Écarts détectés</div><div class="text-3xl font-bold text-amber-500">{{ ecarts }}</div></div>
      <div class="kpi-card"><div class="text-xs text-emerald-600 uppercase tracking-wide mb-1">Sans écart</div><div class="text-3xl font-bold text-emerald-600">{{ lines.length - ecarts }}</div></div>
    </div>

    <div class="card-premium rounded-2xl overflow-hidden">
      <div class="px-5 py-4 border-b border-border"><h3 class="font-semibold">Saisie des quantités comptées</h3></div>
      <table class="w-full text-sm">
        <thead><tr class="border-b border-border bg-muted/30">
          <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Produit</th>
          <th class="text-right px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Qté système</th>
          <th class="text-right px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Qté comptée</th>
          <th class="text-right px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Écart</th>
        </tr></thead>
        <tbody>
          <tr v-for="line in lines" :key="line.id" class="table-row-hover border-b border-border/50 last:border-0">
            <td class="px-4 py-3.5">
              <div class="font-medium text-foreground">{{ line.produit }}</div>
              <div class="text-xs text-muted-foreground font-mono">{{ line.reference }}</div>
            </td>
            <td class="px-4 py-3.5 text-right text-muted-foreground font-medium">{{ line.qteTheorique }}</td>
            <td class="px-4 py-3.5 text-right">
              <input v-model.number="line.qteComptee" type="number" min="0" :disabled="inv.statut === 'TERMINE'"
                class="w-24 px-2 py-1.5 rounded-lg border border-border bg-card text-sm text-right focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary/60 disabled:opacity-50" />
            </td>
            <td class="px-4 py-3.5 text-right">
              <span v-if="line.qteComptee !== null" :class="['font-bold text-sm', line.qteComptee - line.qteTheorique > 0 ? 'text-emerald-600' : line.qteComptee - line.qteTheorique < 0 ? 'text-red-500' : 'text-muted-foreground']">
                {{ line.qteComptee - line.qteTheorique > 0 ? '+' : '' }}{{ line.qteComptee - line.qteTheorique }}
              </span>
              <span v-else class="text-muted-foreground">—</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { CheckCircle2 } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { inventoryService as InventoryService } from '@/services/operations.service'
import ProductService, { type Product } from '@/services/product.service'
import { usePermissions } from '@/composables/usePermissions'
const { can } = usePermissions()
const route = useRoute()
const router = useRouter()
const inv = ref({ id: 0, numero: '', entrepotNom: '', type: '', statut: 'BROUILLON', lignes: [] as any[] })
const lines = ref<any[]>([])
const saving = ref(false)
onMounted(async () => {
  const data = await InventoryService.findById(Number(route.params.id))
  inv.value = data
  const products = (await ProductService.getAll(0, 200)).content
  const theo = products.slice(0, 6).map((p: Product, i: number) => ({ id: i + 1, produitId: p.id, produit: p.nom, reference: p.reference, qteTheorique: p.stockTotal ?? 0, qteComptee: null as number | null }))
  lines.value = theo
})
const ecarts = computed(() => lines.value.filter(l => l.qteComptee !== null && l.qteComptee !== l.qteTheorique).length)
async function cloturer() {
  saving.value = true
  try {
    const res = await InventoryService.validate(inv.value.id)
    inv.value.statut = res.statut
  } catch (e) {
    console.error(e)
  } finally {
    saving.value = false
  }
}
</script>
