<template>
  <RouterLink :to="to" custom v-slot="{ href, navigate, isActive, isExactActive }">
    <a :href="href" @click="navigate" :class="['nav-item', (isActive || isExactActive) ? 'active' : '']" :title="!open ? label : undefined">
      <component :is="iconComponent" :size="18" class="shrink-0 nav-icon" />
      <transition name="fade-item">
        <span v-if="open" class="truncate flex-1">{{ label }}</span>
      </transition>
      <transition name="fade-item">
        <span v-if="open && badge && badge > 0" class="ml-auto shrink-0 text-[10px] font-bold px-1.5 py-0.5 rounded-full" style="background: rgba(239,68,68,0.15); color: #f87171; min-width: 20px; text-align: center;">
          {{ badge > 99 ? '99+' : badge }}
        </span>
      </transition>
    </a>
  </RouterLink>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import * as Icons from 'lucide-vue-next'
const props = defineProps<{ open: boolean; to: string | object; icon: string; label: string; badge?: number }>()
const iconComponent = computed(() => (Icons as Record<string, unknown>)[props.icon])
</script>
<style scoped>
.fade-item-enter-active, .fade-item-leave-active { transition: opacity 0.15s; }
.fade-item-enter-from, .fade-item-leave-to { opacity: 0; }
</style>
