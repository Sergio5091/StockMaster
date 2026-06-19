<template>
  <div class="p-6 max-w-[1400px] mx-auto">
    <PageHeader title="Fournisseurs" :subtitle="`${total} fournisseur(s)`">
      <template #actions>
        <button v-if="can('manage_suppliers')" @click="openCreate" class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white">
          <Plus :size="15" /> Nouveau fournisseur
        </button>
      </template>
    </PageHeader>

    <div class="card-premium rounded-2xl p-4 mb-6 flex flex-wrap gap-3 items-center">
      <div class="relative flex-1 min-w-[200px]">
        <Search :size="15" class="absolute left-3.5 top-1/2 -translate-y-1/2 text-muted-foreground" />
        <input v-model="search" type="search" placeholder="Nom, code…" class="input-field pl-10" />
      </div>
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

    <template v-else>
      <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-5">
        <div v-for="s in filtered" :key="s.id"
          class="card-premium rounded-2xl overflow-hidden hover:shadow-md transition-all duration-200"
          :class="{ 'opacity-60': !s.actif }">
          <div class="px-5 py-4 border-b border-border flex items-start justify-between">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-xl flex items-center justify-center text-white font-bold text-sm shrink-0"
                style="background: linear-gradient(135deg, #3b82f6, #06b6d4);">
                {{ s.nom.slice(0, 2).toUpperCase() }}
              </div>
              <div>
                <div class="font-bold text-foreground">{{ s.nom }}</div>
                <div class="text-xs font-mono text-muted-foreground">{{ s.code }}</div>
              </div>
            </div>
            <StatusBadge :status="s.actif ? 'actif' : 'inactif'" :dot="true" />
          </div>

          <div class="px-5 py-4 space-y-3">
            <div class="flex items-center gap-2 text-sm text-muted-foreground">
              <MapPin :size="13" /><span>{{ s.ville }}, {{ s.pays }}</span>
            </div>
            <div v-if="s.contactPrincipalNom || s.contactPrincipalPrenom" class="flex items-center gap-2 text-sm text-muted-foreground">
              <User :size="13" /><span>{{ s.contactPrincipalPrenom }} {{ s.contactPrincipalNom }}</span>
            </div>
            <div v-if="s.email" class="flex items-center gap-2 text-sm text-muted-foreground">
              <Mail :size="13" /><span class="truncate">{{ s.email }}</span>
            </div>

            <div class="grid grid-cols-1 gap-2 pt-1">
              <div class="p-2 rounded-xl bg-muted/50 text-center">
                <div class="font-bold text-sm text-foreground">{{ s.delaiLivraisonJours ?? '—' }}j</div>
                <div class="text-[10px] text-muted-foreground uppercase">Délai livraison moyen</div>
              </div>
            </div>

            <div class="flex gap-2 pt-1">
              <RouterLink :to="`/suppliers/${s.id}`"
                class="flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs font-semibold hover:bg-emerald-100 transition-colors">
                <Eye :size="12" /> Détail
              </RouterLink>
              <button v-if="can('manage_suppliers')" @click="openEdit(s)"
                class="p-2 rounded-xl bg-muted border border-border hover:bg-muted/80 transition-colors">
                <Edit3 :size="13" class="text-muted-foreground" />
              </button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="!filtered.length" class="text-center py-16 text-muted-foreground">
        <Truck :size="36" class="mx-auto mb-3 opacity-30" />
        <p class="font-medium">Aucun fournisseur trouvé</p>
      </div>
    </template>

    <!-- Modal -->
    <Teleport to="body">
      <div v-if="showForm" class="fixed inset-0 z-50 flex items-center justify-center p-4 modal-overlay" @click.self="closeForm">
        <div class="bg-card rounded-2xl shadow-2xl w-full max-w-xl border border-border">
          <div class="flex items-center justify-between px-6 py-4 border-b border-border">
            <h2 class="font-semibold">{{ editingSupplier ? 'Modifier fournisseur' : 'Nouveau fournisseur' }}</h2>
            <button @click="closeForm"><X :size="16" class="text-muted-foreground" /></button>
          </div>
          <form @submit.prevent="save" class="p-6 space-y-4">
            <div>
              <label class="label-field">Nom de la société *</label>
              <input v-model="form.nom" class="input-field" required />
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div><label class="label-field">Ville *</label><input v-model="form.ville" class="input-field" required /></div>
              <div><label class="label-field">Pays</label><input v-model="form.pays" class="input-field" placeholder="France" /></div>
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div><label class="label-field">Prénom contact</label><input v-model="form.contactPrincipalPrenom" class="input-field" /></div>
              <div><label class="label-field">Nom contact</label><input v-model="form.contactPrincipalNom" class="input-field" /></div>
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div><label class="label-field">Email</label><input v-model="form.email" type="email" class="input-field" /></div>
              <div><label class="label-field">Téléphone</label><input v-model="form.telephone" class="input-field" /></div>
            </div>
            <div>
              <label class="label-field">Délai livraison moyen (jours)</label>
              <input v-model.number="form.delaiLivraisonJours" type="number" class="input-field" min="1" />
            </div>
            <div v-if="formError" class="text-red-500 text-sm">{{ formError }}</div>
            <div class="flex gap-3 pt-2 border-t border-border">
              <button type="button" @click="closeForm" class="flex-1 py-2.5 rounded-xl border border-border text-sm hover:bg-muted transition-colors">
                Annuler
              </button>
              <button type="submit" :disabled="saving" class="flex-1 btn-primary py-2.5 rounded-xl text-sm font-semibold text-white disabled:opacity-60">
                {{ saving ? '…' : (editingSupplier ? 'Enregistrer' : 'Créer') }}
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
import { RouterLink } from 'vue-router'
import { Plus, Search, MapPin, User, Mail, Eye, Edit3, X, Truck } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { usePermissions } from '@/composables/usePermissions'
import { supplierService, type Supplier, type SupplierCreateDTO } from '@/services/supplier.service'

const { can } = usePermissions()

const suppliers = ref<Supplier[]>([])
const loading = ref(false)
const error = ref('')
const search = ref('')
const filterStatut = ref('')
const total = ref(0)
const showForm = ref(false)
const saving = ref(false)
const formError = ref('')
const editingSupplier = ref<Supplier | null>(null)

const form = reactive({
  nom: '', ville: '', pays: 'France',
  email: '', telephone: '',
  contactPrincipalNom: '', contactPrincipalPrenom: '',
  contactPrincipalEmail: '', contactPrincipalTelephone: '',
  delaiLivraisonJours: 7,
})

const filtered = computed(() => suppliers.value.filter(s => {
  if (search.value && !`${s.nom} ${s.code}`.toLowerCase().includes(search.value.toLowerCase())) return false
  if (filterStatut.value === 'actif' && !s.actif) return false
  if (filterStatut.value === 'inactif' && s.actif) return false
  return true
}))

async function load() {
  loading.value = true; error.value = ''
  try {
    const page = await supplierService.findAll(0, 200)
    suppliers.value = page.content
    total.value = page.totalElements
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors du chargement des fournisseurs'
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingSupplier.value = null
  Object.assign(form, { nom: '', ville: '', pays: 'France', email: '', telephone: '', contactPrincipalNom: '', contactPrincipalPrenom: '', contactPrincipalEmail: '', contactPrincipalTelephone: '', delaiLivraisonJours: 7 })
  formError.value = ''
  showForm.value = true
}

function openEdit(s: Supplier) {
  editingSupplier.value = s
  Object.assign(form, {
    nom: s.nom, ville: s.ville, pays: s.pays ?? 'France',
    email: s.email ?? '', telephone: s.telephone ?? '',
    contactPrincipalNom: s.contactPrincipalNom ?? '',
    contactPrincipalPrenom: s.contactPrincipalPrenom ?? '',
    contactPrincipalEmail: s.contactPrincipalEmail ?? '',
    contactPrincipalTelephone: s.contactPrincipalTelephone ?? '',
    delaiLivraisonJours: s.delaiLivraisonJours ?? 7,
  })
  formError.value = ''
  showForm.value = true
}

function closeForm() { showForm.value = false; editingSupplier.value = null }

async function save() {
  saving.value = true; formError.value = ''
  try {
    if (editingSupplier.value) {
      await supplierService.update(editingSupplier.value.id, form)
    } else {
      await supplierService.create(form as SupplierCreateDTO)
    }
    await load()
    closeForm()
  } catch (e: any) {
    formError.value = e?.response?.data?.message || 'Une erreur est survenue'
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>
