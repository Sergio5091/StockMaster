<template>
  <Teleport to="body">
    <div class="fixed inset-0 z-50 flex items-center justify-center p-4 modal-overlay" @click.self="$emit('close')">
      <div class="bg-card rounded-2xl shadow-2xl w-full max-w-2xl border border-border max-h-[90vh] flex flex-col">
        <div class="flex items-center justify-between px-6 py-4 border-b border-border shrink-0">
          <h2 class="font-semibold">{{ product ? 'Modifier le produit' : 'Nouveau produit' }}</h2>
          <button @click="$emit('close')" class="p-1.5 rounded-lg hover:bg-muted transition-colors text-muted-foreground"><X :size="16" /></button>
        </div>
        <form @submit.prevent="submit" class="p-6 space-y-4 overflow-y-auto">
          <div class="grid grid-cols-2 gap-4">
            <div class="col-span-2"><label class="label-field">Nom du produit *</label><input v-model="form.nom" class="input-field" required /></div>
            <div><label class="label-field">Catégorie</label>
              <select v-model="form.categorieId" class="select-field">
                <option value="">Sélectionnez une catégorie</option>
                <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.nom }}</option>
              </select>
            </div>
            <div><label class="label-field">Fournisseur principal</label>
              <select v-model="form.fournisseurId" class="select-field">
                <option value="">Sélectionnez un fournisseur</option>
                <option v-for="f in suppliers" :key="f.id" :value="f.id">{{ f.nom }}</option>
              </select>
            </div>
            <div><label class="label-field">Prix d'achat (€)</label><input v-model.number="form.prixAchat" type="number" step="0.01" class="input-field" /></div>
            <div><label class="label-field">Prix de vente (€)</label><input v-model.number="form.prixVente" type="number" step="0.01" class="input-field" /></div>
            <div><label class="label-field">Stock minimum</label><input v-model.number="form.stockMinimum" type="number" class="input-field" /></div>
            <div><label class="label-field">Stock maximum</label><input v-model.number="form.stockMaximum" type="number" class="input-field" /></div>
            <div><label class="label-field">Unité de mesure</label>
              <select v-model="form.uniteMesure" class="select-field">
                <option value="UNITE">Unité</option><option value="KG">Kilogramme</option><option value="LITRE">Litre</option><option value="METRE">Mètre</option><option value="CARTON">Carton</option>
              </select>
            </div>
            <div><label class="label-field">Code-barres</label><input v-model="form.codeBarres" class="input-field" placeholder="Optionnel" /></div>
            <div class="col-span-2"><label class="label-field">Description</label><textarea v-model="form.description" class="input-field h-20 resize-none" /></div>
          </div>
          <div class="flex gap-3 pt-2 border-t border-border">
            <button type="button" @click="$emit('close')" class="flex-1 py-2.5 rounded-xl border border-border text-sm hover:bg-muted transition-colors">Annuler</button>
            <button type="submit" class="flex-1 btn-primary py-2.5 rounded-xl text-sm font-semibold text-white">{{ product ? 'Enregistrer' : 'Créer' }}</button>
          </div>
        </form>
      </div>
    </div>
  </Teleport>
</template>
<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { X } from 'lucide-vue-next'
import { categoryService, type Category } from '@/services/category.service'
import { supplierService, type Supplier } from '@/services/supplier.service'

const props = defineProps<{ product: any | null }>()
const emit = defineEmits<{ close: []; save: [p: any] }>()

const categories = ref<Category[]>([])
const suppliers = ref<Supplier[]>([])

const form = reactive({ 
  ...props.product ?? { 
    id: Date.now(), 
    reference: 'PRD-NEW', 
    codeBarres: null, 
    nom: '', 
    description: '', 
    categorieId: null, 
    categorieNom: '', 
    fournisseurId: null, 
    fournisseurNom: '', 
    prixAchat: 0, 
    prixVente: 0, 
    poidsKg: 0, 
    volumeM3: 0, 
    stockMinimum: 5, 
    stockMaximum: 50, 
    uniteMesure: 'UNITE', 
    actif: true, 
    imageUrl: null, 
    stockTotal: 0, 
    stockStatut: 'normal' 
  } 
})

async function loadCategories() {
  try {
    categories.value = await categoryService.findActiveCategories()
  } catch (error) {
    console.error('Erreur chargement catégories:', error)
  }
}

async function loadSuppliers() {
  try {
    suppliers.value = await supplierService.findAll()
  } catch (error) {
    console.error('Erreur chargement fournisseurs:', error)
  }
}

function submit() { 
  emit('save', { ...form }) 
}

onMounted(() => {
  loadCategories()
  loadSuppliers()
})
</script>
