<template>
  <div>
    <div class="mb-8">
      <h2 class="text-2xl font-bold text-white mb-1">Inscription</h2>
      <p class="text-green-200/50 text-sm">Créez un compte pour accéder à StockMaster</p>
    </div>

    <form @submit.prevent="handleRegister" class="space-y-4">
      <div>
        <label class="block text-xs font-medium mb-1.5" style="color: rgba(167,243,208,0.7);">Nom d'utilisateur</label>
        <div class="relative">
          <input v-model="username" required placeholder="nom d'utilisateur" class="w-full pl-4 pr-4 py-3 rounded-xl text-sm text-white placeholder:text-white/25 transition-all duration-150 focus:outline-none" style="background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.1);" />
        </div>
      </div>

      <div>
        <label class="block text-xs font-medium mb-1.5" style="color: rgba(167,243,208,0.7);">Email</label>
        <div class="relative">
          <input v-model="email" type="email" required placeholder="votre@email.com" class="w-full pl-4 pr-4 py-3 rounded-xl text-sm text-white placeholder:text-white/25 transition-all duration-150 focus:outline-none" style="background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.1);" />
        </div>
      </div>

      <div>
        <label class="block text-xs font-medium mb-1.5" style="color: rgba(167,243,208,0.7);">Nom complet</label>
        <div class="relative">
          <input v-model="fullName" placeholder="Prénom Nom" class="w-full pl-4 pr-4 py-3 rounded-xl text-sm text-white placeholder:text-white/25 transition-all duration-150 focus:outline-none" style="background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.1);" />
        </div>
      </div>

      <div>
        <label class="block text-xs font-medium mb-1.5" style="color: rgba(167,243,208,0.7);">Mot de passe</label>
        <div class="relative">
          <input v-model="password" type="password" required placeholder="••••••••" class="w-full pl-4 pr-4 py-3 rounded-xl text-sm text-white placeholder:text-white/25 transition-all duration-150 focus:outline-none" style="background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.1);" />
        </div>
      </div>

      <div>
        <label class="block text-xs font-medium mb-1.5" style="color: rgba(167,243,208,0.7);">Rôle</label>
        <div class="relative">
          <select v-model="role" class="w-full pl-4 pr-4 py-3 rounded-xl text-sm text-white transition-all duration-150 focus:outline-none" style="background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.1);">
            <option value="ADMINISTRATEUR">Administrateur</option>
            <option value="GESTIONNAIRE">Gestionnaire</option>
            <option value="MAGASINIER">Magasinier</option>
            <option value="AUDITEUR">Auditeur</option>
          </select>
        </div>
      </div>

      <div v-if="error" class="text-red-300 text-sm">{{ error }}</div>

      <button type="submit" :disabled="loading" class="w-full py-3 rounded-xl text-sm font-semibold text-white relative overflow-hidden transition-all duration-150" :style="loading ? 'background: rgba(5,150,105,0.5); cursor: not-allowed;' : 'background: linear-gradient(135deg, #059669 0%, #10b981 100%); box-shadow: 0 4px 12px rgba(5,150,105,0.35);'">
        <span v-if="!loading">S'inscrire</span>
        <span v-else>Inscription...</span>
      </button>
    </form>

    <p class="mt-6 text-xs">Déjà un compte ? <a @click.prevent="goLogin" class="text-green-300 cursor-pointer">Se connecter</a></p>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AuthService from '@/services/auth.service'

const router = useRouter()
const username = ref('')
const email = ref('')
const fullName = ref('')
const password = ref('')
const role = ref('GESTIONNAIRE')
const loading = ref(false)
const error = ref('')

async function handleRegister() {
  loading.value = true
  error.value = ''
  try {
    const resp = await AuthService.register({ username: username.value, password: password.value, email: email.value, fullName: fullName.value, role: role.value })
    // store tokens already done by service
    router.push('/dashboard')
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors de l\'inscription'
  } finally {
    loading.value = false
  }
}

function goLogin() { router.push('/login') }
</script>

<style scoped>
.cursor-pointer { cursor: pointer }
</style>
