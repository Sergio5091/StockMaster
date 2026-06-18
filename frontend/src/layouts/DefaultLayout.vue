<template>
  <div class="flex h-screen overflow-hidden bg-background">
    <aside :class="['sidebar-glass flex flex-col transition-all duration-300 z-30 shrink-0', sidebarOpen ? 'w-64' : 'w-16', 'fixed lg:relative h-full']">
      <div class="flex items-center gap-3 px-4 py-5 border-b" style="border-color: rgba(255,255,255,0.06);">
        <div class="w-8 h-8 rounded-lg shrink-0 flex items-center justify-center" style="background: linear-gradient(135deg, #059669, #10b981);">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z" stroke="white" stroke-width="2.5"/><polyline points="9,22 9,12 15,12 15,22" stroke="white" stroke-width="2.5"/></svg>
        </div>
        <transition name="fade-slide">
          <span v-if="sidebarOpen" class="text-white font-bold text-base tracking-tight whitespace-nowrap">StockMaster</span>
        </transition>
        <button v-if="sidebarOpen" @click="sidebarOpen = false" class="ml-auto text-green-400/60 hover:text-green-300 transition-colors">
          <ChevronLeft :size="16" />
        </button>
      </div>
      <button v-if="!sidebarOpen" @click="sidebarOpen = true" class="flex items-center justify-center py-4 text-green-400/60 hover:text-green-300 transition-colors">
        <ChevronRight :size="16" />
      </button>
      <nav class="flex-1 overflow-y-auto py-4 px-2 space-y-0.5">
        <NavItem :open="sidebarOpen" :to="{ name: 'dashboard' }" icon="LayoutDashboard" label="Tableau de bord" />
        <NavSection v-if="sidebarOpen" label="Structure" />
        <NavItem :open="sidebarOpen" :to="{ name: 'warehouses' }" icon="Warehouse" label="Entrepôts" />
        <NavSection v-if="sidebarOpen" label="Catalogue" />
        <NavItem :open="sidebarOpen" :to="{ name: 'categories' }" icon="Tag" label="Catégories" />
        <NavItem :open="sidebarOpen" :to="{ name: 'products' }" icon="Package" label="Produits" />
        <NavItem :open="sidebarOpen" :to="{ name: 'suppliers' }" icon="Truck" label="Fournisseurs" />
        <NavSection v-if="sidebarOpen" label="Stocks" />
        <NavItem :open="sidebarOpen" :to="{ name: 'stocks' }" icon="BarChart3" label="Niveaux de stock" />
        <NavItem :open="sidebarOpen" :to="{ name: 'stock-movements' }" icon="ArrowLeftRight" label="Mouvements" />
        <NavSection v-if="sidebarOpen" label="Opérations" />
        <NavItem :open="sidebarOpen" :to="{ name: 'receipts' }" icon="PackagePlus" label="Bons de réception" />
        <NavItem :open="sidebarOpen" :to="{ name: 'issues' }" icon="PackageMinus" label="Bons de sortie" />
        <NavItem :open="sidebarOpen" :to="{ name: 'transfers' }" icon="MoveRight" label="Transferts" />
        <NavItem :open="sidebarOpen" :to="{ name: 'purchase-orders' }" icon="ShoppingCart" label="Commandes" />
        <NavItem :open="sidebarOpen" :to="{ name: 'inventories' }" icon="ClipboardList" label="Inventaires" />
        <NavSection v-if="sidebarOpen" label="Supervision" />
        <NavItem :open="sidebarOpen" :to="{ name: 'alerts' }" icon="Bell" label="Alertes" :badge="unreadAlerts" />
        <NavItem :open="sidebarOpen" :to="{ name: 'reports' }" icon="FileText" label="Rapports" />
        <NavItem :open="sidebarOpen" :to="{ name: 'audit' }" icon="History" label="Audit" />
        <NavSection v-if="sidebarOpen" label="Administration" />
        <NavItem :open="sidebarOpen" :to="{ name: 'users' }" icon="Users" label="Utilisateurs" />
      </nav>
      <div class="border-t p-3" style="border-color: rgba(255,255,255,0.06);">
        <RouterLink to="/profile" class="flex items-center gap-3 p-2 rounded-xl hover:bg-white/5 transition-colors cursor-pointer">
          <div class="w-8 h-8 rounded-lg shrink-0 flex items-center justify-center text-sm font-semibold text-white" style="background: linear-gradient(135deg, #059669 0%, #10b981 100%);">
            {{ auth.user?.prenom?.[0] }}{{ auth.user?.nom?.[0] }}
          </div>
          <transition name="fade-slide">
            <div v-if="sidebarOpen" class="overflow-hidden">
              <div class="text-sm font-medium text-white/90 truncate">{{ auth.fullName }}</div>
              <div class="text-xs truncate" style="color: rgba(110,231,183,0.6);">{{ auth.roleLabel }}</div>
            </div>
          </transition>
        </RouterLink>
      </div>
    </aside>

    <div v-if="sidebarOpen" @click="sidebarOpen = false" class="fixed inset-0 z-20 bg-black/50 lg:hidden" />

    <div class="flex-1 flex flex-col min-w-0 overflow-hidden">
      <header class="shrink-0 flex items-center gap-3 px-6 py-3.5 bg-card border-b border-border">
        <button @click="sidebarOpen = !sidebarOpen" class="lg:hidden p-2 rounded-lg hover:bg-muted text-muted-foreground transition-colors">
          <Menu :size="20" />
        </button>
        <div class="flex-1 min-w-0">
          <h2 class="text-sm font-semibold text-foreground truncate">{{ pageTitle }}</h2>
        </div>
        <div class="flex items-center gap-2">
          <RouterLink to="/alerts" class="relative p-2 rounded-lg hover:bg-muted text-muted-foreground hover:text-foreground transition-colors">
            <Bell :size="18" />
            <span v-if="unreadAlerts > 0" class="absolute top-1 right-1 w-4 h-4 rounded-full text-[10px] font-bold text-white flex items-center justify-center" style="background: #ef4444;">
              {{ unreadAlerts > 9 ? '9+' : unreadAlerts }}
            </span>
          </RouterLink>
          <div class="relative" ref="userMenuRef">
            <button @click="userMenuOpen = !userMenuOpen" class="flex items-center gap-2 p-1.5 pr-3 rounded-xl hover:bg-muted transition-colors">
              <div class="w-7 h-7 rounded-lg flex items-center justify-center text-xs font-bold text-white shrink-0" style="background: linear-gradient(135deg, #059669, #10b981);">
                {{ auth.user?.prenom?.[0] }}{{ auth.user?.nom?.[0] }}
              </div>
              <span class="hidden sm:block text-sm font-medium text-foreground/80">{{ auth.user?.prenom }}</span>
              <ChevronDown :size="14" class="text-muted-foreground" />
            </button>
            <Transition name="dropdown">
              <div v-if="userMenuOpen" class="absolute right-0 top-full mt-1.5 w-52 rounded-2xl bg-card border border-border shadow-lg z-50 overflow-hidden py-1.5">
                <div class="px-3 py-2 border-b border-border mb-1">
                  <div class="text-sm font-semibold text-foreground">{{ auth.fullName }}</div>
                  <div class="text-xs text-muted-foreground">{{ auth.user?.email }}</div>
                </div>
                <RouterLink to="/profile" @click="userMenuOpen = false" class="flex items-center gap-2.5 px-3 py-2 text-sm text-foreground/80 hover:bg-muted hover:text-foreground transition-colors">
                  <User :size="14" /> Mon profil
                </RouterLink>
                <button @click="handleLogout" class="w-full flex items-center gap-2.5 px-3 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors">
                  <LogOut :size="14" /> Déconnexion
                </button>
              </div>
            </Transition>
          </div>
        </div>
      </header>
      <main class="flex-1 overflow-y-auto">
        <RouterView v-slot="{ Component }">
          <Transition name="page" mode="out-in">
            <component :is="Component" />
          </Transition>
        </RouterView>
      </main>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { RouterView, RouterLink, useRoute, useRouter } from 'vue-router'
import { LayoutDashboard, Warehouse, Tag, Package, Truck, BarChart3, ArrowLeftRight, PackagePlus, PackageMinus, MoveRight, ShoppingCart, ClipboardList, Bell, FileText, History, Users, Menu, ChevronLeft, ChevronRight, ChevronDown, User, LogOut } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import { useNotificationsStore } from '@/stores/notifications'
import NavItem from '@/components/common/NavItem.vue'
import NavSection from '@/components/common/NavSection.vue'

const auth = useAuthStore()
const notifications = useNotificationsStore()
const route = useRoute()
const router = useRouter()
const sidebarOpen = ref(true)
const userMenuOpen = ref(false)
const userMenuRef = ref<HTMLElement | null>(null)
const unreadAlerts = computed(() => notifications.unreadCount)
const PAGE_TITLES: Record<string, string> = {
  dashboard: 'Tableau de bord', warehouses: 'Entrepôts', 'warehouse-detail': 'Détail entrepôt',
  zones: 'Zones', locations: 'Emplacements', categories: 'Catégories', products: 'Produits',
  'product-detail': 'Fiche produit', suppliers: 'Fournisseurs', 'supplier-detail': 'Fiche fournisseur',
  stocks: 'Niveaux de stock', 'stock-movements': 'Historique mouvements', receipts: 'Bons de réception',
  'receipt-new': 'Nouveau bon de réception', 'receipt-detail': 'Bon de réception', issues: 'Bons de sortie',
  'issue-new': 'Nouveau bon de sortie', 'issue-detail': 'Bon de sortie', transfers: 'Transferts',
  'transfer-new': 'Nouveau transfert', 'transfer-detail': 'Transfert', 'purchase-orders': 'Commandes fournisseurs',
  'purchase-order-new': 'Nouvelle commande', 'purchase-order-detail': 'Commande fournisseur',
  inventories: 'Inventaires', 'inventory-new': "Planifier un inventaire", 'inventory-session': "Session d'inventaire",
  alerts: 'Alertes', 'alerts-config': 'Configuration alertes', reports: 'Rapports & Exports',
  audit: "Journal d'audit", users: 'Utilisateurs', profile: 'Mon profil',
}
const pageTitle = computed(() => PAGE_TITLES[route.name as string] || 'StockMaster')
async function handleLogout() { auth.logout(); await router.push('/login') }
function handleClickOutside(e: MouseEvent) {
  if (userMenuRef.value && !userMenuRef.value.contains(e.target as Node)) userMenuOpen.value = false
}
onMounted(() => document.addEventListener('click', handleClickOutside))
onUnmounted(() => document.removeEventListener('click', handleClickOutside))
</script>
<style scoped>
.fade-slide-enter-active, .fade-slide-leave-active { transition: opacity 0.15s, transform 0.15s; }
.fade-slide-enter-from { opacity: 0; transform: translateX(-6px); }
.fade-slide-leave-to { opacity: 0; transform: translateX(-6px); }
.dropdown-enter-active, .dropdown-leave-active { transition: opacity 0.12s, transform 0.12s; }
.dropdown-enter-from, .dropdown-leave-to { opacity: 0; transform: translateY(-4px) scale(0.97); }
</style>
