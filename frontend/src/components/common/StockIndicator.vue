<template>
  <div class="flex items-center gap-2">
    <div class="relative w-24 h-1.5 rounded-full bg-muted overflow-hidden">
      <div :class="['absolute inset-y-0 left-0 rounded-full transition-all duration-500', barClass]" :style="{ width: pct + '%' }" />
    </div>
    <span :class="['text-xs font-medium', textClass]">{{ qty }}</span>
    <span v-if="showStatus" :class="['text-xs px-1.5 py-0.5 rounded-md font-medium border', badgeClass]">{{ statusLabel }}</span>
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue'
const props = defineProps<{ qty: number; min: number; max: number; showStatus?: boolean }>()
const pct = computed(() => Math.min(100, Math.round((props.qty / Math.max(1, props.max)) * 100)))
const status = computed(() => {
  if (props.qty < props.min) return 'critical'
  if (props.qty < props.min * 1.5) return 'low'
  if (props.qty > props.max) return 'excess'
  return 'normal'
})
const barClass = computed(() => ({ critical: 'bg-red-500', low: 'bg-amber-400', normal: 'bg-emerald-500', excess: 'bg-blue-400' }[status.value]))
const textClass = computed(() => ({ critical: 'text-red-600', low: 'text-amber-600', normal: 'text-foreground', excess: 'text-blue-600' }[status.value]))
const badgeClass = computed(() => ({ critical: 'bg-red-50 text-red-600 border-red-200', low: 'bg-amber-50 text-amber-600 border-amber-200', normal: 'bg-emerald-50 text-emerald-700 border-emerald-200', excess: 'bg-blue-50 text-blue-600 border-blue-200' }[status.value]))
const statusLabel = computed(() => ({ critical: '⚠ Critique', low: '▼ Faible', normal: '✓ Normal', excess: '▲ Excédent' }[status.value]))
</script>
