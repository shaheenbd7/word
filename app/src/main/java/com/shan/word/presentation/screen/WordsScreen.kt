package com.shan.word.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.shan.word.domain.entity.Word
import kotlinx.coroutines.flow.StateFlow
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordsScreen(
    filenameId: Int?,
    filenameName: String,
    wordsFlow: StateFlow<List<Word>>,
    onBack: () -> Unit,
    navController: NavHostController
) {
    val words by wordsFlow.collectAsState()
    val uniqueWords = remember(words) { words.map { it.word }.distinct() }
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(filenameName) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(uniqueWords) { word ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            val encodedWord = URLEncoder.encode(word, "UTF-8")
                            navController.navigate("wordDetail/$encodedWord")
                        }
                ) {
                    Text(word, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
} 