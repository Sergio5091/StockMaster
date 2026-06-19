import api from './api'

export interface Warehouse {
  id: number
  code: string
  nom: string
  adresse?: string
  ville: string
  pays?: string
  telephone?: string
  email?: string
  responsableId?: number
  responsableUsername?: string
  capaciteTotale: number
  capaciteUtilisee: number
  nbZones: number
  actif: boolean
}

export interface WarehouseCreateDTO {
  nom: string
  adresse?: string
  ville: string
  pays?: string
  telephone?: string
  email?: string
  responsableId?: number
  capaciteTotale: number
}

export interface WarehouseUpdateDTO extends WarehouseCreateDTO {}

export interface Zone {
  id: number
  code: string
  nom: string
  type: string
  entrepotId: number
  entrepotNom?: string
  capaciteM3: number
  occupationM3: number
  tauxOccupation: number
  actif: boolean
}

export interface ZoneCreateDTO {
  nom: string
  type: string
  capaciteM3?: number
}

export interface Location {
  id: number
  code: string
  zoneId: number
  zoneNom?: string
  rayon?: string
  etagere?: string
  position?: string
  capaciteKg: number
  poidsActuel: number
  statut: string
  produitId?: number
  produitNom?: string
}

export interface LocationCreateDTO {
  rayon?: string
  etagere?: string
  position?: string
  capaciteKg?: number
}

export interface Alert {
  id: number
  type: string
  titre: string
  message: string
  entrepotId?: number
  entrepotNom?: string
  traitee: boolean
  traiteeParUsername?: string
  traiteeAt?: string
  createdAt: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
}

const WarehouseService = {
  async getAll(): Promise<Warehouse[]> {
    const { data } = await api.get('/warehouses')
    return data
  },
  async getById(id: number): Promise<Warehouse> {
    const { data } = await api.get(`/warehouses/${id}`)
    return data
  },
  async create(dto: WarehouseCreateDTO): Promise<Warehouse> {
    const { data } = await api.post('/warehouses', dto)
    return data
  },
  async update(id: number, dto: WarehouseUpdateDTO): Promise<Warehouse> {
    const { data } = await api.put(`/warehouses/${id}`, dto)
    return data
  },
  async delete(id: number): Promise<void> {
    await api.delete(`/warehouses/${id}`)
  },

  // Zones
  async getZones(warehouseId: number): Promise<Zone[]> {
    const { data } = await api.get(`/warehouses/${warehouseId}/zones`)
    return data
  },
  async createZone(warehouseId: number, dto: ZoneCreateDTO): Promise<Zone> {
    const { data } = await api.post(`/warehouses/${warehouseId}/zones`, dto)
    return data
  },
  async deleteZone(zoneId: number): Promise<void> {
    await api.delete(`/zones/${zoneId}`)
  },

  // Locations
  async getLocations(zoneId: number): Promise<Location[]> {
    const { data } = await api.get(`/zones/${zoneId}/locations`)
    return data
  },
  async getLocationById(id: number): Promise<Location> {
    const { data } = await api.get(`/locations/${id}`)
    return data
  },
  async createLocation(zoneId: number, dto: LocationCreateDTO): Promise<Location> {
    const { data } = await api.post(`/zones/${zoneId}/locations`, dto)
    return data
  },

  // Alerts
  async getAlerts(page = 0, size = 50): Promise<PageResponse<Alert>> {
    const { data } = await api.get('/alerts', { params: { page, size } })
    return data
  },
  async getRecentAlerts(): Promise<Alert[]> {
    const { data } = await api.get('/alerts/recent')
    return data
  },
  async treatAlert(id: number): Promise<Alert> {
    const { data } = await api.post(`/alerts/${id}/treat`)
    return data
  },
  async reopenAlert(id: number): Promise<Alert> {
    const { data } = await api.post(`/alerts/${id}/reopen`)
    return data
  },
  async treatAllAlerts(): Promise<void> {
    await api.post('/alerts/treat-all')
  },
  async countUnreadAlerts(): Promise<number> {
    const { data } = await api.get('/alerts/count-unread')
    return data.count
  },
}

export default WarehouseService
