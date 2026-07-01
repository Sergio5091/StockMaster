<template>
  <div class="p-6 max-w-[900px] mx-auto">

    <!-- Loading -->
    <div v-if="loading" class="flex justify-center py-20">
      <div class="w-8 h-8 border-4 border-emerald-500 border-t-transparent rounded-full animate-spin" />
    </div>

    <!-- Erreur -->
    <div v-else-if="error" class="card-premium rounded-2xl p-8 text-center">
      <div class="w-12 h-12 rounded-xl bg-red-100 flex items-center justify-center mx-auto mb-3">
        <AlertTriangle :size="20" class="text-red-500" />
      </div>
      <p class="font-medium text-red-500 mb-1">Impossible de charger l'emplacement</p>
      <p class="text-sm text-muted-foreground mb-4">{{ error }}</p>
      <button @click="load" class="px-4 py-2 rounded-xl bg-muted border border-border text-sm hover:bg-muted/80 transition-colors">
        Réessayer
      </button>
    </div>

    <!-- Contenu -->
    <template v-else-if="loc">
      <PageHeader :title="loc.code" subtitle="Détail de l'emplacement" back="Emplacements">
        <template #actions>
          <StatusBadge :status="String(loc.statut).toLowerCase()" :dot="true" />
        </template>
      </PageHeader>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">

        <!-- Informations physiques -->
        <div class="card-premium rounded-2xl p-5 space-y-4">
          <h3 class="font-semibold text-foreground flex items-center gap-2">
            <MapPin :size="15" class="text-emerald-600" /> Informations physiques
          </h3>
          <div class="space-y-2.5 text-sm">
            <div class="flex justify-between">
              <span class="text-muted-foreground">Zone</span>
              <span class="font-medium">{{ loc.zoneNom || '—' }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-muted-foreground">Rayon</span>
              <span class="font-medium font-mono">{{ loc.rayon || '—' }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-muted-foreground">Étagère</span>
              <span class="font-medium font-mono">{{ loc.etagere || '—' }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-muted-foreground">Position</span>
              <span class="font-medium font-mono">{{ loc.position || '—' }}</span>
            </div>
            <div class="pt-2 border-t border-border">
              <div class="flex justify-between mb-1.5">
                <span class="text-muted-foreground">Capacité poids</span>
                <span class="font-medium">{{ loc.capaciteKg }} kg</span>
              </div>
              <!-- Barre de charge -->
              <div class="w-full h-2.5 bg-muted rounded-full overflow-hidden">
                <div
                  class="h-full rounded-full transition-all duration-700"
                  :class="chargePercent > 85 ? 'bg-red-500' : chargePercent > 65 ? 'bg-amber-400' : 'bg-emerald-500'"
                  :style="{ width: chargePercent + '%' }"
                />
              </div>
              <div class="flex justify-between mt-1 text-xs text-muted-foreground">
                <span>{{ loc.poidsActuel }} kg utilisés</span>
                <span :class="chargePercent > 85 ? 'text-red-500 font-semibold' : 'text-muted-foreground'">
                  {{ chargePercent }}%
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- Contenu + QR Code -->
        <div class="space-y-4">

          <!-- Produit stocké -->
          <div class="card-premium rounded-2xl p-5">
            <h3 class="font-semibold text-foreground mb-4 flex items-center gap-2">
              <Package :size="15" class="text-emerald-600" /> Contenu
            </h3>
            <div v-if="loc.produitNom" class="p-4 rounded-xl bg-emerald-50 border border-emerald-200">
              <div class="flex items-center gap-3 mb-3">
                <div class="w-10 h-10 rounded-xl bg-emerald-100 flex items-center justify-center shrink-0">
                  <Package :size="18" class="text-emerald-600" />
                </div>
                <div>
                  <div class="font-semibold text-foreground">{{ loc.produitNom }}</div>
                  <div class="text-xs text-muted-foreground font-mono">Produit #{{ loc.produitId }}</div>
                </div>
              </div>
              <div class="flex items-center justify-between p-3 rounded-lg bg-white border border-emerald-100">
                <span class="text-sm text-muted-foreground">Poids total</span>
                <span class="font-bold text-emerald-700">{{ loc.poidsActuel }} kg</span>
              </div>
            </div>
            <div v-else class="text-center py-8 rounded-xl bg-muted/30 border border-dashed border-border">
              <Package :size="32" class="mx-auto mb-2 text-muted-foreground opacity-40" />
              <p class="text-sm text-muted-foreground font-medium">Emplacement vide</p>
              <p class="text-xs text-muted-foreground mt-1">Aucun produit rangé ici</p>
            </div>
          </div>

          <!-- QR Code -->
          <div class="card-premium rounded-2xl p-5">
            <h3 class="font-semibold text-foreground mb-4 flex items-center gap-2">
              <QrCode :size="15" class="text-emerald-600" /> QR Code emplacement
            </h3>

            <!-- QR Code visuel généré en SVG simple -->
            <div class="flex flex-col items-center gap-4">
              <div class="p-4 bg-white border-2 border-border rounded-xl shadow-sm">
                <!-- Représentation QR Code simulée -->
                <div class="w-32 h-32 relative">
                  <svg viewBox="0 0 100 100" class="w-full h-full" xmlns="http://www.w3.org/2000/svg">
                    <!-- Coin haut-gauche -->
                    <rect x="5" y="5" width="25" height="25" rx="2" fill="none" stroke="#111" stroke-width="3"/>
                    <rect x="11" y="11" width="13" height="13" rx="1" fill="#111"/>
                    <!-- Coin haut-droite -->
                    <rect x="70" y="5" width="25" height="25" rx="2" fill="none" stroke="#111" stroke-width="3"/>
                    <rect x="76" y="11" width="13" height="13" rx="1" fill="#111"/>
                    <!-- Coin bas-gauche -->
                    <rect x="5" y="70" width="25" height="25" rx="2" fill="none" stroke="#111" stroke-width="3"/>
                    <rect x="11" y="76" width="13" height="13" rx="1" fill="#111"/>
                    <!-- Données simulées (pattern unique basé sur le code) -->
                    <g fill="#111" opacity="0.85">
                      <rect :x="getX(0)" :y="getY(0)" width="6" height="6"/>
                      <rect :x="getX(1)" :y="getY(1)" width="6" height="6"/>
                      <rect :x="getX(2)" :y="getY(2)" width="6" height="6"/>
                      <rect :x="getX(3)" :y="getY(3)" width="6" height="6"/>
                      <rect :x="getX(4)" :y="getY(4)" width="6" height="6"/>
                      <rect :x="getX(5)" :y="getY(5)" width="6" height="6"/>
                      <rect :x="getX(6)" :y="getY(6)" width="6" height="6"/>
                      <rect :x="getX(7)" :y="getY(7)" width="6" height="6"/>
                    </g>
                  </svg>
                </div>
              </div>

              <!-- Code texte lisible -->
              <div class="w-full p-3 rounded-xl bg-muted/50 border border-border text-center">
                <div class="text-xs text-muted-foreground mb-1 font-medium uppercase tracking-wide">Code</div>
                <div class="font-mono text-sm font-bold text-foreground break-all">{{ loc.code }}</div>
              </div>

              <!-- Bouton imprimer -->
              <button
                @click="printQR"
                class="w-full flex items-center justify-center gap-2 py-2.5 rounded-xl bg-muted border border-border text-sm font-medium hover:bg-muted/80 transition-colors text-muted-foreground"
              >
                <Printer :size="14" /> Imprimer l'étiquette
              </button>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { MapPin, Package, QrCode, Printer, AlertTriangle } from 'lucide-vue-next'
import PageHeader from '@/components/common/PageHeader.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'
import WarehouseService, { type Location } from '@/services/warehouse.service'

const route = useRoute()
const loc = ref<Location | null>(null)
const loading = ref(true)
const error = ref('')

// Charge depuis l'API
async function load() {
  loading.value = true
  error.value = ''
  try {
    loc.value = await WarehouseService.getLocationById(Number(route.params.id))
  } catch (e: any) {
    error.value = e?.response?.data?.message || 'Emplacement introuvable'
  } finally {
    loading.value = false
  }
}

onMounted(load)

// Taux de charge (poids actuel / capacité)
const chargePercent = computed(() => {
  if (!loc.value || !loc.value.capaciteKg) return 0
  return Math.min(100, Math.round((loc.value.poidsActuel / loc.value.capaciteKg) * 100))
})

// Génère des coordonnées pseudo-aléatoires basées sur le code de l'emplacement
// pour que le QR soit différent d'un emplacement à l'autre
function hash(str: string, index: number): number {
  let h = 0
  for (let i = 0; i < str.length; i++) {
    h = (h * 31 + str.charCodeAt(i) + index * 7) & 0xffff
  }
  return h
}
function getX(i: number): number {
  return 38 + (hash(loc.value?.code || 'x', i * 3) % 55)
}
function getY(i: number): number {
  return 38 + (hash(loc.value?.code || 'x', i * 5 + 13) % 55)
}

// Impression de l'étiquette
function printQR() {
  if (!loc.value) return
  const win = window.open('', '_blank', 'width=400,height=500')
  if (!win) return
  win.document.write(`
    <html><head><title>Étiquette ${loc.value.code}</title>
    <style>
      body { font-family: monospace; text-align: center; padding: 20px; }
      h2 { font-size: 14px; margin-bottom: 8px; }
      .code { font-size: 12px; word-break: break-all; border: 1px solid #ccc; padding: 8px; border-radius: 4px; margin-top: 12px; }
      .zone { font-size: 11px; color: #666; margin-top: 4px; }
    </style></head><body>
    <h2>StockMaster</h2>
    <div style="font-size:48px">▣</div>
    <div class="code">${loc.value.code}</div>
    <div class="zone">Zone : ${loc.value.zoneNom || '—'}</div>
    <div class="zone">Rayon ${loc.value.rayon || '—'} · Étagère ${loc.value.etagere || '—'} · Position ${loc.value.position || '—'}</div>
    <script>window.print();window.close();<\/script>
    </body></html>
  `)
}
</script>
