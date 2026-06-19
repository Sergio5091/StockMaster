import api from './api'

// Aligne avec l'enum Role backend : ADMINISTRATEUR, GESTIONNAIRE, MAGASINIER, AUDITEUR
export type Role = 'ADMINISTRATEUR' | 'GESTIONNAIRE' | 'MAGASINIER' | 'AUDITEUR'

export interface User {
  id: number
  username: string
  email?: string
  fullName?: string
  role: Role
  active: boolean
  createdAt?: string
  updatedAt?: string
}

export interface UserCreateDTO {
  username: string
  password: string
  email?: string
  fullName?: string
  role: Role
}

export interface UserUpdateDTO {
  username?: string
  email?: string
  fullName?: string
  role?: Role
  active?: boolean
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
}

export const userService = {
  async findAll(page = 0, size = 50): Promise<PageResponse<User>> {
    const response = await api.get('/users', { params: { page, size } })
    return response.data
  },

  async findById(id: number): Promise<User> {
    const response = await api.get(`/users/${id}`)
    return response.data
  },

  async create(data: UserCreateDTO): Promise<User> {
    const response = await api.post('/users', data)
    return response.data
  },

  async update(id: number, data: UserUpdateDTO): Promise<User> {
    const response = await api.put(`/users/${id}`, data)
    return response.data
  },

  async deactivate(id: number): Promise<void> {
    await api.patch(`/users/${id}/deactivate`)
  },

  async activate(id: number): Promise<void> {
    await api.patch(`/users/${id}/activate`)
  },

  async resetPassword(id: number, newPassword: string): Promise<void> {
    await api.post(`/users/${id}/reset-password`, { newPassword })
  },
}
