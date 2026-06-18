<template>
  <div class="p-6 max-w-[700px] mx-auto">
    <PageHeader title="Planifier un inventaire" subtitle="Définir le périmètre du comptage" back="Inventaires" />
    <form @submit.prevent="submit" class="space-y-5">
      <div class="card-premium rounded-2xl p-6 space-y-4">
        <div><label class="label-field">Type d'inventaire *</label>
          <select v-model="form.type" class="select-field" required>
            <option value="COMPLET">Complet</option>
            <option value="PARTIEL">Partiel</option>
          </select>
        </div>
        <div><label class="label-field">Entrepôt *</label>
          <select v-model="form.entrepotId" class="select-field" required>
            <option v-for="e in warehouses" :key="e.id" :value="e.id">{{ e.code }} — {{ e.nom }}</option>
          </select>
        </div>
        <div v-if="form.type === 'PARTIEL'"><label class="label-field">Zone (optionnel)</label>
          <select v-model="form.zoneId" class="select-field">
            <option :value="null">— Toute l'entrepôt —</option>
            <option v-for="z in zones" :key="z.id" :value="z.id">{{ z.code }} — {{ z.nom }}</option>
          </select>
        </div>
        <div><label class="label-field">Date planifiée *</label><input v-model="form.datePlanifiee" type="date" class="input-field" required /></div>
        <div><label class="label-field">Note</label><textarea v-model="form.note" class="input-field h-20 resize-none" /></div>
      </div>
      <div class="flex items-center gap-3 justify-end">
        <RouterLink to="/inventories" class="px-4 py-2.5 rounded-xl border border-border text-sm font-medium hover:bg-muted transition-colors">Annuler</RouterLink>
        <button type="submit" class="btn-primary px-5 py-2.5 rounded-xl text-sm font-semibold text-white">Planifier l'inventaire</button>
      </div>
    </form>
  </div>
</template>
<script setup lang="ts">
import { reactive, computed } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import PageHeader from '@/components/common/PageHeader.vue'
import { WAREHOUSES, ZONES } from '@/services/mockData'
const router = useRouter()
const warehouses = WAREHOUSES.filter(w => w.actif)
const form = reactive({ type: 'COMPLET', entrepotId: warehouses[0]?.id ?? 1, zoneId: null as number | null, datePlanifiee: new Date().toISOString().slice(0,10), note: '' })
const zones = computed(() => ZONES.filter(z => z.entrepotId === form.entrepotId))
function submit() { router.push('/inventories') }
</script>
