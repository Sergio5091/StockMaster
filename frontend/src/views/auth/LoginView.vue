<template>
  <div>
    <div class="mb-8">
      <h2 class="text-2xl font-bold text-white mb-1">Connexion</h2>
      <p class="text-green-200/50 text-sm">Entrez vos identifiants pour accéder à StockMaster</p>
    </div>
    <!-- Demo accounts -->
    <div class="mb-6 rounded-xl p-3.5" style="background: rgba(16,185,129,0.08); border: 1px solid rgba(16,185,129,0.18);">
      <p class="text-xs font-semibold text-green-300 mb-2.5 uppercase tracking-wide">Comptes de démo</p>
      <div class="grid grid-cols-2 gap-1.5">
        <button v-for="demo in demoAccounts" :key="demo.email" @click="fillDemo(demo)"
          class="text-left p-2 rounded-lg transition-colors text-xs"
          style="background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.06);">
          <div class="font-semibold text-white/80">{{ demo.role }}</div>
          <div class="text-green-300/50 mt-0.5">{{ demo.email.split('@')[0] }}</div>
        </button>
      </div>
    </div>
    <form @submit.prevent="handleLogin" class="space-y-4">
      <Transition name="slide-down">
        <div v-if="error" class="flex items-start gap-2.5 p-3.5 rounded-xl text-sm text-red-300" style="background: rgba(239,68,68,0.12); border: 1px solid rgba(239,68,68,0.25);">
          <AlertCircle :size="16" class="shrink-0 mt-0.5" />
          <span>{{ error }}</span>
        </div>
      </Transition>
      <div>
        <label class="block text-xs font-medium mb-1.5" style="color: rgba(167,243,208,0.7);">Adresse email</label>
        <div class="relative">
          <Mail :size="15" class="absolute left-3.5 top-1/2 -translate-y-1/2" style="color: rgba(110,231,183,0.5);" />
          <input v-model="email" type="email" autocomplete="username" placeholder="votre@email.com" required
            class="w-full pl-10 pr-4 py-3 rounded-xl text-sm text-white placeholder:text-white/25 transition-all duration-150 focus:outline-none"
            style="background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.1);" />
        </div>
      </div>
      <div>
        <label class="block text-xs font-medium mb-1.5" style="color: rgba(167,243,208,0.7);">Mot de passe</label>
        <div class="relative">
          <Lock :size="15" class="absolute left-3.5 top-1/2 -translate-y-1/2" style="color: rgba(110,231,183,0.5);" />
          <input v-model="password" :type="showPassword ? 'text' : 'password'" autocomplete="current-password" placeholder="••••••••" required
            class="w-full pl-10 pr-12 py-3 rounded-xl text-sm text-white placeholder:text-white/25 transition-all duration-150 focus:outline-none"
            style="background: rgba(255,255,255,0.06); border: 1px solid rgba(255,255,255,0.1);" />
          <button type="button" @click="showPassword = !showPassword" class="absolute right-3.5 top-1/2 -translate-y-1/2 transition-colors" style="color: rgba(110,231,183,0.4);">
            <Eye v-if="!showPassword" :size="15" /><EyeOff v-else :size="15" />
          </button>
        </div>
      </div>
      <button type="submit" :disabled="loading" class="w-full py-3 rounded-xl text-sm font-semibold text-white relative overflow-hidden transition-all duration-150"
        :style="loading ? 'background: rgba(5,150,105,0.5); cursor: not-allowed;' : 'background: linear-gradient(135deg, #059669 0%, #10b981 100%); box-shadow: 0 4px 12px rgba(5,150,105,0.35);'">
        <Transition name="btn-content" mode="out-in">
          <span v-if="!loading" class="flex items-center justify-center gap-2">Se connecter <ArrowRight :size="14" /></span>
          <span v-else class="flex items-center justify-center gap-2"><Loader2 :size="14" class="animate-spin" /> Connexion...</span>
        </Transition>
      </button>
    </form>
    <p class="mt-6 text-center text-xs" style="color: rgba(167,243,208,0.3);">StockMaster v1.0 — Gestion de stocks multi-entrepôts</p>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Mail, Lock, Eye, EyeOff, ArrowRight, Loader2, AlertCircle } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
const router = useRouter()
const auth = useAuthStore()
const email = ref(''); const password = ref(''); const showPassword = ref(false); const loading = ref(false); const error = ref('')
const demoAccounts = [
  { role: 'Administrateur', email: 'admin@stockmaster.com', password: 'Admin1234!' },
  { role: 'Gestionnaire', email: 'manager@stockmaster.com', password: 'Manager1234!' },
  { role: 'Magasinier', email: 'operator@stockmaster.com', password: 'Operator1234!' },
  { role: 'Auditeur', email: 'auditor@stockmaster.com', password: 'Auditor1234!' },
]
function fillDemo(demo: { email: string; password: string }) { email.value = demo.email; password.value = demo.password; error.value = '' }
async function handleLogin() {
  loading.value = true; error.value = ''
  const result = await auth.login(email.value, password.value)
  loading.value = false
  if (result.success) router.push('/dashboard')
  else error.value = result.error || 'Erreur de connexion'
}
</script>
<style scoped>
input:focus { outline: none; border-color: rgba(16,185,129,0.5) !important; box-shadow: 0 0 0 3px rgba(16,185,129,0.1); }
.slide-down-enter-active, .slide-down-leave-active { transition: all 0.2s ease; }
.slide-down-enter-from { opacity: 0; transform: translateY(-8px); }
.slide-down-leave-to { opacity: 0; }
.btn-content-enter-active, .btn-content-leave-active { transition: all 0.15s; }
.btn-content-enter-from, .btn-content-leave-to { opacity: 0; }
</style>
