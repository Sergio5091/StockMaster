<template>
  <div class="p-6 max-w-[1400px] mx-auto">
    <PageHeader title="Utilisateurs" :subtitle="`${total} utilisateur(s)`">
      <template #actions>
        <button v-if="can('manage_users')" @click="openCreate"
          class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white">
          <UserPlus :size="15" /> Nouvel utilisateur
        </button>
      </template>
    </PageHeader>

    <!-- Filtres -->
    <div class="card-premium rounded-2xl p-4 mb-6 flex flex-wrap gap-3 items-center">
      <div class="relative flex-1 min-w-[200px]">
        <Search :size="15" class="absolute left-3.5 top-1/2 -translate-y-1/2 text-muted-foreground" />
        <input v-model="search" type="search" placeholder="Nom, email, username…" class="input-field pl-10" />
      </div>
      <select v-model="filterRole" class="select-field w-auto min-w-[170px]">
        <option value="">Tous les rôles</option>
        <option value="ADMINISTRATEUR">Administrateur</option>
        <option value="GESTIONNAIRE">Gestionnaire</option>
        <option value="MAGASINIER">Magasinier</option>
        <option value="AUDITEUR">Auditeur</option>
      </select>
      <select v-model="filterStatut" class="select-field w-auto min-w-[130px]">
        <option value="">Tous</option>
        <option value="actif">Actifs</option>
        <option value="inactif">Inactifs</option>
      </select>
    </div>

    <!-- Loader -->
    <div v-if="loading" class="flex justify-center py-20">
      <div class="w-8 h-8 border-4 border-emerald-500 border-t-transparent rounded-full animate-spin" />
    </div>

    <!-- Erreur -->
    <div v-else-if="error" class="card-premium rounded-2xl p-8 text-center text-red-500">
      <p class="font-medium">{{ error }}</p>
      <button @click="load" class="mt-3 text-sm underline">Réessayer</button>
    </div>

    <!-- Table -->
    <div v-else class="card-premium rounded-2xl overflow-hidden">
      <table class="w-full text-sm">
        <thead>
          <tr class="border-b border-border bg-muted/30">
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Utilisateur</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden md:table-cell">Rôle</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden lg:table-cell">Créé le</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Statut</th>
            <th class="px-4 py-3.5"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="u in filtered" :key="u.id" class="table-row-hover border-b border-border/50 last:border-0">
            <td class="px-4 py-3.5">
              <div class="flex items-center gap-3">
                <div class="w-9 h-9 rounded-xl flex items-center justify-center text-white text-xs font-bold shrink-0"
                  :class="roleAvatarBg(u.role)">
                  {{ initials(u) }}
                </div>
                <div>
                  <div class="font-semibold text-foreground">{{ u.fullName || u.username }}</div>
                  <div class="text-xs text-muted-foreground">{{ u.email || u.username }}</div>
                </div>
              </div>
            </td>
            <td class="px-4 py-3.5 hidden md:table-cell">
              <span :class="['inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border', roleBadge(u.role)]">
                {{ roleLabel(u.role) }}
              </span>
            </td>
            <td class="px-4 py-3.5 hidden lg:table-cell text-xs text-muted-foreground">
              {{ u.createdAt ? formatDatetime(u.createdAt) : '—' }}
            </td>
            <td class="px-4 py-3.5">
              <StatusBadge :status="u.active ? 'actif' : 'inactif'" :dot="true" />
            </td>
            <td class="px-4 py-3.5">
              <div class="flex items-center gap-1 justify-end">
                <button v-if="can('manage_users')" @click="openEdit(u)"
                  class="p-1.5 rounded-lg hover:bg-muted text-muted-foreground hover:text-foreground transition-colors" title="Modifier">
                  <Edit3 :size="14" />
                </button>
                <button v-if="can('manage_users') && u.active" @click="deactivate(u)"
                  class="p-1.5 rounded-lg hover:bg-red-50 text-muted-foreground hover:text-red-500 transition-colors" title="Désactiver">
                  <UserX :size="14" />
                </button>
                <button v-if="can('manage_users') && !u.active" @click="activate(u)"
                  class="p-1.5 rounded-lg hover:bg-emerald-50 text-muted-foreground hover:text-emerald-600 transition-colors" title="Activer">
                  <UserCheck :size="14" />
                </button>
                <button v-if="can('manage_users')" @click="openResetPassword(u)"
                  class="p-1.5 rounded-lg hover:bg-amber-50 text-muted-foreground hover:text-amber-600 transition-colors" title="Réinitialiser mot de passe">
                  <KeyRound :size="14" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!filtered.length" class="text-center py-16 text-muted-foreground">
        <Users :size="36" class="mx-auto mb-3 opacity-30" />
        <p class="font-medium">Aucun utilisateur trouvé</p>
      </div>
    </div>

    <!-- Modale reset password -->
    <Teleport to="body">
      <div v-if="showResetModal" class="fixed inset-0 z-50 flex items-center justify-center p-4 modal-overlay" @click.self="showResetModal = false">
        <div class="bg-card rounded-2xl shadow-2xl w-full max-w-sm border border-border">
          <div class="flex items-center justify-between px-6 py-4 border-b border-border">
            <h2 class="font-semibold">Réinitialiser le mot de passe</h2>
            <button @click="showResetModal = false"><X :size="16" class="text-muted-foreground" /></button>
          </div>
          <form @submit.prevent="confirmResetPassword" class="p-6 space-y-4">
            <p class="text-sm text-muted-foreground">
              Nouveau mot de passe pour <span class="font-semibold text-foreground">{{ resetTarget?.fullName || resetTarget?.username }}</span>
            </p>
            <div>
              <label class="label-field">Nouveau mot de passe *</label>
              <input v-model="newPassword" type="password" class="input-field" required minlength="6" />
            </div>
            <div v-if="resetError" class="text-red-500 text-sm">{{ resetError }}</div>
            <div class="flex gap-3 pt-2 border-t border-border">
              <button type="button" @click="showResetModal = false" class="flex-1 py-2.5 rounded-xl border border-border text-sm hover:bg-muted transition-colors">Annuler</button>
              <button type="submit" :disabled="resetting" class="flex-1 btn-primary py-2.5 rounded-xl text-sm font-semibold text-white disabled:opacity-60">
                {{ resetting ? '…' : 'Confirmer' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>

    <UserFormModal v-if="showForm" :user="editingUser" @close="closeForm" @saved="onSaved" />
  </div>
</template>
<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { UserPlus, Search, Edit3, UserX, UserCheck, KeyRound, Users, X } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import UserFormModal from './UserFormModal.vue'
import { formatDatetime } from '@/utils/formatters'
import { usePermissions } from '@/composables/usePermissions'
import { userService, type User } from '@/services/user.service'

const { can } = usePermissions()
const users = ref<User[]>([])
const loading = ref(false)
const error = ref('')
const total = ref(0)
const search = ref('')
const filterRole = ref('')
const filterStatut = ref('')
const showForm = ref(false)
const editingUser = ref<User | null>(null)
const showResetModal = ref(false)
const resetTarget = ref<User | null>(null)
const newPassword = ref('')
const resetting = ref(false)
const resetError = ref('')

const filtered = computed(() => users.value.filter(u => {
  if (search.value && !`${u.fullName || ''} ${u.username} ${u.email || ''}`.toLowerCase().includes(search.value.toLowerCase())) return false
  if (filterRole.value && u.role !== filterRole.value) return false
  if (filterStatut.value === 'actif' && !u.active) return false
  if (filterStatut.value === 'inactif' && u.active) return false
  return true
}))

async function load() {
  loading.value = true; error.value = ''
  try {
    const page = await userService.findAll(0, 200)
    users.value = page.content
    total.value = page.totalElements
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors du chargement des utilisateurs'
  } finally {
    loading.value = false
  }
}

function openCreate() { editingUser.value = null; showForm.value = true }
function openEdit(u: User) { editingUser.value = u; showForm.value = true }
function closeForm() { showForm.value = false; editingUser.value = null }
function onSaved() { closeForm(); load() }

async function deactivate(u: User) {
  try {
    await userService.deactivate(u.id)
    u.active = false
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors de la désactivation'
  }
}

async function activate(u: User) {
  try {
    await userService.activate(u.id)
    u.active = true
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors de la réactivation'
  }
}

function openResetPassword(u: User) {
  resetTarget.value = u
  newPassword.value = ''
  resetError.value = ''
  showResetModal.value = true
}

async function confirmResetPassword() {
  if (!resetTarget.value) return
  resetting.value = true; resetError.value = ''
  try {
    await userService.resetPassword(resetTarget.value.id, newPassword.value)
    showResetModal.value = false
  } catch (e: any) {
    resetError.value = e?.response?.data?.message || 'Erreur lors de la réinitialisation'
  } finally {
    resetting.value = false
  }
}

function initials(u: User) {
  if (u.fullName) {
    const parts = u.fullName.trim().split(' ')
    return parts.length >= 2 ? parts[0][0] + parts[1][0] : parts[0].slice(0, 2)
  }
  return u.username.slice(0, 2).toUpperCase()
}
function roleLabel(r: string) {
  return ({ ADMINISTRATEUR: 'Admin', GESTIONNAIRE: 'Gestionnaire', MAGASINIER: 'Magasinier', AUDITEUR: 'Auditeur' } as any)[r] || r
}
function roleBadge(r: string) {
  return ({
    ADMINISTRATEUR: 'bg-purple-50 text-purple-700 border-purple-200',
    GESTIONNAIRE: 'bg-blue-50 text-blue-700 border-blue-200',
    MAGASINIER: 'bg-emerald-50 text-emerald-700 border-emerald-200',
    AUDITEUR: 'bg-amber-50 text-amber-700 border-amber-200',
  } as any)[r] || 'badge-neutral'
}
function roleAvatarBg(r: string) {
  return ({
    ADMINISTRATEUR: 'bg-gradient-to-br from-purple-500 to-purple-600',
    GESTIONNAIRE: 'bg-gradient-to-br from-blue-500 to-blue-600',
    MAGASINIER: 'bg-gradient-to-br from-emerald-500 to-emerald-600',
    AUDITEUR: 'bg-gradient-to-br from-amber-500 to-amber-600',
  } as any)[r] || 'bg-gradient-to-br from-gray-400 to-gray-500'
}

onMounted(load)
</script>
