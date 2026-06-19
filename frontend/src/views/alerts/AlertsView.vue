<template>
  <div class="p-6 max-w-[1200px] mx-auto">
    <PageHeader title="Alertes" :subtitle="`${unread} alerte(s) non traitée(s)`">
      <template #actions>
        <button v-if="can('configure_alerts')" @click="$router.push('/alerts/config')"
          class="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-muted border border-border text-sm font-medium hover:bg-muted/80 transition-colors">
          <Settings :size="15" /> Configurer
        </button>
        <button @click="treatAll" :disabled="saving"
          class="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-muted border border-border text-sm font-medium hover:bg-muted/80 transition-colors disabled:opacity-50">
          <CheckCheck :size="15" /> Tout traiter
        </button>
      </template>
    </PageHeader>

    <!-- Filtres type -->
    <div class="flex gap-2 mb-6 flex-wrap">
      <button v-for="f in typeFilters" :key="f.val" @click="filterType = f.val"
        :class="['flex items-center gap-1.5 px-3.5 py-2 rounded-xl text-sm font-medium border transition-all',
          filterType === f.val ? 'border-foreground bg-foreground text-background' : 'border-border text-muted-foreground hover:border-foreground/40']">
        <span :class="f.dot" class="w-2 h-2 rounded-full" />
        {{ f.label }}
        <span v-if="f.count > 0" class="text-xs">{{ f.count }}</span>
      </button>
    </div>

    <div v-if="loading" class="flex justify-center py-20">
      <div class="w-8 h-8 border-4 border-emerald-500 border-t-transparent rounded-full animate-spin" />
    </div>

    <div v-else-if="error" class="card-premium rounded-2xl p-8 text-center text-red-500">
      <p class="font-medium">{{ error }}</p>
      <button @click="load" class="mt-3 text-sm underline">Réessayer</button>
    </div>

    <div v-else class="space-y-3">
      <div v-for="a in filtered" :key="a.id"
        :class="['card-premium rounded-2xl overflow-hidden transition-all duration-200',
          !a.traitee ? 'border-l-4' : 'opacity-70', alertBorderColor(a.type)]">
        <div class="px-5 py-4 flex items-start gap-4">
          <div :class="['w-10 h-10 rounded-xl flex items-center justify-center shrink-0', alertIconBg(a.type)]">
            <component :is="alertIcon(a.type)" :size="18" :class="alertIconColor(a.type)" />
          </div>
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2 mb-1">
              <span class="font-semibold text-foreground">{{ a.titre }}</span>
              <StatusBadge :status="a.type" />
              <span v-if="!a.traitee" class="text-xs px-2 py-0.5 rounded-full bg-red-100 text-red-600 font-medium">Non traitée</span>
            </div>
            <p class="text-sm text-muted-foreground">{{ a.message }}</p>
            <div class="flex items-center gap-3 mt-2 text-xs text-muted-foreground">
              <span v-if="a.entrepotNom" class="flex items-center gap-1"><Warehouse :size="11" />{{ a.entrepotNom }}</span>
              <span>{{ formatDatetime(a.createdAt) }}</span>
            </div>
          </div>
          <div class="flex items-center gap-2 shrink-0">
            <button v-if="!a.traitee" @click="treat(a)"
              class="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs font-medium hover:bg-emerald-100 transition-colors">
              <Check :size="12" /> Traiter
            </button>
            <button v-else @click="reopen(a)"
              class="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-muted border border-border text-muted-foreground text-xs font-medium hover:bg-muted/80 transition-colors">
              <RotateCcw :size="12" /> Rouvrir
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="!filtered.length && !loading" class="text-center py-20 text-muted-foreground">
      <Bell :size="36" class="mx-auto mb-3 opacity-30" />
      <p class="font-medium">Aucune alerte dans cette catégorie</p>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Settings, CheckCheck, Check, RotateCcw, Bell, Warehouse, AlertTriangle, TrendingUp, Layers, Clock } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import WarehouseService, { type Alert } from '@/services/warehouse.service'
import { usePermissions } from '@/composables/usePermissions'

const { can } = usePermissions()
const alerts = ref<Alert[]>([])
const loading = ref(false)
const saving = ref(false)
const error = ref('')
const filterType = ref('')

function formatDatetime(d: string) {
  if (!d) return '—'
  return new Date(d).toLocaleString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

const unread = computed(() => alerts.value.filter(a => !a.traitee).length)
const filtered = computed(() => filterType.value ? alerts.value.filter(a => a.type === filterType.value) : alerts.value)

const typeFilters = computed(() => [
  { val: '', label: 'Toutes', dot: 'bg-muted-foreground', count: unread.value },
  { val: 'STOCK_CRITIQUE', label: 'Critique', dot: 'bg-red-500', count: alerts.value.filter(a => a.type === 'STOCK_CRITIQUE' && !a.traitee).length },
  { val: 'COMMANDE_EN_RETARD', label: 'Retard cmd.', dot: 'bg-red-400', count: alerts.value.filter(a => a.type === 'COMMANDE_EN_RETARD' && !a.traitee).length },
  { val: 'ZONE_SATUREE', label: 'Zone saturée', dot: 'bg-amber-500', count: alerts.value.filter(a => a.type === 'ZONE_SATUREE' && !a.traitee).length },
  { val: 'STOCK_EXCESSIF', label: 'Excédent', dot: 'bg-blue-500', count: alerts.value.filter(a => a.type === 'STOCK_EXCESSIF' && !a.traitee).length },
])

async function load() {
  loading.value = true; error.value = ''
  try {
    const page = await WarehouseService.getAlerts(0, 100)
    alerts.value = page.content
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors du chargement des alertes'
  } finally {
    loading.value = false
  }
}

async function treat(a: Alert) {
  try {
    const updated = await WarehouseService.treatAlert(a.id)
    const idx = alerts.value.findIndex(x => x.id === a.id)
    if (idx >= 0) alerts.value[idx] = updated
  } catch { /* silencieux */ }
}

async function reopen(a: Alert) {
  try {
    const updated = await WarehouseService.reopenAlert(a.id)
    const idx = alerts.value.findIndex(x => x.id === a.id)
    if (idx >= 0) alerts.value[idx] = updated
  } catch { /* silencieux */ }
}

async function treatAll() {
  saving.value = true
  try {
    await WarehouseService.treatAllAlerts()
    await load()
  } finally {
    saving.value = false
  }
}

function alertIcon(t: string) {
  return ({ STOCK_CRITIQUE: AlertTriangle, STOCK_EXCESSIF: TrendingUp, ZONE_SATUREE: Layers, COMMANDE_EN_RETARD: Clock } as any)[t] || Bell
}
function alertIconBg(t: string) {
  return ({ STOCK_CRITIQUE: 'bg-red-100', STOCK_EXCESSIF: 'bg-blue-100', ZONE_SATUREE: 'bg-amber-100', COMMANDE_EN_RETARD: 'bg-orange-100' } as any)[t] || 'bg-muted'
}
function alertIconColor(t: string) {
  return ({ STOCK_CRITIQUE: 'text-red-500', STOCK_EXCESSIF: 'text-blue-500', ZONE_SATUREE: 'text-amber-500', COMMANDE_EN_RETARD: 'text-orange-500' } as any)[t] || 'text-muted-foreground'
}
function alertBorderColor(t: string) {
  return ({ STOCK_CRITIQUE: 'border-red-400', STOCK_EXCESSIF: 'border-blue-400', ZONE_SATUREE: 'border-amber-400', COMMANDE_EN_RETARD: 'border-orange-400' } as any)[t] || 'border-border'
}

onMounted(load)
</script>
