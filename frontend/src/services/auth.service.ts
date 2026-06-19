import api from './api'

export interface LoginPayload {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  email?: string
  fullName?: string
  role?: string
}

export interface AuthResponse {
  token: string
  refreshToken: string
  user: {
    id: number
    username: string
    email: string
    fullName: string
    role: string
  }
}

const AuthService = {
  async login(payload: LoginPayload): Promise<AuthResponse> {
    const { data } = await api.post<AuthResponse>('/auth/login', payload)
    localStorage.setItem('token', data.token)
    localStorage.setItem('refreshToken', data.refreshToken)
    return data
  },

  async register(payload: RegisterRequest): Promise<AuthResponse> {
    const { data } = await api.post<AuthResponse>('/auth/register', payload)
    localStorage.setItem('token', data.token)
    localStorage.setItem('refreshToken', data.refreshToken)
    return data
  },

  async logout(): Promise<void> {
    try {
      await api.post('/auth/logout')
    } finally {
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
    }
  },

  async refresh(): Promise<string> {
    const refreshToken = localStorage.getItem('refreshToken')
    const { data } = await api.post<{ token: string }>('/auth/refresh', { refreshToken })
    localStorage.setItem('token', data.token)
    return data.token
  }
}

export default AuthService
