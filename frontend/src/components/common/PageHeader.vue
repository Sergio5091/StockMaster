<template>
  <div class="flex items-start justify-between gap-4 mb-6">
    <div>
      <div v-if="back" class="flex items-center gap-1.5 text-sm text-muted-foreground hover:text-foreground cursor-pointer mb-2 group" @click="goBack">
        <ArrowLeft :size="14" class="group-hover:-translate-x-0.5 transition-transform" />
        <span>{{ back }}</span>
      </div>
      <h1 class="section-title">{{ title }}</h1>
      <p v-if="subtitle" class="text-sm text-muted-foreground mt-0.5">{{ subtitle }}</p>
    </div>
    <div v-if="$slots.actions" class="flex items-center gap-2 shrink-0 pt-0.5">
      <slot name="actions" />
    </div>
  </div>
</template>
<script setup lang="ts">
import { ArrowLeft } from 'lucide-vue-next'
import { useRouter } from 'vue-router'
defineProps<{ title: string; subtitle?: string; back?: string }>()
const router = useRouter()
function goBack() { router.back() }
</script>
