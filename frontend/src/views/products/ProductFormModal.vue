<template>
  <Teleport to="body">
    <div class="fixed inset-0 z-50 flex items-center justify-center p-4 modal-overlay" @click.self="$emit('close')">
      <div class="bg-card rounded-2xl shadow-2xl w-full max-w-2xl border border-border max-h-[90vh] flex flex-col">
        <div class="flex items-center justify-between px-6 py-4 border-b border-border shrink-0">
          <h2 class="font-semibold">{{ product ? 'Modifier le produit' : 'Nouveau produit' }}</h2>
          <button @click="$emit('close')" class="p-1.5 rounded-lg hover:bg-muted transition-colors text-muted-foreground">
            <X :size="16" />
          </button>
        </div>

        <form @submit.prevent="submit" class="p-6 space-y-4 overflow-y-auto">
          <div class="grid grid-cols-2 gap-4">
            <div class="col-span-2">
              <label class="label-field">Nom du produit *</label>
              <input v-model="form.nom" class="input-field" required />
            </div>
            <div>
              <label class="label-field">Catégorie *</label>
              <select v-model="form.categorieId" class="select-field" required>
                <option value="">Sélectionnez une catégorie</option>
                <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.nom }}</option>
              </select>
            </div>
            <div>
              <label class="label-field">Fournisseur principal</label>
              <select v-model="form.fournisseurPrincipalId" class="select-field">
                <option value="">Sélectionnez un fournisseur</option>
                <option v-for="f in suppliers" :key="f.id" :value="f.id">{{ f.nom }}</option>
              </select>
            </div>
            <div>
              <label class="label-field">Prix d'achat (€)</label>
              <input v-model.number="form.prixAchat" type="number" step="0.01" min="0" class="input-field" />
            </div>
            <div>
              <label class="label-field">Prix de vente (€)</label>
              <input v-model.number="form.prixVente" type="number" step="0.01" min="0" class="input-field" />
            </div>
            <div>
              <label class="label-field">Stock minimum</label>
              <input v-model.number="form.stockMinimum" type="number" min="0" class="input-field" />
            </div>
            <div>
              <label class="label-field">Stock maximum</label>
              <input v-model.number="form.stockMaximum" type="number" min="0" class="input-field" />
            </div>
            <div>
              <label class="label-field">Unité de mesure</label>
              <select v-model="form.uniteMesure" class="select-field">
                <option value="UNITE">Unité</option>
                <option value="KG">Kilogramme</option>
                <option value="LITRE">Litre</option>
                <option value="METRE">Mètre</option>
                <option value="CARTON">Carton</option>
              </select>
            </div>
            <div>
              <label class="label-field">Code-barres</label>
              <input v-model="form.codeBarre" class="input-field" placeholder="Optionnel" />
            </div>
            <div class="col-span-2">
              <label class="label-field">Description</label>
              <textarea v-model="form.description" class="input-field h-20 resize-none" />
            </div>

            <!-- Upload image (seulement en mode édition) -->
            <div v-if="product" class="col-span-2">
              <label class="label-field">Image produit</label>
              <div class="flex items-center gap-3">
                <img v-if="product.imageUrl" :src="product.imageUrl" class="w-12 h-12 rounded-lg object-cover border border-border" />
                <input type="file" accept="image/*" @change="onFileChange" class="input-field" />
              </div>
            </div>
          </div>

          <div v-if="formError" class="text-red-500 text-sm">{{ formError }}</div>

          <div class="flex gap-3 pt-2 border-t border-border">
            <button type="button" @click="$emit('close')" class="flex-1 py-2.5 rounded-xl border border-border text-sm hover:bg-muted transition-colors">
              Annuler
            </button>
            <button type="submit" :disabled="saving" class="flex-1 btn-primary py-2.5 rounded-xl text-sm font-semibold text-white disabled:opacity-60">
              {{ saving ? '…' : (product ? 'Enregistrer' : 'Créer') }}
            </button>
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
import ProductService, { type Product, type ProductCreateDTO } from '@/services/product.service'

const props = defineProps<{ product: Product | null }>()
const emit = defineEmits<{ close: []; saved: [] }>()

const categories = ref<Category[]>([])
const suppliers = ref<Supplier[]>([])
const saving = ref(false)
const formError = ref('')
const imageFile = ref<File | null>(null)

const form = reactive<ProductCreateDTO & { id?: number; codeBarre?: string; fournisseurPrincipalId?: number }>({
  nom: props.product?.nom ?? '',
  description: props.product?.description ?? '',
  categorieId: props.product?.categorieId ?? ('' as any),
  codeBarre: props.product?.codeBarre ?? '',
  fournisseurPrincipalId: props.product?.fournisseurPrincipalId,
  prixAchat: props.product?.prixAchat,
  prixVente: props.product?.prixVente,
  poidsKg: props.product?.poidsKg,
  volumeM3: props.product?.volumeM3,
  stockMinimum: props.product?.stockMinimum ?? 0,
  stockMaximum: props.product?.stockMaximum ?? 9999,
  uniteMesure: props.product?.uniteMesure ?? 'UNITE',
})

function onFileChange(e: Event) {
  const input = e.target as HTMLInputElement
  if (input.files?.length) imageFile.value = input.files[0]
}

async function submit() {
  saving.value = true; formError.value = ''
  try {
    const payload: ProductCreateDTO = {
      nom: form.nom,
      description: form.description,
      categorieId: Number(form.categorieId),
      codeBarre: form.codeBarre || undefined,
      fournisseurPrincipalId: form.fournisseurPrincipalId || undefined,
      prixAchat: form.prixAchat,
      prixVente: form.prixVente,
      poidsKg: form.poidsKg,
      volumeM3: form.volumeM3,
      stockMinimum: form.stockMinimum,
      stockMaximum: form.stockMaximum,
      uniteMesure: form.uniteMesure,
    }

    let saved: Product
    if (props.product) {
      saved = await ProductService.update(props.product.id, payload)
    } else {
      saved = await ProductService.create(payload)
    }

    // Upload image si sélectionnée
    if (imageFile.value) {
      await ProductService.uploadImage(saved.id, imageFile.value)
    }

    emit('saved')
  } catch (e: any) {
    formError.value = e?.response?.data?.message || 'Une erreur est survenue'
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  const [cats, sups] = await Promise.all([
    categoryService.findActiveCategories(),
    supplierService.findAllList(),
  ])
  categories.value = cats
  suppliers.value = sups
})
</script>
