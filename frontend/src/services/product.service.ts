import api from './api'

export interface Product {
  id: number
  reference: string
  codeBarre?: string
  nom: string
  description?: string
  categorieId?: number
  categorieNom?: string
  fournisseurPrincipalId?: number
  fournisseurNom?: string
  prixAchat?: number
  prixVente?: number
  poidsKg?: number
  volumeM3?: number
  stockMinimum: number
  stockMaximum: number
  uniteMesure: string
  actif: boolean
  imageUrl?: string
  niveauStock?: string
}

export interface ProductCreateDTO {
  nom: string
  description?: string
  categorieId: number
  codeBarre?: string
  fournisseurPrincipalId?: number
  prixAchat?: number
  prixVente?: number
  poidsKg?: number
  volumeM3?: number
  stockMinimum: number
  stockMaximum: number
  uniteMesure: string
}

export interface ProductUpdateDTO extends ProductCreateDTO {}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

const ProductService = {
  async getAll(page = 0, size = 20, categorieId?: number, fournisseurId?: number): Promise<PageResponse<Product>> {
    const { data } = await api.get('/products', {
      params: { page, size, ...(categorieId ? { categorieId } : {}), ...(fournisseurId ? { fournisseurId } : {}) },
    })
    return data
  },

  async getById(id: number): Promise<Product> {
    const { data } = await api.get(`/products/${id}`)
    return data
  },

  async create(product: ProductCreateDTO): Promise<Product> {
    const { data } = await api.post('/products', product)
    return data
  },

  async update(id: number, product: ProductUpdateDTO): Promise<Product> {
    const { data } = await api.put(`/products/${id}`, product)
    return data
  },

  async deactivate(id: number): Promise<void> {
    await api.patch(`/products/${id}/deactivate`)
  },

  async search(query: string, page = 0, size = 20): Promise<PageResponse<Product>> {
    const { data } = await api.get('/products/search', { params: { q: query, page, size } })
    return data
  },

  async uploadImage(id: number, file: File): Promise<Product> {
    const formData = new FormData()
    formData.append('image', file)
    const { data } = await api.post(`/products/${id}/image`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    return data
  },
}

export default ProductService
