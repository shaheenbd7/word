package com.shan.word.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.shan.word.domain.entity.Word
import com.shan.word.domain.entity.WordStatus
import kotlinx.coroutines.flow.StateFlow
import java.net.URLEncoder

enum class WordCategory {
    FAVORITE, KNOWN, UNKNOWN, DISCARDED, ALL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyListScreen(
    wordsFlow: StateFlow<List<Word>>,
    onBack: () -> Unit,
    navController: NavHostController
) {
    val words by wordsFlow.collectAsState()
    val categories = listOf(
        WordCategory.FAVORITE to "Favorite Words",
        WordCategory.KNOWN to "Known Words",
        WordCategory.UNKNOWN to "Unknown Words",
        WordCategory.DISCARDED to "Discarded Words",
        WordCategory.ALL to "All Words"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My List") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(categories) { (category, title) ->
                val filteredWords = when (category) {
                    WordCategory.FAVORITE -> words.filter { it.isFavorite }
                    WordCategory.KNOWN -> words.filter { it.status == WordStatus.KNOWN }
                    WordCategory.UNKNOWN -> words.filter { it.status == WordStatus.UNKNOWN }
                    WordCategory.DISCARDED -> words.filter { it.status == WordStatus.DISCARDED }
                    WordCategory.ALL -> words.filter { it.status != WordStatus.DISCARDED }
                }.map { it.word }.distinct().sorted()

                CategoryCard(
                    title = title,
                    count = filteredWords.size,
                    icon = when (category) {
                        WordCategory.FAVORITE -> Icons.Default.Star
                        WordCategory.KNOWN -> Icons.Default.CheckCircle
                        WordCategory.UNKNOWN -> Icons.Default.Warning
                        WordCategory.DISCARDED -> Icons.Default.Delete
                        WordCategory.ALL -> Icons.Default.List
                    },
                    onClick = {
                        val filterName = when (category) {
                            WordCategory.FAVORITE -> "FAVORITES"
                            WordCategory.KNOWN -> "KNOWN"
                            WordCategory.UNKNOWN -> "UNKNOWN"
                            WordCategory.DISCARDED -> "DISCARDED"
                            WordCategory.ALL -> "ALL"
                        }
                        navController.navigate("filteredWords/$filterName")
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun CategoryCard(
    title: String,
    count: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$count words",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "View",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
