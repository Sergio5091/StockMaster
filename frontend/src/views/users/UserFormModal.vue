<template>
  <Teleport to="body">
    <div class="fixed inset-0 z-50 flex items-center justify-center p-4 modal-overlay" @click.self="$emit('close')">
      <div class="bg-card rounded-2xl shadow-2xl w-full max-w-lg border border-border">
        <div class="flex items-center justify-between px-6 py-4 border-b border-border">
          <h2 class="font-semibold text-foreground">{{ user ? "Modifier l'utilisateur" : 'Nouvel utilisateur' }}</h2>
          <button @click="$emit('close')" class="p-1.5 rounded-lg hover:bg-muted transition-colors text-muted-foreground"><X :size="16" /></button>
        </div>
        <form @submit.prevent="submit" class="p-6 space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div><label class="label-field">Prénom *</label><input v-model="form.prenom" type="text" class="input-field" required /></div>
            <div><label class="label-field">Nom *</label><input v-model="form.nom" type="text" class="input-field" required /></div>
          </div>
          <div><label class="label-field">Email *</label><input v-model="form.email" type="email" class="input-field" required /></div>
          <div><label class="label-field">Rôle *</label>
            <select v-model="form.role" class="select-field" required>
              <option value="ROLE_ADMIN">Administrateur</option>
              <option value="ROLE_MANAGER">Gestionnaire d'entrepôt</option>
              <option value="ROLE_OPERATOR">Magasinier</option>
              <option value="ROLE_AUDITOR">Auditeur</option>
            </select>
          </div>
          <div v-if="form.role === 'ROLE_MANAGER' || form.role === 'ROLE_OPERATOR'">
            <label class="label-field">Entrepôts assignés</label>
            <div class="flex flex-wrap gap-2 mt-1">
              <label v-for="e in ['ENT-001', 'ENT-002', 'ENT-003']" :key="e" class="flex items-center gap-2 text-sm cursor-pointer">
                <input type="checkbox" :value="e" v-model="form.entrepots" class="accent-emerald-600" /> {{ e }}
              </label>
            </div>
          </div>
          <div class="flex items-center gap-3 pt-2 border-t border-border">
            <button type="button" @click="$emit('close')" class="flex-1 py-2.5 rounded-xl border border-border text-sm font-medium text-foreground hover:bg-muted transition-colors">Annuler</button>
            <button type="submit" class="flex-1 btn-primary py-2.5 rounded-xl text-sm font-semibold text-white">{{ user ? 'Enregistrer' : 'Créer' }}</button>
          </div>
        </form>
      </div>
    </div>
  </Teleport>
</template>
<script setup lang="ts">
import { reactive } from 'vue'
import { X } from 'lucide-vue-next'
import { USERS } from '@/services/mockData'
const props = defineProps<{ user: typeof USERS[0] | null }>()
const emit = defineEmits<{ close: []; save: [user: any] }>()
const form = reactive({ id: props.user?.id ?? 0, prenom: props.user?.prenom ?? '', nom: props.user?.nom ?? '', email: props.user?.email ?? '', role: props.user?.role ?? 'ROLE_OPERATOR', entrepots: props.user?.entrepots ?? [] as string[], actif: props.user?.actif ?? true, createdAt: props.user?.createdAt ?? new Date().toISOString().slice(0,10), lastLogin: props.user?.lastLogin ?? '' })
function submit() { emit('save', { ...form }) }
</script>
