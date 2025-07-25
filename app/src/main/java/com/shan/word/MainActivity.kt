package com.shan.word

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shan.word.ui.theme.WordTheme
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.InputStream

class WordViewModel(private val db: WordDatabase) : ViewModel() {
    val filenames: StateFlow<List<Filename>> = db.wordDao().getAllFilenames()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _selectedFilenameId = MutableStateFlow<Int?>(null)
    val selectedFilenameId: StateFlow<Int?> get() = _selectedFilenameId

    private val _parsingInProgress = MutableStateFlow(false)
    val parsingInProgress: StateFlow<Boolean> get() = _parsingInProgress

    private val _wordsFound = MutableStateFlow(0)
    val wordsFound: StateFlow<Int> get() = _wordsFound

    val wordsForSelectedFile: StateFlow<List<Word>> =
        _selectedFilenameId.flatMapLatest { id ->
            if (id == null) kotlinx.coroutines.flow.flowOf(emptyList())
            else db.wordDao().getWordsForFilenameId(id)
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun selectFilenameId(id: Int?) {
        _selectedFilenameId.value = id
    }

    fun insertWords(words: List<String>, filename: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Insert filename if not exists, get its id
            var filenameId = db.wordDao().getFilenameByName(filename)?.id
            if (filenameId == null) {
                filenameId = db.wordDao().insertFilename(Filename(name = filename)).toInt()
            }
            db.wordDao().insertAll(words.map { Word(
                word = it,
                filenameId = filenameId!!
            ) })
        }
    }

    fun deleteAllWords() {
        viewModelScope.launch(Dispatchers.IO) {
            db.wordDao().deleteAllWords()
        }
    }

    fun setParsingInProgress(inProgress: Boolean) {
        _parsingInProgress.value = inProgress
    }

    fun setWordsFound(count: Int) {
        _wordsFound.value = count
    }
}

class MainActivity : ComponentActivity() {
    private lateinit var db: WordDatabase
    private val viewModel: WordViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                db = WordDatabase.getDatabase(applicationContext)
                @Suppress("UNCHECKED_CAST")
                return WordViewModel(db) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        PDFBoxResourceLoader.init(applicationContext)

        val pdfPicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { processPdf(it) }
        }

        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "main") {
                composable("main") {
                    MainScreen(
                        onSelectPdf = { pdfPicker.launch("application/pdf") },
                        onDeleteAll = { viewModel.deleteAllWords() },
                        filenamesFlow = viewModel.filenames,
                        onFileSelected = { filename ->
                            viewModel.selectFilenameId(filename.id)
                            navController.navigate("words/${filename.id}/${filename.name}")
                        },
                        parsingInProgress = viewModel.parsingInProgress,
                        wordsFound = viewModel.wordsFound
                    )
                }
                composable("words/{filenameId}/{filenameName}") { backStackEntry ->
                    val filenameId = backStackEntry.arguments?.getString("filenameId")?.toIntOrNull()
                    val filenameName = backStackEntry.arguments?.getString("filenameName") ?: ""
                    WordsScreen(
                        filenameId = filenameId,
                        filenameName = filenameName,
                        wordsFlow = viewModel.wordsForSelectedFile,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }

    private fun processPdf(uri: Uri) {
        viewModel.setParsingInProgress(true)
        viewModel.setWordsFound(0)
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
                    viewModel.setWordsFound(idx + 1)
                }
                val filename = getFileName(uri)
                viewModel.insertWords(words, filename)
            }
            viewModel.setParsingInProgress(false)
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

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onSelectPdf: () -> Unit,
    onDeleteAll: () -> Unit,
    filenamesFlow: StateFlow<List<Filename>>,
    onFileSelected: (Filename) -> Unit,
    parsingInProgress: StateFlow<Boolean>,
    wordsFound: StateFlow<Int>
) {
    val filenames by filenamesFlow.collectAsState()
    val parsing by parsingInProgress.collectAsState()
    val found by wordsFound.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    var selectedFilename by remember { mutableStateOf<Filename?>(null) }
    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(title = { Text("Word PDF Parser") })
        Column(modifier = Modifier.padding(16.dp)) {
            Button(onClick = onSelectPdf) {
                Text("Select PDF")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onDeleteAll) {
                Text("Delete All Words")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box {
                Button(onClick = { expanded = true }, enabled = filenames.isNotEmpty()) {
                    Text(selectedFilename?.name ?: "Select a file")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    filenames.forEach { filename ->
                        DropdownMenuItem(onClick = {
                            selectedFilename = filename
                            onFileSelected(filename)
                            expanded = false
                        }, text = { Text(filename.name) })
                    }
                }
            }
            if (parsing) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 5.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Parsing... $found words found")
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun WordsScreen(
    filenameId: Int?,
    filenameName: String,
    wordsFlow: StateFlow<List<Word>>,
    onBack: () -> Unit
) {
    val words by wordsFlow.collectAsState()
    val uniqueWords = remember(words) { words.map { it.word }.distinct() }
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(filenameName) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    androidx.compose.material3.Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(uniqueWords) { word ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)) {
                    Text(word, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WordTheme {
        Greeting("Android")
    }
}