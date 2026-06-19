import api from './api'

// ── Types ──────────────────────────────────────────────────────────────────

export interface Receipt {
  id: number; numero: string; fournisseurId: number; fournisseurNom: string
  entrepotId: number; entrepotNom: string; commandeFournisseurId?: number
  statut: string; dateReception: string; note?: string
  creePar: string; valideePar?: string; dateValidation?: string
  lignes: ReceiptLine[]
}
export interface ReceiptLine {
  id: number; produitId: number; produitNom: string
  quantiteAttendue: number; quantiteRecue: number; qualiteOk: boolean; noteQualite?: string
}
export interface ReceiptCreateDTO {
  fournisseurId: number; entrepotId: number; commandeFournisseurId?: number
  dateReception: string; note?: string
  lignes: { produitId: number; quantiteAttendue?: number; quantiteRecue: number; qualiteOk: boolean; noteQualite?: string }[]
}

export interface Issue {
  id: number; numero: string; entrepotId: number; entrepotNom: string
  motif: string; clientNom?: string; clientReference?: string
  statut: string; dateSortie: string; note?: string
  creePar: string; valideePar?: string; lignes: IssueLine[]
}
export interface IssueLine {
  id: number; produitId: number; produitNom: string
  quantiteDemandee: number; quantiteSortie: number
}
export interface IssueCreateDTO {
  entrepotId: number; motif: string; clientNom?: string; clientReference?: string
  dateSortie: string; note?: string
  lignes: { produitId: number; quantiteDemandee: number }[]
}

export interface Transfer {
  id: number; numero: string
  entrepotSourceId: number; entrepotSourceNom: string
  entrepotDestinationId: number; entrepotDestinationNom: string
  statut: string; dateExpedition?: string; dateReception?: string
  creePar: string; expedieePar?: string; recuePar?: string; note?: string
  lignes: TransferLine[]
}
export interface TransferLine {
  id: number; produitId: number; produitNom: string
  quantiteDemandee: number; quantiteRecue: number
}
export interface TransferCreateDTO {
  entrepotSourceId: number; entrepotDestinationId: number; note?: string
  lignes: { produitId: number; quantiteDemandee: number }[]
}

export interface PurchaseOrder {
  id: number; numero: string; fournisseurId: number; fournisseurNom: string
  entrepotDestinationId: number; entrepotNom: string; statut: string
  dateCommande: string; dateLivraisonPrevue?: string; dateLivraisonReelle?: string
  montantTotal: number; note?: string; creePar: string; valideeePar?: string
  lignes: POLine[]
}
export interface POLine {
  id: number; produitId: number; produitNom: string
  quantiteCommandee: number; quantiteRecue: number
  prixUnitaire: number; montantLigne: number
}
export interface POCreateDTO {
  fournisseurId: number; entrepotDestinationId: number
  dateCommande?: string; dateLivraisonPrevue?: string; note?: string
  lignes: { produitId: number; quantiteCommandee: number; prixUnitaire: number }[]
}

export interface Inventory {
  id: number; numero: string; entrepotId: number; entrepotNom: string
  zoneId?: number; zoneNom?: string; categorieId?: number; categorieNom?: string
  type: string; statut: string
  datePlanifiee: string; dateDebut?: string; dateFin?: string
  note?: string; creePar: string; valideePar?: string
  nbLignes: number; nbEcarts: number; lignes: InventoryLine[]
}
export interface InventoryLine {
  id: number; produitId: number; produitNom: string; produitRef: string
  quantiteTheorique: number; quantiteComptee: number; ecart: number; ajuste: boolean; note?: string
}
export interface InventoryCreateDTO {
  entrepotId: number; zoneId?: number; categorieId?: number
  type?: string; datePlanifiee: string; note?: string
}

// ── Page wrapper ──────────────────────────────────────────────────────────

export interface Page<T> { content: T[]; totalElements: number; totalPages: number; number: number; size: number }

// ── Services ──────────────────────────────────────────────────────────────

export const receiptService = {
  findAll: (page = 0, size = 20) => api.get<Page<Receipt>>('/receipts', { params: { page, size } }).then(r => r.data),
  findById: (id: number) => api.get<Receipt>(`/receipts/${id}`).then(r => r.data),
  create: (dto: ReceiptCreateDTO) => api.post<Receipt>('/receipts', dto).then(r => r.data),
  submit: (id: number) => api.post<Receipt>(`/receipts/${id}/submit`).then(r => r.data),
  validate: (id: number) => api.post<Receipt>(`/receipts/${id}/validate`).then(r => r.data),
  reject: (id: number) => api.post<Receipt>(`/receipts/${id}/reject`).then(r => r.data),
}

export const issueService = {
  findAll: (page = 0, size = 20) => api.get<Page<Issue>>('/issues', { params: { page, size } }).then(r => r.data),
  findById: (id: number) => api.get<Issue>(`/issues/${id}`).then(r => r.data),
  create: (dto: IssueCreateDTO) => api.post<Issue>('/issues', dto).then(r => r.data),
  submit: (id: number) => api.post<Issue>(`/issues/${id}/submit`).then(r => r.data),
  validate: (id: number) => api.post<Issue>(`/issues/${id}/validate`).then(r => r.data),
  cancel: (id: number) => api.post<Issue>(`/issues/${id}/cancel`).then(r => r.data),
}

export const transferService = {
  findAll: (page = 0, size = 20) => api.get<Page<Transfer>>('/transfers', { params: { page, size } }).then(r => r.data),
  findById: (id: number) => api.get<Transfer>(`/transfers/${id}`).then(r => r.data),
  create: (dto: TransferCreateDTO) => api.post<Transfer>('/transfers', dto).then(r => r.data),
  ship: (id: number) => api.post<Transfer>(`/transfers/${id}/ship`).then(r => r.data),
  receive: (id: number) => api.post<Transfer>(`/transfers/${id}/receive`).then(r => r.data),
  cancel: (id: number) => api.post<Transfer>(`/transfers/${id}/cancel`).then(r => r.data),
}

export const purchaseOrderService = {
  findAll: (page = 0, size = 20) => api.get<Page<PurchaseOrder>>('/purchase-orders', { params: { page, size } }).then(r => r.data),
  findById: (id: number) => api.get<PurchaseOrder>(`/purchase-orders/${id}`).then(r => r.data),
  create: (dto: POCreateDTO) => api.post<PurchaseOrder>('/purchase-orders', dto).then(r => r.data),
  validate: (id: number) => api.post<PurchaseOrder>(`/purchase-orders/${id}/validate`).then(r => r.data),
  send: (id: number) => api.post<PurchaseOrder>(`/purchase-orders/${id}/send`).then(r => r.data),
  deliver: (id: number) => api.post<PurchaseOrder>(`/purchase-orders/${id}/deliver`).then(r => r.data),
  cancel: (id: number) => api.post<PurchaseOrder>(`/purchase-orders/${id}/cancel`).then(r => r.data),
}

export const inventoryService = {
  findAll: (page = 0, size = 20) => api.get<Page<Inventory>>('/inventories', { params: { page, size } }).then(r => r.data),
  findById: (id: number) => api.get<Inventory>(`/inventories/${id}`).then(r => r.data),
  create: (dto: InventoryCreateDTO) => api.post<Inventory>('/inventories', dto).then(r => r.data),
  start: (id: number) => api.post<Inventory>(`/inventories/${id}/start`).then(r => r.data),
  count: (id: number, dto: { produitId: number; quantiteComptee: number; note?: string }) =>
    api.post<Inventory>(`/inventories/${id}/count`, dto).then(r => r.data),
  validate: (id: number) => api.post<Inventory>(`/inventories/${id}/validate`).then(r => r.data),
  cancel: (id: number) => api.post<Inventory>(`/inventories/${id}/cancel`).then(r => r.data),
}
