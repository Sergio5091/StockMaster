import api from './api'

export interface Category {
  id: number
  nom: string
  description?: string
  parentId?: number
  parentNom?: string
  actif: boolean
}

export interface CategoryCreateDTO {
  nom: string
  description?: string
  parentId?: number
}

export interface CategoryUpdateDTO {
  nom?: string
  description?: string
  parentId?: number
}

export const categoryService = {
  async findAll(): Promise<Category[]> {
    const response = await api.get('/categories')
    return response.data
  },

  async findActiveCategories(): Promise<Category[]> {
    const response = await api.get('/categories/active')
    return response.data
  },

  async findById(id: number): Promise<Category> {
    const response = await api.get(`/categories/${id}`)
    return response.data
  },

  async create(data: CategoryCreateDTO): Promise<Category> {
    const response = await api.post('/categories', data)
    return response.data
  },

  async update(id: number, data: CategoryUpdateDTO): Promise<Category> {
    const response = await api.put(`/categories/${id}`, data)
    return response.data
  },

  async delete(id: number): Promise<void> {
    await api.delete(`/categories/${id}`)
  },
}
