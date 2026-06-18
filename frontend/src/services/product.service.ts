import api from './api'

export interface Product {
  id: number
  reference: string
  codeBarres?: string
  nom: string
  description: string
  categorieId: number
  categorieNom?: string
  fournisseurId?: number
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
  stockTotal?: number
  stockStatut?: string
}

export interface ProductCreateDTO {
  nom: string
  description: string
  categorieId: number
  codeBarres?: string
  fournisseurId?: number
  prixAchat?: number
  prixVente?: number
  poidsKg?: number
  volumeM3?: number
  stockMinimum: number
  stockMaximum: number
  uniteMesure: string
}

export interface ProductUpdateDTO extends ProductCreateDTO {}

const ProductService = {
  async getAll(page = 0, size = 20): Promise<{ content: Product[], totalElements: number }> {
    const { data } = await api.get(`/products?page=${page}&size=${size}`)
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

  async delete(id: number): Promise<void> {
    await api.patch(`/products/${id}/deactivate`)
  },

  async search(query: string): Promise<Product[]> {
    const { data } = await api.get(`/products/search?q=${encodeURIComponent(query)}`)
    return data
  }
}

export default ProductService