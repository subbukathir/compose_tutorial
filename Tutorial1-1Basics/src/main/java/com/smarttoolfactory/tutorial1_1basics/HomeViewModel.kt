package com.smarttoolfactory.tutorial1_1basics


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.smarttoolfactory.tutorial1_1basics.model.PaginationState
import com.smarttoolfactory.tutorial1_1basics.model.SuggestionModel
import com.smarttoolfactory.tutorial1_1basics.model.TutorialSectionModel

class HomeViewModel : ViewModel() {

    val tutorialList = mutableListOf<List<TutorialSectionModel>>()
    
    // Store current search results for pagination
    private var currentSearchResults = listOf<TutorialSectionModel>()
    
    // Pagination states for each tab
    var componentsPaginationState by mutableStateOf(PaginationState(pageSize = 10))
        private set
    var layoutPaginationState by mutableStateOf(PaginationState(pageSize = 10))
        private set
    var statePaginationState by mutableStateOf(PaginationState(pageSize = 10))
        private set
    var gesturePaginationState by mutableStateOf(PaginationState(pageSize = 10))
        private set
    var graphicsPaginationState by mutableStateOf(PaginationState(pageSize = 10))
        private set
    
    // Pagination state for search results
    var searchPaginationState by mutableStateOf(PaginationState(pageSize = 10))
        private set

    fun updatePaginationStates() {
        if (tutorialList.isNotEmpty()) {
            componentsPaginationState = componentsPaginationState.updateTotalItems(
                tutorialList.getOrNull(0)?.size ?: 0
            )
            layoutPaginationState = layoutPaginationState.updateTotalItems(
                tutorialList.getOrNull(1)?.size ?: 0
            )
            statePaginationState = statePaginationState.updateTotalItems(
                tutorialList.getOrNull(2)?.size ?: 0
            )
            gesturePaginationState = gesturePaginationState.updateTotalItems(
                tutorialList.getOrNull(3)?.size ?: 0
            )
            graphicsPaginationState = graphicsPaginationState.updateTotalItems(
                tutorialList.getOrNull(4)?.size ?: 0
            )
        }
    }

    fun updatePageForTab(tabIndex: Int, newPaginationState: PaginationState) {
        when (tabIndex) {
            0 -> componentsPaginationState = newPaginationState
            1 -> layoutPaginationState = newPaginationState
            2 -> statePaginationState = newPaginationState
            3 -> gesturePaginationState = newPaginationState
            4 -> graphicsPaginationState = newPaginationState
        }
    }

    fun getPaginationStateForTab(tabIndex: Int): PaginationState {
        return when (tabIndex) {
            0 -> componentsPaginationState
            1 -> layoutPaginationState
            2 -> statePaginationState
            3 -> gesturePaginationState
            4 -> graphicsPaginationState
            else -> PaginationState()
        }
    }

    fun getPaginatedTutorials(tabIndex: Int): List<TutorialSectionModel> {
        val fullList = tutorialList.getOrNull(tabIndex) ?: return emptyList()
        val paginationState = getPaginationStateForTab(tabIndex)
        
        return if (paginationState.totalItems > 0) {
            fullList.subList(
                paginationState.startIndex,
                paginationState.endIndex
            )
        } else {
            emptyList()
        }
    }

    fun getTutorials(query: String): List<TutorialSectionModel> {

        val filteredList = linkedSetOf<TutorialSectionModel>()

        tutorialList.forEach { list: List<TutorialSectionModel> ->

            list.forEach { tutorialSectionModel ->

                if (tutorialSectionModel.title.contains(query, ignoreCase = true)) {
                    filteredList.add(tutorialSectionModel)
                }

                if (tutorialSectionModel.description.contains(query, ignoreCase = true)) {
                    filteredList.add(tutorialSectionModel)
                }

                tutorialSectionModel.tags.forEach {
                    if (it.contains(query, ignoreCase = true)) {
                        filteredList.add(tutorialSectionModel)
                    }
                }
            }
        }

        // Store search results and update pagination state
        currentSearchResults = filteredList.toList()
        searchPaginationState = searchPaginationState.copy(
            currentPage = 0,
            totalItems = currentSearchResults.size
        )

//        println("🤖 ViewModel Query: $query, filteredList: ${filteredList.size}")

        return currentSearchResults
    }
    
    fun getPaginatedSearchResults(): List<TutorialSectionModel> {
        return if (searchPaginationState.totalItems > 0) {
            currentSearchResults.subList(
                searchPaginationState.startIndex,
                searchPaginationState.endIndex
            )
        } else {
            emptyList()
        }
    }
    
    fun updateSearchPage(newPaginationState: PaginationState) {
        searchPaginationState = newPaginationState
    }
}


val suggestionList = listOf(
    SuggestionModel("Modifier"),
    SuggestionModel("Row"),
    SuggestionModel("Column"),
    SuggestionModel("BottomSheet"),
    SuggestionModel("Dialog"),
    SuggestionModel("Checkbox"),
    SuggestionModel("Layout"),
    SuggestionModel("Modifier"),
    SuggestionModel("SubcomposeLayout"),
    SuggestionModel("Recomposition"),
    SuggestionModel("SideEffect"),
    SuggestionModel("PointerInput"),
    SuggestionModel("AwaitPointerEventScope"),
    SuggestionModel("Gesture"),
    SuggestionModel("Drag"),
    SuggestionModel("Transform"),
    SuggestionModel("Canvas"),
    SuggestionModel("DrawScope"),
    SuggestionModel("Path"),
    SuggestionModel("PathEffect"),
    SuggestionModel("PathOperation"),
    SuggestionModel("Blend Mode"),
)