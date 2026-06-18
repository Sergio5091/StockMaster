import { ref, computed } from 'vue'

export function usePagination<T>(items: T[], pageSize = 10) {
  const currentPage = ref(1)
  const itemsPerPage = ref(pageSize)

  const totalPages = computed(() => Math.max(1, Math.ceil(items.length / itemsPerPage.value)))
  const paginatedItems = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage.value
    return items.slice(start, start + itemsPerPage.value)
  })
  const totalItems = computed(() => items.length)

  function goToPage(page: number) { if (page >= 1 && page <= totalPages.value) currentPage.value = page }
  function nextPage() { goToPage(currentPage.value + 1) }
  function prevPage() { goToPage(currentPage.value - 1) }
  function reset() { currentPage.value = 1 }

  return { currentPage, itemsPerPage, totalPages, paginatedItems, totalItems, goToPage, nextPage, prevPage, reset }
}

export function useReactivePagination<T>(getItems: () => T[], pageSize = 10) {
  const currentPage = ref(1)
  const itemsPerPage = ref(pageSize)

  const totalPages = computed(() => Math.max(1, Math.ceil(getItems().length / itemsPerPage.value)))
  const paginatedItems = computed(() => {
    const items = getItems()
    const start = (currentPage.value - 1) * itemsPerPage.value
    return items.slice(start, start + itemsPerPage.value)
  })
  const totalItems = computed(() => getItems().length)

  function goToPage(page: number) { if (page >= 1 && page <= totalPages.value) currentPage.value = page }
  function reset() { currentPage.value = 1 }

  return { currentPage, itemsPerPage, totalPages, paginatedItems, totalItems, goToPage, reset }
}
