package com.shan.word

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shan.word.ui.theme.WordTheme
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.InputStream
import androidx.lifecycle.lifecycleScope
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader

class WordViewModel(private val db: WordDatabase) : ViewModel() {
    val words: StateFlow<List<Word>> = db.wordDao().getAllWords()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun insertWords(words: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            db.wordDao().insertAll(words.map { Word(word = it) })
        }
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
            WordTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Button(onClick = { pdfPicker.launch("application/pdf") }) {
                            Text("Select PDF")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        val words by viewModel.words.collectAsState()
                        LazyColumn {
                            items(words) { word ->
                                Card(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)) {
                                    Text(word.word, modifier = Modifier.padding(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun processPdf(uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            inputStream?.use {
                val document = PDDocument.load(it)
                val text = PDFTextStripper().getText(document)
                document.close()
                val words = text.split("\\W+".toRegex())
                    .map { it.trim().lowercase() }
                    .filter { it.isNotBlank() }
                    .distinct()
                viewModel.insertWords(words)
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