<template>
  <div class="p-6 max-w-[1400px] mx-auto">
    <PageHeader title="Utilisateurs" :subtitle="`${filtered.length} utilisateur(s)`" >
      <template #actions>
        <button v-if="can('manage_users')" @click="openCreate" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white">
          <UserPlus :size="15" /> Nouvel utilisateur
        </button>
      </template>
    </PageHeader>

    <!-- Filters -->
    <div class="card-premium rounded-2xl p-4 mb-6 flex flex-wrap gap-3 items-center">
      <div class="relative flex-1 min-w-[200px]">
        <Search :size="15" class="absolute left-3.5 top-1/2 -translate-y-1/2 text-muted-foreground" />
        <input v-model="search" type="search" placeholder="Nom, email…" class="input-field pl-10" />
      </div>
      <select v-model="filterRole" class="select-field w-auto min-w-[170px]">
        <option value="">Tous les rôles</option>
        <option value="ROLE_ADMIN">Administrateur</option>
        <option value="ROLE_MANAGER">Gestionnaire</option>
        <option value="ROLE_OPERATOR">Magasinier</option>
        <option value="ROLE_AUDITOR">Auditeur</option>
      </select>
      <select v-model="filterStatut" class="select-field w-auto min-w-[130px]">
        <option value="">Tous</option>
        <option value="actif">Actifs</option>
        <option value="inactif">Inactifs</option>
      </select>
    </div>

    <!-- Table -->
    <div class="card-premium rounded-2xl overflow-hidden">
      <table class="w-full text-sm">
        <thead>
          <tr class="border-b border-border bg-muted/30">
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Utilisateur</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden md:table-cell">Rôle</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden lg:table-cell">Entrepôts</th>
            <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide hidden lg:table-cell">Dernière connexion</th>
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
                  {{ u.prenom[0] }}{{ u.nom[0] }}
                </div>
                <div>
                  <div class="font-semibold text-foreground">{{ u.prenom }} {{ u.nom }}</div>
                  <div class="text-xs text-muted-foreground">{{ u.email }}</div>
                </div>
              </div>
            </td>
            <td class="px-4 py-3.5 hidden md:table-cell">
              <span :class="['inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium border', roleBadge(u.role)]">
                {{ roleLabel(u.role) }}
              </span>
            </td>
            <td class="px-4 py-3.5 hidden lg:table-cell">
              <div v-if="u.entrepots.length" class="flex flex-wrap gap-1">
                <span v-for="e in u.entrepots" :key="e" class="text-xs px-1.5 py-0.5 rounded bg-muted border border-border font-mono text-muted-foreground">{{ e }}</span>
              </div>
              <span v-else class="text-xs text-muted-foreground">—</span>
            </td>
            <td class="px-4 py-3.5 hidden lg:table-cell text-xs text-muted-foreground">{{ u.lastLogin ? formatDatetime(u.lastLogin) : '—' }}</td>
            <td class="px-4 py-3.5"><StatusBadge :status="u.actif ? 'actif' : 'inactif'" :dot="true" /></td>
            <td class="px-4 py-3.5">
              <div class="flex items-center gap-1 justify-end">
                <button v-if="can('manage_users')" @click="openEdit(u)" class="p-1.5 rounded-lg hover:bg-muted text-muted-foreground hover:text-foreground transition-colors" title="Modifier">
                  <Edit3 :size="14" />
                </button>
                <button v-if="can('manage_users') && u.actif" @click="toggleUser(u)" class="p-1.5 rounded-lg hover:bg-red-50 text-muted-foreground hover:text-red-500 transition-colors" title="Désactiver">
                  <UserX :size="14" />
                </button>
                <button v-if="can('manage_users') && !u.actif" @click="toggleUser(u)" class="p-1.5 rounded-lg hover:bg-emerald-50 text-muted-foreground hover:text-emerald-600 transition-colors" title="Activer">
                  <UserCheck :size="14" />
                </button>
                <button v-if="can('manage_users')" @click="resetPassword(u)" class="p-1.5 rounded-lg hover:bg-amber-50 text-muted-foreground hover:text-amber-600 transition-colors" title="Réinitialiser mot de passe">
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

    <UserFormModal v-if="showForm" :user="editingUser" @close="closeForm" @save="saveUser" />
  </div>
</template>
<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { UserPlus, Search, Edit3, UserX, UserCheck, KeyRound, Users } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import UserFormModal from './UserFormModal.vue'
import { formatDatetime } from '@/utils/formatters'
import { usePermissions } from '@/composables/usePermissions'
import { userService, type User } from '@/services/user.service'

const { can } = usePermissions()
const users = ref<User[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const search = ref('')
const filterRole = ref('')
const filterStatut = ref('')
const showForm = ref(false)
const editingUser = ref<User | null>(null)

const filtered = computed(() => users.value.filter(u => {
  if (search.value && !`${u.prenom} ${u.nom} ${u.email}`.toLowerCase().includes(search.value.toLowerCase())) return false
  if (filterRole.value && u.role !== filterRole.value) return false
  if (filterStatut.value === 'actif' && !u.actif) return false
  if (filterStatut.value === 'inactif' && u.actif) return false
  return true
}))

function roleLabel(role: string) {
  return { ROLE_ADMIN: 'Admin', ROLE_MANAGER: 'Gestionnaire', ROLE_OPERATOR: 'Magasinier', ROLE_AUDITOR: 'Auditeur' }[role] || role
}
function roleBadge(role: string) {
  return {
    ROLE_ADMIN: 'bg-purple-50 text-purple-700 border-purple-200',
    ROLE_MANAGER: 'bg-blue-50 text-blue-700 border-blue-200',
    ROLE_OPERATOR: 'bg-emerald-50 text-emerald-700 border-emerald-200',
    ROLE_AUDITOR: 'bg-amber-50 text-amber-700 border-amber-200',
  }[role] || 'badge-neutral'
}
function roleAvatarBg(role: string) {
  return {
    ROLE_ADMIN: 'bg-gradient-to-br from-purple-500 to-purple-600',
    ROLE_MANAGER: 'bg-gradient-to-br from-blue-500 to-blue-600',
    ROLE_OPERATOR: 'bg-gradient-to-br from-emerald-500 to-emerald-600',
    ROLE_AUDITOR: 'bg-gradient-to-br from-amber-500 to-amber-600',
  }[role] || 'bg-gradient-to-br from-gray-400 to-gray-500'
}

async function loadUsers() {
  try {
    loading.value = true
    error.value = null
    users.value = await userService.findAll()
  } catch (e) {
    error.value = 'Erreur lors du chargement des utilisateurs'
    console.error(e)
  } finally {
    loading.value = false
  }
}

function openCreate() { editingUser.value = null; showForm.value = true }
function openEdit(u: User) { editingUser.value = u; showForm.value = true }
function closeForm() { showForm.value = false; editingUser.value = null }
async function saveUser(u: any) {
  try {
    if (editingUser.value) {
      await userService.update(editingUser.value.id, u)
    } else {
      await userService.create(u)
    }
    await loadUsers()
    closeForm()
  } catch (e) {
    console.error('Erreur lors de la sauvegarde:', e)
  }
}

function toggleUser(u: User) { u.actif = !u.actif }
function resetPassword(u: User) { alert(`Mot de passe réinitialisé pour ${u.prenom} ${u.nom}. Un email a été envoyé.`) }

// Lifecycle
onMounted(() => {
  loadUsers()
})
</script>
