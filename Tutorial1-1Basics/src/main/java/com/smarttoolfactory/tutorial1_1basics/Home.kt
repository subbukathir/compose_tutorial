@file:OptIn(ExperimentalFoundationApi::class)

package com.smarttoolfactory.tutorial1_1basics

import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.tutorial1_1basics.model.SuggestionModel
import com.smarttoolfactory.tutorial1_1basics.model.TutorialSectionModel
import com.smarttoolfactory.tutorial1_1basics.ui.components.CancelableChip
import com.smarttoolfactory.tutorial1_1basics.ui.components.JumpToTopButton
import com.smarttoolfactory.tutorial1_1basics.ui.components.PaginatedTutorialListContent
import com.smarttoolfactory.tutorial1_1basics.ui.components.StaggeredGrid
import com.smarttoolfactory.tutorial1_1basics.ui.components.TutorialSectionCard
import kotlinx.coroutines.launch

internal val tabList = listOf("Components", "Layout", "State", "Gesture", "Graphics", "Theming")

/**
 * This is Home Screen that contains Search bar, Tabs, and tutorial pages in Pager
 */
@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalAnimationApi
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    navigateToTutorial: (String) -> Unit,

    ) {

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        val context = LocalContext.current

        val state =
            rememberSearchState(
                initialResults = viewModel.tutorialList,
                suggestions = suggestionList,
                timeoutMillis = 600,
            ) { query: TextFieldValue ->
                viewModel.getTutorials(query.text)
            }

        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current

        val dispatcher: OnBackPressedDispatcher =
            LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher

        val backCallback = remember {
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!state.focused) {
                        isEnabled = false
                        Toast.makeText(context, "Back", Toast.LENGTH_SHORT).show()
                        dispatcher.onBackPressed()
                    } else {
                        state.query = TextFieldValue("")
                        state.focused = false
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                }
            }
        }

        DisposableEffect(dispatcher) { // dispose/relaunch if dispatcher changes
            dispatcher.addCallback(backCallback)
            onDispose {
                backCallback.remove() // avoid leaks!
            }
        }

        SearchBar(
            query = state.query,
            onQueryChange = { state.query = it },
            onSearchFocusChange = { state.focused = it },
            onClearQuery = { state.query = TextFieldValue("") },
            onBack = { state.query = TextFieldValue("") },
            searching = state.searching,
            focused = state.focused,
            modifier = modifier
        )


        when (state.searchDisplay) {
            // This is initial state, first time screen is opened or no query is done
            SearchDisplay.InitialResults -> {
                HomeContent(modifier, state.initialResults, navigateToTutorial, viewModel)
            }

            SearchDisplay.NoResults -> {
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("❌ No Results!", fontSize = 24.sp, color = Color(0xffDD2C00))
                }
            }

            SearchDisplay.Suggestions -> {
                SuggestionGridLayout(suggestions = state.suggestions) {
                    var text = state.query.text
                    if (text.isEmpty()) text = it else text += " $it"
                    text.trim()
                    // Set text and cursor position to end of text
                    state.query = TextFieldValue(text, TextRange(text.length))
                }
            }

            SearchDisplay.Results -> {
                PaginatedTutorialListContent(
                    modifier = modifier,
                    tutorialList = viewModel.getPaginatedSearchResults(),
                    paginationState = viewModel.searchPaginationState,
                    onPageChange = { newState -> viewModel.updateSearchPage(newState) },
                    navigateToTutorial = navigateToTutorial
                )
            }

            SearchDisplay.SearchInProgress -> {
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun SuggestionGridLayout(
    modifier: Modifier = Modifier,
    suggestions: List<SuggestionModel>,
    onSuggestionClick: (String) -> Unit
) {

    StaggeredGrid(
        modifier = modifier.padding(4.dp)
    ) {
        suggestions.forEach { suggestionModel ->
            CancelableChip(
                modifier = Modifier.padding(4.dp),
                suggestion = suggestionModel,
                onClick = {
                    onSuggestionClick(it.tag)
                },
                onCancel = {

                }
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun HomeContent(
    modifier: Modifier,
    tutorialList: List<List<TutorialSectionModel>>,
    navigateToTutorial: (String) -> Unit,
    viewModel: HomeViewModel
) {

    val pagerState: PagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        // provide pageCount
        tabList.size
    }
    val coroutineScope = rememberCoroutineScope()

    // Initialize pagination states when tutorial list is available
    LaunchedEffect(tutorialList) {
        if (tutorialList.isNotEmpty()) {
            viewModel.updatePaginationStates()
        }
    }

    ScrollableTabRow(
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        edgePadding = 8.dp,
        // selected tab is our current page
        selectedTabIndex = pagerState.currentPage,
        // Override the indicator, using the provided pagerTabIndicatorOffset modifier

    ) {
        // Add tabs for all of our pages
        tabList.forEachIndexed { index, title ->
            Tab(
                text = { Text(title) },
                selected = pagerState.currentPage == index,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
            )
        }
    }

    HorizontalPager(
        modifier = Modifier,
        pageSpacing = 0.dp,
        beyondBoundsPageCount = 2,
        pageSize = PageSize.Fill,
        state = pagerState
    ) { page: Int ->
        when (page) {
            0 -> PaginatedTutorialListContent(
                modifier = modifier,
                tutorialList = viewModel.getPaginatedTutorials(0),
                paginationState = viewModel.getPaginationStateForTab(0),
                onPageChange = { newState -> viewModel.updatePageForTab(0, newState) },
                navigateToTutorial = navigateToTutorial
            )
            1 -> PaginatedTutorialListContent(
                modifier = modifier,
                tutorialList = viewModel.getPaginatedTutorials(1),
                paginationState = viewModel.getPaginationStateForTab(1),
                onPageChange = { newState -> viewModel.updatePageForTab(1, newState) },
                navigateToTutorial = navigateToTutorial
            )
            2 -> PaginatedTutorialListContent(
                modifier = modifier,
                tutorialList = viewModel.getPaginatedTutorials(2),
                paginationState = viewModel.getPaginationStateForTab(2),
                onPageChange = { newState -> viewModel.updatePageForTab(2, newState) },
                navigateToTutorial = navigateToTutorial
            )
            3 -> PaginatedTutorialListContent(
                modifier = modifier,
                tutorialList = viewModel.getPaginatedTutorials(3),
                paginationState = viewModel.getPaginationStateForTab(3),
                onPageChange = { newState -> viewModel.updatePageForTab(3, newState) },
                navigateToTutorial = navigateToTutorial
            )
            4 -> PaginatedTutorialListContent(
                modifier = modifier,
                tutorialList = viewModel.getPaginatedTutorials(4),
                paginationState = viewModel.getPaginationStateForTab(4),
                onPageChange = { newState -> viewModel.updatePageForTab(4, newState) },
                navigateToTutorial = navigateToTutorial
            )
            else -> ComingSoonScreen()
        }
    }

//    HorizontalPager(
//        modifier = Modifier,
//        state = pagerState,
//        pageSpacing = 0.dp,
//        userScrollEnabled = true,
//        reverseLayout = false,
//        contentPadding = PaddingValues(0.dp),
//        beyondBoundsPageCount = 2,
//        pageSize = PageSize.Fill,
//        flingBehavior = PagerDefaults.flingBehavior(state = state),
//        key = null,
//        pageContent = fun PagerScope.(page: Int) {
//            when (page) {
//                0 -> TutorialListContent(modifier, tutorialList[0], navigateToTutorial)
//                1 -> TutorialListContent(modifier, tutorialList[1], navigateToTutorial)
//                2 -> TutorialListContent(modifier, tutorialList[2], navigateToTutorial)
//                3 -> TutorialListContent(modifier, tutorialList[3], navigateToTutorial)
//                4 -> TutorialListContent(modifier, tutorialList[4], navigateToTutorial)
//                else -> ComingSoonScreen()
//            }
//        }
//    )
}

@Composable
fun ComingSoonScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "⚠️ Under Construction!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
    }
}

@ExperimentalAnimationApi
@Composable
fun TutorialListContent(
    modifier: Modifier = Modifier,
    tutorialList: List<TutorialSectionModel>,
    navigateToTutorial: (String) -> Unit
) {

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xffEEEEEE)
    ) {

        Box {

            val scrollState = rememberLazyListState()

            // List of Tutorials
            LazyColumn(
                state = scrollState,
                contentPadding = WindowInsets.systemBars
                    .only(WindowInsetsSides.Bottom)
                    .add(
                        WindowInsets(left = 8.dp, right = 8.dp, top = 16.dp, bottom = 16.dp)
                    )
                    .asPaddingValues(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = {

                    items(
                        items = tutorialList,
                        key = {
                            it.title
                        }
                    ) { item: TutorialSectionModel ->

                        var isExpanded by remember(key1 = item.title) { mutableStateOf(item.expanded) }

                        TutorialSectionCard(
                            model = item,
                            onClick = {
                                navigateToTutorial(item.title)
                            },
                            onExpandClicked = {
                                item.expanded = !item.expanded
                                isExpanded = item.expanded
                            },
                            expanded = isExpanded
                        )
                    }
                }
            )

            // Jump to bottom button shows up when user scrolls past a threshold.
            // Convert to pixels:
            val jumpThreshold = with(LocalDensity.current) {
                56.dp.toPx()
            }

            // Show the button if the first visible item is not the first one or if the offset is
            // greater than the threshold.
            val jumpToBottomButtonEnabled by remember {
                derivedStateOf {
                    scrollState.firstVisibleItemIndex != 0 ||
                            scrollState.firstVisibleItemScrollOffset > jumpThreshold
                }
            }

            val coroutineScope = rememberCoroutineScope()
            JumpToTopButton(
                enabled = jumpToBottomButtonEnabled,
                onClicked = {
                    coroutineScope.launch {
                        scrollState.scrollToItem(0)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
            )
        }
    }
}