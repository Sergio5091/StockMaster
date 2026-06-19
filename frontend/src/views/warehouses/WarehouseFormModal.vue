<template>
  <Teleport to="body">
    <div class="fixed inset-0 z-50 flex items-center justify-center p-4 modal-overlay" @click.self="$emit('close')">
      <div class="bg-card rounded-2xl shadow-2xl w-full max-w-lg border border-border">
        <div class="flex items-center justify-between px-6 py-4 border-b border-border">
          <h2 class="font-semibold text-foreground">{{ warehouse ? "Modifier l'entrepôt" : 'Nouvel entrepôt' }}</h2>
          <button @click="$emit('close')" class="p-1.5 rounded-lg hover:bg-muted transition-colors text-muted-foreground">
            <X :size="16" />
          </button>
        </div>
        <form @submit.prevent="submit" class="p-6 space-y-4">
          <div>
            <label class="label-field">Nom de l'entrepôt *</label>
            <input v-model="form.nom" type="text" class="input-field" required />
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div><label class="label-field">Ville *</label><input v-model="form.ville" type="text" class="input-field" required /></div>
            <div><label class="label-field">Pays</label><input v-model="form.pays" type="text" class="input-field" placeholder="France" /></div>
          </div>
          <div>
            <label class="label-field">Adresse</label>
            <input v-model="form.adresse" type="text" class="input-field" />
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div><label class="label-field">Téléphone</label><input v-model="form.telephone" type="tel" class="input-field" /></div>
            <div><label class="label-field">Email</label><input v-model="form.email" type="email" class="input-field" /></div>
          </div>
          <div>
            <label class="label-field">Capacité totale (m³) *</label>
            <input v-model.number="form.capaciteTotale" type="number" class="input-field" required min="1" />
          </div>
          <div v-if="formError" class="text-red-500 text-sm">{{ formError }}</div>
          <div class="flex items-center gap-3 pt-2 border-t border-border">
            <button type="button" @click="$emit('close')" class="flex-1 py-2.5 rounded-xl border border-border text-sm font-medium hover:bg-muted transition-colors">
              Annuler
            </button>
            <button type="submit" :disabled="saving" class="flex-1 btn-primary py-2.5 rounded-xl text-sm font-semibold text-white disabled:opacity-60">
              {{ saving ? '…' : (warehouse ? 'Enregistrer' : 'Créer') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </Teleport>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { X } from 'lucide-vue-next'
import type { Warehouse, WarehouseCreateDTO } from '@/services/warehouse.service'

const props = defineProps<{ warehouse: Warehouse | null }>()
const emit = defineEmits<{ close: []; save: [dto: WarehouseCreateDTO] }>()

const saving = ref(false)
const formError = ref('')

const form = reactive<WarehouseCreateDTO>({
  nom: props.warehouse?.nom ?? '',
  adresse: props.warehouse?.adresse ?? '',
  ville: props.warehouse?.ville ?? '',
  pays: props.warehouse?.pays ?? 'France',
  telephone: props.warehouse?.telephone ?? '',
  email: props.warehouse?.email ?? '',
  responsableId: props.warehouse?.responsableId,
  capaciteTotale: props.warehouse?.capaciteTotale ?? 1000,
})

function submit() {
  emit('save', { ...form })
}
</script>
