package com.shan.word.presentation.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.shan.word.presentation.screen.*
import com.shan.word.presentation.viewmodel.*

@Composable
fun AppNavigation(
    navController: NavHostController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    mainViewModel: MainViewModel,
    wordsViewModel: WordsViewModel,
    allWordsViewModel: AllWordsViewModel,
    wordDetailViewModel: WordDetailViewModel,
    filteredWordsViewModel: FilteredWordsViewModel,
    onSelectDocument: (String) -> Unit,
    onSelectImage: () -> Unit,
    onDeleteAllWords: () -> Unit,
    onNavigateToAllWords: () -> Unit,
    onNavigateToFileParser: () -> Unit,
    onNavigateToMyList: () -> Unit,
    onNavigateToWordSources: () -> Unit
) {
    val onDeleteAllAction = {
        onDeleteAllWords()
        scope.launch { drawerState.close() }
        Unit
    }

    val onShowAllWordsAction = {
        onNavigateToAllWords()
        scope.launch { drawerState.close() }
        Unit
    }

    val onSelectFileAction = {
        onNavigateToFileParser()
        scope.launch { drawerState.close() }
        Unit
    }

    AppNavigationDrawer(
        drawerState = drawerState,
        onDeleteAll = onDeleteAllAction,
        onShowAllWords = onShowAllWordsAction,
        onSelectFile = onSelectFileAction
    ) {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen(
                    onDeleteAll = onDeleteAllWords,
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    onShowAllWords = onNavigateToAllWords,
                    onOpenFileParser = onNavigateToFileParser,
                    onOpenMyList = onNavigateToMyList,
                    onOpenWordSources = onNavigateToWordSources,
                    parsingInProgress = mainViewModel.parsingInProgress.collectAsState().value,
                    wordsFound = mainViewModel.wordsFound.collectAsState().value
                )
            }
            composable("allWords") {
                AllWordsScreen(
                    wordsFlow = allWordsViewModel.allWords,
                    onBack = { navController.popBackStack() },
                    navController = navController,
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            }
            composable("myList") {
                MyListScreen(
                    wordsFlow = allWordsViewModel.allWords,
                    onBack = { navController.popBackStack() },
                    navController = navController
                )
            }
            composable("wordSources") {
                WordSourcesScreen(
                    filenamesFlow = mainViewModel.filenames,
                    onBack = { navController.popBackStack() },
                    navController = navController
                )
            }
            composable("filteredWords/{filter}") { backStackEntry ->
                val filterName = backStackEntry.arguments?.getString("filter") ?: "ALL"
                val filter = WordFilter.valueOf(filterName)
                val wordsFlow = when (filter) {
                    WordFilter.ALL -> allWordsViewModel.allWords
                    else -> {
                        filteredWordsViewModel.loadWords(filter)
                        filteredWordsViewModel.words
                    }
                }
                AllWordsScreen(
                    wordsFlow = wordsFlow,
                    onBack = { navController.popBackStack() },
                    navController = navController,
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            }
            composable("fileParser") {
                FileParserScreen(
                    onBack = { navController.popBackStack() },
                    onSelectFile = onSelectDocument,
                    onSelectImage = onSelectImage,
                    parsingInProgress = mainViewModel.parsingInProgress.collectAsState().value,
                    wordsFound = mainViewModel.wordsFound.collectAsState().value
                )
            }
            composable("main") {
                MainScreen(
                    onDeleteAll = onDeleteAllWords,
                    filenamesFlow = mainViewModel.filenames,
                    onFileSelected = { filename ->
                        mainViewModel.selectFilenameId(filename.id)
                        navController.navigate("words/${filename.id}/${filename.name}")
                    },
                    parsingInProgress = mainViewModel.parsingInProgress,
                    wordsFound = mainViewModel.wordsFound,
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    onShowAllWords = {
                        navController.navigate("allWords") {
                            popUpTo("allWords") { inclusive = true }
                        }
                    }
                )
            }
            composable("words/{filenameId}/{filenameName}") { backStackEntry ->
                val filenameId = backStackEntry.arguments?.getString("filenameId")?.toIntOrNull()
                val filenameName = backStackEntry.arguments?.getString("filenameName") ?: ""
                WordsScreen(
                    filenameId = filenameId,
                    filenameName = filenameName,
                    wordsFlow = wordsViewModel.getWordsForFilename(filenameId ?: 0),
                    onBack = { navController.popBackStack() },
                    navController = navController,
                    onOpenDrawer = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            }
            composable("wordDetail/{word}") { backStackEntry ->
                val word = backStackEntry.arguments?.getString("word") ?: ""
                LaunchedEffect(word) {
                    wordDetailViewModel.loadWordDetails(word)
                }
                val wordDetails = wordDetailViewModel.wordDetails.collectAsState().value
                WordDetailScreen(
                    word = word,
                    onBack = { navController.popBackStack() },
                    onToggleFavorite = { wordText ->
                        wordDetailViewModel.toggleFavorite(wordText)
                    },
                    onMarkAsKnown = { wordText ->
                        wordDetailViewModel.markAsKnown(wordText)
                    },
                    onMarkAsDiscarded = { wordText ->
                        wordDetailViewModel.markAsDiscarded(wordText)
                    },
                    wordDetails = wordDetails
                )
            }
        }
    }
}
