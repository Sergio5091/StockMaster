import api from './api'

// ── Types ──────────────────────────────────────────────────────────────────

export interface StockItem {
  id: number
  produitId: number
  produitRef: string
  produitNom: string
  entrepotId: number
  entrepotCode: string
  entrepotNom: string
  quantiteDisponible: number
  quantiteReservee: number
  quantiteEnTransit: number
  stockMinimum?: number
  stockMaximum?: number
  statut: string // critical | low | normal | excess
}

export interface StockMovement {
  id: number
  type: string
  produitId: number
  produitRef: string
  produitNom: string
  entrepotSourceId?: number
  entrepotSourceNom?: string
  entrepotDestinationId?: number
  entrepotDestinationNom?: string
  quantite: number
  quantiteAvant: number
  quantiteApres: number
  referenceDocument?: string
  note?: string
  createdAt: string
}

export type MovementType =
  | 'ENTREE'
  | 'SORTIE'
  | 'TRANSFERT_SORTANT'
  | 'TRANSFERT_ENTRANT'
  | 'AJUSTEMENT_INVENTAIRE'

export interface StockAdjustRequest {
  produitId: number
  entrepotId: number
  /** Positif = entrée, négatif = sortie */
  quantite: number
  type?: MovementType
  justification: string
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

// ── Service ────────────────────────────────────────────────────────────────

export const stockService = {
  /** Liste paginée de tous les stocks */
  findAll: (page = 0, size = 500) =>
    api.get<Page<StockItem>>('/stocks', { params: { page, size } }).then(r => r.data),

  /** Stocks d'un entrepôt */
  findByWarehouse: (warehouseId: number) =>
    api.get<StockItem[]>(`/stocks/warehouse/${warehouseId}`).then(r => r.data),

  /** Stock d'un produit dans un entrepôt */
  findByProduitAndEntrepot: (produitId: number, entrepotId: number) =>
    api.get<StockItem>(`/stocks/${produitId}/${entrepotId}`).then(r => r.data),
}

export const stockMovementService = {
  /** Historique avec filtres optionnels */
  findAll: (params: {
    page?: number
    size?: number
    entrepotId?: number
    type?: MovementType
    dateDebut?: string
    dateFin?: string
  } = {}) =>
    api
      .get<Page<StockMovement>>('/stock-movements', { params: { page: 0, size: 50, ...params } })
      .then(r => r.data),

  /** Mouvements d'un produit */
  findByProduit: (produitId: number, page = 0, size = 50) =>
    api
      .get<Page<StockMovement>>(`/stock-movements/product/${produitId}`, { params: { page, size } })
      .then(r => r.data),

  /** Ajustement manuel (entrée ou sortie) */
  adjust: (req: StockAdjustRequest) =>
    api.post<StockMovement>('/stock-movements/adjust', req).then(r => r.data),
}
