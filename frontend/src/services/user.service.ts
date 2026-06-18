import { api } from './api.config'

export interface User {
  id: number
  nom: string
  prenom: string
  email: string
  role: string
  actif: boolean
  entrepots: string[]
  createdAt: string
  lastLogin?: string
}

export interface UserCreateDTO {
  nom: string
  prenom: string
  email: string
  role: string
  entrepots: string[]
}

export interface UserUpdateDTO {
  nom?: string
  prenom?: string
  email?: string
  role?: string
  entrepots?: string[]
  actif?: boolean
}

export const userService = {
  async findAll(): Promise<User[]> {
    const response = await api.get('/users')
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

  async delete(id: number): Promise<void> {
    await api.delete(`/users/${id}`)
  }
}