import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '../services/api'

export interface User {
  id: number
  nom: string
  prenom: string
  email: string
  role: 'ROLE_ADMIN' | 'ROLE_MANAGER' | 'ROLE_OPERATOR' | 'ROLE_AUDITOR'
  actif: boolean
  entrepots: { id: number; nom: string; code: string }[]
  forcePasswordChange?: boolean
}

const DEMO_USERS: Record<string, { password: string; user: User }> = {
  'admin@stockmaster.com': {
    password: 'Admin1234!',
    user: {
      id: 1, nom: 'Dupont', prenom: 'Admin', email: 'admin@stockmaster.com',
      role: 'ROLE_ADMIN', actif: true,
      entrepots: [],
    },
  },
  'manager@stockmaster.com': {
    password: 'Manager1234!',
    user: {
      id: 2, nom: 'Martin', prenom: 'Bernard', email: 'manager@stockmaster.com',
      role: 'ROLE_MANAGER', actif: true,
      entrepots: [],
    },
  },
  'operator@stockmaster.com': {
    password: 'Operator1234!',
    user: {
      id: 3, nom: 'Leroy', prenom: 'Claire', email: 'operator@stockmaster.com',
      role: 'ROLE_OPERATOR', actif: true,
      entrepots: [],
    },
  },
  'auditor@stockmaster.com': {
    password: 'Auditor1234!',
    user: {
      id: 4, nom: 'Moreau', prenom: 'David', email: 'auditor@stockmaster.com',
      role: 'ROLE_AUDITOR', actif: true,
      entrepots: [],
    },
  },
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('sm_token'))
  const user = ref<User | null>(JSON.parse(localStorage.getItem('sm_user') || 'null'))
  const loginAttempts = ref(0)
  const blockedUntil = ref<Date | null>(null)

  const isAuthenticated = computed(() => !!token.value && !!user.value)
  const role = computed(() => user.value?.role)
  const isAdmin = computed(() => role.value === 'ROLE_ADMIN')
  const isManager = computed(() => role.value === 'ROLE_MANAGER')
  const isOperator = computed(() => role.value === 'ROLE_OPERATOR')
  const isAuditor = computed(() => role.value === 'ROLE_AUDITOR')
  const fullName = computed(() => user.value ? `${user.value.prenom} ${user.value.nom}` : '')
  const roleLabel = computed(() => {
    const map: Record<string, string> = {
      ROLE_ADMIN: 'Administrateur',
      ROLE_MANAGER: 'Gestionnaire',
      ROLE_OPERATOR: 'Magasinier',
      ROLE_AUDITOR: 'Auditeur',
    }
    return map[role.value || ''] || ''
  })

  function hasRole(r: string) { return role.value === r }
  function hasAnyRole(roles: string[]) { return roles.includes(role.value || '') }

  async function login(email: string, password: string): Promise<{ success: boolean; error?: string }> {
    if (blockedUntil.value && new Date() < blockedUntil.value) {
      const mins = Math.ceil((blockedUntil.value.getTime() - Date.now()) / 60000)
      return { success: false, error: `Compte bloqué. Réessayez dans ${mins} minute(s).` }
    }

    // Dériver le username depuis l'email (ex: admin@stockmaster.com → admin)
    const username = email.includes('@') ? email.split('@')[0] : email

    try {
      const { data } = await api.post('/auth/login', { username, password })
      loginAttempts.value = 0
      blockedUntil.value = null

      token.value = data.token
      localStorage.setItem('sm_token', data.token)
      if (data.refreshToken) localStorage.setItem('sm_refreshToken', data.refreshToken)

      // Construire l'objet user depuis la réponse backend
      const backendUser = data.user
      const roleMap: Record<string, User['role']> = {
        ADMINISTRATEUR: 'ROLE_ADMIN',
        GESTIONNAIRE: 'ROLE_MANAGER',
        MAGASINIER: 'ROLE_OPERATOR',
        AUDITEUR: 'ROLE_AUDITOR',
      }
      user.value = {
        id: backendUser.id,
        nom: backendUser.fullName?.split(' ').slice(1).join(' ') || '',
        prenom: backendUser.fullName?.split(' ')[0] || '',
        email: backendUser.email,
        role: roleMap[backendUser.role] ?? 'ROLE_OPERATOR',
        actif: true,
        entrepots: [],
      }
      localStorage.setItem('sm_user', JSON.stringify(user.value))
      return { success: true }
    } catch (err: any) {
      loginAttempts.value++
      if (loginAttempts.value >= 5) {
        blockedUntil.value = new Date(Date.now() + 15 * 60 * 1000)
        loginAttempts.value = 0
        return { success: false, error: 'Trop de tentatives. Compte bloqué 15 minutes.' }
      }
      const msg = err?.response?.data?.message || err?.response?.data || 'Identifiants invalides'
      return { success: false, error: `${msg}. Tentative ${loginAttempts.value}/5.` }
    }
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('sm_token')
    localStorage.removeItem('sm_user')
    localStorage.removeItem('sm_refreshToken')
  }

  function updateProfile(updates: Partial<User>) {
    if (user.value) {
      user.value = { ...user.value, ...updates }
      localStorage.setItem('sm_user', JSON.stringify(user.value))
    }
  }

  return {
    token, user, isAuthenticated, role, isAdmin, isManager, isOperator, isAuditor,
    fullName, roleLabel, hasRole, hasAnyRole, login, logout, updateProfile,
  }
})
