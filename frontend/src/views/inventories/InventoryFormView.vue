<template>
  <div class="p-6 max-w-[700px] mx-auto">
    <PageHeader title="Planifier un inventaire" subtitle="Définir le périmètre du comptage" back="Inventaires" />
    <div v-if="error" class="mb-4 rounded-xl bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">{{ error }}</div>
    <form @submit.prevent="submit" class="space-y-5">
      <div class="card-premium rounded-2xl p-6 space-y-4">
        <div>
          <label class="label-field">Type d'inventaire *</label>
          <select v-model="form.type" class="select-field" required>
            <option value="COMPLET">Complet</option>
            <option value="PARTIEL">Partiel</option>
          </select>
        </div>
        <div>
          <label class="label-field">Entrepôt *</label>
          <select v-model="form.entrepotId" class="select-field" required @change="onEntrepotChange">
            <option :value="null" disabled>— Choisir un entrepôt —</option>
            <option v-for="e in warehouses" :key="e.id" :value="e.id">{{ e.code }} — {{ e.nom }}</option>
          </select>
        </div>
        <template v-if="form.type === 'PARTIEL'">
          <div>
            <label class="label-field">Zone (optionnel)</label>
            <select v-model="form.zoneId" class="select-field" :disabled="loadingZones">
              <option :value="null">— Toute l'entrepôt —</option>
              <option v-for="z in zones" :key="z.id" :value="z.id">{{ z.code }} — {{ z.nom }}</option>
            </select>
          </div>
          <div>
            <label class="label-field">Catégorie (optionnel)</label>
            <select v-model="form.categorieId" class="select-field">
              <option :value="null">— Toutes les catégories —</option>
              <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.nom }}</option>
            </select>
          </div>
        </template>
        <div>
          <label class="label-field">Date planifiée *</label>
          <input v-model="form.datePlanifiee" type="date" class="input-field" required />
        </div>
        <div>
          <label class="label-field">Note</label>
          <textarea v-model="form.note" class="input-field h-20 resize-none" />
        </div>
      </div>
      <div class="flex items-center gap-3 justify-end">
        <RouterLink to="/inventories" class="px-4 py-2.5 rounded-xl border border-border text-sm font-medium hover:bg-muted transition-colors">
          Annuler
        </RouterLink>
        <button type="submit" :disabled="loading" class="btn-primary px-5 py-2.5 rounded-xl text-sm font-semibold text-white disabled:opacity-60 flex items-center gap-2">
          <div v-if="loading" class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
          <span>Planifier l'inventaire</span>
        </button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import PageHeader from '@/components/common/PageHeader.vue'
import WarehouseService, { type Warehouse } from '@/services/warehouse.service'
import { inventoryService } from '@/services/operations.service'
import api from '@/services/api'

const router = useRouter()
const warehouses = ref<Warehouse[]>([])
const zones = ref<any[]>([])
const categories = ref<any[]>([])
const loading = ref(false)
const loadingZones = ref(false)
const error = ref('')

const form = reactive({
  type: 'COMPLET',
  entrepotId: null as number | null,
  zoneId: null as number | null,
  categorieId: null as number | null,
  datePlanifiee: new Date().toISOString().slice(0, 10),
  note: '',
})

async function onEntrepotChange() {
  form.zoneId = null
  zones.value = []
  if (!form.entrepotId) return
  loadingZones.value = true
  try {
    const res = await api.get(`/warehouses/${form.entrepotId}/zones`)
    zones.value = res.data || []
  } catch {
    zones.value = []
  } finally {
    loadingZones.value = false
  }
}

onMounted(async () => {
  const [whs, cats] = await Promise.allSettled([
    WarehouseService.getAll(),
    api.get('/categories/active'),
  ])
  if (whs.status === 'fulfilled') {
    warehouses.value = whs.value.filter((w: any) => w.actif)
    if (warehouses.value.length > 0) {
      form.entrepotId = warehouses.value[0].id
      await onEntrepotChange()
    }
  }
  if (cats.status === 'fulfilled') {
    categories.value = cats.value.data || []
  }
})

async function submit() {
  if (!form.entrepotId || !form.datePlanifiee) return
  loading.value = true
  error.value = ''
  try {
    await inventoryService.create({
      entrepotId: form.entrepotId,
      zoneId: form.zoneId || undefined,
      categorieId: form.categorieId || undefined,
      type: form.type,
      datePlanifiee: form.datePlanifiee,
      note: form.note || undefined,
    })
    router.push('/inventories')
  } catch (e: any) {
    error.value = e?.response?.data?.message || "Erreur lors de la création de l'inventaire"
  } finally {
    loading.value = false
  }
}
</script>
