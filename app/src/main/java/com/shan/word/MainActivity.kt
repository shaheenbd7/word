package com.shan.word

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.launch
import com.shan.word.presentation.screen.MainScreen
import com.shan.word.presentation.screen.WordsScreen
import com.shan.word.presentation.screen.WordDetailScreen
import com.shan.word.presentation.screen.AllWordsScreen
import com.shan.word.presentation.screen.HomeScreen
import com.shan.word.presentation.screen.AppNavigationDrawer
import com.shan.word.presentation.viewmodel.MainViewModel
import com.shan.word.presentation.viewmodel.WordsViewModel
import com.shan.word.presentation.viewmodel.AllWordsViewModel
import com.shan.word.ui.theme.WordTheme
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val wordsViewModel: WordsViewModel by viewModels()
    private val allWordsViewModel: AllWordsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable edge-to-edge but with proper insets handling
        enableEdgeToEdge()
        PDFBoxResourceLoader.init(applicationContext)

        val pdfPicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { processPdf(it) }
        }

        setContent {
            WordTheme {
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                
                AppNavigationDrawer(
                    drawerState = drawerState,
                    onSelectPdf = { 
                        pdfPicker.launch("application/pdf")
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    onDeleteAll = { 
                        mainViewModel.deleteAllWords()
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    onShowAllWords = {
                        navController.navigate("allWords") {
                            popUpTo("home") { inclusive = false }
                        }
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    onSelectFile = {
                        navController.navigate("main") {
                            popUpTo("home") { inclusive = false }
                        }
                        scope.launch {
                            drawerState.close()
                        }
                    }
                ) {
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                onSelectPdf = { pdfPicker.launch("application/pdf") },
                                onDeleteAll = { mainViewModel.deleteAllWords() },
                                onOpenDrawer = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                },
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
                        composable("main") {
                            MainScreen(
                                onSelectPdf = { pdfPicker.launch("application/pdf") },
                                onDeleteAll = { mainViewModel.deleteAllWords() },
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
                            WordDetailScreen(
                                word = word, 
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun processPdf(uri: Uri) {
        mainViewModel.setParsingInProgress(true)
        mainViewModel.setWordsFound(0)
        lifecycleScope.launch(Dispatchers.IO) {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            inputStream?.use {
                val document = PDDocument.load(it)
                val text = PDFTextStripper().getText(document)
                document.close()
                val words = text.split("\\W+".toRegex())
                    .map { it.trim().lowercase() }
                    .filter { it.isNotBlank() && it.length > 2 && it.all { ch -> ch.isLetter() } }
                    .distinct()
                // Update progress as we process words
                words.forEachIndexed { idx, _ ->
                    mainViewModel.setWordsFound(idx + 1)
                }
                val filename = getFileName(uri)
                mainViewModel.insertWords(words, filename)
            }
            mainViewModel.setParsingInProgress(false)
        }
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndexOrThrow(android.provider.OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "unknown.pdf"
    }
}