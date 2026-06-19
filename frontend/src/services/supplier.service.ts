import api from './api'

export interface Supplier {
  id: number
  code: string
  nom: string
  adresse?: string
  ville: string
  pays: string
  telephone?: string
  email?: string
  contactPrincipalNom?: string
  contactPrincipalPrenom?: string
  contactPrincipalTelephone?: string
  contactPrincipalEmail?: string
  delaiLivraisonJours: number
  actif: boolean
}

export interface SupplierCreateDTO {
  nom: string
  adresse?: string
  ville: string
  pays: string
  telephone?: string
  email?: string
  contactPrincipalNom?: string
  contactPrincipalPrenom?: string
  contactPrincipalTelephone?: string
  contactPrincipalEmail?: string
  delaiLivraisonJours: number
}

export interface SupplierUpdateDTO {
  nom?: string
  adresse?: string
  ville?: string
  pays?: string
  telephone?: string
  email?: string
  contactPrincipalNom?: string
  contactPrincipalPrenom?: string
  contactPrincipalTelephone?: string
  contactPrincipalEmail?: string
  delaiLivraisonJours?: number
  actif?: boolean
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export const supplierService = {
  async findAll(page = 0, size = 100): Promise<PageResponse<Supplier>> {
    const response = await api.get('/suppliers', { params: { page, size } })
    return response.data
  },

  /** Raccourci pour les selects / listes simples */
  async findAllList(): Promise<Supplier[]> {
    const page = await supplierService.findAll(0, 200)
    return page.content
  },

  async findById(id: number): Promise<Supplier> {
    const response = await api.get(`/suppliers/${id}`)
    return response.data
  },

  async create(data: SupplierCreateDTO): Promise<Supplier> {
    const response = await api.post('/suppliers', data)
    return response.data
  },

  async update(id: number, data: SupplierUpdateDTO): Promise<Supplier> {
    const response = await api.put(`/suppliers/${id}`, data)
    return response.data
  },

  async deactivate(id: number): Promise<void> {
    await api.patch(`/suppliers/${id}/deactivate`)
  },
}
