import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      component: () => import('@/layouts/AuthLayout.vue'),
      children: [{ path: '', name: 'login', component: () => import('@/views/auth/LoginView.vue') }],
      meta: { guest: true },
    },
    {
      path: '/',
      component: () => import('@/layouts/DefaultLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        { path: '', redirect: '/dashboard' },
        { path: 'dashboard', name: 'dashboard', component: () => import('@/views/dashboard/DashboardView.vue') },
        { path: 'users', name: 'users', component: () => import('@/views/users/UsersView.vue'), meta: { roles: ['ROLE_ADMIN'] } },
        { path: 'warehouses', name: 'warehouses', component: () => import('@/views/warehouses/WarehousesView.vue') },
        { path: 'warehouses/:id', name: 'warehouse-detail', component: () => import('@/views/warehouses/WarehouseDetailView.vue') },
        { path: 'warehouses/:warehouseId/zones', name: 'zones', component: () => import('@/views/zones/ZonesView.vue') },
        { path: 'zones/:id/locations', name: 'locations', component: () => import('@/views/locations/LocationsView.vue') },
        { path: 'locations/:id', name: 'location-detail', component: () => import('@/views/locations/LocationDetailView.vue') },
        { path: 'categories', name: 'categories', component: () => import('@/views/categories/CategoriesView.vue'), meta: { roles: ['ROLE_ADMIN'] } },
        { path: 'products', name: 'products', component: () => import('@/views/products/ProductsView.vue') },
        { path: 'products/:id', name: 'product-detail', component: () => import('@/views/products/ProductDetailView.vue') },
        { path: 'suppliers', name: 'suppliers', component: () => import('@/views/suppliers/SuppliersView.vue') },
        { path: 'suppliers/:id', name: 'supplier-detail', component: () => import('@/views/suppliers/SupplierDetailView.vue') },
        { path: 'stocks', name: 'stocks', component: () => import('@/views/stocks/StocksView.vue') },
        { path: 'stocks/movements', name: 'stock-movements', component: () => import('@/views/stocks/StockMovementsView.vue') },
        { path: 'receipts', name: 'receipts', component: () => import('@/views/receipts/ReceiptsView.vue') },
        { path: 'receipts/new', name: 'receipt-new', component: () => import('@/views/receipts/ReceiptFormView.vue') },
        { path: 'receipts/:id', name: 'receipt-detail', component: () => import('@/views/receipts/ReceiptDetailView.vue') },
        { path: 'issues', name: 'issues', component: () => import('@/views/issues/IssuesView.vue') },
        { path: 'issues/new', name: 'issue-new', component: () => import('@/views/issues/IssueFormView.vue') },
        { path: 'issues/:id', name: 'issue-detail', component: () => import('@/views/issues/IssueDetailView.vue') },
        { path: 'transfers', name: 'transfers', component: () => import('@/views/transfers/TransfersView.vue') },
        { path: 'transfers/new', name: 'transfer-new', component: () => import('@/views/transfers/TransferFormView.vue') },
        { path: 'transfers/:id', name: 'transfer-detail', component: () => import('@/views/transfers/TransferDetailView.vue') },
        { path: 'purchase-orders', name: 'purchase-orders', component: () => import('@/views/orders/PurchaseOrdersView.vue') },
        { path: 'purchase-orders/new', name: 'purchase-order-new', component: () => import('@/views/orders/PurchaseOrderFormView.vue') },
        { path: 'purchase-orders/:id', name: 'purchase-order-detail', component: () => import('@/views/orders/PurchaseOrderDetailView.vue') },
        { path: 'inventories', name: 'inventories', component: () => import('@/views/inventories/InventoriesView.vue') },
        { path: 'inventories/new', name: 'inventory-new', component: () => import('@/views/inventories/InventoryFormView.vue') },
        { path: 'inventories/:id/session', name: 'inventory-session', component: () => import('@/views/inventories/InventorySessionView.vue') },
        { path: 'alerts', name: 'alerts', component: () => import('@/views/alerts/AlertsView.vue') },
        { path: 'alerts/config', name: 'alerts-config', component: () => import('@/views/alerts/AlertConfigView.vue') },
        { path: 'reports', name: 'reports', component: () => import('@/views/reports/ReportsView.vue') },
        { path: 'audit', name: 'audit', component: () => import('@/views/audit/AuditLogsView.vue'), meta: { roles: ['ROLE_ADMIN'] } },
        { path: 'profile', name: 'profile', component: () => import('@/views/profile/ProfileView.vue') },
      ],
    },
    { path: '/:pathMatch(.*)*', redirect: '/dashboard' },
  ],
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isAuthenticated) return '/login'
  if (to.meta.guest && auth.isAuthenticated) return '/dashboard'
  if (to.meta.roles && !auth.hasAnyRole(to.meta.roles as string[])) return '/dashboard'
  return true
})

export default router
