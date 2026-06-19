<template>
  <div class="p-6 max-w-[800px] mx-auto">
    <PageHeader title="Configuration des alertes" subtitle="Paramétrage des seuils d'alerte" back="Alertes" />
    <div v-if="statusMessage" class="mb-4 rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">
      {{ statusMessage }}
    </div>
    <div class="space-y-4">
      <div v-for="cfg in configs" :key="cfg.type" class="card-premium rounded-2xl p-5">
        <div class="flex items-start justify-between mb-4">
          <div class="flex items-center gap-3">
            <div :class="['w-9 h-9 rounded-xl flex items-center justify-center', cfg.bg]">
              <component :is="cfg.icon" :size="16" :class="cfg.color" />
            </div>
            <div><div class="font-semibold text-foreground">{{ cfg.label }}</div><div class="text-xs text-muted-foreground">{{ cfg.description }}</div></div>
          </div>
          <label class="flex items-center gap-2 cursor-pointer">
            <input type="checkbox" v-model="cfg.actif" class="accent-emerald-600 w-4 h-4" />
            <span class="text-sm text-muted-foreground">Actif</span>
          </label>
        </div>
        <div v-if="cfg.actif" class="grid grid-cols-2 gap-4">
          <div><label class="label-field">Seuil (%)</label><input v-model.number="cfg.seuil" type="number" min="0" max="100" class="input-field" /></div>
          <div><label class="label-field">Email supplémentaire</label><input v-model="cfg.email" type="email" class="input-field" placeholder="optionnel" /></div>
        </div>
      </div>
      <div class="flex justify-end">
        <button @click="save" class="btn-primary px-6 py-2.5 rounded-xl text-sm font-semibold text-white flex items-center gap-2"><Save :size="15" /> Enregistrer</button>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { AlertTriangle, TrendingUp, Layers, Clock, Save } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
const STORAGE_KEY = 'stockmaster-alert-configs'
const statusMessage = ref('')
const baseConfigs = [
  { type: 'stock_critical', label: 'Stock critique', description: 'Alerte si stock < minimum', actif: true, seuil: 0, email: '', icon: AlertTriangle, bg: 'bg-red-100', color: 'text-red-500' },
  { type: 'stock_excess', label: 'Stock excédentaire', description: 'Alerte si stock > maximum', actif: true, seuil: 0, email: '', icon: TrendingUp, bg: 'bg-blue-100', color: 'text-blue-500' },
  { type: 'zone_full', label: 'Zone saturée', description: "Alerte si occupation > seuil", actif: true, seuil: 90, email: '', icon: Layers, bg: 'bg-amber-100', color: 'text-amber-500' },
  { type: 'order_late', label: 'Commande en retard', description: 'Alerte si date livraison dépassée', actif: true, seuil: 0, email: '', icon: Clock, bg: 'bg-orange-100', color: 'text-orange-500' },
]

const configs = ref(baseConfigs.map(cfg => ({ ...cfg })))

function loadSavedConfigs() {
  const raw = localStorage.getItem(STORAGE_KEY)
  if (!raw) return
  try {
    const saved = JSON.parse(raw) as Array<{ type: string; actif: boolean; seuil: number; email: string }>
    configs.value = baseConfigs.map(cfg => {
      const found = saved.find(item => item.type === cfg.type)
      return found ? { ...cfg, actif: found.actif, seuil: found.seuil, email: found.email } : { ...cfg }
    })
  } catch {
    localStorage.removeItem(STORAGE_KEY)
  }
}

function save() {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(configs.value.map(({ type, actif, seuil, email }) => ({ type, actif, seuil, email }))))
  statusMessage.value = 'Configuration enregistrée localement'
}

onMounted(loadSavedConfigs)
</script>
