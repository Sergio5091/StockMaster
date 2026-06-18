// Fichier temporaire pour éviter les erreurs d'import
// Toutes les données sont vides - utilisez les services API à la place

export const CATEGORIES: any[] = []
export const PRODUCTS: any[] = []
export const STOCKS: any[] = []
export const STOCK_MOVEMENTS: any[] = []
export const RECEIPTS: any[] = []
export const ISSUES: any[] = []
export const TRANSFERS: any[] = []
export const PURCHASE_ORDERS: any[] = []
export const INVENTORIES: any[] = []
export const ALERTS: any[] = []
export const AUDIT_LOGS: any[] = []
export const ZONES: any[] = []
export const LOCATIONS: any[] = []
export const SUPPLIERS: any[] = []

// Fonctions utilitaires déplacées vers @/utils/formatters
export function formatDate(dateStr: string) {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

export function formatDatetime(dateStr: string) {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

export function formatCurrency(amount: number) {
  return new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(amount)
}