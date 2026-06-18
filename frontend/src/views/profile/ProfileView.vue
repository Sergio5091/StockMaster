<template>
  <div class="p-6 max-w-[800px] mx-auto">
    <PageHeader title="Mon profil" subtitle="Informations personnelles et sécurité" />
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <!-- Profile info -->
      <div class="card-premium rounded-2xl p-6 space-y-5">
        <div class="flex items-center gap-4">
          <div class="w-16 h-16 rounded-2xl flex items-center justify-center text-white text-xl font-bold" style="background: linear-gradient(135deg, #059669, #10b981);">
            {{ auth.user?.prenom?.[0] }}{{ auth.user?.nom?.[0] }}
          </div>
          <div><div class="font-bold text-foreground text-lg">{{ auth.fullName }}</div><div class="text-sm text-muted-foreground">{{ auth.roleLabel }}</div></div>
        </div>
        <form @submit.prevent="saveProfile" class="space-y-4">
          <div class="grid grid-cols-2 gap-3">
            <div><label class="label-field">Prénom</label><input v-model="form.prenom" type="text" class="input-field" /></div>
            <div><label class="label-field">Nom</label><input v-model="form.nom" type="text" class="input-field" /></div>
          </div>
          <div><label class="label-field">Email</label><input v-model="form.email" type="email" class="input-field" /></div>
          <button type="submit" class="btn-primary w-full py-2.5 rounded-xl text-sm font-semibold text-white">Enregistrer les modifications</button>
        </form>
      </div>
      <!-- Password & warehouses -->
      <div class="space-y-5">
        <div class="card-premium rounded-2xl p-6">
          <h3 class="font-semibold text-foreground mb-4 flex items-center gap-2"><Lock :size="15" /> Changer le mot de passe</h3>
          <form @submit.prevent="changePassword" class="space-y-3">
            <div><label class="label-field">Ancien mot de passe</label><input v-model="pwForm.ancien" type="password" class="input-field" /></div>
            <div><label class="label-field">Nouveau mot de passe</label><input v-model="pwForm.nouveau" type="password" class="input-field" /></div>
            <div><label class="label-field">Confirmer</label><input v-model="pwForm.confirmer" type="password" class="input-field" /></div>
            <button type="submit" class="w-full py-2.5 rounded-xl border border-border text-sm font-medium hover:bg-muted transition-colors">Changer le mot de passe</button>
          </form>
        </div>
        <div class="card-premium rounded-2xl p-5">
          <h3 class="font-semibold text-foreground mb-3 flex items-center gap-2"><Warehouse :size="15" class="text-emerald-600" /> Entrepôts assignés</h3>
          <div v-if="auth.user?.entrepots.length" class="flex flex-wrap gap-2">
            <span v-for="e in auth.user.entrepots" :key="e.id" class="px-3 py-1.5 rounded-lg bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs font-medium">{{ e.code }}</span>
          </div>
          <p v-else class="text-sm text-muted-foreground">Aucun entrepôt assigné</p>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { reactive } from 'vue'
import { Lock, Warehouse } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import { useAuthStore } from '@/stores/auth'
const auth = useAuthStore()
const form = reactive({ prenom: auth.user?.prenom ?? '', nom: auth.user?.nom ?? '', email: auth.user?.email ?? '' })
const pwForm = reactive({ ancien: '', nouveau: '', confirmer: '' })
function saveProfile() { auth.updateProfile({ prenom: form.prenom, nom: form.nom, email: form.email }) }
function changePassword() { if (pwForm.nouveau !== pwForm.confirmer) { alert('Les mots de passe ne correspondent pas'); return }; Object.assign(pwForm, { ancien: '', nouveau: '', confirmer: '' }); alert('Mot de passe modifié') }
</script>
