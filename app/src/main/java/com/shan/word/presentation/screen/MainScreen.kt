package com.shan.word.presentation.screen

import androidx.compose.foundation.layout.*
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