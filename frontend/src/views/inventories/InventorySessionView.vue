<template>
  <div class="p-6 max-w-[1200px] mx-auto">
    <div v-if="loading" class="flex justify-center py-20">
      <div class="w-8 h-8 border-4 border-emerald-500 border-t-transparent rounded-full animate-spin" />
    </div>

    <div v-else-if="error" class="card-premium rounded-2xl p-8 text-center text-red-500">
      <p class="font-medium">{{ error }}</p>
      <button @click="init" class="mt-3 text-sm underline">Réessayer</button>
    </div>

    <template v-else>
      <PageHeader
        :title="`Session · ${inv.numero}`"
        :subtitle="`${inv.entrepotNom} · ${inv.type}`"
        back="Inventaires">
        <template #actions>
          <StatusBadge :status="inv.statut" :dot="true" />
          <button
            v-if="inv.statut === 'EN_COURS'"
            @click="cloturer"
            :disabled="actionLoading"
            class="btn-primary flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold text-white disabled:opacity-60">
            <CheckCircle2 :size="15" />
            {{ actionLoading ? '…' : 'Clôturer l\'inventaire' }}
          </button>
        </template>
      </PageHeader>

      <!-- Erreur action -->
      <div v-if="actionError" class="mb-4 p-3 rounded-xl bg-red-50 border border-red-200 text-red-600 text-sm">
        {{ actionError }}
      </div>

      <!-- Statut PLANIFIE : bouton démarrer -->
      <div v-if="inv.statut === 'PLANIFIE'" class="card-premium rounded-2xl p-8 text-center mb-6">
        <ClipboardList :size="36" class="mx-auto mb-3 text-emerald-500" />
        <p class="font-semibold text-foreground mb-1">Inventaire planifié</p>
        <p class="text-sm text-muted-foreground mb-4">Démarrez la session pour charger les lignes de stock et commencer le comptage.</p>
        <button
          @click="startInventory"
          :disabled="actionLoading"
          class="btn-primary px-6 py-2.5 rounded-xl text-sm font-semibold text-white disabled:opacity-60">
          {{ actionLoading ? 'Démarrage…' : 'Démarrer le comptage' }}
        </button>
      </div>

      <!-- Statut EN_COURS ou TERMINE : tableau de saisie -->
      <template v-if="['EN_COURS', 'TERMINE'].includes(inv.statut)">
        <!-- Summary KPIs -->
        <div class="grid grid-cols-3 gap-4 mb-6">
          <div class="kpi-card">
            <div class="text-xs text-muted-foreground uppercase tracking-wide mb-1">Lignes total</div>
            <div class="text-3xl font-bold text-foreground">{{ lines.length }}</div>
          </div>
          <div class="kpi-card">
            <div class="text-xs text-amber-500 uppercase tracking-wide mb-1">Écarts détectés</div>
            <div class="text-3xl font-bold text-amber-500">{{ ecarts }}</div>
          </div>
          <div class="kpi-card">
            <div class="text-xs text-emerald-600 uppercase tracking-wide mb-1">Sans écart</div>
            <div class="text-3xl font-bold text-emerald-600">{{ lines.length - ecarts }}</div>
          </div>
        </div>

        <div class="card-premium rounded-2xl overflow-hidden">
          <div class="px-5 py-4 border-b border-border flex items-center justify-between">
            <h3 class="font-semibold">Saisie des quantités comptées</h3>
            <button
              v-if="inv.statut === 'EN_COURS' && pendingSaves.size > 0"
              @click="saveAll"
              :disabled="actionLoading"
              class="flex items-center gap-1.5 px-3.5 py-2 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs font-semibold hover:bg-emerald-100 transition-colors disabled:opacity-60">
              <Save :size="13" /> Sauvegarder ({{ pendingSaves.size }})
            </button>
          </div>
          <table class="w-full text-sm">
            <thead>
              <tr class="border-b border-border bg-muted/30">
                <th class="text-left px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Produit</th>
                <th class="text-right px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Qté système</th>
                <th class="text-right px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Qté comptée</th>
                <th class="text-right px-4 py-3.5 text-xs font-semibold text-muted-foreground uppercase tracking-wide">Écart</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="line in lines" :key="line.id" class="table-row-hover border-b border-border/50 last:border-0">
                <td class="px-4 py-3.5">
                  <div class="font-medium text-foreground">{{ line.produitNom }}</div>
                  <div class="text-xs text-muted-foreground font-mono">{{ line.produitRef }}</div>
                </td>
                <td class="px-4 py-3.5 text-right text-muted-foreground font-medium">{{ line.quantiteTheorique }}</td>
                <td class="px-4 py-3.5 text-right">
                  <input
                    v-model.number="line.quantiteComptee"
                    type="number" min="0"
                    :disabled="inv.statut === 'TERMINE'"
                    @change="markPending(line)"
                    class="w-24 px-2 py-1.5 rounded-lg border border-border bg-card text-sm text-right focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary/60 disabled:opacity-50" />
                </td>
                <td class="px-4 py-3.5 text-right">
                  <span v-if="line.quantiteComptee !== null && line.quantiteComptee !== undefined"
                    :class="['font-bold text-sm',
                      (line.quantiteComptee - line.quantiteTheorique) > 0 ? 'text-emerald-600' :
                      (line.quantiteComptee - line.quantiteTheorique) < 0 ? 'text-red-500' :
                      'text-muted-foreground']">
                    {{ (line.quantiteComptee - line.quantiteTheorique) > 0 ? '+' : '' }}{{ line.quantiteComptee - line.quantiteTheorique }}
                  </span>
                  <span v-else class="text-muted-foreground">—</span>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-if="!lines.length" class="text-center py-16 text-muted-foreground">
            <ClipboardList :size="32" class="mx-auto mb-2 opacity-30" />
            <p>Aucune ligne de comptage</p>
          </div>
        </div>
      </template>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { CheckCircle2, ClipboardList, Save } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import { inventoryService, type Inventory, type InventoryLine } from '@/services/operations.service'

const route = useRoute()
const router = useRouter()

const inv = ref<Inventory>({ id: 0, numero: '', entrepotId: 0, entrepotNom: '', type: '', statut: 'PLANIFIE', datePlanifiee: '', note: '', creePar: '', nbLignes: 0, nbEcarts: 0, lignes: [] })
const lines = ref<InventoryLine[]>([])
const loading = ref(true)
const actionLoading = ref(false)
const error = ref('')
const actionError = ref('')
// Lignes modifiées en attente de sauvegarde
const pendingSaves = ref<Set<number>>(new Set())

const ecarts = computed(() =>
  lines.value.filter(l => l.quantiteComptee !== null && l.quantiteComptee !== undefined && l.quantiteComptee !== l.quantiteTheorique).length
)

async function init() {
  loading.value = true
  error.value = ''
  try {
    const data = await inventoryService.findById(Number(route.params.id))
    inv.value = data
    // Charger les lignes depuis les données backend
    lines.value = data.lignes || []
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Erreur lors du chargement de l\'inventaire'
  } finally {
    loading.value = false
  }
}

async function startInventory() {
  actionLoading.value = true
  actionError.value = ''
  try {
    const started = await inventoryService.start(inv.value.id)
    inv.value = started
    lines.value = started.lignes || []
  } catch (e: any) {
    actionError.value = e?.response?.data?.message || 'Erreur lors du démarrage'
  } finally {
    actionLoading.value = false
  }
}

function markPending(line: InventoryLine) {
  pendingSaves.value.add(line.id)
}

async function saveAll() {
  actionLoading.value = true
  actionError.value = ''
  try {
    for (const line of lines.value) {
      if (pendingSaves.value.has(line.id) && line.quantiteComptee !== null && line.quantiteComptee !== undefined) {
        await inventoryService.count(inv.value.id, {
          produitId: line.produitId,
          quantiteComptee: line.quantiteComptee,
          note: line.note,
        })
      }
    }
    pendingSaves.value.clear()
    // Recharger pour avoir les écarts calculés côté serveur
    const refreshed = await inventoryService.findById(inv.value.id)
    lines.value = refreshed.lignes || []
  } catch (e: any) {
    actionError.value = e?.response?.data?.message || 'Erreur lors de la sauvegarde'
  } finally {
    actionLoading.value = false
  }
}

async function cloturer() {
  if (!confirm('Clôturer l\'inventaire ? Cette action ajustera le stock pour tous les écarts.')) return
  // Sauvegarder d'abord les modifications en attente
  if (pendingSaves.value.size > 0) await saveAll()
  if (actionError.value) return

  actionLoading.value = true
  actionError.value = ''
  try {
    const result = await inventoryService.validate(inv.value.id)
    inv.value = result
    lines.value = result.lignes || []
  } catch (e: any) {
    actionError.value = e?.response?.data?.message || 'Erreur lors de la clôture'
  } finally {
    actionLoading.value = false
  }
}

onMounted(init)
</script>
