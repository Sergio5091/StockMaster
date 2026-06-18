<template>
  <div class="px-3 pt-4 pb-1">
    <span class="text-[10px] font-semibold uppercase tracking-widest" style="color: rgba(110,231,183,0.35);">{{ label }}</span>
  </div>
</template>
<script setup lang="ts">
defineProps<{ label: string }>()
</script>
