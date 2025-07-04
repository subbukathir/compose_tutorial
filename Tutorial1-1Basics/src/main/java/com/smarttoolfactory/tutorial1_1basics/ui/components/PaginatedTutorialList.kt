package com.smarttoolfactory.tutorial1_1basics.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.model.PaginationState
import com.smarttoolfactory.tutorial1_1basics.model.TutorialSectionModel
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@Composable
fun PaginatedTutorialListContent(
    modifier: Modifier = Modifier,
    tutorialList: List<TutorialSectionModel>,
    paginationState: PaginationState,
    onPageChange: (PaginationState) -> Unit,
    navigateToTutorial: (String) -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xffEEEEEE)
    ) {
        Column {
            Box(
                modifier = Modifier.weight(1f)
            ) {
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
                            key = { it.title }
                        ) { item: TutorialSectionModel ->

                            var isExpanded by remember(key1 = item.title) { 
                                mutableStateOf(item.expanded) 
                            }

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

                // Jump to top button shows up when user scrolls past a threshold.
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

            // Pagination controls
            PaginationControls(
                paginationState = paginationState,
                onPageChange = { newPaginationState ->
                    onPageChange(newPaginationState)
                },
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            )
        }
        
        // Reset scroll to top when page changes
        LaunchedEffect(paginationState.currentPage) {
            scrollState.scrollToItem(0)
        }
    }
}