<template>
  <div class="p-6 max-w-[1200px] mx-auto">
    <PageHeader title="Catégories" subtitle="Arborescence des catégories de produits">
      <template #actions>
        <button @click="showForm = true" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white">
          <Plus :size="15" /> Nouvelle catégorie
        </button>
      </template>
    </PageHeader>

    <!-- Parent categories -->
    <div class="space-y-4">
      <div v-for="parent in parents" :key="parent.id" class="card-premium rounded-2xl overflow-hidden">
        <div class="flex items-center justify-between px-5 py-4 bg-muted/30 border-b border-border">
          <div class="flex items-center gap-3">
            <div class="w-9 h-9 rounded-xl bg-emerald-100 flex items-center justify-center">
              <Tag :size="16" class="text-emerald-600" />
            </div>
            <div>
              <span class="font-semibold text-foreground">{{ parent.nom }}</span>
              <span class="ml-2 text-xs text-muted-foreground">{{ parent.produits }} produits</span>
            </div>
          </div>
          <div class="flex items-center gap-2">
            <StatusBadge :status="parent.actif ? 'actif' : 'inactif'" />
            <button @click="editCat(parent)" class="p-1.5 rounded-lg hover:bg-muted transition-colors"><Edit3 :size="14" class="text-muted-foreground" /></button>
            <button @click="toggleCat(parent)" class="p-1.5 rounded-lg hover:bg-muted transition-colors">
              <EyeOff v-if="parent.actif" :size="14" class="text-muted-foreground" />
              <Eye v-else :size="14" class="text-muted-foreground" />
            </button>
          </div>
        </div>
        <!-- Children -->
        <div v-if="children(parent.id).length" class="divide-y divide-border/50">
          <div v-for="child in children(parent.id)" :key="child.id" class="flex items-center justify-between px-5 py-3 hover:bg-muted/20 transition-colors">
            <div class="flex items-center gap-3">
              <div class="w-5 border-l-2 border-dashed border-border ml-4 h-4" />
              <div class="w-7 h-7 rounded-lg bg-blue-100 flex items-center justify-center">
                <Tag :size="12" class="text-blue-600" />
              </div>
              <span class="text-sm font-medium text-foreground">{{ child.nom }}</span>
              <span class="text-xs text-muted-foreground">{{ child.produits }} produits</span>
              <span class="text-xs text-muted-foreground italic">{{ child.description }}</span>
            </div>
            <div class="flex items-center gap-2">
              <StatusBadge :status="child.actif ? 'actif' : 'inactif'" />
              <button @click="editCat(child)" class="p-1.5 rounded-lg hover:bg-muted transition-colors"><Edit3 :size="13" class="text-muted-foreground" /></button>
            </div>
          </div>
        </div>
        <div v-else class="px-5 py-3 text-sm text-muted-foreground italic">Aucune sous-catégorie</div>
      </div>
    </div>

    <!-- Form modal -->
    <Teleport to="body">
      <div v-if="showForm" class="fixed inset-0 z-50 flex items-center justify-center p-4 modal-overlay" @click.self="closeForm">
        <div class="bg-card rounded-2xl shadow-2xl w-full max-w-md border border-border">
          <div class="flex items-center justify-between px-6 py-4 border-b border-border">
            <h2 class="font-semibold">{{ editingCat ? 'Modifier' : 'Nouvelle catégorie' }}</h2>
            <button @click="closeForm"><X :size="16" class="text-muted-foreground" /></button>
          </div>
          <form @submit.prevent="saveCat" class="p-6 space-y-4">
            <div><label class="label-field">Nom *</label><input v-model="form.nom" class="input-field" required /></div>
            <div><label class="label-field">Description</label><input v-model="form.description" class="input-field" /></div>
            <div>
              <label class="label-field">Catégorie parente</label>
              <select v-model="form.parentId" class="select-field">
                <option :value="null">— Catégorie principale —</option>
                <option v-for="p in parents" :key="p.id" :value="p.id">{{ p.nom }}</option>
              </select>
            </div>
            <div class="flex gap-3 pt-2 border-t border-border">
              <button type="button" @click="closeForm" class="flex-1 py-2.5 rounded-xl border border-border text-sm hover:bg-muted transition-colors">Annuler</button>
              <button type="submit" class="flex-1 btn-primary py-2.5 rounded-xl text-sm font-semibold text-white">{{ editingCat ? 'Enregistrer' : 'Créer' }}</button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>
  </div>
</template>
<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import { Plus, Tag, Edit3, Eye, EyeOff, X } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { CATEGORIES } from '@/services/mockData'

const cats = ref([...CATEGORIES])
const parents = computed(() => cats.value.filter(c => !c.parentId))
const children = (pid: number) => cats.value.filter(c => c.parentId === pid)
const showForm = ref(false)
const editingCat = ref<typeof CATEGORIES[0] | null>(null)
const form = reactive({ id: 0, nom: '', description: '', parentId: null as number | null, actif: true, produits: 0, enfants: [] as string[] })

function editCat(c: typeof CATEGORIES[0]) { Object.assign(form, { ...c, parentId: c.parentId ?? null }); editingCat.value = c; showForm.value = true }
function closeForm() { showForm.value = false; editingCat.value = null; Object.assign(form, { id: 0, nom: '', description: '', parentId: null }) }
function saveCat() { const idx = cats.value.findIndex(c => c.id === form.id); if (idx >= 0) cats.value[idx] = { ...cats.value[idx], ...form }; else cats.value.push({ ...form, id: Date.now(), parentNom: '' }); closeForm() }
function toggleCat(c: typeof CATEGORIES[0]) { c.actif = !c.actif }
</script>
