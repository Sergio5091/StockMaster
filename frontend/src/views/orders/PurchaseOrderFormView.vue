<template>
  <div class="p-6 max-w-[1000px] mx-auto">
    <PageHeader title="Nouvelle commande fournisseur" subtitle="Saisie du bon de commande" back="Commandes fournisseurs" />
    <form @submit.prevent="submit" class="space-y-6">
      <div class="card-premium rounded-2xl p-6">
        <h3 class="font-semibold text-foreground mb-4">Informations générales</h3>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div><label class="label-field">Fournisseur *</label>
            <select v-model="form.fournisseurId" class="select-field" required>
              <option :value="null" disabled>— Sélectionner —</option>
              <option v-for="s in suppliers" :key="s.id" :value="s.id">{{ s.nom }}</option>
            </select>
          </div>
          <div><label class="label-field">Entrepôt destination *</label>
            <select v-model="form.entrepotId" class="select-field" required>
              <option v-for="e in warehouses" :key="e.id" :value="e.id">{{ e.code }} — {{ e.nom }}</option>
            </select>
          </div>
          <div><label class="label-field">Date de livraison prévue</label><input v-model="form.dateLivraisonPrevue" type="date" class="input-field" /></div>
          <div><label class="label-field">Note</label><input v-model="form.note" type="text" class="input-field" /></div>
        </div>
      </div>
      <div class="card-premium rounded-2xl overflow-hidden">
        <div class="px-6 py-4 border-b border-border flex items-center justify-between">
          <h3 class="font-semibold">Lignes de commande</h3>
          <button type="button" @click="addLine" class="flex items-center gap-1.5 px-3.5 py-2 rounded-xl text-xs font-semibold bg-emerald-50 border border-emerald-200 text-emerald-700 hover:bg-emerald-100 transition-colors"><Plus :size="13" /> Ajouter une ligne</button>
        </div>
        <div class="p-4 space-y-3">
          <div v-if="!form.lignes.length" class="text-center py-10 text-sm text-muted-foreground"><ShoppingCart :size="28" class="mx-auto mb-2 opacity-30" />Aucune ligne</div>
          <div v-for="(l, i) in form.lignes" :key="i" class="flex items-end gap-3 p-4 rounded-xl bg-muted/30 border border-border/50">
            <div class="flex-1"><label class="label-field">Produit *</label>
              <select v-model="l.produitRef" class="select-field" required>
                <option v-for="p in products" :key="p.reference" :value="p.reference">{{ p.nom }}</option>
              </select>
            </div>
            <div class="w-28"><label class="label-field">Quantité *</label><input v-model.number="l.quantite" type="number" min="1" class="input-field" required /></div>
            <div class="w-32"><label class="label-field">Prix unitaire (€)</label><input v-model.number="l.prixUnitaire" type="number" min="0" step="0.01" class="input-field" /></div>
            <button type="button" @click="form.lignes.splice(i,1)" class="pb-2.5 p-1.5 rounded-lg text-muted-foreground hover:text-red-500 hover:bg-red-50 transition-colors"><Trash2 :size="15" /></button>
          </div>
          <div v-if="form.lignes.length" class="flex justify-end pt-2 border-t border-border">
            <span class="font-bold text-foreground">Total : {{ formatCurrency(total) }}</span>
          </div>
        </div>
      </div>
      <div class="flex items-center gap-3 justify-end">
        <RouterLink to="/purchase-orders" class="px-4 py-2.5 rounded-xl border border-border text-sm font-medium hover:bg-muted transition-colors">Annuler</RouterLink>
        <button type="submit" class="btn-primary px-5 py-2.5 rounded-xl text-sm font-semibold text-white">Créer la commande</button>
      </div>
    </form>
  </div>
</template>
<script setup lang="ts">
import { reactive, computed, onMounted, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { Plus, ShoppingCart, Trash2 } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import { supplierService, type Supplier } from '@/services/supplier.service'
import WarehouseService, { type Warehouse } from '@/services/warehouse.service'
import ProductService, { type Product } from '@/services/product.service'
import { formatCurrency } from '@/utils/formatters'
import { purchaseOrderService } from '@/services/operations.service'
const router = useRouter()
const suppliers = reactive<Supplier[]>([])
const warehouses = reactive<Warehouse[]>([])
const products = reactive<Product[]>([])
const loading = ref(false)
onMounted(async () => {
  suppliers.push(...(await supplierService.findAllList()).filter(s => s.actif))
  warehouses.push(...(await WarehouseService.getAll()).filter(w => w.actif))
  products.push(...(await ProductService.getAll(0, 200)).content.filter(p => p.actif))
})
const form = reactive({ fournisseurId: null as number | null, entrepotId: warehouses[0]?.id ?? 1, dateLivraisonPrevue: '', note: '', lignes: [] as { produitRef: string; quantite: number; prixUnitaire: number }[] })
function addLine() { form.lignes.push({ produitRef: products[0]?.reference ?? '', quantite: 1, prixUnitaire: 0 }) }
const total = computed(() => form.lignes.reduce((s, l) => s + l.quantite * l.prixUnitaire, 0))
async function submit() {
  if (!form.fournisseurId || !form.entrepotId || !form.lignes.length) return
  loading.value = true
  try {
    await purchaseOrderService.create({
      fournisseurId: form.fournisseurId,
      entrepotDestinationId: form.entrepotId,
      dateLivraisonPrevue: form.dateLivraisonPrevue || undefined,
      note: form.note,
      lignes: form.lignes.map(l => ({
        produitId: products.find(p => p.reference === l.produitRef)!.id,
        quantiteCommandee: l.quantite,
        prixUnitaire: l.prixUnitaire
      }))
    })
    router.push('/purchase-orders')
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>
