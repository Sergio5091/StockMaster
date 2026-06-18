import { api } from './api.config'

export interface Supplier {
  id: number
  code: string
  nom: string
  adresse?: string
  ville: string
  pays: string
  telephone?: string
  email?: string
  contactNom?: string
  contactPrenom?: string
  contactTelephone?: string
  contactEmail?: string
  delaiLivraison: number
  actif: boolean
  commandesTotal: number
  tauxRespectDelai: number
  montantTotal: number
}

export interface SupplierCreateDTO {
  nom: string
  adresse?: string
  ville: string
  pays: string
  telephone?: string
  email?: string
  contactNom?: string
  contactPrenom?: string
  contactTelephone?: string
  contactEmail?: string
  delaiLivraison: number
}

export interface SupplierUpdateDTO {
  nom?: string
  adresse?: string
  ville?: string
  pays?: string
  telephone?: string
  email?: string
  contactNom?: string
  contactPrenom?: string
  contactTelephone?: string
  contactEmail?: string
  delaiLivraison?: number
  actif?: boolean
}

export const supplierService = {
  async findAll(): Promise<Supplier[]> {
    const response = await api.get('/suppliers')
    return response.data
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

  async delete(id: number): Promise<void> {
    await api.delete(`/suppliers/${id}`)
  }
}