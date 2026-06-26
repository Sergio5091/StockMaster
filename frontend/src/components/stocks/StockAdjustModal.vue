<template>
  <!-- Backdrop -->
  <div class="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4" @click.self="$emit('close')">
    <div class="bg-background border border-border rounded-2xl shadow-2xl w-full max-w-md">
      <!-- Header -->
      <div class="flex items-center justify-between px-6 py-4 border-b border-border">
        <div>
          <h2 class="text-base font-semibold text-foreground">Ajustement de stock</h2>
          <p class="text-xs text-muted-foreground mt-0.5">Entrée ou sortie manuelle avec justification</p>
        </div>
        <button @click="$emit('close')" class="p-1.5 rounded-lg hover:bg-muted transition-colors">
          <X :size="18" class="text-muted-foreground" />
        </button>
      </div>

      <!-- Form -->
      <form @submit.prevent="submit" class="px-6 py-5 space-y-4">
        <!-- Pré-rempli si stock passé en prop -->
        <div v-if="stock" class="flex items-center gap-3 p-3 rounded-xl bg-muted/60 border border-border">
          <Package :size="18" class="text-muted-foreground shrink-0" />
          <div>
            <div class="text-sm font-medium text-foreground">{{ stock.produitNom }}</div>
            <div class="text-xs text-muted-foreground">
              {{ stock.entrepotNom }} · Stock actuel :
              <span class="font-semibold" :class="stockColor(stock.statut)">{{ stock.quantiteDisponible }}</span>
            </div>
          </div>
        </div>

        <!-- Direction -->
        <div>
          <label class="block text-xs font-semibold text-muted-foreground uppercase tracking-wide mb-2">Direction</label>
          <div class="grid grid-cols-2 gap-2">
            <button type="button" @click="direction = 'entree'"
              :class="['flex items-center justify-center gap-2 py-2.5 rounded-xl border text-sm font-medium transition-colors',
                direction === 'entree'
                  ? 'bg-emerald-50 dark:bg-emerald-950/40 border-emerald-400 text-emerald-700 dark:text-emerald-400'
                  : 'border-border text-muted-foreground hover:bg-muted']">
              <TrendingUp :size="16" /> Entrée (+)
            </button>
            <button type="button" @click="direction = 'sortie'"
              :class="['flex items-center justify-center gap-2 py-2.5 rounded-xl border text-sm font-medium transition-colors',
                direction === 'sortie'
                  ? 'bg-red-50 dark:bg-red-950/40 border-red-400 text-red-700 dark:text-red-400'
                  : 'border-border text-muted-foreground hover:bg-muted']">
              <TrendingDown :size="16" /> Sortie (-)
            </button>
          </div>
        </div>

        <!-- Quantité -->
        <div>
          <label class="block text-xs font-semibold text-muted-foreground uppercase tracking-wide mb-1.5">Quantité *</label>
          <input v-model.number="form.quantite" type="number" min="1" required
            class="input-field w-full" placeholder="Ex: 10" />
        </div>

        <!-- Justification -->
        <div>
          <label class="block text-xs font-semibold text-muted-foreground uppercase tracking-wide mb-1.5">Justification *</label>
          <textarea v-model="form.justification" rows="3" required
            class="input-field w-full resize-none"
            placeholder="Ex: Correction d'erreur de comptage, retour fournisseur…" />
        </div>

        <!-- Erreur -->
        <div v-if="error" class="flex items-start gap-2 p-3 rounded-xl bg-red-50 dark:bg-red-950/30 border border-red-200 dark:border-red-800 text-red-600 dark:text-red-400 text-sm">
          <AlertCircle :size="16" class="shrink-0 mt-0.5" />
          <span>{{ error }}</span>
        </div>

        <!-- Actions -->
        <div class="flex gap-3 pt-1">
          <button type="button" @click="$emit('close')"
            class="flex-1 py-2.5 rounded-xl border border-border text-sm font-medium hover:bg-muted transition-colors">
            Annuler
          </button>
          <button type="submit" :disabled="loading"
            :class="['flex-1 py-2.5 rounded-xl text-sm font-medium transition-colors disabled:opacity-60 flex items-center justify-center gap-2',
              direction === 'entree'
                ? 'bg-emerald-600 hover:bg-emerald-700 text-white'
                : 'bg-red-600 hover:bg-red-700 text-white']">
            <div v-if="loading" class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
            <span v-else>{{ direction === 'entree' ? 'Confirmer l\'entrée' : 'Confirmer la sortie' }}</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { X, Package, TrendingUp, TrendingDown, AlertCircle } from 'lucide-vue-next'
import { stockMovementService, type StockItem } from '@/services/stock.service'

const props = defineProps<{
  stock?: StockItem
}>()

const emit = defineEmits<{
  close: []
  adjusted: []
}>()

const direction = ref<'entree' | 'sortie'>('entree')
const form = reactive({ quantite: null as number | null, justification: '' })
const loading = ref(false)
const error = ref('')

function stockColor(statut: string) {
  return ({ critical: 'text-red-600', low: 'text-amber-600', normal: 'text-emerald-600', excess: 'text-blue-600' } as Record<string, string>)[statut] || 'text-foreground'
}

async function submit() {
  if (!form.quantite || form.quantite <= 0) {
    error.value = 'La quantité doit être supérieure à zéro'
    return
  }
  if (!form.justification.trim()) {
    error.value = 'La justification est obligatoire'
    return
  }
  if (!props.stock) {
    error.value = 'Aucun stock sélectionné'
    return
  }

  loading.value = true
  error.value = ''
  try {
    const qte = direction.value === 'entree' ? form.quantite : -form.quantite
    await stockMovementService.adjust({
      produitId: props.stock.produitId,
      entrepotId: props.stock.entrepotId,
      quantite: qte,
      justification: form.justification.trim(),
    })
    emit('adjusted')
    emit('close')
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors de l\'ajustement'
  } finally {
    loading.value = false
  }
}
</script>
