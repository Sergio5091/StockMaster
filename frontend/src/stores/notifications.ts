import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface Notification {
  id: string
  type: 'stock_critical' | 'stock_excess' | 'zone_full' | 'order_late' | 'info' | 'success' | 'warning' | 'error'
  title: string
  message: string
  read: boolean
  createdAt: Date
  entityId?: number
  entityType?: string
}

export const useNotificationsStore = defineStore('notifications', () => {
  const notifications = ref<Notification[]>([
    { id: '1', type: 'stock_critical', title: 'Stock critique', message: 'Clavier mécanique RGB (PRD-000123) — stock en dessous du minimum (2 restants)', read: false, createdAt: new Date(Date.now() - 5 * 60000) },
    { id: '2', type: 'order_late', title: 'Commande en retard', message: 'CF-2024-0034 — TechSupply SA — livraison prévue le 12/06, toujours ENVOYÉE', read: false, createdAt: new Date(Date.now() - 30 * 60000) },
    { id: '3', type: 'zone_full', title: 'Zone saturée', message: 'ZONE-B (Stockage) — ENT-001 Paris-Nord — occupation à 93%', read: false, createdAt: new Date(Date.now() - 2 * 3600000) },
    { id: '4', type: 'stock_critical', title: 'Stock critique', message: 'Moniteur 27" 4K (PRD-000456) — 0 unité disponible', read: true, createdAt: new Date(Date.now() - 4 * 3600000) },
    { id: '5', type: 'info', title: 'Inventaire planifié', message: 'INV-2024-007 — Entrepôt Lyon-Sud — planifié pour demain 08h00', read: true, createdAt: new Date(Date.now() - 6 * 3600000) },
  ])

  const unreadCount = computed(() => notifications.value.filter(n => !n.read).length)
  const unreadList = computed(() => notifications.value.filter(n => !n.read))

  function markRead(id: string) {
    const n = notifications.value.find(n => n.id === id)
    if (n) n.read = true
  }
  function markAllRead() { notifications.value.forEach(n => n.read = true) }
  function addNotification(n: Omit<Notification, 'id' | 'createdAt'>) {
    notifications.value.unshift({ ...n, id: Date.now().toString(), createdAt: new Date() })
  }
  function dismiss(id: string) { notifications.value = notifications.value.filter(n => n.id !== id) }

  return { notifications, unreadCount, unreadList, markRead, markAllRead, addNotification, dismiss }
})
