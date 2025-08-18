package com.shan.word.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shan.word.domain.entity.Filename
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onSelectPdf: () -> Unit,
    onDeleteAll: () -> Unit,
    filenamesFlow: StateFlow<List<Filename>>,
    onFileSelected: (Filename) -> Unit,
    parsingInProgress: StateFlow<Boolean>,
    wordsFound: StateFlow<Int>,
    onOpenDrawer: () -> Unit,
    onShowAllWords: () -> Unit
) {
    val filenames by filenamesFlow.collectAsState()
    val parsing by parsingInProgress.collectAsState()
    val found by wordsFound.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    var selectedFilename by remember { mutableStateOf<Filename?>(null) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Word PDF Parser") },
            navigationIcon = {
                IconButton(onClick = onOpenDrawer) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }
            }
        )
        
        // Main content - now shows all words by default
        Column(modifier = Modifier.padding(16.dp)) {
            // Quick action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSelectPdf,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Select PDF")
                }
                
                Button(
                    onClick = onDeleteAll,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete All")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // View All Words button
            Button(
                onClick = onShowAllWords,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View All Words")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // File selection dropdown
            Box {
                Button(
                    onClick = { expanded = true },
                    enabled = filenames.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
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