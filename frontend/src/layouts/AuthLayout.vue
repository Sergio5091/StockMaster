<template>
  <div class="min-h-screen flex items-stretch" style="background: linear-gradient(135deg, #0a1a0f 0%, #0d2015 40%, #071a0e 100%);">
    <!-- Left panel -->
    <div class="hidden lg:flex lg:w-[52%] flex-col justify-between p-12 relative overflow-hidden">
      <div class="absolute inset-0 opacity-5">
        <svg width="100%" height="100%" xmlns="http://www.w3.org/2000/svg">
          <defs><pattern id="grid" width="48" height="48" patternUnits="userSpaceOnUse"><path d="M 48 0 L 0 0 0 48" fill="none" stroke="#10b981" stroke-width="0.5"/></pattern></defs>
          <rect width="100%" height="100%" fill="url(#grid)" />
        </svg>
      </div>
      <div class="absolute top-1/4 left-1/3 w-96 h-96 rounded-full opacity-10" style="background: radial-gradient(circle, #059669 0%, transparent 70%); filter: blur(40px);"></div>
      <div class="relative z-10">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-xl flex items-center justify-center" style="background: linear-gradient(135deg, #059669, #10b981);">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z" stroke="white" stroke-width="2"/><polyline points="9,22 9,12 15,12 15,22" stroke="white" stroke-width="2"/></svg>
          </div>
          <span class="text-white text-xl font-bold tracking-tight">StockMaster</span>
        </div>
      </div>
      <div class="relative z-10 max-w-sm">
        <h1 class="text-4xl font-bold text-white leading-tight mb-4">
          Gérez vos stocks<br/>
          <span style="background: linear-gradient(135deg, #34d399, #10b981); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text;">en temps réel</span>
        </h1>
        <p class="text-green-200/60 text-lg leading-relaxed mb-8">
          Plateforme multi-entrepôts avec traçabilité complète, alertes intelligentes et reporting avancé.
        </p>
        <div class="flex flex-wrap gap-2">
          <span v-for="feat in features" :key="feat" class="text-xs px-3 py-1.5 rounded-full text-green-300 border" style="border-color: rgba(16,185,129,0.25); background: rgba(16,185,129,0.08);">{{ feat }}</span>
        </div>
      </div>
      <div class="relative z-10 grid grid-cols-3 gap-4">
        <div v-for="stat in stats" :key="stat.label" class="rounded-xl p-4" style="background: rgba(16,185,129,0.06); border: 1px solid rgba(16,185,129,0.12);">
          <div class="text-2xl font-bold text-white mb-0.5">{{ stat.value }}</div>
          <div class="text-xs text-green-300/60">{{ stat.label }}</div>
        </div>
      </div>
    </div>
    <!-- Right panel -->
    <div class="flex-1 flex items-center justify-center p-6 lg:p-12">
      <div class="w-full max-w-md">
        <div class="lg:hidden flex items-center gap-3 mb-8">
          <div class="w-9 h-9 rounded-xl flex items-center justify-center" style="background: linear-gradient(135deg, #059669, #10b981);">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z" stroke="white" stroke-width="2.5"/><polyline points="9,22 9,12 15,12 15,22" stroke="white" stroke-width="2.5"/></svg>
          </div>
          <span class="text-white text-lg font-bold">StockMaster</span>
        </div>
        <RouterView />
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { RouterView } from 'vue-router'
const features = ['Multi-entrepôts', 'Temps réel', 'Bons de réception', 'Transferts', 'Inventaires', 'Alertes']
const stats = [{ value: '17+', label: 'Modules métier' }, { value: '100%', label: 'Traçabilité' }, { value: 'Live', label: 'Synchronisation' }]
</script>
