package com.smarttoolfactory.tutorial1_1basics

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smarttoolfactory.tutorial1_1basics.model.TutorialSectionModel
import com.smarttoolfactory.tutorial1_1basics.tutorial_list.*

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun TutorialNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destinations.BASICS_START,
) {

    val mainViewModel: HomeViewModel = viewModel()

    if (mainViewModel.tutorialList.isEmpty()) {

        val componentTutorialList: List<TutorialSectionModel> = createComponentTutorialList {
            navController.navigateUp()
        }
        val layoutTutorialList = createLayoutTutorialList()
        val stateTutorialList = createStateTutorialList()
        val gestureTutorialList = createGestureTutorialList()
        val graphicsTutorialList = createGraphicsTutorialList()

        mainViewModel.tutorialList.add(componentTutorialList)
        mainViewModel.tutorialList.add(layoutTutorialList)
        mainViewModel.tutorialList.add(stateTutorialList)
        mainViewModel.tutorialList.add(gestureTutorialList)
        mainViewModel.tutorialList.add(graphicsTutorialList)
        
        // Initialize pagination states
        mainViewModel.updatePaginationStates()
    }

    // Create Navigation for each Composable Page
    NavHost(
        modifier = modifier.statusBarsPadding(),
        navController = navController,
        startDestination = startDestination
    ) {

        // BASIC TUTORIALS
        composable(route = Destinations.BASICS_START) { navBackEntryStack ->
            HomeScreen(
                viewModel = mainViewModel,
                navigateToTutorial = { tutorialTitle ->
                    navController.navigate(tutorialTitle)
                }
            )
        }

        // Set navigation route as title of tutorial card
        // and invoke @Composable inside lambda of this card.
        mainViewModel.tutorialList.forEach { list ->
            list.forEach { model ->
                composable(route = model.title) { navBackEntryStack ->
                    // This column is used for setting navigation padding since
                    // NavHost only has statusBarsPadding to let main screen list have
                    // inset at the bottom with WindowInsetsSides.Bottom
                  Column(Modifier.navigationBarsPadding()) {
                      // 🔥 These are @Composable screens such as Tutorial2_1Screen()
                      model.action?.invoke()
                  }
                }
            }
        }
    }
}

object Destinations {
    const val BASICS_START = "start_destinations"
}