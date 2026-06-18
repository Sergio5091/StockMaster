import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import WarehouseService, { type Warehouse, type WarehouseCreateDTO, type WarehouseUpdateDTO } from '@/services/warehouse.service'

export const useWarehouseStore = defineStore('warehouse', () => {
  const warehouses = ref<Warehouse[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Computed
  const warehousesActive = computed(() => warehouses.value.filter(w => w.actif))
  
  // Actions
  async function fetchWarehouses() {
    loading.value = true
    error.value = null
    try {
      warehouses.value = await WarehouseService.getAll()
    } catch (err: any) {
      error.value = err.message || 'Erreur lors du chargement des entrepôts'
      console.error('Error fetching warehouses:', err)
    } finally {
      loading.value = false
    }
  }

  async function createWarehouse(warehouse: WarehouseCreateDTO) {
    loading.value = true
    error.value = null
    try {
      const newWarehouse = await WarehouseService.create(warehouse)
      warehouses.value.unshift(newWarehouse)
      return newWarehouse
    } catch (err: any) {
      error.value = err.message || 'Erreur lors de la création de l\'entrepôt'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function updateWarehouse(id: number, warehouse: WarehouseUpdateDTO) {
    loading.value = true
    error.value = null
    try {
      const updated = await WarehouseService.update(id, warehouse)
      const index = warehouses.value.findIndex(w => w.id === id)
      if (index !== -1) {
        warehouses.value[index] = updated
      }
      return updated
    } catch (err: any) {
      error.value = err.message || 'Erreur lors de la modification de l\'entrepôt'
      throw err
    } finally {
      loading.value = false
    }
  }

  async function deleteWarehouse(id: number) {
    loading.value = true
    error.value = null
    try {
      await WarehouseService.delete(id)
      const index = warehouses.value.findIndex(w => w.id === id)
      if (index !== -1) {
        warehouses.value.splice(index, 1)
      }
    } catch (err: any) {
      error.value = err.message || 'Erreur lors de la suppression de l\'entrepôt'
      throw err
    } finally {
      loading.value = false
    }
  }

  function getWarehouseById(id: number): Warehouse | undefined {
    return warehouses.value.find(w => w.id === id)
  }

  return {
    // State
    warehouses,
    loading,
    error,
    
    // Getters
    warehousesActive,
    
    // Actions
    fetchWarehouses,
    createWarehouse,
    updateWarehouse,
    deleteWarehouse,
    getWarehouseById
  }
})