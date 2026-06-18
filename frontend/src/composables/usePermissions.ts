import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'

export function usePermissions() {
  const auth = useAuthStore()

  const can = (permission: string): boolean => {
    const role = auth.role
    const perms: Record<string, string[]> = {
      'manage_users':         ['ROLE_ADMIN'],
      'manage_warehouses':    ['ROLE_ADMIN', 'ROLE_MANAGER'],
      'manage_zones':         ['ROLE_ADMIN', 'ROLE_MANAGER'],
      'manage_locations':     ['ROLE_ADMIN', 'ROLE_MANAGER'],
      'manage_categories':    ['ROLE_ADMIN'],
      'manage_products':      ['ROLE_ADMIN', 'ROLE_MANAGER'],
      'manage_suppliers':     ['ROLE_ADMIN', 'ROLE_MANAGER'],
      'view_stocks':          ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR', 'ROLE_AUDITOR'],
      'create_receipt':       ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR'],
      'validate_receipt':     ['ROLE_ADMIN', 'ROLE_MANAGER'],
      'create_issue':         ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR'],
      'validate_issue':       ['ROLE_ADMIN', 'ROLE_MANAGER'],
      'create_transfer':      ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR'],
      'validate_transfer':    ['ROLE_ADMIN', 'ROLE_MANAGER'],
      'manage_orders':        ['ROLE_ADMIN', 'ROLE_MANAGER'],
      'launch_inventory':     ['ROLE_ADMIN', 'ROLE_MANAGER'],
      'fill_inventory':       ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR'],
      'view_reports':         ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_AUDITOR'],
      'configure_alerts':     ['ROLE_ADMIN', 'ROLE_MANAGER'],
      'view_audit':           ['ROLE_ADMIN'],
      'view_dashboard':       ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR', 'ROLE_AUDITOR'],
    }
    return perms[permission]?.includes(role || '') ?? false
  }

  const canAccess = (resource: string, warehouseId?: number): boolean => {
    if (auth.isAdmin) return true
    if (resource === 'warehouse' && warehouseId) {
      return auth.user?.entrepots.some(e => e.id === warehouseId) ?? false
    }
    return can(`view_${resource}s`)
  }

  return {
    can,
    canAccess,
    isAdmin: computed(() => auth.isAdmin),
    isManager: computed(() => auth.isManager),
    isOperator: computed(() => auth.isOperator),
    isAuditor: computed(() => auth.isAuditor),
  }
}
