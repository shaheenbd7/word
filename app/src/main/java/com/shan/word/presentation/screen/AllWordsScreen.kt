package com.shan.word.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.shan.word.domain.entity.Word
import com.shan.word.domain.entity.WordStatus
import com.shan.word.presentation.ui.GradientTopAppBar
import kotlinx.coroutines.flow.StateFlow
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllWordsScreen(
    wordsFlow: StateFlow<List<Word>>,
    onBack: () -> Unit,
    navController: NavHostController,
    onOpenDrawer: () -> Unit
) {
    val words by wordsFlow.collectAsState()
    val uniqueWords = remember(words) {
        words.filter { it.status != WordStatus.DISCARDED }
            .map { it.word }
            .distinct()
            .sorted()
    }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // Debug: Log the number of words
    LaunchedEffect(words) {
        println("AllWordsScreen: Received ${words.size} words")
        // Once we receive words (even empty list), we're no longer loading
        isLoading = false
    }

    // Show loading state if still loading
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading words...")
            }
        }
        return
    }

    // Show empty state when loading is complete but no words exist
    if (uniqueWords.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Empty dictionary",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Dictionary is Empty",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No words found. Try parsing a document first.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
        return
    }
    
    val filteredWords = remember(uniqueWords, searchQuery) {
        if (searchQuery.isEmpty()) {
            uniqueWords
        } else {
            uniqueWords.filter { it.contains(searchQuery, ignoreCase = true) }
        }
    }
    
    Scaffold(
        topBar = {
            GradientTopAppBar(
                title = { Text("All Words (${filteredWords.size})") }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(filteredWords) { word ->
                    WordCard(
                        word = word,
                        onClick = {
                            val encodedWord = URLEncoder.encode(word, "UTF-8")
                            navController.navigate("wordDetail/$encodedWord")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search words...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search")
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
fun WordCard(
    word: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = word,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "View details",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
