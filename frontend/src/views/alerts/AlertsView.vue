<template>
  <div class="p-6 max-w-[1200px] mx-auto">
    <PageHeader title="Alertes" :subtitle="`${unread} alerte(s) non traitée(s)`">
      <template #actions>
        <button v-if="can('configure_alerts')" @click="$router.push('/alerts/config')" class="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-muted border border-border text-sm font-medium hover:bg-muted/80 transition-colors">
          <Settings :size="15" /> Configurer les alertes
        </button>
        <button @click="markAll" class="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-muted border border-border text-sm font-medium hover:bg-muted/80 transition-colors">
          <CheckCheck :size="15" /> Tout marquer traité
        </button>
      </template>
    </PageHeader>

    <!-- Type filters -->
    <div class="flex gap-2 mb-6 flex-wrap">
      <button v-for="f in typeFilters" :key="f.val" @click="filterType = f.val" :class="['flex items-center gap-1.5 px-3.5 py-2 rounded-xl text-sm font-medium border transition-all', filterType === f.val ? 'border-foreground bg-foreground text-background' : 'border-border text-muted-foreground hover:border-foreground/40']">
        <span :class="f.dot" class="w-2 h-2 rounded-full" />
        {{ f.label }}
        <span v-if="f.count > 0" class="text-xs">{{ f.count }}</span>
      </button>
    </div>

    <div class="space-y-3">
      <div
        v-for="a in filtered" :key="a.id"
        :class="['card-premium rounded-2xl overflow-hidden transition-all duration-200', !a.traitee ? 'border-l-4' : 'opacity-70', alertBorderColor(a.type)]"
      >
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
              <span class="flex items-center gap-1"><Warehouse :size="11" />{{ a.entrepot }}</span>
              <span>{{ formatDatetime(a.createdAt) }}</span>
            </div>
          </div>
          <div class="flex items-center gap-2 shrink-0">
            <button v-if="!a.traitee" @click="treat(a)" class="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs font-medium hover:bg-emerald-100 transition-colors">
              <Check :size="12" /> Traiter
            </button>
            <button v-else @click="a.traitee = false" class="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-muted border border-border text-muted-foreground text-xs font-medium hover:bg-muted/80 transition-colors">
              <RotateCcw :size="12" /> Rouvrir
            </button>
          </div>
        </div>
      </div>
    </div>
    <div v-if="!filtered.length" class="text-center py-20 text-muted-foreground">
      <Bell :size="36" class="mx-auto mb-3 opacity-30" />
      <p class="font-medium">Aucune alerte dans cette catégorie</p>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Settings, CheckCheck, Check, RotateCcw, Bell, Warehouse, AlertTriangle, TrendingUp, Layers, Clock } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { ALERTS, formatDatetime } from '@/services/mockData'
import { usePermissions } from '@/composables/usePermissions'

const { can } = usePermissions()
const alerts = ref([...ALERTS])
const filterType = ref('')

const unread = computed(() => alerts.value.filter(a => !a.traitee).length)
const filtered = computed(() => filterType.value ? alerts.value.filter(a => a.type === filterType.value) : alerts.value)

const typeFilters = computed(() => [
  { val: '', label: 'Toutes', dot: 'bg-muted-foreground', count: alerts.value.filter(a => !a.traitee).length },
  { val: 'stock_critical', label: 'Critique', dot: 'bg-red-500', count: alerts.value.filter(a => a.type === 'stock_critical' && !a.traitee).length },
  { val: 'order_late', label: 'Retard cmd.', dot: 'bg-red-400', count: alerts.value.filter(a => a.type === 'order_late' && !a.traitee).length },
  { val: 'zone_full', label: 'Zone saturée', dot: 'bg-amber-500', count: alerts.value.filter(a => a.type === 'zone_full' && !a.traitee).length },
  { val: 'stock_excess', label: 'Excédent', dot: 'bg-blue-500', count: alerts.value.filter(a => a.type === 'stock_excess' && !a.traitee).length },
])

function alertIcon(t: string) { return { stock_critical: AlertTriangle, stock_excess: TrendingUp, zone_full: Layers, order_late: Clock }[t] || Bell }
function alertIconBg(t: string) { return { stock_critical: 'bg-red-100', stock_excess: 'bg-blue-100', zone_full: 'bg-amber-100', order_late: 'bg-orange-100' }[t] || 'bg-muted' }
function alertIconColor(t: string) { return { stock_critical: 'text-red-500', stock_excess: 'text-blue-500', zone_full: 'text-amber-500', order_late: 'text-orange-500' }[t] || 'text-muted-foreground' }
function alertBorderColor(t: string) { return { stock_critical: 'border-red-400', stock_excess: 'border-blue-400', zone_full: 'border-amber-400', order_late: 'border-orange-400' }[t] || 'border-border' }
function treat(a: typeof ALERTS[0]) { a.traitee = true }
function markAll() { alerts.value.forEach(a => a.traitee = true) }
</script>
