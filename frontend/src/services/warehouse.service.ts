import api from './api'

export interface Warehouse {
  id: number
  code: string
  nom: string
  adresse: string
  ville: string
  pays: string
  telephone: string
  email: string
  responsable?: {
    id: number
    nom: string
  }
  capaciteTotale: number
  capaciteUtilisee: number
  actif: boolean
  zones?: number
  createdAt: string
}

export interface WarehouseCreateDTO {
  nom: string
  adresse: string
  ville: string
  pays: string
  telephone: string
  email: string
  responsableId?: number
  capaciteTotale: number
}

export interface WarehouseUpdateDTO extends WarehouseCreateDTO {}

const WarehouseService = {
  async getAll(): Promise<Warehouse[]> {
    const { data } = await api.get('/warehouses')
    return data
  },

  async getById(id: number): Promise<Warehouse> {
    const { data } = await api.get(`/warehouses/${id}`)
    return data
  },

  async create(warehouse: WarehouseCreateDTO): Promise<Warehouse> {
    const { data } = await api.post('/warehouses', warehouse)
    return data
  },

  async update(id: number, warehouse: WarehouseUpdateDTO): Promise<Warehouse> {
    const { data } = await api.put(`/warehouses/${id}`, warehouse)
    return data
  },

  async delete(id: number): Promise<void> {
    await api.delete(`/warehouses/${id}`)
  },

  async getStats(id: number): Promise<any> {
    const { data } = await api.get(`/warehouses/${id}/stats`)
    return data
  }
}

export default WarehouseService