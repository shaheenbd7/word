package com.shan.word

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import com.shan.word.presentation.navigation.AppNavigation
import com.shan.word.presentation.viewmodel.MainViewModel
import com.shan.word.presentation.viewmodel.WordsViewModel
import com.shan.word.presentation.viewmodel.AllWordsViewModel
import com.shan.word.presentation.viewmodel.WordDetailViewModel
import com.shan.word.presentation.viewmodel.FilteredWordsViewModel
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
    private val wordDetailViewModel: WordDetailViewModel by viewModels()
    private val filteredWordsViewModel: FilteredWordsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable edge-to-edge but with proper insets handling
        enableEdgeToEdge()
        PDFBoxResourceLoader.init(applicationContext)

        val pdfPicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { processPdf(it) }
        }

        val documentPicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { processDocument(it) }
        }

        val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { processImage(it) }
        }

        setContent {
            WordTheme {
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val onNavigateToAllWords = {
                    navController.navigate("allWords") {
                        popUpTo("home") { inclusive = false }
                    }
                }

                val onNavigateToFileParser = {
                    navController.navigate("fileParser") {
                        popUpTo("home") { inclusive = false }
                    }
                }

                AppNavigation(
                    navController = navController,
                    drawerState = drawerState,
                    scope = scope,
                    mainViewModel = mainViewModel,
                    wordsViewModel = wordsViewModel,
                    allWordsViewModel = allWordsViewModel,
                    wordDetailViewModel = wordDetailViewModel,
                    filteredWordsViewModel = filteredWordsViewModel,
                    onSelectPdf = { pdfPicker.launch("application/pdf") },
                    onSelectDocument = { mimeType -> documentPicker.launch(mimeType) },
                    onSelectImage = { imagePicker.launch("image/*") },
                    onDeleteAllWords = { mainViewModel.deleteAllWords() },
                    onNavigateToAllWords = onNavigateToAllWords,
                    onNavigateToFileParser = onNavigateToFileParser
                )
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

    private fun processDocument(uri: Uri) {
        mainViewModel.setParsingInProgress(true)
        mainViewModel.setWordsFound(0)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                inputStream?.use {
                    // For now, we'll handle different file types here
                    // This is a simplified implementation
                    val text = it.bufferedReader().use { reader -> reader.readText() }
                    val words = text.split("\\W+".toRegex())
                        .map { word -> word.trim().lowercase() }
                        .filter { it.isNotBlank() && it.length > 2 && it.all { ch -> ch.isLetter() } }
                        .distinct()
                    
                    words.forEachIndexed { idx, _ ->
                        mainViewModel.setWordsFound(idx + 1)
                    }
                    val filename = getFileName(uri)
                    mainViewModel.insertWords(words, filename)
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                mainViewModel.setParsingInProgress(false)
            }
        }
    }

    private fun processImage(uri: Uri) {
        mainViewModel.setParsingInProgress(true)
        mainViewModel.setWordsFound(0)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // For now, we'll implement basic OCR here
                // This would require additional OCR libraries like ML Kit Text Recognition
                val filename = getFileName(uri)
                // Placeholder for OCR implementation
                mainViewModel.setWordsFound(0)
            } catch (e: Exception) {
                // Handle error
            } finally {
                mainViewModel.setParsingInProgress(false)
            }
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
