<template>
  <div class="p-6 max-w-[1000px] mx-auto">
    <PageHeader title="Nouveau bon de sortie" subtitle="Saisie des articles à sortir" back="Bons de sortie" />
    <form @submit.prevent="submit" class="space-y-6">
      <div class="card-premium rounded-2xl p-6">
        <h3 class="font-semibold text-foreground mb-4">Informations générales</h3>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div><label class="label-field">Entrepôt *</label>
            <select v-model="form.entrepotId" class="select-field" required>
              <option v-for="e in warehouses" :key="e.id" :value="e.id">{{ e.code }} — {{ e.nom }}</option>
            </select>
          </div>
          <div><label class="label-field">Motif *</label>
            <select v-model="form.motif" class="select-field" required>
              <option value="LIVRAISON_CLIENT">Livraison client</option>
              <option value="USAGE_INTERNE">Usage interne</option>
              <option value="DESTRUCTION">Destruction</option>
              <option value="RETOUR">Retour</option>
              <option value="AUTRE">Autre</option>
            </select>
          </div>
          <div v-if="form.motif === 'LIVRAISON_CLIENT'"><label class="label-field">Nom du client</label><input v-model="form.clientNom" type="text" class="input-field" /></div>
          <div v-if="form.motif === 'LIVRAISON_CLIENT'"><label class="label-field">Référence client</label><input v-model="form.clientReference" type="text" class="input-field" /></div>
          <div><label class="label-field">Date de sortie *</label><input v-model="form.dateSortie" type="date" class="input-field" required /></div>
          <div><label class="label-field">Note</label><input v-model="form.note" type="text" class="input-field" /></div>
        </div>
      </div>
      <div class="card-premium rounded-2xl overflow-hidden">
        <div class="px-6 py-4 border-b border-border flex items-center justify-between">
          <h3 class="font-semibold">Lignes de sortie</h3>
          <button type="button" @click="addLine" class="flex items-center gap-1.5 px-3.5 py-2 rounded-xl text-xs font-semibold bg-emerald-50 border border-emerald-200 text-emerald-700 hover:bg-emerald-100 transition-colors"><Plus :size="13" /> Ajouter une ligne</button>
        </div>
        <div class="p-4 space-y-3">
          <div v-if="!form.lignes.length" class="text-center py-10 text-sm text-muted-foreground"><Package :size="28" class="mx-auto mb-2 opacity-30" />Aucune ligne</div>
          <div v-for="(l, i) in form.lignes" :key="i" class="flex items-end gap-3 p-4 rounded-xl bg-muted/30 border border-border/50">
            <div class="flex-1"><label class="label-field">Produit *</label>
              <select v-model="l.produitRef" class="select-field" required>
                <option v-for="p in products" :key="p.reference" :value="p.reference">{{ p.nom }} ({{ p.reference }})</option>
              </select>
            </div>
            <div class="w-32"><label class="label-field">Quantité *</label><input v-model.number="l.quantite" type="number" min="1" class="input-field" required /></div>
            <button type="button" @click="form.lignes.splice(i,1)" class="pb-2.5 p-1.5 rounded-lg text-muted-foreground hover:text-red-500 hover:bg-red-50 transition-colors"><Trash2 :size="15" /></button>
          </div>
        </div>
      </div>
      <div class="flex items-center gap-3 justify-end">
        <RouterLink to="/issues" class="px-4 py-2.5 rounded-xl border border-border text-sm font-medium hover:bg-muted transition-colors">Annuler</RouterLink>
        <button type="submit" class="btn-primary px-5 py-2.5 rounded-xl text-sm font-semibold text-white">Soumettre pour validation</button>
      </div>
    </form>
  </div>
</template>
<script setup lang="ts">
import { reactive } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { Plus, Package, Trash2 } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import { WAREHOUSES, PRODUCTS } from '@/services/mockData'
const router = useRouter()
const warehouses = WAREHOUSES.filter(w => w.actif); const products = PRODUCTS
const form = reactive({ entrepotId: warehouses[0]?.id ?? 1, motif: 'LIVRAISON_CLIENT', clientNom: '', clientReference: '', dateSortie: new Date().toISOString().slice(0,10), note: '', lignes: [] as { produitRef: string; quantite: number }[] })
function addLine() { form.lignes.push({ produitRef: products[0]?.reference ?? '', quantite: 1 }) }
function submit() { router.push('/issues') }
</script>
