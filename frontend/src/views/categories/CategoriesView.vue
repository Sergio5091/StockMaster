<template>
  <div class="p-6 max-w-[1200px] mx-auto">
    <PageHeader title="Catégories" subtitle="Arborescence des catégories de produits">
      <template #actions>
        <button @click="openCreate" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white">
          <Plus :size="15" /> Nouvelle catégorie
        </button>
      </template>
    </PageHeader>

    <!-- Loader -->
    <div v-if="loading" class="flex justify-center py-20">
      <div class="w-8 h-8 border-4 border-emerald-500 border-t-transparent rounded-full animate-spin" />
    </div>

    <!-- Erreur -->
    <div v-else-if="error" class="card-premium rounded-2xl p-8 text-center text-red-500">
      <p class="font-medium">{{ error }}</p>
      <button @click="load" class="mt-3 text-sm underline">Réessayer</button>
    </div>

    <!-- Liste -->
    <div v-else class="space-y-4">
      <div v-for="parent in parents" :key="parent.id" class="card-premium rounded-2xl overflow-hidden">
        <div class="flex items-center justify-between px-5 py-4 bg-muted/30 border-b border-border">
          <div class="flex items-center gap-3">
            <div class="w-9 h-9 rounded-xl bg-emerald-100 flex items-center justify-center">
              <Tag :size="16" class="text-emerald-600" />
            </div>
            <div>
              <span class="font-semibold text-foreground">{{ parent.nom }}</span>
              <span v-if="parent.description" class="ml-2 text-xs text-muted-foreground italic">{{ parent.description }}</span>
            </div>
          </div>
          <div class="flex items-center gap-2">
            <StatusBadge :status="parent.actif ? 'actif' : 'inactif'" />
            <button @click="openEdit(parent)" class="p-1.5 rounded-lg hover:bg-muted transition-colors" title="Modifier">
              <Edit3 :size="14" class="text-muted-foreground" />
            </button>
            <button @click="toggleActive(parent)" class="p-1.5 rounded-lg hover:bg-muted transition-colors" :title="parent.actif ? 'Désactiver' : 'Réactiver'">
              <EyeOff v-if="parent.actif" :size="14" class="text-muted-foreground" />
              <Eye v-else :size="14" class="text-muted-foreground" />
            </button>
          </div>
        </div>

        <!-- Sous-catégories -->
        <div v-if="children(parent.id).length" class="divide-y divide-border/50">
          <div v-for="child in children(parent.id)" :key="child.id"
            class="flex items-center justify-between px-5 py-3 hover:bg-muted/20 transition-colors">
            <div class="flex items-center gap-3">
              <div class="w-5 border-l-2 border-dashed border-border ml-4 h-4" />
              <div class="w-7 h-7 rounded-lg bg-blue-100 flex items-center justify-center">
                <Tag :size="12" class="text-blue-600" />
              </div>
              <span class="text-sm font-medium text-foreground">{{ child.nom }}</span>
              <span v-if="child.description" class="text-xs text-muted-foreground italic">{{ child.description }}</span>
            </div>
            <div class="flex items-center gap-2">
              <StatusBadge :status="child.actif ? 'actif' : 'inactif'" />
              <button @click="openEdit(child)" class="p-1.5 rounded-lg hover:bg-muted transition-colors">
                <Edit3 :size="13" class="text-muted-foreground" />
              </button>
            </div>
          </div>
        </div>
        <div v-else class="px-5 py-3 text-sm text-muted-foreground italic">Aucune sous-catégorie</div>
      </div>

      <div v-if="!parents.length" class="text-center py-16 text-muted-foreground">
        <Tag :size="36" class="mx-auto mb-3 opacity-30" />
        <p class="font-medium">Aucune catégorie</p>
      </div>
    </div>

    <!-- Modal -->
    <Teleport to="body">
      <div v-if="showForm" class="fixed inset-0 z-50 flex items-center justify-center p-4 modal-overlay" @click.self="closeForm">
        <div class="bg-card rounded-2xl shadow-2xl w-full max-w-md border border-border">
          <div class="flex items-center justify-between px-6 py-4 border-b border-border">
            <h2 class="font-semibold">{{ editingCat ? 'Modifier la catégorie' : 'Nouvelle catégorie' }}</h2>
            <button @click="closeForm"><X :size="16" class="text-muted-foreground" /></button>
          </div>
          <form @submit.prevent="save" class="p-6 space-y-4">
            <div>
              <label class="label-field">Nom *</label>
              <input v-model="form.nom" class="input-field" required />
            </div>
            <div>
              <label class="label-field">Description</label>
              <input v-model="form.description" class="input-field" />
            </div>
            <div>
              <label class="label-field">Catégorie parente</label>
              <select v-model="form.parentId" class="select-field">
                <option :value="undefined">— Catégorie principale —</option>
                <option v-for="p in parents.filter(p => p.id !== editingCat?.id)" :key="p.id" :value="p.id">{{ p.nom }}</option>
              </select>
            </div>
            <div v-if="formError" class="text-red-500 text-sm">{{ formError }}</div>
            <div class="flex gap-3 pt-2 border-t border-border">
              <button type="button" @click="closeForm" class="flex-1 py-2.5 rounded-xl border border-border text-sm hover:bg-muted transition-colors">Annuler</button>
              <button type="submit" :disabled="saving" class="flex-1 btn-primary py-2.5 rounded-xl text-sm font-semibold text-white disabled:opacity-60">
                {{ saving ? '…' : (editingCat ? 'Enregistrer' : 'Créer') }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>
  </div>
</template>
<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue'
import { Plus, Tag, Edit3, Eye, EyeOff, X } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { categoryService, type Category, type CategoryCreateDTO } from '@/services/category.service'

const cats = ref<Category[]>([])
const loading = ref(false)
const error = ref('')
const showForm = ref(false)
const saving = ref(false)
const formError = ref('')
const editingCat = ref<Category | null>(null)

const form = reactive<{ nom: string; description: string; parentId: number | undefined }>({
  nom: '', description: '', parentId: undefined,
})

const parents = computed(() => cats.value.filter(c => !c.parentId))
const children = (pid: number) => cats.value.filter(c => c.parentId === pid)

async function load() {
  loading.value = true; error.value = ''
  try {
    cats.value = await categoryService.findAll()
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors du chargement des catégories'
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingCat.value = null
  Object.assign(form, { nom: '', description: '', parentId: undefined })
  formError.value = ''
  showForm.value = true
}

function openEdit(c: Category) {
  editingCat.value = c
  Object.assign(form, { nom: c.nom, description: c.description ?? '', parentId: c.parentId })
  formError.value = ''
  showForm.value = true
}

function closeForm() {
  showForm.value = false
  editingCat.value = null
}

async function save() {
  saving.value = true; formError.value = ''
  try {
    const payload: CategoryCreateDTO = { nom: form.nom, description: form.description || undefined, parentId: form.parentId }
    if (editingCat.value) {
      await categoryService.update(editingCat.value.id, payload)
    } else {
      await categoryService.create(payload)
    }
    await load()
    closeForm()
  } catch (e: any) {
    formError.value = e?.response?.data?.message || 'Une erreur est survenue'
  } finally {
    saving.value = false
  }
}

async function toggleActive(c: Category) {
  try {
    if (c.actif) {
      await categoryService.delete(c.id)
    } else {
      await categoryService.update(c.id, { nom: c.nom })
    }
    await load()
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors de la mise à jour'
  }
}

onMounted(load)
</script>
