import api from './api'

export interface StockMovementChart {
  month: string
  incoming: number
  outgoing: number
  netChange: number
}

export interface ProductRotationChart {
  month: string
  productsMoved: number
  entries: number
  exits: number
}

export interface DashboardKPIResponse {
  totalWarehouses: number
  activeWarehouses: number
  totalProducts: number
  productsInStock: number
  criticalStockProducts: number
  totalStockValue: number
  totalMovementsThisMonth: number
  incomingMovementsThisMonth: number
  outgoingMovementsThisMonth: number
  pendingInventories: number
  pendingTransfers: number
  pendingReceipts: number
  activeAlerts: number
  stockMovementChart: StockMovementChart[]
  productRotationChart: ProductRotationChart[]
}

export const dashboardService = {
  getKPIs: () => api.get<DashboardKPIResponse>('/v1/dashboard/kpis').then(r => r.data),
}

export default dashboardService
