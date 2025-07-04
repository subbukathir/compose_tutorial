package com.smarttoolfactory.tutorial1_1basics.model

data class PaginationState(
    val currentPage: Int = 0,
    val pageSize: Int = 10,
    val totalItems: Int = 0
) {
    val totalPages: Int
        get() = if (totalItems == 0) 0 else (totalItems - 1) / pageSize + 1
    
    val hasNextPage: Boolean
        get() = currentPage < totalPages - 1
    
    val hasPreviousPage: Boolean
        get() = currentPage > 0
    
    val startIndex: Int
        get() = currentPage * pageSize
    
    val endIndex: Int
        get() = kotlin.math.min(startIndex + pageSize, totalItems)
    
    val pageRange: IntRange
        get() = startIndex until endIndex
    
    fun nextPage(): PaginationState = copy(currentPage = currentPage + 1)
    
    fun previousPage(): PaginationState = copy(currentPage = currentPage - 1)
    
    fun goToPage(page: Int): PaginationState = copy(currentPage = page.coerceIn(0, totalPages - 1))
    
    fun updateTotalItems(total: Int): PaginationState = copy(
        totalItems = total,
        currentPage = if (currentPage >= (total - 1) / pageSize + 1) 0 else currentPage
    )
}