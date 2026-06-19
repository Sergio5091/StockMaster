<template>
  <Teleport to="body">
    <div class="fixed inset-0 z-50 flex items-center justify-center p-4 modal-overlay" @click.self="$emit('close')">
      <div class="bg-card rounded-2xl shadow-2xl w-full max-w-lg border border-border">
        <div class="flex items-center justify-between px-6 py-4 border-b border-border">
          <h2 class="font-semibold text-foreground">{{ user ? "Modifier l'utilisateur" : 'Nouvel utilisateur' }}</h2>
          <button @click="$emit('close')" class="p-1.5 rounded-lg hover:bg-muted transition-colors text-muted-foreground">
            <X :size="16" />
          </button>
        </div>
        <form @submit.prevent="submit" class="p-6 space-y-4">
          <div>
            <label class="label-field">Nom d'utilisateur (login) *</label>
            <input v-model="form.username" type="text" class="input-field" required :disabled="!!user" />
          </div>
          <div>
            <label class="label-field">Nom complet</label>
            <input v-model="form.fullName" type="text" class="input-field" />
          </div>
          <div>
            <label class="label-field">Email</label>
            <input v-model="form.email" type="email" class="input-field" />
          </div>
          <div>
            <label class="label-field">Rôle *</label>
            <select v-model="form.role" class="select-field" required>
              <option value="ADMINISTRATEUR">Administrateur</option>
              <option value="GESTIONNAIRE">Gestionnaire d'entrepôt</option>
              <option value="MAGASINIER">Magasinier</option>
              <option value="AUDITEUR">Auditeur</option>
            </select>
          </div>
          <!-- Mot de passe uniquement à la création -->
          <div v-if="!user">
            <label class="label-field">Mot de passe *</label>
            <input v-model="form.password" type="password" class="input-field" required minlength="6" />
          </div>
          <div v-if="formError" class="text-red-500 text-sm">{{ formError }}</div>
          <div class="flex items-center gap-3 pt-2 border-t border-border">
            <button type="button" @click="$emit('close')" class="flex-1 py-2.5 rounded-xl border border-border text-sm font-medium hover:bg-muted transition-colors">
              Annuler
            </button>
            <button type="submit" :disabled="saving" class="flex-1 btn-primary py-2.5 rounded-xl text-sm font-semibold text-white disabled:opacity-60">
              {{ saving ? '…' : (user ? 'Enregistrer' : 'Créer') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </Teleport>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { X } from 'lucide-vue-next'
import { userService, type User, type UserCreateDTO, type UserUpdateDTO } from '@/services/user.service'

const props = defineProps<{ user: User | null }>()
const emit = defineEmits<{ close: []; saved: [] }>()

const saving = ref(false)
const formError = ref('')

const form = reactive({
  username: props.user?.username ?? '',
  fullName: props.user?.fullName ?? '',
  email: props.user?.email ?? '',
  role: props.user?.role ?? 'MAGASINIER' as const,
  password: '',
})

async function submit() {
  saving.value = true; formError.value = ''
  try {
    if (props.user) {
      const dto: UserUpdateDTO = { fullName: form.fullName, email: form.email, role: form.role }
      await userService.update(props.user.id, dto)
    } else {
      const dto: UserCreateDTO = { username: form.username, password: form.password, fullName: form.fullName, email: form.email, role: form.role }
      await userService.create(dto)
    }
    emit('saved')
  } catch (e: any) {
    formError.value = e?.response?.data?.message || 'Une erreur est survenue'
  } finally {
    saving.value = false
  }
}
</script>
